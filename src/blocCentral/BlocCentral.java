package blocCentral;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import programmes.Semaphore;
import javax.bluetooth.RemoteDevice;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.util.Delay;

public class BlocCentral {
	// Liste des robots connect�s
	private List<Robot> robotsConnectes = new LinkedList<Robot>();

	// Liste des personnes a sauver + semaphore
	private List<Personne> personnesASauver = new LinkedList<Personne>();
	private Semaphore occupe = new Semaphore(1);

	// Taille du labyrinthe
	private int x;
	private int y;
	
	// Liste des threads d'�coute
	private List<ThreadReceptionBlocCentral> threads = new LinkedList<ThreadReceptionBlocCentral>();

	// Constructeur
	public BlocCentral() {
		this.x = ask("Taille laby : x", 0, 15);
		this.y = ask("Taille laby : y", 0, 15);
	}
	
	// Getters
	public List<ThreadReceptionBlocCentral> getThreads() {
		return this.threads;
	}
	
	public List<Robot> getRobotsConnectes() {
		return this.robotsConnectes;
	}
	
	public List<Personne> getPersonnesASauver() {
		return this.personnesASauver;
	}
	
	public Semaphore getOccupe() {
		return this.occupe;
	}

	// Affichage pour demander les infos de base du labyrinthe
	private int ask(String message, int valMin, int valMax) {
		int valeur = valMin;
		LCD.clear(5);
		LCD.clear(6);
		LCD.drawString(message, 1, 5);
		LCD.drawString(("< " + valeur + " >"), 1, 6);
		int button;
		while (true) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT) {
				if (valeur == valMin) {
					valeur = valMax;
				} else {
					valeur--;
				}
				LCD.clear(6);
				LCD.drawString("< " + valeur + " >", 1, 6);
			} else if (button == Button.ID_RIGHT) {
				if (valeur == valMax) {
					valeur = valMin;
				} else {
					valeur++;
				}
				LCD.clear(6);
				LCD.drawString("< " + valeur + " >", 1, 6);
			} else if (button == Button.ID_ENTER) {
				LCD.clear(5);
				LCD.clear(6);
				return valeur;
			}
		}
	}
	
	// Valeurs des directions
	public static final int NORD = 0;
	public static final int EST = 1;
	public static final int SUD = 2;
	public static final int OUEST = 3;
	
	// Affichage pour demander la direction
	private int askDirection() {
		int valeur = NORD;
		LCD.clear(5);
		LCD.clear(6);
		LCD.drawString("Dir du robot", 1, 5);
		LCD.drawString("< " + "NORD" + " >"	, 1, 6);
			int button;
		while (true) {
			button = Button.waitForAnyPress();
			if (button == Button.ID_LEFT) {
				switch (valeur) {
					case NORD :
						valeur = OUEST;
						LCD.clear(6);
						LCD.drawString("< " + "OUEST" + " >", 1, 6);
						break;
					case EST :
						valeur = NORD;
						LCD.clear(6);
						LCD.drawString("< " + "NORD" + " >", 1, 6);
						break;
					case SUD :
						valeur = EST;
						LCD.clear(6);
						LCD.drawString("< " + "EST" + " >", 1, 6);
						break;
					case OUEST :
						valeur = SUD;
						LCD.clear(6);
						LCD.drawString("< " + "SUD" + " >", 1, 6);
						break;
					default :
						break;
				}
			} else if (button == Button.ID_RIGHT) {
				switch (valeur) {
					case NORD :
						valeur = EST;
						LCD.clear(6);
						LCD.drawString("< " + "EST" + " >", 1, 6);
						break;
					case EST :
						valeur = SUD;
						LCD.clear(6);
						LCD.drawString("< " + "SUD" + " >", 1, 6);
						break;
					case SUD :
						valeur = OUEST;
						LCD.clear(6);
						LCD.drawString("< " + "OUEST" + " >", 1, 6);
						break;
					case OUEST :
						valeur = NORD;
						LCD.clear(6);
						LCD.drawString("< " + "NORD" + " >", 1, 6);
						break;
					default :
						break;
				}
			} else if (button == Button.ID_ENTER) {
				LCD.clear(5);
				LCD.clear(6);
				return valeur;
			}
		}
	}
	
	private void echanger(List<Personne> liste, int a, int b) {
		Personne ap = liste.get(a);
		Personne bp = liste.get(b);
		liste.add(a, bp);
		liste.remove(a + 1);
		liste.add(b, ap);
		liste.remove(b + 1);
		
	}
	
	private void triBulle(List<Personne> liste) {
		int longueur = liste.size();
		boolean inversion;

		do {
			inversion = false;
			for (int i = 0; i < longueur - 1; i++) {
				if (liste.get(i).superiorTo(liste.get(i + 1))) {
					echanger(liste, i, i + 1);
					inversion = true;
				}
			}
			longueur--;
		} while (inversion);
	}
	
	// Codes des messages bluetooth
	private static final int POSITIONROBOT = 0;
	private static final int ORIENTATIONROBOT = 1;
	private static final int TAILLELABY = 2;
	private static final int PERSONNE = 3;
	private static final int FINPERSONNE = 4;
	private static final int VALEURPERSONNECHOISIE = 6;
	private static final int DEPART = 7;

	public void run() {
		// Determination des personnes par l'utilisateur
		int nbPersonnes = ask("Nb personnes = ", 1, 99);
		for (int i = 0; i < nbPersonnes; i++) {
			personnesASauver
					.add(new Personne(ask("personne " + i + " : x", 0, this.x), ask("personne " + i + " : y", 0, this.y)));
		}
		
		// Connexion � tous les robots pair�s avec le bloc central
		// Param�tres determin�s, et envoy�s aux robots
		for (RemoteDevice rd : Bluetooth.getKnownDevicesList()) {
			String nom = rd.getFriendlyName(false);
			LCD.drawString("Allumer " + nom, 0, 0);
			LCD.drawString("Press Button", 0, 1);
			Button.waitForAnyPress();
			LCD.clear(1);
			LCD.drawString("Connexion...", 0, 1);
			BTConnection cx = Bluetooth.connect(rd);
			DataInputStream in = cx.openDataInputStream();
			DataOutputStream out = cx.openDataOutputStream();
			Robot r = new Robot(in, out, cx);
			robotsConnectes.add(r);
			LCD.clear();
			LCD.drawString(nom + " connecte !!", 0, 0);
			Delay.msDelay(1500);
			LCD.clear();
			// Envoi des coordonnees de depart
			r.send(POSITIONROBOT, ask("x Depart " + nom, 0, this.x), ask("y Depart " + nom, 0, this.y));
			// Envoi de l'orientation de depart
			r.send(ORIENTATIONROBOT, askDirection(), 0);
			// Envoi de la taille du labyrinthe
			r.send(TAILLELABY, this.x, this.y);
			// Envoi des personnes a sauver
			for (Personne p : personnesASauver) {
				r.send(PERSONNE, p.getX(), p.getY());
			}
			// Fin d'envoi des personnes
			r.send(FINPERSONNE, 0, 0);
		}
		
		// Envoi du signal de d�part
		LCD.drawString(robotsConnectes.size() + " robots connectes", 0, 0);
		LCD.drawString("Press Button to start", 0, 1);
		Button.waitForAnyPress();
		LCD.clear(1);
		for (Robot r : robotsConnectes) {
			r.send(DEPART, 0, 0);
		}
		
		// Valeurs recues initialis�es � -1
		int codeRecu = -1;
		int xRecu = -1;
		int yRecu = -1;
		
		// Reception des valeurs des robots pour les personnes
		// Chaque personne de la liste re�oit une valeur (distance mini d'un robot)
		// et un robot (robot le plus proche
		for (Robot r : robotsConnectes) {
			do {
				// Reception de la personne
				try {
					codeRecu = r.getIn().readInt();
					xRecu = r.getIn().readInt();
					yRecu = r.getIn().readInt();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (codeRecu == PERSONNE) {
					// Recherche de la personne dans la liste
					for (Personne p : personnesASauver) {
						if (p.getX() == xRecu && p.getY() == yRecu) {
							// Reception de sa valeur
							try {
								codeRecu = r.getIn().readInt();
								xRecu = r.getIn().readInt();
								yRecu = r.getIn().readInt();
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (codeRecu == VALEURPERSONNECHOISIE) {
								// Si valeur meilleure, affectation de la valeur et du robot
								if ((xRecu < p.getValeur()) || (p.getValeur() == -1)) {
									p.setValeur(new Integer(xRecu));
									p.setRobot(r);
									break;
								}
							}
						}
					}
				}
			} while (codeRecu != FINPERSONNE);
		}

		// Envoi du meilleur choix de chaque robot
		this.triBulle(this.personnesASauver);
		for (Robot r : robotsConnectes) {
			Personne pp = null;
			for (Personne p : personnesASauver) {
				if (p.getRobot() == r) {
					pp = p;
					r.send(PERSONNE, p.getX(), p.getY());
					break;
				}
			}
			personnesASauver.remove(pp);
		}
		
		// Thread de reception/envoi pour chaque robot
		for (Robot r : robotsConnectes) {
			threads.add(new ThreadReceptionBlocCentral(this, r));
		}
		// Demarrage des threads une fois que tous les autres threads sont cr��s
		// pour �viter d'envoyer des donn�es a un thread non encore cr��
		for (ThreadReceptionBlocCentral t  : threads) {
			t.setDaemon(true);
			t.start();
			t.demarrer();
		}
		
		// Attente, quand tout est fini, on peut appuyer sur le bouton
		LCD.clear();
		LCD.drawString("Appuyer pour deconnecter", 0, 0);
		Button.waitForAnyPress();
		
		// Arret des threads
		for (ThreadReceptionBlocCentral t : threads) {
			t.arreter();
		}
		
		// Deconnexion des robots
		for (Robot r : robotsConnectes) {
			r.disconnect();
		}
	}
}
