package source.mdtn.bundle;


public class PayloadBlock extends Block {

	private static final long serialVersionUID = 6423850683812810944L;

	/*
	private int blockType = 1; //A payload block
	private boolean processingControlFlag[];
	private long blockLenght;
	private byte payload[];
	*/
	
	/** Costruttore standard di un PayloadBlock */
	public PayloadBlock(){
		super();
		setBlockType(1); //Il payloadBlock si imposta sempre a 1
		
	}
	
	
	/**
	 * Imposta il payload.
	 * @param data I dati espressi come byte che rappresentano il payload.
	 */
	public void setPayloadData(byte data[]){
		setData(data);
	}
	
	/**
	 * Metodo che ritorna il payload.
	 * @return un array di byte che rappresenta i dati contenuti nel payload.
	 */
	public byte[] getPayloadData(){
		return getData();
	}
	
}
