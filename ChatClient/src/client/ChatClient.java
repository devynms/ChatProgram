package client;

import javax.swing.JOptionPane;

public class ChatClient{
	
	private CommunicationLayer layer;
	private Display gui;
	
	
	public ChatClient(){
		layer = new CommunicationLayer(this.IPDialog(), this);
		gui = new Display(this);
	}
	
	public void sendToLayer(String message){
		layer.sendMessage(message);
	}
	
	public void sendToGui(String message){
		gui.printMessage(message);
	}
	
	public void close(){
		layer.close();
		gui.close();
	}
	
	//prompt the user for the server ip
	public String IPDialog(){
		JOptionPane p = new JOptionPane();
		return p.showInputDialog("What IP would you like to connect to?");
	}
	
	public static void main(String[] args){
		
		new ChatClient();
		
	}
}
