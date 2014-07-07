package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import org.json.JSONObject;


public class CommunicationLayer{

	private Socket s;
	private DataInputStream input;
	private DataOutputStream output;
	public LinkedList<String> messages;
	private ChatClient client;
	
	public CommunicationLayer(String ip, ChatClient client){
		try{
		s = new Socket(ip, 2013);
		input =  new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		}
		catch (Exception e){}
		this.client = client;
		messages = new LinkedList<String>();
		MessageFetcher fetcher = new MessageFetcher();
		new Thread(fetcher).start();
	}
	
	public void sendMessage(String message){
		try{
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("type", "message");
		jsonMessage.put("content", "me: " + message);
		output.writeUTF(jsonMessage.toString());
		System.out.println("sent a message: " + jsonMessage.toString());
		}
		catch(Exception e){}
	}
	
	public void processMessage(){
		if (messages.peek() != null){
			client.sendToGui(messages.pop());
		}
	}
	
	public void close(){
		try{
		output.close();
		s.close();
		}
		catch(Exception e){}
	}
	
	public boolean isConnected(){
		return s.isConnected();
	}
	
	public class MessageFetcher implements Runnable{
		
		public MessageFetcher(){
		}
		
		private void GrabMessages(){
				Object message = null;
				try {
					String s = input.readUTF();
					JSONObject jsonMsg = new JSONObject(s);
					if (jsonMsg.getString("type").equals("message")) {
						message = jsonMsg.getString("content");
					}
				}
				catch(Exception e){}
				if(message != null){
					messages.add((String) message);
					processMessage();
				}
		}
		
		public void run(){
			while(s.isConnected()){
				GrabMessages();
			}
			System.out.println("halting fetcher");
			close();
		}
	}
}
