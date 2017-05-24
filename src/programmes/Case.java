package programmes;

public class Case {
	// Coordonnees
	private int x;
	private int y;
	
	// Voisins
	private Case nord;
	private Case sud;
	private Case est;
	private Case ouest;
	
	// Valeurs pour le A*
	private int g;	// valeur de la distance parcourue
	private int h; // valeur de l'heuristique
	private Case previous; // predecesseur
	
	// Case cartographiée
	private boolean cartographie;
	
	// Constructeurs
	public Case(int ligne, int colonne) {
		this.x = ligne;
		this.y = colonne;
		this.nord = null;
		this.sud = null;
		this.est = null;
		this.ouest = null;
		this.g = 0;
		this.h = 0;
		this.previous = null;
		this.cartographie = false;
	}
	
	// Accesseurs
	public Case getNord() {
		return this.nord;
	}
	
	public Case getSud() {
		return this.sud;
	}
	
	public Case getEst() {
		return this.est;
	}
	
	public Case getOuest() {
		return this.ouest;
	}
	
	public void setNord(Case valeur) {
		this.nord = valeur;
	}
	
	public void setSud(Case valeur) {
		this.sud = valeur;
	}
	
	public void setEst(Case valeur) {
		this.est = valeur;
	}
	
	public void setOuest(Case valeur) {
		this.ouest = valeur;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getG() {
		return this.g;
	}
	
	public int getH() {
		return this.h;
	}
	
	public Case getPrevious() {
		return this.previous;
	}
	
	public boolean isCartographie() {
		return this.cartographie;
	}
	
	public void setG(int valeur) {
		this.g = valeur;
	}
	
	public void setH(int valeur) {
		this.h = valeur;
	}
	
	public void setPrevious(Case previous) {
		this.previous = previous;
	}
	
	public void setCartographie(boolean carto) {
		this.cartographie = carto;
	}
	
	// Methode toString
	public String toString() {
		return ("(" + this.x + ", " + this.y + ")");
	}
}
