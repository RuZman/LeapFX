package de.ruzman.hand;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
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

	public static void main(String[] args) {
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Group group = new Group();		
		Scene scene = new Scene(group, 500, 500);
		
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateY(100);
		camera.setRotationAxis(Rotate.X_AXIS);
		camera.setRotate(90);
		scene.setCamera(camera);
		
		group.getChildren().add(create3DBall());
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addPointMotionListener(this);
	}

	private Node create3DBall() {
		Sphere sphere = new Sphere();
		
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.GREEN);
		material.setDiffuseColor(Color.DARKGREEN);
		sphere.setMaterial(material);
		
		return sphere;
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
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}
