package de.ruzman.hand;

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

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Group group = new Group();
		Scene scene = new Scene(group, 500, 500);
		
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateY(10);
		camera.setRotationAxis(Rotate.X_AXIS);
		camera.setRotate(90);
		scene.setCamera(camera);
		
		group.getChildren().add(create3DBall());
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Node create3DBall() {
		Sphere sphere = new Sphere();
		
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.GREEN);
		material.setDiffuseColor(Color.DARKGREEN);
		sphere.setMaterial(material);
		
		return sphere;
	}
}
