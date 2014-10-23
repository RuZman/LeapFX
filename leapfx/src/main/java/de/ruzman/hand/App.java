package de.ruzman.hand;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class App extends Application implements PointMotionListener {	
	private Group group;
	private Map<Integer, Sphere> hands;
	
	public static void main(String[] args) {
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		group = new Group();
		hands = new HashMap<>();
		
		Scene scene = new Scene(group, 500, 500);
		
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateY(100);
		camera.setRotationAxis(Rotate.X_AXIS);
		camera.setRotate(90);
		scene.setCamera(camera);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addPointMotionListener(this);
	}

	private void synchronizeWithLeapMotion() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1.0 / 60.0), ea -> LeapApp
						.update()));
		timeline.play();
	}
	
	@Override
	public void pointMoved(PointEvent event) {
		int handId = event.getSource().id();
		Sphere hand = hands.get(handId);
		
		if(event.leftViewPort()) {
			hands.remove(handId);
			group.getChildren().remove(hand);
		} else if(hand == null) {
			hand = createSphere();
			hands.put(handId, hand);
			group.getChildren().add(hand);
		}
		
		if(hand != null) {
			hand.setTranslateX(event.getAbsoluteX());
			hand.setTranslateZ(event.getAbsoluteZ());
		}
	}
	
	private Sphere createSphere() {
		Sphere sphere = new Sphere(5);
		
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.RED);
		material.setDiffuseColor(Color.DARKRED);
		sphere.setMaterial(material);
		
		return sphere;
	}		

	@Override
	public void pointDragged(PointEvent event) {
	}
}
