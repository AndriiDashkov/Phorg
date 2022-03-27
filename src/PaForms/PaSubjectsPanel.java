package PaForms;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import PaCollection.PaSubject;
import PaCollection.PaSubjectContainer;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventSubjectRefreshNewPhotoDialog;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * 
 * <p>Special panel for multiselection subject operation; it is divided into two lists - one with all subjects
 * and other with chosen subjects; buttons help to move subjects from one list to other</p>
 *  @author Andrii Dashkov
 */

public class PaSubjectsPanel {
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.SUBJECT_REF_NEW_IMAGE_EVENT, this, "refreshSubjectWindow");
	}
	
	private JPanel mainPanel;
	
	private PaSubjectsListModel m_subListModel;
	
	private PaSubjectsListModel m_subListModelPhoto;

	private JList<Object> m_leftList;		
	
	private JList<Object> m_rightLIst;				
	
	private final String icon_path = PaUtils.get().getIconsPath();
	
	private Forwarder forwarder = new Forwarder();

	public JButton m_addButton, m_delButton, m_addAllButton, m_delAllButton, m_newSubjectButton;
	
	public PaSubjectsPanel (boolean newSubject, ArrayList<Integer> Id, PaSubjectContainer temContainer, String namePanel) {
		
		mainPanel = createGUI(newSubject, Id, temContainer, namePanel);
	}	
	
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	public PaSubjectsListModel get_temListModelPhoto() {
		
		return m_subListModelPhoto;
	}

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_addButton ) onAddSubject (e);
			
			if ( e.getSource() == m_delButton ) onDelSubject (e);
			
			if ( e.getSource() == m_addAllButton ) onAddAllTem (e);
			
			if ( e.getSource() == m_delAllButton ) onDelAllTem (e);
			
			if ( e.getSource() == m_newSubjectButton ) onNewTem (e);
		}
	}
	

	public void onAddSubject (ActionEvent e) {
		
		try {
			
			int [] indexTems = new int [m_leftList.getSelectedIndices().length];
			
			int i = 0;
			
			for (int index : m_leftList.getSelectedIndices()) {
		
				indexTems [i] = m_subListModel.getRowAt(index).getId();
				
				m_subListModelPhoto.addTema(m_subListModel.getRowAt(index));
				
				i++;
			}
			
			m_subListModel.removeData(indexTems);
			
			m_leftList.clearSelection();
			
			m_leftList.updateUI();
			
	       	m_rightLIst.updateUI();
	       	
	       	if (m_subListModel.getSize() == 0) {
	       		
	       		m_addAllButton.setEnabled(false);
	       	}
	       	
	       	m_delAllButton.setEnabled(true);
	       	
		} catch (IndexOutOfBoundsException e1) {
//			
		}	
	}

	public void onDelSubject (ActionEvent e) {
		
		try {
			
			int [] indexTems = new int [m_rightLIst.getSelectedIndices().length];
			
			int i = 0;
			
			for (int index : m_rightLIst.getSelectedIndices()) {
		
				indexTems [i] = m_subListModelPhoto.getRowAt(index).getId();
				
				m_subListModel.addTema(m_subListModelPhoto.getRowAt(index));
				
				i++;
			}
			
			m_subListModelPhoto.removeData(indexTems);
			
			m_rightLIst.clearSelection();
			
			m_leftList.updateUI();
			
	       	m_rightLIst.updateUI();
	       	
	       	if (m_subListModelPhoto.getSize() == 0) {
	       		
	       		m_delAllButton.setEnabled(false);
	       	}
	       	
	       	m_addAllButton.setEnabled(true);
	       	
		} catch (IndexOutOfBoundsException e1) {

		}		
	}
	

	public void onAddAllTem (ActionEvent e) {
		
		int [] idTems = new int [m_subListModel.getSize()];
		
		int i = 0;
		
		for (PaSubject tema : m_subListModel.get_temsList()) {
	
			idTems [i] = tema.getId();
			
			m_subListModelPhoto.addTema(tema);
			
			i++;
		}
		
		m_subListModel.removeData(idTems);
		
		m_leftList.clearSelection();
		
		m_leftList.updateUI();
		
       	m_rightLIst.updateUI();
       	
       	m_addAllButton.setEnabled(false);
       	
       	m_delAllButton.setEnabled(true);
	}


	public void onDelAllTem (ActionEvent e) {
		
		clearAllSelected();
	}
	
	public void clearAllSelected() {
		
		int [] idTems = new int [m_subListModelPhoto.getSize()];
		
		int i = 0;
		
		for (PaSubject tema : m_subListModelPhoto.get_temsList()) {
	
			idTems[i] = tema.getId();
			
			m_subListModel.addTema(tema);
			
			i++;
		}
		
		m_subListModelPhoto.removeData(idTems);
		
		m_leftList.updateUI();
		
	   	m_rightLIst.updateUI();
	   	
	   	m_addAllButton.setEnabled(true);
	   	
	   	m_delAllButton.setEnabled(false);
	
	}
	
	public void onNewTem (ActionEvent e) {
		
		PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SUBJECT_NEW_EVENT) );		
	}
	
	public void refreshSubjectWindow (PaEventSubjectRefreshNewPhotoDialog eventRefrash) {

		m_subListModel.addTema(eventRefrash.get());
		
        m_leftList.updateUI();	
	}
	
	public void setEnabledAll( boolean enabled ) {
		
		mainPanel.setEnabled(enabled);
		
		m_leftList.setEnabled(enabled);
		
		m_rightLIst.setEnabled(enabled);
		
		m_addButton.setEnabled(enabled);
		
		m_delButton.setEnabled(enabled);
		
		m_addAllButton.setEnabled(enabled);
		
		m_delAllButton.setEnabled(enabled);
		
		m_newSubjectButton.setEnabled(enabled);
		 
		if ( enabled) {
			 
			 m_addButton.setEnabled(false) ;
			 
			 m_delButton.setEnabled(false);
			 
			 setButtonsEnabled();
		}
		
	}
	
	
	private void setButtonsEnabled() {
		
		if (m_subListModelPhoto.getSize() == 0) {
			
			m_delAllButton.setEnabled(false);
			
		} else {
			
			m_delAllButton.setEnabled(true);
			
		}
		if (m_subListModel.getSize() == 0) {
			
			m_addAllButton.setEnabled(false);
			
		} else {
			
			m_addAllButton.setEnabled(true);
		}
	}
	
	private JPanel createGUI(boolean newTem, ArrayList<Integer> temId, PaSubjectContainer temContainer, String namePanel) {
		
			
		m_subListModel = new PaSubjectsListModel();	
		
		m_subListModel.setDataSourse(temContainer);
		
		m_subListModel.removeData(temId);
	
		m_leftList = new JList<Object>(m_subListModel);
					 
		m_subListModelPhoto = new PaSubjectsListModel();
		
		m_subListModelPhoto.setDataPhotos(temId);
				
		m_rightLIst = new JList<Object>(m_subListModelPhoto);
		
		JPanel panel_Center = PaGuiTools.createHorizontalPanel();
		
		JPanel panel_CenterGroup = PaGuiTools.createVerticalPanel();
		
		JPanel panel_but_5 = new JPanel( new GridLayout( 5,1,0,5) );

		panel_but_5.add(m_addButton = new JButton( new ImageIcon (icon_path + "paarrright.png")));
		
		panel_but_5.add(m_delButton = new JButton( new ImageIcon (icon_path + "paarrleft.png")));
		
		panel_but_5.add(m_addAllButton = new JButton( new ImageIcon (icon_path + "paarrrightall.png")));
		
		panel_but_5.add(m_delAllButton = new JButton( new ImageIcon (icon_path + "paarrleftall.png")));
		
		panel_but_5.add(m_newSubjectButton = new JButton( new ImageIcon (icon_path + "patermnew.png")));
		
		 m_addButton.setToolTipText(getGuiStrs("toolTipButtonSubjectAdd"));
		 
		 m_delButton.setToolTipText(getGuiStrs("toolTipButtonSubjectRemove"));
		 
		 m_addAllButton.setToolTipText(getGuiStrs("toolTipButtonSubjectAddAll"));
		 
		 m_delAllButton.setToolTipText(getGuiStrs("toolTipButtonSubjectRemoveAll"));
		 
		 m_newSubjectButton.setToolTipText(getGuiStrs("toolTipButtonSubjectNew"));
			
		m_addButton.addActionListener(forwarder);
		
		m_delButton.addActionListener(forwarder);
		
		m_addAllButton.addActionListener(forwarder);
		
		m_delAllButton.addActionListener(forwarder);
		
		m_newSubjectButton.addActionListener(forwarder);
		
		m_addButton.setEnabled(false);
		
		m_delButton.setEnabled(false);
		
		m_newSubjectButton.setVisible(newTem);
		
		setButtonsEnabled();
		
		m_leftList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (m_leftList.getSelectedIndices().length == 0) {
					
					m_addButton.setEnabled(false);
					
				} else {
					
					m_addButton.setEnabled(true);
				}
				
			}
		});
		
		m_rightLIst.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (m_rightLIst.getSelectedIndices().length == 0) {
					
					m_delButton.setEnabled(false);
					
				} else {
					
					m_delButton.setEnabled(true);
				}
			}
		});
			

		JPanel panel_but_51 = new JPanel( new FlowLayout(FlowLayout.CENTER, 0, 0) );
		
		panel_but_51.add(panel_but_5);		
			
		Border but_5 = BorderFactory.createEmptyBorder();
		
		Border tit_but_5 = BorderFactory.createTitledBorder(but_5, " ");
		
		panel_but_51.setBorder(tit_but_5);
			
		JScrollPane scrol_left = new JScrollPane(m_leftList);
		
		JLabel capLabel = new JLabel(getGuiStrs("subjectsListCaption"), JLabel.LEFT);
		
		scrol_left.setColumnHeaderView(capLabel );

		scrol_left.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		
		scrol_left.setBorder(BorderFactory.createEmptyBorder());
		
		 Font newLabelFont=new Font(capLabel .getFont().getName(),Font.PLAIN,capLabel .getFont().getSize());  
		  
		 capLabel.setFont(newLabelFont); 
			
		Dimension DIM = new Dimension(180, 180);
		
		scrol_left.setSize(DIM);
		
		scrol_left.setPreferredSize(DIM);
		
		JScrollPane scrol_right = new JScrollPane(m_rightLIst);
		
		scrol_right.setPreferredSize(DIM);
		
		JLabel capLabel2 = new JLabel(getGuiStrs("subjectsSelectedListCaption"), JLabel.LEFT);

		scrol_right.setColumnHeaderView(capLabel2); 
		
		scrol_right.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		
		scrol_right.setBorder(BorderFactory.createEmptyBorder());
		
		capLabel2.setFont(newLabelFont); 
						
		panel_Center.add(Box.createHorizontalStrut(12));
		
		panel_Center.add(scrol_left);
		
		panel_Center.add(Box.createHorizontalStrut(3));
		
		panel_Center.add(panel_but_51);
		
		panel_Center.add(Box.createHorizontalStrut(3));
		
		panel_Center.add(scrol_right);
		
		panel_Center.add(Box.createHorizontalStrut(12));
					
		PaGuiTools.setGroupAlignmentX( new JComponent[] {scrol_left, scrol_right, panel_but_51}, 
											Component.CENTER_ALIGNMENT);

		panel_CenterGroup.add(panel_Center);
		
		panel_CenterGroup.add(Box.createVerticalStrut(12));

		Border etched_0 = BorderFactory.createEtchedBorder();
		
		TitledBorder titled_0 = BorderFactory.createTitledBorder(etched_0, namePanel);
		
		titled_0.setTitleFont(newLabelFont);
		
		panel_CenterGroup.setBorder(titled_0);
		
		setToolTips();

		return panel_CenterGroup;
	}
	 /**
     * <p>Moves list of subject's from left panel to right panel, or make 'select' operation for these subjects</p>
     * @param list - list of subjects 
     */
	public void setSubjectsToBeSelected(ArrayList<PaSubject> list) 
	{
		
		for(PaSubject t: list) {
			
			moveSubjectToRight(t);
		}
	}
	 /**
     * <p>Moves one subject from left panel to right panel, or make 'select' operation for this subject</p>
     * @param t - subject to choose 
     */
	public void moveSubjectToRight(PaSubject t)
	{
		int [] indexes = new int [1];
		
		int index = m_subListModel.findSubject(t);
		
		if(index != -1) {
			
			m_subListModelPhoto.addTema(m_subListModel.getRowAt(index));
			
			indexes[0] = t.getId();
			
			m_subListModel.removeData(indexes);
			
			m_leftList.clearSelection();
			
			m_leftList.updateUI();
			
	       	m_rightLIst.updateUI();
	       	
	       	if (m_subListModel.getSize() == 0) {
	       		
	       		m_addAllButton.setEnabled(false);
	       	}
	       	
	       	m_delAllButton.setEnabled(true);
			
		}
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		 m_addButton.setToolTipText(getGuiStrs("addSubPanelToolTip"));
		 
		 m_delButton.setToolTipText(getGuiStrs("delSubPanelToolTip"));
		 
		 m_addAllButton.setToolTipText(getGuiStrs("addAllSubPanelTip"));
		 
		 m_delAllButton.setToolTipText(getGuiStrs("delAllSubPanelTip"));
		 
		 m_newSubjectButton.setToolTipText(getGuiStrs("newSubPanelToolTip"));

		 m_leftList.setToolTipText(getGuiStrs("leftListSubPanelToolTip"));
		 
		 m_rightLIst.setToolTipText(getGuiStrs("rigthListSubPanelToolTip"));
			 	  	 
	 }
}
