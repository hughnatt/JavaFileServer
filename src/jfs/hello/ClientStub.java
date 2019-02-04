/*
 * Java File Server 
 * Application de transfert de fichier TCP
 * INFO4 : TP Application Réparties
 * @author A.Ancrenaz T.Sauton
 * Polytech Grenoble - Janvier 2019
 */
package jfs.hello;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class ClientStub {
	
	
	public static void main(String[] args) throws IOException {
		String serverName = "localhost";
		int port = 4242;
		
		Socket server;
		try {
			server = new Socket(serverName,port);	
		} catch (ConnectException e) {
			System.out.println("Can't connect to server. Is the server running ?");
			return;
		} catch (UnknownHostException e) {
			System.out.println("Unknown host " + serverName);
			return;
		} catch (Exception e) {
			System.out.println("Unhandled Exception");
			e.printStackTrace();
			return;
		}
		
		
		OutputStream os = server.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		
		InputStream is = server.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		
		
		String name = "ClientName";
		
		byte[] b = name.getBytes();
		
		dos.writeInt(b.length);
		dos.write(b);
		
		int length = dis.readInt();
		byte[] message = new byte[length];
		
		int nread = 0;
		int nb = 0;
		
		while(nread<length) {
			nb = dis.read(message,nread,length-nread);
			if(nb == -1)
				break;
			nread = nread + nb; 
		}
		
		String display = new String(message);
		
		System.out.println(display);
		
		server.close();
		
	}
}
