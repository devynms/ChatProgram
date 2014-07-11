package server.actors;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	public void handleJson(JSONObject msg) throws JSONException{
		if (msg.getString("destination").equals("server")){
			if (msg.getString("type").equals("status")){
				JSONObject[] statuses = getStatus();
				for(User u : users) {
					if(u.name.equals(msg.getString("user")))
				for(JSONObject status: statuses){
						u.sendMessage(status);
					}
				
				}
				return;
			}
		}
		log("caught chat message");
		for(User u : users) {
			u.sendMessage(msg);
		}
	}

	public void handleUserDisconnectMessage(User.UserDisconnectMessage msg) {
		log("handling user disconnect message");
		users.remove(msg.user);
	}
	
	public JSONObject[] getStatus(){
		JSONObject[] userStatuses = new JSONObject[users.size()];
		for(int i = 0; i < users.size(); i++){
			JSONObject jsonMessage = new JSONObject();
			try{
			jsonMessage.put("destination", "client");
			jsonMessage.put("type", "status");
			jsonMessage.put("content", users.get(i).status);
			jsonMessage.put("user", users.get(i).name);
			userStatuses[i] = jsonMessage;
			} catch(JSONException e){
				//TODO
			}
		}
		return userStatuses;
	}

	@Override
	public void runOnce() {
		Object msg = this.receiveMessage();
		log("handling message");
		if ( msg instanceof ServerListener.HandleUserMessage ) {
			handleHandleUserMessage((ServerListener.HandleUserMessage)msg);
		} else if ( msg instanceof User.UserDisconnectMessage ) {
			handleUserDisconnectMessage((User.UserDisconnectMessage)msg);
		} else if ( msg instanceof JSONObject){
			try {
				handleJson((JSONObject)msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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