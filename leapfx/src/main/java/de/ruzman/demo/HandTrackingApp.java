package de.ruzman.demo;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.LeapAppBuilder;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;
import de.ruzman.leap.fx.HandFX3D;
public class HandTrackingApp extends Application implements PointMotionListener {	
	private Group group;
	private Map<Integer, HandFX3D> hands;

	public static void main(String[] args) {
		new LeapAppBuilder().initLeapApp();
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		group = new Group();
		group.setDepthTest(DepthTest.ENABLE);
		
		hands = new HashMap<>();
		
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-500);
		camera.setTranslateY(-200);
		camera.setFarClip(1000);
		camera.setFieldOfView(40);
		
		Scene scene = new Scene(group, 500, 500);		
		scene.setCamera(camera);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addListener(this);
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
	public void enteredViewoport(PointEvent event) {
		HandFX3D hand = new HandFX3D();
		hands.put(event.getSource().id(), hand);
		group.getChildren().add(hand);
	}
	
	@Override
	public void moved(PointEvent event) {
		int handId = event.getSource().id();
		HandFX3D hand = hands.get(handId);
		hand.update(LeapApp.getController().frame().hand(handId));
	}

	@Override
	public void leftViewport(PointEvent event) {
		int handId = event.getSource().id();
		hands.remove(handId);
		group.getChildren().remove(hands.get(handId));
	}
}
