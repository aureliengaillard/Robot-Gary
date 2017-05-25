package programmes;

import java.io.IOException;
import lejos.util.Delay;

public class ThreadReceptionRobot extends Thread {
	// Robot à qui appartient le thread
	private Parcourer robot;

	// Flag d'arret des traitements du thread
	private volatile boolean continueThread = false;

	// Constructeur
	public ThreadReceptionRobot(Parcourer robot) {
		this.robot = robot;
	}
	
	// Arret des traitements du thread
	public void arreter() {
		this.continueThread = false;
	}

	// Demarrage des traitements du thread
	public void demarrer() {
		this.continueThread = true;
	}
	
	// Codes Bluetooth
	private static final int MURNORD = 8;
	private static final int MURSUD = 9;
	private static final int MUROUEST = 10;
	private static final int MUREST = 11;
	private static final int POSITION = 12;
	private static final int FINPOSITION = 13;
	private static final int OK = 14;
	private static final int NOK = 15;
	private static final int FIN = 16;

	// Fonction de traitement executée en parallèle
	public void run() {
		// Données reçues initialisées à -1
		int codeRecu = -1;
		int xRecu = -1;
		int yRecu = -1;
		// Boucle de reception infinie
		while (true) {
			// Traitement uniquement si flag = true
			if (continueThread) {
				// Reception des données
				try {
					codeRecu = robot.getIn().readInt();
					xRecu = robot.getIn().readInt();
					yRecu = robot.getIn().readInt();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Traitement différent selon le code reçu
				switch (codeRecu) {
				// Si code Mur : Mise à jour de la grille et on dit aux robots de recalculer leurs chmins
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
					// Mise à jour de la liste des positions des autres robots
					case POSITION : {
						this.robot.getAutresRobots().add(this.robot.getGrille().getCase(xRecu, yRecu));
						break;
					}
					case FINPOSITION : {
						this.robot.getAutresRobots().remove(this.robot.getGrille().getCase(xRecu, yRecu));
						break;
					}
					// Réponses du bloc central lors du choix des personnes à sauver
					case OK : {
						this.robot.setOk(true);
						break;
					}
					case NOK : {
						this.robot.setNok(true);
						break;
					}
					// Fin des sauvetage, on indique au robot qu'il peut retourner dans un coin
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
}
