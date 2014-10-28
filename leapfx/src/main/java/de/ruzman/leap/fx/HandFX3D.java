package de.ruzman.leap.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;

public class HandFX3D extends Group {
	private Sphere palm;
	private Sphere metacarpal;
	private Sphere[] fingers = new Sphere[5];
	private Sphere[] distal = new Sphere[5];
	private Sphere[] proximal = new Sphere[5];
	private Sphere[] intermediate = new Sphere[5];

	private List<JointFX3D> joints;

	public HandFX3D(int handId) {
		joints = new ArrayList<>();

		palm = createSphere();
		metacarpal = createSphere();

		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = createSphere();
			distal[i] = createSphere();
			intermediate[i] = createSphere();
			proximal[i] = createSphere();

			getChildren().addAll(fingers[i], distal[i], proximal[i],
					intermediate[i]);
		}

		getChildren().addAll(palm, metacarpal);

		for (int i = 0; i < fingers.length; i++) {
			connectSpheres(fingers[i], distal[i]);
			connectSpheres(distal[i], intermediate[i]);
			connectSpheres(intermediate[i], proximal[i]);
		}

		connectSpheres(proximal[1], proximal[2]);
		connectSpheres(proximal[2], proximal[3]);
		connectSpheres(proximal[3], proximal[4]);
		connectSpheres(proximal[0], proximal[1]);
		connectSpheres(proximal[0], metacarpal);
		connectSpheres(metacarpal, proximal[4]);
	}

	private Sphere createSphere() {
		Sphere sphere = new Sphere(5);

		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.RED);
		material.setDiffuseColor(Color.DARKRED);
		sphere.setMaterial(material);

		return sphere;
	}

	private void connectSpheres(Sphere fromSphere, Sphere toSphere) {
		JointFX3D jointFX3D = new JointFX3D(fromSphere, toSphere);
		joints.add(jointFX3D);
		getChildren().add(jointFX3D.getBone());
	}

	public void update(Hand hand) {
		FXUtil.transform(palm, hand.palmPosition());

		Iterator<Finger> itFinger = hand.fingers().iterator();

		Finger finger = null;
		for (int i = 0; i < fingers.length; i++) {
			finger = itFinger.next();

			FXUtil.transform(fingers[i], finger.tipPosition());
			FXUtil.transform(distal[i], finger.bone(Type.TYPE_DISTAL).prevJoint());
			FXUtil.transform(intermediate[i], finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			FXUtil.transform(proximal[i], finger.bone(Type.TYPE_PROXIMAL).prevJoint());
		}
		FXUtil.transform(metacarpal, finger.bone(Type.TYPE_METACARPAL)
				.prevJoint());

		for (JointFX3D joint : joints) {
			joint.update();
		}
	}

	private class JointFX3D {
		private Sphere fromSphere;
		private Sphere toSphere;
		private Cylinder bone;
		private Rotate joint;

		public JointFX3D(Sphere fromSphere, Sphere toSphere) {
			this.fromSphere = fromSphere;
			this.toSphere = toSphere;
			this.joint = new Rotate();
			this.bone = createBone(joint);
		}

		private Cylinder createBone(Rotate joint) {
			PhongMaterial material = new PhongMaterial();
			material.setSpecularColor(Color.BLUE);
			material.setDiffuseColor(Color.DARKBLUE);

			Cylinder cylinder = new Cylinder();
			cylinder.setRadius(2);
			cylinder.setMaterial(material);
			cylinder.getTransforms().add(joint);

			return cylinder;
		}

		public void update() {
			double dx = (float) (fromSphere.getTranslateX() - toSphere.getTranslateX());
			double dy = (float) (fromSphere.getTranslateY() - toSphere.getTranslateY());
			double dz = (float) (fromSphere.getTranslateZ() - toSphere.getTranslateZ());

			bone.setHeight(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)
					+ Math.pow(dz, 2)));
			bone.setTranslateX(fromSphere.getTranslateX());
			bone.setTranslateY(fromSphere.getTranslateY() - bone.getHeight() / 2);
			bone.setTranslateZ(fromSphere.getTranslateZ());

			joint.setPivotY(bone.getHeight() / 2);
			joint.setAxis(new Point3D(dz, 0, -dx));
			joint.setAngle(180 - new Point3D(dx, -dy, dz).angle(Rotate.Y_AXIS));
		}

		public Cylinder getBone() {
			return bone;
		}
	}
}