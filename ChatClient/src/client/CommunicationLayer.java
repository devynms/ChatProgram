package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Austin Hacker
 *
 */
public class CommunicationLayer{

	private Socket s;
	private DataInputStream input;
	private DataOutputStream output;
	private LinkedList<JSONObject> messages;
	private ChatClient client;
	private Display display;
	
	/**
	 * A constructor for CommunicationLayer.
	 * 
	 * @param ip The IP address of the server.
	 * @param client An object that contains startup and shutdown procedures.
	 */
	public CommunicationLayer(String ip, ChatClient client){
		try{
		s = new Socket(ip, 2013);
		input =  new DataInputStream(s.getInputStream());
		output = new DataOutputStream(s.getOutputStream());
		} catch (IOException e){
			System.out.println("Error establishing connection to the server. Will shutdown client.");
			client.close();
		}
		this.client = client;
		messages = new LinkedList<JSONObject>();
		MessageFetcher fetcher = new MessageFetcher();
		new Thread(fetcher).start();
	}
	
	/**
	 * Sets the Display parameter for this object. This allows this object to send messages to the gui.
	 * 
	 * @param display A Display object that acts as the gui of the chat client.
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * Packages a JSON to send to the server.
	 * 
	 * @param type The type of message being sent.
	 * @param content The message being sent.
	 */
	protected void sendJson(String destination, String type, String content) {
		try{
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("destination", destination);
			jsonMessage.put("type", type);
			jsonMessage.put("content", content);
			output.writeUTF(jsonMessage.toString());
			System.out.println("sent a message: " + jsonMessage.toString());
			} catch(JSONException e){
				System.out.println("Null key. Message will not be sent.");
			} catch (IOException e) {
				System.out.println("Error sending message. Message will not be sent.");
			}
	}
	
	/**
	 * Unpacks a JSON to send to the gui.
	 * 
	 * @throws JSONException if value of type or content is null
	 */
	private void processMessage(){
		if (messages.peek() != null){
			JSONObject message = messages.pop();
			try{
			display.recieveMessage(message.getString("type"),
					message.getString("user") + ": " + message.getString("content"));
			} catch (JSONException e) {
				System.out.println("Incoming message lacks type or content");
			}
		}
	}
	
	/**
	 * Closes resources being consumed by this object.
	 * 
	 * @throws IOException if an IO error occurs while closing the socket
	 */
	protected void close(){
		try{
		s.close();
		} catch(IOException e){
			System.out.println("Error upon closing the socket.");
		}
	}
	
	/**
	 * Checks if the socket is still connected to the server.
	 * 
	 * @return true if the server is still connected.
	 */
	public boolean isConnected(){
		return s.isConnected();
	}
	
	/**
	 * An object to listen to the server for messages.
	 * 
	 * @author Austin Hacker
	 *
	 */
	class MessageFetcher implements Runnable{
		
		boolean halt = false;
		
		/**
		 * Listens to the server for incoming messages and stores any messages in a LinkedList for the
		 * CommunicationLayer object to unpack and send on to the gui.
		 * 
		 * @throws JSONException if the message is not a JSONObject
		 * @throws IOException if an error occured while reading from the server.
		 */
		private void GrabMessages(){
			try {
				String message = input.readUTF();
				JSONObject jsonMsg = new JSONObject(message);
				messages.add(jsonMsg);
				processMessage();
			} catch(JSONException e){
				System.out.println("Incoming message not a JSON text string. Message will be discarded.");
			} catch (IOException e) {
				System.out.println("Error upon reading from input.");
				halt = true;
			}
		}
		
		/**
		 * Continuously checks the server for messages while the socket is connected to the server.
		 * When connection to the server is lost, the client will shutdown.
		 */
		public void run(){
			while(!halt){
				GrabMessages();
			}
			System.out.println("Server disconnected. Ending client.");
			client.close();
		}
	}
}
