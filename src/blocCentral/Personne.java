package blocCentral;

public class Personne implements Comparable<Personne> {
	// Position de la personne
	private int x;
	private int y;
	
	// Valeur pour sauvetage
	private int valeur;
	
	// Robot associé a la valeur
	private Robot robot;
	
	// Constructeur
	public Personne(int x, int y) {
		this.x = x;
		this.y = y;
		this.valeur = -1;
		this.robot = null;	
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getValeur() {
		return this.valeur;
	}
	
	public Robot getRobot() {
		return this.robot;
	}
	
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}
	
	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public int compareTo(Personne p) {
		return this.valeur - p.valeur;
	}
	
	public boolean superiorTo(Personne p) {
		return (this.valeur > p.getValeur());
	}
}
