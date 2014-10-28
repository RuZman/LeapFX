package de.ruzman.hand;

import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
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
		camera.setTranslateX(-700);
		camera.setFarClip(1000);
		camera.setFieldOfView(40);
		camera.setRotationAxis(Rotate.Y_AXIS);
		camera.setRotate(90);

		Scene scene = new Scene(group, 500, 500, true);
		scene.setCamera(camera);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void demonstrateCylinder(Group group) {
		Sphere fromSphere = createSphere(30,-10,70);
		Sphere toSphere = createSphere(80, -20,20);
		
		Cylinder cylinder = new Cylinder();
		cylinder.setRadius(5);
		addMaterial(cylinder);
		connect(cylinder, fromSphere, toSphere);

		group.getChildren().addAll(cylinder, fromSphere, toSphere);
	}

	private <T> void connect(Cylinder cylinder, Sphere fromSphere, Sphere toSphere) {
		// Cylinder position
		cylinder.translateXProperty().bind(fromSphere.translateXProperty());
		cylinder.translateYProperty().bind(
				fromSphere.translateYProperty().subtract(
						cylinder.heightProperty().divide(2)));
		cylinder.translateZProperty().bind(fromSphere.translateZProperty());

		// Cylinder height	
		DoubleBinding height = new DoubleBinding() {
		     {
		         super.bind(fromSphere.translateXProperty());
		         super.bind(fromSphere.translateYProperty());
		         super.bind(fromSphere.translateZProperty());
		         
		         super.bind(toSphere.translateXProperty());
		         super.bind(toSphere.translateYProperty());
		         super.bind(toSphere.translateZProperty());
		     }
			
			@Override
			protected double computeValue() {
				return Math.sqrt(Math.pow(toSphere.getTranslateX() - fromSphere.getTranslateX(), 2)
						+ Math.pow(toSphere.getTranslateY() - fromSphere.getTranslateY(), 2)
						+ Math.pow(toSphere.getTranslateZ() - fromSphere.getTranslateZ(), 2));
			}
		};
		cylinder.heightProperty().bind(height);
		
		// Cylinder angle
		Rotate rotate = new Rotate();
		
		double dx = fromSphere.getTranslateX() - toSphere.getTranslateX();
		double dy = fromSphere.getTranslateY() - toSphere.getTranslateY();
		double dz = fromSphere.getTranslateZ() - toSphere.getTranslateZ();

		rotate.setAxis(new Point3D(dz, 0, -dx));
		rotate.setAngle(180 - new Point3D(dx, dy, dz).angle(new Point3D(0, -1, 0)));
		rotate.pivotYProperty().bind(cylinder.heightProperty().divide(2));
		
		cylinder.getTransforms().add(rotate);
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
