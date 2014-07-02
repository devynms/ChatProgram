package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;

import org.json.JSONObject;


public class CommunicationLayer{

	private Socket s;
	DataInputStream input;
	private DataOutputStream output;
	private MessageFetcher fetcher;
	private Display gui;
	
	public CommunicationLayer(String ip){
		try{
		s = new Socket(ip, 2013);
		input =  new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		//s.setSoTimeout(500);
		}
		catch (Exception e){}
		gui = new Display(this);
		fetcher = new MessageFetcher(this);
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
		if (fetcher.messages.peek() != null){
			gui.printMessage(fetcher.messages.pop());
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
}
