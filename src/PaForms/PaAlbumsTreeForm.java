
package paforms;

import static paglobal.PaLog.*;
import static paglobal.PaUtils.GUI_DATE_FORMAT;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getGuiStrs;
import static paglobal.PaUtils.getMessagesStrs;
import static paglobal.PaUtils.stringToDate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import paactions.PaActionsMngr;
import pacollection.PaAlbum;
import pacollection.PaAlbumContainer;
import pacollection.PaImage;
import pacollection.PaImageContainer;
import pacollection.PaMainConainer;
import padialogs.PaAlbumFindDialog;
import padialogs.PaAlbumNewDialog;
import padialogs.PaAlbumPropDialog;
import padialogs.PaAlbumsDelDialog;
import padialogs.PaAlbumsMergeDialog;
import padialogs.PaAlbumsMoveDialog;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paglobal.PaButtonsGroup;
import paglobal.PaCloseFlag;
import paglobal.PaUtils;
import palong.PaMoveToStandardTask;
import paundoredo.PaUndoRedoDeque;

/**
 * @author Andrii Dashkov
 * <p>This panel supports album tree view and all operations around it</p>
 */
public class PaAlbumsTreeForm extends JPanel implements TreeSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	   private JTree m_tree;
	   
	   private JScrollPane m_scrollPanel;
	   
	   private PaAlbumTreeNode m_root;
	   
	   private PaAlbumPopupMenu m_popupMenu;
	   
	   private ArrayList<PaAlbumTreeNode> m_nodes;
	   
	   private PaButtonsGroup m_buttonGroup;
	 
	   private PaAlbumContainer m_cont = PaUtils.get().getAlbumContainer();
	   
	   private PaMainConainer m_mainContainer = PaUtils.get().getMainContainer();
	   
	   private JFrame m_mainWindow = PaUtils.get().getMainWindow();
	   
	   private boolean m_customIconsEnabled = PaUtils.get().getSettings().isCustomIconsEnabled();
	   
		{
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_REFRESH_EVENT, this, "refreshAlbumsForm");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_PASTE_EVENT, this, "pasteBufferInSelectedAlbum");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_NEW_ICON_EVENT, this, "refreshAlbumIcon");
					
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_NEW_EVENT, this, "addAlbom");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_EDIT_EVENT, this, "editAlbum");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_DEL_EVENT, this, "delAlbum");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_MOVE_EVENT, this, "moveAlbum");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_MERGE_EVENT, this, "mergeAlbums");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ALBUM_FIND_EVENT, this, "findAlbum");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.REFRESH_EVENT, this, "refreshView");
			
			PaEventDispatcher.get().addConnect(PaEventDispatcher.MOVE_TO_STANDARD_FOLDER, this, "moveImagesToStandardFolder");
		}
	   
	   
	   public PaAlbumsTreeForm() 
	   {
		   m_nodes = new ArrayList<PaAlbumTreeNode>();
		   
		   createGui();
	   }
		/** 
		 * <p>Creates all gui elements of  albom's tree form</p>
		 */
	   private void createGui()
	   {
			setLayout(new BorderLayout());
			
			setToolTipText(getGuiStrs("albomTreeToolTipText"));
			
			m_root = new PaAlbumTreeNode(PaUtils.getAlbomsRootName());
			
			m_root.setId(PaUtils.ALBUM_TOP_PARENT_ID);
			
			m_root.setAlbumName(PaUtils.getAlbomsRootName());
			
			m_nodes.add(m_root);
			
			m_tree = new JTree(m_root);
			
			m_scrollPanel = new JScrollPane(m_tree);
			   		   
			m_popupMenu = new PaAlbumPopupMenu();
			
			m_popupMenu.add_mouseAdapter( m_tree);
			
			m_popupMenu.add_mouseAdapter( m_scrollPanel);
				
			ImageIcon ic = new ImageIcon(PaUtils.get().getIconsPath() + "paalbomtableicon.png");
			
			m_buttonGroup = new PaButtonsGroup("  "+ getGuiStrs("albomListTableCaption") + " " + m_cont.size(), ic); 
			
			add(m_buttonGroup.getMainPanel(), BorderLayout.NORTH);
			
			add(m_scrollPanel, BorderLayout.CENTER);
			
			m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			
			m_tree.addTreeSelectionListener(this);	
			
			Font f = PaUtils.get().getLargeFont();
			m_tree.setFont(f);
			
			init();
							   	
	   }

		/** 
		 * <p>Inits the main album's tree view</p>
		 */
	   public void init()
	   {	   
		   	m_root.removeAllChildren();
		   	
		   	DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();
		   	
		   	model.reload();
		
		   	m_nodes.clear();
		   	
			m_nodes.add(m_root);
			
			m_tree.setCellRenderer(new PaTreeCellRenderer());
		   
			Iterator<PaAlbum> iter = m_cont.iterator();
			
	 		PaAlbum x;
	 		
	 		while (iter.hasNext ()) {
	 			
	 			x = iter.next();
	 			
	 			if (x.getParentId() == PaUtils.ALBUM_TOP_PARENT_ID) {
	 				
	 				PaAlbumTreeNode top = new PaAlbumTreeNode(x.getName());
	 				
	 				top.setId(x.getId());
	 				
	 				top.setAlbumName(x.getName());
	 				
	 				m_nodes.add(top);
	 				
	 				buildBranch(top,m_cont);
	 				
	 				m_root.add(top);
	 			}
	 		}
	 		
	 		for (int i = 0; i < m_tree.getRowCount(); ++i) {
	 			
	 		    m_tree.expandRow(i);
	 		}
	 		
	 		m_tree.addMouseListener(new MouseList());
	   }
		/** 
		 * <p>Recursive function to build branch of the albums tree</p>
		 * @param root - root node of the branch
		 * @param cont - album's container with data
		 */
	   public void buildBranch(PaAlbumTreeNode root, PaAlbumContainer cont)
	   {
		   ArrayList<Integer> listIds = cont.findChildsForParentId(root.getId());
		   
		   for(int i=0; i < listIds.size(); ++i)
		   {
			   PaAlbum al = cont.getAlbum(listIds.get(i));
			   
			   PaAlbumTreeNode child = new PaAlbumTreeNode(al.getName());
			   
			   child.setId(al.getId());
			   
			   child.setAlbumName(al.getName());
			   
			   root.add(child);
			   
			   m_nodes.add(child);
			   
			   buildBranch(child, cont);
		   }	   
	   }
		/** 
		 * <p>Reloads all nodes in the albom's tree from album's container. Posts the events to control access to GUI elements</p>
		 * @param e - refresh event
		 */
		public void refreshAlbumsForm (PaEvent e) {
			
			if (e.getEventType() != PaEventDispatcher.ALBUM_REFRESH_EVENT  ) { return; }
			
			init();
			
			if (m_cont.size() > 0) {
				
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.ALBUM_LIST_IS_NOT_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
			} else {
				
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.ALBUM_LIST_IS_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event);
			}
			
			m_buttonGroup.setMainText("  "+ getGuiStrs("albomListTableCaption") + " " + m_cont.size());
			
		}
		
		/** 
		 * <p>Inserts the images from the buffer in the selected albom. The selected albom can no be current albom</p>
		 * @param e - refresh event
		 */
	    public void pasteBufferInSelectedAlbum ( PaEvent e) {
	    	
	    	if (e.getEventType() != PaEventDispatcher.IMAGE_PASTE_EVENT ) { return; }
	    		 	
			 	PaAlbumTreeNode node = (PaAlbumTreeNode)m_tree.getLastSelectedPathComponent();
			 	
		    	if ( node != null ) {
		    		
		    		int n = JOptionPane.showConfirmDialog(		
		    			    PaUtils.get().getMainWindow(),
		    			    getMessagesStrs("messagePaseIntoSelectedAlbomQuestion")+NEXT_ROW+
		    			    getMessagesStrs("messagePaseIntoSelectedAlbomQuestion1")+NEXT_ROW+
		    			    getMessagesStrs("selectedAlbomMessageCaption")+" "+ 
		    			    PaUtils.get().getAlbumContainer().getAlbum(node.getId()).getName(),
		    			    getMessagesStrs("messageAnswerCaption"),
		    			    JOptionPane.OK_CANCEL_OPTION);
		    		
		    		if ( n == JOptionPane.OK_OPTION) {
			    		PaImageContainer p= PaUtils.get().getMainContainer().getContainer(node.getId());
			    		ArrayList<PaImage> listBuffer = PaUtils.get().getCopyPhotoBuffer();
			    		int sz = listBuffer.size();
			    		
			    		p.pasteInBuffer(listBuffer);
			    		
		
			    		writeLog(getMessagesStrs("imagesWereCopiedFromBuffer")+sz, null, true, true, true);
			    		
			    		writeLogOnly("Paste from buffer: images "+sz +" id albom = "+p.getId()+NEXT_ROW+" buffer has been cleaned",null);
			    	
		    		}
		    	}
		    	else {
		    		
					JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
		    				getMessagesStrs("messageNoSelectedAlbomForBufferOperation"),
		    			    getMessagesStrs("messageInfoCaption"),
		    			    JOptionPane.INFORMATION_MESSAGE);
		    		
		    	}
	    }
	    
		/**
		 * <p>Refreshes album icon</p>
		 * @param event
		 */
		public void refreshAlbumIcon (PaEvent event ) 
		{
			
			int id = PaUtils.get().getMainContainer().getCurrentContainer().getId();
			
			if ( id != -1 ) {
				
				setSelected(id);
				
			}
		}
		
	    
		public void setSelected(int id) 
		{			
			PaAlbumTreeNode node = find(id);
			
			if ( node != null ) {
				
				m_tree.getSelectionModel().clearSelection();
				
				m_tree.addSelectionRow(node.getIndex(node));
				
			}
			
		}
		
		public PaAlbumTreeNode find(int id)
		{
			for(int i=0; i < m_nodes.size(); ++i) {
				
				PaAlbumTreeNode node = m_nodes.get(i);
				
				if(node.getId() == id ) {
					
					return  node;
				}
			}
			return null;
			
		}


		@Override
		public void valueChanged(TreeSelectionEvent e) 
		{
			
			int sz = getSelectedAlbomsIds().size();
			
			switch (sz) {
			
				case 0: {			
					
					PaEventDispatcher.get()
						.fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.ALBUM_SELECTION_EMPTY));
					
					break;
				}
				
				case 1: {
					
					PaEventDispatcher.get()
						.fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.ALBUM_SELECTED_1));	
					
					int id = getSelectedId();
					
					String name = PaUtils.get().getAlbumContainer().getAlbum(id).getName();
					
					writeInfoOnly(getMessagesStrs("selectedAlbumOneMessage") + " "+ name);
					
					break;
				}
				
				case 2: {		
					
					PaEventDispatcher.get()
							.fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.ALBUM_SELECTED_2));
						writeInfoOnly(getMessagesStrs("selectedAlbumMultiMessage") + " "+ sz);
						
					break;
				}
			}
			
			if(sz > 2 ) { 
				
				PaEventDispatcher.get().fireCustomEvent(
					new PaEventEnable(PaEventEnable.TYPE.ALBUM_MULTI_SELECTION));
				
				writeInfoOnly(getMessagesStrs("selectedAlbumMultiMessage") + " "+ sz);
			}
		}
		
		/**
		 * <p>Invokes the add album dialog window and performs adding operation after that</p>
		 */
		public void addAlbum(PaEvent event) {
			
			if ( event.getEventType() !=  PaEventDispatcher.ALBUM_NEW_EVENT ) { return; }
			try {	
				
				PaAlbumNewDialog dialog = new PaAlbumNewDialog(m_nodes,m_mainWindow, 
						m_cont,0,getGuiStrs("newAlbomDialogCaptionName"));

				ArrayList<Integer> l = getSelectedAlbomsIds(); 
				
				if(l.size() == 1) {
					
					if(l.get(0) == PaUtils.ALBUM_TOP_PARENT_ID) {
						
						dialog.setParentComboItem(PaUtils.getAlbomsRootName());
						
					}
					else {
						
						dialog.setParentComboItem(m_cont.getAlbum(l.get(0)).getName());
					}
				}
				else {
					
					dialog.setParentComboItem(PaUtils.getAlbomsRootName());			
				}
			
				dialog.setVisible(true);
				
				String newText = dialog.getAlbomName();
				
				if (dialog.getClosedFlagValue() == 1) {
					
					Date date_alb = dialog.getDate();
					
					boolean albomShouldBeLoaded = dialog.isAlbomShouldBeLoaded();
					
					if ( date_alb == null ) { date_alb=new Date(); }
					
					PaAlbum al = new PaAlbum (newText, dialog.getCommentAlbum(), 
							dialog.getRootPath(), date_alb, dialog.getFolderName());
					
					al.setParentId(dialog.getParentAlbomId());
					
					int id = m_cont.addAlbum(al);
					
					writeLogOnly("Album adding: An albom with id = " + id +" has been created" +
							NEXT_ROW+"folder name :" + al.getFolderName() + NEXT_ROW +
							"path : " + al.getFullStandardPath(), null);
					
					if ( id != -1) {
						try {
		
							PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
							
							if(albomShouldBeLoaded) {
								
								PaUtils.get().getMainContainer().setCurrentLoadedContainer(
										PaUtils.get().getMainContainer().getContainer(id));
								
								PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.REFRESH_EVENT) );
								
								//reset after the new album is loaded
								PaUndoRedoDeque.get().clearAll();
							}
							
							setSelected(id);
							
							//alboum creation creates folders and files - we should save all data after this
							PaActionsMngr.get().getAction("paactionsave").actionPerformed(null);
								
							writeLog(getMessagesStrs("newAlbomHasAdded")+ " : " + newText, null, true, true, true);
							
							writeLogOnly("Album adding: the operation is finished id = " + id, null);
							
						} catch (Exception e0) {
							
							writeLog("Exception :  " + NEXT_ROW, e0, true, false, true);
						}
						
						
					} 
					else {
						
						writeLog(getMessagesStrs("valueExists"), null, true, true, true);
					}	
				}
				else {
					
					writeLogOnly("Album adding: the operation is canceled", null);
				}
			}
			finally {
				
				PaUtils.get().resetCursor(Cursor.DEFAULT_CURSOR);
			}
		}
		
		
		/**
		 * <p>Invokes the edit albom dialog window and performs edit operation after that</p>
		 */
		public void editAlbum(PaEvent event) {

			if (event.getEventType() != PaEventDispatcher.ALBUM_EDIT_EVENT ) { return; }
			
			try {
				
				int[] selected = m_tree.getSelectionRows();
				
				if(selected.length == 0 || selected.length > 1) {
					
					writeInfoOnly(getGuiStrs("noAlbomSelectionForOper")); 
				}
				
			
				int id =  getSelectedId();
				
				if( id == PaUtils.ALBUM_TOP_PARENT_ID) return;
				       
				PaAlbumNewDialog dialog =new PaAlbumNewDialog(m_nodes,m_mainWindow, m_cont, id
						,getGuiStrs("albomEditDialogCaptionName"));
				
				
				//sets info for parent item
				int idParent =m_cont.getAlbum(id).getParentId();
				
				if(idParent == PaUtils.ALBUM_TOP_PARENT_ID) {
					
					dialog.setParentComboItem(PaUtils.getAlbomsRootName());			
				}
				else {
					
					dialog.setParentComboItem(m_cont.getAlbum(idParent).getName());
				}
				
				dialog.setVisible(true);
				
				String name_alb = dialog.getAlbomName();
				
				String path_alb = dialog.getRootPath();
				
				String coment_alb = dialog.getCommentAlbum();
				
				String folder_alb = dialog.getFolderName();
				
				int parent_id = dialog.getParentAlbomId();	
				
				if(parent_id == id) {//strange situation when id of parent = id of myself - not acceptable
					
					parent_id = PaUtils.ALBUM_TOP_PARENT_ID;
				}
				
				Date date_alb = stringToDate(dialog.getDateAsString(), GUI_DATE_FORMAT);
				
				if ( date_alb == null ) { date_alb = new Date(); }
				
				if (dialog.getClosedFlagValue() == 1) {	
						
					PaAlbum _albom = new PaAlbum( name_alb, coment_alb, path_alb, date_alb,folder_alb, id);
					
					_albom.setParentId(parent_id);
					
					if (m_cont.editAlbum(_albom)== false) {
						
						writeLog(getMessagesStrs("valueExists"), null, true, true, true);
					} 
					else {
						try {
						
							writeLog(getMessagesStrs("valueWasEdited"), null, true, true, true);
							
						}  catch (Exception e0) {
							
							writeLog("Exception :  " + NEXT_ROW, e0, true, false, true);
						}
					}
				}
			}  catch (IndexOutOfBoundsException e3) {
				

				writeLog(getMessagesStrs("listIsEmpty"), null, true, true, true);
			}
		}
		
		/**
		 * <p>Invokes the delete dialog window and performs delete operation after that</p>
		 */
		public void delAlbum(PaEvent event) throws CloneNotSupportedException 
		{
	            if (event.getEventType() == PaEventDispatcher.ALBUM_DEL_EVENT) {
	            	
	    			int id =  getSelectedId();
	    			
					if(id == -1) return;
				
	            	PaAlbumsDelDialog dialog = new PaAlbumsDelDialog(m_mainWindow, m_cont.getAlbum(id).getName());
	            	
					dialog.setVisible(true);
					
				    ALBOMS_CLEAR_TYPES flag = dialog.getClosedFlagValue();
					
					if(flag != ALBOMS_CLEAR_TYPES.NO_OPERATION) {		
						
						deleteAlbum(flag, dialog.isChildAlbomsInvolved(), id);
					}
				
	            }			
		}
		
		/**
		 * <p>Deletes the album with Id_albom.</p>
		 * @param flag - type of delete operation 
		 * @see ALBOMS_CLEAR_TYPES
		 * @param childsAlBomsInvolved - true if you want to involve all childs alboms into operation
		 * @param Id_albom - alboms id which will be deleted
		 */
		protected void deleteAlbum(ALBOMS_CLEAR_TYPES flag, boolean childsAlBomsInvolved, Integer Id_albom) 
				throws CloneNotSupportedException
		{
			
			String albomPath = m_cont.getAlbum(Id_albom).getFullStandardPath();
			
		
			ArrayList<Integer> childs = m_cont.getAllChildAlbumsIds(Id_albom);
			
			boolean childAlbomLoaded = m_mainContainer.hasCurrent(childs);
			
			boolean currentAlbomLoaded = m_mainContainer.isCurrent(Id_albom);
			
			childsAlBomsInvolved &= !childs.isEmpty();//no sense to start operations for children
			
			if (currentAlbomLoaded || (childsAlBomsInvolved && childAlbomLoaded)) {
					
					if(!showQuestionAboutLoadedAlbum(flag))  {  return; }
			}

			try {
				
				switch(flag) {
						
					case CLEAR_LINKS_ONLY:	// cleaning without deletion of the image files
					{			
						m_mainContainer.getContainer(Id_albom).clearImages();
						
						if(childsAlBomsInvolved) {
							
							m_mainContainer.clearContainers(childs);
						}
						
						writeLog(getMessagesStrs("allAlbomPhotosWereDeleted"), null, true, true, true);
						
						PaUndoRedoDeque.get().clearAll();
						
						break;
					}
						
					case CLEAR_WITH_FILES:	//cleaning with deletion of the image files from the standard folder without deletion of standard folder itself
					{							
						m_mainContainer.getContainer(Id_albom).clearImages();	
				
						PaUtils.get().deleteDirChilds(albomPath,
								getMessagesStrs("albomAndImagesFilesWereDeleted"),
								getMessagesStrs("albomAndImagesFilesWereNotDeleted"));
						
						if(childsAlBomsInvolved) {
							
							m_mainContainer.clearContainers(childs);
							
							for(int i: childs) {
								
								String path = m_cont.getAlbum(i).getFullStandardPath();
								
								//delete only childs without root folder 
								PaUtils.get().deleteDirChilds(path,
										getMessagesStrs("albomAndImagesFilesWereDeleted"),
										getMessagesStrs("albomAndImagesFilesWereNotDeleted"));
							}	
						}
			
						writeLog(getMessagesStrs("allAlbomPhotosAndImagesWereDeleted"), null, true, true, true);
						
						PaUndoRedoDeque.get().clearAll();
						
						break;
					}
					
					case DELETE_WITHOUT_FILES:	// remove album without files deletion
					{
						
						if(childsAlBomsInvolved) {

							if(m_cont.remove(Id_albom)) //after this call the childs albums are reparented
							{							
								for(int i: childs){
									
									m_cont.remove(i);							
								}
								
								PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT));
								
								PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
												
								writeLog(getMessagesStrs("albomWasDeletedMessage"), null, true, true, true);
								
								PaUndoRedoDeque.get().clearAll();
							}
						}
						else {
							
							if(m_cont.removeAlbum(Id_albom)) {
								
								setDataSourse();			
								
								writeLog(getMessagesStrs("albomWasDeletedMessage"), null, true, true, true);	
								
								PaUndoRedoDeque.get().clearAll();
							}
						}
			
						PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT));
						
						break;
					}
					
					case DELETE_WITH_FILES:	// total remove with physical deletion
					{	
						
						// only images in the standard albom's path will be deleted							
						if(childsAlBomsInvolved) {

							if(m_cont.remove(Id_albom)) //after this call the childs albums are reparented
							{	
								
								PaUtils.get().deleteNotEmptyDir(albomPath,
										getMessagesStrs("albomAndImagesFilesWereDeleted"),
										getMessagesStrs("albomAndImagesFilesWereNotDeleted"));
								
								for(int i: childs){
									
									String path = m_cont.getAlbum(i).getFullStandardPath();
									
									if(m_cont.remove(i)) {	
										
										//delete all folder
										PaUtils.get().deleteNotEmptyDir(path,
												getMessagesStrs("albomAndImagesFilesWereDeleted"),
												getMessagesStrs("albomAndImagesFilesWereNotDeleted"));
									}
								}
								
								PaEventDispatcher.get().fireCustomEvent(  new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT) );
								
								PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
								
								writeLog(getMessagesStrs("albomWasDeletedMessage"), null, true, true, true);
								
								PaUndoRedoDeque.get().clearAll();
							}
						}
						else 
						if(m_cont.removeAlbum(Id_albom)) {
						
							//delete all folder
							//control of included folder; if they exist, don't delete the folder
							File dir = new File(albomPath);
							
							File[] list = dir.listFiles();
							
							boolean hasInnerFolders = false;
							
							for(File f: list) {
								
								if(f.isDirectory() ) { hasInnerFolders = true; }
							}
							
							if(!hasInnerFolders) {
								
								PaUtils.get().deleteNotEmptyDir(albomPath,getMessagesStrs("albomAndImagesFilesWereDeleted"),
									getMessagesStrs("albomAndImagesFilesWereNotDeleted"));
							}
							else {
								
				        		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),getMessagesStrs("standFolderHasInnerFolder"),
				        				getMessagesStrs("messageInfoCaption"),JOptionPane.INFORMATION_MESSAGE);
							}
										
							PaUndoRedoDeque.get().clearAll();
						}
						
						PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.ALBUM_REFRESH_EVENT));					
						
						break;
					}
					case NO_OPERATION: {	break; }
					default: { break; }					
				}
			} catch (IndexOutOfBoundsException e3) {
	
				writeLog(getMessagesStrs("listIsEmpty"), e3, true, true, true);
			}	
			finally {		
	
				if(currentAlbomLoaded) { //if we worked with loaded album we should refresh main view panel
					
					PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
				}
				
				PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SAVE_EVENT) );
				
			}
		
		}	

		/**
		 * <p>Shows dialog with question about album that is loaded (current) </p>
		 * @param flag - type of  operation delete or clear, it is used for defining of test message 
		 * @see ALBOMS_CLEAR_TYPES
		 * @return false if the answer is no (operation should be skipped)
		 */
		private boolean showQuestionAboutLoadedAlbum(ALBOMS_CLEAR_TYPES flag) 
		{		
			String s = getMessagesStrs("deleteLoadedAlbomQuestion");
			
			switch(flag) {
			
				case CLEAR_LINKS_ONLY:	
					
				case CLEAR_WITH_FILES: {
					
					s = getMessagesStrs("clearLoadedAlbomQuestion");
					
					break;
				}
				default:
			};
			
			int n = JOptionPane.showConfirmDialog(
				    PaUtils.get().getMainWindow(),
				    s,
				    getMessagesStrs("messageAnswerCaption"),
				    JOptionPane.YES_NO_OPTION);
			
			if ( n == JOptionPane.NO_OPTION) { return false;}
			
			return true;
		}
		
		/**
		 * <p>Invokes the move dialog window and performs move operation after that. Is used with event dispatcher framework</p>
		 */
		public void moveAlbum(PaEvent event) 
		{

			if (event.getEventType() != PaEventDispatcher.ALBUM_MOVE_EVENT ) { return; }
			
			
			int id = getSelectedId();
			
			if(id == -1 ) return;
			
			PaAlbumsMoveDialog dialog = new PaAlbumsMoveDialog(m_mainWindow, m_cont,
					m_cont.getAlbum(id).getName(),id);
			
			dialog.setVisible(true);
			
			
			if(dialog.isOkClosedFlag()) 
			{	
				PaAlbum albom = m_cont.getAlbum(dialog.getAlbomId());
				
				String oldFolderName = albom.getFolderName();
				
				String oldAlbomStandartPath = albom.getFullStandardPath();

				albom.setRootPathAndFolder(dialog.getPathText()); 
				
				PaUtils.get().getMainContainer().reallocImages(id, albom.getFullStandardPath(),oldFolderName,oldAlbomStandartPath, 
						dialog.isFolderDelete());		
			}
		}
		
		
		
		/**
		 * <p>Invokes the merge dialog window and performs merge operation after that. Is used with event dispatcher framework</p>
		 */
		public void mergeAlbums(PaEvent event) {
			
			if ( event.get_EventType() != PaEventDispatcher.ALBUM_MERGE_EVENT ) { return; }
			
			ArrayList<Integer> listIds = getSelectedAlbomsIds();
			
			if ( listIds.size() > 1 ) {
				
				PaAlbumsMergeDialog dialog = new PaAlbumsMergeDialog(m_mainWindow,m_cont,listIds);
				
				dialog.setVisible(true);
				
				if(dialog.getFlag() == PaCloseFlag.OK ){
					
					m_cont.mergeAlbums(listIds, dialog.resultId(), dialog.isCopyFiles());
				}
				
			}
			else {
				
				writeLog(getMessagesStrs("multiSelectionNeedMerge"), null, true, true, true); 
			}
		}
		
		/**
		 * <p>Returns the list with ids of albums which are selected in the tree, but without
		 * root item</p>
		 * @return list of albom's ids
		 */
		public ArrayList<Integer> getSelectedAlbomsIds() 
		{
			
			ArrayList<Integer> list = getSelectedIds();
			
			for(int i =0 ; i < list.size(); ++i) {
				
				if(list.get(i) == PaUtils.ALBUM_TOP_PARENT_ID) {
					
					list.remove(i);
					
					break;
				}
			}	
			
			return list;
		}
		
		/**
		 * <p>Returns the list with ids of albums which are selected in the tree</p>
		 * @return list of album's ids
		 */
		private ArrayList<Integer> getSelectedIds() 
		{
			
			ArrayList<Integer> list = new ArrayList<Integer>();
					
	      	TreePath[] selectedPaths = m_tree.getSelectionPaths();
	      	
	    	if(selectedPaths == null) {
	    		
	    		writeInfoOnly(getGuiStrs("noAlbomSelectionForOper")); 
	    		
	    		return list;
	    	}
	   
	    	for(TreePath p: selectedPaths) {
	    		
	    		PaAlbumTreeNode selectedNode =
	    				((PaAlbumTreeNode)p.getLastPathComponent());
	    		
	    		if(selectedNode != null) {
	    			
	    			list.add(selectedNode.getId());
	    		}
	    	}
				return list;
		}
		/**
		 * <p>Opens alboms find dialog.The function is in the event framework, connected through dispatcher.</p>
		 * @param event - find event
		 */
		public void findAlbum(PaEvent event) {

			if (event.getEventType() != PaEventDispatcher.ALBUM_FIND_EVENT ) { return; } 
			
			PaAlbumFindDialog findDialog = new PaAlbumFindDialog (m_mainWindow, this);
			
			findDialog.setVisible(true);	
		}
		
		/**
		 * <p>Refreshes images for currently loaded album.
		 * The function is in the event framework, connected through dispatcher.</p>
		 * @param event - refresh event
		 */
		public void refreshView (PaEvent event) throws CloneNotSupportedException {		

			if ( event.getEventType() != PaEventDispatcher.REFRESH_EVENT ) { return; }
			
			if (PaUtils.get().getMainContainer().get_size() > 0) {
 
				int Id =  getSelectedId();
				
				if(Id == -1) return;
				
				if (m_mainContainer.getCurrentContainer() != null && 
										Id == m_mainContainer.getCurrentContainer().getId()) {
					
				
					m_mainContainer.setCurrentLoadedContainer(m_mainContainer.getContainer(Id));
					
					PaUtils.get().getMainLabel().setText("");
					
					
				} else {
					
								
						if (PaActionsMngr.get().getAction("paactionsave").isEnabled() == false) {

							PaEventInt eventEnable = new PaEventEnable(PaEventEnable.TYPE.ALBUM_SELECTED_1);
							PaEventDispatcher.get().fireCustomEvent(eventEnable);
							
						} else {
							
							int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),
								    getMessagesStrs("confirmPrevAlbomChanges"), 
								    getMessagesStrs("messageAnswerCaption"),
								    JOptionPane.YES_NO_OPTION);
							
							if ( n == JOptionPane.YES_OPTION) {
							
								PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SAVE_EVENT) );
								
								PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.ALBUM_SELECTED_1));
								
								PaEventDispatcher.get().fireCustomEvent( new PaEventEnable(PaEventEnable.TYPE.DATA_SAVED));
								
							}
						}
					
					
					
					m_mainContainer.setCurrentLoadedContainer(m_mainContainer.getContainer(Id));	
					
					PaUtils.get().getMainLabel().setText("");
					
					//reset all undo and redo after albom's change
					PaUndoRedoDeque.get().clearAll();
					
				}
			} else {
				
				m_mainContainer.setCurrentLoadedContainer(null);
				
				PaUtils.get().getMainLabel().setText(getMessagesStrs("listIsEmpty"));
			}

		}
		
		/**
		 * Sets the data source for the album tree
		 */
		public void setDataSourse() {
			
			if(m_cont == null) {
				
				writeLog("Null pointer for albom container", null, true, false, true);
				
				return;
			}
			
			init();
			
			if (m_cont.size() > 0) {
				
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.ALBUM_LIST_IS_NOT_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event);
				
			} else {
				
				PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.ALBUM_LIST_IS_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event);			
			}
		}
		
		/**
		 * <p>Gets selected id in the tree</p>
		 * @return -1 if there is no selection
		 */
		public int getSelectedId() 
		{	
	      	TreePath selectedPath = m_tree.getSelectionPath();
	      	
	    	if(selectedPath == null) {
	    		
	    		writeLog(getGuiStrs("noAlbomSelectionForOper"), null, true, true, true);
	    		
	    		return -1;
	    	}
	    	
			PaAlbumTreeNode selectedNode = ((PaAlbumTreeNode)selectedPath.getLastPathComponent());
			
			if(selectedNode == null || selectedNode.isRoot()) return -1;
			
			return selectedNode.getId();
		}
		
		public void openPropertyDialog()
		{
			int id  = getSelectedId();
			
			if(id == -1) { 
				
				return;
			}
			
			Set<String> list = PaUtils.get().getMainContainer().getContainer(id).getUsedSubjectsList();
			
			PaAlbum al = m_cont.getAlbum(id);
			
			PaAlbumPropDialog dialog = new PaAlbumPropDialog(m_mainWindow,al, 
					m_cont.getChildsCount(al.getId()),list);
			
			dialog.setVisible(true);
		}
		/**
		 * <p>Gets button group which is on the top of albom's form</p>
		 */
		public PaButtonsGroup getButtonGroup() {
			
			return m_buttonGroup;
		}
		
		/**
		 * @param valueForSeach - string value for search operation
		 * @param findResults - container for results of find operation
		 */
		public void findRows(String valueForSeach,HashSet<Integer> findResults)
		{
			
			findResults.clear();
			
			for ( int i=0; i < m_tree.getRowCount(); ++i  ) {
						
		      	TreePath p = m_tree.getPathForRow(i);
		    
				PaAlbumTreeNode node = ((PaAlbumTreeNode)p.getLastPathComponent());
						
				int Id = node.getId();
				
				if ( Id != PaUtils.ALBUM_TOP_PARENT_ID &&  m_cont.getAlbum(Id).getName().contains(valueForSeach) ) {
					
					 findResults.add(i);
				}	
			}
		}
		
		public void setRowSelected(Integer index) 
		{
			m_tree.setSelectionInterval(index,index);
		}
		
		/**
		 * <p>Ensures visibility of TreeNode with index</p>
		 * @param index of tree node that should be moved into visible part of the scroll area (view port)
		 * 
		 */
		public void ensureVisible(Integer index)
		{
			TreePath pt = m_tree.getPathForRow(index);
			
		    PaAlbumTreeNode n = (PaAlbumTreeNode) pt.getLastPathComponent();
		    
		    if(n == null ) {
		    	
		    	writeLog("Can't find an albom treenode with index "+index+" to ensure visibility", null, true, false, true);
		    	
		    	return;
		    }
		    
			Rectangle r = m_tree.getRowBounds(index);
				
			if(isVisibleInViewport(r)) return;
			
			Point p = new Point(r.getLocation());
			
		    //function scrollVisible... works awfully - we use scrollbars directly
			m_scrollPanel.getVerticalScrollBar().setValue(p.y);
			
			if(!isVisibleInViewport(r)) {
				
				m_scrollPanel.getHorizontalScrollBar().setValue(p.x);
			}
	
		}
		
	    /**
	     * @param r- rectangle to check visibility in a viewport of scroll pane
	     * @return true if the rectangle is entirely  in the viewport
	     */
	    private boolean isVisibleInViewport(Rectangle r)
	    {
	    	Rectangle viewPortImage = new Rectangle(m_scrollPanel.getViewport().getLocationOnScreen(),
	    			m_scrollPanel.getViewport().getSize());
	    	
	    	return viewPortImage.contains(r);
	    	
	    }
		
		
		/**
		 * <p>Custom render class for tree items</p>
		 * @class PaTreeCellRenderer - custom render class
		 */
		class PaTreeCellRenderer extends DefaultTreeCellRenderer {

			private static final long serialVersionUID = 1L;

			public PaTreeCellRenderer() {
				
				super();
			}
			
			

		    public Component getTreeCellRendererComponent(
		                        JTree tree,
		                        Object value,
		                        boolean sel,
		                        boolean expanded,
		                        boolean leaf,
		                        int row,
		                        boolean hasFocus) {

		        super.getTreeCellRendererComponent(
		                        tree, value, sel,
		                        expanded, leaf, row,
		                        hasFocus);
		        
		        PaAlbumTreeNode node = ( PaAlbumTreeNode) value;
		        
		        int id = node.getId();
		        
		        if( id != PaUtils.ALBUM_TOP_PARENT_ID) {
		        	
		        	
			        PaAlbum al = m_cont.getAlbum(id);
			        
			        if(al != null) {
			        	
				        String name = al.getName();
				        
				        Date dt = al.getDate();
				          
						if(m_customIconsEnabled) { 
							
					        Border border = BorderFactory.createEmptyBorder ( 3, 3, 3, 3 );
					        
					        setBorder(border);
					        
							setIcon(al.getIcon()); 
						}
						
						this.setText(name + "     " + PaUtils.dateToString(dt,PaUtils.GUI_DATE_FORMAT));
						
						setToolTipText(getGuiStrs("albomTreeNodeTooltip"));
			        }
			        else {
			        	
			        	writeLog("Can't render the tree item for albom with id = "+id, null, true, false, true);	
			        }
			
		        }

		        return this;
		    }

		}
		
		/**
		 * <p>Types of album delete operation. The enum is used in the delete dialog.</p>
		 */
		public enum ALBOMS_CLEAR_TYPES {
			
   			CLEAR_LINKS_ONLY,        
   			
	        CLEAR_WITH_FILES,     
	        
	        DELETE_WITHOUT_FILES,   
	        
	        DELETE_WITH_FILES,
	        
	        NO_OPERATION
		};
		
		/**
		 * <p>Moves all linked images to standard folder for selected albom</p>
		 */
		public void moveImagesToStandardFolder(PaEvent e) 
		{
			if(e.getEventType()!= PaEventDispatcher.MOVE_TO_STANDARD_FOLDER) return;
			
			int id = getSelectedId();
			
			if(id == -1) {
				
				writeLog(getMessagesStrs("moveAllLinksNoSelection"),null,true,true,true);
				
				return;
			}
			
			PaAlbum al = PaUtils.get().getAlbumContainer().getAlbum(id);
			
			int n = JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),		    
				    getMessagesStrs("moveAllLinksIntoStandard")+NEXT_ROW+
				    getMessagesStrs("moveAllLinksSelectedAlbom") + " " +
		    		al.getName()+NEXT_ROW+
		    		getMessagesStrs("moveAllLinksStandardPath") + " " +al.getFullStandardPath(),
				    getMessagesStrs("moveAllLinksIntoStandardCaption"), 
				    JOptionPane.OK_CANCEL_OPTION);
			
			if ( n == JOptionPane.OK_OPTION) {
			
			
				ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
					getMessagesStrs("moveToStandardOperationCaption"),
					getMessagesStrs("moveToStandardOperationNote"), 0, 100);
				
					
				progressMonitor.setMillisToDecideToPopup(0);
				
				progressMonitor.setMillisToPopup(0);
				
				//copy images operation is potentially long, we start it using the SwingWorker 
				PaMoveToStandardTask ts = new PaMoveToStandardTask(progressMonitor,id, null);
				
				ts.execute();
			}
		}
		/**
		 * Listener to catch the mouse double click event and start the album edit operation
		 * @author Andrii Dashkov
		 *
		 */
		private class MouseList implements MouseListener {


			@Override
			public void mouseClicked(MouseEvent e) {
			
					if(e.getClickCount() == 2) {
						
						int id = getSelectedId();
						
						if(id != -1) {	
							
							PaActionsMngr.get().getAction("paalbomactionedit").actionPerformed(null);
						}
					}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
		
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
		}
}