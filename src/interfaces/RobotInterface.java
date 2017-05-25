package interfaces;

/**
 * Interface que tous les robots lÃ©go doivent implémenter
 */

public interface RobotInterface
{
	/**
	 * Le robot doit avancer d'une (et d'une seule) case en marche avant.
	 * Il est conseillé que le robot se recale automatiquement avec la bande blanche pendant le déplacement.
	 */
	public void avanceUneCase();

	/**
	 * Le robot doit tourner d'un quart de tour vers la gauche.
	 */
	public void tourneAGauche();
	
	/**
	 * Le robot doit tourner d'un quart de tour vers la droite.
	 */
	public void tourneADroite();
	
	/**
	 * Le robot doit faire un demi-tour sur lui-même.
	 */
	public void tourneDemiTour();
	
	/**
	 * Le robot doit regarder s'il y a un mur devant lui.
	 * @return true s'il y a un mur, false sinon
	 */
	public boolean regardeToutDroit();
	
	/**
	 * Le robot doit regarder s'il y a un mur à sa gauche.
	 * A la fin de cette méthode, le robot doit être au même endroit (et même orientation) qu'à  son début.
	 * @return true s'il y a un mur, false sinon
	 */
	public boolean regardeAGauche();
	
	/**
	 * Le robot doit regarder s'il y a un mur à sa droite.
	 * A la fin de cette méthode, le robot doit être au même endroit (et même orientation) qu'à son début.
	 * @return true s'il y a un mur, false sinon
	 */
	public boolean regardeADroite();
	
}
