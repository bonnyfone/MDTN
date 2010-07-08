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
	
	/**Contatore dei byte letti*/
	private long counter;
	
	/**Dimensione totale*/
	private long size;
	
	/**InpusStream di base*/
	private InputStream input;
	
	/**Timeout*/
	private long timeout;
	
	/**
	 * Costruttore dello stream polled.
	 * 
	 * @param input
	 * @param timeout
	 * @param estimatedSize
	 */
	public PolledInputStream(InputStream input, long timeout, long estimatedSize) {
		this.input = input;
		this.timeout = timeout;
		size= estimatedSize;
		counter=0;
	}

	/** Mask dello stream normale*/
	public int available() throws IOException {
		return input.available();
	}

	/** Mask dello stream normale*/
	public void close() throws IOException {
		input.close();
	}

	/** Mask dello stream normale*/
	public void mark(int readlimit) {
		input.mark(readlimit);
	}

	/** Mask dello stream normale*/
	public boolean markSupported() {
		return input.markSupported();
	}

	/**Legge i byte disponibili entro il timeout.*/
	public int read() throws IOException {
		waitForAvailable();
		return input.read();
	}

	/**Legge i byte disponibili dentro l'array*/
	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}

	/**Legge i byte disponibili dentro l'array a partire dall'offset specificato*/
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

	/** Mask dello stream normale*/
	public void reset() throws IOException {
		input.reset();
	}

	/** Mask dello stream normale*/
	public long skip(long n) throws IOException {
		return input.skip(n);
	}

	/**
	 * Metodo che indica se l'operazione è stata completata.
	 * @return un booleano che indica se l'operazione è finita.
	 */
	public boolean finished(){
		if(counter>=size) return true;
		return false;
	}

	/**
	 * Metodo che attende (fino al timeout) l'arrivo di dati.
	 * @throws IOException
	 */
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