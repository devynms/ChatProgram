package server.actors;

import java.util.concurrent.*;
import server.Component;

public abstract class Actor extends Component {

	private LinkedBlockingQueue<Object> messageBox;
	//private Component	parent;
	
	protected Actor() {
		messageBox = new LinkedBlockingQueue<Object>();
	}
	
	public final boolean sendMessage( Object msg ) {
		boolean b = messageBox.offer(msg);
		return b;
	}
	
	protected final Object receiveMessage() {
		Object msg = null;
		try {
			msg = messageBox.take();
			log("receiving message: " + msg.toString());
		} catch (InterruptedException e) {
			logAndPost("swallowed interrupted exception");
		}
		return msg;
	}
	
	protected final Object receiveMessage(long timeoutMillis) {
		Object msg = null;
		try {
			msg = messageBox.poll(timeoutMillis, TimeUnit.MILLISECONDS);
			if(msg != null)
				log("receiving message: " + msg.toString());
		} catch (InterruptedException e) {
			logAndPost("swallowed interrupted exception in revieceMessage(timeoutMillis)");
		}
		return msg;
	}
}
