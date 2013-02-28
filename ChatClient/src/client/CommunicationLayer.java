package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.json.JSONObject;


public class CommunicationLayer{

	private Socket s;
	private DataInputStream input;
	private DataOutputStream output;
	
	
	public CommunicationLayer(String ip){
		try{
		s = new Socket(ip/*"172.20.1.158"*//*"129.22.33.44"*/, 2013);
		input =  new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		s.setSoTimeout(500);
		}
		catch (Exception e){}
	}
	
	public void sendMessage(String message){
		try{
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("type", "message");
		jsonMessage.put("content", "xXx Hookers xXx: " + message);
		output.writeUTF(jsonMessage.toString());
		System.out.println("sent a message: " + jsonMessage.toString());
		}
		catch(Exception e){}
	}
	
	public String getMessage(){
		try{
		String serverMessage = input.readUTF();
		JSONObject messageReceived = new JSONObject(serverMessage);
		if(messageReceived.get("type").equals("message")) {
			return (String)messageReceived.get("content");
		}
		else
			return null;
		}
		catch (Exception e){}
		return null;
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
	
	public boolean checkForMessage(){
		
		return true; //TODO something here
	}
}
