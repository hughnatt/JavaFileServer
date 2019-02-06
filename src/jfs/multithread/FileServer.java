/*
 * Java File Server 
 * Application de transfert de fichier TCP
 * INFO4 : TP Application Réparties
 * @author A.Ancrenaz T.Sauton
 * Polytech Grenoble - Janvier 2019
 */
package jfs.multithread;

import java.io.IOException;
import java.net.*;

/**
 * JFileServer - Multithreaded Server Application.
 */
public class FileServer {

	// Server port
	private static int port;
	// Default path for files
	private static String path;
	
	/**
	 * Reads command line arguments and
	 * setup port and path values properly
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("[WARNING] Port not provided, using default port 4242");
			port = 4242;
		}
		
		try {
			path = args[1];
		} catch (Exception e) {
			System.out.println("[WARNING] Default path for files not provided, using files/ directory");
			path = "files/";
		}
	}
	
	public static void main(String[] args) {
		
		// Getting server port and default path for file
		parseArgs(args);
		
		// Creating server socket
		ServerSocket server;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("[ERROR] Server creation failed, exiting.");
			return;
		}
		
		System.out.println("[DEBUG] Server running on port " + port);
		
		while(true) {
			// Connecting to client
			Socket client;
			try {
				client = server.accept();
			} catch (IOException e) {
				System.out.println("[WARNING] Error when accepting a client.");
				return;
			}
			
			
			System.out.println("[DEBUG] Client "+ client.getInetAddress() + " connected");
			
			//Creating new worker thread to handle client request
			Thread t = new Thread(new Worker(client,path));
			t.start();
			
			System.out.println("[DEBUG] Worker " + t.getId() + " handles client " + client);
			
		}
	}
}
