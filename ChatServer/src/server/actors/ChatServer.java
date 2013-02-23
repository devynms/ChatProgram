package server.actors;

import java.util.ArrayList;
import server.services.*;

public class ChatServer extends Actor {
	
	private ArrayList<User> users;
	
	public ChatServer() {
		users = new ArrayList<User>(5);
	}
	
	public void handleHandleUserMessage(ServerListener.HandleUserMessage msg) {
		User user = msg.user;
		users.add(user);
		user.sendMessage(new JoinedServerMessage(this));
	}
	
	public void handleChatMessage(MessageFetcher.ChatMessage msg) {
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
		} else if ( msg instanceof MessageFetcher.ChatMessage ) {
			handleChatMessage((MessageFetcher.ChatMessage)msg);
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

	@Override
	protected void onError() {
		// nothing so far, but we'd probably have to tell all our users that we're dead!
	}

}
