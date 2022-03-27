
package PaROI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * This is the parser of the ROI file format
 *
 */
public class PaRoiFileParser {

	/**
	 * 
	 */
	public PaRoiFileParser() { }
	

	/**
	 * Saves a special ROI file with list of rectangles which has been marked by instrument.
	 * If the file exists, the adds new rectangles to it.
	 */
	public static void parseRoiFile(String filePath, HashMap<String,ArrayList<PaRectangle>> roi_map, 
			ArrayList<String> current_roi_list_for_types) 
	{
		
		File f = new File(filePath);
		
		if(!f.exists() && f.isDirectory()) {
			
			return;
		}
		
		FileInputStream fstream = null;
		
		BufferedReader br = null;
		
		try {	
	
			fstream = new FileInputStream(filePath);
		
			br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;
			
			//first line
			if((strLine = br.readLine()) != null) {
				
				String rects[] = strLine.trim().split(PaUtils.getRoiDelimeter1()); 
				
				if(rects[0].equals("types")) {
					
					for(int i = 1; i < rects.length; ++i) {
						
						current_roi_list_for_types.add(rects[i]);
					}
					
				}
				
			}

			while ((strLine = br.readLine()) != null)   {
				
				String rects[] = strLine.trim().split(PaUtils.getRoiDelimeter1()); 
				
				String fileName = rects[0];

				ArrayList<PaRectangle> list = new ArrayList<PaRectangle>() ;
				
				for(int i = 1; i < rects.length; ++i) {
					
					String coord[] = rects[i].trim().split(PaUtils.getRoiDelimeter2());
					
					PaRectangle r = new PaRectangle(
							Integer.parseInt(coord[0]),
							Integer.parseInt(coord[1]),
							Integer.parseInt(coord[2]),
							Integer.parseInt(coord[3]) );
					
					//to support old files:
					if(coord.length == 5) {
						
						r.type = coord[4];
						
					}
					else {
						
						if(current_roi_list_for_types != null && current_roi_list_for_types.size() != 0) {
							
							r.type = current_roi_list_for_types.get(0);
							
						}
					}
					
					list.add(r);
				}
				
				roi_map.put(fileName, list);
			}
		
		}
		catch (FileNotFoundException  e) {
			
			e.printStackTrace();
			
		}
		catch (IOException   e) {
			
			e.printStackTrace();
			
		}
		finally {
			
			if (br != null) { 
				
				try {
					
					br.close();
					
				} catch (IOException e) {

					e.printStackTrace();
				} 
			}
        }
		
	}
	
	
	
	public static void saveRoiFile( HashMap<String,ArrayList<PaRectangle>> roi_map, ArrayList<String> current_roi_list_for_types,
			String filePath)
	{
		
		ArrayList<String> resList = new ArrayList<String>();
		
		String types = "types";
		
		for(int i =0; i < current_roi_list_for_types.size(); ++i) {
			
			types = types + PaUtils.getRoiDelimeter1() + current_roi_list_for_types.get(i); 
		}
		
		resList.add(types);
	
		for (Entry<String, ArrayList<PaRectangle>> entry : roi_map.entrySet()) {
			
			String name = entry.getKey();
			
			ArrayList<PaRectangle> list =  entry.getValue();
			
			String dl =  PaUtils.getRoiDelimeter2();
			
			StringBuilder b = new StringBuilder();
			
			b.append(name);
			
			for(int i =0; i < list.size(); ++i) {
				
				PaRectangle rec = list.get(i);
			
				String recStr = PaUtils.getRoiDelimeter1() + rec.x + dl + rec.y + dl + rec.w + dl + rec.h + dl + rec.type;
				
				b.append(recStr);
			}
		    
			resList.add(b.toString());
		}
		
		File fl = new File(filePath);
		
		BufferedWriter writer = null;
		
        try {
        	
        	writer = new BufferedWriter(new FileWriter(fl));
        	
        	for(String s : resList) {
        		
        		writer.write(s);
        		
        		writer.newLine();
        	}
        } catch (Exception e) {
        	
            e.printStackTrace();
            
        } finally {
        	
            try {
            	
                writer.close();
                
            } 
            catch (Exception e) {}
        }
	
	}
	
}
