package source.mdtn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe di utilità che fornisce metodi per la gestione/conversione/buffering di oggetti.
 */
public class Buffering {
	
	/**
	 * Metodo che converte un oggetto in un'array di Byte.
	 * @param object l'oggetto da convertire.
	 * @return un array di byte rappresentante l'oggetto.
	 */
	public static byte[] toBytes(Object object){
		java.io.ByteArrayOutputStream baos = new
		java.io.ByteArrayOutputStream();
		try{
			java.io.ObjectOutputStream oos = new
			java.io.ObjectOutputStream(baos);
			oos.writeObject(object);
		}catch(java.io.IOException ioe){
			ioe.printStackTrace();
		}
		return baos.toByteArray();
	}


	/**
	 * Metodo che converte un 'array di byte in un oggetto.
	 * @param bytes un array di byte rappresentante l'oggetto.
	 * @return l'oggetto costruito a partire dai bytes.
	 */
	public static Object toObject(byte[] bytes){
		Object object = null;
		try{
			object = new java.io.ObjectInputStream(new
					java.io.ByteArrayInputStream(bytes)).readObject();
		}catch(java.io.IOException ioe){
			ioe.printStackTrace();
		}catch(java.lang.ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}
		return object;
	}
	
	
	/**
	 * Legge i bytes da un file.
	 * @param file il file da leggere.
	 * @return un array di byte.
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    // Get the size of the file
	    long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length &&  (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	}
	
	/**
	 * Metodo che scrive un array di bytes sul file specificato.
	 * @param file path del file su cui salvare.
	 * @param data array di bytes dei dati da salvare.
	 * @return true=salvato, false=errore salvataggio
	 */
	public static boolean writeBytesToFile(String file, byte[] data){
		try {
			File toWrite = new File(file);
			FileOutputStream fos = new FileOutputStream(toWrite);
			fos.write(data);
			fos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}	
		
	}
	
}
