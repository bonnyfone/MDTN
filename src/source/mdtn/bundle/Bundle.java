package source.mdtn.bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Bundle implements Serializable {

	private static final long serialVersionUID = 5274983398789301270L;

	/** Il PrimaryBlock del bundle. */
	private PrimaryBlock bundlePrimaryBlock;

	/** Il PayloadBlock del bundle. */
	private PayloadBlock bundlePayloadBlock;

	/** Numero di creazione sequenziale */
	private static int sequenceCounter=0;


	//TODO Il costruttore va rivisto per l'uso pratico che si farà
	public Bundle(){
		bundlePrimaryBlock = new PrimaryBlock();
		bundlePayloadBlock = new PayloadBlock();

		sequenceCounter++;

		bundlePrimaryBlock.setCreationSequenceNumber(sequenceCounter);
	}


	/**
	 * Salvataggio fisico del bundle su disco. 
	 * @param path path della directory in cui salvare il bundle.
	 * @return true=salvato, false=non salvato.
	 */
	public boolean store(String path){
		String filename = "";

		if(getPayload().getType().equals("REPORT")){
			filename= getPrimary().getReportTo().getHost()+"_"+ 
			getPrimary().getCreationTimestamp() +"_"+
			getPrimary().getCreationSequenceNumber()+
			".report";
		}
		else{
			filename= getPrimary().getSource().getHost()+"_"+ 
			getPrimary().getCreationTimestamp() +"_"+
			getPrimary().getCreationSequenceNumber()+
			".bundle";
		}

		System.out.println("Store: "+filename);
		try{
			FileOutputStream fos = new FileOutputStream(path+filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();	
		}
		catch(IOException e){return false;}
		return true;
	}

	public boolean delete(String path){
		String filename = "";

		if(getPayload().getType().equals("REPORT")){
			filename= getPrimary().getReportTo().getHost()+"_"+ 
			getPrimary().getCreationTimestamp() +"_"+
			getPrimary().getCreationSequenceNumber()+
			".report";
		}
		else{
			filename= getPrimary().getSource().getHost()+"_"+ 
			getPrimary().getCreationTimestamp() +"_"+
			getPrimary().getCreationSequenceNumber()+
			".bundle";
		}

		System.out.println("DELETING: "+filename);

		File toDelete = new File(path+filename);
		boolean r=toDelete.delete();

		//		File ren = new File(filename+".deleted");
		//		if(!r)return toDelete.renameTo(ren);

		return r;
	}

	public String getFilePath(){
		String filename = getPrimary().getSource().getHost()+"_"+ 
		getPrimary().getCreationTimestamp() +"_"+
		getPrimary().getCreationSequenceNumber()+ ".bundle";

		return filename;
	}

	/**
	 * Recupera un bundle dal disco.
	 * @param filepath path del file contenente il bundle.
	 * @return il Bundle salvato su disco.
	 */
	public static Bundle retrive(String filepath){
		try{
			File f = new File(filepath);
			if(!f.canRead())System.out.println("Non si legge");
			FileInputStream fis = new FileInputStream(filepath);
			ObjectInputStream oos = new ObjectInputStream(fis);
			Bundle r = (Bundle) oos.readObject();
			oos.close();
			return r;
		}
		catch(IOException e){System.out.println("IOExc");return null;} catch (ClassNotFoundException e) {return null;}
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

	/** Metodo che resetta il sequenceNumber
	 */
	public static void resetSequenceNumber(){
		sequenceCounter=0;
	}
}
