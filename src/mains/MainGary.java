package mains;

import interfaces.RobotInterface;
import programmes.Parcourer;
import robotGary.RobotGary;

public class MainGary {
	// Fonction Main executée par le RobotGary
	public static void main(String[] args) {
		RobotInterface gary = new RobotGary();
		Parcourer garyParcourer = new Parcourer(gary);
		garyParcourer.sauverLesGens();
	}
}
