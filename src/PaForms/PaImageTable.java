package PaForms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import PaCollection.PaImageContainer;
import PaDialogs.PaImageFindDialog;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaButtonsGroup;
import PaGlobal.PaUtils;
import PaImage.PaViewPanel;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;

/**
 * @author Andrey Dashkov
 * Class for the  table with list of  images
 */
public class PaImageTable extends JPanel {
	
	private static final long serialVersionUID = 1L;
	

	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT, this, "refreshForm");
	}
	
	private JScrollPane m_scroll;
	
	private JToolBar m_toolbar_ph = new JToolBar(); 
	
	private PaButtonsGroup m_buttonGroup;
	
	private JTable m_table;
	
	private PaImageTabModel tm_ph;
	
	private TableRowSorter<PaImageTabModel> m_sorted;
	
	ImageIcon m_icon = new ImageIcon(PaUtils.get().getIconsPath() + "paphototableicon.png");
	
	private PaViewPanel m_viewPanel;
	
	public PaImageTable (PaViewPanel view ) {
		
		setLayout(new BorderLayout());
		
		setToolTipText(getGuiStrs("photosFormsToolTip"));
		
		m_viewPanel =  view ;
		
		m_viewPanel.setPhotoFormTable( this );

		PaImageContainer photoContainer = PaUtils.get().getMainContainer().getCurrentContainer();
		
		tm_ph = new PaImageTabModel();
		
		tm_ph.setDataSourse(photoContainer);
		
		m_table = new JTable(tm_ph);
		
		m_sorted = new TableRowSorter<PaImageTabModel>(tm_ph);
		
		m_table.setRowSorter(m_sorted);
		
		tm_ph.set_photoTable(m_table);
		
		m_table.addMouseListener(new MouseL());
		
		m_table.setToolTipText(getGuiStrs("photoTableToolTipText"));
						
		JTableHeader header = m_table.getTableHeader();
		
		header.setReorderingAllowed(false);
		
		header.setResizingAllowed(false);
		
		header.setBackground(Color.lightGray);
		
		m_table.setRowHeight(23);
	
		Enumeration<TableColumn> e = m_table.getColumnModel().getColumns();
		
		while (e.hasMoreElements()) {
			
			TableColumn column = e.nextElement();
			
			if (column.getModelIndex()==3) {
				
				column.setMaxWidth(100);
			} 
		}
		
		TableColumnModel colModel = m_table.getColumnModel();

		m_table.removeColumn(colModel.getColumn(0));
		
		m_table.removeColumn(colModel.getColumn(0));
		
		m_table.removeColumn(colModel.getColumn(4));
		
		m_table.removeColumn(colModel.getColumn(4));
		
		colModel.getColumn(0).setMaxWidth(20);
		
		colModel.getColumn(1).setMaxWidth(20);
		
		colModel.getColumn(3).setMaxWidth(80);
		
		m_table.setShowGrid(false);
			
		m_toolbar_ph.setAlignmentX(SwingConstants.LEFT);
		
		m_scroll = new JScrollPane(m_table);
	
		PaImageTablePopupMenu popupMenu =new PaImageTablePopupMenu ( this );
		
		popupMenu.addMouseAdapter(m_table);
		
		m_buttonGroup = new PaButtonsGroup(getGuiStrs("photoTableCaption"),m_icon); 
		
		setRefreshFormCaption(photoContainer);

		add(m_buttonGroup.getMainPanel(), BorderLayout.NORTH);

		add(m_scroll, BorderLayout.CENTER);
	}
	
	/**
	 * Refreshes the data in the form
	 * @param e
	 */
	public void refreshForm (PaEvent e) {
		
		if ( e.getEventType() != PaEventDispatcher.VIEWPANEL_REFRESH_EVENT ) { return; }
		
		PaImageContainer cont = PaUtils.get().getMainContainer().getCurrentContainer();
		
		
		try {
		
			tm_ph.setDataSourse(cont);
		
		}
		catch(java.util.ConcurrentModificationException e1) {
			
			writeLog("ConcurrentModificationException in PaImageTable. refreshForm()  : not critical .", null,
					true, false, false );
		}
		
		m_sorted.sort();
		
		m_table.updateUI();
		
		setRefreshFormCaption(cont);
		
	}
	
	public PaButtonsGroup get_ButtonGroup() {
		
		return m_buttonGroup;
	}

	public void ensureVisible() {
		
		m_viewPanel.ensureVisible((int) tm_ph.getValueAt(m_table.convertRowIndexToModel(m_table.getSelectedRow()),1));
		
	}
	

	public void setSelected(int id) {
		
		int index =tm_ph.findIndexforId(id);
		
		if ( index != -1 ) {
			
			m_table.clearSelection();
			
			tm_ph.setRowSelected(index);
		}
		
	}
	
	public void openFindDialog()
	{
		PaImageFindDialog dialogWin = new PaImageFindDialog( PaUtils.get().getMainWindow(), tm_ph );  	
		
		dialogWin.setVisible(true);
		
	}
	
	class MouseL implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 2 ) {
				
				ensureVisible();
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
		public void mouseReleased(MouseEvent arg0) {
			
		}
	}
	
    /**
     * 
     * @param n - size of the subject container (number of elements)
     */
    private void setRefreshFormCaption(PaImageContainer c) {
    	
    	if(c != null) {
    		
    		m_buttonGroup.setMainText(getGuiStrs("photoTableCaption") + " " + c.size());
    	}
    	else {
    		
    		m_buttonGroup.setMainText(getGuiStrs("photoTableCaption"));
    	}
	}
			
}
