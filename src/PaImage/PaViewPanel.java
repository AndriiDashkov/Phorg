package PaImage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ArrayBlockingQueue;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaAlbum;
import PaCollection.PaAlbumContainer;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaDialogs.PaAlbumNewDialog;
import PaDialogs.PaImageMoveDialog;
import PaDialogs.PaSliderDialog;
import PaDialogs.PaSpecialDialog;
import PaDialogs.PaSpecialDialog.DialogType;
import PaEditor.PaEnumInstrumentNames;
import PaEditor.PaInstrumentsWindow;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;
import PaEvents.PaEventSelect;
import PaEvents.PaEventSortStart;
import PaExif.PaExifLoader;
import PaExif.PaImageExf;
import PaForms.PaAlbumTreeNode;
import PaForms.PaImageTable;
import PaGlobal.PaButtonsGroup;
import PaGlobal.PaCloseFlag;
import PaGlobal.PaSortOrderEnum;
import PaGlobal.PaUtils;
import PaGlobal.PaUtils.OSType;
import PaLong.PaCopyToAlbumTask;
import PaLong.PaMoveToStandardTask;
import PaUndoRedo.PaDelCommand;
import PaUndoRedo.PaUndoRedoDeque;
import static PaExif.PaTagParser.getString;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;
import PaROI.PaRectangle;
import PaROI.PaRoiWindow;


/**
 * <p>The main view panel of the application</p>
 * @author Andrey Dashkov
 *
 */

public class PaViewPanel extends JPanel {	
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT, this, "refreshPhotosInAlbom");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SELECT_ALL_EVENT, this, "selectedAllPhotos");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SELECT_EVENT, this, "clearSelected");
	
		PaEventDispatcher.get().addConnect(PaEventDispatcher.COPY_BUFFER_EVENT, this, "copyInBuffer");
			
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SORT_START_EVENT, this, "sort");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_REFRESH_EVENT, this, "refreshInfoLabel");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_CUT_EVENT, this, "cutInBuffer");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_EDITED_EVENT, this, "refreshBoostIconEvent");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SET_BOOKMARKED_EVENT, this, "setBookmarkedEvent");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SET_UN_BOOKMARKED_EVENT, this, "setUnBookmarkedEvent");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SET_SLIDER_SHOW_EVENT, this, "setSliderVisibleMarkEvent");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SET_SLIDER_HIDE_EVENT, this, "setSliderHideMarkEvent");
		
	}
	
	JScrollPane m_scrollPane;
	
	JPanel m_panelGrid;
	
	private JPanel m_panel;
	
	private PaButtonsGroup buttonGroup;
	
	private ArrayList<PaViewPhotosForm> m_containerViewPhotoForm;
	
	private PaImageContainer m_photoContainer = null;
	
	private PaImageTable m_photoTable = null;
	
    private static DataFlavor  m_dragAndDropFlavor = null;
    
    private int m_keyCurrentSelection;//current selection index - used for key selection operation
	
    public final static int VERT_SCROLL_UNIT_INC = 20;
    
    /**
     * Container to save the current instruments parameters, which can be multiple (that is why ArrayList is used)
     * PaEnumInstrumentNames key - inner name of  instruments, ArrayBlockingQueue<Object> - list of data object
     * (for every instrument/complex button) the data has a different structure which reflect instruments parameters.
     * Every instrument/complex button has its own queue inside m_instrumnetsDat.
     */
    HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> m_instrumnetsData;
    
    final int  INST_INITIAL_DATA_QUEUE_CAPASITY = 10;

	public PaViewPanel () {
		
		m_keyCurrentSelection = -1; //this means no selection
		//this.setTransferHandler(new PaTransferHandler());
		
		setTransferHandler(new PaTransferHandler());
		
		 // Create the listener to do the work when dropping on this object!
		setDropTarget(new DropTarget(this, new PaDropTargetListener(this)));
			
		setLayout(new BorderLayout());
		
		setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0));

		m_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		m_panel.setBackground(Color.WHITE);
		
		m_panel.setTransferHandler(new PaTransferHandler());
		
		 // Create the listener to do the work when dropping on this object!
       m_panel.setDropTarget(new DropTarget(this, new PaDropTargetListener(this)));
       
       m_panelGrid = new JPanel();
       
       m_panel.add(m_panelGrid);
       
       m_panelGrid.addKeyListener(new KeyAdapter());
       
   		m_panelGrid.setBackground(Color.white);
		
		m_panelGrid.setFocusTraversalKeysEnabled(false);
       
   		m_panelGrid.setTransferHandler(new PaTransferHandler());
   		
   		// Create the listener to do the work when dropping on this object!
   		m_panelGrid.setDropTarget(new DropTarget(PaViewPanel.this, new PaDropTargetListener(PaViewPanel.this)));

		m_containerViewPhotoForm = new ArrayList<PaViewPhotosForm>();

		m_scrollPane = new JScrollPane(m_panel);
		
		m_scrollPane.getVerticalScrollBar().setUnitIncrement(VERT_SCROLL_UNIT_INC);
		
		
		m_scrollPane.setTransferHandler(new PaTransferHandler());
		
		 // Create the listener to do the work when dropping on this object!
		m_scrollPane.setDropTarget(new DropTarget(this, new PaDropTargetListener(this)));
		
		ImageIcon ic = new ImageIcon(PaUtils.get().getIconsPath() + "paphototableicon.png");
		
		buttonGroup = new PaButtonsGroup("  "+getGuiStrs("viewPanelTotalCaptionAlbom"),ic);
		
		buttonGroup.setFilterLabelVisible( true);
		
		buttonGroup.setFilterLabelEnabled(false);
	
		add(buttonGroup.getMainPanel(), BorderLayout.NORTH);
		
		add (m_scrollPane, BorderLayout.CENTER);	
		
		setToolTipText(getGuiStrs("viewPanelToolTipText"));
		
		initInstrumentsDataContainer();
    
	}
	
	public ArrayList<PaViewPhotosForm> get_ContainerViewPhotoForm() {
		
		return m_containerViewPhotoForm;
	}
		
	public JPanel get_Panel() {
		
		return m_panel;
	}
	
	public PaImageContainer getPhotoContainer() {
	
		return m_photoContainer;
	}
	
	/**
	 * 
	 * @return the link to the panel with buttons open/close
	 */
	public PaButtonsGroup get_ButtonGroup() {
		
		return buttonGroup;
	}
	
	public void setPhotoFormTable( PaImageTable _photoTable) {
		
		 this.m_photoTable = _photoTable;
	}
	/**
	 * <p>Sets main view data in view panel </p>
	 * @param pContainer - container to be visible in view panel
	 * @param endMessage - special message which can be shown in the end of process; in the case of
	 * null the usual message will be shown
	 */
	public void setData(PaImageContainer pContainer, String endMessage, int ensureVisible) 
	{	
	
		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
				getMessagesStrs("loadAlbomAllImagesOperationCaption"),
				getMessagesStrs("numberOfLoadedImagesInAlbomCaption"), 0, 100);
		
		progressMonitor.setMillisToDecideToPopup(0);
		
		progressMonitor.setMillisToPopup(0);
		
		clearSelected(new PaEventSelect());
		
		PaLoadImages ts = new PaLoadImages(progressMonitor,pContainer,endMessage, ensureVisible);
		
		ts.execute();
		
	}
	/**
	 * <p>Sets information label for main view panel</p>
	 */	
	public void setInfoLabel( ) 
	{
		String s = "  " + getGuiStrs("viewPanelTotalCaptionCurrentAlbom") + "    " + getGuiStrs("viewPanelTotalCaptionItems")+ "    " 
					+ getGuiStrs("viewPanelTotalCaptionPrinted") + "    " +getGuiStrs("viewPanelTotalCaptionBookmarked");
		
		if(m_photoContainer != null ) {
			
			PaAlbum al = PaUtils.get().getAlbumContainer().getAlbum(m_photoContainer.getId());
			
			if(al != null) {
				
				int[] ar =m_photoContainer.getPrintedPhotos();
				
				s = "  " + getGuiStrs("viewPanelTotalCaptionCurrentAlbom") + " " + al.getName()+ "    " + getGuiStrs("viewPanelTotalCaptionItems")+ " " + Integer.toString( ar[2] ) + 
						"    " + getGuiStrs("viewPanelTotalCaptionPrinted") + " " +Integer.toString( ar[0] ) + "    " +
						getGuiStrs("viewPanelTotalCaptionBookmarked") + " " + Integer.toString( ar[1] );
			}
		}
		
		buttonGroup.getMainText().setText(s);
	}
	
	public void setFilterIconEnable(boolean flag) {
		
		buttonGroup.setFilterLabelEnabled(flag);
	}
	
	public void setSelectedInTableForm(int id) {
		
		m_photoTable.setSelected(id);
	}
	
	/**
	 *  Refreshes images in the album, makes control for existence of standard folder
	 * @param e - event to catch
	 */
	public void refreshPhotosInAlbom (PaEvent e) {		

		    if ( e.getEventType() != PaEventDispatcher.VIEWPANEL_REFRESH_EVENT ) { return; }
		    
		    PaImageContainer c = PaUtils.get().getMainContainer().getCurrentContainer();
		    
		    if(c != null &&!PaUtils.get().getAlbumContainer().isStandardFolderExist(c.getId())) {
		    	
		    	if(PaUtils.get().getAlbumContainer().createAlbumStandardFolder(c.getId())) {
		    	
			  		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
		    				getMessagesStrs("standardFolderHasBeenRestored"),
		    			    getMessagesStrs("messageInfoCaption"),
		    			    JOptionPane.INFORMATION_MESSAGE);
			  		
			  		writeLog("The standard folder has been recreated for the album with id = " + c.getId(),
			  				null, true, false, true);
		    	}
		    	else {
		    		
		    		writeLog("Can't recreate the standard folder fo album with id = " + c.getId(), null, true, false, true);
		    	}
		    	
		    }
		    
			setData(c,e.getEndMessage(),e.getId());
			
	}
	
	/**
	 *  Clears all selections
	 * @param eventSelect - select event to catch
	 */
	public void clearSelected(PaEventSelect eventSelect) 
	{
		if (PaUtils.get().getSelectedImages().size() > 0 && eventSelect.isClearOtherSelection()) 
		{
			for (PaViewPhotosForm viewPhoto : PaUtils.get().getSelectedImages().getList()) {
				
				viewPhoto.selectBackgroundPanels(false);
			}
			
			PaUtils.get().getSelectedImages().clear();
			
			m_keyCurrentSelection = -1;
		}
	}
	
	/**
	 * 
	 * @param eventSelect
	 */
	public void selectedAllPhotos(PaEvent eventSelect) {
		
		if ( PaEventDispatcher.SELECT_ALL_EVENT != eventSelect.getEventType() ) { return; }
		
		if (PaUtils.get().getMainContainer().getCurrentContainer() != null) {	
			
			PaUtils.get().getSelectedImages().clear();
			
			for (PaViewPhotosForm viewPhoto : m_containerViewPhotoForm) {

				PaUtils.get().getSelectedImages().add(viewPhoto);
				
				viewPhoto.selectBackgroundPanels(true);
			}
		}
	}
	
	public void setIconViewPhotoForm (PaViewPhotosForm view) {
		
		Runnable runnable = new PaViewRunnable(view, this);
		
		Thread thread = new Thread(runnable);
		
		thread.start();
		
		try {
			
			thread.join();
	
			m_panel.repaint();
			
		} 
		catch (InterruptedException e) {
			writeLog("InterruptedException :  " + NEXT_ROW, e, true, false, true);
		}
	}
	/**
	 * <p>It is for scroll bars moving in the programmatic way: image object with id must be visible on screen </p>
	 * @param image object's id that should be moved into visible part of the scroll area (view port)
	 * 
	 */
	public void ensureVisible(int id) 
	{
		 PaViewPhotosForm x= findImageObject(id);
		 
		 if( x != null ) {
			 
			 x.setEnsureSelected();
			 
			 ensureVisible(x);		 
		 }		
	}
	/**
	 * <p>It is for scroll bars moving in the programmatic way: x must be visible on screen </p>
	 * @param image object that should be moved into visible part of the scroll area (view port)
	 * 
	 */
	private void ensureVisible(PaViewPhotosForm x)
	{
		if(isVisibleInViewport(x)) return;
		
		Point p = new Point(x.getLocation());
		
	    //function scrollVisible... works awfully - we use scrollbars directly
		m_scrollPane.getVerticalScrollBar().setValue(p.y);
		
		if(!isVisibleInViewport(x)) {
			
			m_scrollPane.getHorizontalScrollBar().setValue(p.x);
		}

	}
	/**
	 * Moves horizontal scrollbar
	 * @param delta - the distance of moving, can be negative
	 */
	public void incrementHorizontalScrollbar(int delta) {
		
		int current = m_scrollPane.getHorizontalScrollBar().getValue();
		
		m_scrollPane.getHorizontalScrollBar().setValue(current + delta);
		
	}
	/**
	 * Moves vertical scrollbar
	 * @param delta - the distance of moving, can be negative
	 */
	public void incrementVerticalScrollbar(int delta) {
		
		int current = m_scrollPane.getVerticalScrollBar().getValue();
		
		m_scrollPane.getVerticalScrollBar().setValue(current + delta);
		
	}
	
	/**
	 * @return the link to the selected object
	 * 
	 */
	public PaViewPhotosForm getFirstSelectedItem() 
	{	
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.isSelected() ) {
				
				return x;
			}
		}
		return null;
	}
	
	/**
	 * Makes next object to be selected.
	 * @return the link to the next object after selected object
	 * 
	 */
	public PaViewPhotosForm getNextAfterSelectedItem(boolean select) 
	{	
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.isSelected() &&  it.hasNext()) {
				
				x = it.next();
				
				if(select) {
					
					x.setSelected(true, null);
				}
				
				return x; 
			}
		}
		return null;
	}
	
	public PaViewPhotosForm getBeforeSelectedItem(boolean select) 
	{	
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		PaViewPhotosForm prev = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.isSelected() ) {
				
				if(select && prev != null) {
					
					prev.setSelected(true, null);
					
				}
				
				return prev; 
			}
			
			prev = x;
		}
		return null;
	}
	
	/**
	 * @return index for selected item; in the case of multiselection the index of first selected item
	 * <p>In the case of nonselection returns -1</p>
	 */
	public int getFirstSelectedIndex() 
	{
		int sz = m_containerViewPhotoForm.size();
		
		for(int i=0; i < sz; ++i) {			
			
			if(m_containerViewPhotoForm.get(i).isSelected()) { return i; }
			
		}
		return -1;
	}
	/**
	 * @param image object's id that should be found 
	 * @return link to the image object with id
	 */
	public PaViewPhotosForm findImageObject(int id) 
	{	
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.getImage().getId() == id ) {
				
				return x;
			}
		}
		return null;
	}
	
	/**
	 * <p>Copies selected images objects into buffer container</p>
	 * @param e  - event of COPY_BUFFER_EVENT  type 
	 */
	public void copyInBuffer(PaEvent e) {
		
		if (e.getEventType() != PaEventDispatcher.COPY_BUFFER_EVENT ) { return; }
		
		copySelectedInBuffer(null);//because of null the selected images will be used inside
				
	}
	
	/**
	 * <p>Copies selected images objects into buffer container and then deletes them.
	 * Files on the disk are NOT DELETED, only links are under operation.</p>
	 * @param e  - event of IMAGE_CUT_EVENT  type 
	 */
	public void cutInBuffer(PaEvent e) {
		
		if (e.getEventType() != PaEventDispatcher.IMAGE_CUT_EVENT ) { return; }
		
		//the check for images in the standard folder
		//cut operation is forbidden, because can lead to logical troubles with albums
		ArrayList<PaImage> selectedList = getSelectedItems();
		
		Iterator<PaImage> it = selectedList.iterator();
		
		int s = selectedList.size();
		
		int counter = 0;
		
		while (it.hasNext()) {
			
			if(!it.next().isLink()) { it.remove(); ++counter; }
			
		}
		
		if(counter > 0) {
			
			writeLog(getMessagesStrs("someImInStFolderCutInBuffer")+" "
					+ counter,null, true, true, false);
			
			if(s == 1 && counter == 1) {
				
				JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
	    				getMessagesStrs("oneImInStFolderCutInBuffer"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
			}
			else {
				
	 		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("someImInStFolderCutInBuffer") + " " + counter,
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			}
		
		}
		
		copySelectedInBuffer(selectedList);
		
		PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
		
		if(cont != null /*&& cont.removeImages (selectedList, false) != 0*/) { //we delete links only!!!!
			
			int removeCounter = cont.removeImages (selectedList, false);
			
			if(removeCounter > 0){
				
				PaUndoRedoDeque.get().addUndo(new PaDelCommand(selectedList,cont.getId()));
				
				writeLog(getMessagesStrs("imagesAreInBufferAndCut")+" " + removeCounter, null, true, true, true);
			}
		}
	}
	/**
	 * <p>Copies selected images objects into buffer, private function. It is called from
	 * cutInBuffer() and  copyInBuffer()</p>
	 * @param list - the list of selected items, which can come from outside for cut operation for example;
	 * in the case of null we use the current selected images
	 */
	private void copySelectedInBuffer(ArrayList<PaImage> list)
	{
		ArrayList<PaImage> selectedList = list;
		
		if(list == null ) { selectedList = getSelectedItems(); }
		
		if( selectedList == null ||  selectedList.isEmpty()) {
			
			writeLog(getMessagesStrs("noSelectionToUseBuffer"),null, true, true, false);
			
			return;
		}
		
		ArrayList<PaImage> copyBuffer = PaUtils.get().getCopyPhotoBuffer();
		
		copyBuffer.clear();
	
		for(PaImage p: selectedList){
			
			PaImage pNew = new PaImage(p);
			
			pNew.setLink(p.isLink()); //because the used constructor always sets link property to true
			
			copyBuffer.add(pNew);
		}
		
		if(!copyBuffer.isEmpty()) {
			
			PaEventDispatcher.get().fireCustomEvent( 
					
					new PaEventEnable(PaEventEnable.TYPE.COPY_BUFFER_ACTIVATED));
			
			writeLogOnly("Images put in buffer "+ selectedList.size(),null);
			
			writeLog(getMessagesStrs("imagesCopiedInBuffer")+" "
					+ selectedList.size(),null, true, true, true);
		}
	}
	
	/**
	 * <p>Insert selected subject into all selected images.</p>
	 */
	public void insertSubjectInSelectedImages()
	{		
		
		ArrayList<PaImage> selectedList = getSelectedItems();
		
		ArrayList<Integer> selectedSubjects = PaUtils.get().getSubjectsForm().getSelectedRowsIds();
		
		if(!selectedList.isEmpty() && !selectedSubjects.isEmpty()) {
			
			for(PaImage p: selectedList){
				
				for(int id : selectedSubjects){
					
					p.addKey(id);
				}
			}
			
			writeLog(getMessagesStrs("selectedSubjectHasBeenInserted"),null, true, true, true);
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
			
		}else {
			
			writeLogOnly("No selection for subjects insert operation",null);
			
			writeLog(getMessagesStrs("noSelectionForSubjectInsert"),null, true, true, true);
		}
	}
	
	
	/**
	 * <p>Removes selected subject from all selected images.</p>
	 */
	public void removeSubjectInSelectedImages()
	{		
		ArrayList<PaImage> selectedList = getSelectedItems();
		
		ArrayList<Integer> selectedSubjects = PaUtils.get().getSubjectsForm().getSelectedRowsIds();
		
		if(!selectedList.isEmpty() && !selectedSubjects.isEmpty()) {
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("removeSubjectForImagesQuery"),
				    getMessagesStrs("messageAnswerCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n != JOptionPane.OK_OPTION) { return; } 
			
			for(PaImage p: selectedList){
				
				for(int id : selectedSubjects){
					
					p.removeKey(id);
				}
			}
			
			writeLog(getMessagesStrs("selectedSubjectHasBeenRemoved"),null, true, true, true);
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
			
		}else {
			
			writeLogOnly("No selection for subjects remove operation",null);
			
			writeLog(getMessagesStrs("noSelectionForSubjectInsert"),null, true, true, true);
		}
	}
	
	/**
	 * <p>Starts the external command which is described in Settings. It is used for start
	 * external graph editor</p>
	 * @param command - OS specific command string
	 */
	public void startExternalCommand(String command ) 
	{
		
		PaViewPhotosForm selectItem = getFirstSelectedItem();
		
		if ( selectItem != null ) {
			
			String pathName = selectItem.getImage().getFullPath();
			
			String[] com = {command,pathName};		
			
			startExternalCommandImpl(com);
	
		}
	}
	
	/**
	 * This reloaded variant with array as a command
	 * [0] - name of the program
	 * [1] - arguments
	 * @param command -to start
	 */
	private void startExternalCommandImpl(String[] command ) 
	{
		
		try {
			
			Runtime.getRuntime().exec(command);
			
		} catch (IOException e1) {
			
			writeLog("IOException  : start of external program " + NEXT_ROW, e1, true, false, true);
    		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("messageImpossibleToStartExtEditor"),
    			    getMessagesStrs("messageErrorCaption"),
    			    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Sets the current image as an icon of the album
	 */
	public void setAsAlbumIcon() 
	{
		PaViewPhotosForm f = getFirstSelectedItem();
		
		if ( f != null) {
		
			String path = f.getImage().getFullPath();
			
			m_photoContainer.getAlbum().setAlbomIcon(path);
		}
		
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.ALBUM_NEW_ICON_EVENT) );
		
	}
	/**
	 * Opens the window with graphic instruments
	 */
	public void openInstrumentsWindow()
	{
		int currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
		try {
			
			PaViewPhotosForm fr = getFirstSelectedItem();
			
			PaImage p = fr.getImage();
			
			File f = new File(p.getFullPath());
			
			if(PaUtils.get().isImageFile(f)) {//prevent the opening of a video file
			
				PaInstrumentsWindow win = new PaInstrumentsWindow(PaUtils.get().getMainWindow(), p , this );
			
				win.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); 
				
				win.setVisible(true);
				
				win.loadImage();
			}
			else {
				
				PaUtils.get().resetCursor(currentCursorType);
				
				writeInfoOnly(getMessagesStrs("cantOpenFileInInstrWin") + p.getFullPath());
			}
	
		}
		finally {

			PaUtils.get().resetCursor(currentCursorType);
		}
	}
	
	
	/**
	 * Opens the window with graphic instruments
	 */
	public void openRoiInstrumentsWindow()
	{
		int currentCursorType = PaUtils.get().getCurrentCursor();
		
		PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
		
		try {
			
			PaImage p = getFirstSelectedItem().getImage();
			
			File f = new File(p.getFullPath());
			
			if(PaUtils.get().isImageFile(f)) {//prevent the opening of a video file
			
				PaRoiWindow win = new PaRoiWindow(PaUtils.get().getMainWindow(), p , this );
			
				win.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); 
				
				win.setVisible(true);
				
				win.loadImage();
				
				//info message to help a user jump in
				PaSpecialDialog dialog = new PaSpecialDialog(win, DialogType.OK_OPTION,
						 getMessagesStrs("messageInfoCaption"), 
						 getMessagesStrs("loadROIInfosMessage"),true,JOptionPane.OK_OPTION,
						 "", 4);
				
				dialog.setVisible(true);
			}
			else {
				
				PaUtils.get().resetCursor(currentCursorType);
				
				writeInfoOnly(getMessagesStrs("cantOpenFileInInstrWin") + p.getFullPath());
			}
	
		}
		finally {

			PaUtils.get().resetCursor(currentCursorType);
		}
	}
	
	
	
	public  void loadNextImage(PaInstrumentsWindow win)
	{
		
		File f = null;
		
		PaImage p = null;
		
		while(!PaUtils.get().isImageFile(f)) {//prevent the opening of a video file
		
			PaViewPhotosForm nImageFrom = getNextAfterSelectedItem(true);
			
			if (null == nImageFrom) {  return; }
			
			p =  nImageFrom.getImage();
			
			f = new File(p.getFullPath());
			
		
		}	
		
		win.loadImage(p);
	}
	
	
	public  void loadPrevImage(PaInstrumentsWindow win)
	{
		
		File f = null;
		
		PaImage p = null;
		
		while(!PaUtils.get().isImageFile(f)) {//prevent the opening of a video file
		
			PaViewPhotosForm nImageFrom =  getBeforeSelectedItem(true);
			
			if (null == nImageFrom) {  return; }
			
			p =  nImageFrom.getImage();
			
			f = new File(p.getFullPath());
			
		
		}	
		
		
		win.loadImage(p);
	}
	
	/**
	 * Loads the first image with namefor which there is no labeling (no ROI for the image)
	 */
	public  void loadImageWithFirstAbsentName(PaInstrumentsWindow win, HashMap<String,ArrayList<PaRectangle>> roi_container)
	{
		
		PaImage p = null;
		
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			p =  x.getImage();
			
			String name = getFileNameFromString(p.getFullPath()); 
			
			ArrayList<PaRectangle> ar = roi_container.get(name); 
			
			if ( ar == null || ar.isEmpty() ) {
				
				x.setSelected(true, null);//need it for further working of next/prev move functionality
		
				break;
			}
			
			p = null;
			
		}
	
		win.loadImage(p);
	}
	
	
	/**
	 * Refreshes the boos icon for single image
	 * @param e event with id of single image
	 */
	public void refreshBoostIconEvent(PaEvent e){
		
		refreshBoostIcon(e.getId());
	}
	/**
	 * Reloads the boost icon for 1 image only and refreshes the view of the form for this image in the view panel
	 * @param id - id of the image to be refreshed
	 */
	public void refreshBoostIcon(int id){
	
		PaViewPhotosForm f = findImageObject(id);
		
		PaImage p = f.getImage();
		
		PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
		
		//this creates boost image on the disk
		cont.createSpecialLoadImage(p);
		
		//then we have to refresh image on the form; some geometry recalculation are needed
		String fileName = new String( new Integer(id).toString());
		
		String fullBoostImagePath = concatPathName( PaUtils.get().getPathToBoostIcons(cont), fileName+".jpeg");
		
		String pPath = p.getFullPath();
		
		try {
			
			File sFile = new File(pPath);
			
			BufferedImage sImage = ImageIO.read(sFile);
			
			int w = sImage.getWidth(); //full size
			
			int h = sImage.getHeight();//full size
			
			p.setWidth(w); //image object should be refreshed with new sizes(size can be changed in instruments window)
			
			p.setHeight(h);
			
			//the image can change its proportion - we have to recalculate the size of visible image on the form
			int scale = PaUtils.get().getSettings().getPhotoScale();
			
			if (w > h) {			
				
				f.setIconWidth(scale);
				
				int hC = (int) Math.round(scale/(((double)w)/h));
				
				hC = hC <= 0 ? 2 : hC;
				
				f.setIconHeight(hC);

			} else {
				
				int wC = (int) Math.round(scale*(((double)w)/h));
				
				wC = wC <= 0 ? 2 : wC;
				
				f.setIconWidth(wC);
				
				f.setIconHeight((int) Math.round(scale)); 
			}
	
			
			File outFile = new File(fullBoostImagePath);
			
			BufferedImage ic = null;
			
			ic = ImageIO.read(outFile);
			
			//not sure about that - is it really needs?
			p.setImageIcon(
					PaAlgoTransform.getScaledImage(ic, f.m_widthIcon, f.m_heightIcon));
				
			f.getIconLabel().setIcon(p.getImageIcon());
			
			p.set_scale(PaUtils.get().getSettings().getPhotoScale());
			
			f.repaint();
			
			writeInfoOnly(getMessagesStrs("refreshBoostOperFinished"));
		} 
		catch (IIOException exp) {
			
			writeLog("IIOException :  can't read image file for boost icon creation " + 
						NEXT_ROW + "Path : " + pPath + NEXT_ROW +
						"Image name : " + p.getName(), exp, true, false, true);
			
			writeInfoOnly(getMessagesStrs("refreshBoostOperFinishedProblems"));
			
		}
		catch (IOException exp) {
			
			writeLog("IOException :  can't read image file for boost icon creation " + 
						NEXT_ROW + "Path : " + pPath + NEXT_ROW +
						"Image name : " + p.getName(), exp, true, false, true);
			
			writeInfoOnly(getMessagesStrs("refreshBoostOperFinishedProblems"));
		}
	}
	/**
	 * Opens the window for the slide show
	 */
	public void openSlideWindow() 
	{
		if ( m_photoContainer != null ) { 
			
			ArrayList<PaImage> list = getSelectedItems();
			
			//if there is no selection, or the only one image is selected - then we believe that a user wants to see all images in album
			if(list.isEmpty() || list.size() == 1) {
				
				list = new ArrayList<PaImage>(m_photoContainer.getList());
				
			}
			
			//removing visible for which the flag "no show in slider" is set
			//the flag isVisible is used because the filtered out images must no be shown also.
			ListIterator<PaImage> it = list.listIterator();
			
			while(it.hasNext()) {
				
				PaImage p = it.next();
				
				if(p != null && (!p.isVisibleInSlider() || !p.isVisible() ||
						!PaUtils.get().isImageFileStr(p.getFullPath()))) {
					
					it.remove();
				}
			}
			
			//Create dialog
			PaSliderDialog frame =new PaSliderDialog(PaUtils.get().getMainWindow(), 
					getGuiStrs("slideShowDialogCaption") + " " +
							getGuiStrs("albomTableColumnAlbomName") + " : " +
							m_photoContainer.getAlbum().getName() + " " +
							getGuiStrs("imagesCaptionSliderName") + " " +list.size(),list);
			
			frame.setVisible(true);
				
		}
		else {
			
	 		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("albomNotLoadedForSlider"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	/**
	 * @param e - sorting event
	 * function to set sort order for all images
	 */
	public void sort(PaEventSortStart e) {
		
		if (e.getEventType() != PaEventDispatcher.SORT_START_EVENT ) { return; }
		
		if ( m_photoContainer == null || m_photoContainer.isEmpty()) { 
			writeLog( getMessagesStrs("noSortForEmptyContainer"), null, true, true, true);
			return; 
		}
		
		//_photoContainer.setSortOrder(ev.getSortIndex()); sorting now is in refresh process
		clearSelected(new PaEventSelect());
		
		PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));	
		
	}

	 /**
     * <p>Performs main drop operation over the inner image container</p>
     * @param eventLocation - the event mouse point where the drop is done
     * @param idToDrop - the id of image to drop
     */
	public void doDrop(Point eventLocation, int idToDrop)
	{
		if( idToDrop != -1 && eventLocation.getX() != -1) {
			
		
			int currentCursorType = PaUtils.get().getCurrentCursor();
			
			PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
			
			try {
				
				int insertId = idToDrop;
				
				int index = getIndexCloseToPoint(eventLocation);//here we receive the id after which user wants to do the reorder
				//here we try to understand if the drop BEFORE nearest image, or AFTER
				
				boolean beforeFlag = true;
				
				Point pC = m_containerViewPhotoForm.get(index).getLocationOnScreen();
				
				Point p1 = new Point(eventLocation);
				
				SwingUtilities.convertPointToScreen(p1, m_panel);
				
				if(pC.getX() < p1.getX()) beforeFlag = false;
				
				int id =  m_containerViewPhotoForm.get(index).getImage().getId();
				
				m_photoContainer.insertCustomSortId(insertId, id, beforeFlag);
					
				m_photoContainer.setSortOrder(PaSortOrderEnum.CUSTOM_ORDER); //only custom sorting for drag and drop operations
		
				PaEvent ev = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
				
				ev.setId(idToDrop);//set the id to make ensureVisible after drop
				
				PaEventDispatcher.get().fireCustomEvent(ev);
				
				//data has been changed - enable save operation
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(event);
	
				writeLog( getMessagesStrs("dropOperationComplete"), null, true, true, true);
			}
			finally {

				PaUtils.get().resetCursor(currentCursorType);
			}
						
		}	
		else {	
			writeLog( getMessagesStrs("dropOperationCantBeDone"), null, true, true, true);
		}
	}
    /**
     * <p>Returns image id which is visually before the point p in the main view panel</p>
     * <p>it is used for drag and drop operations</p>
     * @param p1 - the point on the view panel, usually this is a mouse point where the drop is released
     * @return the image id
     */
	private int getIndexCloseToPoint(Point p1)
	{
		
		double min = 10000.0;
		
		int index = -1;
		
		Point p = new Point(p1);
		
		SwingUtilities.convertPointToScreen(p, m_panel);
		
		 for(int i=0; i < m_containerViewPhotoForm.size(); ++i)
		 {
			 Point pComp = m_containerViewPhotoForm.get(i).getLocationOnScreen();
			 
			 double d1 = pComp.distance(p);
			 
			 pComp.x = pComp.x +  m_containerViewPhotoForm.get(i).getWidth();
			 
			 double d2 = pComp.distance(p);
			 
			 double d = d1 < d2 ? d1 : d2;
			 
			 if(p.getY() > pComp.getY() && d < min ) {	
				 
				min = d;
				
				index = i;
			 }		 
		 }
		 return index;
		
	}
	

	//Called when the drag operation has terminated with a drop on the operable part of the drop site for the 
	//DropTarget registered with this listener.
	public void dropActionChanged(DropTargetDragEvent dtde)
	{}
	//Called if the user has modified the current drop gesture.
	
    /**
     * <p>Returns (creating, if necessary) the DataFlavor representing PaViewPhotosForm</p>
     * @return
     */
    public static DataFlavor getDragAndDropFlavor() throws Exception {
        // Lazy load/create the flavor
        if (m_dragAndDropFlavor == null) {
        	
            m_dragAndDropFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=PaImage.PaViewPhotosForm");
        }

        return  m_dragAndDropFlavor;
    }
    
    class KeyAdapter implements KeyListener {



		@Override
		public void keyPressed(KeyEvent e) 
		{
			
			 int onmask = InputEvent.SHIFT_DOWN_MASK ;
			 
			 int offmask = InputEvent.CTRL_DOWN_MASK;
			 
			 boolean shiftOrCtrlFlag = (e.getModifiersEx() & (onmask | offmask)) == onmask;
		
			 int columns = PaUtils.get().getSettings().getColumnsAmount();
			 
			 int index = getFirstSelectedIndex();
			 
			 if(index == -1) return;
			 
			 int lastIndex = m_containerViewPhotoForm.size() -1;
				
			 if(e.getKeyCode() == KeyEvent.VK_TAB) {
				 
				 int nextIndex;
				 
				 if(shiftOrCtrlFlag) {
					 
						nextIndex = index - 1;
						
						if( nextIndex < 0) nextIndex = 0;
				 }
				 else {
					 
					nextIndex = index + 1;
					
					if( nextIndex > lastIndex) nextIndex = lastIndex;
				 }
					
				PaViewPhotosForm nextIndexImage = m_containerViewPhotoForm.get(nextIndex);
				
				PaViewPhotosForm indexImage = m_containerViewPhotoForm.get(index);
				
				indexImage.setSelected(false, e);
				
				nextIndexImage.setSelected(true, e);
				
				ensureVisible(nextIndexImage);
				
				return;
			 }
			
			 if (!shiftOrCtrlFlag) {		
				 
				 return;//if no shift or control modifier, the scroll area will dispatch arrows events
			 }
	
	
			if(m_keyCurrentSelection != -1 ) {
				
				index = m_keyCurrentSelection;
			}
	     
	        switch(e.getKeyCode()) {
		        case KeyEvent.VK_DOWN: 
		        {
		        	
		        	int nextIndex = index + columns;
		        	
		        	if( nextIndex > lastIndex) nextIndex = lastIndex;
		        	
		        	PaViewPhotosForm nextIndexImage = m_containerViewPhotoForm.get(nextIndex);
		        	
		        	if(m_containerViewPhotoForm.get(nextIndex).isSelected()) {
		        		
		        		for(int i= index; i <= nextIndex; ++i) {
		        			
		        			m_containerViewPhotoForm.get(i).setSelected(false, e);
		        		}
		        		
		        		ensureVisible(nextIndexImage);
		        		
		        		m_keyCurrentSelection = nextIndex;
		        	}
		        	else {
		        		for(int i= index; i <= nextIndex; ++i) {
		        			
		        			m_containerViewPhotoForm.get(i).setSelected(true, e);
		        		}
		        		
		        		ensureVisible(nextIndexImage);
		        		
		        		m_keyCurrentSelection = nextIndex;
		        	}
		        	return;
		        }
		        case KeyEvent.VK_UP: 
		        {	
		         	int nextIndex = index - columns;
		         	
		        	if( nextIndex < 0) nextIndex = 0;
		        	
		         	PaViewPhotosForm nextIndexImage = m_containerViewPhotoForm.get(nextIndex);
		         	
		          	if(nextIndexImage.isSelected()) {
		          		
		        		for(int i = index; i >= nextIndex; --i) 
		        		{
		        			m_containerViewPhotoForm.get(i).setSelected(false, e);
		        		}
		        		
		        		ensureVisible(nextIndexImage);
		        		
		        		m_keyCurrentSelection = nextIndex;
		        	}
		        	else {
		        		for(int i = index; i >= nextIndex; --i) {
		        			
		        			m_containerViewPhotoForm.get(i).setSelected(true, e);
		        		}
		        		
		        		ensureVisible(nextIndexImage);
		        		
		        		m_keyCurrentSelection = nextIndex;
		        	}
		        	return;
		        }
		        case KeyEvent.VK_LEFT: 
		        {
		        	PaViewPhotosForm indexImage = m_containerViewPhotoForm.get(index);
		        	
		        	int nextIndex = index - 1;
		        	
		        	if( nextIndex < 0)  {
		        		
		        		indexImage.setSelected(false, e);
		        		
		        		m_keyCurrentSelection = -1;
		        		
		        		return;
		        		
		        	}
		        	
		        	PaViewPhotosForm nextIndexImage = m_containerViewPhotoForm.get(nextIndex);
		        	
		        	if(nextIndexImage.isSelected()) {
		        		
		        		indexImage.setSelected(false, e);
		        		
		        		ensureVisible(nextIndexImage);
		        	}
		        	else {
		        		
		        		nextIndexImage.setSelected(true, e);
		        		
		        		ensureVisible(nextIndexImage);
		        	}
		        	
		        	m_keyCurrentSelection = nextIndex;
		        	
		        	return ;
		        }
		        case KeyEvent.VK_RIGHT: 
		        {
		        	int nextIndex = index + 1;
		        	
		        	if( nextIndex > lastIndex) nextIndex = lastIndex;
		        	
		        	PaViewPhotosForm nextIndexImage = m_containerViewPhotoForm.get(nextIndex);
		        	
		        	if(nextIndexImage.isSelected()) {

		        	}
		        	else {
		        		
		        		nextIndexImage.setSelected(true, e);
		        		
		        		ensureVisible(nextIndexImage);
		        		
		        	}
		        	
		        	m_keyCurrentSelection = nextIndex;
		        	
		        	return;
		        	
		        }
			    default:
	        }

			
		}


		@Override
		public void keyReleased(KeyEvent e) {}


		@Override
		public void keyTyped(KeyEvent e) {}
    	

    }
    
    /**
     * @param x - link to the image object to check visibility in a viewport of scroll pane
     * @return true if the image is entirely  in the viewport
     */
    private boolean isVisibleInViewport(PaViewPhotosForm x)
    {
    	Rectangle rImage = new Rectangle(x.getLocationOnScreen(),x.getSize());
    	
    	Rectangle viewPortImage = new Rectangle(m_scrollPane.getViewport().getLocationOnScreen(),
    			m_scrollPane.getViewport().getSize());
    	
    	return viewPortImage.contains(rImage);
    	
    }
    /**
     * 
     * @return the screen location of scroll pane; we need it for control of drag and drop operation
     */
    public Point getScrollPaneLocationOnScreen()
    {

    	return	m_scrollPane.getLocationOnScreen();
 
    }
    /**
     * 
     * @return the size of scroll pane;  we need it for control of drag and drop operation
     */
    public Dimension getScrollPaneSize()
    {
    	Dimension d = m_scrollPane.getSize();
   
    	if(m_scrollPane.getVerticalScrollBar().isVisible()) {
    	
    		d.width -= m_scrollPane.getVerticalScrollBar().getSize().width;
    	}
    	if(m_scrollPane.getHorizontalScrollBar().isVisible()) {
    		
    		d.height -= m_scrollPane.getHorizontalScrollBar().getSize().height;
    	}
    	return	m_scrollPane.getSize();
 
    }

    /**
     * <p> Refreshes info label of this panel - in the case, for example, if the current album was renamed</p>
     * 
     */
	public void refreshInfoLabel(PaEvent e) {
		
		setInfoLabel();
	}
    
    /**
     * <p>Opens the system folder (Explorer for Windows) where the selected image is</p> m_Ok
     * 
     */
	public void openSystemFolder()
	{
		PaViewPhotosForm  im = getFirstSelectedItem();
		
		if(im != null) {
			
			PaImage p = im.getImage();
			
			String path = new String();
			
			if(p != null){
				
				try {
					
					path = PaUtils.get().getPathFromString(p.getFullPath());
					
					if(Desktop.isDesktopSupported()) {
						
						if(PaUtils.OS_TYPE == OSType.LINUX ){
							
							Desktop.getDesktop().open(new File("//"+path));
							
						}
						else {
							
							Desktop.getDesktop().open(new File(path));
						}
					}
					else {
						writeLog(getMessagesStrs("desktopOpeartionNotSupp"),null,true,true,true);
						
					}
				} catch (IOException | IllegalArgumentException e) {
					
					//last chance to open the dolphin in Suse; for strange reason for Suse this row
					//Desktop.getDesktop().open(new File(path));
					//leads to exception
					if(PaUtils.OS_TYPE == OSType.LINUX && !path.isEmpty() ){
						
						//direct call for dolphin
						String[] command = {"dolphin " ,path}; //to avoid problems with paths which have spaces 
						
						startExternalCommandImpl(command);
						
					}
					else {
						
						writeLogOnly("Can't open folder for the image :", e);
						
						writeLog(getMessagesStrs("cantfindSystemFolderForImage"),e,true,true,true);
					}
			
				}
				
			}
			else {
				
				writeLogOnly("Can't open folder for the image; the reason - can't get the pointer to paphoto object", null);
				
				writeLog(getMessagesStrs("noObjectForSystemFolderOperation"),null,true,true,true);
			}
		}
		else {		
			
			writeLogOnly("Can't open folder for the image; the reason - no selection for operation :", null);
			
			writeLog(getMessagesStrs("noSelectionForSystemFolderOperation"),null,true,true,true);
		}
		
	}
	   /**
  * <p>Creates a new album with current selected images</p>
  */
	public void createNewAlbumForSelectedItems() {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		if (list.isEmpty()) { 
			
    		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
    		
			return; 
		}
		
		PaAlbumContainer albumContainer = PaUtils.get().getAlbumContainer();
		
		ArrayList<PaAlbumTreeNode> parentsList = new ArrayList<PaAlbumTreeNode>();
		
		int parentId  = m_photoContainer.getId();
		
		PaAlbumTreeNode node = new PaAlbumTreeNode();
		
		node.setAlbomName(albumContainer.getAlbum(parentId).getName());
		
		node.setId(parentId);
		
		parentsList.add(node); //only 1 parent - current album
		
		PaAlbumNewDialog dialog = new PaAlbumNewDialog(parentsList,PaUtils.get().getMainWindow(), albumContainer,
				0,getGuiStrs("newAlbomSelectionCaptionName")); 
	
		dialog.setVisible(true);
		
		String newText=dialog.getAlbomName();
		
		if (dialog.getClosedFlagValue() == 1) {
						
			PaAlbum albumToAdd = new PaAlbum (newText, dialog.getCommentAlbum(), 
					dialog.getRootPath(), dialog.getDate() , dialog.getFolderName());
			
			albumToAdd.setParentId(parentId);
			
			int currentCursorType = PaUtils.get().getCurrentCursor();
			
			PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
			
			try {
				
						
				int id = albumContainer.addAlbum(albumToAdd);
				
				if ( id != -1) {
							
						PaImageContainer cont = PaUtils.get().getMainContainer().getContainer(id);
						
						//cont.copyAllImagesAsLinks(list);
						cont.copyImagesWithControl(list);
					
						PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
						
						if(dialog.isAlbomShouldBeLoaded()) {
							
							PaUtils.get().getMainContainer().setCurrentLoadedContainer(cont);

						}
						
											
						PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
						
						PaEventDispatcher.get().fireCustomEvent(event);
						
						writeLog(getMessagesStrs("newAlbomWasAdded"), null, true, true, false) ;
						
				} 
				else {
					
					writeLog(getMessagesStrs("newAlbomWasNotAdded"),null, true, true, false) ; 			
				}	
				
			} catch (Exception e0) {
				
				writeLog("Exception", e0, true, false, true) ;
			}
			finally {
				
				PaUtils.get().resetCursor(currentCursorType);
			}
		}
		
		
		
	}
	
	
	 /**
     * <p>Gets all selected items</p>
     * @return list of selected items
     */
	public ArrayList<PaImage> getSelectedItems() {
		
		ArrayList<PaImage> list = new ArrayList<PaImage>();
		
		Iterator<PaViewPhotosForm> it = m_containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.isSelected() ) {
				
				list.add(x.getImage());
			}
		}
		return  list;
	}
	
	/**
	* <p>Copies selected images to other album</p>
	*/
	public void  moveSelectedImagesToAlbum() {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		if ( list.size()  == 0 ) {
			
	   		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			return; 
		}
	
		PaImageMoveDialog dialog = new PaImageMoveDialog(PaUtils.get().getMainWindow(),
				PaUtils.get().getAlbumContainer());
		
		String currentAlbumName = PaUtils.get().getAlbumContainer().getAlbum(
				PaUtils.get().getMainContainer().getCurrentContainer().getId()).getName();
		
		dialog.removeAlbumFromList(currentAlbumName);//current album must be not in the list
		
		dialog.setVisible(true);	
		
		if(dialog.getFlag() == PaCloseFlag.OK) {
		
	   		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
					getMessagesStrs("copyInAlbomOperationCaption")+" "
					+dialog.getSelectedName(),getMessagesStrs("copyInAlbomOperationNote"), 0, 100);
			
			progressMonitor.setMillisToDecideToPopup(0);
			
			progressMonitor.setMillisToPopup(0);
			
			//copy images operation is potentially long, we start it using the SwingWorker 
			PaCopyToAlbumTask ts = new PaCopyToAlbumTask(progressMonitor,dialog.getResultAlbomId(), list,
					dialog.isCopySelected(),dialog.getSelectedName());
			
			ts.execute();
		}
		
		
	}
	
	
	/**
	 * @author avd
	 *
	 */

	/**
	 * <p>Class PaLoadImages uses SwingWorker to start a long task into background. 
	 * Here is the operation of load images into view panel</p>
	 */
	 protected class PaLoadImages extends SwingWorker<Void, Void> implements PropertyChangeListener 
	 {

		 ProgressMonitor m_progressMonitor;
		 
		 int counter;
		 
		 int m_currentCursorType;
		 
		 int m_ensureVisibleId;
		 
		 /**
		  * End message can come with event; the problems is that long tasks are doing asynchronously so
		  * it is difficult to create proper message in the very end - the message can be shadowed be
		  * message of the long task
		  */
		 
		 String m_endMessage;
		 /**
		 * <p>Constructor has parameters to start add images operation properly</p>
		 * @param progressMonitor - progress monitor
		 * @param cont - container for loading images in view panel
		 * @param endMessage - special message which can be shown at the end of loading; not mandatory, can be null
		 */
		 public PaLoadImages(ProgressMonitor progressMonitor, PaImageContainer cont, String endMessage,
				 int ensureVisibleId) 
		 {
			 m_progressMonitor = progressMonitor;
			 
			 m_photoContainer = cont;
			 
			 m_endMessage = endMessage;
			 
			 m_ensureVisibleId = ensureVisibleId;
			 
			 addPropertyChangeListener(this);	
		 }
		 
	    @Override
	    public Void doInBackground() 
	    {
			counter = 1;
			
			m_currentCursorType = PaUtils.get().getCurrentCursor();
			
			PaUtils.get().setCursor(m_currentCursorType,Cursor.WAIT_CURSOR);
			
			//if the container is null then we clear all
			if(m_photoContainer == null) {
				
				m_panelGrid.removeAll();
				
				m_containerViewPhotoForm.clear();
				
				//m_panel.removeAll();
				m_panel.setVisible(false);
				
				m_panel.setVisible(true);
				
				setProgress(100);
				
				return null;
			}
			
			//Iterator<PaImage> it = PaUtils.get().getMainContainer().getCurrentContainer().iterator();
			int lng = m_photoContainer.getSize();
			
			try{
				
				
				if (m_photoContainer != null) {
					
					//filter using
					m_photoContainer.applyCurrentFilter();
					
					//we always do the sorting while refresh
					m_photoContainer.setSortOrder(PaSortOrderEnum.fromInt(PaUtils.get().getSortCombo().getSelectedIndex()));
					
					int column = PaUtils.get().getSettings().getColumnsAmount();
				
					m_panelGrid.removeAll();
					
					//m_panelGrid = null;
					//m_panelGrid = new JPanel( new GridLayout( 0, column,20,20));
					
					m_panelGrid.setLayout(new GridLayout( 0, column,20,20));
					//m_panelGrid.setBackground(Color.white);
					
					//m_panelGrid.setFocusTraversalKeysEnabled(false);
					//m_panel.removeAll();
					m_containerViewPhotoForm.clear();
								
					setInfoLabel();
					
					//m_panelGrid.setTransferHandler(new PaTransferHandler());
					 // Create the listener to do the work when dropping on this object!
					//m_panelGrid.setDropTarget(new DropTarget(PaViewPanel.this, new PaDropTargetListener(PaViewPanel.this)));
			
					//int sz = m_photoContainer.size();
					if (m_photoContainer.size() > 0) {
						
						//Iterator<PaPhoto> iter = _photoContainer.get_photos_sort().iterator();
						Iterator<PaImage> iter = m_photoContainer.getList().iterator();
			
						PaImage photo;
						
						int scale = PaUtils.get().getSettings().getPhotoScale();
						
						while (iter.hasNext()) {
							
							photo = iter.next();
							
							if (photo.isVisible() == true) {
								
								PaViewPhotosForm viewPhotosForm = new PaViewPhotosForm(photo, scale);	
								
								PaUtils.get().get_popUp().addMouseAdapter( viewPhotosForm.getFormPanel());
												
								viewPhotosForm.setParentViewPanel(PaViewPanel.this);
								
								m_containerViewPhotoForm.add(viewPhotosForm);
								
								m_panelGrid.add(viewPhotosForm);	
							}
							
						}	
						
						Iterator<PaViewPhotosForm> iterView = m_containerViewPhotoForm.iterator();
						
						PaViewPhotosForm viewForm = null;
						
						while (iterView.hasNext()) {
							
							viewForm = iterView.next();
							
							setIconViewPhotoForm(viewForm);
							
							if(this.isCancelled()) { break; }
							
							int pr = counter*99/lng;
							
							if(pr < 1) pr = 1;
							
							if(pr >= 100) pr = 99;
							
							setProgress(pr);	    
							
							++counter;
						}

					} else {

						m_panelGrid.removeAll();
						
						m_containerViewPhotoForm.clear();
						
						//m_panel.removeAll();
						
						m_panel.setVisible(false);
						
						m_panel.setVisible(true);
						
						
						writeLog("  "+getGuiStrs("viewPanelCaptionAlbomEmpty"), null, true, true, true);
					}	
				
				} else {
					
					m_panelGrid.removeAll();
					
					m_containerViewPhotoForm.clear();
					
					m_panel.setVisible(false);
					
					m_panel.setVisible(true);
					
					buttonGroup.getMainText().setText("  "+getGuiStrs("viewPanelTotalCaptionAlbom"));
				}
			}
			finally {	
				
				setProgress(100);
			}
			return null;
	    }

	    /*
	     * Executed in event dispatching thread
	     */
	    @Override
	    public void done() 
	    {
			PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);  
			
			if( m_endMessage == null) {
				
				writeLog(getMessagesStrs("numberOfLoadedImagesInAlbomCaption")+" "+(counter-1), null, true, true, false);
			}
			else {
				
				writeLog(m_endMessage, null, true, true, false);
			}
			
			if(PaSortOrderEnum.isCustomOrder(PaUtils.get().getSortCombo().getSelectedIndex())){
				
				writeLog(getMessagesStrs("dragAndDropIsAvailable"), null, true, true, false);
			}
			
		
			if(m_panelGrid != null) {
				
				m_panelGrid.requestFocusInWindow();
			}
			if(m_ensureVisibleId != -1) {
				
				 ensureVisible(m_ensureVisibleId);
			}
	    }
	    
	  	/* (non-Javadoc)
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			 if ("progress" == evt.getPropertyName()) {
				 
			 	int p = (Integer) evt.getNewValue();
			 	
	            m_progressMonitor.setProgress(p);
	            
	            String message =
	                    String.format(getMessagesStrs("numberOfLoadedImagesInAlbomCaption")+" %d%% \n", p);
	            
	            m_progressMonitor.setNote(message);
	      
	            if (m_progressMonitor.isCanceled() || this.isDone()) {
	            	
	                Toolkit.getDefaultToolkit().beep();
	                
	                if (m_progressMonitor.isCanceled()) {
	                	
	                    this.cancel(true);
	    	
	                } 
	            }
	        }			
		}
	}
	
	 /**
	  * Rotates all selected images
	  * @param flag - true if the rotation in right direction; false for left direction
	  */
	 public void rotateSelectedImages(boolean flag) {
		 
			ArrayList<PaImage> list = getSelectedItems();

			PaExifLoader ld = new PaExifLoader();
	
			if (list.isEmpty()) { 
				
	    		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
	    				getMessagesStrs("messageNoSelectedItems"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
	    		
				return; 
			}
			
			int currentCursorType = PaUtils.get().getCurrentCursor();
			
			PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
			
			int counter = 0;
			try {
			
				
				for(PaImage pp : list) {
					
					BufferedImage im = null;
					
					try {
						String pth = pp.getFullPath();
						
						if(PaUtils.get().isImageFileStr(pth)) {
						
							File file = new File(pth);
							
							String tmpPathStr = pth+"_tmp";
							
							Path tmpPath = Paths.get(tmpPathStr);
							
							File tmpFile = new File(tmpPathStr);
						
							im = ImageIO.read(file);
							
							String ext = PaUtils.getNameAndExtension(pth)[1];
						
							if(im != null) {
								
								BufferedImage newIm = PaAlgoTransform.getRotatedImage(im,90.0f,flag);
								
								if(ImageIO.write(newIm, ext, tmpFile)) {
									
									//here we try to save the previous exif data in anew file
									if(!ld.getMergeExifBlock(tmpFile, file, pth)) {
										
										//can't save exif  - save without exif support
										ImageIO.write(newIm, ext, file);
									}
									Files.deleteIfExists(tmpPath );
									
									++counter;
									
									refreshBoostIcon(pp.getId());
									
									pp.setWidth(newIm.getWidth());
									
								    pp.setHeight(newIm.getHeight());
									
									PaUtils.get().setMainLabel(getMessagesStrs("rotatedImNumberMessage") + " " + counter);
								}
							}
						}
										 
					} catch (IOException e) {
						
						writeLog("IOException (rotate selection operation) for image  :"+ pp.getFullPath() + NEXT_ROW,e, true, false, true) ;
		
					}
				}
			}
			finally {
				
				if(counter > 0) {
					
					PaEventDispatcher.get().fireCustomEvent( new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
				}
				
				PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);
			}
		 
	 }
	 
		/**
		 * <p>Moves all linked images to standard folder for selected albom</p>
		 */
		public void moveImagesToStandardFolder() 
		{
			ArrayList<PaImage> list =  getSelectedItems();
			
			if(list.isEmpty()) {
				
				writeLog(getMessagesStrs("messageNoSelectedItems") + NEXT_ROW,null, true, true, false) ;
				
				JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
	    				getMessagesStrs("messageNoSelectedItems"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
				return; 
			
			}
			int count = 0;
			
			for(PaImage p: list) {
				
				if(!p.isLink()) { ++count; }
				
			}
			if(count == list.size()) {
				
				writeLog(getMessagesStrs("allSelectedImagesInStandFolder") + NEXT_ROW,null, true, true, false) ;
				
				JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
	    				getMessagesStrs("allSelectedImagesInStandFolder"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
				return;	
			}
			

			PaAlbum al =  m_photoContainer.getAlbum();
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("moveSelectedLinksIntoStandard")+NEXT_ROW+
		    		getMessagesStrs("moveAllLinksStandardPath") + " " +al.getFullStandardPath(),
				    getMessagesStrs("moveAllLinksIntoStandardCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n == JOptionPane.OK_OPTION) {
			
				ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
					getMessagesStrs("moveToStandardOperationCaption"),
					getMessagesStrs("moveToStandardOperationNote"), 0, 100);
				
					
				progressMonitor.setMillisToDecideToPopup(0);
				
				progressMonitor.setMillisToPopup(0);
				
				//the operation is potentially long, we start it using the SwingWorker 
				PaMoveToStandardTask ts = new PaMoveToStandardTask(progressMonitor,al.getId(), list);
				
				ts.execute();
			}
		}
		/**
		 * 
		 * @param id - image id
		 * @param flag - flag to set true - bookmark, false - unbookmark
		 */
		public void setBookmarked(int id, boolean flag) {
	
			PaViewPhotosForm f = findImageObject(id);
			
			if(f == null) return;
			
			PaImage im = f.getImage();
			
			im.setBookmarked(flag);

			f.setIcons() ;

			setInfoLabel();
			
			setSelectedInTableForm(id);

			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		
		}
		public void setBookmarkedEvent(PaEvent ev) {
			
			 int id = ev.getId();
			 
			 setBookmarked(id, true);
			 
		 }
		public void setUnBookmarkedEvent(PaEvent ev) {
			
			 int id = ev.getId();
			 
			 setBookmarked(id, false);
		 }

		/**
		 * Reset all bookmarks in the current album
		 */
		public void resetAllBookmarks() {
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("resetAllBookmarksQuery"),
				    getMessagesStrs("messageAnswerCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n != JOptionPane.OK_OPTION) { return; } 
			
			
			boolean anyChanges = false;
			
			for (PaViewPhotosForm f : m_containerViewPhotoForm) {

				PaImage im = f.getImage();
				
				im.setBookmarked(false);

				f.setIcons() ;

				setInfoLabel();
				
				setSelectedInTableForm(f.getImage().getId());
				
				anyChanges = true;
			}
			if(anyChanges) {
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
				
				writeLog(getMessagesStrs("allBookmarksWerereset") + NEXT_ROW,null, true, true, false) ;
			}
			
		}
		/**
		 * Catches the event and performs the set of slider mark to hide state
		 * the id of image is in the ev parameter
		 * @param ev - event with type SET_SLIDER_HIDE_EVENT
		 */
		public void setSliderHideMarkEvent(PaEvent ev) {
			
			setSliderMarkVisible(ev.getId(), false);
		
		}
		/**
		 * Catches the event and performs the set of slider mark to 'visible' state
		 * the id of image is in the ev parameter
		 * @param ev - event with type SET_SLIDER_SHOW_EVENT
		 */
		public void setSliderVisibleMarkEvent(PaEvent ev) {
			
			setSliderMarkVisible(ev.getId(), true);
		}
		/**
		 * 
		 * @param id - id of an image
		 * @param flag - true if you want to set it be visible in the slider window 
		 */
		public void setSliderMarkVisible(int id, boolean flag) {
			
			PaViewPhotosForm f = findImageObject(id);
			
			if(f == null) return;
			
			PaImage im = f.getImage();
			
			im.setVisibleInSlider(flag);
	
			f.setIcons() ;
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
			
		}
	
		/**
		 * Reset all slider marks in the current album
		 */
		public void resetAllSliderMarks() {
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("resetAllSliderMarksQuery"),
				    getMessagesStrs("messageAnswerCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n != JOptionPane.OK_OPTION) { return; } 
			
			
			boolean anyChanges = false;
			
			for (PaViewPhotosForm f : m_containerViewPhotoForm) {

				PaImage im = f.getImage();
				
				im.setVisibleInSlider(true);

				f.setIcons() ;
				
				anyChanges = true;
			}
			if(anyChanges) {
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
				
				writeLog(getMessagesStrs("allSliderMarksWerereset") + NEXT_ROW,null, true, true, false) ;
				
			}
			
		}
		
		/**
		 * Deals with selected images and set a exif date as an album date
		 */
		public void syncDates() {
			
			ArrayList<PaImage> list = getSelectedItems();
			
			if(list.isEmpty()) { return; }
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("syncDateExifQuery"),
				    getMessagesStrs("messageAnswerCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n != JOptionPane.OK_OPTION) { return; } 
			
			PaExifLoader loader = new  PaExifLoader();
			
			String noData = getGuiStrs("noDataString");
			
			boolean flag = false;
			
			try {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
				for(PaImage p : list) {
					
					if(loader.loadFile(p.getFullPath())) {
						
						String d = getString(loader.getExifElement(PaImageExf.DateTime),"Date/time");
						
						if(!d.equals(noData)) {
							
							String[] l = d.split(":");
							
							String[] l1 = l[2].split(" ");
							
							Date dt = PaUtils.stringToDate(l[0]+"-" + l[1] + "-" + l1[0],"yy-MM-dd");
							
							if(dt != null) {
								
								p.setDate(dt);
								
								flag = true;
							}
						}
					}
				}
		
			}
			finally {
				
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				if(flag) {
					
					PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
					
					PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
				}
				
			}
		}
		/**
		 * Creates data circle buffers (based on queue) for every instrument.
		 * This will be used for saving instruments parameter between different sessions of Editor.
		 */
		private void initInstrumentsDataContainer() {
			
			 m_instrumnetsData = new HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>>();
			
			 PaEnumInstrumentNames[] ar = PaEnumInstrumentNames.toArray();
			 
			 for(PaEnumInstrumentNames t : ar) {
				 
				 if(t != PaEnumInstrumentNames.NONE) {
					 
					 m_instrumnetsData.put(t, new ArrayBlockingQueue<Object>(INST_INITIAL_DATA_QUEUE_CAPASITY));
				 } 
			 }		
		}
		/**
		 * 
		 * @return the circle buffer for all instruments data to use in the Editor
		 */
		public HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> getDataCircleBuffer()
		{
			return  m_instrumnetsData;	
		}
		
		
		public HashMap<String,Dimension> getImageSizesMap()
		{
			
			HashMap<String,Dimension> map = new HashMap<String,Dimension>();
			
			for(int i = 0; i < m_containerViewPhotoForm.size(); ++i) {
				
				 PaImage pa = m_containerViewPhotoForm.get(i).getImage();
				 
				 Dimension d = new Dimension( pa.getWidth(), pa.getHeight());
				 
				 map.put( PaUtils.getFileNameFromString(pa.getFullPath()), d);
				 
			}
			
			return map;
		}
		
		//key - short name, value - full path
		public HashMap<String,String> getImagesPathMap()
		{
			
			HashMap<String,String> map = new HashMap<String,String>();
			
			for(int i = 0; i < m_containerViewPhotoForm.size(); ++i) {
				
				 PaImage pa = m_containerViewPhotoForm.get(i).getImage();
				 
				 map.put( PaUtils.getFileNameFromString(pa.getFullPath()), pa.getFullPath());
				 
			}
			
			return map;
		}
}
