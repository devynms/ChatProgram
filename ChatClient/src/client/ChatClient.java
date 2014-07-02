package client;

import javax.swing.JOptionPane;

public class ChatClient{
	
	public static String IPDialog(){
		JOptionPane p = new JOptionPane();
		return p.showInputDialog("What IP would you like to connect to?");
	}
	
	public static void main(String[] args){
		
		CommunicationLayer layer = new CommunicationLayer(ChatClient.IPDialog());

		
	}
}
