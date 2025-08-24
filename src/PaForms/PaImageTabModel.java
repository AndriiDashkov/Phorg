
package paforms;

import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import pacollection.PaFilterInfo;
import pacollection.PaImage;
import pacollection.PaImageContainer;
import padialogs.PaImageDelDialog;
import padialogs.PaImageEditDialog;
import padialogs.PaImageNewDialog;
import padialogs.PaImagePropertiesDialog;
import padialogs.PaImagesCopyInDialog;
import padialogs.PaImagesFilterDialog;
import padialogs.PaImagesGroupNewDialog;
import padialogs.PaSpecialDialog;
import padialogs.PaImagesFilterDialog.PaFilterType;
import padialogs.PaSpecialDialog.DialogType;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventPhotoFilter;
import paevents.PaEventPhotoGroupNew;
import paevents.PaEventSelect;
import paglobal.PaCloseFlag;
import paglobal.PaLog;
import paglobal.PaUtils;
import paimage.PaViewPhotosForm;
import palong.PaAllAlbumsFilterApply;
import paundoredo.PaAddCommand;
import paundoredo.PaDelCommand;
import paundoredo.PaEditCommand;
import paundoredo.PaUndoRedoDeque;

/**
 * 
 * @author Andrii Dashkov
 *
 */
public class PaImageTabModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_NEW_EVENT, this, "addImage");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_EDIT_EVENT, this, "editImage");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_DEL_EVENT, this, "deleteImage");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.GROUP_NEW_EVENT, this, "addImagesGroup");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.COPY_IN_EVENT, this, "onCopyIn");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_FILTER_EVENT, this, "activateFilter");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.IMAGE_PROP_EVENT, this, "onProperties");
	}

	private ArrayList<PaImage> _modelPhotos;
	
	private PaImageContainer _phpc; 
	
	private PaImagesFilterDialog filterDialog;
	
	private PaImageDelDialog dialog_del;
	
	private PaImagesGroupNewDialog newDialogGroup;
	
	private PaImagesCopyInDialog copyInDialog;
	
	private PaImagePropertiesDialog propertiesDialog;

	private JTable m_table;
	
	private final int columnId = 1;
	
	private ImageIcon printedIcon= new ImageIcon(PaUtils.get().getIconsPath() + "paprintlabel.png");
	
	private ImageIcon printedIconBW= new ImageIcon(PaUtils.get().getIconsPath() + "paprintlabelbw.png");
	
	private ImageIcon bookIcon = new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabel.png");
	
	private ImageIcon bookIconBW= new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabelbw.png");
	
	private JFrame m_mainWindow = PaUtils.get().getMainWindow();
	
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	
	public PaImageTabModel() {
		
		_modelPhotos = new ArrayList<PaImage>();

	}
	
	public void setDataSourse (PaImageContainer photoContainer) {
		
		PaUtils.get().getSelectedImages().clear();	
		
		_modelPhotos.clear();
		
		if (PaUtils.get().getMainContainer().getCurrentContainer() != null) {		
			
			if (PaUtils.get().getMainContainer().getCurrentContainer().size() > 0) {			
			
				ArrayList<PaImage> list=  photoContainer.getList();
				
				for(Iterator<PaImage> iter = list.iterator(); iter.hasNext();){	
					
					_modelPhotos.add(iter.next());
				}
				
				PaEventEnable event_01 = new PaEventEnable(5);
				
				event_01.setType(PaEventEnable.TYPE.IMAGE_LIST_NOT_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event_01);	
				
			} else {
				
				PaEventEnable event_02 = new PaEventEnable(4);
				
				event_02.setType(PaEventEnable.TYPE.IMAGE_LIST_IS_EMPTY);
				
				PaEventDispatcher.get().fireCustomEvent(event_02);
			}
		} else {
			
			PaEventEnable event_03 = new PaEventEnable(6);
			
			event_03.setType(PaEventEnable.TYPE.SELECT_CONTAINER_IS_EMPTY);
			
			PaEventDispatcher.get().fireCustomEvent(event_03);
			
		}
		
		_phpc = photoContainer;
	}
	
	public PaImageContainer getPhotoContainer () {
		
		return _phpc;
	}

	public void addTableModelListener(TableModelListener listener) {
		
	   	listeners.add(listener);
	}

	public boolean isCellEditable(int row, int col) {

		return col == 5;
    }
	
	public void removeTableModelListener(TableModelListener listener) {
		
	   	listeners.remove(listener);
	}
	
	public int getColumnCount() {	
		
		return 8;
	}

	public int getRowCount() {

		return _modelPhotos.size();
	}
	

	public String getColumnName (int column) {
		
		switch (column) {  
		
	        case 0: return "�";  
	        
	        case 1: return "id";  
	        
	        case 2: return "";  
	        
	        case 3: return ""; 
	        
	        case 4: return getGuiStrs("photoTableColumnName");
	        
	        case 5: return getGuiStrs("photoTableColumnDate");  
	        
	        case 6: return getGuiStrs("photoTableColumnPath");
	
	        case 7: return getGuiStrs("photoTableColumnSubList");        

        }  
		return "";
	}
	
	public Class<?> getColumnClass (int column) {
		
		switch (column) {
		
			case 0: return Integer.class;
			
			case 1: return Integer.class;
			
			case 2: return Integer.class;
			
			case 3: return Integer.class;
			
			case 4: return String.class;
			
			case 5: return String.class;
			
			case 6: return String.class;
			
			case 7: return ArrayList.class;

			default: return Object.class; 
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {  
		
		try {
			
	        PaImage photo =_modelPhotos.get(rowIndex);   

	        switch (columnIndex) {  
	        
        	case 0: return rowIndex;
        	
        	case 1: return photo.getId(); 
        	
        	case 2: {
        		
        		if ( photo.isPrinted() ) {
        			
        			return 1;
        		}
        		else {
        			
        			return 0;
        		}
       	
        	}
        	case 3:  {
        		
        		if ( photo.isBookmarked() ) {
        			
        			return 1;
        		}
        		else {
        			return 0;
        		}
        	}
        	 
        	case 4: return photo.getName();
        	
        	case 5: return dateToString(photo.getDate(),GUI_DATE_FORMAT);
        	
        	case 6: return photo.getFullPath();
        	
        	case 7: return photo.getSubjectsList(); 
    
	       }  
	       return null;
	       
		} catch (ArrayIndexOutOfBoundsException e1) {

			writeLog("ArrayIndexOutOfBoundsException :  " + NEXT_ROW, e1, true, false, true);
		}
		
		return columnIndex;
	}
	
	public PaImage get_RowPhoto (int rowIndex) {
		
		return _modelPhotos.get(rowIndex);
	}
	
	public int findIndexforId(int id) {
			
		int endIndex = getRowCount();
		 
		for( int rowIndex=0; rowIndex < endIndex; ++rowIndex) {
			
			if ( get_RowPhoto(rowIndex).getId() == id ) {
				
				return rowIndex;
			}
		}
			 
		return -1;
	}
    
	public void set_photoTable (JTable t) {
		
		m_table = t;
		
		m_table.getColumnModel().getColumn(2).setCellRenderer(new DateRenderer(0));
		
		m_table.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer(1));
	}

	/**
	 * <p>Filter activation procedure, calls the filter dialog<p>
	 * @param eventFilter - catches this event through PaEventDispatcher
	 */
	public void activateFilter(PaEventPhotoFilter eventFilter) 
	{				
		if ( eventFilter.getType() == PaEventPhotoFilter.TYPE.CLEARFILTER ) {
			
			PaUtils.get().getMainContainer().getCurrentContainer().resetFilter();
			
			PaEvent e = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
			
			e.setEndMessage(getMessagesStrs("filterHasBeenCleared"));
			
			PaEventDispatcher.get().fireCustomEvent(e);
			
			return;
		}
				
		PaFilterInfo filtr = PaUtils.get().getMainContainer().getCurrentContainer().getFilter();
		
		if(filtr == null) { filtr = new PaFilterInfo(); }
		
		else {
			
			filtr = new PaFilterInfo(PaUtils.get().getMainContainer().getCurrentContainer().getFilter());
		}
		
		filterDialog = new PaImagesFilterDialog(m_mainWindow, filtr);
		
		filterDialog.setVisible(true);
		
		if (filterDialog.getClosedFlagValue() != PaFilterType.CANCEL) {
			
			PaFilterInfo fl = filterDialog.getFilter();
			
			if(fl.isAllIgnored()) {
				
				writeLog(getMessagesStrs("allIgnoredFilterOperation"), null, true, true, false);
		 		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
	    				getMessagesStrs("allIgnoredFilterOperationMessage"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
		 		
		 		return;
			}
	
		

			if (filterDialog.getClosedFlagValue() == PaFilterType.ALL_ALBOMS) {
				
		   		ProgressMonitor progressMonitor = new ProgressMonitor(PaUtils.get().getMainWindow(),
						getMessagesStrs("filterFromAllAlbomsCaption"),
						getMessagesStrs("filterFromAllAlbomsOperationNote"), 0, 100);
				
		   		
				progressMonitor.setMillisToDecideToPopup(0);
				
				progressMonitor.setMillisToPopup(0);
				
			
				//images operation is potentially long, we start it using the SwingWorker 
				PaAllAlbumsFilterApply ts = new PaAllAlbumsFilterApply(progressMonitor,fl,
						getMessagesStrs("filterAllAlbomsResultsFinished"));
				
				ts.execute();
					
			}  else if (filterDialog.getClosedFlagValue() == PaFilterType.SINGLE_ALBOM) {
					
					PaUtils.get().getMainContainer().getCurrentContainer().setFilter(fl);
					
					PaEvent ev = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
					ev.setEndMessage(getMessagesStrs("filterSingleBeenActivated"));
					
					PaEventDispatcher.get().fireCustomEvent(ev);
					
					PaUtils.get().getViewPanel().setFilterIconEnable(true);
					
					PaLog.writeLogOnly("PaPhotoTabModel.activateFilter for current albom " + NEXT_ROW +
					fl.toString(), null);
					
			} else if (filterDialog.getClosedFlagValue() == PaFilterType.CANCEL ) {
	
			
			}
		}
		
	}

	/**
	 * <p>Adds new image to the album</p>
	 * @param e - event (can be used to transfer a data)
	 */
	public void addImage (PaEvent e) {
		
		if (e.getEventType() != PaEventDispatcher.IMAGE_NEW_EVENT) { return; }
		
		 PaImageNewDialog dialogAdd = new PaImageNewDialog(m_mainWindow, getGuiStrs("newPhotoDialogCaption"), "", "", 
				 dateToString(new Date(),GUI_DATE_FORMAT ), new ArrayList<Integer>(),_phpc,-1, false);
		 
		dialogAdd.setVisible(true);
		
		Date dNew = stringToDate(dialogAdd.getImageDate(),GUI_DATE_FORMAT);

		if ( dNew == null) { dNew = new Date(); }

		if (dialogAdd.getClosedFlagValue() == PaCloseFlag.OK) {	

			
			PaImage newPhoto = new PaImage(dialogAdd.getImageName(), dialogAdd.getImagePath(),
					dialogAdd.getSubjectsList(), dNew);
			
			newPhoto.setBookmarked( dialogAdd.isBookmarked() );
			
			newPhoto.setPrinted( dialogAdd.isPrinted() );
			
			newPhoto.setComments( dialogAdd.getComments() );
			
			PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
			
			if (cont!= null && cont.add_Image(newPhoto, dialogAdd.isCopyFile() ) == true) {
				
				PaImage undoPhoto = newPhoto.cloneData();
				
				PaUndoRedoDeque.get().addUndo(new PaAddCommand(undoPhoto, cont.getId()));
				 
				writeLog(getMessagesStrs("valueWasAdded"), null, true, true, false); 
				
				PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT) );
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
					
			} else {
				
				writeLog(getMessagesStrs("valueExists"), null, true, true, false);
			}
		}
	}
	
	/**
	 * <p>Function calls the dialog for adding group of image, and after that close the dialog and does the operation </p>
	 * @param event - add image group event  (can be used to transfer a data)
	 * 
	 */
	public void addImagesGroup(PaEventPhotoGroupNew event) {

		newDialogGroup = new PaImagesGroupNewDialog(m_mainWindow, getGuiStrs("groupAddDialogCaption")); 

		newDialogGroup.setVisible(true);
		
		boolean flag= false;
		
		PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
		
		if(cont == null) {
			writeLog(getMessagesStrs("noContainerForGroupOperation"), null, true, true, false);
	 		JOptionPane.showMessageDialog( PaUtils.get().getMainWindow(),
    				getMessagesStrs("noContainerForGroupOperation"),
    			    getMessagesStrs("messageErrorCaption"),
    			    JOptionPane.ERROR_MESSAGE);
	 		
			return;
		}

		
		switch(newDialogGroup.getClosedFlagValue()) {
		
			case COPY_IMAGES_AS_LINKS: {
				
				flag = cont.loadImagesGroup (newDialogGroup.getSourcePath(), new String(), false,
							newDialogGroup.isRecurSelected()) ;
				break;
			}
			case COPY_IMAGES_IN_STANDARD: {
				
				flag = cont.loadImagesGroup (newDialogGroup.getSourcePath(), new String(), 
							true,newDialogGroup.isRecurSelected()) ;
				break;
			}
			case COPY_IMAGES_IN_SEL_FOLDER: {
				
				flag = cont.loadImagesGroup (newDialogGroup.getSourcePath(), 
								newDialogGroup.getPathNew(), true,newDialogGroup.isRecurSelected()) ;
				break;
			}
			
			default:
		}
		
		if ( !flag ) {
			
			writeLog(getMessagesStrs("valuesWereNotAdded"), null, true, true, false);
		}
	}
	
	/**
	 * <p>Function calls the dialog for edit image operation and does the edit changes </p>
	 * @param event - event of type PaEventDispatcher.IMAGE_EDIT_EVENT
	 * 
	 */
	public void editImage(PaEvent event) {	
		
		if ( event.getEventType() != PaEventDispatcher.IMAGE_EDIT_EVENT ) { return; }
		
		PaImage photoSelect = null;			
		
		ArrayList<PaViewPhotosForm> selectedList = PaUtils.get().getSelectedImages().getList();
		
		if(selectedList.size() == 0 ) {
			
			writeLog(getMessagesStrs("noSelectionToEdit"), null, true, true, true);
			
			return;	
			
		} else if(selectedList.size() > 1 ) {
			
			writeLog(getMessagesStrs("manySelectionsToEdit"), null, true, true, true);
			
			return;
			
		} else {
			
			photoSelect = selectedList.get(0).getImage();
		}
	
		PaImage photoForUndo = photoSelect.cloneData();
		
		PaImageEditDialog dialogEdit = new PaImageEditDialog(m_mainWindow, getGuiStrs("editPhotoDialogCaption"), photoSelect,_phpc);
		
		dialogEdit.setId( photoSelect.getId());
		
		dialogEdit.setVisible(true);
		
		Integer Id = photoSelect.getId();

		Date date_ph = null;
		
		boolean albumReloadRequest = false;

		if (dialogEdit.getClosedFlagValue() == PaCloseFlag.OK) {
			
		
			date_ph = stringToDate(dialogEdit.getImageDate(),GUI_DATE_FORMAT);
			
		
			PaImage _photo = new PaImage(Id, dialogEdit.getImageName(), dialogEdit.getImagePath(),
					dialogEdit.getSubjectsList(), date_ph);

			_photo.setBookmarked(dialogEdit.isBookmarked());
			
			_photo.setPrinted(dialogEdit.isPrinted());
			
			_photo.setComments(dialogEdit.getComments());
			
			_photo.setSortId(photoSelect.getSortId());
			
			PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
			
			if (cont != null && cont.editImage(_photo,Id) == true) {		
			
				 PaUndoRedoDeque.get().addUndo(
						 new PaEditCommand(photoForUndo, cont.getId()));
				
				writeLog(getMessagesStrs("valueWasEdited"), null, true, true, false);
				
			} else {
				
				writeLog(getMessagesStrs("valueExists"), null, true, true, false);
				
			}
			
			if(PaUtils.get().getMainContainer().isCurrent(cont.getId())) {
				
				PaSpecialDialog checkDialog = new PaSpecialDialog(m_mainWindow, DialogType.YES_NO_OPTION, 
						getMessagesStrs("messageAnswerCaption"),"<html>" + getMessagesStrs("reloadAlbumrequestAfterEdit")+
						"<br>"+ getMessagesStrs("reloadAlbumrequestAfterEdit2")+"</html>", 
						true, true, 1);
				
				checkDialog.setVisible(true);
				
				albumReloadRequest = (checkDialog.getCloseFlag() == JOptionPane.YES_OPTION);
				
				if(albumReloadRequest) {
					
					PaEventDispatcher.get().fireCustomEvent(new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT));
				}
				else {
					
					PaEventDispatcher.get().fireCustomEvent(new PaEventSelect());
				}
				
				PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
			}
		}
	}
	
	/**
	 * 
	 * @param e event
	 */
	public void deleteImage(PaEvent e) {
		
		if ( PaEventDispatcher.IMAGE_DEL_EVENT != e.getEventType() ) { return; }
		
			dialog_del = new PaImageDelDialog(m_mainWindow, getGuiStrs("deletePhotoDialogCaption")); 
					
			dialog_del.setVisible(true);
			
			ArrayList<PaImage> list = new ArrayList<PaImage>();
			
			for (PaViewPhotosForm viewPhotoForm : PaUtils.get().getSelectedImages().getList()) {
				
				list.add(viewPhotoForm.getImage());
			}
			
			int n = 0;
			
			switch(dialog_del.getClosedFlagValue()) {
			
				case CLEAR_ALBOM_SAVE_FILES: { 
					
					ArrayList<PaViewPhotosForm> viewPhotos = PaUtils.get().
							getSelectedImages().getList();

					PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
					
					if(cont != null && cont.removeImagesFromForms(viewPhotos, false) != 0) {
						
						PaUndoRedoDeque.get().addUndo(new PaDelCommand(list,cont.getId()));					
					}
					break;
				}
				
				case CLEAR_ALBOM_DELETE_FILES: {
					
					PaUtils.get().getMainContainer().getCurrentContainer().removeImagesFromForms(PaUtils.get().
							getSelectedImages().getList(), true);
					
					n =1;
					
					break;
				}
				case DELETE_ALBOM_SAVE_FILES: {
					
					n = PaUtils.get().getMainContainer().removeImageFromAllContainers (  list, false ); 
					
					break;
				}
				case DELETE_ALBOM_DELETE_FILES: {
					 n = PaUtils.get().getMainContainer().removeImageFromAllContainers (  list, true ); 
			
					break;
				}
				
				default:
					
				case CANCEL: return;
			
			}
			
			PaUtils.get().getSelectedImages().clear();
			
			writeLog(getMessagesStrs("deletedImagesMessage")+" " + n, null, true,true,true);
	
	}
	
	/**
	 * Opens copy dialog and then makes copy of selected image files into target folder
	 * @param eventPhoto- event to catch
	 */
	public void onCopyIn(PaEvent eventPhoto) {
		
		if ( eventPhoto.getEventType() != PaEventDispatcher.COPY_IN_EVENT ) { return; } 

		try {
			copyInDialog = new PaImagesCopyInDialog(m_mainWindow, getGuiStrs("copyInDialogCaption"));
			
			copyInDialog.setVisible(true);
			
			if (copyInDialog.getClosedFlagValue() == 1) {
				
				if (PaUtils.get().getSelectedImages().size() > 0) {
				
					if (Files.exists(Paths.get(copyInDialog.get_dirPhoto().getText()))) {
						
						int counter = 0 ;
						
						int negativeCounter = 0;
						
						for (PaViewPhotosForm viewPhotoForm : PaUtils.get().getSelectedImages().getList()) {
							
							boolean flag = copyImage(viewPhotoForm.getImage().getFullPath(), copyInDialog.get_dirPhoto().getText());
							
							if(flag) { ++counter;} else { ++negativeCounter; }
						}
						if(negativeCounter > 0) {
							
							writeLog(getMessagesStrs("numberFilesCopiedMessage") + " " + counter + "  " +
									getMessagesStrs("numberFilesNotCopiedMessage") + " " +negativeCounter +  NEXT_ROW, null, true, true, false);
						}
						else {
							
							writeLog(getMessagesStrs("numberFilesCopiedMessage") +counter + NEXT_ROW, null, true, true, false);
						}
					}
					else {
						
						writeLog(getMessagesStrs("targetFolderNotExistMessage") + NEXT_ROW, null, true, true, false);
					}
				}								
			}

		} catch (IndexOutOfBoundsException e21) {
			
			writeLog(getMessagesStrs("saveError"), e21, true, true, true);
		}		
	}
	
	/**
	 * Copies the file with full path inputFile to folder outDir
	 * @param inputFile - path to input file
	 * @param outDir - output directory
	 * @return true if the operation was successful
	 */
	public boolean copyImage (String inputFile, String outDir) { 
		
		File inFile = new File(inputFile);
		
		BufferedInputStream in = null;
		
		BufferedOutputStream out = null;
		
		boolean flag = true;
		
		try {
			
			in = new BufferedInputStream(new FileInputStream(inputFile));
			
			out = new BufferedOutputStream(new FileOutputStream(outDir + getSeparator() +inFile.getName()));
			
			int i;
			
			do {
				
				i = in.read();
				
				if (i != -1) out.write(i);
				
			} while (i != -1);
			
		} catch (FileNotFoundException e) {

			writeLog("FileNotFoundException :  " + NEXT_ROW, e, true, false, true);
			
			writeLog(getMessagesStrs("imageFileCannotFindMessage") + NEXT_ROW, null, true, true, false);
			
			flag  = false;
		}
		catch (IOException e) {
			
			writeLog("IOException :  " + NEXT_ROW, e, true, false, true);
			
			writeLog(getMessagesStrs("errorFileMessage") + NEXT_ROW, null, true, true, false);
			
			flag  = false;
		}
		finally {
			
			try {
				if(in != null) in.close();
				
				if(out != null) out.close();
				
			} catch (IOException e) {
				
				writeLog("IOException :  " + NEXT_ROW, e, true, false, true);
				
				flag = false;
			}
			
		}
		return flag;
	}
	
	public void onProperties(PaEvent eventPhoto) {

		if ( eventPhoto.getEventType() != PaEventDispatcher.IMAGE_PROP_EVENT ) { return; }
		
		PaImage photoSelect = null;	
		
		for (PaViewPhotosForm x : PaUtils.get().getSelectedImages().getList()) {
			
			photoSelect = x.getImage();
		}
		
		propertiesDialog = new PaImagePropertiesDialog (m_mainWindow, getGuiStrs("propertyPhotoDialogCaption"),
				photoSelect, PaUtils.get().getMainContainer().getCurrentContainer().getId()); 	
		
		propertiesDialog.setVisible(true);

	}
	

	public void findRows(String valueForSeach,HashSet<Integer> findResults)
	{
		
		findResults.clear();
		
		for ( int i=0; i < getRowCount (); ++i  ) {
			
			int Id = (Integer) getValueAt( m_table.convertRowIndexToModel(i), columnId);
			
			if (  _phpc.getImage(Id).getName().contains( valueForSeach ) ) {
				
				 findResults.add(i);
			}	
		}		
	}
	
	public void setRowSelected(Integer index) {
		
		 m_table.setRowSelectionInterval(index,index);
		 
		 m_table.scrollRectToVisible(m_table.getCellRect(index, 2,true));
	}
	

	class DateRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;
		
		private int type = 0; //0 - print icon , 1 - bookmark icon
		
		ImageIcon activeIcon = null;
		
		ImageIcon unActiveIcon = null;
		
	    public DateRenderer(int type  ) {
	    	
	    	super(); 
	    	
	    	this.type = type;
	    
	    	if ( this.type == 0 ) {
	    		
	    		activeIcon = printedIcon;
	    		
	    		unActiveIcon = printedIconBW;
	    		
	    	}
	    	else {
	    		
	    		activeIcon =  bookIcon;
	    		
	    	    unActiveIcon = bookIconBW;
	    	}
	    }

	    public void setValue(Object value) {
	    	
	    	if ( ( (int) value ) == 1 ) {
	    		
	    		setIcon(  activeIcon );
	    		
	    	}
	    	else {
	    			
	    		setIcon(  unActiveIcon );
	    	}
	    }
	     
	}	
}
