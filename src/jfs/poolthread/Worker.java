/*
 * Java File Server 
 * Application de transfert de fichier TCP
 * INFO4 : TP Application Réparties
 * @author A.Ancrenaz T.Sauton
 * Polytech Grenoble - Janvier 2019
 */
package jfs.poolthread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * The worker class tries to get a client socket
 * from the pool buffer and handles his request
 */
public class Worker implements Runnable  {
	
	String path;
	BlockingQueue<Socket> pool;
	int id; //just for debug
	
	/**
	 * 
	 * @param path Path to the files
	 * @param pool Client socket pool
	 * @param id Thread id
	 */
	Worker(String path, BlockingQueue<Socket> pool, int id){
		this.path = path;
		this.pool = pool;
		this.id = id;
	}
	
	/**
	 * Wait for receiving a filename from s
	 * and send the corresponding file
	 * @param s
	 * @throws IOException 
	 */
	private void sendFile(Socket client) throws IOException {
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

	@Override
	public void run()  {
		while(true) {
			
			//Getting client socket from the pool
			Socket client;
			try {
				client = pool.take();
			} catch (InterruptedException e) {
				return;
			}
			
			System.out.println("[DEBUG] Worker " + id + " working on client " + client);
			
			// Handling client request
			try {
				sendFile(client);
			} catch (IOException e) {
				System.out.println("[ERROR] An unhandled exception happened when trying to get the file, transfer failed.");
				return;
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
