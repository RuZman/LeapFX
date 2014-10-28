package de.ruzman.hand;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

public class CylinderApp extends Application {	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Group group = new Group();
		demonstrateCylinder(group);
		
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-500);
		camera.setFarClip(1000);
		camera.setFieldOfView(40);
		
		Scene scene = new Scene(group, 500, 500, true);		
		scene.setCamera(camera);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void demonstrateCylinder(Group group) {
		Sphere sphere = new Sphere(10);
		Cylinder cylinder = new Cylinder(5, 200);
		
		addMaterial(sphere);
		addMaterial(cylinder);
		
		sphere.setTranslateX(30);
		sphere.setTranslateY(20);
		sphere.setTranslateZ(100);

		cylinder.setTranslateX(30);
		cylinder.setTranslateY(20);
		cylinder.setTranslateZ(100);
		
		group.getChildren().addAll(sphere, cylinder);
	}

	private void addMaterial(Shape3D shape3D) {
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.BLUE);
		material.setDiffuseColor(Color.DARKBLUE);
		shape3D.setMaterial(material);
	}
}
