import { useState, useEffect, useRef } from "react";

import { useSelector, useDispatch } from 'react-redux';
import { clearCall } from '../redux/callSlice';

import ws from "../utils/ws";

// Defines the ICE stun server that will be used
const ICE_SERVERS = { iceServers: [{ urls: ["stun:stun.l.google.com:19302"] }] }


export default function CallRoomModal() {

    // State variable to track users currently in the call
    const [joinedUsers, setJoinedUsers] = useState([]);
    // Dispatch used to send actions to the Redux store
    const dispatch = useDispatch();
    // Retrieve the current call chat id from the Redux store
    const chatId = useSelector((state) => state.call.activeCallId);
    // Retrieves the current user data  from the Redux store
    const currentUser = useSelector((state) => state.user.user);

    // useRef for the local media stream (audio)
    const localStreamRef = useRef(null);
    // useRef for a map of the webRTC peer connections
    const peersRef = useRef(new Map());

    // Helper function to publish the different callRoom events
    const send = (type, targetUserId, payload = {}) => {
        ws.publish(`/app/call-room/${chatId}`, {
            type,
            user: { id: currentUser.id, username: currentUser.username, pfp: currentUser.pfp },
            targetUserId,
            ...payload,
        });
    }

    // Notifies call members that a new user has joined the call
    const joinCall = () => send("join");

    // Clean up notifying the other users, closing the modal, closing the peer connections, emptying the pc map and unsubscribing
    const leaveCall = () => {
        peersRef.current.forEach((pc) => pc.close()); // Closes all peer connections
        peersRef.current.clear(); // Empties the map

        // Stops the mic recoriding
        if (localStreamRef.current) {
            localStreamRef.current.getTracks().forEach((t) => t.stop());
        }

        // Notifies the other users
        send("leave");

        // Unsubscribe from call room topic
        ws.unsubscribe(`/topic/call-room/${chatId}`);

        // Clears Redux state closing the call modal
        dispatch(clearCall());

        console.log("CALL CLOSED SUCCESSFULLY");
    };

    // Echoes your presence when a new user joins, so they can see existing participants
    const peerEcho = () => send("echo");

    // Function to create RTCPeerConnections for one peer
    const createPeerConnection = (peer) => {
        // Initiates the new peer connection passing the ICE server
        const pc = new RTCPeerConnection(ICE_SERVERS);

        // Sends the local audio (mic track) to the peer
        localStreamRef.current.getTracks().forEach((track => pc.addTrack(track, localStreamRef.current)));

        // Event listener that sends ICE candidates as they are discovered (e.g., via STUN/TURN)
        pc.onicecandidate = (e) => {
            if (e.candidate) {
                send("candidate", peer.id, {
                    candidate: {
                        candidate: e.candidate.candidate,
                        sdpMid: e.candidate.sdpMid,
                        sdpMLineIndex: e.candidate.sdpMLineIndex,
                    },
                });
            }
        };

        // Event listener triggered when the remote peer adds a track (e.g., audio)
        // Usually fired after remote description is set and ICE negotiation is underway
        pc.ontrack = (e) => {
            // Create a new HTMLAudioElement to play the incoming audio
            const remoteAudio = new Audio();

            // Sets the audio source to the first MediaStream from the event
            // 'e.streams[0]' contains the remote stream that includes the audio track
            remoteAudio.srcObject = e.streams[0];


            // Starts playing the remote audio stream
            remoteAudio.play().catch((err) => {
                console.warn("Autoplay failed:", err);
            });

        };

        // Adds the new peer connetion to the map
        peersRef.current.set(peer.id, pc);
        return pc;
    };


    // Handles incoming websocket call events
    const handleMessage = async (incoming) => {
        // Deconstructs the incoming data
        const { type, user: incomingUser, sdp, sdpType, candidate, targetUserId } = incoming;

        // Early return to ignore the current user events
        if (incomingUser.id === currentUser.id) return;

        // Switch to handle the different events
        switch (type) {
            case "join":
                console.log(incomingUser.username, " just joined");
                // Updates the UI (adds the new user to the joined members uf not already there)
                setJoinedUsers((prev) =>
                    prev.some((u) => u.id === incomingUser.id) ?
                        prev :
                        [...prev, incomingUser]
                );

                //Echoes the presence to the recently joined member
                peerEcho();

                // Creates an offer to set an RTC peer connection with the new member
                {
                    const pc = createPeerConnection(incomingUser); // Creates the peer connection
                    const offer = await pc.createOffer(); // Initiates the creation of an SDP offer
                    await pc.setLocalDescription(offer); // Specifies the properties of the local end of the connection

                    // Emmits the offer websocket event
                    send("offer", incomingUser.id, { sdp: offer.sdp, sdpType: offer.type });
                }
                break;

            case "leave":

                console.log(incomingUser.username, " just left");
                // Updates the UI (removes the user from the members)
                setJoinedUsers((prev) => prev.filter((u) => u.id !== incomingUser.id));

                // Closes the peer connection with the leaving user
                {
                    const pc = peersRef.current.get(incomingUser.id); // Gets the peer connection from the map
                    // If the conection exists in the map closes the peer connection and removes it from the map
                    if (pc) {
                        pc.close();
                        peersRef.current.delete(incomingUser.id);
                    }
                }
                break;

            case "echo":
                console.log(incomingUser.username, "just echoed");

                // Updates the UI (adds the users that are echoing so that the newly joined user is up to date)
                setJoinedUsers((prev) =>
                    prev.some((u) => u.id === incomingUser.id) ?
                        prev :
                        [...prev, incomingUser]
                );
                break;

            case "offer":
                // If the offer is to the current user creates the connection
                if (targetUserId === currentUser.id) {
                    const pc = createPeerConnection(incomingUser); // Creates the peer connection
                    // Specifies the properties of the remote end of the connection, including the media format
                    await pc.setRemoteDescription(
                        new RTCSessionDescription({ type: sdpType, sdp })
                    );

                    const answer = await pc.createAnswer(); // Creates an SDP answer to an offer received from a remote peer during the offer/answer negotiation of a WebRTC connection
                    await pc.setLocalDescription(answer); // Specifies the properties of the local end of the connection
                    // Emmits the answer websocket event
                    send("answer", incomingUser.id, {
                        sdp: answer.sdp,
                        sdpType: answer.type,
                    });
                }
                break;

            case "answer":
                // If the offer is to the current user set that users peer connection remote description
                if (targetUserId === currentUser.id) {
                    const pc = peersRef.current.get(incomingUser.id); // Gets the peer connection from the map
                    // Verifies if the peer connection exists in the map
                    if (pc) {
                        // Specifies the properties of the remote end of the connection
                        await pc.setRemoteDescription(
                            new RTCSessionDescription({ type: sdpType, sdp })
                        );
                    }
                }
                break;

            case "candidate":
                // If the candidate event is targetting the current user add the ice candidate the peer connection
                if (targetUserId === currentUser.id) {
                    const pc = peersRef.current.get(incomingUser.id); // Gets the peer connection from the map
                    // Ensures the peer connection and the ICE candidate are present
                    if (pc && candidate) {
                        // Adds this new remote candidate to the RTCPeerConnection's remote description
                        await pc.addIceCandidate(new RTCIceCandidate(candidate));
                    }
                }
                break;

            default:
                break;
        }
    };

    // UseEffect to handle setting up the call room event subscription
    useEffect(() => {
        // Sets the websocket subscription to the call events
        const topic = `/topic/call-room/${chatId}`;
        ws.subscribe(topic, handleMessage);
        // Immediately Invoked Async Function Expression to get the audio media from the browser using the media devics API
        (async () => {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
                localStreamRef.current = stream;

                // Emits the join event
                joinCall();
            } catch (err) {
                console.error("Failed to get audio:", err);
            }
        })();
    }, [chatId]);


    return (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex flex-col items-center justify-center p-4">
            <div className="bg-white rounded-2xl shadow-lg w-full max-w-md flex flex-col">

                <div className="flex justify-end p-4">
                    <button
                        onClick={leaveCall}
                        className="text-gray-500 hover:text-red-500 text-xl font-bold"
                    >
                        ×
                    </button>
                </div>


                <div className="flex flex-wrap justify-center gap-4 p-4">
                    {joinedUsers.map((u) => (
                        <div key={u.id} className="flex flex-col items-center">
                            <img
                                src={u.pfp || "/logo192.png"}
                                alt={u.username}
                                className="w-16 h-16 rounded-full object-cover mb-2"
                            />
                            <h4 className="text-sm font-medium">{u.username}</h4>
                        </div>
                    ))}
                </div>

                <div className="p-4 flex justify-center">
                    <button
                        onClick={leaveCall}
                        className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-6 rounded-full shadow-md"
                    >
                        End Call
                    </button>
                </div>

            </div>
        </div>
    );
}