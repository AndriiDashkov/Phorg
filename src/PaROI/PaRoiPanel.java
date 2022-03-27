package PaROI;

import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMenusStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import static PaGlobal.PaUtils.getSeparator;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import PaActions.PaAction;
import PaEditor.PaInstrumentsWindow;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;


/**
 * <p>The panel represents the ROI information: two tables with ROIs and ROI types. </p>
 * @author avd
 *
 */
public class PaRoiPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	String roi_file_path = null;
	
	String current_image_file_name = null;
	
	private JFileChooser m_sourceFileChooser  = null;
		
	JTable m_roi_table = null;
	
	JTable m_types_table = null;
	
	PaRoiTableModel roi_table_model = new PaRoiTableModel();
	
	PaRoiTypesTableModel types_table_model = new PaRoiTypesTableModel(this);
	
	HashMap<String,ArrayList<PaRectangle>> m_roi_map = new HashMap<String,ArrayList<PaRectangle>>();
	
	ArrayList<PaRectangle> current_roi_list_for_image = null;
	
	private ArrayList<String> m_current_roi_list_for_types = null ;
	
	PaInstrumentsWindow m_window_parent = null;
	
	int selectedIndex = -1;
	
	PaRectangle selectedRoi = null;
	
	int m_types_selectedIndex = -1;
	
	PaRoiPanel(PaInstrumentsWindow parent) {
		
		m_window_parent = parent;
		
		m_current_roi_list_for_types = types_table_model.getTypesList();
		
		createUI();
	}


	/**
	 *  Creates and makes layouts for all components  
	 * 
	 */
	private void createUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel filePanel = PaGuiTools.createVerticalPanel();
		
		m_types_table = new JTable( types_table_model);
		
		m_types_table.getColumnModel().getColumn(0).setMaxWidth(60);
		
		JScrollPane roi_scroll = new JScrollPane(m_types_table);
		
		PaRoiTypesTablePopupMenu popupMenu = new PaRoiTypesTablePopupMenu (roi_scroll);
		
		popupMenu.addMouseAdapter(m_types_table);
		
		popupMenu.addMouseAdapter(roi_scroll);
		
		JPanel typesROIPanel = PaGuiTools.createVerticalPanel();
		
		JPanel typesLabelPanel = PaGuiTools.createHorizontalPanel();
		
		typesLabelPanel.add(new JLabel(getGuiStrs("roiTypesLabel")));
		
		typesLabelPanel.add(Box.createHorizontalGlue());
		
		typesROIPanel.add(typesLabelPanel);
		
		typesROIPanel.add(roi_scroll);
		
		filePanel.add(typesROIPanel);
		
		filePanel.add(Box.createVerticalGlue());
		
		m_roi_table = new JTable(roi_table_model);
		
		m_roi_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
        m_roi_table.getColumnModel().getColumn(0).setMaxWidth(60);
	
		JScrollPane scroll = new JScrollPane(m_roi_table);
		
		PaRoiTablePopupMenu popupMenuRoi = new PaRoiTablePopupMenu (scroll);
		
		popupMenuRoi.addMouseAdapter(m_roi_table);
		
		popupMenuRoi.addMouseAdapter(scroll);
		
		setAllListeners();
		
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   filePanel, scroll);
        
        splitPane.setOneTouchExpandable(true);
        
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
        
        splitPane.setDividerLocation(0.25);
        
        //bug: partial edit operation inside the cell, and than move the focus
		m_types_table.addFocusListener(new FocusAdapter() {
			
	        @Override
	        public void focusLost(FocusEvent e) {
	        	
	        	if(null != m_types_table.getCellEditor()) {
	        		
	        		m_types_table.getCellEditor().stopCellEditing();
	        		
	        	}
	        		
	        }           
	    });
		
	}
	
	public String loadROIfile() 
	{
		
		m_window_parent.resetInstrument();
		
		 m_sourceFileChooser  = new JFileChooser ();
		 
		 m_sourceFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		if( roi_file_path != null && !roi_file_path.isEmpty()) {
			
			 m_sourceFileChooser.setCurrentDirectory(new File(roi_file_path));
			 
		}
		else {
			
			m_sourceFileChooser.setCurrentDirectory(new File(PaUtils.get().getSettings().getStandardFolderPlace() + "."));
		}
		
		int result = m_sourceFileChooser.showOpenDialog(m_window_parent);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_sourceFileChooser.getSelectedFile().getPath();
			
			
			roi_file_path = new String(name + getSeparator());
			
			File f = new File(roi_file_path);
			
			if(!f.exists()) {
				
				try {
					
					f.createNewFile();
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
					
				}
				
			}
			
			loadNewRoiData();
			
			return roi_file_path;
			
		}	
		
		return null;
	}
	
	/**
	 *Create new file of ROI rectangles
	 */
	public String createNewRoiFile() 
	{
		
		m_window_parent.resetInstrument();
		
		 m_sourceFileChooser  = new JFileChooser ();
		 
		 m_sourceFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		m_sourceFileChooser.setCurrentDirectory(new File(PaUtils.get().getSettings().getStandardFolderPlace() + "."));
		
		
		int result = m_sourceFileChooser.showOpenDialog(m_window_parent);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = m_sourceFileChooser.getSelectedFile().getPath();
			
			String fullName = name + getSeparator();// + "list_of_ROI.txt";
			
			roi_file_path = new String(fullName);
			
			File f = new File(roi_file_path);
			
			if(!f.exists()) {
				
				try {
					
					f.createNewFile();
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
			}
			else {
				
				JOptionPane.showMessageDialog(m_window_parent,
	    				getMessagesStrs("messageROIFileExist"),
	    			    getMessagesStrs("messageInfoCaption"),
	    			    JOptionPane.INFORMATION_MESSAGE);
				
			}
			
			loadNewRoiData();
			
			return roi_file_path;
			
		}	
		
		return null;
			
	}
	
	
	public PaRectangle getSelectedRoi() { return selectedRoi; }
	
	public int getSelectedRoiIndex() { return selectedIndex; }
	
	
	private void loadNewRoiData()
	{
		m_window_parent.resetInstrument();
		
		m_roi_map.clear();
		
		m_current_roi_list_for_types.clear();
		
		selectedRoi = null;
		
		selectedIndex = -1;
		
		PaRoiFileParser.parseRoiFile(roi_file_path, m_roi_map, m_current_roi_list_for_types);
		
		refreshRoiListForImage();
		
		collectTypesStatistic(types_table_model.getStatisticsList());
		
		if(roi_table_model.getRowCount() != 0) {
			
			selectedIndex = 0;
			
			m_roi_table.setRowSelectionInterval(0, 0);
		
		}
		
		m_window_parent.loadImage();
	}
	
	public void loadNewRoiData(String file_path)
	{
		roi_file_path = file_path;
		
		loadNewRoiData();
	}
	
	public void setNewImage(String imageName)
	{
		m_window_parent.resetInstrument();
		
		current_image_file_name = imageName;
		
		refreshRoiListForImage();
		
		selectedRoi = null;
		
		selectedIndex = -1;
		
		if(roi_table_model.getRowCount() != 0) {
			
			selectedIndex = 0;
			
			m_roi_table.setRowSelectionInterval(0, 0);
		
		}
	
		
	}
	
	public void refreshTypesList()
	{
		collectTypesStatistic(types_table_model.getStatisticsList());
		
		types_table_model.fireTableDataChanged();
		
	}
	
	public void refreshRoiListForImage()
	{
		
		 current_roi_list_for_image = m_roi_map.get(current_image_file_name);
			
		if( current_roi_list_for_image == null ) {
			
			current_roi_list_for_image = new  ArrayList<PaRectangle>(); 
			
			m_roi_map.put(current_image_file_name, current_roi_list_for_image);
			
		}
		
		roi_table_model.loadData(current_roi_list_for_image);
		
		roi_table_model.fireTableDataChanged();
		
		m_roi_table.updateUI();
		
		types_table_model.loadData(m_current_roi_list_for_types);
		
		types_table_model.fireTableDataChanged();
		
		m_types_table.updateUI();
		
		if ( current_roi_list_for_image.isEmpty()) {
			
			((PaRoiWindow) m_window_parent).setEditRoiButtonEnabled(false);
		}
		else {
			
			((PaRoiWindow) m_window_parent).setEditRoiButtonEnabled(true);
		}
		
	}
		
	
	private void setAllListeners() {
		
	    ListSelectionModel listSelectionModel = m_roi_table.getSelectionModel();
	    
	    listSelectionModel.addListSelectionListener(new SelectionHandler());
	    
	    ListSelectionModel listSelectionModel1 = m_types_table.getSelectionModel();
	    
	    listSelectionModel1.addListSelectionListener(new SelectionHandlerTypes());
	}
	
	
	
	class SelectionHandler implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	    	
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        if (!lsm.isSelectionEmpty()) {
	            
	            selectedIndex =  lsm.getMinSelectionIndex();
	            
	            selectedRoi = roi_table_model.getRectangleAt(selectedIndex);
	          
	            ((PaRoiWindow) m_window_parent).refreshImage();
	            
	            ((PaRoiWindow) m_window_parent).setEditRoiButtonEnabled(true);
	            
	        }
	        else {
	        	
	        	((PaRoiWindow) m_window_parent).setEditRoiButtonEnabled(false);
	        }

	    }
	}
	
	
	class SelectionHandlerTypes implements ListSelectionListener {
		
	    public void valueChanged(ListSelectionEvent e) {
	    	
	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        if (!lsm.isSelectionEmpty()) {
	            
	            m_types_selectedIndex =  lsm.getMinSelectionIndex();
	          
	        }

	    }
	}
	
	public void addNewRoiType()
	{
		m_window_parent.resetInstrument();
		
		types_table_model.addNewRoiType(); 
		
		types_table_model.fireTableDataChanged();
		
		m_types_table.updateUI();
		
	}
	
	public void deleteType()
	{
		
		boolean modificationFlag = false;
		
		if(m_types_selectedIndex != -1) {
			
			//remove all reference to this ROI type from the ROI map
			String type_for_delete = types_table_model.getTypeAt(m_types_selectedIndex);
			
			for (Entry<String, ArrayList<PaRectangle>> entry : m_roi_map.entrySet()) {
							
				ArrayList<PaRectangle> list =  entry.getValue();
				
				
				for(int i =0; i < list.size(); ++i) {
					
					PaRectangle rec = list.get(i);
					
					if(rec.type.equals(type_for_delete)) {
						
						list.get(i).type = "";
						
						modificationFlag = true;
					}
				
				}
			    
			}
			
			types_table_model.deleteRoiType(m_types_selectedIndex);
			
			m_types_selectedIndex = -1;
			
		}
	
		m_window_parent.resetInstrument();
		
		types_table_model.fireTableDataChanged();
		
		m_types_table.updateUI();
		
		if(modificationFlag == true) {
			
			roi_table_model.fireTableDataChanged();
			
			m_roi_table.updateUI();
		}
		
	}

    
	public ArrayList<PaRectangle> getCurrentRoiList()
	{
		return current_roi_list_for_image;
	}
	
	
	public String getRoiFilePath()
	{
			return roi_file_path;
	}
	
	public void setRoiFilePath(String path)
	{
			roi_file_path = path;
	}
	
	
	HashMap<String,ArrayList<PaRectangle>> getRoiMap() { return m_roi_map; }
	
	
	 ArrayList<String>  getTypesList() { return m_current_roi_list_for_types; }
	 
	 
	 public class PaRoiTypesTablePopupMenu {

			private JPopupMenu m_menu;
		
			public  PaRoiTypesTablePopupMenu ( JScrollPane roi_scroll) {

				m_menu = new JPopupMenu();

				m_menu.add(new PaAddNewType(PaRoiPanel.this) );
				
				m_menu.add(new PaDeleteType(PaRoiPanel.this) );

			}

			public void addMouseAdapter (JComponent compounent) {
				
				compounent.addMouseListener(new PopupMouseAdapter());
				
			}
			
			class PopupMouseAdapter extends MouseAdapter {

				public void mousePressed(MouseEvent e) {

					maybeShowPopup(e);
				}

				public void mouseReleased(MouseEvent e) {

					maybeShowPopup(e);
				}

				private void maybeShowPopup(MouseEvent e) {
					
					if (e.isPopupTrigger()) {
						
						m_window_parent.resetInstrument();
						
						m_menu.show(e.getComponent(), e.getX(), e.getY());
						
					}
				}
			}

		}
	 
	 
	 public class PaRoiTablePopupMenu {

			private JPopupMenu m_menu;
	
			public  PaRoiTablePopupMenu ( JScrollPane roi_scroll) {

				m_menu = new JPopupMenu();

				m_menu.add(new PaDeleteRoi(PaRoiPanel.this) );

			}

			public void addMouseAdapter (JComponent compounent) {
				
				compounent.addMouseListener(new PopupMouseAdapter());
				
			}
			
			class PopupMouseAdapter extends MouseAdapter {

				public void mousePressed(MouseEvent e) {

					maybeShowPopup(e);
				}

				public void mouseReleased(MouseEvent e) {

					maybeShowPopup(e);
				}

				private void maybeShowPopup(MouseEvent e) {
					
					if (e.isPopupTrigger()) {
						
						m_window_parent.resetInstrument();
						
						m_menu.show(e.getComponent(), e.getX(), e.getY());
						
					}
				}
			}

		}
	 
	 public class PaAddNewType extends PaAction {
			
			private PaRoiPanel panel;
			
			private static final long serialVersionUID = 1L;
			{

			}
			public PaAddNewType(PaRoiPanel panel) {
				
				super("paaddnewroitype");
				
				this.panel = panel;

				putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("addNewRoiTypeToolTipName")); 
				
				putValue(NAME, getMenusStrs("addNewRoiMenuName"));
				

			}

			public void actionPerformed(ActionEvent e) {
				
				panel.addNewRoiType();
				
				m_window_parent.resetInstrument();
				
			}		
		}
	 
	 public class PaDeleteType extends PaAction {
			
			private PaRoiPanel panel;
			
			private static final long serialVersionUID = 1L;
			{

			}
			
			public PaDeleteType(PaRoiPanel panel) {
				
				super("padeleteroitype");
				
				this.panel = panel;

				putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("deleteRoiTypeToolTipName")); 
				
				putValue(NAME, getMenusStrs("deleteRoiMenuName"));

			}

			public void actionPerformed(ActionEvent e) {
				
				
				int n = JOptionPane.showConfirmDialog(
						((PaRoiWindow) m_window_parent),
					    getMessagesStrs("deleteROItypeMessage"),
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.YES_NO_CANCEL_OPTION);
				
				if ( n == JOptionPane.YES_OPTION) {
					
					panel.deleteType();
					
					m_window_parent.resetInstrument();
					
					//save operation must be activated
					m_window_parent.setSaveButtonEnabled(true);
					
					m_window_parent.setPreviewCurrentImageChanged(true);
				}
				
			}		
		}
	 
	 
	 public class PaDeleteRoi extends PaAction {
			
			
			private static final long serialVersionUID = 1L;
			{

			}
			public PaDeleteRoi(PaRoiPanel panel) {
				
				super("padeleteroi");
				
				
				putValue(AbstractAction.SHORT_DESCRIPTION, getMenusStrs("deleteRoiToolTipName")); 
				
				putValue(NAME, getMenusStrs("deleteRoiMenuName"));

			}

			public void actionPerformed(ActionEvent e) {
				
				
				if(selectedIndex >= 0 && selectedIndex < current_roi_list_for_image.size()) {
					
					current_roi_list_for_image.remove(selectedIndex);
					
					((PaRoiWindow) m_window_parent).refreshImage();
					
					roi_table_model.fireTableDataChanged();
					
					//save operation must be activated
					m_window_parent.setSaveButtonEnabled(true);
					
					m_window_parent.setPreviewCurrentImageChanged(true);
					
				}
			}		
		}
	 
	 
		void checkRoiDataForCurrentImage(String currentImageName)
		{
			
			 current_roi_list_for_image = m_roi_map.get(currentImageName);
				
			if( current_roi_list_for_image == null ) {
				
				current_roi_list_for_image = new  ArrayList<PaRectangle>(); 
				
				m_roi_map.put(currentImageName, current_roi_list_for_image);
			}
			
			
			roi_table_model.loadData(current_roi_list_for_image);
			
			roi_table_model.fireTableDataChanged();
			
			m_roi_table.updateUI();
			
	
			types_table_model.loadData(m_current_roi_list_for_types);
			
			types_table_model.fireTableDataChanged();
			
			m_types_table.updateUI();
			
		}
		
		
		public void changeAllRoiTypeReferences(String old_type_value, String new_type_value)
		{
			
			for (Entry<String, ArrayList<PaRectangle>> entry : m_roi_map.entrySet()) {
				
				ArrayList<PaRectangle> list =  entry.getValue();
				
				
				for(int i =0; i < list.size(); ++i) {
					
					PaRectangle rec = list.get(i);
					
					if(rec.type.equals( old_type_value)) {
						
						list.get(i).type = new_type_value;
					}
				
				}
			    
			}
			
			roi_table_model.fireTableDataChanged();
			
			m_roi_table.updateUI();
			
		}
		
		
		/**
		 * Collects statistics about ROI type all over ROI data.
		 */
		public void collectTypesStatistic(ArrayList<Integer> stat_list)
		{
			
			stat_list.clear();
			
			for(int i = 0; i < types_table_model.getRowCount(); ++i) {
				
				stat_list.add(0);
			}
			
			for (Entry<String, ArrayList<PaRectangle>> entry : m_roi_map.entrySet()) {
							
				ArrayList<PaRectangle> list =  entry.getValue();
				
				
				for(int i =0; i < list.size(); ++i) {
					
					PaRectangle rec = list.get(i);
					
					int index = types_table_model.indexOf(rec.type);
					
					if (index != -1) {
						
						 stat_list.set(index, stat_list.get(index) + 1 );
					}
				
				}
			    
			}
			
		}
		
		
		public boolean isRoiListFileLoaded() 
		{
			
			return roi_file_path != null;
		}
	
}
