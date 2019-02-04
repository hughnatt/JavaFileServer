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

public class ServerStub {

	public static void main(String[] args) throws IOException {
		int port = 4242;
		ServerSocket server = new ServerSocket(port);
		
		while(true) {
			Socket client = server.accept();
			System.out.println("Client "+ client.getInetAddress() + " connected");
			
			OutputStream os = client.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			
			InputStream is = client.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			
			int length = dis.readInt();
			byte[] name = new byte[length];
			
			int nread = 0;
			int nb = 0;
			
			while(nread<length) {
				nb = dis.read(name,nread,length-nread);
				if(nb == -1)
					break;
				
				nread = nread + nb;
			}
					
			String nameClient = new String(name);	
			
			nameClient = "Hello " + nameClient;
			System.out.println(nameClient);
			
			byte[] b = nameClient.getBytes();
			
			dos.writeInt(b.length);
			dos.write(b);
			
			client.close();
				
		}
	}
}
