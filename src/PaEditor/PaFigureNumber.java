
package PaEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import javax.imageio.ImageIO;



/**
 * @author Andrey Dashkov
 * 
 * The class is intended to receive boundaries of the objects and chain codes of the boundaries.
 * The input data for this class is an image with binary data (black/white). Black color is treated as
 * background, white color as an object for receiving its boundary. 
 * The general algorithm:
 * 1)division of the image to a grid with desirable scale of the boundary (usually 3x3, 5x5 pixels);
 * every cell where some pixels (but not all) are equal zero believes to be a boundary element.
 * 2)determine the top start point of the objects as a start point for boundary creation.
 * 3)using BFS for moving along the boundary into clockwise direction.
 * 4)using the resulting shortest path after BFS as a boundary array for decoding into chain code.
 *
 */
public class PaFigureNumber {

	
	final int m_cellSize = 3;
	final int m_cellSize_2 = m_cellSize*m_cellSize;
	
	String m_testFileName = null;

	Test m_test = null;
	
	final int [][] m_chainCode = {{3,2,1}, {4,-1,0}, {5,6,7}};
	
	/**
	 * 
	 */
	public PaFigureNumber(BufferedImage im, String testFolder) {
		
		m_test = new Test(im, testFolder);
	}
	
	public void setTestFileName(String s) {
		
		m_testFileName = s;
	}
	
	private class Pair {
		
		public Pair(int x, int y) {
			
			this.x = x;
			this.y = y;
	
		}
		
		public int x;
		
		public int y;

	}
	
	public void setCounter(int counter) {
		
		m_test.setCounter(counter);
	}
	
	
	private class Test {
		
		boolean m_testFlag = true;
		
		BufferedImage m_testImage = null;
		
		String m_testImageFilePath = "D:\\tempImages\\generated_data\\";
		
		int m_counter = 0;
		
		public void setCounter(int c) {
			m_counter = c;
		}
		
		public Test(BufferedImage im, String testFolder) {
			
			if(im == null || !m_testFlag) { return; }
			
			m_testImageFilePath = testFolder;
			
			m_testImage = deepCopy(im);
		}
		
		public void visualizeCell(int x, int y)
		{
			if(m_testImage == null || !m_testFlag) { return; }

				Color c = Color.GREEN;
				
				for( int i = x*m_cellSize; i < (x*m_cellSize + m_cellSize); ++i) {
					
					for( int j = y*m_cellSize; j < (y*m_cellSize + m_cellSize); ++j) {
					
						m_testImage.setRGB(i,j,c.getRGB());
						
					}
				}
		}
		
		public void saveTestImage(String newName) {
			
			if(m_testImage == null || !m_testFlag) { return; }
			
			
			String name = m_testImageFilePath + m_counter + ".jpg";
		
			
			if(newName != null) {
				
				name = m_testImageFilePath + newName;
				
			}
			
			try {
				
				ImageIO.write(m_testImage, "jpg",new File(name));
				
			} catch (IOException e) {
				
				System.out.println("Can't save the test image : " + name);
			}
		
		}
		
		public void printChainCode(ArrayList<Integer> list) {
			
			if(m_testImage == null || !m_testFlag) { return; }
			
		   for (Integer i : list) {
			   System.out.print(i);
		   }
		   
		   System.out.println("");
		   
		   System.out.println("Size of string representation  = " + list.size());
		}
		   
	}
	

	

	
	private ArrayList<Integer> getAdjacentCells(int v, int[][] integral_data, int w, int h, 
			int initW, int initH) {
		
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		 Pair p = getCellXY(v, w);
		 
			int x = p.x;
			int y = p.y;
			
			//int prevX = x;
			//int prevY = y;
			
			int [][] neib = {{-1, -1, -1},{-1, 0, -1}, {-1, -1, -1}};
			
			//boolean flagFound = false;
			for(int i = 0; i < 3; ++i) {
				
				int globalX = x + i - 1;
				
				for(int j = 0; j < 3; ++j) {
									
					int globalY = y + j - 1;
					//central element (current) and previous cell (forbiden for investigation) is set to -1
					if(j == 1 && i == 1 ) {  continue; }
					//mask can be out of the scaled grid - then the mask element is set to 0 (as if it were an element with background pixels only)
					if((globalY < 0 || globalX < 0) || (globalY >= h || globalX >= w)) { continue; } 
					
					int c = getSumCell(integral_data, initW, initH, globalX, globalY);
					
					if(c < m_cellSize_2 &&  c != 0 ) {
						
						neib[j][i] = 0;
						
						res.add(getCellNumber(globalX, globalY, w)); /*flagFound = true;*/
					}
					
					if(c == m_cellSize_2) {  neib[j][i] = 1; }
									
				}
			}
			//int y_1 = y - 1;
			//int x_1 = x - 1;
			//if(flagFound) {
				if(neib[0][0] == 1 && (neib[1][0] == -1 || neib[0][1] == -1 )) {   res.add(getCellNumber(x + 0 - 1,  y + 0 - 1, w));      }
				
				if(neib[0][1] == 1 && (neib[0][0] == -1 || neib[0][2] == -1 )) {   res.add(getCellNumber(x + 1 - 1,  y + 0 - 1, w));      }
				
				if(neib[0][2] == 1 && (neib[0][1] == -1 || neib[1][2] == -1 )) {   res.add(getCellNumber(x + 2 - 1,  y + 0 - 1, w));      }
				
				if(neib[1][0] == 1 && (neib[0][0] == -1 || neib[2][0] == -1 )) {   res.add(getCellNumber(x + 0 - 1,  y + 1 - 1, w));      }
				
				if(neib[2][0] == 1 && (neib[1][0] == -1 || neib[2][1] == -1 )) {   res.add(getCellNumber(x + 0 - 1,  y + 2 - 1, w));      }
				
				if(neib[2][1] == 1 && (neib[2][0] == -1 || neib[2][2] == -1 )) {   res.add(getCellNumber(x + 1 - 1,  y + 2 - 1, w));      }
				
				if(neib[2][2] == 1 && (neib[2][1] == -1 || neib[1][2] == -1 )) {   res.add(getCellNumber(x + 2 - 1,  y + 2 - 1, w));      }
				
				if(neib[1][2] == 1 && (neib[2][2] == -1 || neib[0][2] == -1 )) {   res.add(getCellNumber(x + 2 - 1,  y + 1 - 1, w));      }
			//}
		
			return res;
		
	}

	
	
	/**
	 * This function tires to find some first cell in order to establish the preffered direction for BFS : clockwise direction
	 * @param v
	 * @param integral_data
	 * @param w
	 * @param h
	 * @param initW
	 * @param initH
	 * @param edgeTo
	 * @param marked
	 * @param initialGlobalX
	 * @param initialGlobalY
	 * @return
	 */
	private int getFirstAdjacentCells(int v, int[][] integral_data, int w, int h, 
			int initW, int initH, int[] edgeTo, boolean[] marked, int initialGlobalX, int initialGlobalY) {
		
		 	Pair p = getCellXY(v, w);
		 
		 	//this parameter determines how far we must be from the start point to guarantee the further BFS to be directed into
		 	//clockwise direction; this parameter is very important; not wise to set it < 2
		 	int distanceThreshold = 2; //means number of cells
		 	
			int x = p.x;
			
			int y = p.y;
			
			int index;
			
			int globalY = y;
			
			int globalX = x + 1;
			
			int c = getSumCell(integral_data, initW, initH, globalX, globalY); 
			
			if(c != 0) {
				
				index = getCellNumber(globalX,globalY,w);
				
				edgeTo[index] = v;
				
				marked[index] = true;
			
				//m_test.visualizeCell(globalX, globalY);
				//m_test.saveTestImage("11.jpg");
				
				if((globalX - initialGlobalX) > distanceThreshold || ( globalY - initialGlobalY) > distanceThreshold) { return index; }
				
				return  getFirstAdjacentCells(index, integral_data, w, h, initW, initH, edgeTo, marked, initialGlobalX, initialGlobalY);
			} 
			
			globalY = y + 1;
			
			globalX = x + 1;
			
			c = getSumCell(integral_data, initW, initH, globalX, globalY); 
			
			if(c != 0) {
				
				index = getCellNumber(globalX,globalY,w);
				
				edgeTo[index] = v;
				
				marked[index] = true;
				//m_test.visualizeCell(globalX, globalY);
				//m_test.saveTestImage("11.jpg");

				if((globalX - initialGlobalX) > distanceThreshold || ( globalY -  initialGlobalY) > distanceThreshold) { return index; }
				
				return  getFirstAdjacentCells(index, integral_data, w, h, initW, initH, edgeTo, marked, initialGlobalX, initialGlobalY);
			}
			
			globalY = y + 1;
			
			globalX = x;
			
			c = getSumCell(integral_data, initW, initH, globalX, globalY); 
			
			if(c != 0) {
				
				index = getCellNumber(globalX,globalY,w);
				
				edgeTo[index] = v;
				
				marked[index] = true;
				//m_test.visualizeCell(globalX, globalY);
				//m_test.saveTestImage("11.jpg");

				if((globalX - initialGlobalX) > distanceThreshold || ( globalY - initialGlobalY ) > distanceThreshold) { return index; }
				
				return  getFirstAdjacentCells(index, integral_data, w, h, initW, initH, edgeTo, marked, initialGlobalX, initialGlobalY);
			} 
			
			
			return -1;
	}
	
	
	/**
	 * 
	 * @param integral_data
	 * @param initW
	 * @param initH
	 * @param globalX - X cell coordinates
	 * @param globalY  - Y cell coordinates
	 * @return
	 */
	private int getSumCell(int[][] integral_data, int initW, int initH, int globalX, int globalY) {
		
		//recalculation into real pixal coordinates
		int pixelGlobalX = globalX*m_cellSize;
		
		int pixelGlobalY = globalY*m_cellSize;
		
		int w =  m_cellSize;
		
		int h =  m_cellSize;
		
		int y_ = pixelGlobalY + w;
		
		int x_ = pixelGlobalX + h;
		
		if(x_ >= initW) {
			
			w = x_ - pixelGlobalX;
		}
		if(y_ >= initH) {
			
			h = y_ - pixelGlobalY;
		}
		
			return PaIntegralImageData.getSum(integral_data, pixelGlobalX, 
				pixelGlobalY, w, h);
	
	}
	/**
	 * Creates chain code of the boundary with using as input date the array after BFS.
	 * @param edgeTo - array of the graph nodes after BFS
	 * @param s - start point of the boundary
	 * @param v - last point of the boundary
	 * @param w - width of cell grid 
	 * @return the chain code according to schema in m_chainCode array
	 */
	private Iterable<Integer> decodeToChainCodes(int[] edgeTo, int s, int v, int w)
	{
		
	
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		Stack<Integer> path = new Stack<Integer>();
		//path.push(s);
		int x1 = v;
		
		for (; x1 != s; x1 = edgeTo[x1]) {
			
			path.push(x1);
		}
		
		path.push(s);
		
		int previous = path.pop();
		
		while(!path.isEmpty()) {
			
			
			int current = path.pop();
		
			Pair cur = getCellXY(current, w);
			
			Pair prev = getCellXY(previous, w);
			
			int y =  cur.y - prev.y  + 1;
			
			int x =  cur.x - prev.x  + 1;
			
			res.add( m_chainCode[y][x] );
			
			m_test.visualizeCell(cur.x, cur.y);
			
			//m_test.saveTestImage(null);
			previous = current;
		}
		
		m_test.saveTestImage(m_testFileName);
		
		
		return res;
	}
	

private int getCellNumber(int x, int y, int w) {
	
	return y*w + x;
}

private Pair getCellXY(int n, int w) {
	
	int y = n / w ;
	int x = n - y * w;
	
	return new Pair(x, y);
}


/**
 * This binary search has been changed to deal with integral data; the main idea is
 * to find the first nonzero pixel.
 * It deals with only one row of integral data ( y is fixed). It works with real pixel coordinates.
 * @param data - integral data of the image
 * @param y - fixed row inside integral data array where the nonzero pixel must be found
 * @param initW - width of the image
 * @param initH - height of the image
 * @param left - left point for binary search
 * @param right - right point for binary search
 * @return the x-coordinate of the first non zero pixel
 */
public int binarySearch(int[][] data, int y,  int initW, int initH, int left, int right) {
	
	if((right - left) < 2) { return left; }
	
	int center = (left + right)/2;
	
	int sumLeft = PaIntegralImageData.getSum(data,  left,  y, center - left, 1);
	
	if(sumLeft != 0) {
		return binarySearch(data, y, initW, initH, left, center);
	}
	else {
		return binarySearch(data, y, initW, initH, center, right);
	}
}
/**
 * This function finds the top boundary point; this point is start point for the process of boundary creation.
 * @param integral_data - integral data of the image
 * @param initW - width of the image
 * @param initH - height of the image
 * @return
 */
public Pair getFirstPoint(int[][] integral_data, int initW, int initH) {

	
	int y = 0;
	
	//finding the first row (y coordinate) which contains non zero pixel
	do {
		int sum = PaIntegralImageData.getSum(integral_data,  0,  0, initW, y + 1);
		if(sum != 0) { break; }
		++y;
	}while(true);
	
	int xPixel = 0;
	
	//now the finding of the first non 0 pixel along the found row (x coordinate)
	xPixel = binarySearch(integral_data, y, initW, initH, 0, initW - 1);
		
	//return in the cells coordinate systems
	return new Pair(xPixel/m_cellSize,y/m_cellSize);
}
/**
 * 
 * @param sIm
 * @return the chain code of the boundary for an image.
 * The only one object must be on the image. This object can represent some overlapped objects,
 * but morphologically the spot on the image must be not separable. 
 */
public ArrayList<Integer> getFigureNumber( BufferedImage sIm ) {
	
	int initH = sIm.getHeight();
	
	int initW = sIm.getWidth();
	
	
	int h = initH/m_cellSize;
	
	int w = initW/m_cellSize;

	
	int w_h = w*h;
	
	boolean[] marked= new boolean[w_h];
	int[] edgeTo = new int[w_h];
	
	int[][] integral_data =  PaIntegralImageData.getIntegralDataFromImage(sIm, 200); //threshold - 200
	

	Pair startP = getFirstPoint(integral_data, initW, initH);
	
	int xStart = startP.x;
	
	int yStart = startP.y;

	//number of start cell
	int sStart = getCellNumber(xStart, yStart, w);
	
	marked[sStart] = true;
	
	//some first point of the boundary are found "manually" in order to garantee cloclwise direction of BFS
	int s = getFirstAdjacentCells(sStart, integral_data, w, h, initW, initH, edgeTo, marked, xStart, yStart);
	
	//marked[s] = true;
	
	int v = -1;
	
	int counter = 0;	

	LinkedList<Integer> q = new LinkedList<Integer>();
	
	q.add(s);

	int lastV = -1;
	
	//BFS on the boundary
	while (!q.isEmpty())
	{
		
		System.out.println(counter);
		
		v = q.pollLast();
		
		Pair p1 = getCellXY(v, w);
		
		System.out.println("v = " + v + "x = " + p1.x + " y = " + p1.y);
		
		ArrayList<Integer> listInt = getAdjacentCells(v, integral_data, w, h, initW, initH);
		
		for (int w1 : listInt)
		{
			if (!marked[w1])
			{

				q.add(w1);
				
				marked[w1] = true;
				
				edgeTo[w1] = v;
				
				lastV = v; //this is the current last node of the graph; 
			}
			
			Pair check = getCellXY(w1, w);
			
			if(/*counter > 20 & */check.x == xStart && check.y == yStart) {
				//edgeTo[w1] = v;
				
				q.clear();
				
				break;
			}
		}
	}

	return (ArrayList<Integer>)decodeToChainCodes(edgeTo, sStart, lastV, w);


}



public int getStatisticalResult(ArrayList<Integer> chainCode) {
	
	m_test.printChainCode(chainCode);
	
	   //766666600000000070
	//int[]  template1 = {7,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,7,0};
	int[]  template1 = {6,6,6,6,7,0,0,0,0};
	
	int rA1 = stringCompare(chainCode, template1);
	
	System.out.println("************************************");
	
	//int[]  template2 = {5,2,2,2,2,2,2,2,1,0,0,0,0,0,0,0,0,0};
	
	int[]  template2 = {2,2,2,2,1,0,0,0,0};
	
	int rA2 = stringCompare(chainCode, template2);
	
	System.out.println("************************************");
	
	if(rA2 > rA1) { rA1 = rA2; }
	
	//int[]  template3 = {1,0,0,0,0,0,0,0,1,2,2,2,2,2,2,2,2,2};
	
	int[]  template3 = {0,0,0,0,1,2,2,2,2};
	
	int lA1 = stringCompare(chainCode, template3);
	
	System.out.println("************************************");
	
	//int[]  template4 = {2,2,2,2,2,2,2,2,3,4,4,4,4,4,4,4,4,4};
	
	int[]  template4 = {2,2,2,2,3,4,4,4,4};
	
	int lA2 = stringCompare(chainCode, template4);
	
	if(lA2 > lA1) { lA1 = lA2; }
//if(cnt2 > cnt1) { return ++cnt2; }
	
	return (rA1 + lA1 + 1);
	
}

public int stringCompare(ArrayList<Integer> s, int[]  template) {
	
	
	if(s.get(s.size() - 1) == -1) { s.remove(s.size() - 1); }
	
	ArrayList<Integer> newList = s;
	
	for(int i = 0; i < template.length/2; ++i) { s.add(s.get(i)); }

	
	//int resCounter = 0;
	
	int parseSize = newList.size() - template.length;
	
	float[] lRes = new float[parseSize]; 
	
	float max  = 0.0001f;
	
	int maxIndex = -1;
	
	for(int i = 0; i < parseSize; ++i) {
		
		int cnt = 0;
		
		System.out.print("Original = ");
		
		for(int j = 0; j < template.length; ++j) {
			
			System.out.print(newList.get(i + j));
			
		}
		System.out.print(" Res = ");
		
		for(int j = 0; j < template.length; ++j) {
			
			int res = newList.get(i + j) - template[j];
			
			if(res == 0) { ++cnt; }
			
			System.out.print(res);
			
		}
		int beta = template.length - cnt;
		
		float R = Float.MAX_VALUE;
		
		if(beta != 0) {
			
			R = cnt/(float)beta;
		}
		
		System.out.println(" - " + R);

		if(R >= 3.0) {
			
			lRes[i] = R;
			
			if(max < R) {
				
				max = R;
				
				maxIndex = i;
			}
		}
		else {
			
			lRes[i] = 0.0f;
		}
	}
	
	int counter = 0;
	
	int limit = template.length*3/4;
	
	while(maxIndex != -1) {
		
		++counter;
		
		for(int i = 0; i < limit; ++i) {
			
			int index = maxIndex - limit/2 + i;
			
			if(index == lRes.length || index < 0) { continue; }
			
			lRes[index ] = 0.0f;
		}

		
		max  = 0.0001f;
		
		maxIndex = -1;
		
		for(int i = 0; i < lRes.length; ++i) {
			
			if(lRes[i] != 0.0 && max < lRes[i]) {
				
				max = lRes[i];
				
				maxIndex = i;
			}
		}
	}
	 return counter; 
	//return resCounter;
	
}

BufferedImage deepCopy(BufferedImage im) {
	
	BufferedImage res = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_RGB);
		
	Graphics g = res.getGraphics();
	
	g.drawImage(im, 0, 0, im.getWidth(), im.getHeight(), null);
	
	
	return res;
}

public static void main(String[] args) {
	
	String sourceImagesPath = "D:\\tempImages\\generated_data\\";
	
	File folder = new File(sourceImagesPath);
	
	if(!folder.exists()) {
		System.out.println("The source folder soesn't exist : " + sourceImagesPath);
		return;
	}
		
	File[] listOfFiles = folder.listFiles();
	
	if(listOfFiles.length == 0) { throw new NullPointerException(); }
	
	//int counter = 0;
	for(File f: listOfFiles) {
		
		if(f.isDirectory()) { continue; }
		
		System.out.println(f.getName());
		
		//testing
		 BufferedImage img = null;
		 try {
				//img = ImageIO.read(new File("D:\\tempImages\\data\\t10.jpg"));
				img = ImageIO.read(f);
					
		 } catch (IOException | IllegalArgumentException e1) {
				
		 }
		 
		 PaFigureNumber fN = new PaFigureNumber(img, "D:\\tempImages\\generated_data\\boundary\\");
		 
		 fN.setTestFileName(f.getName());

		
	}	
}

}
