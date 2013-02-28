package server.actors;

import java.util.ArrayList;
import server.services.*;

public class ChatServer extends Actor {
	
	private ArrayList<User> users;
	
	public ChatServer() {
		users = new ArrayList<User>(5);
	}
	
	public void handleHandleUserMessage(ServerListener.HandleUserMessage msg) {
		log("caught handle user message");
		User user = msg.user;
		users.add(user);
		user.sendMessage(new JoinedServerMessage(this));
	}
	
	public void handleChatMessage(MessageFetcher.ChatMessage msg) {
		log("caught chat message");
		for(User u : users) {
			u.sendMessage(msg);
		}
	}
	
	public void handleUserDisconnectMessage(User.UserDisconnectMessage msg) {
		log("handling user disconnect message");
		users.remove(msg.user);
	}

	@Override
	public void runOnce() {
		Object msg = this.receiveMessage();
		log("handling message");
		if ( msg instanceof ServerListener.HandleUserMessage ) {
			handleHandleUserMessage((ServerListener.HandleUserMessage)msg);
		} else if ( msg instanceof MessageFetcher.ChatMessage ) {
			handleChatMessage((MessageFetcher.ChatMessage)msg);
		} else if ( msg instanceof User.UserDisconnectMessage ) {
			handleUserDisconnectMessage((User.UserDisconnectMessage)msg);
		}
		log(toString());
		postLog();
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Chat Server#{ ");
		for(User u : users) {
			b.append(u.toString());
		}
		b.append(" }");
		return b.toString();
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
