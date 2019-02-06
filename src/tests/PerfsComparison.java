package tests;

/**
 * Test class for comparing the different server implementations
 * This will create multiple clients and display
 * NB: The server must be running before starting the test1
 */
public class PerfsComparison {

	private final static int NCLIENTS = 10;
	private final static String SERVER = "127.0.0.1";
	private final static int PORT = 4242;
	private final static String FILE = "chameau.jpg";
	
	public static void main(String args[]) {
		
		System.out.println("Launch the server you want to test before testing");
		
		MultiClient clients[] = new MultiClient[NCLIENTS];
		
		for (int i = 0; i < NCLIENTS; i++) {
			clients[i] = new MultiClient(SERVER,PORT,FILE);
		}
		
		long start,end;
		
		start = System.currentTimeMillis();
		
		for (int i = 0; i < NCLIENTS; i++) {
			clients[i].start();
		}
		
		for (int i = 0; i < NCLIENTS; i++) {
			try {
				clients[i].join();
			} catch (InterruptedException e) {
				System.out.println("An interruption occured, displayed time might not be accurate");
			}
		}
		
		end = System.currentTimeMillis();
		
		long time = end - start;
		
		System.out.println("All files retrieved in " + time + " ms");
	}
}
