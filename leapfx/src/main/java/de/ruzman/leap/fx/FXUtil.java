package de.ruzman.leap.fx;

import javafx.scene.Node;

import com.leapmotion.leap.Vector;

public class FXUtil {
	public static void transform(Node node, Vector vector) {
		node.setTranslateX(vector.getX());
		node.setTranslateY(-vector.getY());
		node.setTranslateZ(-vector.getZ());
	}
}
