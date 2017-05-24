package programmes;

import java.io.IOException;

import lejos.nxt.LCD;
import lejos.util.Delay;

public class ThreadReception extends Thread {
	// Robot d'origine
	private Parcourer robot;

	// Flag d'arret
	private volatile boolean continueThread = false;

	// Constructeur
	public ThreadReception(Parcourer robot) {
		this.robot = robot;
	}
	
	// Codes Bluetooth
	private static final int PERSONNE = 3;
	private static final int MURNORD = 8;
	private static final int MURSUD = 9;
	private static final int MUROUEST = 10;
	private static final int MUREST = 11;
	private static final int POSITION = 12;
	private static final int FINPOSITION = 13;
	private static final int OK = 14;
	private static final int NOK = 15;
	private static final int FIN = 16;

	public void run() {
		int codeRecu = -1;
		int xRecu = -1;
		int yRecu = -1;
		while (true) {
			if (continueThread) {
				try {
					codeRecu = robot.getIn().readInt();
					xRecu = robot.getIn().readInt();
					yRecu = robot.getIn().readInt();
				} catch (IOException e) {
					e.printStackTrace();
				}
				switch (codeRecu) {
					case MURNORD : {
						this.robot.getSemGrille().acquire();
						this.robot.murNord(xRecu, yRecu);
						this.robot.getGrille().getCase(xRecu, yRecu).setCartographie(true);
						this.robot.getSemGrille().release();
						this.robot.setRecalcul(true);
						break;
					}
					case MURSUD : {
						this.robot.getSemGrille().acquire();
						this.robot.murSud(xRecu, yRecu);
						this.robot.getGrille().getCase(xRecu, yRecu).setCartographie(true);
						this.robot.getSemGrille().release();
						this.robot.setRecalcul(true);
						break;
					}
					case MUREST : {
						this.robot.getSemGrille().acquire();
						this.robot.murEst(xRecu, yRecu);
						this.robot.getGrille().getCase(xRecu, yRecu).setCartographie(true);
						this.robot.getSemGrille().release();
						this.robot.setRecalcul(true);
						break;
					}
					case MUROUEST : {
						this.robot.getSemGrille().acquire();
						this.robot.murOuest(xRecu, yRecu);
						this.robot.getGrille().getCase(xRecu, yRecu).setCartographie(true);
						this.robot.getSemGrille().release();
						this.robot.setRecalcul(true);
						break;
					}
					case POSITION : {
						this.robot.getAutresRobots().add(this.robot.getGrille().getCase(xRecu, yRecu));
						break;
					}
					case FINPOSITION : {
						this.robot.getAutresRobots().remove(this.robot.getGrille().getCase(xRecu, yRecu));
						break;
					}
					case OK : {
						this.robot.setOk(true);
						break;
					}
					case NOK : {
						this.robot.setNok(true);
						break;
					}
					case FIN : {
						this.robot.setContinuer(false);
						break;
					}
					default : {
						break;
					}
				}
			}
			Delay.msDelay(200);
		}
	}

	public void arreter() {
		this.continueThread = false;
	}

	public void demarrer() {
		this.continueThread = true;
	}
}
