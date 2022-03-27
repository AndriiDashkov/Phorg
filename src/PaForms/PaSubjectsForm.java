package PaForms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import PaCollection.PaSubjectContainer;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaButtonsGroup;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * Form to show and control the subjects
 * @author avd
 *
 */
public class PaSubjectsForm extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SUBJECT_REFRESH_EVENT, this, "refreshForm");
	} 

	private PaSubjectsTabModel m_tm;
	
	private PaButtonsGroup m_buttonGroup;
	
	private PaSubjectsPopupMenu popupMenu;
	
	private JScrollPane m_scroll;
	
	ImageIcon m_icon = new ImageIcon(PaUtils.get().getIconsPath() + "pasubjecttableicon.png");
	
	private JTable m_table;
	
	private TableRowSorter<PaSubjectsTabModel> sorted;
	
	private int selectRows;
	
	public PaSubjectsForm () {
		
		setLayout(new BorderLayout());
		
		setToolTipText(getGuiStrs("subjectsFormsToolTip"));
		
		m_tm = new PaSubjectsTabModel();
		
		PaSubjectContainer cont = PaUtils.get().getSubjectsContainer();
		
		m_tm.setData(cont);
		
		m_table = new JTable(m_tm);
		
		sorted = new TableRowSorter<PaSubjectsTabModel>(m_tm);
		
		m_table.setRowSorter(sorted);
		
		m_tm.set_myTable(m_table);

		JTableHeader header = m_table.getTableHeader();
		
		header.setReorderingAllowed(false);
		
		header.setResizingAllowed(false);
		
		header.setBackground(Color.lightGray);
		
		m_table.setRowHeight(23);
	
		m_table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		m_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
							
				switch(m_table.getSelectedRows().length) {
				
					case 0:  {
						
						PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SUBJECT_NOT_SELECTED));
						
						break;
					}
					case 1: {
						
						PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SUBJECT_SELECTED_1));
						
						break;
					}
					default: {
						
						PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.SUBJECT_MULTISELECTED));
					}
				
				}
			}
		});
			
		Enumeration<TableColumn> e = m_table.getColumnModel().getColumns();
		
		while (e.hasMoreElements()) {
			
			TableColumn column = e.nextElement();
			
			if (column.getModelIndex()<2) {
				
				column.setMaxWidth(30);
			} 
		}
		
		TableColumnModel colModel = m_table.getColumnModel();
		
		TableColumn column0 = colModel.getColumn(0);
		
		TableColumn column1 = colModel.getColumn(1);
		
		m_table.removeColumn(column0);
		
		m_table.removeColumn(column1);
		
		m_table.setShowGrid(false);
		
		setLayout(new BorderLayout());

		m_scroll = new JScrollPane(m_table);
		
		popupMenu = new PaSubjectsPopupMenu();
		
		popupMenu.add_mouseAdapter(m_table);
		
		popupMenu.add_mouseAdapter(m_scroll);

		m_buttonGroup = new PaButtonsGroup(getGuiStrs("listSubjectsTableCaption"),m_icon); 
		
		setRefreshFormCaption(cont);

		add(m_buttonGroup.getMainPanel(), BorderLayout.NORTH);
		
		add(m_scroll, BorderLayout.CENTER);		
	}
	
	public PaButtonsGroup get_ButtonGroup() {
		
		return m_buttonGroup;
	}
	
	public int getSelectRows() {
		
		selectRows = m_table.getSelectedRows().length;
		
		return selectRows;
	}
	

	/**
	 * @return  - list of ids for selected subject's rows
	 */
	public ArrayList<Integer> getSelectedRowsIds()
	{		
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		PaSubjectsTabModel model = (PaSubjectsTabModel)m_table.getModel();
		
		for(int index: m_table.getSelectedRows()) {
			
			list.add((Integer)model.getValueAt(index, PaSubjectsTabModel.COLUMN_ID));
		}
		
		return list;
	}
	
	/**
	 * Refreshes the data in the form
	 * @param e - refresh event
	 */
	public void refreshForm (PaEvent e) {
		
		if (e.getEventType() != PaEventDispatcher.SUBJECT_REFRESH_EVENT ) { return; }
		
		PaSubjectContainer cont = PaUtils.get().getSubjectsContainer();
		
		m_tm.setData(cont);
		
		sorted.sort();
		
		m_table.updateUI();
		
		setRefreshFormCaption(cont);
				
	}
	
	
	/**
	 * <p>It is for scroll bars moving in the programmatic way: cell with index must be visible on screen </p>
	 * @param index of component that should be moved into visible part of the scroll area (view port)
	 * 
	 */
	public void ensureVisible(Integer index)
	{
		
		Rectangle r =m_table.getCellRect(index, 2,true);
		
		if(isVisibleInViewport(r)) return;
		
		Point p = new Point(r.getLocation());
		
	    //function scrollVisible... works awfully - we use scrollbars directly
		m_scroll.getVerticalScrollBar().setValue(p.y);
		
		if(!isVisibleInViewport(r)) {
			
			m_scroll.getHorizontalScrollBar().setValue(p.x);
		}
	}
	
    /**
     * @param r - rectangle to check visibility in a viewport of scroll pane
     * @return true if the rectangle is entirely  in the viewport
     */
    private boolean isVisibleInViewport(Rectangle r)
    {

    	Rectangle viewPortImage = new Rectangle(m_scroll.getViewport().getLocationOnScreen(),
    			m_scroll.getViewport().getSize());
    	
    	return viewPortImage.contains(r);
    	
    }
    /**
     * 
     * @param cont - container to get the size of number of elements
     */
    private void setRefreshFormCaption(PaSubjectContainer cont) {
	
    	if(cont != null) {
    		
    		m_buttonGroup.setMainText(getGuiStrs("listSubjectsTableCaption") + " " + cont.size());
    	}
    	else {
    		
    		m_buttonGroup.setMainText(getGuiStrs("listSubjectsTableCaption"));
    	}
	}
}
