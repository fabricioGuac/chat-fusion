package com.fabricio.practice.chat_fusion.request;

import com.fabricio.practice.chat_fusion.model.User;

//DTO (Data Transfer Object) for call events
public class CallEventRequest {
	// Type of call event ("join", "leave", "echo", "offer", "answer", "candidate")	
	private String type;
	// User that is emitting the event
	private User user;
	// ID of the user the RCT requests are targetting
	private String targetUserId;
	// Session Description Protocol (SDP) content used in "offer" or "answer"
	private String sdp;
	// Type of SDP message ("offer" or "answer"), used together with the 'sdp' field
	private String sdpType;
	// ICE (Interactive Connectivity Establishment) candidate information for NAT traversal
	private IceCandidateDTO candidate;
	
	
	// Default no-arguments constructor
	public CallEventRequest() {
	
	}
	
	// Constructor to create a CallEventRequest with the specified fields
	public CallEventRequest(String type, User user,String targetUserId, String sdp, String sdpType, IceCandidateDTO candidate) {
		super();
		this.type = type;
		this.user = user;
		this.targetUserId = targetUserId;
		this.sdp = sdp;
		this.sdpType = sdpType;
		this.candidate = candidate;
	}

	// Getters and Setters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTargetUserId() {
		return targetUserId;
	}
	
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}
	
	public String getSdp() {
		return sdp;
	}
	
	public void setSdp(String sdp) {
		this.sdp = sdp;
	}
	
	public String getSdpType() {
		return sdpType;
	}
	
	public void setSdpType(String sdpType) {
		this.sdpType = sdpType;
	}
	
	public IceCandidateDTO getCandidate() {
		return candidate;
	}
	
	public void setCandidate(IceCandidateDTO candidate) {
		this.candidate = candidate;
	}
	
//	Nested DTO for ICE candidate details used during connection negotiation
	public static class IceCandidateDTO {
		//  // The candidate string containing network info (IP, port, transport protocol, etc.)
		private String candidate;
		// The media stream identification (mid) this candidate is associated with
		private String sdpMid;
		// The index of the media description in the SDP this candidate relates to
		private int sdpMLineIndex;
		
		
		// Default no argument constructor
		public IceCandidateDTO() {
			
		}
		
		// Constructor to create an IceCandidateDTO with the specified fields
		public IceCandidateDTO(String candidate,String sdpMid, int sdpMLineIndex ) {
			this.candidate = candidate;
			this.sdpMid = sdpMid;
			this.sdpMLineIndex = sdpMLineIndex;
		}
		
		// Getters and setters
		public String getCandidate() {
			return candidate;
		}
		
		public void setCandidate(String candidate) {
			this.candidate = candidate;
		}
		
		public String getSdpMid() {
			return sdpMid;
		}
		
		public void setSdpMid(String sdpMid) {
			this.sdpMid = sdpMid;
		}
		
		public int getSdpMLineIndex() {
			return sdpMLineIndex;
		}
		
		public void setSdpMLineIndex(int sdpMLineIndex) {
			this.sdpMLineIndex = sdpMLineIndex;
		}
	}
}
