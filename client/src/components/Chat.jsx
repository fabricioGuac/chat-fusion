


export default function Chat({chat}){

    console.log(chat);

    return (
        <>
        <h1>Chat with the id: {chat.id}</h1>
        <h1>Chat between: {chat.members[0].username}</h1>
        <h1> {chat.isGroup ? "is group": "is private"}</h1>
        </>
    );
}