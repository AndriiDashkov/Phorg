
package PaROI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import PaEditor.PaInstrumentsPanel;
import PaGlobal.PaGuiTools;
import PaROI.PaROIStatistics.TypesStatData;
import static PaGlobal.PaUtils.*;

/**
 * @author Andrey Dashkov
 * The dialog window for choosing parameters for ROI statistic calculation.
 */

public class PaROIStatisticsDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	JButton m_getStatButton = null;
	
	JScrollPane m_tableSizeStatScrollPane = null;
	
	JTable m_sizeStatTable = null;
	
	JScrollPane m_tableOvelapStatScrollPane = null;
	
	JTable m_overlapStatTable = null;
	
	JComboBox<String> m_typesCombo = null;
	
	String[] m_types = {};
	
	PaROIPositionsPanel m_positionsWidget = null;
	
	PaInstrumentsRoiPanel m_roi_inst_panel = null;
	
	PaRoiWindow m_parent = null;
	
	Dimension m_grid = new Dimension(100,50);//the grid for the position diagram 
	
	ArrayList<String> m_typesList = null; 
	
	ArrayList<TypesStatData> m_statData = null;

	
	String[] columnNameSizesTable = {getGuiStrs("typeSizeRoiTableColumnName"), 	getGuiStrs("numberSizeRoiTableColumnName"),
			getGuiStrs("minSizeRoiTableColumnName"), getGuiStrs("maxSizeRoiTableColumnName"),
			getGuiStrs("averageSizeRoiTableColumnName"), getGuiStrs("medianSizeRoiTableTypeColumnName"),
			getGuiStrs("averageRatioSizeRoiTableColumnName"), getGuiStrs("medianRatioRoiTableTypeColumnName")};
	
	String[][] dataSizes = {};
	

	
	String[] columnNameOverlapTable = {getGuiStrs("typeSizeRoiTableColumnName"), getGuiStrs("numberOverlapRoiTableColumnName"),
			getGuiStrs("averageOverlapRoiTableColumnName"), getGuiStrs("medianOverlapRoiTableTypeColumnName") };
	
	String[][] dataOverlap = {};
	
	public PaROIStatisticsDialog (PaRoiWindow parent, PaInstrumentsPanel roi_inst_panel) 
	{
		
		super (parent, getGuiStrs("roiStatDialog"), true); 
		
		m_parent = parent;
		
		HashMap<String,Dimension> images_sizes_map = m_parent.getImageSizesMap();
		 
		Dimension averaged_image_size = getAveragedImageSize(images_sizes_map);
		
		if(m_grid.width > averaged_image_size.width) {m_grid.width = (int) (averaged_image_size.width/1.5); }
		 
		m_grid.height = (int) (m_grid.width/(averaged_image_size.width/(float)averaged_image_size.height));
		
		m_roi_inst_panel = ((PaInstrumentsRoiPanel)roi_inst_panel);
		
		m_typesList  = ((PaInstrumentsRoiPanel)roi_inst_panel).getRoiPanel().getTypesList();
		
		HashMap<String,ArrayList<PaRectangle>> roi_map = m_roi_inst_panel.getRoiPanel().getRoiMap();
		
		ArrayList<String> tmp = new ArrayList<String>();
		
		tmp.add("All");
		
		for(int i = 0; i < m_typesList.size(); ++i) {
			
			tmp.add(new String(m_typesList.get(i)));
		}
		
		m_types = (String[]) tmp.toArray(m_types);
		

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
		
		
		 m_statData = loadStatData(roi_map,  m_typesList, images_sizes_map);
		
		 m_sizeStatTable = new JTable(dataSizes, columnNameSizesTable);
		 
		 m_sizeStatTable.getColumnModel().getColumn(1).setMaxWidth(80);
		 
		 makeTableNonEditable(m_sizeStatTable); 
		 
		 m_overlapStatTable = new JTable(dataOverlap, columnNameOverlapTable);
		 
		 m_overlapStatTable.getColumnModel().getColumn(1).setMaxWidth(80);
		 
		 makeTableNonEditable(m_overlapStatTable); 
		
		 add( createGUI(averaged_image_size) );
		
		 m_tableSizeStatScrollPane.setPreferredSize(new Dimension(900,200));
		
		 m_tableOvelapStatScrollPane.setPreferredSize(new Dimension(900,200));
		
		 m_typesCombo.addActionListener(new ComboListener());

		 pack();
		
		 setResizable(false);
	}
	
    /**
     * @param averaged_images_size - average size of images in the set (album); it's needed for the creation of
     * the special 'map of ROI position' widget
     * @return the panel with all UI controls
     */
	private JPanel createGUI (Dimension averaged_images_size) 
	{
		
		JLabel sizesStatTable = new JLabel(getGuiStrs("sizesStatTableLabel")); 
		
		JPanel panelSizesLabel = PaGuiTools.createHorizontalPanel();
		
		panelSizesLabel.add(sizesStatTable);
		
		panelSizesLabel.add(Box.createHorizontalGlue());
		 
		JPanel panelSizesTable = PaGuiTools.createVerticalPanel();
		 
		panelSizesTable.add(panelSizesLabel);
		 
		m_tableSizeStatScrollPane = new JScrollPane(m_sizeStatTable);
		 
		panelSizesTable.add(m_tableSizeStatScrollPane);
		
		JLabel overlapStatTableLabel = new JLabel(getGuiStrs("overlapStatTableLabel"));
		
		JPanel panelOverlapLabel = PaGuiTools.createHorizontalPanel();
		
		panelOverlapLabel.add(overlapStatTableLabel);
		
		panelOverlapLabel.add(Box.createHorizontalGlue());
		
		JPanel panelOvelapTable = PaGuiTools.createVerticalPanel();
		 
		panelOvelapTable.add(panelOverlapLabel);
		 
		m_tableOvelapStatScrollPane = new JScrollPane(m_overlapStatTable);
		 
		panelOvelapTable.add(m_tableOvelapStatScrollPane);
		
		JLabel typesComboLabel = new JLabel(getGuiStrs("typesROIComboLabel"));
		
		m_typesCombo  = new JComboBox<String>(m_types);
		
		JPanel typesComboPanel = PaGuiTools.createHorizontalPanel();
		
		typesComboPanel.add(typesComboLabel);
		
		typesComboPanel.add(m_typesCombo);
		
		typesComboPanel.add(Box.createHorizontalGlue());
		
		JPanel positionsPanel = PaGuiTools.createHorizontalPanel();
		
		float ratio = averaged_images_size.width/(float)averaged_images_size.height;
		
		int widgetWidth = 500;
		
		int widgetHeight = (int) (500/ratio);
		
		m_positionsWidget = new PaROIPositionsPanel(widgetWidth, widgetHeight, m_grid,   
				m_statData.get(0). position_stat_inside_image_list);
		
		m_positionsWidget.setPreferredSize(new Dimension(widgetWidth,widgetHeight));
		
		positionsPanel.add(Box.createHorizontalGlue());
		
		positionsPanel.add(m_positionsWidget);
		
		positionsPanel.add(Box.createHorizontalGlue());
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		panel_MAIN.add(panelSizesTable);
		
		panel_MAIN.add(panelOvelapTable);
		
		panel_MAIN.add(typesComboPanel);
		
		panel_MAIN.add(Box.createRigidArea(new Dimension(0, 10)));
		
		panel_MAIN.add(positionsPanel);
		
		return panel_MAIN;
	}
	
	
	public ArrayList<TypesStatData> loadStatData(HashMap<String,ArrayList<PaRectangle>> roi_map, 
			ArrayList<String> typesList, HashMap<String,Dimension> images_sizes_map) 
	{
		
		PaROIStatistics statCalc = new PaROIStatistics();
		
		ArrayList<TypesStatData> statList = statCalc.calculateStatistics(roi_map, typesList, m_grid, images_sizes_map);
		
		int typesList_size = typesList.size() + 1;
		
		dataSizes = new String[typesList_size][8];
		
		dataOverlap = new String[typesList_size][4];
		
		for(int i = 0; i <  typesList_size; ++i) {
			
			TypesStatData dt =  statList.get(i);
			
			if (i == 0) {
				
				dataSizes[i][0] =  "all";
				
				dataOverlap[i][0] =  "all";
			}
			else {
				
				dataSizes[i][0] =  typesList.get(i - 1);
				
				dataOverlap[i][0] =  typesList.get(i - 1);
			}
			
			dataSizes[i][1] = Integer.toString(dt.number);
			
			dataSizes[i][2] = Integer.toString(dt.min_size.width) + "," + Integer.toString(dt.min_size.height);
			
			dataSizes[i][3] = Integer.toString(dt.max_size.width) + "," + Integer.toString(dt.max_size.height);
			
			dataSizes[i][4] = Integer.toString(dt.averaged_size.width) + "," + Integer.toString(dt.averaged_size.height);
			
			dataSizes[i][5] = Integer.toString(dt.median_size.width) + "," + Integer.toString(dt.median_size.height); 
			
			dataSizes[i][6] = Double.toString(dt.averaged_aspect_ratio);
			
			dataSizes[i][7] = Double.toString(dt.median_aspect_ratio);
			
			dataOverlap[i][1] = Integer.toString(dt.overlap_data_list.get(i).size());
		
			dataOverlap[i][2] = Double.toString(dt.result_averaged_overlap_data_list.get(i));
			
			dataOverlap[i][3] = Double.toString(dt.result_median_overlap_data_list.get(i)); 
					
		}
		
		return statList;
	}
	
	
	public class PaROIPositionsPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;

		int m_width;
		
		int m_height;
		
		int m_gridWidth;
		
		int m_gridHeight;
		
		Dimension m_grid;
		
		HashMap<Dimension,Integer> m_color_map = null;
		
		public  PaROIPositionsPanel(int width, int height, Dimension grid, HashMap<Dimension,Integer> color_map ) {
			
			m_width = width;
			
			m_height = height;
			
			m_grid = grid;
			
			m_color_map = color_map;
						
			m_gridWidth = (int) Math.ceil(m_width/(float)grid.width);
			 
			m_gridHeight = (int) Math.ceil(m_height/(float)grid.height);

		}
		
		public void setColorMap(HashMap<Dimension,Integer> color_map) { m_color_map = color_map;}
		
		public void paint(Graphics g) 
		{
			int max = 0;
			
			for (Entry<Dimension,Integer> entry : m_color_map.entrySet()) {
				
				int v =  entry.getValue();
				
				if (max < v) { max = v; }
				
			}
			
			for (Entry<Dimension,Integer> entry : m_color_map.entrySet()) {
				
				int v =  entry.getValue();
				
				Dimension indices =  entry.getKey();
				
				Rectangle rec = new Rectangle(indices.width*m_gridWidth, indices.height*m_gridHeight, m_gridWidth, m_gridHeight);
				
				float max_f = (float)max;
				
				int red =  (int) ((v/max_f) * 255) ;
				
				int blue = (int) ((1.0 - v/max_f)*255);
				
				Color c = new Color(red, 0, blue);
				
				g.setColor(c);
				
				((Graphics2D) g).fill(rec);
				
			}	
		}
	}
	
	
	 private Dimension getAveragedImageSize(HashMap<String,Dimension> images_sizes_map)
	 {
		 Dimension averaged_size = new Dimension(0,0);
		 
		 int count = 0;
		 
		 for (Entry<String,Dimension> entry : images_sizes_map.entrySet()) {
				
			Dimension d = entry.getValue();
			
			averaged_size.width += d.width;
			
			averaged_size.height += d.height;
			
			++count;
		 }
		 
		 averaged_size.width /= count;
			
		 averaged_size.height /= count;
		 
		 return averaged_size;
	 }
	 
	 
	 private class ComboListener implements ActionListener {
	

			@Override
			public void actionPerformed(ActionEvent e) {
				
			  
			       String typeName = (String)m_typesCombo.getSelectedItem();
			       
			       int typeIndex = m_typesList.indexOf(typeName) + 1;
			       
			       m_positionsWidget.setColorMap(m_statData.get(typeIndex). position_stat_inside_image_list);
			       
			       m_positionsWidget.repaint();
			}
		
		}
	 
	 
	 
	 private void makeTableNonEditable(JTable t) 
	 {
		 for (int i = 0; i < t.getColumnCount(); i++)
		 {
		     Class<?> col_ = t.getColumnClass(i);
		     
		     t.setDefaultEditor(col_, null);        
		 }
	 }
	
	
}
