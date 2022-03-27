package PaROI;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import PaCollection.PaImage;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaUtils;
import PaImage.PaViewPanel;
import PaEditor.PaComplexButton;
import PaEditor.PaInstrument;
import PaEditor.PaSaveButton;
import PaEditor.PaEnumInstrumentNames;
import PaEditor.PaInstrumentsWindow;
import PaEditor.PaZoomRectButton;
import PaEditor.PaZoomButton;


/**
 * The main window for ROI  instruments
 * @author avd
 *
 */
public class  PaRoiWindow extends PaInstrumentsWindow {
	
	{
	
	}
	
	private static final long serialVersionUID = 1L;
	
	private PaRoiChangeButton m_butRoi;
	
	PaComplexButton butMovetoLatest;
	
	PaCutRoiButton m_butCutS;
	
	PaComplexButton m_butStat;
	
	PaComplexButton m_butROIsave;
	
	PaSaveButton m_butSave;
	
	PaRoiLoadButton m_butLoad;
	
	PaPixSaveButton m_pixSaveButton;
	
	private String m_roi_file_name = "";
	

	public  PaRoiWindow (JFrame jf, PaImage imgSource, PaViewPanel panel ) {
		
		super(jf, imgSource, panel);
		
		m_hideFlag = true;
		
		setTitle (getGuiStrs("roiWindowTitle"));
		
		setIconImage((new ImageIcon(PaUtils.get().getIconsPath() + "painstruments.png")).getImage());
		
	}


	protected void createUI() 
	
	{
		
		Container pane = getContentPane();
		
		JPanel panelMain = new JPanel();

		BorderLayout l = new BorderLayout();

		panelMain.setLayout(l);
	
		JToolBar buttonToolBar = createMainButtonToolBar(); 
		
		panelMain.add(buttonToolBar, BorderLayout.LINE_START);
		
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
		
		m_butLoad = new PaRoiLoadButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butLoad);
				
		m_butLoad.setAction( new PaRoiWindow.PaRoiLoadNewReentAction(m_butLoad));
		
		toolBar.add(m_butLoad);
		
		m_butSave = new PaSaveButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butSave);
		
		m_saveAction = new PaInstrumentsWindow.PaSaveAction(m_butSave);
		
		m_butSave.setAction(m_saveAction);
		
		m_saveAction.setEnabled(false);
		
		m_butSave.setSpecialButtonEnabled(false);
		
		m_butSave.setToolTips(getGuiStrs("roiSaveMainButToolTip"), 
				getGuiStrs("roiSaveRadioButToolTip"), getGuiStrs("roiSaveAsRadioButToolTip"));
		
		toolBar.add(m_butSave);
		
		toolBar.addSeparator();
		
		PaComplexButton butNext = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butNext);
		
		butNext.setAction(new PaNextImageRoiAction());
		
		butNext.setSpecialButtonEnabled(false);
		
		butNext.setSpecialButtonVisible(false);
		
		toolBar.add(butNext);
		
		PaComplexButton butPrev = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size,
				
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butPrev);
		
		butPrev.setAction(new PaPrevImageRoiAction());
		
		butPrev.setSpecialButtonEnabled(false);
		
		butPrev.setSpecialButtonVisible(false);
		
		toolBar.add(butPrev);
		
		butMovetoLatest = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size,
				
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(butMovetoLatest);
		
		butMovetoLatest.setAction(new PaMoveTolatestImageRoiAction());
		
		butMovetoLatest.setSpecialButtonEnabled(false);
		
		butMovetoLatest.setSpecialButtonVisible(false);
		
		butMovetoLatest.getMainButton().setEnabled(false);
		
		toolBar.add(butMovetoLatest);
		
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
		
		m_butCutS = new PaCutRoiButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butCutS);
		
		toolBar.add(m_butCutS);
		
		m_butStat = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butStat);
		
		m_butStat.setAction(new PaGetStatRoiAction());
		
		m_butStat.setSpecialButtonEnabled(false);
		
		m_butStat.setSpecialButtonVisible(false);
		
		toolBar.add(m_butStat);

		m_butROIsave = new PaComplexButton(PaEnumInstrumentNames.NONE, this, null, size, 
				false, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butROIsave);
		
		m_butROIsave.setAction(new PaSaveRoiSubimagesAction());
		
		m_butROIsave.setSpecialButtonEnabled(false);
		
		m_butROIsave.setSpecialButtonVisible(false);
		
		toolBar.add(m_butROIsave);
		
		m_butRoi = new PaRoiChangeButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_butRoi);
		
		toolBar.add(m_butRoi);
		
		setEditRoiButtonEnabled(false);
		
		setROIDependentButtonsEnabled(false);
		
		toolBar.addSeparator();
		
		m_pixSaveButton = new PaPixSaveButton(this, size, m_parent.getDataCircleBuffer());
		
		m_complexButtons.add(m_pixSaveButton);
		
		toolBar.add(m_pixSaveButton);
	
		return toolBar;
			
	}
	
	/**
	 * Creates the main panel with image; for ROI window it has only one part with images
	 */
	protected JPanel  createImagesPanel()
	{
		JPanel imagesPanel = new JPanel();
		
		lt = new BoxLayout(imagesPanel, BoxLayout.X_AXIS);
		
		imagesPanel.setLayout(lt);
		
		//work area
		m_instPanel = new PaInstrumentsRoiPanel(this,m_imgSource, getGuiStrs("workAreaLabel"),
				getGuiStrs("workAreaLabelToolTip"));
	
		
		imagesPanel.add( m_instPanel  );

		
		return  imagesPanel ;
		
	}
	
	public void loadImage( ) {
	
		((PaInstrumentsRoiPanel) m_instPanel).setNewImageNameForRoi();
		
		((PaInstrumentsRoiPanel) m_instPanel).setImageWithRoi();
		
		m_instPanel.fitViewImage( );
		
		m_instPanel.repaint();
		
		((PaInstrumentsRoiPanel) m_instPanel).setMainLabel(createMailLabel());

	}
	
	public void refreshImage( ) {
		
		
		((PaInstrumentsRoiPanel) m_instPanel).setImageWithRoi();
		
		((PaInstrumentsRoiPanel) m_instPanel).refreshViewImage( );
		
		m_instPanel.repaint();
		
		((PaInstrumentsRoiPanel) m_instPanel).setMainLabel(createMailLabel());

	}
	
	
	
	public void loadImage( PaImage imgSource) 
	{
		if(imgSource == null) { return; }
		
		this.m_imgSource = imgSource;
		
		m_instPanel.initImage(imgSource);
		
		loadImage( );
		
	}
	
	public class PaRoiLoadNewReentAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
		PaRoiLoadButton m_button = null;

		public PaRoiLoadNewReentAction(PaRoiLoadButton button) {

			m_button = button;
			
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paloadroifile.png"));
				
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("loadROIButtonToolTip")); 

		}
			
		public void actionPerformed(ActionEvent e) {	
			
			switch(m_button.getOperationCode()) {
			
				case 0: {
					
					m_roi_file_name = ((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel().loadROIfile();
					
					break;
				}
				
				case 1: {
					
					m_roi_file_name  = ((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel().createNewRoiFile();
				
					break;
				}
				
				case 2: {
					
					m_roi_file_name = m_button.loadRecentFile();
					
					if (null != m_roi_file_name && !m_roi_file_name.isEmpty() ) {
						
						((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel().loadNewRoiData(m_roi_file_name);
						
					}
		
					break;
				}
				
				default: { m_roi_file_name = ""; }
			
			};
			
			//the activation of disabled buttons
			if (null != m_roi_file_name && !m_roi_file_name.isEmpty()) {
				
				((PaInstrumentsRoiPanel) m_instPanel).setMainLabel(createMailLabel());
				
				butMovetoLatest.getMainButton().setEnabled(true);
				
				setROIDependentButtonsEnabled(true);
				
				m_saveAction.setEnabled(true);
				
				m_butSave.setSpecialButtonEnabled(true);
				
				PaUtils.get().getRecents().addToRecentRoiList(m_roi_file_name);
				
			}
		}		
	}
	
	
	
	public String createMailLabel() {
		
		String im_file_name = ((PaInstrumentsRoiPanel) m_instPanel).getName();
		
		return getGuiStrs("roiFileMainCaptionName") + "   " + m_roi_file_name + "     " +
				 getGuiStrs("roiImFileName") + "   " + im_file_name;
	}
	
	public class  PaNextImageRoiAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
	
		public  PaNextImageRoiAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "panextimageroi.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("editorNextImageToolTip")); 
			
			putValue(NAME, getMenusStrs("nextImageButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			m_parent.loadNextImage(PaRoiWindow.this);
			
		}		
	}
	
	public class  PaPrevImageRoiAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
	
		public  PaPrevImageRoiAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paprevimageroi.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("roiPrevImageToolTip"));
			
			putValue(NAME, getMenusStrs("nextImageButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			m_parent.loadPrevImage(PaRoiWindow.this);
			
		}		
	}
	
	/**
	 * 
	 * @author avd
	 * <p>Jumps to the first image which was not labeled with any ROI yet.</p>
	 */
	public class  PaMoveTolatestImageRoiAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
	
		public  PaMoveTolatestImageRoiAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "palatestimageroi.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("roilatestImageToolTip")); 
			
			putValue(NAME, getMenusStrs("roiLatestImageButtonName"));	
		}
		

		
		public void actionPerformed(ActionEvent e) {
			
			if (  ((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel().getRoiFilePath() != null) {
			
				m_parent.loadImageWithFirstAbsentName(PaRoiWindow.this, getRoiMap());
				
			}
			else {
				
				JOptionPane.showMessageDialog( PaRoiWindow.this,
	    				getMessagesStrs("messageInfoNeedRoiFile"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
			}
			
		}		
	}
	
	

	public class  PaGetStatRoiAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
	
		public PaGetStatRoiAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "paroistat.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("roiStatActionButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("roiStatActionButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			

			PaROIStatisticsDialog statDialog = new PaROIStatisticsDialog(PaRoiWindow.this, m_instPanel);
			
			statDialog.setLocation(100,100);
			
			statDialog.setVisible(true);
			
		}		
	}	
	
	
	public class  PaSaveRoiSubimagesAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		
	
		public PaSaveRoiSubimagesAction () {
	
			putValue(AbstractAction.SMALL_ICON, new ImageIcon(PaUtils.get().getIconsPath() + "cutRoiIcon.png"));
			
			putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("saveRoiActionButtonToolTip")); 
			
			putValue(NAME, getMenusStrs("roiStatActionButtonName"));	
		}
		
		public void actionPerformed(ActionEvent e) {
			
			
			PaRoiPanel roi_panel = ((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel();
			
			PaRoiSaveRoiDialog sDialog = new PaRoiSaveRoiDialog(PaRoiWindow.this, 
					 roi_panel.getTypesList(),  roi_panel.getRoiFilePath());
			
			
			sDialog.setLocation(100,100);
			
			sDialog.setVisible(true);
			
		}		
	}
	
	

	 /**
	  * <p>Saves changes of ROI file </p>
	  * @param saveAs - true if we need the operation 'save as'
	  * @return dialog window response code
	  * @author Dashkov Andrey
	  */
	public int save(boolean saveAs) 
	{
		
		String fullPath = ((PaInstrumentsRoiPanel) m_instPanel).getRoiFilePath();
		
		String pathToSave = fullPath; 
	
		String names[] =  PaUtils.getNameAndExtension(fullPath);
		
		String ext = names[1];
		
		int n = JOptionPane.NO_OPTION;
		
		boolean flagSaveAs = false;//true if save as operation was used
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try
		{
			if(saveAs) {
				
				JFileChooser f =new JFileChooser();
	
				int result = f.showSaveDialog(this);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					
					String name = f.getSelectedFile().getPath();
					 
					pathToSave = name + "." + ext;			   
				    
				    n = JOptionPane.YES_OPTION;
				    
				    flagSaveAs = true;
				    
				}
				
				//get back the save radio button state
				m_butSave.setRadioButtonSaveAs(false);
	
			}
			else {
				
				n = JOptionPane.showConfirmDialog(
					    this,
					    getMessagesStrs("saveROIfileMessage"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.YES_NO_CANCEL_OPTION);
				
				if ( n == JOptionPane.YES_OPTION) {
					
					pathToSave = fullPath;
				}
				
			}
			
	
			
			if(n == JOptionPane.YES_OPTION) {
				
			    ((PaInstrumentsRoiPanel) m_instPanel).saveRoiFile(pathToSave);
			    
			    //operation is successful
			    setSaveButtonEnabled(false);	
			    
			    setPreviewCurrentImageChanged(true);
			    
			    
			    m_imageHasBeenSaved = !flagSaveAs; //this flag should not be set to true for save as operation
			    //because the flag m_wasOnceSave is used to detect the case when the image in view panel should be
			    //refreshed
			    
				if(!saveAs) {//we need to refresh an icon for edited image
					
					PaEvent e = new PaEvent(PaEventDispatcher.IMAGE_EDITED_EVENT);
					
					e.setId(m_imgSource.getId());
					
					PaEventDispatcher.get().fireCustomEvent(e);
				}
				else {
					
					//refresh ROI file path after save as operation
					m_roi_file_name = pathToSave;
					
					((PaInstrumentsRoiPanel) m_instPanel).setRoiFilePath(pathToSave);
					
					((PaInstrumentsRoiPanel) m_instPanel).setMainLabel(createMailLabel());
					
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
	
	public boolean isRoiListFileLoaded() 
	{
		
		return ((PaInstrumentsRoiPanel) m_instPanel).isRoiListFileLoaded();
		
	}
	
	public PaRectangle getCurrentRoi() 
	{
		
		return ((PaInstrumentsRoiPanel) m_instPanel).getCurrentROI();
	}

	
	public HashMap<String,Dimension> getImageSizesMap()
	{
		
		return m_parent.getImageSizesMap();
	}
	
	public HashMap<String,String> getImagesPathMap()
	{
		
		return m_parent.getImagesPathMap();
	}
	
	//returns pair of current image file name and full path to it
	public HashMap<String,String> getCurrentImagePathMap()
	{
		
		String fullPathCurrentImage = m_instPanel.getFullPath();
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		map.put( PaUtils.getFileNameFromString(fullPathCurrentImage), fullPathCurrentImage);
			 
		return map;

	}
	
	
	HashMap<String,ArrayList<PaRectangle>> getRoiMap() 
	{
		
		return ((PaInstrumentsRoiPanel) m_instPanel).getRoiPanel().getRoiMap(); 
		
	}
	
	public void setEditRoiButtonEnabled(boolean flag)
	{
		m_butRoi.getMainButton().setEnabled(flag);
		
		m_butRoi.setSpecialButtonEnabled(flag);
	}
	
	public void setROIDependentButtonsEnabled(boolean flag)
	{
		m_butCutS.getMainButton().setEnabled(flag);
		
		m_butCutS.setSpecialButtonEnabled(flag);
		
		m_butStat.getMainButton().setEnabled(flag);
		
		m_butROIsave.getMainButton().setEnabled(flag);
		
	}
	
}


