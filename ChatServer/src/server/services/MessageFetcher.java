package server.services;

import java.io.DataInputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import server.actors.User;

public class MessageFetcher extends Service {
	
	DataInputStream	input;
	
	public MessageFetcher(User master, DataInputStream userConnection) {
		super(master);
		input = userConnection;
	}

	@Override
	public Object procureService() {
		Object message = null;
		try {
			String s = input.readUTF();
			JSONObject jsonMsg = new JSONObject(s);
			if (jsonMsg.getString("type").equals("message")) {
				message = new ChatMessage(jsonMsg.getString("content"));
			}
		} catch ( IOException e ) {
			message = new CommunicationFailureMessage();
			signalError();
		} catch ( JSONException e ) {
			logAndPost("swallowed jsonexception\tUser no message");
		}
		postLog();
		return message;
	}
	
	@Override
	public String toString() {
		return "Message Fetcher#{ " + masterToString() + " }";
	}
	
	public class CommunicationFailureMessage {
		private CommunicationFailureMessage() {
			// private constructor
		}
	}
	
	public class ChatMessage {
		public final String contents;
		
		private ChatMessage(String contents) {
			this.contents = contents;
		}
		
		public String toString() {
			return "ChatMessage#{ " + contents + " }";
		}
	}

	@Override
	protected void onError() {
		try {
			input.close();
		} catch (IOException e) {
			this.logAndPost("swallowed IOException\tMessageFetcher.onError");
		}
	}
}
