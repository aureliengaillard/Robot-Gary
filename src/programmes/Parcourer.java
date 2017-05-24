package programmes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import programmes.Semaphore;

import blocCentral.Personne;
import interfaces.RobotInterface;
import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;

public class Parcourer {
	// Variable debug
	public boolean debug = false;
	
	// Robot "générique"
	private RobotInterface robot;
	
	// Grille + Semaphore
	private Grille grille;
	private Semaphore semGrille = new Semaphore(1);
	
	// Orientation du robot
	public static final int NORD = 0;
	public static final int EST = 1;
	public static final int SUD = 2;
	public static final int OUEST = 3;
	
	private int orientation;
	
	// Coordonnées
	private int x;
	private int y;
	
	// Coordonnees but
	private int xBut;
	private int yBut;
	
	// Liste des positions des autres robots
	private List<Case> autresRobots = new LinkedList<Case>();
	
	// Constructeur
	public Parcourer(RobotInterface robot) {
		this.robot = robot;
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
	
	// Fonction de mise a jour des coordonnees
	private void miseAJourCoordonnees() {
		switch (orientation) {
		case NORD :
			this.y--;
			break;
		case EST :
			this.x++;;
			break;
		case SUD :
			this.y++;
			break;
		case OUEST :
			this.x--;
			break;
		default :
			break;
		}
	}
	
	// Avance d'une case	
	private void avanceUneCase() {
		robot.avanceUneCase();
		miseAJourCoordonnees();
	}
	
	// Pivoter à gauche a 90 degrés
	private void tourneAGauche() {
		robot.tourneAGauche();
		if (this.orientation == NORD) {
			this.orientation = OUEST;
		} else {
			this.orientation--;
		}
	}
	
	// Pivoter a droite a 90 degrés
	private void tourneADroite() {
		robot.tourneADroite();
		this.orientation = (this.orientation + 1) % 4;
	}
	
	// Demi tour
	private void tourneDemiTour() {
		robot.tourneDemiTour();
		this.orientation = (this.orientation + 2) % 4;
	}
	
	// Orienter le robot
	private void orienter(int direction) {
		switch (this.orientation) {
			case NORD :
				switch (direction) {
					case NORD :
						break;
					case EST :
						this.tourneADroite();
						break;
					case OUEST :
						this.tourneAGauche();
						break;
					case SUD :
						this.tourneDemiTour();
						break;
					default :
						break;
				}
				break;
			case EST :
				switch (direction) {
					case NORD :
						tourneAGauche();
						break;
					case EST :
						break;
					case OUEST :
						this.tourneDemiTour();
						break;
					case SUD :
						this.tourneADroite();
						break;
					default :
						break;
				}
				break;
			case OUEST :
				switch (direction) {
					case NORD :
						this.tourneADroite();
						break;
					case EST :
						this.tourneDemiTour();
						break;
					case OUEST :
						break;
					case SUD :
						this.tourneAGauche();
						break;
					default :
						break;
				}
				break;
			case SUD :
				switch (direction) {
					case NORD :
						this.tourneDemiTour();
						break;
					case EST :
						this.tourneAGauche();
						break;
					case OUEST :
						this.tourneADroite();
						break;
					case SUD :
						break;
					default :
						break;
				}
				break;
			default :
				break;
		}
	}

	// Detection en face
	private boolean regardeToutDroit() {
		return robot.regardeToutDroit();
	}
	
	// Detection a gauche
	private boolean regardeAGauche() {
		return robot.regardeAGauche();
	}
	
	// Detection a droite	
	private boolean regardeADroite() {
		return robot.regardeADroite();
	}
	
	// Jouer un son a partir d'un tableau d'entiers
	private void playSound(int[] instr, int[] notes, int len) {
		for (int i = 0; i < notes.length; i++) {
			Sound.playNote(instr, notes[i], len);
		}
	}
	
	// IA de base
	public void parcoursBasique() {
		if (debug)
			LCD.drawString("ParcoursBasique", 0, 0);
		this.orientation = askDirection();
		this.x = ask("Depart : x =", 0, 15);
		this.y = ask("Depart : y =", 0, 15);
		this.xBut = ask("But : x = ", 0, 15);
		this.yBut = ask("But : y =", 0, 15);
		grille = new Grille(ask("Taille laby : x =", 0, 15), ask("Taille laby : y =", 0, 15));
		LCD.drawString("Press button", 0, 1);
		Button.waitForAnyPress();
		LCD.clear(1);
		while (true) {
			if ((this.x == this.xBut) && (this.y == this.yBut)) {
				int notes[] = {500, 600, 700, 800, 900, 1500};
				this.playSound(Sound.XYLOPHONE, notes, 100);
				break;
			}
			while (!regardeToutDroit()) {
				avanceUneCase();
			}
			if (!this.regardeAGauche()) {
				this.tourneAGauche();
			} else if (!this.regardeADroite()) {
				this.tourneADroite();
			} else {
				this.tourneDemiTour();
			}
			Delay.msDelay(10);
		}
	}
	
	// IA - Toujours un mur a droite
	public void parcoursMurADroite() {
		if (debug)
			LCD.drawString("ParcoursBasique", 0, 0);
		this.orientation = askDirection();
		this.x = ask("Depart : x =", 0, 15);
		this.y = ask("Depart : y =", 0, 15);
		this.xBut = ask("But : x = ", 0, 15);
		this.yBut = ask("But : y =", 0, 15);
		grille = new Grille(ask("Taille laby : x =", 0, 15), ask("Taille laby : y =", 0, 15));
		LCD.drawString("Press button", 0, 1);
		Button.waitForAnyPress();
		LCD.clear(1);
		while (true) {
			if ((this.x == this.xBut) && (this.y == this.yBut)) {
				int notes[] = {500, 600, 700, 800, 900, 1500};
				this.playSound(Sound.XYLOPHONE, notes, 100);
				break;
			}
			if (this.regardeADroite()) {
				if (this.regardeToutDroit()) {
					if (this.regardeAGauche()) {
						this.tourneDemiTour();
						this.avanceUneCase();
					} else {
						this.tourneAGauche();
						this.avanceUneCase();
					}
				} else {
					this.avanceUneCase();
				}
			} else {
				this.tourneADroite();
				this.avanceUneCase();
			}
		}
	}
	
	// Parcours de pledge
	public void parcoursPledge() {
		if (debug)
			LCD.drawString("ParcoursPledge", 0, 0);
		this.orientation = askDirection();
		this.x = ask("Depart : x =", 0, 15);
		this.y = ask("Depart : y =", 0, 15);
		this.xBut = ask("But : x = ", 0, 15);
		this.yBut = ask("But : y =", 0, 15);
		grille = new Grille(ask("Taille laby : x =", 0, 15), ask("Taille laby : y =", 0, 15));
		LCD.drawString("Press button", 0, 1);
		Button.waitForAnyPress();
		LCD.clear(1);
		int valeur = 0;
		while (true) {
			if ((this.x == this.xBut) && (this.y == this.yBut)) {
				int notes[] = {500, 600, 700, 800, 900, 1500};
				this.playSound(Sound.XYLOPHONE, notes, 100);
				break;
			}
			if (valeur == 0) {
				while (!regardeToutDroit()) {
					avanceUneCase();
				}
				tourneADroite();
				valeur--;
			} else {
				if (!regardeToutDroit()) {
					avanceUneCase();
				}
				if (!regardeAGauche()) {
					tourneAGauche();
					valeur++;
				} else {
					if (regardeToutDroit()) {
						tourneADroite();
						valeur--;
					}
				}
			}
			Delay.msDelay(10);
		}
	}

	// Mise a jour des murs pendant le parcours
	private void murNord() {
		murNord(this.x, this.y);
	}
	
	public void murNord(int xCase, int yCase) {
		if (this.grille.getCase(xCase, yCase).getNord() != null) {
			this.grille.getCase(xCase, yCase).getNord().setSud(null);
			this.grille.getCase(xCase, yCase).setNord(null);
		}
	}
	
	private void murEst() {
		murEst(this.x, this.y);
	}
	
	public void murEst(int xCase, int yCase) {
		if (this.grille.getCase(xCase, yCase).getEst() != null) {
			this.grille.getCase(xCase, yCase).getEst().setOuest(null);
			this.grille.getCase(xCase, yCase).setEst(null);
		}
	}
	
	private void murOuest() {
		murOuest(this.x, this.y);
	}
	
	public void murOuest(int xCase, int yCase) {
		if (this.grille.getCase(xCase, yCase).getOuest() != null) {
			this.grille.getCase(xCase, yCase).getOuest().setEst(null);
			this.grille.getCase(xCase, yCase).setOuest(null);
		}
	}
	
	private void murSud() {
		murSud(this.x, this.y);
	}
	
	public void murSud(int xCase, int yCase) {
		if (this.grille.getCase(xCase, yCase).getSud() != null) {
			this.grille.getCase(xCase, yCase).getSud().setNord(null);
			this.grille.getCase(xCase, yCase).setSud(null);
		}
	}

	// Cartographie autour d'une case
	private void cartographierAutour() {
		switch (orientation) {
			case NORD :
				if (regardeToutDroit()) {
					murNord();
				}
				if (regardeADroite()) {
					murEst();
				}
				if (regardeAGauche()) {
					murOuest();
				}
				break;
			case OUEST :
				if (regardeToutDroit()) {
					murOuest();
				}
				if (regardeADroite()) {
					murNord();
				}
				if (regardeAGauche()) {
					murSud();
				}
				break;
			case EST :
				if (regardeToutDroit()) {
					murEst();
				}
				if (regardeADroite()) {
					murSud();
				}
				if (regardeAGauche()) {
					murNord();
				}
				break;
			case SUD :
				if (regardeToutDroit()) {
					murSud();
				}
				if (regardeADroite()) {
					murOuest();
				}
				if (regardeAGauche()) {
					murEst();
				}
				break;
			default :
				break;
		}
	}
	
	// Pour sauver les gens
	private void cartographierAutourConnecte() {
		switch (orientation) {
			case NORD :
				if (regardeToutDroit()) {
					this.semGrille.acquire();
					murNord();
					this.semGrille.release();
					this.send(MURNORD, this.x, this.y);
				}
				if (regardeADroite()) {
					this.semGrille.acquire();
					murEst();
					this.semGrille.release();
					this.send(MUREST, this.x, this.y);
				}
				if (regardeAGauche()) {
					this.semGrille.acquire();
					murOuest();
					this.semGrille.release();
					this.send(MUROUEST, this.x, this.y);
				}
				break;
			case OUEST :
				if (regardeToutDroit()) {
					this.semGrille.acquire();
					murOuest();
					this.semGrille.release();
					this.send(MUROUEST, this.x, this.y);
				}
				if (regardeADroite()) {
					this.semGrille.acquire();
					murNord();
					this.semGrille.release();
					this.send(MURNORD, this.x, this.y);
				}
				if (regardeAGauche()) {
					this.semGrille.acquire();
					murSud();
					this.semGrille.release();
					this.send(MURSUD, this.x, this.y);
				}
				break;
			case EST :
				if (regardeToutDroit()) {
					this.semGrille.acquire();
					murEst();
					this.semGrille.release();
					this.send(MUREST, this.x, this.y);
					
				}
				if (regardeADroite()) {
					this.semGrille.acquire();
					murSud();
					this.semGrille.release();
					this.send(MURSUD, this.x, this.y);
				}
				if (regardeAGauche()) {
					this.semGrille.acquire();
					murNord();
					this.semGrille.release();
					this.send(MURNORD, this.x, this.y);
				}
				break;
			case SUD :
				if (regardeToutDroit()) {
					this.semGrille.acquire();
					murSud();
					this.semGrille.release();
					this.send(MURSUD, this.x, this.y);
				}
				if (regardeADroite()) {
					this.semGrille.acquire();
					murOuest();
					this.semGrille.release();
					this.send(MUROUEST, this.x, this.y);
				}
				if (regardeAGauche()) {
					this.semGrille.acquire();
					murEst();
					this.semGrille.release();
					this.send(MUREST, this.x, this.y);
				}
				break;
			default :
				break;
		}
	}
	
	// Aller a une case voisine
	private void allerCaseVoisine(Case voisine) {
		Case current = this.grille.getCase(x, y);
		if (voisine == current.getNord()) {
			this.orienter(NORD);
		} else if (voisine == current.getOuest()) {
			this.orienter(OUEST);
		} else if (voisine == current.getEst()) {
			this.orienter(EST);
		} else if (voisine == current.getSud()) {
			this.orienter(SUD);
		} else {
			System.out.println("Err-allerCaseVoisine");
		}
		this.avanceUneCase();
	}
	
	// Version bluetooth
	private void allerCaseVoisineConnecte(Case voisine) {
		Case current = this.grille.getCase(x, y);
		if (voisine == current.getNord()) {
			this.orienter(NORD);
		} else if (voisine == current.getOuest()) {
			this.orienter(OUEST);
		} else if (voisine == current.getEst()) {
			this.orienter(EST);
		} else if (voisine == current.getSud()) {
			this.orienter(SUD);
		} else {
			System.out.println("Err-allerCaseVoisine");
		}
		int xtmp = new Integer(this.x);
		int ytmp = new Integer(this.y);
		this.avanceUneCase();
		this.send(POSITION, this.x, this.y);
		this.send(FINPOSITION, xtmp, ytmp);
	}
	
	// Possible d'accéder a la prochaine case
	private boolean canAccess(Case prochaine) {
		List<Case> voisins = new LinkedList<Case>();
		Case current = grille.getCase(x, y);
		if (current.getNord() != null) {
			voisins.add(current.getNord());
		}
		if (current.getEst() != null) {
			voisins.add(current.getEst());
		}
		if (current.getOuest() != null) {
			voisins.add(current.getOuest());
		}
		if (current.getSud() != null) {
			voisins.add(current.getSud());
		}
		return voisins.contains(prochaine);
	}

	// IA - D* lite
	public void parcoursDEtoileLite() {
		if (debug)
			LCD.drawString("parcoursD*Lite", 0, 0);
		this.orientation = askDirection();
		this.x = ask("Depart : x =", 0, 15);
		this.y = ask("Depart : y =", 0, 15);
		this.xBut = ask("But : x = ", 0, 15);
		this.yBut = ask("But : y =", 0, 15);
		grille = new Grille(ask("Taille laby : x =", 0, 15), ask("Taille laby : y =", 0, 15));
		LCD.drawString("Press button", 0, 1);
		Button.waitForAnyPress();
		LCD.clear(1);
		this.cartographierAutour();
		this.tourneADroite();
		this.cartographierAutour();
		List<Case> cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
		this.grille.getCase(x, y).setCartographie(true);;
		while (!cheminPlusCourt.isEmpty()) {
			this.afficherGrille();
			Case prochaine = cheminPlusCourt.get(0);

			cheminPlusCourt.remove(0);
			if (this.canAccess(prochaine)) {
				allerCaseVoisine(prochaine);
				if (!this.grille.getCase(x, y).isCartographie()) {
					cartographierAutour();
					this.grille.getCase(x, y).setCartographie(true);
				}
			} else {
				cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
			}
		}
		if ((this.x == this.xBut) && (this.y == this.yBut)) {
			int notes[] = {500, 600, 700, 800, 900, 1500};
			this.playSound(Sound.XYLOPHONE, notes, 100);
		} else {
			int notes[] = {1000, 900, 800, 700, 600, 500};
			this.playSound(Sound.PIANO, notes, 500);
		}
	}
	
	public void afficherGrille() {
		LCD.clear();
		this.grille.afficherGrille();
	}
	
	// Attibuts bluetooth
	private NXTConnection cx;
	private DataOutputStream out;
	private Semaphore semOut = new Semaphore(1);
	private DataInputStream in;
	private Semaphore semIn = new Semaphore(1);
	
	// Getters
	public DataInputStream getIn() {
		return this.in;
	}
	
	public DataOutputStream getOut() {
		return this.out;
	}
	
	public Grille getGrille() {
		return this.grille;
	}
	
	public List<Case> getAutresRobots() {
		return this.autresRobots;
	}
	
	public Semaphore getSemGrille() {
		return this.semGrille;
	}
	public void setContinuer(boolean continuer) {
		this.continuer = continuer;
	}
	
	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public void setNok(boolean nok) {
		this.nok = nok;
	}
	
	public void setRecalcul(boolean recalcul) {
		this.recalcul = recalcul;
	}
	
	public Semaphore getSemIn() {
		return semIn;
	}

	private void send(int codeAEnvoyer, int xAEnvoyer, int yAEnvoyer) {
		semOut.acquire();
		try {
			out.writeInt(codeAEnvoyer);
			out.flush();
			out.writeInt(xAEnvoyer);
			out.flush();
			out.writeInt(yAEnvoyer);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		semOut.release();
	}
	
	// Codes des messages bluetooth
	private static final int POSITIONROBOT = 0;
	private static final int ORIENTATIONROBOT = 1;
	private static final int TAILLELABY = 2;
	private static final int PERSONNE = 3;
	private static final int FINPERSONNE = 4;
	private static final int VALEURPERSONNECHOISIE = 6;
	private static final int DEPART = 7;
	private static final int MURNORD = 8;
	private static final int MURSUD = 9;
	private static final int MUROUEST = 10;
	private static final int MUREST = 11;
	private static final int POSITION = 12;
	private static final int FINPOSITION = 13;
	private static final int OK = 14;
	private static final int NOK = 15;
	private static final int FIN = 16;
	
	// Thread de reception d'informations
	private ThreadReception thread;
	
	// Fin du sauvetage
	private boolean continuer = true;
	
	// Variable de recalcul
	private boolean recalcul = false;
	
	// Boolean ok et nok
	private boolean ok = true;
	private boolean nok = false;

	public void sauverLesGens() {
		LCD.drawString("Attente de connexion", 0, 0);
		cx = Bluetooth.waitForConnection();
		out = cx.openDataOutputStream();
		in = cx.openDataInputStream();
		LCD.clear();
		LCD.drawString("Connecte !", 0, 0);
		int codeRecu = -1;
		int xRecu = -1;
		int yRecu = -1;
		
		// Reception position robot
		semIn.acquire();
		try {
			codeRecu = in.readInt();
			xRecu = in.readInt();
			yRecu = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (codeRecu == POSITIONROBOT) {
			this.x = new Integer(xRecu);
			this.y = new Integer(yRecu);
		}
		semIn.release();
		
		// Reception orientation du robot
		semIn.acquire();
		try {
			codeRecu = in.readInt();
			xRecu = in.readInt();
			yRecu = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (codeRecu == ORIENTATIONROBOT) {
			this.orientation = new Integer(xRecu);
		}
		semIn.release();
		
		// Reception de la taille du labyrinthe
		semIn.acquire();
		try {
			codeRecu = in.readInt();
			xRecu = in.readInt();
			yRecu = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (codeRecu == TAILLELABY) {
			grille = new Grille(xRecu, yRecu);
		}
		semIn.release();
		
		// Reception des personnes à sauver
		List<Personne> personnesASauver = new LinkedList<Personne>();
		do {
			semIn.acquire();
			try {
				codeRecu = in.readInt();
				xRecu = in.readInt();
				yRecu = in.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (codeRecu == PERSONNE) {
				personnesASauver.add(new Personne(new Integer(xRecu), new Integer(yRecu)));
			}
			semIn.release();
		} while (codeRecu != FINPERSONNE);
		
		// Signal de départ
		do {
			semIn.acquire();
			try {
				codeRecu = in.readInt();
				xRecu = in.readInt();
				yRecu = in.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			semIn.release();
		} while (codeRecu != DEPART);
		
		// Determination des valeurs des personnes a sauver, et envoi
		for (Personne p : personnesASauver) {
			List<Case> tmp = this.grille.aEtoile(this.grille.getCase(this.x, this.y), this.grille.getCase(p.getX(), p.getY()));
			this.send(PERSONNE, p.getX(), p.getY());
			this.send(VALEURPERSONNECHOISIE, tmp.size(), 0);
		}
		this.send(FINPERSONNE, 0, 0);

		// Reception de la première personne à sauver
		Personne sauv = null;
		semIn.acquire();
		try {
			codeRecu = in.readInt();
			xRecu = in.readInt();
			yRecu = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (codeRecu == PERSONNE) {
			for (Personne p : personnesASauver) {
				if (p.getX() == xRecu && p.getY() == yRecu) {
					sauv = p;
					break;
				}
			}
		}
		this.xBut = new Integer(sauv.getX());
		this.yBut = new Integer(sauv.getY());
		semIn.release();
		
		// Lancement du thread
		thread = new ThreadReception(this);
		thread.setDaemon(true);
		thread.start();
		thread.demarrer();
			
		boolean premiereCase = true;
		
		// D*
		while (continuer) {
			while (!ok && !nok);
			if (nok) {
				while (true) {
					while (!nok && !ok);
					LCD.drawString("oknok1", 0, 0);
					Delay.msDelay(2000);
					if (nok) {
						personnesASauver.remove(sauv);
					} else {
						break;
					}
					if (personnesASauver.isEmpty()) {
						continuer = false;
						break;
					}
					Personne meilleure = personnesASauver.get(0);
					int meilleureValeur = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(meilleure.getX(), meilleure.getY())).size();
					for (int i = 1; i < personnesASauver.size(); i++) {
						Personne p = personnesASauver.get(i);
						int val = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(p.getX(), p.getY())).size();
						if (val < meilleureValeur) {
							meilleure = p;
							meilleureValeur = val;
						}
					}
					this.xBut = meilleure.getX();
					this.yBut = meilleure.getY();
					sauv = meilleure;
					send(PERSONNE, this.x, this.y);
					nok = false;
				}
			}
			if (!continuer) {
				break;
			}
			ok = false;
			nok = false;
			LCD.clear();
			LCD.drawString("But=(" + this.xBut + ", " + this.yBut + ")", 0, 0);
			if (premiereCase) {
				this.cartographierAutourConnecte();
				this.tourneADroite();
				this.cartographierAutourConnecte();
				premiereCase = false;
			}
			List<Case> cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
			this.grille.getCase(x, y).setCartographie(true);;
			while (!cheminPlusCourt.isEmpty()) {
				Case prochaine = cheminPlusCourt.get(0);
				
				cheminPlusCourt.remove(0);
				if (this.canAccess(prochaine)) {
					allerCaseVoisineConnecte(prochaine);
					if (!this.grille.getCase(x, y).isCartographie()) {
						cartographierAutourConnecte();
						this.grille.getCase(x, y).setCartographie(true);
					}
				} else {
					cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
				}
				if (recalcul) {
					recalcul = false;
					semGrille.acquire();
					if (this.x != this.xBut || this.y != this.yBut) {
						cheminPlusCourt = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(this.xBut, this.yBut));
					}
					semGrille.release();
				}
			}
			if ((this.x == this.xBut) && (this.y == this.yBut)) {
				int notes[] = {500, 600, 700, 800, 900, 1500};
				this.playSound(Sound.XYLOPHONE, notes, 100);
				personnesASauver.remove(sauv);
			} else {
				int notes[] = {1000, 900, 800, 700, 600, 500};
				this.playSound(Sound.PIANO, notes, 500);
			}
			if (personnesASauver.isEmpty()) {
				break;
			}
			Personne meilleure = personnesASauver.get(0);
			int meilleureValeur = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(meilleure.getX(), meilleure.getY())).size();
			for (Personne p : personnesASauver) {
				int val = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(p.getX(), p.getY())).size();
				if (val < meilleureValeur) {
					meilleure = p;
					meilleureValeur = val;
				}
			}
			this.xBut = meilleure.getX();
			this.yBut = meilleure.getY();
			sauv = meilleure;
			this.send(PERSONNE, this.xBut, this.yBut);
		}
		
		List<Case> coins = new LinkedList<Case>();
		coins.add(grille.getCase(0, 0));
		coins.add(grille.getCase(0, (grille.getY() - 1)));
		coins.add(grille.getCase((grille.getX() - 1), 0));
		coins.add(grille.getCase((grille.getX() - 1), (grille.getY() - 1)));
		boolean continuar = true;
		while (continuar) {
			Case meilleurCoin = coins.get(0);
			int meilleurCoinVal = grille.aEtoile(grille.getCase(this.x, this.y), meilleurCoin).size();
			for (int i = 1; i < coins.size(); i++) {
				Case c = coins.get(i);
				int tmp = grille.aEtoile(grille.getCase(this.x, this.y), c).size();
				if (tmp < meilleurCoinVal) {
					meilleurCoin = c;
					meilleurCoinVal = tmp;
				}
			}
			this.xBut = meilleurCoin.getX();
			this.yBut = meilleurCoin.getY();
			List<Case> cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
			// ?? this.grille.getCase(x, y).setCartographie(true);
			while (!cheminPlusCourt.isEmpty()) {
				Case prochaine = cheminPlusCourt.get(0);
				
				cheminPlusCourt.remove(0);
				if (this.canAccess(prochaine)) {
					allerCaseVoisineConnecte(prochaine);
					if (!this.grille.getCase(x, y).isCartographie()) {
						cartographierAutourConnecte();
						this.grille.getCase(x, y).setCartographie(true);
					}
				} else {
					cheminPlusCourt = this.grille.aEtoile(this.x, this.y, this.xBut, this.yBut);
				}
				if (recalcul) {
					recalcul = false;
					semGrille.acquire();
					cheminPlusCourt = grille.aEtoile(grille.getCase(this.x, this.y), grille.getCase(this.xBut, this.yBut));
					semGrille.release();
				}
			}
			if ((this.x == this.xBut) && (this.y == this.yBut)) {
				int notes[] = {500, 600, 700, 800, 900, 1500};
				this.playSound(Sound.XYLOPHONE, notes, 100);
				continuar = false;
			} else {
				int notes[] = {1000, 900, 800, 700, 600, 500};
				this.playSound(Sound.PIANO, notes, 500);
				coins.remove(meilleurCoin);
			}
		}
		LCD.clear();
		LCD.drawString("End", 1, 1);
		LCD.clear();
		afficherGrille();
		Button.waitForAnyPress();
		
		// TODO Tenir compte des autres robots
	}
}
