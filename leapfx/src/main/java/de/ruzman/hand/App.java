package de.ruzman.hand;

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
import de.ruzman.leap.LeapApp.Mode;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;
import de.ruzman.leap.fx.HandFX3D;

public class App extends Application implements PointMotionListener {	
	private Group group;
	private Map<Integer, HandFX3D> hands;

	public static void main(String[] args) {
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		
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
		HandFX3D hand = hands.get(handId);
		
		if(event.leftViewPort()) {
			hands.remove(handId);
			group.getChildren().remove(hand);
		} else if(hand == null) {
			hand = new HandFX3D(handId);
			hands.put(handId, hand);
			group.getChildren().add(hand);
		}
		
		if(hand != null) {
			hand.update(LeapApp.getController().frame().hand(handId));
		}
	}
	
	@Override
	public void pointDragged(PointEvent event) {
	}
}
