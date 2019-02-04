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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JFileServer - Basic Client Application 
 */
public class FileClient {
	
	//The name and the port of the server we'll connect to
	private static String serverName;
	private static int port;
	
	// The file to get
	private static String filename;
	
	/**
	 * Reads bufSize bytes from dis and put them into buf
	 * @param dis
	 * @param buf
	 * @param bufSize
	 * @throws IOException
	 */
	private static void readByte(DataInputStream dis, byte[] buf, int bufSize) throws IOException {
		int nread = dis.read(buf);
		int nb = 0;
		while (nread < bufSize) {
			nb = dis.read(buf, nread, bufSize - nread);
			if (nb == -1)
				break;

			nread = nread + nb;
		}
	}
	
	/**
	 * Get the file "filename" from server s
	 * @param s
	 * @param filename
	 * @throws IOException
	 */
	private static void getFile(Socket s) throws IOException {
		
		// Getting socket streams
		OutputStream os = s.getOutputStream();
		InputStream is = s.getInputStream();
		
		DataOutputStream dos = new DataOutputStream(os);
		DataInputStream dis = new DataInputStream(is);
		
		
		byte[] b = filename.getBytes();

		// Sending filename to server
		dos.writeInt(b.length);
		dos.write(b);

		
		// Extracting filename and creating corresponding file
		// For example if we want the file  /animals/dolphin.png from the server
		// It will be created as dolphin.png on client side
		Path p = Paths.get(filename);
		filename = p.getFileName().toString(); 		
		FileOutputStream file = new FileOutputStream(filename);
		System.out.println("Création du fichier : " + filename);

		// Receiving file chunk by chunk
		byte buf[] = new byte[1024];

		// Reading chunks
		int bufSize = dis.readInt();
		
		while (bufSize == buf.length) {
			
			readByte(dis,buf,bufSize);
			file.write(buf);
			
			bufSize = dis.readInt();
		}
		
		// Last chunk (smaller size)
		readByte(dis,buf,bufSize);
		file.write(buf, 0, bufSize);
		
		file.close();
	}
	
	/**
	 * Reads command line arguments
	 * and setup all values properly
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		try {
			serverName = args[0];
		} catch (Exception e) {
			System.out.println("Server not provided, using localhost");
			serverName = "127.0.0.1";
		}
		
		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Port not provided, using default port 4242");
			port = 4242;
		}
		
		try {
			filename = args[2];
		} catch (Exception e) {
			System.out.println("Filename not provided, for test purpose, we'll use a chameau.jpg");
			filename = "chameau.jpg";
		}
	}
	
	
	public static void main(String[] args) {

		// Getting server name and port from the command line
		parseArgs(args);

		// Connecting to server
		Socket server;
		try {
			server = new Socket(serverName, port);
		} catch (ConnectException e) {
			System.out.println("Can't connect to server. Is the server running ?");
			return;
		} catch (UnknownHostException e) {
			System.out.println("Unknown host " + serverName);
			return;
		} catch (Exception e) {
			System.out.println("Unhandled Exception, exiting.");
			return;
		}
		
		// Getting the file from the server
		try {
			getFile(server);
		} catch (IOException io_e){
			System.out.println("An unhandled exception happened when trying to get the file, transfer failed.");
		}
		

		// Closing the server
		try {
			server.close();
		} catch (IOException io_e) {
			System.out.println("Error when closing server socket.");
		}
		
		return;
	}
}
