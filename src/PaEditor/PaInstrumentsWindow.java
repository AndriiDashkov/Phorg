package paeditor;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import pacollection.PaImage;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paexif.PaExifLoader;
import paglobal.PaGuiTools;
import paglobal.PaUtils;
import paglobal.PaUtils.OSType;
import paimage.PaViewPanel;


/**
 * The main window for instruments manipulations
 * @author Andrii Dashkov
 *
 */
public class  PaInstrumentsWindow extends JFrame {
	
	{

	}
	
	private static final long serialVersionUID = 1L;
	
	protected PaImage m_imgSource = null;
 
	protected PaViewPanel m_parent = null;
	
	protected PaInstrumentsPanel m_instPanel = null;

	protected PaTabPreview m_instViewPanel = null;
	
	protected BoxLayout lt = null;
	
	protected Dimension m_buttonSize =  new Dimension(40, 35);
	
	/**
	 * Collection of complex buttons; we need it for event disconnection
	 */
	protected ArrayList<PaComplexButton> m_complexButtons = new ArrayList<PaComplexButton> ();
	
	protected PaUndoAction m_undoAct;
	
	protected PaRedoAction m_redoAct;
	
	protected PaUndoToStartAction m_undoToStartAct;
	
	protected PaSaveAction m_saveAction;
	
	protected PaHidePreViewAction m_hidePrevViewAction; 
	
	protected PaConfirmAction m_confirmAction;
	
	protected PaResetPreviewAction m_resetPreviewAction;
	
	protected PaNextImageAction m_nextImageAction;

	protected boolean m_prevCurrentImageChanged = false;
	/**
	 * the flag if the image was save at limit 1 time
	 */
	protected boolean m_imageHasBeenSaved = false;
	/**
	 * Flag to control the changed state of view area
	 */
	protected boolean m_viewWasChanged = false;
	
   //flag to control visibility of preview panel
	protected boolean m_hideFlag = false;
	

	public  PaInstrumentsWindow (JFrame jf, PaImage imgSource, PaViewPanel panel ) {
				
		setTitle (getGuiStrs("instrWindowTitle"));
		
		setIconImage((new ImageIcon(PaUtils.get().getIconsPath() + "painstruments.png")).getImage());
		
		this.m_imgSource = imgSource;
		
		m_prevCurrentImageChanged = false;
		
		m_imageHasBeenSaved = false;
		
		m_viewWasChanged = false;
		
		m_parent = panel;
				
		createUI();

		addWindowListener(new CloseWinListener());	
				
		Dimension d  = PaUtils.get().getSettings().getInstrumentsWinInitSize();
		
		setBounds(150, 150, d.width, d.height);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	
		setBounds(150, 150, d.width, d.height);
		
		pack();
	}
	
	
	public void loadImage( ) 
	{
			
		m_instPanel.fitViewImage( );
		
		m_instPanel.refreshHistogram();
		
		m_instViewPanel.setImage(m_instPanel.getCurrentImage());
		
		m_instPanel.repaint();

	}
	
	public void loadImage( PaImage imgSource) 
	{
		if(imgSource == null) { return; }
		
		this.m_imgSource = imgSource;
		
		m_instPanel.initImage(imgSource);
		
		loadImage( );

	}
	
	public void loadNextImage( ) 
	{
		m_parent.loadNextImage(PaInstrumentsWindow.this);
	}
	

	
	public void  setResultView(Image img, String instrumentName) {
				
		setNewResultView(img, instrumentName);
		
		PaInstrument.m_previewImageNotConfirmed = true;
		
		if(m_hideFlag){
			
			m_instPanel.confirmChanges();
		}
	}
	//the difference with upper function is that upper flag is not set
	public void  setNewResultView(Image img, String instrumentName) {
		
		if(img == null) {
			
			 JOptionPane.showMessageDialog(
					 	this,
					    getMessagesStrs("cantSetNewImageForInstrummentMessage") + NEXT_ROW +
					    m_instPanel.getFullPath(),
					    getMessagesStrs("messageInfoCaption"),
					    JOptionPane.OK_OPTION);
			
			 writeLog(getMessagesStrs("cantSetNewImageForInstrummentMessage")+" : " +
					instrumentName, null, true, true, true);
			return;
		}
		
		if (m_instViewPanel != null) {
			
			m_instViewPanel.setImage(img);
			
			if(instrumentName != null) {
				
				m_instViewPanel.setInstrumentName(instrumentName);
				
			} else if(m_instPanel.getInstrument() != null ) {
				
				m_instViewPanel.setInstrumentName(m_instPanel.getInstrument().getName());
			}

			m_instViewPanel.repaint();
		}

	}
	
	//this flag determines if there were changes in window
	public void setPreviewCurrentImageChanged(boolean wasChanged) {
		
		m_prevCurrentImageChanged = wasChanged;
	}
	
	public boolean getPreviewCurrentImageChanged() {
		
		return m_prevCurrentImageChanged;
	}
	
	/**
	 * 
	 * @return current image for preview tab; if it is null, then return current image for work area
	 */
	public Image  getResultViewImage() {
		
		if(null != m_instViewPanel) {
			
			BufferedImage im = m_instViewPanel.getCurrentImage();
			
			if(im != null) return im;
			
		}
		
		return (BufferedImage)m_instPanel.getCurrentImage();	
	}
	/**
	 * 
	 * @return current source image from the work area
	 */
	public Image  getSourceViewImage() {
		
		return m_instPanel.getCurrentImage();	
	}
	
	protected void createUI() 
	
	{
		
		Container pane = getContentPane();
		
		JPanel panelMain = new JPanel();

		BorderLayout l = new BorderLayout();
	
		panelMain.setLayout(l);
		
		JPanel panelToolBars = PaGuiTools.createHorizontalPanel();
		
		JToolBar buttonToolBar = createMainButtonToolBar(); 
		
		panelToolBars.add(buttonToolBar);
		
		JToolBar buttonToolBar2 = createMainButtonToolBar2();
		
		panelToolBars.add(buttonToolBar2);
		
		panelMain.add(panelToolBars, BorderLayout.LINE_START);
		
		JPanel  imagesPanel = createImagesPanel(); 
		
		panelMain.add(imagesPanel, BorderLayout.CENTER);
		
		pane.add(panelMain);	
		
		PaInstrument.isAnyInstrumentWasUsed = false;
		
		PaInstrument.m_previewImageNotConfirmed = false;
	
	}
	/**
	 * 
	 * @return main left toolbar with all instruments
	 */
	protected JToolBar createMainButtonToolBar()
	{
		JToolBar toolBar = new JToolBar();
		
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
		
		toolBar.setOrientation(JToolBar.VERTICAL);
		
		
		toolBar.setBorder( BorderFactory.createEmptyBorder(2*getTopLabelHeight(),PaUtils.VERT_STRUT/2,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT/2));
			
		Dimension size = new Dimension(m_buttonSize.width,m_buttonSize.height);

		PaSaveButton butSave = new PaSaveButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butSave);
		
		m_saveAction = new PaSaveAction(butSave);
		
		butSave.setAction(m_saveAction);
		
		m_saveAction.setEnabled(false);

		toolBar.add(butSave);
		
		toolBar.addSeparator();

		PaComplexButton butUndo = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butUndo);
		
		m_undoAct =new PaUndoAction();
		
		butUndo.setAction(m_undoAct);
		
		m_undoAct.setEnabled(false);
		
		butUndo.setSpecialButtonEnabled(false);
		
		butUndo.setSpecialButtonVisible(false);
		
		toolBar.add(butUndo);
		
		PaComplexButton butRedo = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butRedo);
		
		m_redoAct = new PaRedoAction();
		
		butRedo.setAction(m_redoAct);
		
		m_redoAct.setEnabled(false);
		
		butRedo.setSpecialButtonEnabled(false);
		
		butRedo.setSpecialButtonVisible(false);
		
		toolBar.add(butRedo);
		
		PaComplexButton butUS = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butUS);
		
		m_undoToStartAct = new PaUndoToStartAction();
		
		butUS.setAction(m_undoToStartAct);
		
		m_undoToStartAct.setEnabled(false);
		
		butUS.setSpecialButtonEnabled(false);
		
		butUS.setSpecialButtonVisible(false);
		
		toolBar.add(butUS);
		
		toolBar.addSeparator();
		
		PaTurnButton butTurn = new PaTurnButton(this,size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butTurn);
		
		toolBar.add(butTurn);
		
		PaResizeButton butResize = new PaResizeButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butResize);
		
		toolBar.add(butResize);
		
		PaCropButton butCrop = new PaCropButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butCrop);
		
		toolBar.add(butCrop);
		
		PaHorizonButton butHorizon = new PaHorizonButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butHorizon);
		
		toolBar.add(butHorizon);
		
		toolBar.addSeparator();
		
		PaContrastButton butContr = new PaContrastButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butContr);
		
		toolBar.add(butContr);
		
		PaLinearButton butLinear = new PaLinearButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butLinear);
		
		toolBar.add(butLinear);
		
		PaGammaButton butGamma = new PaGammaButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butGamma);
		
		toolBar.add(butGamma);
		 
		toolBar.addSeparator();
		
		PaRedEyeButton butRedE = new PaRedEyeButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butRedE);
		
		toolBar.add(butRedE);
		
		toolBar.addSeparator();
		
		PaColorBalanceButton butColorB = new PaColorBalanceButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butColorB);
		
		toolBar.add(butColorB);
		
		PaHSIbutton butHSI = new PaHSIbutton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butHSI);
		
		toolBar.add(butHSI);
		
		toolBar.addSeparator();
		
		PaSharpButton butSharp = new PaSharpButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butSharp);
		
		toolBar.add(butSharp);
		
		PaBlurButton butBlur = new PaBlurButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butBlur);
		
		toolBar.add(butBlur);
		
		toolBar.addSeparator();
		
		PaErosionButton erosionMask = new PaErosionButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(erosionMask);
		
		toolBar.add(erosionMask);
		
		toolBar.addSeparator();
		
		PaBinaryButton butBinary = new PaBinaryButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butBinary);
		
		toolBar.add(butBinary);

		PaCornersButton butMask = new PaCornersButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butMask);
		
		toolBar.add(butMask);
		
		PaFftButton butFft = new PaFftButton(this,size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butFft);
		
		toolBar.add(butFft);
		
		toolBar.add(Box.createVerticalGlue() );
	
		return toolBar;
			
	}
	
	
	protected JToolBar createMainButtonToolBar2()
	{
	
		JToolBar toolBar = new JToolBar();
		
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
		
		toolBar.setOrientation(JToolBar.VERTICAL);
		
		toolBar.setBorder( BorderFactory.createEmptyBorder(2*getTopLabelHeight(),PaUtils.VERT_STRUT/2,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT/2));
			
		Dimension size = new Dimension(m_buttonSize.width,m_buttonSize.height);
		
		PaComplexButton butNext = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butNext);
		
		m_nextImageAction = new PaNextImageAction();
		
		butNext.setAction(m_nextImageAction);
		
		butNext.setSpecialButtonEnabled(false);
		
		butNext.setSpecialButtonVisible(false);
		
		toolBar.add(butNext);
		
		PaComplexButton butPrev = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size,		
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butPrev);
		
		butPrev.setAction(new PaPrevImageAction());
		
		butPrev.setSpecialButtonEnabled(false);
		
		butPrev.setSpecialButtonVisible(false);
		
		toolBar.add(butPrev);
		
		toolBar.addSeparator();
		
		
PaZoomRectButton butZoomRect = new PaZoomRectButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butZoomRect);
		
		toolBar.add(butZoomRect); //zoom in - true
		
		PaZoomButton butZoomIn = new PaZoomButton(this, size, true, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butZoomIn);
		
		toolBar.add(butZoomIn); //zoom in - true
		
		PaZoomButton butZoomOut = new PaZoomButton(this, size, false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butZoomOut);
		
		toolBar.add(butZoomOut); //zoom out - false
		
		toolBar.addSeparator();
		
		toolBar.add(Box.createVerticalGlue() );
	
		return toolBar;
			
	}
	
	/**
	 * 
	 * @return additional toolbar between preview area and work area
	 */
	private JToolBar createAddButtonToolBar()
	{
		
		JToolBar toolBar = new JToolBar();
		
		toolBar.setOrientation(JToolBar.VERTICAL);
		
		toolBar.setLayout(new BoxLayout(toolBar , BoxLayout.Y_AXIS));
		
		toolBar.setBorder( BorderFactory.createEmptyBorder(2*getTopLabelHeight(),PaUtils.VERT_STRUT/2,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT/2));
		
		m_hidePrevViewAction = new PaHidePreViewAction();
		
		m_confirmAction = new PaConfirmAction();
		
		m_resetPreviewAction = new PaResetPreviewAction();
		
		toolBar.add(m_hidePrevViewAction).setMaximumSize( m_buttonSize);
		
		toolBar.addSeparator();
		
		toolBar.add(m_confirmAction).setMaximumSize(m_buttonSize);
		
		toolBar.add(m_resetPreviewAction).setMaximumSize(m_buttonSize);
		
		toolBar.addSeparator();		
				
		toolBar.setAlignmentX(JToolBar.TOP_ALIGNMENT);
		
		toolBar.setFloatable(false);
		
		toolBar.setToolTipText(getGuiStrs("additToolBarInstrToolTip"));
	
		return toolBar;
			
	}
	
	/**
	 * Creates the main panel of insruments dialog; it's divided into two part:
	 * work area and preview area
	 */
	protected JPanel  createImagesPanel()
	{
		JPanel imagesPanel = new JPanel();
		lt = new BoxLayout(imagesPanel, BoxLayout.X_AXIS);
		
		imagesPanel.setLayout(lt);
		
		//work area
		m_instPanel = new PaInstrumentsPanel(this,m_imgSource, getGuiStrs("workAreaLabel"),
				getGuiStrs("workAreaLabelToolTip"));
	
		//preview area
		m_instViewPanel = new  PaTabPreview();
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel , BoxLayout.Y_AXIS));
		
		panel.add(createAddButtonToolBar() );
		
		panel.add(Box.createVerticalGlue() );
		
		imagesPanel.add( m_instPanel  );
		
		imagesPanel.add(panel);
		
		imagesPanel.add( m_instViewPanel );
		
		Dimension instrAreaSize = PaUtils.get().getSettings().getPrevAreaFixedSize();
		
		m_instViewPanel.setMinimumSize(instrAreaSize);
		
		m_instViewPanel.setMaximumSize(instrAreaSize);
		
		return  imagesPanel ;
		
	}
	

	
	public void setUndoButtonEnabled(boolean flag) {
		
		m_undoAct.setEnabled(flag);
	}
	
	public void setRedoButtonEnabled(boolean flag) {
		
		m_redoAct.setEnabled(flag);
	}
	
	public void setUndoStartButtonEnabled(boolean flag) {
		
		m_undoToStartAct.setEnabled(flag);
	}
	
	public void setSaveButtonEnabled(boolean flag) {
		
		 m_saveAction.setEnabled(flag);
	}
	
	 /**
	  * <p>Saves changes of image file </p>
	  * @param saveAs - true if we need the operation 'save as'
	  * @return dialog window response code
	  * @author Dashkov Andrii
	  */
	public int save(boolean saveAs) {
		
		
		File outputfile = null;
		
		BufferedImage im = m_instPanel.m_currentImage;
		
		String ext = PaUtils.getNameAndExtension(m_instPanel.getFullPath())[1];
		
		int n = JOptionPane.NO_OPTION;
		
		boolean flagSaveAs = false;//true if save as operation was used
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		String tmpPathStr = null;
		
		String pathStr = null;
		
		try
		{
			if(saveAs) {
				
				JFileChooser f =new JFileChooser();
	
				int result = f.showSaveDialog(this);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					
					String name = f.getSelectedFile().getPath();
					 
					pathStr = name+"."+ext;
					
				    outputfile = new File(m_instPanel.getFullPath());
				    
				    tmpPathStr = pathStr + "_tmp";
				   
				    
				    n = JOptionPane.YES_OPTION;
				    
				    flagSaveAs = true;
				}
	
			}
			else {
				
				n = JOptionPane.showConfirmDialog(
					    this,
					    getMessagesStrs("saveInstrResultsMessage"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.YES_NO_CANCEL_OPTION);
				
				if ( n == JOptionPane.YES_OPTION) {
					
				    outputfile = new File(m_instPanel.getFullPath());
				    
					tmpPathStr = m_instPanel.getFullPath() +"_tmp";
					
					pathStr = m_instPanel.getFullPath();
				}
				
			}
			
	
			
			if(n == JOptionPane.YES_OPTION) {
				
				PaExifLoader ld = new PaExifLoader();
			  	
	    		Path tmpPath = Paths.get(tmpPathStr);
	    		
	    		File tmpFile = new File(tmpPathStr);
	    		
			    try {
			    	 if(PaUtils.OS_TYPE == OSType.LINUX){
			    		 
			    		 //problems with Open SDK to write jpeg files
			    		 BufferedImage im1 = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
			    		 
			    		 im1.getGraphics().drawImage(im, 0, 0, null);
			    		 
			    		 ImageIO.write(im1, ext, tmpFile);
			    		 
			    		//here we try to save the previous exif data in anew file
						 if(!ld.getMergeExifBlock(tmpFile, outputfile,  pathStr)) {
							 
							//can't save exif  - save without exif support
							File f = new File(pathStr);
							
							ImageIO.write(im1, ext, f);
						 }
						 Files.deleteIfExists(tmpPath );
			    		 
			    		 
			    	 }
			    	 else {
			
			    		 ImageIO.write(im, ext, tmpFile);
				    		
				    		//here we try to save the previous exif data in anew file
						 if(!ld.getMergeExifBlock(tmpFile, outputfile, pathStr)) {
							 
							//can't save exif  - save without exif support
							File f = new File(pathStr);
							
							ImageIO.write(im, ext, f);
						 }
						 Files.deleteIfExists(tmpPath );
			    	 }
				} catch (IOException e) {
					
					 JOptionPane.showMessageDialog(
							 	this,
							    getMessagesStrs("cantSaveImageMessage") + NEXT_ROW +
							    m_instPanel.getFullPath(),
							    getMessagesStrs("messageInfoCaption"),
							    JOptionPane.OK_OPTION);
					
					 writeLog(getMessagesStrs("cantSaveImageMessage")+" : " +
							m_instPanel.getFullPath(), null, true, true, true);
				}
			    
			    //operation is successful
			    setSaveButtonEnabled(false);
			    
			    setPreviewCurrentImageChanged(true);
			    
			    m_imageHasBeenSaved = !flagSaveAs; //this flag should not be set to true for save as opearation
			    //because the flag m_wasOnceSave is used to detect the case when the image in view panel should be
			    //refreshed
			    
				if(!saveAs) {//we need to refresh an icon for edited image
					
					PaEvent e = new PaEvent(PaEventDispatcher.IMAGE_EDITED_EVENT);
					
					e.setId(m_imgSource.getId());
					
					PaEventDispatcher.get().fireCustomEvent(e);
				}
				   
			}
		}
		finally {
			
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			writeLog("Instruments window: save operation finished.", null,
					true, false, false );
		}
		return n;
	}

	
	 /**
	  * <p>Action for editor's save image operation</p>
	  * @author Dashkov Andrii
	  */
	public class PaSaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		PaSaveButton m_button = null;

		public PaSaveAction(PaSaveButton button) {

			m_button = button;
			
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pasave.png"));
				
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("saveInstMenuToolTip")); 
			
			putValue(NAME, getMenusStrs("saveInstMenuName"));

			
		}
			
		public void actionPerformed(ActionEvent e) {		
			save(m_button.isSaveAs());
		}		
	}
	
	public class PaConfirmAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
	
		public PaConfirmAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paconfirm.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("confirmViewButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("confirmViewButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			 m_instPanel.confirmChanges();

		}		
	}	
	
	public class  PaHidePreViewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
	
		public  PaHidePreViewAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pacloseview.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("hideViewButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("hideViewButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			if ( ! m_hideFlag ) {
				
				m_instViewPanel.setVisible(false);
				m_hideFlag = true;
				
				putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paopenview.png"));
				
				putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("openViewButtonToolTip"));
				
				m_confirmAction.setEnabled(false);
				
				m_resetPreviewAction.setEnabled(false);
			}
			else {
				
				Dimension s = m_instPanel.getSize();
				
				m_instViewPanel.setVisible(true);
				
				m_instViewPanel.setSize(s.width/2, s.height);
				
				m_instPanel.setSize(s.width/2, s.height);
				
				m_instViewPanel.setPreferredSize(new Dimension(s.width/2, s.height));
				
				m_hideFlag = false;
				
				putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "pacloseview.png"));
				
				putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("hideViewButtonToolTip"));
				
				m_confirmAction.setEnabled(true);
				
				m_resetPreviewAction.setEnabled(true);
				
			}

		}		
	}	
	
	
	public class  PaNextImageAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
	
		public  PaNextImageAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "panextimageroi.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("editorNextImageToolTip")); 
			
			putValue(NAME, getMenusStrs("nextImageButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			
			//if any instrument was used and was not confirmed, then ask user before moving to next
			if ( PaInstrument.isAnyInstrumentWasUsed && PaInstrument.m_previewImageNotConfirmed ) {
				
				
				int n = JOptionPane.showConfirmDialog(
					    null,
					    getMessagesStrs("unAppliedInstrChangesMessage4"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.OK_CANCEL_OPTION);
			
		
				if ( n == JOptionPane.CANCEL_OPTION) {	
					
					 return;			
				}
			}
			
			
			loadNextImage( );
			
			//for new image the initial state must be set to false
			PaInstrument.isAnyInstrumentWasUsed = false;
			
			PaInstrument.m_previewImageNotConfirmed = false;
			
		}		
	}
	
	public class  PaPrevImageAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
	
		public  PaPrevImageAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paprevimageroi.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("roiPrevImageToolTip"));
			
			putValue(NAME, getMenusStrs("nextImageButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			//if any instrument was used and was not confirmed, then ask user before moving to previous
			if ( PaInstrument.isAnyInstrumentWasUsed && PaInstrument.m_previewImageNotConfirmed ) {
				
				
				int n = JOptionPane.showConfirmDialog(
					    null,
					    getMessagesStrs("unAppliedInstrChangesMessage5"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.OK_CANCEL_OPTION);
			
		
				if ( n == JOptionPane.CANCEL_OPTION) {	
					
					 return;			
				}
			}
			
			m_parent.loadPrevImage(PaInstrumentsWindow.this);
			
			//for new image the initial state must be set to false
			PaInstrument.isAnyInstrumentWasUsed = false;
			
			PaInstrument.m_previewImageNotConfirmed = false;
			
		}		
	}
	
	
	
	public class  PaUndoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
	
		public  PaUndoAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paundo.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("undoInstrButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("undoInstrButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			m_instPanel.undoAction();

		}		
	}	
	
	public class  PaUndoToStartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
	
		public   PaUndoToStartAction() {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "painstundostart.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("undoStartInstrButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("undoStartInstrButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			m_instPanel.undoToStartAction() ;

		}		
	}
	
	public class  PaRedoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
	
		public   PaRedoAction() {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "painstredo.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("redoInstButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("redoInstButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			m_instPanel.redoAction();

		}		
	}
	
	
	public class  PaResetPreviewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		

		public  PaResetPreviewAction() {

			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paresetprev.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("resetPrevcToolTip")); 
			
			putValue(NAME, getMenusStrs("resetPrevButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			 setResultView(m_instPanel.getCurrentImage(),getGuiStrs("resetImageOperationName"));
			 
			 writeLog("Instruments window: reset preview area operation", null, true, false, false );
	
		}
	}
	
	/**
	 * <p>Not just close, but send event about closing and check various conditions</p>
	 * @author Andrii Dashkov
	 *
	 */
	private class CloseWinListener  extends WindowAdapter {
		
		public void windowClosing(WindowEvent we) {
			
			//sent close event in ordr to close any opened special windows
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED));
			
			//if any instrument was used and was not confirmed, then ask user before closing
			if ( PaInstrument.isAnyInstrumentWasUsed && PaInstrument.m_previewImageNotConfirmed ) {
				
				
				int n = JOptionPane.showConfirmDialog(
					    null,
					    getMessagesStrs("unAppliedInstrChangesMessage3"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.OK_CANCEL_OPTION);
			
		
				if ( n == JOptionPane.CANCEL_OPTION) {	
					
					 return;			
				}
			}
			
			//here we have situation - the changes was moved into work area, but have not been saved
			if ( getPreviewCurrentImageChanged() &&  m_saveAction.isEnabled()) {
						
				if (save(false) != JOptionPane.CANCEL_OPTION ) {		
					
					PaInstrument.isAnyInstrumentWasUsed = false;
					
					disposeMe();
				}	
			
			} else { 
				
				PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED));
				
				PaInstrument.isAnyInstrumentWasUsed = false;
				
				disposeMe();				
			}
		}
		
		public void windowLostFocus(WindowEvent e) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED));
		}
		
		 public void windowIconified(WindowEvent e) {
        
            PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.IMAGE_EDITOR_CLOSED));
             
		 }
	}
	
	/**
	 * 
	 * @return main work panel 
	 */
	public PaInstrumentsPanel getWorkPanel() { return m_instPanel; }
	
	/**
	 * <p>Delegates setting of instrument to the work panel class</p>
	 * @param i -instrument to set
	 */
	public void setInstrument(PaInstrument i) {
		
		m_instPanel.setInstrument(i);
	}
	
	/**
	 * <p>Delegates resetting of instrument to the work panel class</p>
	 */
	public void resetInstrument() {
		
		m_instPanel.resetInstrument();
	}
	
	/**
	 * 
	 * @return the height of top label , needs for alignment of first toolbars buttons
	 */
	protected int getTopLabelHeight() {
	
		String s = getGuiStrs("workAreaLabel");
		
		JLabel lTemp = new JLabel(s);
	
		Font f = PaUtils.get().getBaseFont();
		
	    FontMetrics fm = lTemp.getFontMetrics(f); 
	
		return (int)f.createGlyphVector(fm.getFontRenderContext(), s).getVisualBounds().getHeight();
	}

	/**
	 * 
	 * @return the flag of save operation. This flag is true even when save operation was done only once
	 */
	public boolean isImageSaved() { return m_imageHasBeenSaved; }
	
	/**
	 * Performs dispose operation and remove all links in event dispatcher
	 */
	void disposeMe() {
		
		PaEventDispatcher.get().disconnect(m_undoAct);
		
		PaEventDispatcher.get().disconnect(m_redoAct);
		
		PaEventDispatcher.get().disconnect(m_undoToStartAct);
		
		PaEventDispatcher.get().disconnect(m_saveAction);
		
		PaEventDispatcher.get().disconnect(m_hidePrevViewAction); 
		
		PaEventDispatcher.get().disconnect(m_confirmAction);
		
		PaEventDispatcher.get().disconnect(m_resetPreviewAction);
		
		for(PaComplexButton b :m_complexButtons) {
			
			PaEventDispatcher.get().disconnect(b);
		}
		
		PaEventDispatcher.get().disconnect(this);
		
		dispose();
	}
	
	/**
	 * Sets the new info about size in the info panel
	 * @param width
	 * @param height
	 */
	public void setImageSizeInfo(int width, int height) {
		
		if( m_instPanel != null) {
			
			m_instPanel.setImageSizeInfo(width,height);
		}
	}

	public PaInstrumentsPanel  getInstrumentPanel() { return m_instPanel; }
}


