package programmes;

import java.util.LinkedList;
import java.util.List;

import lejos.nxt.LCD;
import programmes.Case;

public class Grille {
	// Dimensions de la grille
	private int x;
	private int y;

	// Grille : tableau 2 dimensions de Case
	private Case grille[][];

	// Constructeur
	public Grille(int x, int y) {
		this.x = x;
		this.y = y;
		this.grille = new Case[x][y];
		// Initialisation des Cases
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grille[i][j] = new Case(i, j);
			}
		}
		// Initialisation des références vers les cases voisines, pour chaque case de la grille
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grille[i][j].setNord(this.getNord(i, j));
				grille[i][j].setSud(this.getSud(i, j));
				grille[i][j].setEst(this.getEst(i, j));
				grille[i][j].setOuest(this.getOuest(i, j));
			}
		}
	}
	
	// Accesseurs
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	// Obtenir une case de la grille
	public Case getCase(int x, int y) {
		return this.grille[x][y];
	}

	// Obtenir les voisins d'une Case
	// Si voisin inexistant (Bords de la grille), renvoie null
	public Case getNord(int x, int y) {
		if (y != 0) {
			return grille[x][y - 1];
		} else {
			return null;
		}
	}

	public Case getSud(int x, int y) {
		if (y != (this.y - 1)) {
			return grille[x][y + 1];
		} else {
			return null;
		}
	}

	public Case getEst(int x, int y) {
		if (x != (this.x - 1)) {
			return grille[x + 1][y];
		} else {
			return null;
		}
	}

	public Case getOuest(int x, int y) {
		if (x != 0) {
			return grille[x - 1][y];
		} else {
			return null;
		}
	}

	// Remise a 0 de la grille dans le A* (couts g, h et previous)
	public void reset() {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grille[i][j].setG(0);
				grille[i][j].setH(0);
				grille[i][j].setPrevious(null);
			}
		}
	}

	//// Methodes internes pour A*
	
	// Cout F le plus bas dans les Cases de la liste Open
	// (F = G + H)
	// (G = Cout du parcours deja effectué jusqu'à la case)
	// (H = Cout estimé par l'heuristique pour aller à la cas but)
	private Case lowestFInOpen(LinkedList<Case> openList) {
		Case ret = openList.get(0);
		for (Case it : openList) {
			if ((it.getG() + it.getH()) < (ret.getG() + ret.getH())) {
				ret = it;
			}
		}
		return ret;
	}

	// Retourne la valeur de l'Heuristique choisie pour aller de la case Depart à la case Arrivée
	// Ici, l'heuristique choisie est la distance de Manhattan (ou distance "City Block")
	private int heuristique(Case depart, Case arrivee) {
		return (Math.abs(depart.getX() - arrivee.getX()) + Math.abs(depart.getY() - arrivee.getY()));
	}

	// Retourne la liste des cases adjacentes à la case courrante
	// Si les cases adjacentes sont dans la liste Closed, on ne les retourne pas
	private List<Case> getAdjacent(Case current, List<Case> closedList) {
		List<Case> ret = new LinkedList<Case>();
		if (current.getNord() != null) {
			if (!closedList.contains(current.getNord())) {
				ret.add(current.getNord());
			}
		}
		if (current.getEst() != null) {
			if (!closedList.contains(current.getEst())) {
				ret.add(current.getEst());
			}
		}
		if (current.getSud() != null) {
			if (!closedList.contains(current.getSud())) {
				ret.add(current.getSud());
			}
		}
		if (current.getOuest() != null) {
			if (!closedList.contains(current.getOuest())) {
				ret.add(current.getOuest());
			}
		}
		return ret;
	}

	// Une fois le A* effectué, on parcours le labyrinthe de la fin au début (via les cases previous) pour obtenir le chemin le plus court)
	private List<Case> calcPath(Case depart, Case arrivee) {
		List<Case> ret = new LinkedList<Case>();
		ret.add(arrivee);
		while (ret.get(0).getPrevious() != depart) {
			ret.add(0, ret.get(0).getPrevious());
		}
		return ret;
	}

	// Plus court chemin : A*
	public List<Case> aEtoile(int oldX, int oldY, int newX, int newY) {
		LinkedList<Case> openList = new LinkedList<Case>();
		LinkedList<Case> closedList = new LinkedList<Case>();
		openList.add(getCase(oldX, oldY));

		boolean done = false;
		Case current;
		while (!done) {
			current = lowestFInOpen(openList);
			closedList.add(current);
			openList.remove(current);

			if ((current.getX() == newX) && (current.getY() == newY)) {
				List<Case> chemin = calcPath(getCase(oldX, oldY), current);
				this.reset();
				return chemin;
			}

			List<Case> adjacentNodes = getAdjacent(current, closedList);
			for (int i = 0; i < adjacentNodes.size(); i++) {
				Case currentAdj = adjacentNodes.get(i);
				if (!openList.contains(currentAdj)) {
					currentAdj.setPrevious(current);
					currentAdj.setH(heuristique(current, getCase(newX, newY)));
					currentAdj.setG(current.getG() + 1);
					openList.add(currentAdj);
				} else {
					if (currentAdj.getG() > (current.getG() + 1)) {
						currentAdj.setPrevious(current);
						currentAdj.setG(current.getG() + 1);
					}
				}
			}

			if (openList.isEmpty()) {
				this.reset();
				return new LinkedList<Case>();
			}
		}
		this.reset();
		return null;
	}
	
	// Meme fonction, avec des Case au lieu des coordonnées
	public List<Case> aEtoile(Case depart, Case arrivee) {
		return aEtoile(depart.getX(), depart.getY(), arrivee.getX(), arrivee.getY());
	}
	
	// Affichage de la Grille sur l'écran de la brique NXT
	public void afficherGrille() {
		int largeurCase = LCD.SCREEN_WIDTH / this.x - 1;
		int hauteurCase = LCD.SCREEN_HEIGHT / this.y - 1;
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				if (grille[i][j].getNord() == null) {
					for (int k = (i * largeurCase); k < ((i * largeurCase) + largeurCase); k++) {
						LCD.setPixel(k, (j * hauteurCase), 1);
					}
				}
				if (grille[i][j].getOuest() == null) {
					for (int k = (j * hauteurCase); k < ((j * hauteurCase) + hauteurCase); k++) {
						LCD.setPixel((i * largeurCase), k, 1);
					}
				}
				if (grille[i][j].getSud() == null) {
					for (int k = (i * largeurCase); k < ((i * largeurCase) + largeurCase); k++) {
						LCD.setPixel(k, ((j * hauteurCase) + hauteurCase), 1);
					}
				}
				if (grille[i][j].getEst() == null) {
					for (int k = (j * hauteurCase); k < ((j * hauteurCase) + hauteurCase); k++) {
						LCD.setPixel(((i * largeurCase) + largeurCase), k, 1);
					}
				}
			}
		}
	}
}
