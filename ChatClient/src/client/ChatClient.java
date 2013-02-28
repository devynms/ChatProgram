package client;

import javax.swing.JOptionPane;

public class ChatClient{
	
	public static String IPDialog(){
		JOptionPane p = new JOptionPane();
		return p.showInputDialog("What IP would you like to connect to?");
	}
	
	public static void main(String[] args){
		
		
		CommunicationLayer layer = new CommunicationLayer(ChatClient.IPDialog());
		Display gui = new Display(layer);

		while(layer.isConnected()) {
				if (!gui.isEmpty()) {
					String message = layer.getMessage();
					gui.printMessage(message);
				}
			if(gui.getFrame().isVisible() == false){
				System.out.println("closing...");
				layer.close();
				break;
			}
		}
	}
}
