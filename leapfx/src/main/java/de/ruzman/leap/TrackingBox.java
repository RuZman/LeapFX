package de.ruzman.leap;

import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

public class TrackingBox {
	private static final Vector DEFAULT_BOX;
	private static final Vector DEFAULT_ORIGIN;
	
	static {
		InteractionBox iBox = LeapApp.getController().frame().interactionBox();

		DEFAULT_ORIGIN = new Vector(-(iBox.width()/2)-iBox.center().getX(), 
				-(iBox.height()/2)+iBox.center().getY(), -(iBox.depth()/2)-iBox.center().getZ());
		DEFAULT_BOX = new Vector(iBox.width(), iBox.height(), iBox.depth());
	}
	private Vector box;
	private Vector origin;
	private Vector centroid;
	private Vector step;

	private Vector centroidOrigin;
	private Vector halfBox;
	
	public TrackingBox() {
		this(DEFAULT_BOX, DEFAULT_ORIGIN);
	}

	public TrackingBox(Vector box) {
		this(box, new Vector(-box.getX()/2, 100, box.getZ()/2));
	}

	public TrackingBox(Vector box, Vector origin) {
		setBox(box);
		setOrigin(origin);
	}

	public void setBox(Vector box) {
		this.box = new Vector(box);
		halfBox = box.times(0.5f);

		setStep();
	}

	public void setOrigin(Vector origin) {
		this.origin = new Vector(origin);
		setCentroid();
	}

	private void setStep() {
		step = new Vector(LeapApp.getDisplayWidth() / box.getX(),
				LeapApp.getDisplayHeight() / box.getY(), 1);
	}

	private void setCentroid() {
		centroid = new Vector(box.getX()/2, box.getY()/2, box.getZ()/2);
		centroidOrigin = origin.plus(centroid);
	}
	
	public void calcNormalizedPoint(Vector point, Vector normalizedPoint) {
		normalizedPoint.setX((point.getX()-centroidOrigin.getX())/box.getX());
		normalizedPoint.setY((point.getY()-centroidOrigin.getY())/box.getX());
		normalizedPoint.setZ((point.getZ()-centroidOrigin.getZ())/box.getX());
	}
	
	public void calcScreenPosition(Vector point, Vector position) {
		position.setX((point.getX()-origin.getX())*step.getX());
		position.setY(LeapApp.getDisplayHeight() - ((point.getY()-origin.getY())*step.getY()));
		position.setZ((point.getZ()-origin.getX())*step.getZ());
	}
	
	public void calcZone(Vector point, Vector zone) {
		zone.setX((point.getX()-centroidOrigin.getX())/halfBox.getX());
		zone.setY((point.getY()-centroidOrigin.getY())/halfBox.getY());
		zone.setZ((point.getZ()-centroidOrigin.getZ())/halfBox.getZ());
	}
	
	public static TrackingBox buildOneSideTrackingBox(boolean isLeft) {
		if(isLeft) {
			return new TrackingBox(new Vector(100,150,200), new Vector(-160,100,-100));
		} else {
			return new TrackingBox(new Vector(100,150,200), new Vector(60,100,-100));
		}
	}
}