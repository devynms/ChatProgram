package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.json.JSONObject;


public class CommunicationLayer{

	private static Socket s;
	private static DataInputStream input;
	private static DataOutputStream output;
	private static Display gui;
	
	
	public CommunicationLayer(Display gui){
		try{
		s = new Socket("172.20.1.158"/*"129.22.33.44"*/, 2013);
		input =  new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		this.gui = gui;
		}
		catch (Exception e){}
	}
	
	public static void sendMessage(){
		try{
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("type", "message");
		jsonMessage.put("content", "xXx Hookers xXx: " + gui.getText());
		output.writeUTF(jsonMessage.toString());
		gui.clear();
		}
		catch(Exception e){}
	}
	
	public static void getMessage(){
		try{
		String serverMessage = input.readUTF();
		JSONObject messageReceived = new JSONObject(serverMessage);
		if(messageReceived.get("type").equals("message")) {
			gui.getViewer().append(messageReceived.get("content") + "\n");
		}
		}
		catch (Exception e){}
	}
	
	public static void close(){
		try{
		s.close();
		}
		catch(Exception e){}
	}
	
	public static boolean isConnected(){
		return s.isConnected();
	}
}
