package server.components;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Component {
	
	private static final int PORT_NO = 2013;
	
	private ServerSocket	listener;
	private	ChatServer		onlyServer;
	
	public ServerListener() {
		onlyServer = new ChatServer();
		Component.spawn(onlyServer);
		try {
			listener = new ServerSocket(PORT_NO);
		} catch (IOException e) {
			System.out.println("I died!");
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		ServerListener server = new ServerListener();
		System.out.println("starting...");
		Thread t = new Thread(server);
		t.run();
	}

	@Override
	public void runOnce() {
		try {
			Socket connection = listener.accept();
			User user = new User(connection);
			Component.spawn(user);
			onlyServer.sendMessage(new HandleUserMessage(user));
		} catch (IOException e) {
			System.out.println("couldn't make user");
		}
	}

	public class HandleUserMessage {
		public final User user;
		
		public HandleUserMessage(User user) {
			this.user = user;
		}
		
		public String toString() {
			return "HandleUserMessage#{ " + user.toString() + " }";
		}
	}
}
