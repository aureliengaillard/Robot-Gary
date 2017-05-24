package mains;

import blocCentral.BlocCentral;
import lejos.nxt.Button;

public class MainBlocCentral {

	public static void main(String[] args) {
		BlocCentral bc = new BlocCentral();
		bc.run();
		Button.waitForAnyPress();
	}
}
