package source.mdtn.bundle;

/**
 * Classe che rappresenta un PayloadBlock (RFC5050)
 */
public class PayloadBlock extends Block {

	private static final long serialVersionUID = 6423850683812810944L;

	/** Typo di Payload (campo non previsto da RFC5050)*/
	private String type;
	
	/** Costruttore standard di un PayloadBlock */
	public PayloadBlock(){
		super();
		setBlockType(1); //Il payloadBlock si imposta sempre a 1
		type = "UNKNOWN";
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
	
	/**
	 * Metodo che ritorna il tipo di payload.
	 * @return una stringa rappresentante il tipo del payload.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Metodo che imposta il tipo di payload.
	 * @param type una stringa rappresentante il tipo del payload.
	 */
	public void setType(String type) {
		this.type = type;
	}

}
