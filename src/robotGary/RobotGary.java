package robotGary;

import interfaces.RobotInterface;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class RobotGary implements RobotInterface {
	// Variable debug
	private boolean debug = false;

	// Moteurs
	private NXTRegulatedMotor motorG = Motor.C;
	private NXTRegulatedMotor motorD = Motor.B;
	private NXTRegulatedMotor axeTete = Motor.A;

	// Capteurs lumineux
	private LightSensor lightG = new LightSensor(SensorPort.S4);
	private LightSensor lightD = new LightSensor(SensorPort.S1);

	// Capteur distance
	private UltrasonicSensor ultrasonic = new UltrasonicSensor(SensorPort.S3);

	// Pilote
	private DifferentialPilot pilote = new DifferentialPilot(56, 105, motorG, motorD);

	// Valeur de la vitesse des moteurs par défaut choisie
	private static final int vitesseParDefautRotation = 280;
	private static final int vitesseParDefautTete = 700;
	private static final int vitesseParDefaut = 340;

	// Valeur du seuil de detection des objets
	private static final int seuilDetection = 16;
	private static final int seuilDetectionCotes = 30;
	
	// Valeur seuil pour différencier le noir et le blanc
	private static final int valeurSeuilGauche = 487;
	private static final int valeurSeuilDroit = 487;

	public RobotGary() {
		motorG.setSpeed(vitesseParDefaut);
		motorD.setSpeed(vitesseParDefaut);
		axeTete.setSpeed(vitesseParDefautTete);
		this.pilote.setRotateSpeed(vitesseParDefautRotation);
		this.pilote.setRotateSpeed(vitesseParDefautRotation);
		this.pilote.setTravelSpeed(vitesseParDefaut);
	}

	@Override
	public void avanceUneCase() {
		NXTRegulatedMotor second = null;
		long start = 0;
		long stop = 0;
		pilote.forward();
		while (lightG.getNormalizedLightValue() < valeurSeuilGauche && lightD.getNormalizedLightValue() < valeurSeuilDroit);
		start = System.currentTimeMillis();
		if (lightG.getNormalizedLightValue() > valeurSeuilGauche) {
			while (lightD.getNormalizedLightValue() < valeurSeuilDroit);
			stop = System.currentTimeMillis();
			second = motorD;
		} else {
			while (lightG.getNormalizedLightValue() < valeurSeuilGauche);
			stop = System.currentTimeMillis();
			second = motorG;
		}
		long delai = stop - start;
		pilote.stop();
		if (delai > 15) {
			second.rotate((int) (delai / 1.3));
		}
		pilote.travel(240);
	}
	
	@Override
	public void tourneAGauche() {
		if (debug)
			LCD.drawString("Pivot Gauche", 1, 2);
		pilote.rotate(90);
		if (debug)
			LCD.clear(2);
	}

	@Override
	public void tourneADroite() {
		if (debug)
			LCD.drawString("Pivot Droite", 1, 2);
		pilote.rotate(-90);
		if (debug)
			LCD.clear(2);
	}

	@Override
	public void tourneDemiTour() {
		if (debug)
			LCD.drawString("Demi Tour", 1, 2);
		pilote.rotate(180);
		if (debug)
			LCD.clear(2);
	}

	@Override
	public boolean regardeToutDroit() {
		if (debug)
			LCD.drawString("Regarde en face", 1, 2);
		int ret = this.ultrasonic.getDistance();
		Delay.msDelay(50);
		if (debug)
			LCD.clear(2);
		return (ret < seuilDetection);
	}

	@Override
	public boolean regardeAGauche() {
		if (debug)
			LCD.drawString("Regarde a gauche", 1, 2);
		this.axeTete.rotate(340);
		int ret = this.ultrasonic.getDistance();
		Delay.msDelay(50);
		this.axeTete.rotate(-340);
		if (debug)
			LCD.clear(2);
		return (ret < seuilDetectionCotes);
	}

	@Override
	public boolean regardeADroite() {
		if (debug)
			LCD.drawString("Regarde a droite", 1, 2);
		this.axeTete.rotate(-340);
		int ret = this.ultrasonic.getDistance();
		Delay.msDelay(50);
		this.axeTete.rotate(340);
		if (debug)
			LCD.clear(2);
		return (ret < seuilDetectionCotes);
	}
}
