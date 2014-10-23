package de.ruzman.leap.fx;

import java.util.Iterator;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;

public class HandFX3D extends Group {
	private Sphere palm;
	private Sphere wrist;
	private Sphere[] fingers = new Sphere[5];
	private Sphere[] distal = new Sphere[5];
	private Sphere[] proximal = new Sphere[5];
	private Sphere[] intermediate = new Sphere[5];
	
	public HandFX3D(int handId) {			
		palm = createSphere();
		wrist = createSphere();
		
		for(int i = 0; i < fingers.length; i++) {
			fingers[i] = createSphere();
			distal[i] = createSphere();
			intermediate[i] = createSphere();
			proximal[i] = createSphere();
			
			getChildren().addAll(fingers[i], distal[i], 
					proximal[i], intermediate[i]);
		}
		
		getChildren().addAll(palm, wrist);			
	}
	
	private Sphere createSphere() {
		Sphere sphere = new Sphere(5);
		
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.RED);
		material.setDiffuseColor(Color.DARKRED);
		sphere.setMaterial(material);
		
		return sphere;
	}		
	
	public void update(Hand hand) {
		FXUtil.transform(palm, hand.palmPosition());
		FXUtil.transform(wrist, hand.wristPosition());
		
		Iterator<Finger> itFinger = hand.fingers().iterator();
		
		for(int i = 0; i < fingers.length; i++) {
			Finger finger = itFinger.next();
			
			FXUtil.transform(fingers[i], finger.tipPosition());
			FXUtil.transform(distal[i], finger.bone(Type.TYPE_DISTAL).prevJoint());
			FXUtil.transform(intermediate[i], finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			FXUtil.transform(proximal[i], finger.bone(Type.TYPE_PROXIMAL).prevJoint());
		}
	}
}