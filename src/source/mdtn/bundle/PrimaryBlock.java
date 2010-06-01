package source.mdtn.bundle;

import java.io.Serializable;
import java.net.URI;

/**
 * Classe che rappresenta il PrimaryBlock di un bundle.
 */
public class PrimaryBlock implements Serializable{

	private static final long serialVersionUID = -8537728216389350235L;

	/** Versione del protocollo utilizzato. RFC5050 stabilisce il funzionamento del protocollo alla versione 0x06. */
	private byte version = 0x06;
	
	/** Array contentente i Bundle processing control flag. */
	private boolean processingControlFlag[];
	
	/** Lunghezza in byte del PrimaryBlock */
	private long blockLenght;
	
	/** URI che identifica l'EID di destinazione. */
	private URI destination;
	
	/** URI che identifica l'EID sorgente. */
	private URI source;
	
	/** URI che identifica l'EID a cui inviare i report. */
	private URI reportTo;
	
	/** URI che identifica l'EID del nodo che custodisce il bundle. */
	private URI custodian;
	
	/** Timestamp che rappresenta il momento in cui è stato creato il bundle (espresso in secondi a partire dall'anno 2000) */
	private long creationTimestamp;
	
	/** Numero di sequenza che identifica ulteriormente questo bundle, tra tutti gli altri che sono stati creati nel medesimo secondo. */
	private long creationSequenceNumber;
	
	/** Tempo di vita del bundle espresso in offset di secondi a partire dal creationTimestamp */
	private long lifetime;
	
	/** Valido solo se il bundle è un frammento. In tal caso, indica la posizione in cui il payload va posizionato nel bundle originale. */
	private long fragmentOffset;
	
	/** Valido solo se il bundle è un frammento. In tal caso, rappresenta la dimensione totale del payload del bundle originale. */
	private long totalAppDataLenght;
	
	
	/**
	 * Costruttore standard di un PrimaryBlock.
	 */
	public PrimaryBlock(){ //TODO: IL COSTRUTTORE CONTIENE CODICE DI TESTING! OCCHIO!
		processingControlFlag = new boolean[21]; //inizializzazione "a zero" automatica, tutti i flag a false.

		/*-- codice di prova --*/
		blockLenght = 321;
		creationTimestamp = System.currentTimeMillis() /1000;
		creationSequenceNumber = 1;
		lifetime = 20000;
		fragmentOffset=-1;
		totalAppDataLenght=-1;
	}


	/* Metodi per la gestione dei Flag del PrimaryBlock 
	 * 
	 * Attenzione: i bit in posizione 6,9-13,19,20 sono riservati per usi futuri (RFC-5050, pg.14).
	 * 
	 */
	

	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che identifica se il bundle corrente è un fragment.
	 *  
	 * @param value true=è un fragment, false=non è un fragment.
	 */
	public void setFlag_isAFragment(boolean value){
		processingControlFlag[0] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che identifica se il bundle corrente è un fragment.
	 * @return true=è un fragment, false=non è un fragment.
	 */
	public boolean getFlag_isAFragment(){
		return processingControlFlag[0];
	}
	/**------------------------------------------------------------------------------------------*/
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che identifica se l'application data trasportata è un Administrative Record.
	 *  
	 * @param value true=è un Administrative Record, false=non è un Administrative Record.
	 */
	public void setFlag_appDataIsAdminRecord(boolean value){
		processingControlFlag[1] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che identifica se l'application data trasportata è un Administrative Record.
	 * @return true=è un Administrative Record, false=non è un Administrative Record.
	 */
	public boolean getFlag_appDataIsAdminRecord(){
		return processingControlFlag[1];
	}
	/**------------------------------------------------------------------------------------------*/

	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica di NON frammentare questo bundle.
	 *  
	 * @param value true=NON frammentare, false=nessun vincolo sulla frammentazione.
	 */
	public void setFlag_dontFragment(boolean value){
		processingControlFlag[2] = value;
	}
	
	/**
	 * Ritorna il flag che specifica di NON frammentare questo bundle.
	 * @return true=NON frammentare, false=nessun vincolo sulla frammentazione.
	 */
	public boolean getFlag_dontFragment(){
		return processingControlFlag[2];
	}
	/**------------------------------------------------------------------------------------------*/

	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di "Custody tranfer".
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_custodyTransfer(boolean value){
		processingControlFlag[3] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di "Custody tranfer".
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_custodyTransfer(){
		return processingControlFlag[3];
	}
	/**------------------------------------------------------------------------------------------*/
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica se il destination EID è un singleton.
	 *  
	 * @param value true=è un singleton, false=non è un singleton.
	 */
	public void setFlag_destEIDisSingleton(boolean value){
		processingControlFlag[4] = value;
	}
	
	/**
	 * Ritorna il flag che specifica se il destination EID è un singleton.
	 * @return true=è un singleton, false=non è un singleton.
	 */
	public boolean getFlag_destEIDisSingleton(){
		return processingControlFlag[4];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di "Ack by application".
	 *  
	 * @param value true=Ack richiesto, false=Ack non richiesto.
	 */
	public void setFlag_AckRequest(boolean value){
		processingControlFlag[5] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di "Ack by application".
	 * @return true=Ack richiesto, false=Ack non richiesto.
	 */
	public boolean getFlag_AckRequest(){
		return processingControlFlag[5];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta i flag che specificano la priorità del bundle (CoS).
	 *  
	 * @param value 0=bulk, 1=normal, 2=expedited.
	 */
	public void setFlag_priority(int value){
		if(value==0){//00
			processingControlFlag[8] = false;
			processingControlFlag[7] = false;
		}
		else if(value==1){//01
			processingControlFlag[8] = false;
			processingControlFlag[7] = true;
		}
		else if(value==2){//10
			processingControlFlag[8] = true;
			processingControlFlag[7] = false;
		}
	}
	
	/**
	 * Ritorna il valore dei flag che specificano la priorità del bundle (CoS).
	 * @return 0=bulk, 1=normal, 2=expedited.
	 */
	public int getFlag_priority(){
		int p8 = processingControlFlag[8] ? 1 : 0;
		int p7 = processingControlFlag[7] ? 1 : 0;
		return (p8*2 + p7); //binary -> int
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di inviare un Report in seguito alla ricezione del bundle.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportBundleReception(boolean value){
		processingControlFlag[14] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di inviare un Report in seguito alla ricezione del bundle.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportBundleReception(){
		return processingControlFlag[14];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di inviare un Report in seguito all'accettazione della custodia.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportCustodyAcceptance(boolean value){
		processingControlFlag[15] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di inviare un Report in seguito all'accettazione della custodia.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportCustodyAcceptance(){
		return processingControlFlag[15];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di inviare un Report in seguito al bundle forwarding.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportBundleForwarding(boolean value){
		processingControlFlag[16] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di inviare un Report in seguito al bundle forwarding.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportBundleForwarding(){
		return processingControlFlag[16];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di inviare un Report in seguito alla consegna del bundle.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportBundleDelivery(boolean value){
		processingControlFlag[17] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di inviare un Report in seguito alla consegna del bundle.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportBundleDelivery(){
		return processingControlFlag[17];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica la richiesta di inviare un Report in seguito alla cancellazione del bundle.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportBundleDeletion(boolean value){
		processingControlFlag[18] = value;
	}
	
	/**
	 * Ritorna il flag che specifica la richiesta di inviare un Report in seguito alla cancellazione del bundle.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportBundleDeletion(){
		return processingControlFlag[18];
	}
	/**------------------------------------------------------------------------------------------*/	
	
	
	
	
	/* AUTO GENERATED SET&GET */
	
	public byte getVersion() {
		return version;
	}


	public void setVersion(byte version) {
		this.version = version;
	}


	public boolean[] getProcessingControlFlag() {
		return processingControlFlag;
	}


	public void setProcessingControlFlag(boolean[] processingControlFlag) {
		this.processingControlFlag = processingControlFlag;
	}


	public long getBlockLenght() {
		return blockLenght;
	}


	public void setBlockLenght(long blockLenght) {
		this.blockLenght = blockLenght;
	}


	public long getCreationTimestamp() {
		return creationTimestamp;
	}


	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}


	public long getCreationSequenceNumber() {
		return creationSequenceNumber;
	}


	public void setCreationSequenceNumber(long creationSequenceNumber) {
		this.creationSequenceNumber = creationSequenceNumber;
	}


	public long getLifetime() {
		return lifetime;
	}


	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
	}


	public long getFragmentOffset() {
		return fragmentOffset;
	}


	public void setFragmentOffset(long fragmentOffset) {
		this.fragmentOffset = fragmentOffset;
	}


	public long getTotalAppDataLenght() {
		return totalAppDataLenght;
	}


	public void setTotalAppDataLenght(long totalAppDataLenght) {
		this.totalAppDataLenght = totalAppDataLenght;
	}
}
