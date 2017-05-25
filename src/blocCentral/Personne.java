package blocCentral;

public class Personne {
	// Position de la personne
	private int x;
	private int y;
	
	// Valeur pour sauvetage (heuristique la plus faible parmis les robots)
	private int valeur;
	
	// Robot associé a la valeur (robot le plus proche / meilleure heuristique
	private Robot robot;
	
	// Constructeur
	public Personne(int x, int y) {
		this.x = x;
		this.y = y;
		this.valeur = -1;
		this.robot = null;	
	}
	
	// Getters & setters
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
	
	// Outil de comparaison
	// On utilise pas les mécanismes Java standards, incompatibles Lejos
	public boolean superiorTo(Personne p) {
		return (this.valeur > p.getValeur());
	}
}
