package client;

public class ChatClient{
	
	public static void main(String[] args){
		Display gui = new Display();
		CommunicationLayer c = new CommunicationLayer(gui);

		while(CommunicationLayer.isConnected()) {
				if (!gui.isEmpty()) {
					CommunicationLayer.sendMessage();
				
					CommunicationLayer.getMessage();
				}
			if(gui.getFrame().isVisible() == false){
				System.out.println("closing...");
				CommunicationLayer.close();
				break;
			}
		} 
	}
}
