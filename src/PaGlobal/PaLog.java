/**
 * 
 */
package PaGlobal;

import static PaGlobal.PaUtils.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


/**
 * <p>Log class</p>
 * @author avd
 *
 */
public class PaLog {
	
	
	private static PaLog instance = null;
	
	private static String filePath = new String();
	
	private static boolean m_logIsEnabled = false;
	
	
	private PaLog() 
	{	
		filePath = PaUtils.get().getLogFullPath();
	};
	
	public static PaLog get() {
		
		if (instance == null) {
			
			instance = new PaLog();
			
			logFileSizeControl();
				
		}
		return instance;
	}
	
	private static void logFileSizeControl()
	{
		File f = new File(filePath);
		
		double megabytes = PaUtils.get().getSettings().getMaxLogFileLength();
		
		double bytes = megabytes*1024*1024;
		
		if(f.exists() &&  f.length() > bytes ){
			
			f.delete();
		}
		
		m_logIsEnabled = PaUtils.get().getSettings().isLogEnabled();

		if(m_logIsEnabled) {
			try {
				
				Calendar cal = Calendar.getInstance();
				
		    	cal.getTime().toString();
		    	
				BufferedWriter out = new BufferedWriter(new FileWriter(filePath,true));
				
				out.write("START APP: "+ cal.getTime().toString());
				
				out.newLine();
				
				out.flush();
				
				out.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static void writeLogOnly(String s, Exception exp ) {
		
		writeLog( s, exp, true, false,false );
	
	}
	
	public static void writeInfoOnly(String s ) {
		
		writeLog( s, null, false, true,false );
	
	}
	/**
	 * Writes info to the log
	 * @param s - message to show
	 * @param exp - exception which about info maust be shown (can be null)
	 * @param logFlag - true if we want to save in the log file
	 * @param infoFlag - true if we want to show info in GUI
	 * @param printExcTrace - tru if we want to print stack trace
	 */
	public static void writeLog(String s, Exception exp, boolean logFlag, 
			boolean infoFlag, boolean printExcTrace ) 
	{
		
		if ( infoFlag ) {
			
			PaUtils.get().getMainLabel().setText(s);
		}
		
		Calendar cal = Calendar.getInstance();
		
		s = cal.getTime().toString() + " : "+ s;
		
		if ( m_logIsEnabled  && logFlag ) {
			
			try {
				
				BufferedWriter out = new BufferedWriter(new FileWriter(filePath,true));
				
				if ( exp != null) {
					
					String sExp = new String();
					
					StackTraceElement[] elms = exp.getStackTrace();
					
					for(StackTraceElement elm: elms) {
						
						sExp = sExp + elm.getFileName()+" : "+ elm.getClassName()+" in method : "+elm.getMethodName()+", line " + elm.getLineNumber()+NEXT_ROW;
					}
					
					System.err.println(sExp);
					
					out.write(sExp);
					
					out.newLine();
					
				}
				
				System.err.println(s);
				
				out.write(s);
				
				out.newLine();
				
				out.flush();
				
				out.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			//writing to log file
		}
		
		if ( printExcTrace && exp != null ) {
			
			System.err.println(exp);
			
			System.out.println(exp);
			
			exp.printStackTrace();
			
			System.err.println(s);
		} 
		
	}

}
