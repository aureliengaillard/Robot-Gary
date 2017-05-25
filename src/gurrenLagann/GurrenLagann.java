package gurrenLagann;

import interfaces.RobotInterface;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class GurrenLagann implements RobotInterface {

	private static final int SEUIL = 50;
	private static final int SCAN = 35;
	private static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);	
	private static DifferentialPilot pilot = new DifferentialPilot(56, 112, Motor.C, Motor.B);
	
	public GurrenLagann() {
		pilot.setRotateSpeed(150);		
	}
	
	private static void rotation(char dir, long time) {
		if(dir == 'l') {
			Motor.B.forward();
			Motor.C.backward();
		} else {
			Motor.C.forward();
			Motor.B.backward();
		}
		Delay.msDelay(time);
		pilot.stop();
	}
	
	// Autre fonction de rotation
	/*private static void rotation2(char dir, long time) {
		if(dir == 'l')
			pilot.rotateLeft();
		else
			pilot.rotateRight();
		Delay.msDelay(time);
		pilot.stop();
	}*/
	
	public void avanceUneCase() {
		char dir;
		int x;
		
		LightSensor right = new LightSensor(SensorPort.S1);
		LightSensor left = new LightSensor(SensorPort.S3);
		pilot.forward();
		
		while(left.getLightValue() < SEUIL && right.getLightValue() < SEUIL);
		long start_time = System.currentTimeMillis();
		
		if(left.getLightValue() > SEUIL) {
			while(right.getLightValue() < SEUIL);
			dir = 'l';
			x = 0;
		} else {
			while(left.getLightValue() < SEUIL);
			dir = 'r';
			x = 0;
		}
		
		Delay.msDelay(0);
		long end_time = System.currentTimeMillis();
		long difference = end_time - start_time;
		
		if(difference > 5)
			rotation(dir, difference + x);

		pilot.travel(260, false);
		pilot.stop();
	}
	
	public void tourneAGauche() {
		pilot.rotate(90);
	}
	
	public void tourneADroite() {
		pilot.rotate(-90);
	}
	
	public void tourneDemiTour() {
		pilot.rotate(180);
	}
	
	public boolean regardeToutDroit() {
		Delay.msDelay(100);
		return (us.getDistance() < SCAN);
	}
	
	public boolean regardeAGauche() {
		tourneAGauche();
		boolean r = regardeToutDroit();
		tourneADroite();
		return r;
	}
	
	public boolean regardeADroite() {
		tourneADroite();
		boolean r = regardeToutDroit();
		tourneAGauche();
		return r;
	}
	
}
