package mains;

import gurrenLagann.GurrenLagann;
import interfaces.RobotInterface;
import programmes.Parcourer;

public class MainGurrenLagann {
	// Fonction Main executée par le GurrenLagann
	public static void main(String[] args) {
		RobotInterface gurren = new GurrenLagann();
		Parcourer gurrenParcourer = new Parcourer(gurren);
		gurrenParcourer.sauverLesGens();
	}
}
