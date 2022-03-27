package PaImage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaAlbum;
import PaCollection.PaAlbumContainer;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaCollection.PaMainConainer;
import PaCollection.PaSelectedImages;
import PaDialogs.PaAlbumNewDialog;
import PaDialogs.PaImageEditDialog;
import PaDialogs.PaImageMoveDialog;
import PaDialogs.PaImagePropertiesDialog;
import PaDialogs.PaSpecialDialog;
import PaDialogs.PaSpecialDialog.DialogType;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventInt;
import PaForms.PaAlbumTreeNode;
import PaGlobal.PaGuiTools;
import PaGlobal.PaDesktopPane;
import PaGlobal.PaUtils;
import PaLong.PaCopyToAlbumTask;
import static PaGlobal.PaUtils.*;
import static PaGlobal.PaLog.*;


/**
 * Viewer for filter results over all albums
 * @author avd
 */
public class PaViewFiltr extends JDialog implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private int column = PaUtils.get().getSettings().getColumnsAmount();
	
	private int rows = 0;
	
	private JPanel panel;
	private JPanel panelGrid;
	
	private PaPopupMenu menu =null;
	
	private JFrame jFrame = null;

	private final String EDIT_COMMAND = "1";
	
	private final String CREATE_ALBOM_COMMAND = "2";
	
	private final String MOVE_TO_ALBOM_COMMAND = "3";
	
	private final String PROP_COMMAND = "4";
	private final String DELETE_COMMAND = "5";
	
	private final String SELECT_COMMAND = "6";
	
	private final String DESELECT_COMMAND = "7";
	
	private final String EDIT_FILTER_ONLY_COMMAND = "8";
	
	private final String SELECT_BOOKMARKS_COMMAND = "9";
	
	private final String DESELECT_BOOKMARKS_COMMAND = "10";

	private int flag = 0;
	
	private PaSelectedImages selectedPhoto;
	
	private PaViewPhotosForm viewPhotosForm;
	
	private PaImageContainer _photoContainer = null;
	
	private ArrayList<PaViewPhotosForm> containerViewPhotoForm;

	public PaViewFiltr (JFrame jfrm, PaImageContainer container, int number ) {
		
		super (jfrm, getGuiStrs("filterDialogCaptionName")+"   "+getGuiStrs("filterDialogImagesCaptionName")+"  "+Integer.toString(number), true); //"Результат применения фильтра :"
		
		setLayout(new BorderLayout()); 
		
		jFrame = jfrm;
		
		JPanel panelRight = PaGuiTools.createVerticalPanel();		
		
		JPanel flowPanel = new JPanel (new FlowLayout());
				
		panelRight.add(Box.createVerticalGlue());
		
		panelRight.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panelRight.add(flowPanel);
		
		panelRight.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		add(panelRight, BorderLayout.EAST);
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		panel.setBackground(Color.WHITE);
		
		selectedPhoto = PaUtils.get().getSelectedImages();	
		
		containerViewPhotoForm = new ArrayList<PaViewPhotosForm>();
		
		setData(container);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		
		add (scrollPane, BorderLayout.CENTER);	
		
		setBounds(32, 32, PaDesktopPane.getDefaultWidth() - 64, PaDesktopPane.getDefaultHeight() - 64);

		menu = new PaPopupMenu();
		
		scrollPane.addMouseListener(new PopupMouseAdapter());

	}
	
	public ArrayList<PaViewPhotosForm> get_ContainerViewPhotoForm() {
		
		return containerViewPhotoForm;
	}
	
	public int getClosedFlagValue () {
		
		return flag;
	}

	public void onEdit (ActionEvent e) {

	}
	
	public void onDelete (ActionEvent e) {

		for (PaViewPhotosForm viewForm : selectedPhoto.getList()) {		
			
			containerViewPhotoForm.remove(viewForm);
		}
		
		panelGrid.removeAll();
		
		selectedPhoto.clear();
		
		for (PaViewPhotosForm viewForm : containerViewPhotoForm) {	
			
			panelGrid.add(viewForm);
		}
		
		panelGrid.updateUI();
	}	
	
	public void onApply (ActionEvent e) {

	}	
	
	public void onCancel (ActionEvent e) {
		
		flag = 0;
		
		dispose();
	}	

	public JPanel get_Panel() {
		
		return panel;
	}
	
	public void setData (PaImageContainer photoContainer) {
		
		_photoContainer = photoContainer; 
		
		if (_photoContainer != null) {
			
			int scale = PaUtils.get().getSettings().getPhotoScale();

			panelGrid = new JPanel( new GridLayout( rows,column,20,20));
			
			panelGrid.setBackground(Color.white);
			
			panelGrid.removeAll();
			
			panel.removeAll();
			
			containerViewPhotoForm.clear();

			if (_photoContainer.size() > 0) {
				
				Iterator<PaImage> iter = _photoContainer.getList().iterator();
				
				PaImage photo;
				
				while (iter.hasNext()) {
					
					photo = iter.next();
					
					viewPhotosForm = new PaViewPhotosForm(photo, scale);
					
					viewPhotosForm.setBlockForAllInfoLabel(true); //block of info label changes - no sense to allow the user to do it
					
					viewPhotosForm.getFormPanel().addMouseListener(new PopupMouseAdapter());
						
					containerViewPhotoForm.add(viewPhotosForm);
					
					panelGrid.add(viewPhotosForm);
				}
				
				panel.add(panelGrid);
				
				Thread thread = new Thread(this);
				
				thread.start();
				
			} else {
				
				panel.removeAll();

				writeLog(getGuiStrs("viewPanelCaptionAlbomEmpty"),null, true, true, false) ; //"Альбом пуст.");
			}	
		} else {
			
			panel.removeAll();
		}
	}
		

	@Override
	public void run() {
	
		try {
			Iterator<PaViewPhotosForm> iterView = containerViewPhotoForm.iterator();
			
			PaViewPhotosForm viewForm = null;
			
			if(containerViewPhotoForm.isEmpty()) {
				
				writeLog(getMessagesStrs("filterAllAlbomsResultsEmpty"),null, true, true, false) ;
				
				return;
			}
	
			while (iterView.hasNext()) {
				
				viewForm = iterView.next();
				
				ImageIcon icon = new ImageIcon (
						PaAlgoTransform.getScaledImage(new ImageIcon(viewForm.getImage().getFullPath()).getImage(),
						viewForm.m_widthIcon, viewForm.m_heightIcon));
				
				viewForm.getIconLabel().setIcon(icon);
				
				panel.repaint();
			}
		}
		catch (OutOfMemoryError e) {
		
			writeLog("Filter view : run() -> OutOfMemory :"+NEXT_ROW +e.getMessage(),null,
					true, false, true) ;
		}		
	}


	class PaPopupMenu extends JPopupMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JMenuItem itemEdit =null;
		
		private JMenuItem itemEditF =null;
		
		private JMenuItem itemDel =null;
		
		private JMenuItem itemNewAlbom =null;
		
		private JMenuItem itemMove =null;
		
		private JMenuItem itemProp =null;
		
		private JMenuItem itemSelectAll =null;
		
		private JMenuItem itemDeSelect =null;
		
		private JMenuItem itemDeBookmark =null;
		
		private JMenuItem itemBookmark =null;
		
		public PaPopupMenu() {
					
			MenuListener listener = new MenuListener() ;
			
			itemSelectAll = new JMenuItem(getMenusStrs("selectAllInFilterMenuName"));
			
			itemSelectAll.setActionCommand(SELECT_COMMAND);
			
			itemSelectAll.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "paselect.png"));
			
			itemSelectAll.addActionListener(listener);
			
		    add(itemSelectAll);
		    
		    itemDeSelect = new JMenuItem(getMenusStrs("deselectAllInFilterMenuName"));
		    
		    itemDeSelect.setActionCommand(DESELECT_COMMAND);
		    
		    itemDeSelect.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "padeselect.png"));
		    
		    itemDeSelect.addActionListener(listener);
		    
		    add(itemDeSelect);
			
			addSeparator();
			
			itemBookmark = new JMenuItem(getMenusStrs("selectBookmarkInFilterMenuName"));
			
			itemBookmark.setActionCommand(SELECT_BOOKMARKS_COMMAND);
			
			itemBookmark.addActionListener(listener);
			
		    add(itemBookmark);
		    
			itemDeBookmark = new JMenuItem(getMenusStrs("deselectBookmarkInFilterMenuName"));
			
			itemDeBookmark.setActionCommand(DESELECT_BOOKMARKS_COMMAND);
			
			itemDeBookmark.addActionListener(listener);
			
		    add(itemDeBookmark);
			
		    addSeparator();
			
			itemEdit = new JMenuItem(getMenusStrs("editInFilterMenuName"));
			
			itemEdit.setActionCommand(EDIT_COMMAND);
			
			itemEdit.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "paeditphoto.png"));
			
			itemEdit.addActionListener(listener);
			
		    add(itemEdit);
		    
			itemEditF = new JMenuItem(getMenusStrs("editInFilterOnlyMenuName"));
			
			itemEditF.setActionCommand(EDIT_FILTER_ONLY_COMMAND);

			itemEditF.addActionListener(listener);
			
		    add(itemEditF);
		    
		    itemDel = new JMenuItem(getMenusStrs("deleteInFilterMenuName"));
		    
		    itemDel.setActionCommand(DELETE_COMMAND);
		    
		    itemDel.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "pafilterdel.png"));
		    
		    itemDel.addActionListener(listener);
		    
		    add(itemDel);
		    
		    itemNewAlbom = new JMenuItem(getMenusStrs("createAlbomInFilterMenuName"));
		    
		    itemNewAlbom.setActionCommand(CREATE_ALBOM_COMMAND);
		    
		    itemNewAlbom.addActionListener(listener);
		    
		    itemNewAlbom.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "paselectnewalbom.png"));
		    
		    add(itemNewAlbom);
		    
		    itemMove = new JMenuItem(getMenusStrs("moveToAlbomInFilterMenuName"));
		    
		    itemMove.setActionCommand( MOVE_TO_ALBOM_COMMAND);
		    
		    itemMove.addActionListener(listener);
		    
		    itemMove.setIcon( new ImageIcon(PaUtils.get().getIconsPath() + "pacopytoalbom.png"));
		    
		    add(itemMove);
		    
		    itemProp = new JMenuItem(getMenusStrs("propetiesInFilterMenuName"));
		    
		    itemProp.setActionCommand(PROP_COMMAND);
		    
		    itemProp.addActionListener(listener);
		    
		    add(itemProp);
			
		}
		
		
		public void setItemsEnabled() {
			
			ArrayList<PaImage> x = getSelectedItems();
			
			itemEdit.setEnabled(true);
			
			itemDel.setEnabled(true);		
			
			itemNewAlbom.setEnabled(true);
			
			itemMove.setEnabled(true);	
			
			itemProp.setEnabled(true);
			
			if ( x.size() == 0 ) {
				
				itemEdit.setEnabled(false);
				
				itemDel.setEnabled(false);
				
				itemNewAlbom.setEnabled(false);
				
				itemMove.setEnabled(false);
				
				 itemProp.setEnabled(false);
		
				 return;
				
			}
			
			if ( x.size() > 1 ) {
				
				itemEdit.setEnabled(false);
				
				itemProp.setEnabled(false);
				
				return;
				
			}
		}
		
	}
	
	
	private class MenuListener implements ActionListener {
		
		public MenuListener() {}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
			switch(e.getActionCommand()) {
			
				case  SELECT_COMMAND : {
					
					selectAll(true);
					
					break;
				}
				case  DESELECT_COMMAND : {
					
					selectAll(false);
					
					break;
				}
				case EDIT_COMMAND : {
					
					editImage();
					
					break;				
				}
				case EDIT_FILTER_ONLY_COMMAND : {
					
					editImageInFilterView();
					
					break;
				}
				case DELETE_COMMAND : {
					
					deleteImagesFromFilterList();
					
					break;
				}
				case CREATE_ALBOM_COMMAND : {
					
					createNewAlbum();
					
					break;
				}
				case MOVE_TO_ALBOM_COMMAND : {
					
					moveToAlbum();
					
					break;
				}
				case SELECT_BOOKMARKS_COMMAND : {
					
					setBookmarksForSelected(true) ;
					
					break;
				}
				case DESELECT_BOOKMARKS_COMMAND : {
					
					setBookmarksForSelected(false) ;
					
					break;
				}
				case PROP_COMMAND : {
					
					getProperties();
					
					break;
				}
				
				default : {}
			
			};
		}
	}
	
	
	private void selectAll(boolean flag) {
		
		Iterator<PaViewPhotosForm> it = containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x =null;
		
		while(it.hasNext()) {
			
			x = it.next();

			x.selectBackgroundPanels(flag);			
		
		}
		
	}

	private void deleteImagesFromFilterList() {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		if ( list.size()  == 0 ) { 
		
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
    		
			return;
			
		}
		
		Iterator<PaImage > it = list.iterator();
		
		while(it.hasNext() ) {
			
			
			 _photoContainer.removePhoto(it.next().getId());
			
		}
		
		setData(_photoContainer);
		
		 setTitle(getGuiStrs("filterDialogCaptionName")+"   "+getGuiStrs("filterDialogImagesCaptionName")+"  "
		 +Integer.toString( _photoContainer.getSize()) );
	}
	
	
	
	
	/**
	 * <p>Edits the image from the filter view and performs real edit in album</p>
	 */
	private void editImage() {
		
		PaViewPhotosForm viewForm = getFirstSelectedItem();
		
		if ( viewForm == null ) {
			
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			
			return;
		}
		
		PaImage photoSelect = viewForm.getImage();
		
		PaImageEditDialog dialogEdit = new PaImageEditDialog(jFrame, getGuiStrs("editPhotoDialogCaption"), photoSelect,_photoContainer);
		
		 dialogEdit.setId( photoSelect.getId());
	
		
		dialogEdit.setVisible(true);
		
		//we remembered the real id in the auxiliary value; the current id of the photoSelect 
		//is totally new value and belongs to the current filter view form
		Integer Id = photoSelect.getAuxiliaryId();

		Date date_ph = stringToDate(dialogEdit.getImageDate(),GUI_DATE_FORMAT);
			
		if ( date_ph == null ) date_ph=new Date();
			

			PaImage im = new PaImage(Id, dialogEdit.getImageName(), dialogEdit.getImagePath(),
					dialogEdit.getSubjectsList(), date_ph);

			im.setBookmarked(dialogEdit.isBookmarked());
			
			im.setPrinted(dialogEdit.isPrinted());
			
			im.setComments(dialogEdit.getComments());
			
			PaImageContainer cont = PaUtils.get().getMainContainer().getPhotoContainerForPhoto(Id);
			
			if ( cont != null && cont.editImage(im,Id) == true) {
	
				//viewForm.init();
				//refresh view panel if the edit took place in current container
				if( PaUtils.get().getMainContainer().getCurrentContainer().getId() == cont.getId() ) {
					
					PaViewPhotosForm vForm =PaUtils.get().getViewPanel().findImageObject(Id);
					
					if ( vForm != null ) {
						
						vForm.init();
					}
				}

				writeLog(getMessagesStrs("valueWasEdited"),null, true, true, false) ;
				
				if(PaUtils.get().getMainContainer().isCurrent(cont.getId())) {
					PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
					PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
				}
				
			} else {
				
				writeLog(getMessagesStrs("noSuchValueExistsForEdit"),null, true, true, false) ;
			}
			
		
		}
	
	/**
	 * <p>Sets bookmarks for all selected image in the filter view</p>
	 */
	private void setBookmarksForSelected(boolean flag) {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		ArrayList<PaViewPhotosForm> listForms = getSelectedForms();
		
		if ( list.size()  == 0 ) { 
			
			writeLog(getMessagesStrs("messageNoSelectedItems"),null, true, true, false) ;
			
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
    		
			return; 
		}
		
		String s;
		
		if(flag) {
			
			s = getMessagesStrs("bookmarkSelectInFilterQuestion");
		}
		else {
			
			s = getMessagesStrs("rebookmarkSelectInFilterQuestion");
		}
		
		PaSpecialDialog dialog = new PaSpecialDialog(jFrame, DialogType.YES_NO_OPTION,
				 getMessagesStrs("messageAnswerCaption"), 
				 s,true,JOptionPane.YES_OPTION,
				 getGuiStrs("bookmarkSelectInFilterTooltip"), 3);
		
		dialog.setVisible(true);
		
		int n = dialog.getCloseFlag();
		
		if ( n == JOptionPane.NO_OPTION) {	return; }
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		boolean currentAlbomWasChanged = false;
		
		try {	
			
			int currentLoadedAlbumId = PaUtils.get().getMainContainer().getCurrentContainerId();
			
			
			PaMainConainer mainCont = PaUtils.get().getMainContainer();
			
			ArrayList<PaImageContainer> contList = mainCont.getList();
			
			ListIterator<PaImageContainer> it = contList.listIterator();
			
			//refresh image object in the albums
			while(it.hasNext()) {
				
				PaImageContainer contAl = it.next();
	
				ListIterator<PaImage> it1 = list.listIterator();
				
				while(it1.hasNext()) {
					
					PaImage im = it1.next();
					
					PaImage foundImage = contAl.getImage(im.getAuxiliaryId());
					
					if(foundImage != null) {
						
						foundImage.setBookmarked(flag);
						
						im.setBookmarked(flag);
						
						if(currentLoadedAlbumId == contAl.getId()) {
							
							currentAlbomWasChanged = true;
						}
					}
				}
				
			}
			
			//refresh visible forms in the view panel
			ListIterator<PaViewPhotosForm> it2 =  listForms.listIterator();
			
			while(it2.hasNext()) {
				
				it2.next().init();
			}
		
		}
		finally {
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		if (currentAlbomWasChanged) {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
		}
			
		PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
		
			
	}
	

	/**
	 * <p>Edits the image from the filter view and performs edit only in the filter view
	 * This operation doesn't change the album - source of the image</p>
	 */
	private void editImageInFilterView() {
		
		PaViewPhotosForm viewForm = getFirstSelectedItem();
		
		if ( viewForm == null ) {
			
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			
			return;
		}
		
		PaImage photoSelect = viewForm.getImage();
		
		PaImageEditDialog dialogEdit = new PaImageEditDialog(jFrame, getGuiStrs("editPhotoDialogCaption"), photoSelect,_photoContainer);
		
		 dialogEdit.setId( photoSelect.getId());
	
		
		dialogEdit.setVisible(true);
		
		Integer Id = photoSelect.getId();

		Date date_ph = stringToDate(dialogEdit.getImageDate(),GUI_DATE_FORMAT);
			
		if ( date_ph == null ) date_ph=new Date();
			

			PaImage im = new PaImage(Id, dialogEdit.getImageName(), dialogEdit.getImagePath(),
					dialogEdit.getSubjectsList(), date_ph);

			im.setBookmarked(dialogEdit.isBookmarked());
			
			im.setPrinted(dialogEdit.isPrinted());
			
			im.setComments(dialogEdit.getComments());
			
			if ( _photoContainer != null && _photoContainer.editImage(im,Id) == true) {
				
				viewForm.init();
					
				writeLog(getMessagesStrs("valueWasEdited"),null, true, true, false) ;
				
			} else {
				
			
				writeLog(getMessagesStrs("noSuchValueExistsForEdit"),null, true, true, false) ;
			}
			
		
		}
	 /**
	 * <p>Creates new album for selected items</p>
	 */
	private void  createNewAlbum() {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		if ( list.size()  == 0 ) { 
			
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			return; 
		}
		
		//creates list of all albums
		PaAlbumContainer albomContainer = PaUtils.get().getAlbumContainer();
		
		ArrayList<PaAlbumTreeNode> parentsList = new ArrayList<PaAlbumTreeNode>();
		
		PaAlbumTreeNode rootNode = new PaAlbumTreeNode();
		
		rootNode.setAlbomName(PaUtils.getAlbomsRootName());
		
		rootNode.setId(PaUtils.ALBUM_TOP_PARENT_ID);
		
		parentsList.add(rootNode);
		
		for(PaAlbum al: PaUtils.get().getAlbumContainer().getAlbums()) {
			
			PaAlbumTreeNode node = new PaAlbumTreeNode();
			
			node.setAlbomName(al.getName());
			
			node.setId(al.getId());
			
			parentsList.add(node);
		}
		
		PaAlbumNewDialog dialog = new PaAlbumNewDialog(parentsList,jFrame, albomContainer,0,
				getGuiStrs("newAlbomDialogCaptionName"));
	
		dialog.setVisible(true);
		
		String newText=dialog.getAlbomName();
		
		if (dialog.getClosedFlagValue() == 1) {
						
			PaAlbum albomToAdd = new PaAlbum (newText, dialog.getCommentAlbum(), 
					dialog.getRootPath(), dialog.getDate() , dialog.getFolderName());
			albomToAdd.setParentId(	dialog.getParentAlbomId());
			
			int currentCursorType = PaUtils.get().getCurrentCursor();
			
			PaUtils.get().setCursor(currentCursorType,Cursor.WAIT_CURSOR);
			
			try {
				
						
				int id = albomContainer.addAlbum(albomToAdd);
				
				if ( id != -1) {
					
						
						PaImageContainer cont = PaUtils.get().getMainContainer().getContainer(id);
						
						cont.copyImagesWithControl(list);
						
						PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
						
						if(dialog.isAlbomShouldBeLoaded()) {
							
							PaUtils.get().getMainContainer().setCurrentLoadedContainer(cont);
						
						}
											
						PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
						
						PaEventDispatcher.get().fireCustomEvent(event);
						
						writeLog(getMessagesStrs("newAlbomWasAdded"), null, true, true, false);
						
				} 
				else {
					
					writeLog(getMessagesStrs("newAlbomWasNotAdded"),null, true, true, false);
				
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
	* <p>Copies selected images to other album.
	* The function works for the case then images were filtered over all albums</p>
	*/
	private void  moveToAlbum() {
		
		ArrayList<PaImage> list = getSelectedItems();
		
		if ( list.size()  == 0 ) {
			
	   		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			return; 
		}
	
		PaImageMoveDialog dialog = new PaImageMoveDialog(jFrame,PaUtils.get().getAlbumContainer());
		
		dialog.setVisible(true);	
		

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
	
	private void  getProperties() {
		
		
		PaViewPhotosForm viewForm = getFirstSelectedItem();
		
		if ( viewForm != null ) {
			
			PaImage photoSelect = viewForm.getImage();
			
			PaImagePropertiesDialog propertiesDialog = new PaImagePropertiesDialog (jFrame, 
					getGuiStrs("propertyPhotoDialogCaption"), photoSelect,-1 ); 		
	
			propertiesDialog.setVisible(true);
			
		}
		else {
			
    		JOptionPane.showMessageDialog( jFrame,
    				getMessagesStrs("messageNoSelectedItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	
	
	/**
	 * 
	 * @return the firm which is a first in the selected list
	 */
	public PaViewPhotosForm getFirstSelectedItem() {
		
		Iterator<PaViewPhotosForm> it = containerViewPhotoForm.iterator();
		
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
	 * 
	 * @return the list of selected image objects
	 */
	public ArrayList<PaImage> getSelectedItems() {
		
		ArrayList<PaImage> list = new ArrayList<PaImage>();
		
		Iterator<PaViewPhotosForm> it = containerViewPhotoForm.iterator();
		
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
	 * 
	 * @return the list of selected image forms
	 */
	public ArrayList<PaViewPhotosForm> getSelectedForms() {
		
		ArrayList<PaViewPhotosForm> list = new ArrayList<PaViewPhotosForm>();
		
		Iterator<PaViewPhotosForm> it = containerViewPhotoForm.iterator();
		
		PaViewPhotosForm x = null;
		
		while(it.hasNext() ) {
			
			x = it.next();
			
			if ( x.isSelected() ) {
				
				list.add(x);
			}
		}
		return  list;
	}
	
	private class PopupMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {

			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {

			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			
			if (e.isPopupTrigger()) {
				
				menu.setItemsEnabled();
				
				menu.show(e.getComponent(),e.getX(), e.getY());
				
			}
		}
	}
}
