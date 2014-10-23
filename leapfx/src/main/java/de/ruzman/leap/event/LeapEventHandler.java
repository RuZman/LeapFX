package de.ruzman.leap.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

import de.ruzman.leap.LeapApp;

public final class LeapEventHandler extends Listener {	
	private static final LeapEventHandler SINGLETON = new LeapEventHandler();
	
	private List<LeapListener> leapListeners;
	private LeapEvent leapEvent;
		
	private LeapEventHandler() {
		leapListeners = new CopyOnWriteArrayList<>();
		leapEvent = new LeapEvent();
	}
	
	public static LeapEventHandler getInstance() {
		return SINGLETON;
	}
	
	public static void addLeapListener(LeapListener leapListener) {
		SINGLETON.leapListeners.add(leapListener);
	}
	
	public static void removeLeapListener(LeapListener leapListener) {
		SINGLETON.leapListeners.remove(leapListener);
	}
	
	public static void removeAllLeapListener() {
		SINGLETON.leapListeners.clear();
	}
	
	public static void fireFrameUpdate() {
		SINGLETON.onFrame(LeapApp.getController());
	}
	
	@Override
	public void onDisconnect(Controller controller) {
		leapEvent.setDisconnected(true);
		fireStatusChanged();
	}
	
	@Override
	public void onExit(Controller controller) {
		leapEvent.setExited(true);
		fireStatusChanged();
	}
	
	@Override
	public void onFocusLost(Controller controller) {
		leapEvent.setFocusLost(true);
		fireStatusChanged();
	}
	
	@Override
	public void onFrame(Controller controller) {
		if(controller.frame().hands().count() >= LeapApp.getMinimumHandNumber()) {	
			leapEvent.setFrame(controller.frame());
			
			for(LeapListener leapListener: leapListeners) {
				leapListener.update(leapEvent);
			}
		}
	}
	
	private void fireStatusChanged() {
		for(LeapListener leapListener: leapListeners) {
			leapListener.statusChanged(leapEvent);
		}
	}

	public static void updateFrame() {
		SINGLETON.onFrame(LeapApp.getController());
	}
}