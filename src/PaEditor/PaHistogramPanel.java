package paeditor;

import static paglobal.PaUtils.getGuiStrs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import paalgorithms.PaAlgorithms;
import paglobal.PaGuiTools;
import paglobal.PaUtils;


/**
 * <p>This class can create and draw histogram for the image. Also there is an option
 * to show some base exif data about the image. Histogram can be shown in two variants -
 * with exif data and without exif data. </p>
 * @author Andrii Dashkov
 *
 */
public class PaHistogramPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final private int XMAX = 256;
	
	private final int xGap =  0; 
	
	private final int yGap = 10;
	
	private PairXY[] m_dataRed;
	
	private PairXY[] m_dataGreen;
	
	private PairXY[] m_dataBlue;
	
	GraphPanel m_graphPanel;
	
	JPanel m_infoPanel;
	
	PaStatInfoPanel m_statPanel;
	
	JComboBox<String> m_comboCh;
	
	byte m_currentMask;
	
	final byte REDMASK = 1;
	
	final byte GREENMASK = 2;
	
	final byte BLUEMASK = 8;
	
	final byte ALPHAMASK = 16;
	
	
	public enum VARIANTS {FIRST, SECOND};
	
	JPanel m_panelExifTable;
	
	JPanel m_panelExifLabel;
	
	public PaHistogramPanel(BufferedImage srcImg, String fullPath, VARIANTS v) {
		
		this();
		
		createUI(v);

		setNewData(srcImg);
	
	}
	
	private PaHistogramPanel() {
		
		initEmptyData();
	}
	/**
	 * <p>Init and clears empty data</p>
	 */
	private void initEmptyData() {
		
		m_dataRed = new PairXY[XMAX];
		
		m_dataGreen = new PairXY[XMAX];
		
		m_dataBlue = new PairXY[XMAX];
		
		for(int i=0; i < XMAX; ++i) {
			
			m_dataRed[i] = new PairXY(i,0.0f);
			
			m_dataGreen[i] = new PairXY(i,0.0f);
			
			m_dataBlue[i] = new PairXY(i,0.0f);
			
		}
		
	}
	/**
	 * Sets metadata (exif) for info panel
	 * @param fullPathToImage
	 * @param width
	 * @param height
	 */
	public void setMetaDataInfo(String fullPathToImage, int width, int height){
		
		if(m_infoPanel instanceof PaExifDataPanel) {
			
			((PaExifDataPanel)m_infoPanel).setMetaDataInfo(fullPathToImage, width, height);
		}
	}
	/**
	 * Sets the info about size in info panel
	 * @param width
	 * @param height
	 */
	public void setImageSizeInfo(int width, int height) {
		
		if(m_infoPanel instanceof PaExifDataPanel) {
			
			((PaExifDataPanel)m_infoPanel).setImageSize(width, height);
		}
	}
	
	
	/**
	 * 
	 * @param v - determines what variant of UI will be loaded - with or without exif data
	 * <p>Variant without exif data (SECOND) is used for histogram in preview area</p>
	 */
	public PaHistogramPanel( VARIANTS v) {
		
		this();
		
		createUI(v);
		
		ActionListener actionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) { setChanelInfo();  }
		};
			
		m_comboCh.addActionListener(actionListener);
	}
	/**
	 * 	<p>Creates histogram according to image </p>
	 * @param srcImg - image for histogram creation
	 */
	public void setNewData(BufferedImage srcImg) {
		
		
		if ( srcImg != null ) {
			
			int wMax = srcImg.getWidth();   int hMax =  srcImg.getHeight();
			
			initEmptyData();
	
			for ( int i = 0; i < wMax; i++) {
				
				for ( int j = 0 ; j < hMax; j++) {
					
					Color c1 = new Color(srcImg.getRGB(i, j));
				
					m_dataRed[c1.getRed()].x = c1.getRed();
					
					m_dataRed[c1.getRed()].y += 1.0f;
					
				
					m_dataGreen[c1.getGreen()].x = c1.getGreen();
					
					m_dataGreen[c1.getGreen()].y += 1.0f;
					
				
					m_dataBlue[c1.getBlue()].x = c1.getBlue();
					
					m_dataBlue[c1.getBlue()].y += 1.0f;
				}
			}
			
			
		}
		if(m_statPanel != null) {
			
			PaAlgorithms al = new PaAlgorithms();
			
			m_statPanel.setData(al.getAverageBrightness(srcImg));
			
			m_statPanel.refreshView();
		}
		
		
		m_graphPanel.repaint();
		
	}
	
	/**
	 * <p>Refreshes color statistic data </p>
	 * @param srcImg - source image
	 */
	public void refreshStatData(BufferedImage srcImg) {
		
		if(m_statPanel != null) {
			
			PaAlgorithms al = new PaAlgorithms();
			
			m_statPanel.setData(al.getAverageBrightness(srcImg));
			
			m_statPanel.refreshView();
		}
	}

	
	/**
	 *  Creates and makes layouts for all components  
	 * @param v - gui type ; first - the exif table is shown; second - the exif table is not shown (for Preview tabs)
	 * 
	 */
	private void createUI(VARIANTS v) {
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		
		m_graphPanel = new GraphPanel();
		
		m_infoPanel = new PaExifDataPanel();
		
		m_statPanel = new PaStatInfoPanel();
		
		m_panelExifLabel = PaGuiTools.createHorizontalPanel();
		
		m_panelExifLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		m_panelExifLabel.add(new JLabel(getGuiStrs("exifDataInstLabel")));
		
		m_panelExifLabel.add(Box.createHorizontalGlue());

		JPanel panelLabel2 = PaGuiTools.createHorizontalPanel();
		
		panelLabel2.add(new JLabel(getGuiStrs("colorInfoInstLabel")));
		
		panelLabel2.add(Box.createHorizontalGlue());
		
		m_panelExifTable = PaGuiTools.createVerticalPanel();
		
		m_panelExifTable.add(m_panelExifLabel);
		
		JPanel panelExif = PaGuiTools.createHorizontalPanel();
		
		panelExif.add(m_infoPanel);  panelExif.add(Box.createHorizontalGlue());
		
		m_panelExifTable.add(panelExif);
		
		m_panelExifTable.add(Box.createVerticalGlue());
		
		JPanel panelStatTable = PaGuiTools.createVerticalPanel();
		
		panelStatTable.add(panelLabel2);
		
		JPanel panelS = PaGuiTools.createHorizontalPanel();
		
		panelS.add(m_statPanel);  panelS.add(Box.createHorizontalGlue());
		
		panelStatTable.add(panelS);
		
		panelStatTable.add(Box.createVerticalGlue());
		
		JPanel panelIn = PaGuiTools.createHorizontalPanel();
		
		JScrollPane scrollPanel = new JScrollPane();
			
		//for second variant of istogram panel (which is used for Preview tab) we hide exif data table
		if(v == VARIANTS.FIRST) {
			
			panelIn.add(m_panelExifTable);
			
			panelIn.add(Box.createHorizontalStrut(PaUtils.VERT_STRUT));
		}
		
	
		panelIn.add(panelStatTable);
		
		panelIn.add(Box.createHorizontalGlue());
		
		panelIn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		scrollPanel.setViewportView(panelIn);
		
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0, PaUtils.VERT_STRUT,0,0));
		
		
		JPanel panelGr = PaGuiTools.createVerticalPanel();
		
		JPanel panelCombo = PaGuiTools.createHorizontalPanel();
		
		JLabel comboLabel = new JLabel(getGuiStrs("channelLabelName")+ " : ");
		
		String[] items = {
				
				 getGuiStrs("allHistogramComboName"),
				 
				 getGuiStrs("redHistogramComboName"),
				 
				 getGuiStrs("greenHistogramComboName"),
				 
				 getGuiStrs("blueHistogramComboName"),
				 
				 getGuiStrs("alphaHistogramComboName")
			};
		
		m_comboCh = new JComboBox<String>(items);	
		
		panelCombo.add(comboLabel);
		
		panelCombo.add(m_comboCh);
		
		PaUtils.get().setFixedSizeBehavior(m_comboCh);
		
		m_comboCh.setToolTipText(getGuiStrs("chanelComboInstToolTip"));
		
		panelGr.add(panelCombo);
		
		panelGr.add(Box.createVerticalStrut(PaUtils.HOR_STRUT));
		
		panelGr.add(m_graphPanel);
		
		add(panelGr);
		
		add(scrollPanel);
		
	}
	

	public void setChanelInfo() {
		
		m_currentMask = 0;
		
		switch(m_comboCh.getSelectedIndex() ) {
		
			case 0: {
				
				m_currentMask = REDMASK + GREENMASK + BLUEMASK  + ALPHAMASK;
				
				break;
			}
			case 1: {
				
				m_currentMask = REDMASK;	
				
				break;
				
			}
			case 2: {
				
				m_currentMask = GREENMASK;	
				
				break;
				
			}
			case 3: {
				
				m_currentMask = BLUEMASK;	
				
				break;
				
			}
			case 4: {
				
				m_currentMask = ALPHAMASK;	
				
				break;
				
			}
			
			default: m_currentMask = REDMASK + GREENMASK + BLUEMASK  + ALPHAMASK;
		
		}
				
		m_graphPanel.repaint();
	}
	
	public void setData(PairXY[] dataRed, PairXY[] dataGreen, PairXY[] dataBlue ) {
		
		m_dataRed = dataRed;
		
		m_dataGreen = dataGreen;
		
		m_dataBlue = dataGreen;
		
	}
	/**
	 *  <Draws X and Y axes>
	 * @param g
	 * @param maxX
	 * @param maxY
	 */
	private void drawAxes(Graphics g, int maxX, int maxY ) {
		
		
		Graphics2D g2 = (Graphics2D) g;

		Dimension sZ = calculateBaseSize();
		
		Point endYline = new Point(xGap/2,yGap/2);
		
		Point endXline = new Point(sZ.width+xGap/2,sZ.height-yGap/2);
				
		Point zeroPoint = new Point( xGap/2,-yGap/2+ sZ.height);
			
		Line2D yLine = new Line2D.Double(zeroPoint, endYline);
		
		Line2D xLine = new Line2D.Double(zeroPoint, endXline);
	
			
		g2.draw(xLine);  
		
		g2.draw(yLine);
		
		g2.drawString(Integer.toString(maxY), xGap/2+4, endYline.y + 8);//y caption
		
		g2.drawString(Integer.toString(maxX), endXline.x-25, endXline.y - 5);	//x caption
		
	}
	/**
	 * <p>Draws the graph according to points in data</p>
	 * @param g - painter to draw
	 * @param data - point data to draw
	 * @param col - color to draw
	 */
	private void drawLine(Graphics g, PairXY[] data, Color col ) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		Dimension sZ = calculateBaseSize();
		
		PairXY pMax = getMaxMin(data);
		
		PairXY[] dataNew = translateData(data, pMax.x, pMax.y);
		
		int lenMax = data.length + 2;
		
		int[] xPoints = new int[lenMax];
		
		int[] yPoints = new int[lenMax];
		
		int xPrev = -100;
		
		int i=1;
	
		for(PairXY p: dataNew) {
			
			int xCoord= (int) p.x;
			
			if ( xCoord != xPrev ) {
				
				xPoints[i] = xCoord;
				
				yPoints[i] = (int)p.y;
				
				++i;
				
			}
			
			xPrev = xCoord;
		
		}
		//start point
		xPoints[0] = xPoints[1];
		
		yPoints[0] = -yGap/2+sZ.height;
		
		//end point
		xPoints[i] = xPoints[i-1];
		
		yPoints[i] =  -yGap/2+sZ.height;
		
		Color prevColor = g2.getColor();
		
		g2.setColor(col);
		
		g2.fillPolygon(xPoints, yPoints,i+1); 
		
		g2.setColor(prevColor);
		
	}
	/**
	 * 
	 * @return main drawing area size
	 */
	private Dimension calculateBaseSize() {
				
		Dimension sZ = m_graphPanel.getSize();
				
		sZ.height = sZ.height - yGap;
		
		sZ.width = sZ.width - xGap;
		
		return sZ;
	}
	/**
	 * <p>Translates physical coordinates into java coord system:
	 * the top left corner is a zero point; y coord is increased into down direction</p>
	 * @param data - physical point datda
	 * @param xMax - max value
	 * @param yMax - max value
	 * @return new point data in java paint system
	 */
	private PairXY[] translateData(PairXY[] data, float xMax, float yMax) {
		
		Dimension sZ = calculateBaseSize();
		
		int xW = sZ.width;
		
		int yW = sZ.height;
		
		float kX = ((float)xW )/xMax;
		
		float kY =  ((float) yW )/yMax;
		
		int hmax = yW -yGap/2;
		
		PairXY[] newData = new PairXY[data.length];
		
		int i=0;
		
		for(PairXY p: data) {
			
			newData[i] = new PairXY(p.x*kX+(float )(xGap/2),hmax- p.y*kY);
			
			++i;
		}
		
		return newData;
		
		
	}
	
	private PairXY getMaxMin(PairXY[] data) {
		
		float xMax = 0;
		
		float yMax = 0;
		
		for(PairXY p: data) {
			
			if ( p.x > xMax ) xMax =p.x;
			
			if ( p.y > yMax ) yMax =p.y;
		}
		
		return new PairXY(xMax, yMax);
	}
	
	private PairXY getMaxMin(PairXY[] data1, PairXY[] data2, PairXY[] data3) {
		
		float xMax = 0;
		
		float yMax = 0;
		
		for(PairXY p: data1) {
			
			if ( p.x > xMax ) xMax = p.x;
			
			if ( p.y > yMax ) yMax = p.y;
		}
		for(PairXY p: data2) {
			
			if ( p.x > xMax ) xMax = p.x;
			
			if ( p.y > yMax ) yMax = p.y;
		}
		for(PairXY p: data3) {
			
			if ( p.x > xMax ) xMax = p.x;
			
			if ( p.y > yMax ) yMax = p.y;
		}
		return new PairXY(xMax, yMax);
	}
	
	private class PairXY {
		
		public float x;
		
		public float y;
		
		public PairXY(float x, float y) {
			
			this.x = x;
			
			this.y = y;
			
		}
		
		
	};
	/**
	 * <p>Class for graph panel, it draw the histogram</p>
	 * @author Andrii Dashkov
	 *
	 */
	private class  GraphPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public GraphPanel() {
			
			super();
					
		}
		
		@Override
		public void paint(Graphics g) {
			
			super.paint(g);
			
			if ( m_dataRed != null && m_dataGreen != null &&  m_dataBlue != null  ) {
				
				PairXY pMax = getMaxMin(m_dataRed,m_dataGreen, m_dataBlue);
				
				drawAxes(g, (int) pMax.x, (int) pMax.y );
				
				if ( ( m_currentMask &  REDMASK ) == REDMASK ) drawLine(g, m_dataRed, Color.red);
				
				if ( ( m_currentMask &  GREENMASK ) == GREENMASK ) 	drawLine(g, m_dataGreen,  Color.green);
				
				if ( ( m_currentMask &  BLUEMASK ) == BLUEMASK ) drawLine(g, m_dataBlue,  Color.blue );
			}
			else {
				
				drawAxes(g, 255, 100 );
				
			}
		}
	}
	/**
	 * <p>Resets image on histogram and make the image empty</p>
	 */
	public void resetImage(){
		
		m_dataRed = null;
	
		m_graphPanel.repaint();
		
	}
	
	/**
	 * 
	 * @param flag - sets the exif data table in histogram panel to be visible or not
	 */
	public void setExifDataVisible(boolean flag) {
		
		m_panelExifLabel.setVisible(flag);
		
		m_panelExifTable.setVisible(flag);
	}
}
