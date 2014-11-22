package de.ruzman.path;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import de.ruzman.fx.BezierePath;
import de.ruzman.leap.LeapApp;
import de.ruzman.leap.LeapApp.LeapAppBuilder;
import de.ruzman.leap.TrackingBox;
import de.ruzman.leap.event.PointEvent;
import de.ruzman.leap.event.PointMotionListener;

public class PathApp extends Application implements PointMotionListener {
	private Group root;

	private Map<Integer, BezierePath> paths = new HashMap<>();
	private TrackingBox trackingBox;
	private Vector position = new Vector();

	public static void main(String[] args) {
		new LeapAppBuilder()
			.displayWidth(764)
			.displayHeight(221)
			.maximumHandNumber(1)
			.createLeapApp();
		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new Group();

		Scene scene = new Scene(root, LeapApp.getDisplayWidth(), 
				LeapApp.getDisplayHeight());

		primaryStage.setScene(scene);
		primaryStage.show();

		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addPointMotionListener(this);

		trackingBox = new TrackingBox();
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
		BezierePath path = paths.get(handId);

		Hand hand = LeapApp.getController().frame().hand(handId);
		Finger finger = hand.fingers().get(1);
		trackingBox.calcScreenPosition(finger.tipPosition(), position);

		if (path == null) {
			path = new BezierePath();
			paths.put(handId, path);
			root.getChildren().add(path);
		} else if (event.leftViewPort()) {
			paths.remove(handId);
			root.getChildren().remove(path);
		}

		path.add(position.getX(), position.getY());
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}
