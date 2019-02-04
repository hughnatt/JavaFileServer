/*
 * Java File Server 
 * Application de transfert de fichier TCP
 * INFO4 : TP Application Réparties
 * @author A.Ancrenaz T.Sauton
 * Polytech Grenoble - Janvier 2019
 */
package jfs.basic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

/**
 * JFileServer - Basic Server Application 
 */
public class FileServer {
	
	// Server port
	private static int port;
	// Default path for files
	private static String path;
	
	/**
	 * Wait for receiving a filename from s
	 * and send the corresponding file
	 * @param s
	 * @throws IOException 
	 */
	private static void sendFile(Socket client) throws IOException {
		OutputStream os = client.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		
		
		// Get filename from client
		InputStream is = client.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		
		// We receive the length of the filename
		int length = dis.readInt();
		
		// We read the name in the buffer
		byte[] name = new byte[length];
		
		int nread = 0;
		int nb = 0;
		
		// Secure socket reading of the filename
		while(nread<length) {
			nb = dis.read(name,nread,length-nread);
			if(nb == -1)
				break;
			
			nread = nread + nb;
		}
		
		//We have the filename
		String fileName = new String(name);
		
		//This is the complete path to the file on the server
		fileName = path + fileName;
		System.out.println("[DEBUG] File to send: "+ fileName);
		
		
		//Open file
		
		//TODO : In case of file not found, what are we sending back to client ?
		FileInputStream fis = new FileInputStream(fileName);
		
		// Reading and Sending file content 1024 bytes at a time
		byte buf[] = new byte[1024];
		
		//Read file bytes
		int bufSize = fis.read(buf);
		while (bufSize == buf.length) {
			//Send bytes
			dos.writeInt(bufSize);
			dos.write(buf);
			
			//Read file bytes
			bufSize = fis.read(buf);
		}
		//Send last bytes
		dos.writeInt(bufSize);
		dos.write(buf);
		
		
		//Close file
		fis.close();
	}
	
	/**
	 * Reads command line arguments and
	 * setup port and path values properly
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Port not provided, using default port 4242");
		}
		
		try {
			path = args[1];
		} catch (Exception e) {
			System.out.println("Default path for files not provided, using files/ directory");
		}
	}
	
	/**
	 * Server entrypoint
	 * @param args
	 */
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
			
			// Handling client request
			try {
				sendFile(client);
			} catch (IOException io_e) {
				System.out.println("[ERROR] An unhandled exception happened when trying to get the file, transfer failed.");
			}
			
			// Closing client socket
			try {
				client.close();
			} catch (IOException e) {
				System.out.println("[ERROR] Error when closing client socket.");
			}	
		}
	}
}
