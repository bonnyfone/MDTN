package source.mdtn.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Timing {

	/**
	 * Ritorna l'ora corrente, con dettaglio variabile.
	 * @param detail il dettaglio richiesto nel formato dell'orario:
	 * <br>0 = h
	 * <br>1 = h:m
	 * <br>2 = h:m:s
	 * <br>3 = h:m:s:ms
	 * @param separator Il separatore da usare per separare le varie componenti dell'orario.
	 * @return Una stringa contenente l'ora nel formato specificato.
	 */
	public static String getTime(int detail, String separator){
		String myTime="";
		
		Calendar cal = new GregorianCalendar(); 

		int hour24 = cal.get(Calendar.HOUR_OF_DAY); 
		int min = cal.get(Calendar.MINUTE); 
		int sec = cal.get(Calendar.SECOND); 
		int ms = cal.get(Calendar.MILLISECOND); 
		
		if(detail>=0){
			myTime = hour24+"";
		}
		if(detail>=1){
			myTime = myTime + separator + min;
		}
		if(detail>=2){
			myTime = myTime + separator + sec;
		}
		if(detail>=3){
			myTime = myTime + separator + ms;
		}
		
		return myTime;
	}
	
}
