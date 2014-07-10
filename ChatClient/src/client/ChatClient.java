package client;

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
		layer = new CommunicationLayer(this.IPDialog(), this);
		gui = new Display(this);
		layer.setDisplay(gui);
		gui.setCommunicationLayer(layer);
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
	
	//prompt the user for the server ip
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
	 * A main method to start the chat client.
	 * No arguments are necessary.
	 * 
	 * @param args does nothing.
	 */
	public static void main(String[] args){
		
		new ChatClient();
		
	}
}
