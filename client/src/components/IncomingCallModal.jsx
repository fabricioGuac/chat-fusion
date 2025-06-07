export default function IncomingCallModal({caller, onAccept, onDecline}) {


    return (
        <div className="fixed inset-0 z-50 items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm text-center space-y-4">
                <h3 className="text-xl font-semibold">
                    Incoming call from <span className="text-blue-600">{caller}</span>
                </h3>

                <div className="flex justify-center gap-4">
                    <button
                    onClick={onAccept}
                    className="px-4 py-2 rounded-lg bg-green-500 text-white hover:bg-green-600 transition"
                    >
                        Accept
                    </button>
                    <button
                    onClick={onDecline}
                    className="px-4 py-2 rounded-lg bg-red-500 text-white hover:bg-red-600 transition"
                    >
                        Decline
                    </button>
                    
                </div>
            </div>

        </div>
    );
}