package de.ruzman.path;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.stage.Stage;

public class PathApp extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		addHouse(root);
		addPacman(root);

		Scene scene = new Scene(root, 764, 221);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void addHouse(Group root) {
		Path path = new Path();

		path.getElements().add(new MoveTo(282, 210));
		path.getElements().add(new VLineTo(110));
		path.getElements().add(new HLineTo(382));
		path.getElements().add(new LineTo(332, 30));
		path.getElements().add(new LineTo(282, 110));
		path.getElements().add(new LineTo(382, 210));
		path.getElements().add(new HLineTo(282));
		path.getElements().add(new LineTo(382, 110));
		path.getElements().add(new VLineTo(210));

		root.getChildren().add(path);
	}

	private void addPacman(Group root) {
		Path path = new Path();

		path.getElements().add(new MoveTo(550, 105));
		path.getElements().add(new LineTo(470, 50));

		ArcTo arc = new ArcTo();
		arc.setRadiusX(100);
		arc.setRadiusY(100);
		arc.setX(470);
		arc.setY(180);
		arc.setLargeArcFlag(true);
		arc.setSweepFlag(true);

		path.getElements().add(arc);
		path.getElements().add(new ClosePath());

		path.setFill(Color.YELLOW);
		path.setStroke(null);

		root.getChildren().add(path);
	}
}
