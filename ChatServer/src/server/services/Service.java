package server.services;

import server.Component;
import server.actors.Actor;

public abstract class Service extends Component {
	
	private final Actor master;
	
	protected String masterToString() {
		return master.toString();
	}
	
	public Service(Actor master) {
		this.master = master;
	}
	
	// result is a message
	public abstract Object procureService();
	
	@Override
	public final void runOnce() {
		Object serviceMessage = procureService();
		if (serviceMessage != null) {
			master.sendMessage(serviceMessage);
			logAndPost("sending message " + serviceMessage.toString() + " to master " + master.toString());
		}
	}
}
