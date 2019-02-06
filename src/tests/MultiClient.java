package tests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Special implementation of the File Server Client as a Thread
 */
public class MultiClient extends Thread {

	// The name and the port of the server we'll connect to
	private String serverName;
	private int port;

	// The file to get
	private String filename;

	/**
	 * Create an instance of a client
	 * 
	 * @param serverName The server to connect to
	 * @param port       The port of the server
	 * @param filename   The name of the file to get
	 */
	MultiClient(String serverName, int port, String filename) {
		this.serverName = serverName;
		this.port = port;
		this.filename = filename;
	}

	/**
	 * Reads bufSize bytes from dis and put them into buf
	 * 
	 * @param dis
	 * @param buf
	 * @param bufSize
	 * @throws IOException
	 */
	private void readByte(DataInputStream dis, byte[] buf, int bufSize) throws IOException {
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
	 * 
	 * @param s
	 * @param filename
	 * @throws IOException
	 */
	private void getFile(Socket s) throws IOException {

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
		// For example if we want the file /animals/dolphin.png from the server
		// It will be created as dolphin.png on client side
		Path p = Paths.get(filename);
		filename = p.getFileName().toString();
		FileOutputStream file = new FileOutputStream(filename);

		// Receiving file chunk by chunk
		byte buf[] = new byte[1024];

		// Reading chunks
		int bufSize = dis.readInt();

		while (bufSize == buf.length) {

			readByte(dis, buf, bufSize);
			file.write(buf);

			bufSize = dis.readInt();
		}

		// Last chunk (smaller size)
		readByte(dis, buf, bufSize);
		file.write(buf, 0, bufSize);

		file.close();
	}

	@Override
	public void run() {
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
		} catch (IOException io_e) {
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
