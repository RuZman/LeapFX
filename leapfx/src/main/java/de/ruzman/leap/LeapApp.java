package de.ruzman.leap;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;

import com.leapmotion.leap.Controller;

import de.ruzman.leap.event.LeapEventHandler;

public final class LeapApp {
	private static LeapApp instance;

	private AWTDispatcher awtDispatcher;
	private MotionRegistry motionRegistry;
	private TrackingBox trackingBox;
	private int minimumHandNumber = 1;
	private int maximumHandNumber = Integer.MAX_VALUE;
	private int displayWidth;
	private int displayHeight;
	private boolean usePolling;
	
	private Controller controller;
	
	private LeapApp(Controller controller) {
		this.controller = controller;
	}
	
	private void init(TrackingBox trackingBox,
			int minimumHandNumber,
			int maximumHandNumber,
			int displayWidth,
			int displayHeight,
			boolean usePolling,
			boolean activateAWTDispatcher,
			MotionRegistry motionRegistry) {
		
		this.trackingBox = trackingBox;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		setMinimumHandNumber(minimumHandNumber);
		setMaximumHandNumber(maximumHandNumber);
		this.usePolling = usePolling;
		
		if(!usePolling) {
			controller.addListener(LeapEventHandler.getInstance());
		}
		
		this.motionRegistry = motionRegistry;
		LeapEventHandler.addLeapListener(motionRegistry);
		
		if(activateAWTDispatcher) {
			getAWTMouseListener();
		}
	}
	
	public static void setTrackingBox(TrackingBox trackingBox) {
		instance.trackingBox = trackingBox;
	}
	
	public static TrackingBox getTrackingBox() {
		return instance.trackingBox;
	}
	
	public static void setMinimumHandNumber(int minimumHandNumber) {
		validateHandNumber();
		instance.minimumHandNumber = minimumHandNumber;
	}
	
	public static int getMinimumHandNumber() {
		return instance.minimumHandNumber;
	}
	
	public static void setMaximumHandNumber(int maximumHandNumber) {
		validateHandNumber();
		instance.maximumHandNumber = maximumHandNumber;
	}
	
	public static int getMaximumHandNumber() {
		return instance.maximumHandNumber;
	}
	
	private static void validateHandNumber() {
		if(instance.maximumHandNumber < instance.minimumHandNumber) {
			throw new IllegalArgumentException("MaximumHandNumber must be >= minumumHandNumber");
		}
		
		if(instance.maximumHandNumber < 1) {
			throw new IllegalArgumentException("MinimumHandNumber must be >= 1");
		}
		
		if(instance.minimumHandNumber < 1) {
			throw new IllegalArgumentException("MinimumHandNumber must be >= 1");
		}
	}
	
	public static void setDisplayWidth(int displayWidth) {
		instance.displayWidth = displayWidth;
	}
	
	public static int getDisplayWidth() {
		return instance.displayWidth;
	}
	
	public static void setDisplayHeight(int displayHeight) {
		instance.displayHeight = displayHeight;
	}
	
	public static int getDisplayHeight() {
		return instance.displayHeight;
	}
	
	public static void setMotionRegistry(MotionRegistry motionRegistry) {
		if(motionRegistry != null) {
			LeapEventHandler.removeLeapListener(instance.motionRegistry);
			instance.motionRegistry = motionRegistry;
			LeapEventHandler.addLeapListener(motionRegistry);
		}
	}
	
	public static MotionRegistry getMotionRegistry() {
		return instance.motionRegistry;
	}
	
	public static Controller getController() {
		return instance.controller;
	}
	
	public static void update() {
		if(instance.usePolling) {
			LeapEventHandler.updateFrame();
		}
	}
	
	public static WindowAdapter getAWTMouseListener() {
		if(instance.awtDispatcher == null) {
			instance.awtDispatcher = new AWTDispatcher();
			instance.motionRegistry.setAWTDispatcher(instance.awtDispatcher);
		}
		return instance.awtDispatcher;
	}
	
	public static void destroy() {
		LeapEventHandler.removeAllLeapListener();
		instance.controller.delete();
		try {
			instance.finalize();
		} catch(Throwable t) {
			// Do nothing.
		} finally {
			instance = null;
			System.exit(0);
		}
	}
	
	public static class LeapAppBuilder {
		private TrackingBox trackingBox;
		private int minimumHandNumber = 1;
		private int maximumHandNumber = Integer.MAX_VALUE;
		private int displayWidth = -1;
		private int displayHeight = -1;
		private boolean usePolling = true;
		private boolean activeAWTDispatcher = false;
		private MotionRegistry motionRegistry;
		
		public LeapAppBuilder() {
			this(true);
		}
		
		public LeapAppBuilder(boolean shouldLoadNativeLibraries) {
			this(shouldLoadNativeLibraries, "native");
		}
		
		public LeapAppBuilder(boolean shouldLoadNativeLibraries, String path) {
			if(shouldLoadNativeLibraries) {
				NativeLibrary.loadSystem(path);
			}
			
			instance = new LeapApp(new Controller());
			trackingBox = new TrackingBox();
			motionRegistry = new MotionRegistry();
		}
		
		public LeapApp createLeapApp() {
			return createLeapApp(true);
		}
		
		public LeapApp createLeapApp(boolean shouldOverwiteInstance) {
			if(instance != null && !shouldOverwiteInstance) {
				throw new IllegalArgumentException("The instance of LeapApp is already initialized.");
			}

			initDisplaySize();
			instance.init(trackingBox,
					minimumHandNumber,
					maximumHandNumber,
					displayWidth,
					displayHeight,
					usePolling,
					activeAWTDispatcher,
					motionRegistry);
			
			return instance;
		}
		
		private void initDisplaySize() {
			GraphicsDevice device = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			DisplayMode dispMode = device.getDisplayMode();
			
			if(displayWidth < 0) {
				displayWidth = dispMode.getWidth();
			}
			if(displayHeight < 0) {
				displayHeight = dispMode.getHeight();
			}
		}
		
		public LeapAppBuilder trackingBox(TrackingBox trackingBox) {
			this.trackingBox = trackingBox;
			return this;
		}
		
		public LeapAppBuilder minimumHandNumber(int minimumHandNumber) {
			if(minimumHandNumber < 1) {
				throw new IllegalArgumentException("MinimumHandNumber must be >= 1");
			}
			this.minimumHandNumber = minimumHandNumber;
			return this;
		}
		
		public LeapAppBuilder maximumHandNumber(int maximumHandNumber) {
			if(maximumHandNumber < 1) {
				throw new IllegalArgumentException("MinimumHandNumber must be >= 1");
			}
			this.maximumHandNumber = maximumHandNumber;
			return this;
		}
		
		public LeapAppBuilder displayWidth(int displayWidth) {
			this.displayWidth = displayWidth;
			return this;
		}
		
		public LeapAppBuilder displayHeight(int displayHeight) {
			this.displayHeight = displayHeight;
			return this;
		}
		
		public LeapAppBuilder usePolling(boolean usePolling) {
			this.usePolling = usePolling;
			return this;
		}
		
		public LeapAppBuilder activeAWTDispatcher() {
			this.activeAWTDispatcher = true;
			return this;
		}
		
		public LeapAppBuilder motionRegistry(MotionRegistry motionRegistry) {
			this.motionRegistry = motionRegistry;
			return this;
		}
	}
}