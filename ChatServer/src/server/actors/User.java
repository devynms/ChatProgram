package server.actors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.*;
import server.services.*;

public class User extends Actor {

	private Socket	userSocket;
	private Actor	parent;
	private MessageFetcher		messageFetcher;
	private DataOutputStream	output;
	
	public User(Socket userSocket) {
		this.userSocket = userSocket;
		try {
			messageFetcher = new MessageFetcher(
					this, 
					new DataInputStream(userSocket.getInputStream())
					);
			server.Component.spawn(messageFetcher);
			output = new DataOutputStream(userSocket.getOutputStream());
		} catch (IOException e) {
			log("swallowed ioexception\tUser constructor");
		}
		this.parent = null;
		autoLog = false;
	}
	
	@Override
	public void runOnce() {
		Object msg = receiveMessage();
		if ( msg == null ) {
			Thread.yield();
		} else if ( msg instanceof MessageFetcher.ChatMessage ) {
			if ( parent != null ) {
				handleChatMessage((MessageFetcher.ChatMessage)msg);
			} else {
				this.sendMessage(msg);
			}
		} else if ( msg instanceof ChatServer.JoinedServerMessage ) {
			handleJoinServerMessage((ChatServer.JoinedServerMessage)msg);
		} else if ( msg instanceof MessageFetcher.CommunicationFailureMessage ) {
			if ( parent != null ) {
				parent.sendMessage(new UserDisconnectMessage(this));
				signalError();
			} else { // must make sure that chat server is aware that it lost a user!!
				this.sendMessage(msg);
			}
		}
	}
	
	public void handleChatMessage(MessageFetcher.ChatMessage msg) {
		JSONObject out = new JSONObject();
		try {
			out.put("type", "message");
			out.put("content", msg.contents);
			output.writeUTF(out.toString());
		} catch ( JSONException e ) {
			logAndPost("swallowed JSONException in handleChatMessage");
		} catch ( IOException e ) {
			logAndPost("swallowed IOException in handleChatMessage");
		}
	}
	
	public void handleJoinServerMessage(ChatServer.JoinedServerMessage msg) {
		parent = msg.server;
	}
	
	public String toString() {
		return "User#{ IP" + userSocket.getInetAddress().toString() + " }";
	}
	
	public enum UserCommand {
		JOIN_SERVER
	}
	
	public class PostedCommand {
		public final UserCommand cmd;
		public final String[] args;
		
		private PostedCommand(UserCommand cmd, String[] args) {
			this.cmd = cmd;
			this.args = args;
		}
	}
	
	public class UserDisconnectMessage {
		public final User user;
		
		private UserDisconnectMessage(User user) {
			this.user = user;
		}
		
		public String toString() {
			return "UserDisconnectMessage#{ " + user.toString() + " }";
		}
	}

	@Override
	protected void onError() {
		try {
			output.close();
		} catch (IOException e) {
			this.logAndPost("swallowed IOException\tUser.onError");
		}
	}

}
