package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Component implements Runnable {
	
	private static ExecutorService exec = Executors.newCachedThreadPool();
	
	public static void spawn(Component child) {
		System.out.println("spawning component " + child);
		exec.execute(child);
	}
	
	private boolean isGood = true;
	
	public abstract void runOnce();
	
	protected abstract void onError();
	
	protected void signalError() {
		isGood = false;
	}
	
	@Override
	public final void run() {
		while( isGood ) {
			runOnce();
			if(logMessage != null) { // catch unfinished logs
				if(autoLog)
					postLog();
				else
					logMessage = null;
			}
		}
	}
	
	private	StringBuilder 	logMessage;
	protected	boolean		autoLog;
	
	protected final void log(String msg) {
		if ( logMessage == null ) {
			logMessage = new StringBuilder();
			logMessage.append("LOG      ").append(this.toString()).append("\n");
		}
		logMessage.append("|   ").append(msg).append("\n");
	}
	
	protected final void postLog() {
		if ( logMessage == null ) {
			log("Empty post.");
		}
		System.out.println(logMessage.toString());
		logMessage = null;
	}
	
	protected final void logAndPost(String msg) {
		log(msg);
		postLog();
	}
}
