package server.components;

import java.util.ArrayList;

public class ChatServer extends Component {
	
	private ArrayList<User> users;
	
	public ChatServer() {
		users = new ArrayList<User>(5);
	}
	
	public void handleHandleUserMessage(ServerListener.HandleUserMessage msg) {
		User user = msg.user;
		users.add(user);
		user.sendMessage(new JoinedServerMessage(this));
	}
	
	public void handleChatMessage(User.ChatMessage msg) {
		for(User u : users) {
			u.sendMessage(msg);
		}
		//System.out.println("Caught user message.");
	}
	
	public void handleUserDisconnectMessage(User.UserDisconnectMessage msg) {
		users.remove(msg.user);
		//System.out.println("CHAT SERVER: User disconnected. " + msg.user);
	}

	@Override
	public void runOnce() {
		Object msg = this.receiveMessage();
		//System.out.println("Chat server got a message!!" + msg);
		if ( msg instanceof ServerListener.HandleUserMessage ) {
			handleHandleUserMessage((ServerListener.HandleUserMessage)msg);
		} else if ( msg instanceof User.ChatMessage ) {
			handleChatMessage((User.ChatMessage)msg);
		} else if ( msg instanceof User.UserDisconnectMessage ) {
			handleUserDisconnectMessage((User.UserDisconnectMessage)msg);
		}
	}
	
	public String toString() {
		return "Chat Server";
	}
	
	public class JoinedServerMessage {
		public final ChatServer server;
		
		private JoinedServerMessage(ChatServer server) {
			this.server = server;
		}
		
		public String toString() {
			return "JoinedServerMessage#{ " + server.toString() + " }";
		}
	}

}
