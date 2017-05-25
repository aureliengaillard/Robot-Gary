package mains;

import interfaces.RobotInterface;
import programmes.Parcourer;
import robot2Tetes.Robot2Tetes;

public class MainRobot2Tetes {
	// Fonction Main executée par le Robot2Tetes
	public static void main(String[] args) {
		RobotInterface robot2Tetes = new Robot2Tetes();
		Parcourer robot2TetesParcourer = new Parcourer(robot2Tetes);
		robot2TetesParcourer.sauverLesGens();
	}
}
