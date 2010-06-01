package source.mdtn.bundle;

import java.io.Serializable;

public class Block implements Serializable {

	private static final long serialVersionUID = 1707664001869459467L;

	/** Numero intero che identifica il tipo di blocco (es. 1 = payload block) */
	private int blockType; 
	
	/** Array contentente i Block processing control flag. */
	private boolean processingControlFlag[];
	
	/** Lunghezza del blocco (in questo caso, del payload) */
	private long blockLenght;
	
	/** I dati specifici di questo blocco.*/
	private byte data[];
	
	/** Costruttore standard di un CanonicalBlock */
	public Block(){
		processingControlFlag = new boolean[7];
	}

	
	/* Metodi per la gestione dei Flag del CanonicalBlock  
	 * 
	 * Attenzione: il bit 6 non viene utilizzato.
	 * 
	 */
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica se è necessario replicare tale blocco in ogni fragment (Valido solo se il bundle è frammentato).
	 *  
	 * @param value true=necessario, false=non necessario.
	 */
	public void setFlag_replicateInEveryFragment(boolean value){
		processingControlFlag[0] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che specifica se è necessario replicare tale blocco in ogni fragment (Valido solo se il bundle è frammentato).
	 * @return true=necessario, false=non necessario.
	 */
	public boolean getFlag_replicateInEveryFragment(){
		return processingControlFlag[0];
	}
	/**------------------------------------------------------------------------------------------*/
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica se è richiesto l'invio di un report nel caso il blocco non possa essere processato.
	 *  
	 * @param value true=richiesto, false=non richiesto.
	 */
	public void setFlag_reportIfCantProcess(boolean value){
		processingControlFlag[1] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che specifica se è richiesto l'invio di un report nel caso il blocco non possa essere processato.
	 * @return true=richiesto, false=non richiesto.
	 */
	public boolean getFlag_reportIfCantProcess(){
		return processingControlFlag[1];
	}
	/**------------------------------------------------------------------------------------------*/
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica di cancellare il blocco nel caso questo non possa essere processato.
	 *  
	 * @param value true=cancellare, false=non cancellare.
	 */
	public void setFlag_deleteIfCantProcess(boolean value){
		processingControlFlag[2] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che specifica di cancellare il blocco nel caso questo non possa essere processato.
	 * @return true=cancellare, false=non cancellare.
	 */
	public boolean getFlag_deleteIfCantProcess(){
		return processingControlFlag[2];
	}
	/**------------------------------------------------------------------------------------------*/
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che identifica se il blocco corrente è l'ultimo blocco del bundle.
	 *  
	 * @param value true=è l'ultimo, false=non è l'ultimo.
	 */
	public void setFlag_lastBlock(boolean value){
		processingControlFlag[3] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che identifica se il blocco corrente è l'ultimo blocco del bundle.
	 * @return true=è l'ultimo, false=non è l'ultimo.
	 */
	public boolean getFlag_lastBlock(){
		return processingControlFlag[3];
	}
	/**------------------------------------------------------------------------------------------*/
	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica di scartare il blocco nel caso questo non possa essere processato.
	 *  
	 * @param value true=scartare, false=non scartare.
	 */
	public void setFlag_discardIfCantProcess(boolean value){
		processingControlFlag[4] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che specifica di scartare il blocco nel caso questo non possa essere processato.
	 * @return true=scartare, false=non scartare.
	 */
	public boolean getFlag_discardIfCantProcess(){
		return processingControlFlag[4];
	}
	/**------------------------------------------------------------------------------------------*/
	
	
	/**------------------------------------------------------------------------------------------*/ 
	/**
	 * Imposta il flag che specifica se il blocco ha subito un forwarding senza essere processato.
	 *  
	 * @param value true=forwarded, false=non forwarded.
	 */
	public void setFlag_forwardedWithoutProcess(boolean value){
		processingControlFlag[5] = value;
	}
	
	/**
	 * Ritorna lo stato del flag che specifica se il blocco ha subito un forwarding senza essere processato.
	 * @return true=forwarded, false=non forwarded.
	 */
	public boolean getFlag_forwardedWithoutProcess(){
		return processingControlFlag[5];
	}
	/**------------------------------------------------------------------------------------------*/
	
	
	
	/* AUTO-GEN Set&Get */
	
	public int getBlockType() {
		return blockType;
	}

	public void setBlockType(int blockType) {
		this.blockType = blockType;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	
}