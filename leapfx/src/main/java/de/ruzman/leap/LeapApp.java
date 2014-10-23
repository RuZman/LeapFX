package de.ruzman.leap;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;

import com.leapmotion.leap.Controller;

import de.ruzman.leap.event.LeapEventHandler;

public final class LeapApp {
	public enum Mode {
		// TODO: Better solution ...
		DYNAMIC_ONE_SIDE, INTERACTION_BOX;
	}
	private static LeapApp singleton;
	
	private MotionRegistry motionRegistry;
	private AWTDispatcher awtDispatcher;
	private Controller controller;
	
	private int displayWidth;
	private int displayHeight;
	
	private Mode mode;
	private int maximumHandNumber;
	private int minimumHandNumber;
		
	private LeapApp(boolean activatePolling) {
		NativeLibrary.loadSystem("native");
				
		// TODO: Multiscreen ...
		GraphicsDevice device = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode dispMode = device.getDisplayMode();
		
		displayWidth = dispMode.getWidth();
		displayHeight = dispMode.getHeight();
		
		controller = new Controller();
		if(!activatePolling) {
			controller.addListener(LeapEventHandler.getInstance());
		}

		mode = Mode.DYNAMIC_ONE_SIDE;
		maximumHandNumber = Integer.MAX_VALUE;
		
		motionRegistry = new MotionRegistry();
		LeapEventHandler.addLeapListener(motionRegistry);
	}
	
	public static void init(boolean activePolling) {
		if(singleton == null) {
			singleton = new LeapApp(activePolling);
		}
	}
	
	public static LeapApp getInstance() {
		return singleton;
	}
	
	public static MotionRegistry getMotionRegistry() {
		return singleton.motionRegistry;
	}
	
	public static int getDisplayWidth() {
		return singleton.displayWidth;
	}
	
	public static void setDisplayWidth(int displayWidth) {
		singleton.displayWidth = displayWidth;
	}
	
	public static int getDisplayHeight() {
		return singleton.displayHeight;
	}
	
	public static void setDisplayHeight(int displayHeight) {
		singleton.displayHeight = displayHeight;
	}
	
	public void setMotionRegistry(MotionRegistry motionRegistry) {
		if(motionRegistry != null) {
			LeapEventHandler.removeLeapListener(this.motionRegistry);
			this.motionRegistry = motionRegistry;
			LeapEventHandler.addLeapListener(motionRegistry);
		}
	}
	
	public static WindowAdapter getAndSetupAWTMouseListener() {
		if(singleton.awtDispatcher == null) {
			singleton.awtDispatcher = new AWTDispatcher();
			singleton.motionRegistry.setAWTDispatcher(singleton.awtDispatcher);
			
		}
		return singleton.awtDispatcher;
	}
	
	public static void setMinimumHandNumber(int minimumHandNumer) {
		singleton.minimumHandNumber = minimumHandNumer;
	}
	
	public static int getMinimumHandNumber() {
		return singleton.minimumHandNumber;
	}
	
	public static void setMaximumHandNumber(int maximumHandNumer) {
		singleton.maximumHandNumber = maximumHandNumer;
	}
	
	public static int getMaximumHandNumber() {
		return singleton.maximumHandNumber;
	}
	
	public static void setMode(Mode mode) {
		singleton.mode = mode;
	}
	
	public static Mode getMode() {
		return singleton.mode;
	}
	
	public static Controller getController() {
		return singleton.controller;
	}	
	
	public static void destroy() {
		LeapEventHandler.removeAllLeapListener();
		singleton.controller.delete();
		try {
			singleton.finalize();
		} catch(Throwable t) {
			// Do nothing.
		} finally {
			singleton = null;
			System.exit(0);
		}
	}
	
	public static void update() {
		LeapEventHandler.updateFrame();
	}
}
