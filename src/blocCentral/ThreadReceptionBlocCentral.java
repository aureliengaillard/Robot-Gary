package blocCentral;

import java.io.IOException;
import lejos.util.Delay;

public class ThreadReceptionBlocCentral extends Thread {
	// Bloc d'origine et robot �cout�
	private BlocCentral bloc;
	private Robot robot;

	// Flag d'arret
	private volatile boolean continueThread = false;

	// Constructeur
	public ThreadReceptionBlocCentral(BlocCentral origine, Robot robot) {
		this.bloc = origine;
		this.robot = robot;
	}
	
	// Arret du thread
	public void arreter() {
		this.continueThread = false;
	}

	// Demarrage du thread
	public void demarrer() {
		this.continueThread = true;
	}
	
	// Envoi aux autres threads
	private void sendToOther(int code, int x, int y) {
		for (Robot r : bloc.getRobotsConnectes()) {
			if (r != this.robot) {
				r.send(code, x, y);
			}
		}
	}
	
	// Codes Bluetooth
	private static final int PERSONNE = 3;
	private static final int OK = 14;
	private static final int NOK = 15;
	private static final int FIN = 16;

	public void run() {
		// Donn�es re�ues initialis�es � -1
		int codeRecu = -1;
		int xRecu = -1;
		int yRecu = -1;
		// Boucle infinie de reception
		while (true) {
			// Thread arr�t� ou d�marr� ?
			if (continueThread) {
				// Reception de donn�es
				try {
					codeRecu = robot.getIn().readInt();
					xRecu = robot.getIn().readInt();
					yRecu = robot.getIn().readInt();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Traitement diff�renc, selon ce que l'on re�oit
				switch (codeRecu) {
					// Si c'est une personne, on v�rifie qu'elle est "sauvable", et on r�pond au robot
					// Si sauvable, on la supprime de la liste des personnes
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
					// Dans tous les autres cas, on relai l'information aux autres robots
					default : {
						sendToOther(codeRecu, xRecu, yRecu);
						break;
					}
				}
			}
			Delay.msDelay(200);
		}
	}
}
