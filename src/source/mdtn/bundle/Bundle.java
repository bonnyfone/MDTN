package source.mdtn.bundle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Bundle implements Serializable {
	
	private static final long serialVersionUID = 5274983398789301270L;
	
	/** Il PrimaryBlock del bundle. */
	private PrimaryBlock bundlePrimaryBlock;
	
	/** Il PayloadBlock del bundle. */
	private PayloadBlock bundlePayloadBlock;

	//TODO Il costruttore va rivisto per l'uso pratico che si farà
	public Bundle(){
		bundlePrimaryBlock = new PrimaryBlock();
		bundlePayloadBlock = new PayloadBlock();
		
	}
	
	//MAIN DI PROVA
	public static void main(String args[]) throws Exception{
		System.out.println("AVVIATO");
		Bundle aaa = new Bundle();
		aaa.bundlePrimaryBlock.setFlag_priority(2);
		System.out.println("Creato!\n"+aaa.getPrimary().getCreationTimestamp()+"  prio="+aaa.getPrimary().getFlag_priority() + "  life="+aaa.getPrimary().getLifetime());
		
		//Scrive
		/*
		FileOutputStream fos = new FileOutputStream("oggettino.tmp");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(aaa);
		oos.close();
		
		System.exit(0);
		*/
		
		//Legge
		Bundle readen;
		FileInputStream fis = new FileInputStream("oggettino.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		readen = (Bundle) ois.readObject();
		ois.close();
		System.out.println("Letto!\n"+readen.bundlePrimaryBlock.getCreationTimestamp()+"  prio="+readen.bundlePrimaryBlock.getFlag_priority()  + "  life="+aaa.getPrimary().getLifetime());
	}
	
	
	
	/**
	 * Metodo che ritorna il PrimaryBlock del bundle corrente.
	 * @return il PrimaryBlock del bundle.
	 */
	public PrimaryBlock getPrimary(){
		return bundlePrimaryBlock;
	}
	
	/**
	 * Metodo che ritorna il PayloadBlock del bundle corrente.
	 * @return il PayloadBlock del bundle.
	 */
	public PayloadBlock getPayload(){
		return bundlePayloadBlock;
	}
}
