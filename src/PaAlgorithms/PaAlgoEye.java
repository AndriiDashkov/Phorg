/**
 * 
 */
package paalgorithms;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import paglobal.PaUtils;

/**
 * @author Andrii Dashkov
 *
 * Algorithms class contains function for red eye correction. Under development at the moment.
 */
public class PaAlgoEye {


	public PaAlgoEye() {
		
	}
	
	/**
	 * Auxiliary function - finds the next file number in the folder pathTo;
	 * All files in this folder must be like '1.jpeg, 2.jpeg, 3.jpeg'
	 * @param pathTo
	 * @return the next number to create new file name which will not overwrite existed one.
	 */
	static private int findLastNumber(String pathTo) {
		
		File folder = new File(pathTo);
		
		if(!folder.exists()) { throw new NullPointerException(); }
		
		File[] listOfFiles = folder.listFiles();
		
		int counter = 0;

		
		if(listOfFiles.length == 0) { return 0;}
		
		//counting of jpeg files
		for (int i = 0; i < listOfFiles.length; i++) {
			
			String fileName = listOfFiles[i].getName();
			
			if (listOfFiles[i].isFile() &&  (fileName.contains("jpeg") || fileName.contains("jpg"))) {
				
				String l = fileName.substring(0, fileName.indexOf("."));
				
				try {
					
					Integer value = Integer.valueOf(l);
					
					if(value > counter) { counter = value; }
				}
				catch(NumberFormatException e) {
					//do nothing
				}
				
			} 
		}
		
		return counter + 1;
	}
	

	/**
	 * <p>Function uses the segmentation of color to change red color in an eyes</p>
	 * @param targetImage - target image to find and fix red eye
	 * @param rec - rectangle in the source image with red eyes
	 */
	public  void clearRedEyeArea(BufferedImage targetImage, Rectangle rec){
		
		if(rec == null || rec.width == 0 || rec.height == 0) {
			writeLog("Can't detect the area for red eye instrument",null, true, false, false) ;
			return;
		}
		
				
		ArrayList<Point> sList = new ArrayList<Point>(); //for all points
		//ArrayList<Point> list1 = new ArrayList<Point>();//for red point of 1st eye
		//ArrayList<Point> list2 = new ArrayList<Point>();//for red point of 2nd eye
		
		int x_center = rec.x + rec.width/2;
		
		int y_center = rec.y + rec.height/2;
		
		int a =  rec.width/2;
		
		int b =  rec.height/2;

		for(int x = rec.x; x < rec.x+rec.width; x++ ) {
			
			for(int y = rec.y; y < rec.y+rec.height; y++ ) {
				
				int rx = Math.abs(x - x_center);
				
				int ry = Math.abs(y - y_center);
				
				double rc = Math.sqrt(rx*rx + ry*ry);
				
				double fi = Math.atan(y/(double)x);
				
				double re = a*b/ Math.sqrt(a*a*Math.sin(fi)*Math.sin(fi) + b*b*Math.cos(fi)*Math.cos(fi));
				
				if(rc > re) { continue; }
				
				Color c = new Color(targetImage.getRGB(x, y));
				
				int red = c.getRed();
				
				int blue = c.getBlue();
				
				int green = c.getGreen();
				
				double[] rgb = {(double)red, (double)green, (double)blue};
				
				double[] hsi = new double[3];
						
				PaAlgoConvert.convertRGBtoHSI(rgb,hsi,false);
				
				double h = hsi[0]*360.0;
				

				//collect all red points; we do it in HSI model because this is more convenient
				if ( ((h  >= 0.0 && h  <= 30.0) || (h >= 245.0 && h <= 360.0)) && hsi[2] > 0.2 && hsi[1] > 0.2) {
						//		if ( ((h  >= 0.0 && h  <= 30.0) || (h >= 245.0 && h <= 360.0)) && hsi[2] > 0.135 && hsi[1] > 0.16) {
							//if ( red  > 152 && blue  < 84 && green < 57) {				
					sList.add(new Point(x,y));		
				}
				
			}
				
		}

		if(sList.isEmpty()) {
			
			writeLog("Can't find red points for red eye operation", null,
					true, false, false );
			return;
		}
		
		for (int i =0;  i < sList.size(); ++i) {
			
			Point p = sList.get(i);
			
			Color c = new Color(targetImage.getRGB(p.x, p.y));
			
			
			Color c1 = new Color(c.getRed()/4, c.getGreen(), c.getBlue());
			
			targetImage.setRGB(p.x, p.y, c1.getRGB());
		
		}
	
	}
	
	//debug function, must not be used in the work version
	public  void saveSubImageToFile(BufferedImage targetImage,Rectangle rec, String pathToSave){
		
		if(rec == null || rec.width == 0 || rec.height == 0) {
			
			writeLog("Can't detect the area for save operation",null, true, false, false) ;
			
			return;
		}
		
		File f = new File(pathToSave);
		
		if(!f.isDirectory() || !f.exists()) {
			
			JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
					
    				"<html>" + getMessagesStrs("folderNotValidFileCropSaveOperation")
    				+ "<br>" + f.getAbsolutePath() + "<br>" + 
    				getMessagesStrs("checkParametersMessage") + "</html>",
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			
			return;
		}

		PaAlgorithms.debugSubImage(targetImage, rec, pathToSave + findLastNumber(pathToSave) + ".jpeg");
	
		return;
		
	}
	

	/**
	 * Deprecated
	 * Segregates point according to two biggest areas; other areas are believed as extra noise and these points  are removed
	 * @param sList - full list of all points which have been selected as red 
	 * @param eye1 - output list - it contains the points for first eye
	 * @param eye2 - output list - it contains the points for second eye
	 */
	@Deprecated
	public void segregatePoints(ArrayList<Point> sList, ArrayList<Point> eye1, ArrayList<Point> eye2) {
		
		ArrayList<ArrayList<Point>> lists = new ArrayList<ArrayList<Point>>();
		
		while(!sList.isEmpty()) {
			
			ArrayList<Point> newList = new ArrayList<Point>();
			
			lists.add(newList);

			Iterator<Point> it = sList.iterator();
			
			while(it.hasNext()) {
				
				Point p = it.next();
				
				if(newList.isEmpty()) { //for first point only
					
					newList.add(new Point(p));
					
					it.remove();
					
					continue;
				}
				
				Iterator<Point> it1 = newList.iterator();
				
				while(it1.hasNext()) {
					
					Point p1 = it1.next();
					
					//here we control if two points are in touch 
					if(p.x >= p1.x-1 && p.x <= p1.x+1 && p.y >= p1.y-1 && p.y <= p1.y+1) {
						
						newList.add(new Point(p));
						
						it.remove();
						
						break;
					}
				}
				
			}
		}
		
		//select two biggest container
		
		ArrayList<Point> lMax1 = lists.get(0);
		
		for(ArrayList<Point> l : lists) {
			
			if(lMax1.size() < l.size()) { lMax1 = l; }
		}

		eye1.clear();
		
		if(lMax1.size() > 4) { //control of size of list1 - too small? then it is possible that it is not an eye at all
			
			eye1.addAll(lMax1);
		}
		
		lists.remove(lMax1);
		
		ArrayList<Point> lMax2 = lists.get(0);
		
		for(ArrayList<Point> l : lists) {
			
			if(lMax2.size() < l.size()) { lMax2 = l; }
		}
		eye2.clear();
		
		if(lMax2.size() > 4) { //control of size of list2 - too small? then it is possible that it is not an eye at all
			
			eye2.addAll(lMax2);
		}
	}
		
	
	/**
	 * <p>The function determines where the point p should be inserted - to nearest neiboughs
	 * It is used in red eye instrument; two lists - two eyes</p>
	 * @param p - point to insert
	 * @param list1 - first list of points
	 * @param list2 - second list of points
	 */
	@SuppressWarnings("unused")
	private  void addPointToList(Point p, ArrayList<Point> list1, ArrayList<Point> list2) {
		
		if(list1.isEmpty()) { list1.add(p); return ;}
		
		for(Point p1: list1){
			
			double r = Math.sqrt((p.x-p1.x)*(p.x-p1.x) +(p.y-p1.y)*(p.y-p1.y));
			
			if(r < 10.0) {
				
				list1.add(p);
				
				return;
			}
		}	
		
		list2.add(p);
	}
	
	public void fillArea(ArrayList<Point> list, Point[] minMax, Point center) {

				Point	pMinX = new Point(list.get(0));
				
				Point	pMaxX = new Point(list.get(0));
				
				Point	pMinY = new Point(list.get(0));
				
				Point	pMaxY = new Point(list.get(0));
				
				for(Point p1: list){
					
					if(pMinX.x > p1.x) { pMinX.x = p1.x; pMinX.y = p1.y; }
					
					if(pMaxX.x < p1.x) { pMaxX.x = p1.x; pMaxX.y = p1.y; }
					
					if(pMinY.y > p1.y) { pMinY.x = p1.x; pMinY.y = p1.y; }
					
					if(pMaxY.y < p1.y) { pMaxY.x = p1.x; pMaxY.y = p1.y; }
				}

				int centerX = (int)((pMaxX.x + pMinX.x)/2.0);
				
				int centerY = (int)((pMaxY.y + pMinY.y)/2.0);
				
				double r1 = pMinX.distance(centerX, centerY);
				
				double r2 = pMinY.distance(centerX, centerY);
				
				double r3 = pMaxX.distance(centerX, centerY);
				
				double r4 = pMaxY.distance(centerX, centerY);


				double maxR = r1;
				
				if(r2 >= r1 && r2 >= r3 && r2 >= r4) { maxR = r2;}
				else
				if(r3 >= r1 && r3 >= r2 && r3 >= r4) { maxR = r3;}
				else
				if(r4 >= r1 && r4 >= r2 && r4 >= r3) { maxR = r4;}
				
				int maxRint = (int)maxR;
				
				Point centerP = new Point(centerX, centerY);
				
				for(int x = centerX - maxRint; x <= centerX + maxRint; ++x) {
					
					for(int y = centerY - maxRint; y <= centerY + maxRint; ++y) {
						
						if(centerP.distance(x, y) < maxR) {
							
							addPointToList(list,x,y);
						}
						
					}
				}
				//return values
			
				minMax[0] = pMinX;
				
				minMax[1] = pMaxX;
				
				minMax[2] = pMinY;
				
				minMax[3] = pMaxY;
				
				center.setLocation(centerX ,centerY);
				
	}
	
	
	@SuppressWarnings("unused")
	private int findPoint(ArrayList<Point> list, int x, int y) {
		
		for(Point p1: list){
			
			if(p1.x == x && p1.y == y) return 1;
		}
		return 0;
	
	}
	@SuppressWarnings("unused")
	private void deletePoint(ArrayList<Point> list, int x, int y) {
		
		Iterator<Point> it = list.iterator();
		
		while(it.hasNext()){
			
			Point p = it.next();
			
			if(p.x == x && p.y == y)  {
				
				it.remove();
				
				return ;
			}
		}
		return;
	}
	
	private void addPointToList(ArrayList<Point> list, int x, int y) {
		
		Iterator<Point> it = list.iterator();
		
		while(it.hasNext()){
			
			Point p = it.next();
			
			if(p.x == x && p.y == y)  {
				
				return ;
			}
		}
		
		list.add(new Point(x,y));
	}
	
	/**
	 * Corects the red color for area (points) which is in the list.It's used for red eye instrument.
	 * @param list - the container with red points ; this container should have the set of points
	 * for single eye; the area of points must be coherent (���������) 
	 * @param targetImage - target image
	 * @return the set of colors which was used to correct the eye - it can be used while next cal of this function for next eye
	 */
	@Deprecated
	public  Color[] correctRedEyeColor(ArrayList<Point> list, Point[] minMax, Point center, Color[] prevEyeColors,
			BufferedImage targetImage) {	
		
		Point pMinX = minMax[0];
		
		Point pMaxX = minMax[1];
		
		Point pMinY = minMax[2];
		
		Point pMaxY = minMax[3];
		
		//glare of the pupil  finding ...
		ArrayList<Point> blickList = new ArrayList<Point>();
		
		for(int x = pMinX.x; x <= pMaxX.x; ++x) {
			
			for(int y = pMinY.y; y <= pMaxY.y; ++y){
				
				Color c = new Color(targetImage.getRGB(x, y));
				
				if(c.getRed() > 240 && c.getGreen() > 160 && c.getBlue() > 200){ //shift towards white color
					
					blickList.add(new Point(x,y));	
				}
			}
		}
		
		//glare of the pupil has not been used - paint it manually
		if(blickList.isEmpty() || blickList.size() < 5) {
			
			blickList.add(center);
			
			blickList.add(new Point(center.x,center.y-1));
			
			blickList.add(new Point(center.x,center.y+1));
			
			blickList.add(new Point(center.x-1,center.y));
			
			blickList.add(new Point(center.x+1,center.y));
		}
		
		Color almostBlack = new Color(80,80,80);
		
		for(Point p1: list){
			
			targetImage.setRGB(p1.x, p1.y, almostBlack.getRGB());
		}
		
		for(Point p1: blickList){
			
			targetImage.setRGB(p1.x, p1.y, Color.WHITE.getRGB());
		}

		//bluring of the boundary of the red area
		
		Color cAv = null;
		
		for(int index = 0 ; index < list.size(); ++index){
			
			Point p = list.get(index);
			
			int prevIndex = index -1;
			
			if(prevIndex < 0 ) continue;
			
			Point prevPoint = list.get(prevIndex);
			
			int nextIndex = index -1;
			
			if(nextIndex >= list.size() ) continue;
			
			Point nextPoint = list.get(nextIndex);
			
			if(p.y != nextPoint.y) {
				
				cAv = getAverageColor(new Color(targetImage.getRGB(p.x, p.y)), new Color(targetImage.getRGB(p.x+1, p.y)));
				
				targetImage.setRGB(p.x, p.y, cAv.getRGB());
				
			}
			if(p.y != prevPoint.y) {
				
				cAv = getAverageColor(new Color(targetImage.getRGB(p.x, p.y)), new Color(targetImage.getRGB(p.x-1, p.y)));
				
				targetImage.setRGB(p.x, p.y, cAv.getRGB());
			}
	
		}
		
		return null;
	
		
	}
	/**
	 * Just returns the more darker color
	 * @param c1 - color 1
	 * @param c2 - color 2
	 * @return the more darker color
	 */
	private  Color getMoreDarkColor(Color c1, Color c2) {
		
		if(c1 == null) return c2;
		
		if(c2 == null) return c1;
		
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
		
		if(hsb1[2] < hsb2[2]) return c1;
		
		else return c2;
		
	}
	/**
	 * Concentrates the more dark colors in c1 array
	 * @param c1 color 1
	 * @param c2 - color 2
	 */
	@SuppressWarnings("unused")
	private  void getMoreDarkColors(Color[] c1, Color[] c2) {
		
		if(c1 == null || (c2 != null && c2.length < c1.length) ) return ;
		
		Color dark = null;
		
		for(int i=0; i < c1.length; ++i) {
			
			Color d = null;
			
			if(c2 == null) {
				
				d =  getMoreDarkColor(c1[i], null);
				
			} else {
				
				d =  getMoreDarkColor(c1[i], c2[i]);
			}
			
			dark =  getMoreDarkColor(d, dark);
		}
		
		float[] hsb = Color.RGBtoHSB(dark.getRed(), dark.getGreen(), dark.getBlue(), null);
		
		for(int i=0; i < c1.length; ++i) {
			
			float d = hsb[2] * ((100 - PaAlgoUtils.getRandomValue(20))/100.0f) ;
			
			if(d > 1.0) d = 1.0f;
			
			c1[i] = new Color(Color.HSBtoRGB(hsb[0],hsb[1], d));
		}
	} 
	
	/**
	 * Gets average color between c1 and c2
	 * @param c1 - color 1
	 * @param c2 - color 2
	 * @return
	 */
	private  Color getAverageColor(Color c1, Color c2) {
	
		return  new Color((c2.getRed()+c1.getRed())/2,
				(c2.getGreen()+c1.getGreen())/2,
				(c2.getBlue()+c1.getBlue())/2);
		
	}
	
	/**
	 * FOR DEBUG ONLY!!!!
	 * 
	 * @param list1 - list of point, can be null
	 * @param list2 - list of point, can be null
	 * @param rec - rec to draw
	 * @param nameFilePath - file name where to draw
	 */
	@SuppressWarnings("unused")
	private void debugImage(ArrayList<Point> list1, ArrayList<Point> list2, Rectangle rec, String nameFilePath ) {
		
		BufferedImage debugImage2 = new BufferedImage(rec.width,rec.width, BufferedImage.TYPE_INT_RGB);
		
		for(int x = 0; x < rec.width; x++ ) {
			
			for(int y = 0; y < rec.height; y++ ) {
				
				debugImage2.setRGB(x,y,Color.WHITE.getRGB());
			}
		}
		
		if(list1 != null) {
			
			for(Point p : list1) {
				
				debugImage2.setRGB(Math.abs(rec.x - p.x),Math.abs(rec.y - p.y),Color.RED.getRGB());
			}
		}
		if(list2 != null) {
			
			for(Point p : list2) {
				
				debugImage2.setRGB(Math.abs(rec.x - p.x),Math.abs(rec.y - p.y),Color.RED.getRGB());
			}
		}

		
	
		File outputfile = new File(nameFilePath);
		
		try {
			
			ImageIO.write(debugImage2, "jpg", outputfile);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
