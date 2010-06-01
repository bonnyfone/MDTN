package source.mdtn.bundle;

import java.io.Serializable;

public class PayloadBlock implements Serializable {

	private static final long serialVersionUID = 6423850683812810944L;

	/** Numero intero che identifica il tipo di blocco (es. 1 = payload block) */
	private int blockType = 1; //A payload block
	
	/** Array contentente i Block processing control flag. */
	private boolean processingControlFlag[];
	
	/** Lunghezza del blocco (in questo caso, del payload) */
	private long blockLenght;
	
	/** Il payload, ovvero i dati trasportati.*/
	private byte payload[];
	
	/** Costruttore standard di un PayloadBlock */
	public PayloadBlock(){
		processingControlFlag = new boolean[7];
		
		/* codice di test */
		blockLenght =232;
		payload = new byte[2312];
	}
	
}
