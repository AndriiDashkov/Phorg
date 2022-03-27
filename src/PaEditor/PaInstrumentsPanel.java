package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import PaCollection.PaImage;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;
import PaROI.PaCutRoiButton;
import PaROI.PaCutRoiInstrument;
import PaROI.PaInstrumentsRoiPanel;
import PaROI.PaPixSaveButton;
import PaROI.PaPixSaveInstrument;
import PaROI.PaRoiChangeButton;
import PaROI.PaRoiChangeInstrument;
import PaROI.PaRoiPanel;


public class PaInstrumentsPanel extends JPanel implements ComponentListener, MouseListener {
	

	private static final long serialVersionUID = 1L;
	
	public PaScrollView m_scrollPane; 
	
	PaHistogramPanel m_histPanel;
	
	protected PaRoiPanel m_roiPanel = null;//is used in the child class - PaInstrumentsRoiPanel
	
	/**
	 * Here the full path to the file
	 */
	String m_fullFilePath = null;
	
	protected String m_current_image_base_name = null;
	
	public BufferedImage m_currentImage =  null;
	
	public BufferedImage m_undoImage = null;
	
	public BufferedImage m_redoImage = null;

	protected BufferedImage m_loadedImage =  null;
	
	protected JLabel n_label = null;

	private int m_curWidth = 600;
	
	private int m_curHeight = 600;
	
	protected PaInstrumentsWindow m_parent = null;
	
	 JPanel area;
	 /**
	  * Factory for instrument creation
	  */
	 protected InstrumentsFactory m_instrumentsFactory = new InstrumentsFactory();
	 
	
	//protected float m_currentScale = 1f;

	protected PaInstrument  m_instrument =null;
	/**
	 * Start point for instrument to draw
	 */
	private Point m_instrumentStartPoint = new Point(-10000,-10000);
		
	public   PaInstrumentsPanel(PaInstrumentsWindow parent, PaImage p, String titleStr, String toolTipTest) {
			
		m_parent = parent;
			
		createUI(titleStr, toolTipTest);
	
		initImage( p);
		
	}
	
	protected void createUI(String titleStr, String toolTipTest)
	{
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		
		labelPanel.setLayout(new BoxLayout(labelPanel , BoxLayout.X_AXIS));
		
		n_label = new JLabel(titleStr);
		
		n_label.setToolTipText(toolTipTest);
		
		labelPanel.add(n_label);	
		
		labelPanel.add(Box.createHorizontalGlue());
		
		m_scrollPane = new PaScrollView();
		
		m_histPanel = new PaHistogramPanel( PaHistogramPanel.VARIANTS.FIRST);
		
		int histH = PaUtils.get().getSettings().getHistoPanelHeight();
		
		//keep the height const for better view
		m_histPanel.setPreferredSize(new Dimension(2000,histH));
		
		m_histPanel.setMaximumSize(new Dimension(2000,histH));
		
		m_histPanel.setMinimumSize(new Dimension(200,histH));
		
		add(labelPanel);
		
		add(m_scrollPane);
		
		add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		add (m_histPanel );

		addComponentListener(this);
		
		m_scrollPane.addMouseMotionListener(new PaMouseMoveListener());
		
		m_scrollPane.addMouseListener(new PaMouseListener());
		
		addMouseListener(this);

		//registration of key events : W, A, S, D keys
		registerKeys_W_A_S_D_Action();
		
	}
	

	/**
	 * Confirms the changes; moves image from preview area to the working area
	 */
	public void confirmChanges() {
		
		if (PaInstrument.isAnyInstrumentWasUsed ) {
			
			m_undoImage = m_currentImage;
			
			m_parent.setUndoButtonEnabled(true);
			
			m_parent.setUndoStartButtonEnabled(true);
			
			m_parent.setSaveButtonEnabled(true);
			
			m_currentImage = PaUtils.deepCopy((BufferedImage) m_parent.getResultViewImage());

			fitViewImage( );
		 	
		 	m_scrollPane.refresh(m_currentImage);
	
		 	resetInstrument();
		 	
			repaint();
			
			m_parent.setPreviewCurrentImageChanged(true);
			
			if(m_histPanel != null) {
				
				m_histPanel.setNewData(m_currentImage);
				
				m_histPanel.setImageSizeInfo(m_currentImage.getWidth(),m_currentImage.getHeight());
				
			}

			PaInstrument.m_previewImageNotConfirmed = false;
			
		}
		else {
			
			String mS = getMessagesStrs("messageNoChangesInInstrImage");
			
			JOptionPane.showMessageDialog(null, mS, 
					getMessagesStrs("messageInfoCaption"),
					JOptionPane.INFORMATION_MESSAGE);
			
			writeLog(mS,null,true,true,true);
			
		}
	

	}
	
	public PaInstrument getInstrument() {
		
		return  m_instrument;
	}
	
	//sets the initial point for the instrument
	public void setStartPointForInstrument() {
	

	}
	
	public Point getStartPointForInstrument() {

	 return m_instrumentStartPoint;

	}
	
	public JLabel getImageLabel() {
		
		return  m_scrollPane.getMainLabel();
	}

	@Override
	public void paint(Graphics g) {
		
	    super.paint(g);
	    
	    Graphics2D g2 = (Graphics2D)g.create();
	        
	    if (  m_instrument != null ) {
	    	
	    	m_instrument.drawShape(g2);
	    }
	    
	    g2.dispose();
	    
	}
	
	/**
	 * Sets the instrument 
	 * @param but - instrument's button
	 */
	public void setInstrument(PaInstrumentTypeEnum type, PaComplexButton but) {
		
		resetInstrument();	
		
		setStartPointForInstrument();

		m_instrument = m_instrumentsFactory.getInstrument(type, but);
	
		
		setCursor(m_instrument.getCursor());
		
		repaint();
		
	}
	/**
	 * Sets the instrument of color balance 
	 * @param but - instrument's button
	 */
	public void setInstrument(PaInstrument i) {
		
		resetInstrument();
			
		m_instrument = i;
		
		setCursor(i.getCursor());
		
		repaint();
		
	}
	
	/**
	 * Resets the current instrument and repaints the image in the work area
	 */
	public void resetInstrument() {
		
		 if (  m_instrument != null ) { 

			 setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			 
			 m_instrument.reset();
			 
			 m_instrument  = null;
			 
			 repaint();
		 }	 
	}
	/**
	 * 
	 * @param p -image object which image should be initiated
	 */
	public void initImage( PaImage p) {
		
		 m_curWidth = 600; 
		 
		 m_curHeight = 600;
		 
		try {
			
			m_fullFilePath = p.getFullPath();
			
			m_current_image_base_name = PaUtils.getFileNameFromString(m_fullFilePath);
			
			File file = new File(p.getFullPath());
			
			m_currentImage = ImageIO.read(file);
			
			//the situation can be when there is no IOException, but BufferedImage is corrupted
			//(for example metadata is wrong -  it is == null)
			if(m_currentImage == null) { throw(new IOException());}
			
			m_loadedImage = PaUtils.deepCopy(m_currentImage);
			
			
			File outputfile = new File("D:/loaded_image_0.jpg");
			
			ImageIO.write(m_loadedImage, "jpg", outputfile);
	 
		} catch (IOException e) {
			
			writeLog("IOException :"+NEXT_ROW,e, true, false, true) ;
			
			 m_currentImage = new BufferedImage( m_curWidth,  m_curHeight , BufferedImage.TYPE_INT_RGB);

			 m_loadedImage =   new BufferedImage( m_curWidth, m_curHeight , BufferedImage.TYPE_INT_RGB);
		}
		
		if(m_histPanel != null) {
			
			m_histPanel.setNewData(m_currentImage);
			
			m_histPanel.setMetaDataInfo(p.getFullPath(),m_currentImage.getWidth(),m_currentImage.getHeight() );
		}
	}
	/**
	 * Sets current image
	 * @param img
	 */
	public void setCurrentImage(Image img) {
		
		m_currentImage =  (BufferedImage) img;
		
	}
	/**
	 * 
	 * @return the current image
	 */
	public Image getCurrentImage() {
		
		return m_currentImage;
	}

	public void fitViewImage( ) {
				
		m_scrollPane.init(m_currentImage);
	
	}

		 
	
	public void setNewViewSubImage(Rectangle rec) {
		
		m_scrollPane.setSubImage(rec);
		
		repaint();
	}
	

	/**
	 * Zoom in operation
	 */
	public void zoomIn( ) {
		
		resetInstrument();
		
		m_scrollPane.increaseViewImage();
		
		repaint();
	
	}
	/**
	 * Zoom out operation
	 */
	public void zoomOut( ) {
		
		resetInstrument();
		
		m_scrollPane.decreaseViewImage();
		
		repaint();
	
	}
	
	/**
	 * Sets a new result image in preview area
	 * @param img - new result image to set 
	 */
	protected void setResultView(Image img) {
		
		if ( img != null ) {
			
			m_parent.setResultView(img, m_instrument.getName());	
			
		}
		else {
			
			writeLog("Instruments window: can't set the result view image: null", null,
					true, false, false );
		}
		
	}
	
	public float getXScale() {
		
		return (float) m_currentImage.getWidth() / (float) m_scrollPane.getViewImage().getWidth();


	}
	
	public float getYScale() {
		
		return (float) m_currentImage.getHeight() / (float) m_scrollPane.getViewImage().getHeight();
	}
	
	
	public void undoAction() {
		if (  m_undoImage != null ) {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			try {
				 m_redoImage =  m_currentImage;
				 
				 m_parent.setRedoButtonEnabled(true);
				 
				 m_currentImage =  m_undoImage;
				
				 m_undoImage = null;
				 
				 fitViewImage( );
				 
				 repaint();
				 
				 m_parent.setNewResultView( m_currentImage,null);
				 
				 m_parent.setUndoButtonEnabled(false);
				 
				 m_histPanel.setNewData(m_currentImage);
				 
				 m_histPanel.setImageSizeInfo(m_currentImage.getWidth(),m_currentImage.getHeight());
				 
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				writeLog("Instruments window: undo operation finished.", null,
						true, false, false );
				
			}
			 
		}
		
	}
	/**
	 * Redo operation
	 */
	public void redoAction() {
		
		if (  m_redoImage != null ) {
			
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			try {
			
				 m_undoImage =  m_currentImage;
				 
				 m_parent.setUndoButtonEnabled(true);
				 
				 m_currentImage =  m_redoImage;
				
				 m_redoImage = null;
				 
				 fitViewImage( );
				 
				 repaint();
				 
				 m_parent.setNewResultView( m_currentImage,null);
				 
				 m_parent.setRedoButtonEnabled(false);
				 
				 m_histPanel.setNewData(m_currentImage);
				 
				 m_histPanel.setImageSizeInfo(m_currentImage.getWidth(),m_currentImage.getHeight());
				 
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				writeLog("Instruments window: redo operation finished.", null,
						true, false, false );
				
			}
		}

		
	}
	/**
	 * Does the undo to the very first initial image
	 */
	public void undoToStartAction() {
		
			m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
			try {
		
				 m_redoImage =  m_currentImage;
				 
				 m_parent.setRedoButtonEnabled(true);
				 
				 m_currentImage = PaUtils.deepCopy(m_loadedImage);
				
				 m_undoImage = null;
				 
				 m_parent.setUndoButtonEnabled(false);
				 
				 fitViewImage( );
				 
				 repaint();
				 
				 m_parent.setNewResultView(m_currentImage,null);
				 
				 m_parent.setUndoStartButtonEnabled(false);
				 
				 m_histPanel.setNewData(m_currentImage);
				 
				 m_histPanel.setImageSizeInfo(m_currentImage.getWidth(),m_currentImage.getHeight());
			 
			}
			finally {
				
				m_parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				writeLog("Instruments window: undo to start operation finished.", null,
						true, false, false );
			}		
	}
	

	
	public class PaMouseMoveListener implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
		
			if (  m_instrument != null ) {
				
				if ( m_instrument.filterMouseEvent(e) ) {
					
					if(m_instrument.isRepaintWhileMouseDrag()) {
						
						Image img = m_instrument.getResultView(e,getXScale(), getYScale(), m_currentImage);
						
						if ( img != null) {
							
							setResultView(img);
							
						}
					}
					
					repaint();
				 
				}
			}
		}
		
	
		@Override
		public void mouseMoved(MouseEvent e) {
		
			if (  m_instrument != null ) {
				
				 if ( m_instrument.filterMouseEvent(e) ) repaint();
			}
		}	
		
	}
	
	
	public class PaMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if (  m_instrument != null ) {
				
				if (  m_instrument.filterMouseEvent(e) )  {
					
					Image img = m_instrument.getResultView(e,getXScale(), getYScale(), m_currentImage);
					
					if ( img != null ) {
						
						setResultView(img);
					}
					
					repaint();
				}
								
			}			
		}
		

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent e) {
			
			if (  m_instrument != null ) {
				if ( m_instrument.filterMouseEvent(e) ) repaint();
			}
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			if (  m_instrument != null ) {
				
								
				Image img = m_instrument.getResultView(e,getXScale(), getYScale(), m_currentImage);
				
				//save flag in advance, because the instrument can be cleared
				boolean nextImageFlag = m_instrument.isAutoSwitchNextImageActivated();
				
				boolean keepActivatedFlag = m_instrument.isKeepActivated();
				
				PaComplexButton instButton = m_instrument.getInstrumentButton();
				
				if ( img != null ) {
					
					setResultView(img);
		
				}
					
				//the instrument can be reset in the call setResultView(img);
				if( m_instrument != null) {
					
					m_instrument.clearOperation();
					
				}
				
				if (nextImageFlag) {
					
					m_parent.loadNextImage( );
				}
				
				if (keepActivatedFlag && null != instButton) {
					
					instButton.clickMainButton();
				}
			
			}
		}
	
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {

	}


	@Override
	public void componentMoved(ComponentEvent e) {

	}


	@Override
	public void componentResized(ComponentEvent e) {

	}


	@Override
	public void componentShown(ComponentEvent e) {

	}



	@Override
	public void mouseClicked(MouseEvent arg0) {

	}


	@Override
	public void mouseEntered(MouseEvent arg0) {

	}


	@Override
	public void mouseExited(MouseEvent e) {

		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

	/**
	 * 
	 * @return the size without scrollbars
	 */
	Dimension getInnerSize(){
		
		Dimension d = m_scrollPane.getSize();
		
		if(m_scrollPane.getVerticalScrollBar().isVisible()) {
			
			d.width -= m_scrollPane.getVerticalScrollBar().getWidth();
		}
		if(m_scrollPane.getHorizontalScrollBar().isVisible()) {
			
			d.height -= m_scrollPane.getHorizontalScrollBar().getHeight();
		}
		return d;
	}
	
	/**
	 * 
	 * @return size of vsisible view in view port; if ths size of image view  > viewport then the sizes of viewport are returned
	 */
	Dimension getViewVisibleSize() {
		
		Dimension viewportSize =  m_scrollPane.getViewport().getSize();
		
		Dimension viewSize =  m_scrollPane.getViewSize();
		
		if(viewSize.width > viewportSize.width ) {
			
			viewSize.width = viewportSize.width;
			
		}
		if(viewSize.height > viewportSize.height ) {
			
			viewSize.height = viewportSize.height;
			
		}
		
		return viewSize;
		
	}
	
	 /**
	  * Visible rectangle on the screen can be restricted by image itself and viewport boundaries if the image view is
	  * less then viewport
	  * @return the visible image rectangle on the screen in the screen coordinates
	  */
	public Rectangle getViewVisibleRectOnScreen() {
		
		return m_scrollPane.getViewVisibleRectOnScreen();
		
	}
	/**
	 * Refreshes the histogram view
	 */
	public void refreshHistogram() {
		
		m_histPanel.setChanelInfo();
		
		m_histPanel.setImageSizeInfo(m_currentImage.getWidth(), m_currentImage.getHeight());
	}
	
	/**
	 * 
	 * @param rec - rectangle in the instruments coordinates (or scroll viewport coordinates)
	 * @param baseWindowControl - if true the control of going out of sizes will be performed
	 * @return the rect in the image or base window coordinates
	 */
	public Rectangle convertFromInstrumentRectToImageRect(Rectangle rec, boolean baseWindowControl) {
		
		Rectangle recNew = new  Rectangle(m_scrollPane.getViewport().toViewCoordinates(rec.getLocation()),
				m_scrollPane.getViewport().toViewCoordinates(rec.getSize()));
		
		return m_scrollPane.convertViewRectToBaseWindow(recNew, false, baseWindowControl);
	
	}
	
	
	/**
	 * 
	 * @param rec - rectangle in the processed image coordinates and scale (real ROI size and position)
	 * @return the rect in the scale and position relatively to view image on the screen, 
	 * and shifted relatively to the position of the scroll view inside PaInstrumentPanel, ready to be painted on the screen
	 */
	public Rectangle convertRectFromImageToVisibleRect(Rectangle rec) 
	{
	
		//transform the rectangle to scale and position relative to the current view image inside scroll panel
		Rectangle rc = m_scrollPane.convertRectFromBaseImageToViewImage(rec);
		
		//transformation to the inside position relative in PaInstrumentPanel, after that the rectangle is ready to be painted 
		Point p = SwingUtilities.convertPoint(m_scrollPane.getViewport(),rc.x, rc.y, this);
		
		rc.x += p.x;
		
		rc.y += p.y;
		
		return  rc;
	
	}
	
	
	
	/**
	 * 
	 * @return full path of the image file which has been loaded to the editor
	 */
	public String getFullPath() { return m_fullFilePath; }
	
	/**
	 * 
	 * @return the short name of the image file which has been loaded to the editor
	 */
	public String getName() { return Paths.get(m_fullFilePath).getFileName().toString(); }
	
	/**
	 * Sets the new info about size in the info panel
	 * @param width
	 * @param height
	 */
	public void setImageSizeInfo(int width, int height) {
		
		m_histPanel.setImageSizeInfo(width,height);
	}
	
	/**
	 * Gets main instruments window
	 * @return the main instruments window
	 */
	public PaInstrumentsWindow getWindow() { return m_parent; }
	
	
	/**
	 * Factory method pattern for creation instruments
	 * @author avd
	 *
	 */
	public class InstrumentsFactory {
			
		public PaInstrument getInstrument(PaInstrumentTypeEnum type, PaComplexButton but ) {
			
			switch(type) {
			
				case ColorBalance : {
					
					return  new PaColorBalanceInstrument( PaInstrumentsPanel.this,(PaColorBalanceButton)but);
				}
				case Crop : {
					return  new PaCropInstrument(PaInstrumentsPanel.this,m_scrollPane.getViewport().getExtentSize(),but);
				}
				case ZoomRect : {
					
					return  new PaZoomRectInstrument( PaInstrumentsPanel.this,but);
				}
				case RedEye : {
					
					return  new PaRedEInstrument(PaInstrumentsPanel.this,but);
				}
				case Blur : {
					
					return   new PaBlurInstrument(PaInstrumentsPanel.this,(PaBlurButton)but);
				}
				case Horizon : {
					
					return  new PaHorizonInstrument(PaInstrumentsPanel.this,but);
				}
				case Sharp : {
					
					return  new PaSharpInstrument(PaInstrumentsPanel.this,but);
				}
				case HSIBalance : {
					
					return  new PaHSIinstrument(PaInstrumentsPanel.this,but);
				}
				//case CutSquare : {
				//	return  new PaCutSquareInstrument(PaInstrumentsPanel.this,but);
				//}
				case Binary : {
					
					return  new PaBinaryInstrument(PaInstrumentsPanel.this,(PaBinaryButton)but);
				}
				case CornersEdgesFind : {
					
					return  new PaCornersInstrument(PaInstrumentsPanel.this,(PaCornersButton)but);
				}
				case EROSION_DIL : {
					
					return  new PaErosionInstrument(PaInstrumentsPanel.this,(PaErosionButton)but);
				}
				case ROI_CHANGE : {
					
					return  new PaRoiChangeInstrument((PaInstrumentsRoiPanel) PaInstrumentsPanel.this,(PaRoiChangeButton)but);
					
				}
				case ROI_CUT : {
					
					return  new PaCutRoiInstrument((PaInstrumentsRoiPanel) PaInstrumentsPanel.this,(PaCutRoiButton)but);
					
				}
				case ROI_PIXELS_SAVE : {
					
					return  new PaPixSaveInstrument((PaInstrumentsRoiPanel) PaInstrumentsPanel.this,(PaPixSaveButton)but);
				}
				
				default : { }
			
			}
			return null;
		}
		
	}
	/**
	 * Registration of some key action; some instruments need a key reaction
	 * @author Andrey Dashkov
	 *
	 */
	protected void registerKeys_W_A_S_D_Action(){
		
	        javax.swing.Action refresh_W_key = new AbstractAction() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public void actionPerformed(ActionEvent e) {
					
	            	if (  m_instrument != null ) {
	            		
	    				if (  m_instrument.filterKeyEvent(KeyEvent.VK_W) )  { repaint(); }
	    			}
	            }
	};
	        javax.swing.Action refresh_S_key = new AbstractAction() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public void actionPerformed(ActionEvent e) {
					
	            	if (  m_instrument != null ) {
	            		
	    				if (  m_instrument.filterKeyEvent(KeyEvent.VK_S) )  { repaint(); }
	    			}
	            }
	        };
	        javax.swing.Action refresh_A_key = new AbstractAction() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public void actionPerformed(ActionEvent e) {
					
	            	if (  m_instrument != null ) {
	            		
	    				if (  m_instrument.filterKeyEvent(KeyEvent.VK_A) )  { repaint(); }
	    			}
	            }
	        };
	        javax.swing.Action refresh_D_key = new AbstractAction() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public void actionPerformed(ActionEvent e) {
					
	            	if (  m_instrument != null ) {
	            		
	    				if (  m_instrument.filterKeyEvent(KeyEvent.VK_D) )  { repaint(); }
	    			}
	            }
	        };
	        javax.swing.Action refresh_E_key = new AbstractAction() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public void actionPerformed(ActionEvent e) {
	            	if (  m_instrument != null ) {
	    				if (  m_instrument.filterKeyEvent(KeyEvent.VK_E) )  { repaint(); }
	    			}
	            }
	        };
	        
	        KeyStroke keyStrokeW = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0);
	        
	        KeyStroke keyStrokeS = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
	        
	        KeyStroke keyStrokeA = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
	        
	        KeyStroke keyStrokeD = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0);
	        
	        KeyStroke keyStrokeE = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0);
	        
	        m_scrollPane.getActionMap().put("Action_VK_W", refresh_W_key);
	        
	        m_scrollPane.getActionMap().put("Action_VK_S", refresh_S_key);
	        
	        m_scrollPane.getActionMap().put("Action_VK_A", refresh_A_key);
	        
	        m_scrollPane.getActionMap().put("Action_VK_D", refresh_D_key);
	        
	        m_scrollPane.getActionMap().put("Action_VK_E", refresh_E_key);
	        
	        m_scrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeW, "Action_VK_W");
	        
	        m_scrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeS, "Action_VK_S");
	        
	        m_scrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeA, "Action_VK_A");
	        
	        m_scrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeD, "Action_VK_D");
	        
	        m_scrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeE, "Action_VK_E");
	       
	}
	
	
	public double getCurrentScale() 
	{
		return m_scrollPane.getCurrentScale();
	}
	
	
	public Rectangle getCurrentBaseWindow() 
	{
		return m_scrollPane.getCurrentBaseWindow();
	}
	

	public void setMainLabel(String text)
	{ 
		n_label.setText(text); 
	}
}


 