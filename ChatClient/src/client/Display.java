package client;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author Austin Hacker
 *
 */
public class Display implements ActionListener{

	private String text;
	private JTextField typer;
	private JTextField enter;
	private JTextArea viewer;
	private JTextArea currentUsers;
	private JFrame frame;
	
	private ChatClient client;
	private CommunicationLayer layer;
	
	/**
	 * A constructor for Display. Creates the gui that the user will see.
	 * 
	 * @param client The ChatClient object that starts up the program. Needed for shutdown procedures.
	 */
	public Display(ChatClient client){
		frame = new JFrame();
		frame.setSize(600, 700);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowHandler());
		
		viewer = new JTextArea();
		viewer.setVisible(true);
		//viewer.setSize(300, 300);
		viewer.setEditable(false);
		
		typer = new JTextField();
		typer.setVisible(true);
		typer.setSize(0,0);
		typer.addActionListener(this);
		
		currentUsers = new JTextArea();
		currentUsers.setVisible(true);
		currentUsers.setEditable(false);
		
		Container c = frame.getContentPane();
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, currentUsers, viewer);
		pane.setDividerLocation((int)(frame.getWidth() / 3));
		c.add(pane);
		JSplitPane pane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, typer);
		pane2.setDividerLocation((int)(frame.getHeight() / 1.3));
		c.add(pane2);
		text = "";
		
		frame.setVisible(true);
		
		this.client = client;
	}
	
	/**
	 * A gettter method for the JTextField component of the gui.
	 * The JTextArea is the area where the user types his/her messages for the server.
	 * 
	 * @return the JTExtField component of the gui.
	 */
	public JTextField getTyper(){
		return typer;
	}
	
	/**
	 * A getter method for the JTextArea component of the gui.
	 * The JTextArea displays messages recieved from the server.
	 * 
	 * @return the JTExtArea component of the gui.
	 */
	public JTextArea getViewer(){
		return viewer;
	}
	
	/**
	 * A getter method for the JFrame component of the gui.
	 * The JFrame acts as the container for the entire gui.
	 * 
	 * @return the JFrame component of the gui.
	 */
	public JFrame getFrame(){
		return frame;
	}
	
	/**
	 * A getter method for the current content of the JTextField.
	 * This returns the message as the user has typed it thus far.
	 * 
	 * @return the current string within the JTextField.
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * A setter method for the CommunicationLayer parameter named layer.
	 * This setter is used to grant the gui access to the CommunicationLayer so that
	 * it may pass messages from the user to CommunicationLayer and finally to the server.
	 * 
	 * @param layer The CommunicationLayer object of this chat client.
	 */
	public void setCommunicationLayer(CommunicationLayer layer) {
		this.layer = layer;
		
	}
	
	/**
	 * A method to handle incoming messages from CommunicationLayer.
	 * Depending on how the message has been tagged from the original Json,
	 * this method will invoke the appropriate method to handle the
	 * correct response to the server's message.
	 * 
	 * @param type The type of message the server has sent.
	 * @param content The message from the server.
	 */
	public void recieveMessage(String type, String content) {
		if(type.equals("message")){
			printMessage(content);
		}
		if(type.equals("status")){
			updateStatus(content);
		}
	}
	
	/**
	 * A method to handle text messages from the server.
	 * Text messages will be displayed in the JTextArea of the gui.
	 * 
	 * @param message The string to be displayed in the JTextArea.
	 */
	public void printMessage(String message){
		viewer.append(message + "\n");
	}
	
	/**
	 * Dispays changes in a user's status
	 * @param status A string containing the name of a user and their new status
	 */
	public void updateStatus(String status){
		currentUsers.append(status + "\n");
	}
	
	/**
	 * Clears the JTextField.
	 * This is useful when the user decides to send a message.
	 */
	public void clear(){
		text = "";
	}
	
	/**
	 * Returns whether or not there is any text in the JTextField.
	 * This method is used to prevent useless messages from being sent to the
	 * server and consequently being displayed in the JTextArea.
	 * 
	 * @return true if there is text in the typer.
	 */
	public boolean isEmpty(){
		return text.equals("");
	}
	
	/**
	 * A method to close all resources being used by the gui.
	 */
	public void close(){
		frame.dispose();
	}
	
	/**
	 * A method to send the CommunicationLayer a text message.
	 * This method is called whenever the enter key is pressed.
	 * When this happens, the message is checked for legitamacy,
	 * then sent to the CommunicationLayer to send to the server.
	 * The typer is then cleared of all content.
	 * <p>
	 * This method is required due to implementing ActionListener.
	 */
	public void actionPerformed(ActionEvent e){
		enter = (JTextField)e.getSource();
		this.text = enter.getText();
		typer.setText("");
		layer.sendJson("client", "message", text);
		this.clear();
	}
	
	/**
	 * 
	 * @author Austin Hacker
	 *
	 */
	class WindowHandler extends WindowAdapter{
		
		/**
		 * Starts shutdown procedures if the user closes the gui.
		 */
		@Override
		public void windowClosing(WindowEvent e){
			client.close();
		}
		
		/**
		 * Updates status when the user clicks out of the gui.
		 * Status will be set to away.
		 */
		@Override
		public void windowLostFocus(WindowEvent e){
			layer.sendJson("client", "status", "away");
		}
		
		/**
		 * Updates status when the user clicks back into the gui.
		 * Status will be set to online.
		 */
		@Override
		public void windowGainedFocus(WindowEvent e){
			layer.sendJson("client","status", "online");
		}
	}
}
