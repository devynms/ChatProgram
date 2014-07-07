package client;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Display implements ActionListener{

	private String text;
	private JTextField typer;
	private JTextField enter;
	private JTextArea viewer;
	private JFrame frame;
	private ChatClient client;
	
	public Display(ChatClient client){
		frame = new JFrame();
		frame.setSize(600, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		viewer = new JTextArea();
		viewer.setVisible(true);
		//viewer.setSize(300, 300);
		viewer.setEditable(false);
		
		typer = new JTextField();
		typer.setVisible(true);
		typer.setSize(0,0);
		typer.addActionListener(this);
		
		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, viewer, typer);
		pane.setDividerLocation((int)(frame.getHeight() / 1.3));
		Container c = frame.getContentPane();
		c.add(pane);
		
		text = "";
		
		frame.setVisible(true);
		
		this.client = client;
	}
	
	public JTextField getTyper(){
		return typer;
	}
	
	public JTextArea getViewer(){
		return viewer;
	}
	
	public void printMessage(String message){
		viewer.append(message + "\n");
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public String getText(){
		return text;
	}
	
	public void clear(){
		text = "";
	}
	
	public boolean isEmpty(){
		return text.equals("");
	}
	
	public void close(){
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent e){
		enter = (JTextField)e.getSource();
		this.text = enter.getText();
		typer.setText("");
		client.sendToLayer(text);
		this.clear();
	}	
}
