package client;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JOptionPane;

/**
 * 
 * @author Austin Hacker
 *
 */
public class ChatClient{
	
	private CommunicationLayer layer;
	private Display gui;
	
	/**
	 * Starts up the chat client and makes sure that all components can communicate with each other.
	 */
	public ChatClient(){
		gui = new Display(this);
		layer = new CommunicationLayer(IPDialog(), this);
		layer.setDisplay(gui);
		layer.sendJson("server","user", UsernameDialog());
		gui.setCommunicationLayer(layer);
		layer.sendJson("client", "status", "online");
		layer.sendJson("server", "status", "all");
	}
	
	/**
	 * Closes all resources being consumed by the chat client.
	 * Any remaining resources are closed using System.exit(0)
	 */
	public void close(){
		System.out.println("Closing socket and data streams...");
		layer.close();
		System.out.println("Destroying gui...");
		gui.close();
		System.exit(0);
	}
	
	/**
	 * Prompts the user for the server IP address to connect to.
	 * 
	 * @return The server IP address
	 */
	public String IPDialog(){
		JOptionPane p = new JOptionPane();
		return p.showInputDialog("What IP would you like to connect to?");
	}
	
	/**
	 * Prompts the user for a nickname that will be visible to other users.
	 * 
	 * @return The user's name
	 */
	public String UsernameDialog(){
		JOptionPane p = new JOptionPane();
		return p.showInputDialog("What nickname would you like to use?");
	}
	
	/**
	 * A main method to start the chat client.
	 * No arguments are necessary.
	 * 
	 * @param args does nothing.
	 */
	public static void main(String[] args){
		
		new ChatClient();
		Hashtable<String, String> hash = new Hashtable<String, String>();
		hash.put("a", "b");
		hash.put("ac", "bq");
		hash.put("ad", "ba");
		System.out.println(hash.toString());
	}
}
