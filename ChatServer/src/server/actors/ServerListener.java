package server.actors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Actor {
	
	private static final int PORT_NO = 2013;
	
	private ServerSocket	listener;
	private	ChatServer		onlyServer;
	
	public ServerListener() {
		onlyServer = new ChatServer();
		Actor.spawn(onlyServer);
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
		t.start();
	}

	@Override
	public void runOnce() {
		try {
			System.out.println("try accept");
			Socket connection = listener.accept();
			System.out.println("accepted user");
			User user = new User(connection);
			Actor.spawn(user);
			onlyServer.sendMessage(new HandleUserMessage(user));
		} catch (IOException e) {
			System.out.println("couldn't make user");
		}
	}
	
	@Override
	public String toString() {
		return "ServerListener";
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

	@Override
	protected void onError() {
		try {
			listener.close();
		} catch ( IOException e ) {
			logAndPost("swallowed IOException\tServerListener.onError");
		}
	}
}
