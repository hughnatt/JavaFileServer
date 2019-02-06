package tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


class ReadingPerfs {

	private static OutputStream fos;
	private static FileInputStream fis;
	
	private static void closeFile() {
		try {
			fos.close();
		} catch (IOException e) {
		}
		
		return;
	}
	

	public static void main(String args[]) {
		long start,end,result;
		String sampleFile = "src/tests/perf-sample.file";
		Random r = new Random();
		
		
		System.out.println("[TESTS] Running Performance Tests");
		
		/**
		 * SAMPLE FILE CREATION
		 */
		System.out.print("[STEP1] Creating sample file...");
		
		try {
			fos = new FileOutputStream(sampleFile);
		} catch (FileNotFoundException e) {
			return;
		}
		
		for (int i = 0; i < 1024; i++) {
			byte b[] = new byte[512];
			r.nextBytes(b);
			try {
				fos.write(b);
			} catch (IOException e) {
				closeFile();
				return;
			}
		}
		
		System.out.println("Done");
		
		/**
		 * BYTE BY BYTE READING
		 */
		System.out.println("[STEP2] Running byte by byte reading test...");
		
		// Start Measure Here
		start = System.currentTimeMillis();		
		
		try {
			fis = new FileInputStream(sampleFile);
		} catch (FileNotFoundException e) {
			closeFile();
		}
		
		
		int b = 0;
		
		while (b != -1) {
			try {
				b = fis.read();
			} catch (IOException e1) {
				closeFile();
			}
		}
		
		
		// End of measure here
		end = System.currentTimeMillis();
		
		result = end - start;
		System.out.println("\tFile read in " + result + " ms");
		
		
		/**
		 * CHUNK READING
		 */
		
		System.out.println("[STEP3] Running chunk reading test...");
		// Start Measure here
		start = System.currentTimeMillis();
		
		try {
			fis = new FileInputStream(sampleFile);
		} catch (FileNotFoundException e) {
			closeFile();
			return;
		}
		
		byte buf[] = new byte[1024];
		int bufSize;
		
		try {
			bufSize = fis.read(buf);
		} catch (IOException e) {
			closeFile();
			return;
		}
		
		while (bufSize == buf.length) {
			try {
				bufSize = fis.read(buf);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		// End of measure here
		end = System.currentTimeMillis();
		
		result = end - start;
		
		System.out.println("\tFile read in " + result + " ms");
		
		closeFile();
		return;
	}

}
