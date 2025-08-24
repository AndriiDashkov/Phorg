package paforms;


import static paglobal.PaLog.writeLog;
import static paglobal.PaUtils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import pacollection.PaImage;
import pacollection.PaImageContainer;
import pacollection.PaSubject;
import pacollection.PaSubjectContainer;
import padialogs.PaSubjectAddDialog;
import padialogs.PaSubjectDeleteDialog;
import padialogs.PaSubjectFindDialog;
import paevents.PaEvent;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paglobal.PaCloseFlag;
import paglobal.PaUtils;

public class PaSubjectsTabModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SUBJECT_NEW_EVENT, this, "add");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SUBJECT_EDIT_EVENT, this, "editTema");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SUBJECT_DEL_EVENT, this, "deleteSubject");
		
		PaEventDispatcher.get().addConnect( PaEventDispatcher.SUBJECT_FIND_EVENT, this, "findTema");
	}

	private PaSubjectContainer m_container = PaUtils.get().getSubjectsContainer();
	
	private ArrayList<PaSubject> m_modelSubjects;
	
	private PaSubjectAddDialog dialog;
	
	private PaSubjectDeleteDialog dialog_del;
	
	private JTable m_subjectsTable;	
	
	
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	
	private final int COLUMN_NUM = 0;
	
	public final static int COLUMN_ID = 1;
	
	private final int COLUMN_NAME = 2;
	
	public PaSubjectsTabModel () {
		
		m_modelSubjects = new ArrayList<PaSubject>();
	}

	/**
	 * <p>Sets data for model from container c</p>
	 * @param c - data container 
	 */
	public void setData (PaSubjectContainer c) {
		
		m_modelSubjects.clear();
		
		if (c.size() > 0) {
			
			for(PaSubject tema : c.get_tems_sort()) {
				
				m_modelSubjects.add(tema);
			}

			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SUBJECT_LIST_NOT_EMPTY));
			
		} else {
			
			PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SUBJECT_LIST_EMPTY));		
		}
		
	}

    public void addTableModelListener(TableModelListener listener) {
    	
    	listeners.add(listener);
    }

    public boolean isCellEditable(int arg0, int arg1) {
    	
    	return false;
    }

    public void removeTableModelListener(TableModelListener listener) {
    	
    	listeners.remove(listener);
    }


	public int getColumnCount() {
		
		return 3;
	}
	

	public int getRowCount () {		
		
		return m_modelSubjects.size();
	}
	
	public String getColumnName (int column) {
		
		switch (column) {  
		
			case COLUMN_NUM: return "�";  
			
	        case COLUMN_ID: return "id";  
	        
	        case COLUMN_NAME: return getGuiStrs("termTableColumnNameName");        
        }  
		return "";
	}

	public Class<?> getColumnClass (int column) {
		
		switch (column) {
		
		case COLUMN_NUM: return Integer.class;
		
		case COLUMN_ID: return Integer.class;
		
		case COLUMN_NAME: return String.class;

		default: return Object.class; 
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {  
   
        PaSubject tem = m_modelSubjects.get(rowIndex);

        switch (columnIndex) {  
        
        	case COLUMN_NUM: return rowIndex; 
        	
        	case COLUMN_ID: return tem.getId();  
        	
        	case COLUMN_NAME: return tem.getName();        
        }  
        return "";  
    } 
	
	public void set_myTable (JTable t) {
		
		m_subjectsTable = t;
	}

	/**
	 * <p>Invokes add subject dialog and performs add operation using event</p>
	 */
	public void add(PaEvent event) {
		
		if (PaEventDispatcher.SUBJECT_NEW_EVENT != event.getEventType() ) { return; }
		
		addSubject();
	}
	
	/**
	 * <p>Invokes add subject dialog and performs add operation</p>
	 */
	public void addSubject() {
		
		dialog = new PaSubjectAddDialog(PaUtils.get().getMainWindow(), getGuiStrs("addTermDialogCaption"), 
				  null);

		dialog.setVisible(true); 
		
		String newText=dialog.getSubjectName();

		if (dialog.getClosedFlag() == PaCloseFlag.OK) {	
			
			if (m_container.addSubject(new PaSubject(newText)) == true ) { 

					writeLog(getMessagesStrs("valueWasAdded"), null, true, true, true);
					
					PaEventInt event = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
					
					PaEventDispatcher.get().fireCustomEvent(event);
					
		
			} else {
	
				writeLog(getMessagesStrs("valueExists"), null, true, true, true);
			}
		} else {
			if (dialog.getClosedFlag() == PaCloseFlag.MULTI_ADD) {			
		
				if (m_container.addSubject(new PaSubject(newText)) == true ) { 
					
						addSubject();
						
						writeLog(getMessagesStrs("valueWasAdded"), null, true, true, true); 	
				} else {
					
					PaUtils.get().getMainLabel().setText(getMessagesStrs("valueExists"));
					
					writeLog(getMessagesStrs("valueExists"), null, true, true, true); 
				}
			} 
		} 		
	}
	/**
	 * <p>Uses event to invoke subjects delete operation</p>
	 * @param event - delete event
	 */
	public void deleteSubject(PaEvent event) {
		
		if ( PaEventDispatcher.SUBJECT_DEL_EVENT != event.getEventType() )  { return; }
		
		delete();
	}
	/**
	 * <p>Delete of selected subject operation. Controls existence of the subject inside alboms. Can clear albom from the subject and
	 * can combine clear operation with deletion of subject</p>
	 */
	public void delete() {
		
		if (m_subjectsTable.getSelectedRows ().length == 1) {
			try {			
				Integer Id = (Integer) getValueAt(m_subjectsTable.convertRowIndexToModel(m_subjectsTable.getSelectedRow()), COLUMN_ID);
				
				String name = getValueAt(m_subjectsTable.convertRowIndexToModel(m_subjectsTable.getSelectedRow()), COLUMN_NAME).toString();
				
				if(PaUtils.get().getAlbumContainer().isEmpty()) {
					
					if (JOptionPane.showConfirmDialog(PaUtils.get().getMainWindow(),getMessagesStrs("deleteUnusedSubjectAnswer") +
							NEXT_ROW + name,
						    getMessagesStrs("messageAnswerCaption"),JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{		
						if (m_container.removeSubject(Id)) {
							
							writeLog(getMessagesStrs("subjectWasDeleted") + " : " + name, null, true, true, true); 
						}
						else {
							
							writeLog(getMessagesStrs("subjectWasNotDeleted")+ " : " + name, null, true, true, true); 
						}
					}			
					
					return;
				}
				
				dialog_del = new PaSubjectDeleteDialog(PaUtils.get().getMainWindow(), getGuiStrs("deleteTermDialogCaption")+ " " + 
						name,name);
				
				dialog_del.setVisible(true);
					
				switch (dialog_del.getClosedFlag()) {
					
					case CANCEL : break;
					case OK : {
						
						switch(dialog_del.getCase()) {
						
							case CLEAR_SUB_FROM_ALBOM : {
								
								removeSubFromAlbum(Id,dialog_del.getSelectedAlbom());
								
								break;
							}
							case DELETE_SUB_AT_ALL : {
								
								deleteSubjectAtAll(Id);
								
								break;
							}
							default : break;
						}
						
						break;						
					}
					default : break;
				}
			} catch (IndexOutOfBoundsException e1) {
				
				writeLog(getMessagesStrs("subjectWasNotDeleted"), e1, true, true, true);
			}
		} else {
			
			writeLog(getMessagesStrs("chooseDataForDe"), null, true, true, true);
		}
	}


	public void editTema (PaEvent event) {
		
		if ( PaEventDispatcher.SUBJECT_EDIT_EVENT != event.getEventType() ) { return; }
		
		onEdit();
	}
	
	public void onEdit() {
		
		if (m_subjectsTable.getSelectedRow () != -1) {
			
			try {
				dialog = new PaSubjectAddDialog(PaUtils.get().getMainWindow(), getGuiStrs("editTermDialogCaption"),   
						getValueAt(m_subjectsTable.convertRowIndexToModel(m_subjectsTable.getSelectedRow()), COLUMN_NAME).toString());
				
				dialog .setAddButtonVisible(false);
				
				dialog.setVisible(true);
			
				String newText=dialog.getSubjectName();
				
				Integer Id = (Integer) getValueAt(m_subjectsTable.convertRowIndexToModel(m_subjectsTable.getSelectedRow()), COLUMN_ID);

				if (dialog.getClosedFlag() == PaCloseFlag.OK) {	
					
					if (m_container.edit_tems(new PaSubject(Id, newText)) == true) { 
						
						writeLog(getMessagesStrs("valueWasEdited"), null, true, true, true);
						
					} else {
						
						writeLog(getMessagesStrs("valueExists"), null, true, true, true);
					}
				}
			} catch (IndexOutOfBoundsException e2) {
				
				writeLog(getMessagesStrs("listIsEmpty"), e2, true, true, true);
			}			
		} else {
			
			writeLog(getMessagesStrs("chooseDataForEdit"), null, true, true, true);
		}
	}
	

	public void findTema(PaEvent event) {
		
		if ( PaEventDispatcher.SUBJECT_FIND_EVENT != event.getEventType() ) { return; }
		
		onFind();
	}
	
	public void onFind() {
		
		PaSubjectFindDialog dialog = new PaSubjectFindDialog(PaUtils.get().getMainWindow(), this);
		
		dialog.setVisible(true);
		
		 
	}
	/**
	 * @param  valueForSeach
	 * @param findResults
	 */
	public void findRows(String valueForSeach,HashSet<Integer> findResults)
	{
		
		findResults.clear();
		
		for ( int i=0; i < getRowCount (); ++i  ) {
			
			int Id = (Integer) getValueAt(  m_subjectsTable.convertRowIndexToModel(i), COLUMN_ID);
			
			if ( m_container. getTem(Id).getName().contains( valueForSeach ) ) {
				
				 findResults.add(i);
			}
			
		}	
	}
	/**
	 * @param  index
	 */
	public void setRowSelected(Integer index) 
	{		
		 m_subjectsTable.setRowSelectionInterval(index,index);
	}
	
     /**
      * 
      * @param subjectId - id of subject to remove
      * @param albumName - name of album to remove the subject from
      */
	public void removeSubFromAlbum(Integer subjectId,String albumName) {
					
		int id = PaUtils.get().getAlbumContainer().getAlbumId(albumName);
		
		boolean changedFlag = false;
		
		if ( id != -1 ) {
		
			PaImageContainer photoContainer = PaUtils.get().getMainContainer().getContainer(id);
			
			for (PaImage photo : photoContainer.getList()) {	
				
				if (photo.removeKey(subjectId)) {	
					
					 changedFlag = true;
				}								
			}
		}
		
		if ( changedFlag ) {
			
			PaEventInt event_1 = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				PaEventDispatcher.get().fireCustomEvent(event_1);
							
				writeLog(getMessagesStrs("linksWereDeleted"), null, true, true, true);
		} 
		else {	
		
			writeLog(getMessagesStrs("noLinksWereDeleted"), null, true, true, true);
		}
		
			
	}
	
	public void deleteSubjectAtAll(Integer subjectId) {
		
		for (PaImageContainer photoContainer : PaUtils.get().getMainContainer().getList()) {
			
			for (PaImage photo : photoContainer.getList()) {	
				
				photo.removeKey(subjectId);
			}
		}

		if (m_container.removeSubject(subjectId)) {

			writeLog(getMessagesStrs("valueWasDeleted"), null, true, true, true);
		}
	
	}
	
}
