package blocCentral;

import java.io.IOException;

import lejos.nxt.LCD;
import lejos.util.Delay;

public class ThreadReceptionRobot extends Thread {
	// Bloc d'origine et robot écouté
	private BlocCentral bloc;
	private Robot robot;

	// Flag d'arret
	private volatile boolean continueThread = false;

	// Constructeur
	public ThreadReceptionRobot(BlocCentral origine, Robot robot) {
		this.bloc = origine;
		this.robot = robot;
	}
	
	// Envoi aux autres threads
	private void sendToOther(int code, int x, int y) {
		for (Robot r : bloc.getRobotsConnectes()) {
			if (r != this.robot) {
				r.send(code, x, y);
			}
		}
	}
	
	// Code Bluetooth
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
					case PERSONNE : {
						bloc.getOccupe().acquire();
						boolean found = false;
						Personne pp = null;
						for (Personne p : bloc.getPersonnesASauver()) {
							if (p.getX() == xRecu && p.getY() == yRecu) {
								robot.send(OK, 0, 0);
								pp = p;
								found = true;
								break;
							}
						}
						bloc.getPersonnesASauver().remove(pp);
						if (!found) {
							robot.send(NOK, 0, 0);
						}
						if (bloc.getPersonnesASauver().isEmpty()) {
							robot.send(FIN, 0, 0);
						}
						bloc.getOccupe().release();
						break;
					}
					default : {
						sendToOther(codeRecu, xRecu, yRecu);
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
