package gui;

import javax.swing.*;

public class LayoutChangeMonitor {
	private static LayoutChangeMonitor instance;
	private JComponent component;
	private String constrains;
	
	private LayoutChangeMonitor() {
		
	}
	
	public static LayoutChangeMonitor getInstance() {
		if (instance == null) {
			instance = new LayoutChangeMonitor();
		}
		return instance;
	}

	public synchronized void setLayout(JComponent component, String constrains) {
		this.component = component;
		this.constrains = constrains;
		notifyAll();
	}

	public synchronized JComponent getLayout() {
		try {
			wait();
			/*
				According to the documentation of the Java Condition interface:
					When waiting upon a Condition, a "spurious wakeup" is permitted to occur, in general, as a concession
					to the underlying platform semantics. This has little practical impact on most application programs as
					a Condition should always be waited upon in a loop, testing the state predicate that is being waited for.
					An implementation is free to remove the possibility of spurious wakeups but it is recommended that
					applications programmers always assume that they can occur and so always wait in a loop.
				The same advice is also found for the Object.wait(...) method:
					waits should always occur in loops, like this one:
  						synchronized (obj) {
 							while (<condition does not hold>) {
 								obj.wait(timeout);
 							}
 							 ... // Perform action appropriate to condition
 						}
			 */
		} catch (InterruptedException e) {
			/*
				"InterruptedExceptions should never be ignored in the code, and simply logging the exception counts in this case as "ignoring".
				The throwing of the InterruptedException clears the interrupted state of the Thread, so if the exception is not handled properly
				the fact that the thread was interrupted will be lost. Instead, InterruptedExceptions should either be rethrown - immediately or
				after cleaning up the method's state - or the thread should be re-interrupted by calling Thread.interrupt() even if this is supposed
				to be a single-threaded application. Any other course of action risks delaying thread shutdown and loses the information that the
				thread was interrupted - probably without finishing its task. Similarly, the ThreadDeath exception should also be propagated.
				According to its JavaDoc:
					If ThreadDeath is caught by a method, it is important that it be rethrown so that the thread actually dies."
			 */
		}
		return this.component;
	}

	public String getConstrains() {
		return this.constrains;
	}
}
