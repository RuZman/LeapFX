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
		Sphere fromSphere = createSphere(30, 20, 100);
		Sphere toSphere = createSphere(-20, -10, -10);
		Cylinder cylinder = new Cylinder(5, 200);

		addMaterial(cylinder);
		connect(cylinder, fromSphere, toSphere);

		group.getChildren().addAll(cylinder, fromSphere, toSphere);
	}
	
	private void connect(Cylinder cylinder, Sphere fromSphere, Sphere toSphere) {
		cylinder.translateXProperty().bind(fromSphere.translateXProperty());
		cylinder.translateYProperty().bind(
				fromSphere.translateYProperty().subtract(
						cylinder.heightProperty().divide(2)));
		cylinder.translateZProperty().bind(fromSphere.translateZProperty());
	}
	
	private Sphere createSphere(double x, double y, double z) {
		Sphere sphere = new Sphere(5);
		addMaterial(sphere);
		
		sphere.setTranslateX(x);
		sphere.setTranslateY(y);
		sphere.setTranslateZ(z);
		
		return sphere;
	}

	private void addMaterial(Shape3D shape3D) {
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.BLUE);
		material.setDiffuseColor(Color.DARKBLUE);
		shape3D.setMaterial(material);
	}
}
