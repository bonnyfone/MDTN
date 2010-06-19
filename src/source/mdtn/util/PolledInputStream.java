package source.mdtn.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Adapter to change blocking input into polled input with timeout.
 * @author prima versione di Filip Larsen<br>
 * 
 * Modificato e corretto da Stefano Bonetta per funzionare e terminare correttamente nel caso in cui non ci siano
 * problemi di connessione.
 * 
 */
public class PolledInputStream extends InputStream {
	private boolean finished;
	private long counter;
	private long size;
	
	public PolledInputStream(InputStream input, long timeout, long estimatedSize) {
		this.input = input;
		this.timeout = timeout;
		size= estimatedSize;
		counter=0;
	}

	public int available() throws IOException {
		return input.available();
	}

	public void close() throws IOException {
		input.close();
	}

	public void mark(int readlimit) {
		input.mark(readlimit);
	}

	public boolean markSupported() {
		return input.markSupported();
	}

	public int read() throws IOException {
		waitForAvailable();
		return input.read();
	}

	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if(finished())return -1;
		waitForAvailable();
		int n = available();
		//System.out.println(n);
		int readen =input.read(b, off, Math.min(len,n));
		counter+=readen;
		
		//System.out.println(counter+"/"+size);
		return readen;
	}

	public void reset() throws IOException {
		input.reset();
	}

	public long skip(long n) throws IOException {
		return input.skip(n);
	}


	private InputStream input;
	private long timeout;

	private void sleep(long time) {
		try {
			Thread.sleep(Math.max(0,time));
		} catch (InterruptedException ignore) {
		}
	}
	
	public boolean finished(){
		if(counter>=size) return true;
		return false;
	}

	private void waitForAvailable() throws IOException {
		long until = System.currentTimeMillis() + timeout;
		while (available() == 0) {

			if (System.currentTimeMillis() > until) {
				if(counter>=size) return;
				throw new IOException("input timed out");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignore) {
			}
		}
	}


}