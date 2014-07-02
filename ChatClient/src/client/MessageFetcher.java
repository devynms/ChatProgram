package client;

import java.io.DataInputStream;
import java.util.LinkedList;

import org.json.JSONObject;



public class MessageFetcher implements Runnable{
	
	private DataInputStream input;
	public LinkedList<String> messages;
	private CommunicationLayer layer;
	
	public MessageFetcher(CommunicationLayer layer){
		this.layer = layer;
		this.input = layer.input;
		messages = new LinkedList<String>();
	}
	
	private void GrabMessages(){
		while(true){
			Object message = null;
			try {
				System.out.println();
				String s = input.readUTF();
				JSONObject jsonMsg = new JSONObject(s);
				if (jsonMsg.getString("type").equals("message")) {
					message = jsonMsg.getString("content");
					System.out.println(message);
				}
			}
			catch(Exception e){}
			if(message != null){
				messages.add((String) message);
				layer.processMessage();
			}
		}
	}
	
	public void run(){
		while(true){
			GrabMessages();
		}
	}
}
