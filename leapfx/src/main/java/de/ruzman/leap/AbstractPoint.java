package de.ruzman.leap;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Vector;

import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointEvent.Zone;
import de.ruzman.leap.event.PointListener;
import de.ruzman.leap.event.PointMotionListener;

public abstract class AbstractPoint {
	private int id;
	protected boolean isActive;

	private Vector diameter;

	private float maxXPos;
	private float minYPos;

	protected TrackingBox trackingBox;

	/** PointMotion Listener */
	private List<PointMotionListener> pointMotionListeners;
	private EnumSet<Zone> clickZone;
	private Vector point;
	private Vector position;

	/** Point Listener */
	private List<PointListener> pointListeners;
	private PointEvent pointEvent;
	private Vector zone;
	private EnumSet<Zone> zones;
	private EnumSet<Zone> prevZones;
	private EnumSet<Zone> bufferedZone;

	public AbstractPoint(int id) {
		this(id, new TrackingBox());
	}

	public AbstractPoint(int id, TrackingBox trackingBox) {
		this.id = id;
		this.trackingBox = trackingBox;
		isActive = true;

		pointMotionListeners = new CopyOnWriteArrayList<>();
		clickZone = EnumSet.noneOf(Zone.class);
		position = new Vector();

		pointListeners = new CopyOnWriteArrayList<>();
		zone = new Vector();
		zones = EnumSet.of(Zone.UNKOWN);
		prevZones = EnumSet.of(Zone.UNKOWN);
	}

	public int id() {
		return id;
	}

	protected void update(Frame frame, Vector point, Vector stabilizedPoint) {
		this.point = point;
		
		trackingBox.calcScreenPosition(stabilizedPoint, position);
		trackingBox.calcZone(point, zone);

		if (diameter != null) {
			if (position.getX() < 0) {
				position.setX(0);
			} else if (position.getX() > maxXPos) {
				position.setX(maxXPos);
			}

			if (position.getY() < 0) {
				position.setY(0);
			} else if (position.getY() > minYPos) {
				position.setY(minYPos);
			}
		}
	}

	public void destroy() {
		isActive = false;
		updateEvents();
	}

	public void addPointListener(PointListener pointListener) {
		pointListeners.add(pointListener);
	}

	public void removePointListener(PointListener pointListener) {
		pointListeners.remove(pointListener);
	}
	
	public void addPointMotionListener(PointMotionListener pointMotionListener) {
		pointMotionListeners.add(pointMotionListener);
	}

	public void removePointMotionListener(
			PointMotionListener pointMotionListener) {
		pointMotionListeners.remove(pointMotionListener);
	}

	protected void updateEvents() {
		pointEvent = new PointEvent(this, zones, prevZones,
				clickZone);
		fireUpdateZone();
		fireUpdatePosition();
		fireUpdateDragg();
	}

	public void fireUpdatePosition() {
		for (PointMotionListener pointMotionListener : pointMotionListeners) {
			pointMotionListener.pointMoved(pointEvent);
		}
	}

	public void fireUpdateDragg() {
		if (zones.containsAll(clickZone)) {
			for (PointMotionListener pointMotionListener : pointMotionListeners) {
				pointMotionListener.pointDragged(pointEvent);
			}
		}
	}
	
	public void fireUpdateZone() {
		if (updateZone()) {
			for (PointListener pointListener : pointListeners) {
				pointListener.zoneChanged(pointEvent);
			}
		}
	}

	private boolean updateZone() {
		if (!isActive) {
			bufferedZone = EnumSet.of(Zone.UNKOWN);
		} else {
			bufferedZone = EnumSet.of(
					getZone(zone.getX(), Zone.RIGHT, Zone.LEFT),
					getZone(zone.getY(), Zone.DOWN, Zone.UP),
					getZone(zone.getZ(), Zone.BACK, Zone.FRONT));
		}

		if (!bufferedZone.equals(zones)) {
			prevZones.clear();
			prevZones.addAll(zones);
			zones.clear();
			zones.addAll(bufferedZone);

			return true;
		} else {
			return false;
		}
	}

	private Zone getZone(float value, Zone zone0, Zone zone1) {
		switch ((int) (value + 1)) {
		case 0:
			return zone0;
		case 1:
			return zone1;
		default:
			return Zone.OUTSIDE;
		}
	}

	public final void setDiameter(Vector diameter) {
		if (diameter != null) {
			maxXPos = LeapApp.getDisplayWidth() - diameter.getX();
			minYPos = LeapApp.getDisplayHeight() - diameter.getY();
		}

		this.diameter = diameter;
	}
	
	public void setTrackingBox(TrackingBox trackingBox) {
		this.trackingBox = trackingBox;
	}

	public final void setClickZone(EnumSet<Zone> zones) {
		clickZone.clear();
		clickZone.addAll(zones);
	}
	
	public Vector getPosition() {
		return new Vector(position);
	}
	
	public Vector getPoint() {
		return new Vector(point);
	}
}
