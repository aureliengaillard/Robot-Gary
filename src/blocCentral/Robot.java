package blocCentral;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.comm.BTConnection;
import programmes.Semaphore;

public class Robot {
	// Streams de lecture et d'écriture + semaphores
	private DataInputStream in;
	private DataOutputStream out;
	private Semaphore semOut = new Semaphore(1);
	
	// Connexion Bluetooth
	private BTConnection connexion;
	
	// Constructeur
	public Robot(DataInputStream in, DataOutputStream out, BTConnection cx) {
		this.in = in;
		this.out = out;
		this.connexion = cx;
	}
	
	// Getter
	public DataInputStream getIn() {
		return this.in;
	}
	
	// Deconnexion
	public void disconnect() {
		try {
			in.close();
			out.close();
			connexion.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Envoi d'info
	public void send(int code, int x, int y) {
		semOut.acquire();
		try {
			out.writeInt(code);
			out.flush();
			out.writeInt(x);
			out.flush();
			out.writeInt(y);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		semOut.release();
	}
}
