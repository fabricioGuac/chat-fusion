# Chat Fusion

## Description 

**Chat Fusion** is a dynamic real-time messaging application that combines modern technologies to deliver a smooth, responsive chat experience. It supports one-on-one and group messaging, file sharing, audio notes, and even voice calls via WebRTC.

Built with:
- **Java 21** and **Spring Boot** for the backend
- **React** with **Vite** and **Redux** for the frontend
- **WebSockets** for real-time communication
- **WebRTC** for peer-to-peer audio calls
- **MongoDB** for data persistence
- **AWS S3** for media storage (images, audio, video)


## Usage

1. **Sign Up / Log In**  
   Create your account to access the chat interface.

2. **Profile Customization**  
   Click your avatar or username in the top-left to update your profile picture or change your display name.

3. **Start a Chat**  
   Click the “Start Chat” button, then search for users by their username or email to initiate a conversation.

4. **Create a Group**  
   Click “Create Group”, provide a group name, upload an image, and add members using the same user search.

5. **Chat Interface**  
   - Send **text messages**, **voice notes**, and **files** (images, audio, video).  
   - Edit or delete your own messages.  
   - Unread messages are tracked and displayed in the sidebar.  

6. **Group Management** *(for admins)*  
   Use the three-dot menu in the chat header:  
   - Add/remove members  
   - Promote users to admin  
   - Edit group name or image  
   - Delete the group entirely  

7. **Voice Calling**  
   Use the phone icon in the top-right of any chat to start a voice call with members. WebRTC handles peer-to-peer audio communication.

8. **Leaving or Deleting Chats**  
   Use the three-dot menu in the chat header:  
   - Leave a group  
   - Delete a one-on-one chat  

## Screenshot displaying the working app

![Screenshot of the working app](/client/public/chat-fusion-screenshot.png)

## Live Demo

[chat-fusion1.netlify.app](https://chat-fusion1.netlify.app)

You can create an account or log in and start chatting right away.


## Credits

This project began with guidance from the [Whatsapp Clone series by Code With Zosh](https://www.youtube.com/watch?v=_f5CyVdarXw&list=PL7Oro2kvkIzKsDpydQkyO6I60uC0SyDje), particularly for the initial Spring Boot setup. While the early backend structure was loosely inspired by the series, all logic, frontend architecture, UI/UX design, feature implementation, and infrastructure decisions were built independently over the following months.


## License

MIT License

Copyright (c) 2024 fabricioGuac

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Questions

If you have any questions or need help with the project, feel free to contact me through the following channels: - Connect with me on GitHub at [fabricioGuac](https://github.com/fabricioGuac)  - Drop me an email at [guacutofabricio@gmail.com](https://github.com/guacutofabricio@gmail.com)   Don't hesitate to reach out if you need any clarifications or want to share feedback. I'm here to assist you!