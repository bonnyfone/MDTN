package source.mdtn.util;

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
	
}
