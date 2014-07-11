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
	String name;
	String status;

	public User(Socket userSocket, ChatServer onlyServer) {
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
		this.parent = onlyServer;
		autoLog = false;
	}

	@Override
	public void runOnce() {
		Object msg = receiveMessage();
		if ( msg == null ) {
			Thread.yield();
		} else if (msg instanceof JSONObject){
			if(((JSONObject)msg).has("user")){
				handleJson((JSONObject)msg);
			} else {
				try {
					if (((JSONObject)msg).getString("destination").equals("server")){
						if (((JSONObject)msg).getString("type").equals("user")){
							name = ((JSONObject)msg).getString("content");
							return;
						}
					}
					if (((JSONObject)msg).getString("destination").equals("client")){
						if (((JSONObject)msg).getString("type").equals("status")){
							status = ((JSONObject)msg).getString("content");
						}
					}
					((JSONObject)msg).put("user", name);
					parent.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if ( msg instanceof ChatServer.JoinedServerMessage ) {
			log("handling ChatServer.JoinedServerMessage");
			handleJoinServerMessage((ChatServer.JoinedServerMessage)msg);
		} else if ( msg instanceof MessageFetcher.CommunicationFailureMessage ) {
			log("handling MessageFetcher.CommunicationFailureMessage");
			if ( parent != null ) {
				parent.sendMessage(new UserDisconnectMessage(this));
				signalError();
			} else { // must make sure that chat server is aware that it lost a user!!
				this.sendMessage(msg);
			}
		}
		postLog();
	}

	private void handleJson(JSONObject msg) {
		try{
			output.writeUTF(msg.toString());
		} catch (IOException e){
			logAndPost("swallowed IOException in handleJson");
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
			userSocket.close();
		} catch (IOException e) {
			this.logAndPost("swallowed IOException\tUser.onError");
		}
	}

}