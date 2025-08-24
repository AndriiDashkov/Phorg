package padialogs;

import static paglobal.PaUtils.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import pacollection.PaFilterInfo;
import pacollection.PaSubject;
import paforms.PaSubjectsPanel;
import paglobal.PaGuiTools;
import paglobal.PaUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * @author Andrii Dashkov
 *
 */

	/**
	 * <p>Dialog window for using of filter. It is initiated with the current filter of current album.
	 *</p>
	 */

public class PaImagesFilterDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private PaSubjectsPanel m_subjectsPanel;
	
	private JRadioButton m_allAlbomsRadioBut = new JRadioButton(getGuiStrs("inAllAlbomsRadioButtonCaptionFilter"), true); 

	private JRadioButton m_oneAlbomRadioBut = new JRadioButton(getGuiStrs("inCurrentAlbomRadioButtonCaptionFilter"));
	
	private ButtonGroup m_butGroup_0 = new ButtonGroup();
	//ignore check boxes
	private JCheckBox m_subIgnCheckBox = new JCheckBox(getGuiStrs("ignoreSubCheckButtonCaption"), false);
	
	private JCheckBox m_noFilterBookmarkCheckBox = new JCheckBox(getGuiStrs("ignoreBookmarkButtonCaption"), false);
	
	private JCheckBox m_noFilterPrintedCheckBox = new JCheckBox(getGuiStrs("ignorePrintedButtonCaption"), false);
	
	private JCheckBox m_noFilterDatesCheckBox = new JCheckBox(getGuiStrs("ignoreDatesButtonCaption"), false);
	
	private JCheckBox m_noFilterLinksCheckBox = new JCheckBox(getGuiStrs("ignoreLinksButtonCaption"), false);
	
	private JCheckBox m_activeFilterBookmarkCheckBox = new JCheckBox(getGuiStrs("activeFilterBookmarkButtonCaption"), true);
	
	private JCheckBox m_activeFilterPrintedCheckBox = new JCheckBox(getGuiStrs("activeFilterPrintedButtonCaption"), true);
	
	private JCheckBox m_notActiveFilterBookmarkCheckBox = new JCheckBox(getGuiStrs("notActiveFilterBookmarkButtonCaption"), false);
	
	private JCheckBox m_notActiveFilterPrintedCheckBox = new JCheckBox(getGuiStrs("notActiveFilterPrintedButtonCaption"), false);
	
	private JCheckBox m_activeFilterLinkCheckBox = new JCheckBox(getGuiStrs("activeFilterLinkButtonCaption"), true);
	
	private JCheckBox m_notActiveFilterLinkCheckBox = new JCheckBox(getGuiStrs("notActiveFilterLinkButtonCaption"), false);
	
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN , 12);
	
	private JTextField m_dateFrom = new JTextField(10);
	
	private JTextField m_dateTo = new JTextField(10);
	
	PaFilterInfo m_infoFilter =  new PaFilterInfo();

	/**
	 * Detects the type of operation after closing dialog
	 */
	private PaFilterType m_closeFlag = PaFilterType.CANCEL;
	
	private JLabel jlab_0 = new JLabel(" ");
	
	public JButton m_Ok, m_Cancel, m_ClearFiltr;

	public PaImagesFilterDialog (JFrame jfrm,PaFilterInfo fInfo) {
		
		super (jfrm, getGuiStrs("filterImagesDialogCaption"), true); 
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		m_infoFilter = fInfo;
		
		m_allAlbomsRadioBut.setActionCommand("01");
		
		m_oneAlbomRadioBut.setActionCommand("02");
		
		if (PaUtils.get().getMainContainer().getCurrentContainer() == null) {
			
			m_oneAlbomRadioBut.setEnabled(false);
			
		} else {
			
			m_oneAlbomRadioBut.setText(getGuiStrs("inTheAlbomalbomCaptionFilterDialog")+" : <" + PaUtils.get().getAlbumContainer().
					getAlbum(PaUtils.get().getMainContainer().getCurrentContainer().
							getId()).getName() + ">");
		}
		
		MyItemListener myItemListener = new MyItemListener();
		
		m_allAlbomsRadioBut.addItemListener(myItemListener);
		
		m_oneAlbomRadioBut.addItemListener(myItemListener);
		
		add(createGUI());
		
		setListeners();
		
		setBounds(250, 150, 225, 220);

		pack();
		
		setResizable(false);
		
		setInitialValues();
	}
	
	private void setListeners() {
		
		class Forwarder implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
				
				if ( e.getSource() == m_Ok ) onOK(e);
				
				if ( e.getSource() == m_Cancel ) onCancel(e);
				
				if ( e.getSource() == m_ClearFiltr ) clearFilter(e);
				
				if (e.getSource() == m_subIgnCheckBox ) {	
					
					 m_subjectsPanel.setEnabledAll(!m_subIgnCheckBox.isSelected());		
					 
				}
				if (e.getSource() == m_noFilterBookmarkCheckBox ) {	
					
					m_activeFilterBookmarkCheckBox.setEnabled(!m_noFilterBookmarkCheckBox.isSelected());
					
					m_notActiveFilterBookmarkCheckBox.setEnabled(!m_noFilterBookmarkCheckBox.isSelected());
					
				}
				if (e.getSource() == m_noFilterPrintedCheckBox ) {	
					
					m_activeFilterPrintedCheckBox.setEnabled(!m_noFilterPrintedCheckBox.isSelected());
					
					m_notActiveFilterPrintedCheckBox.setEnabled(!m_noFilterPrintedCheckBox.isSelected());
					
				}
				if (e.getSource() == m_noFilterDatesCheckBox ) {	
					
					m_dateFrom.setEnabled(!m_noFilterDatesCheckBox.isSelected());		
					
					m_dateTo.setEnabled(!m_noFilterDatesCheckBox.isSelected());
					
				}
				if (e.getSource() == m_noFilterLinksCheckBox ) {	
					
					m_activeFilterLinkCheckBox.setEnabled(!m_noFilterLinksCheckBox.isSelected());
					
					m_notActiveFilterLinkCheckBox.setEnabled(!m_noFilterLinksCheckBox.isSelected());
				}
			
			}
		};
		
		Forwarder forwarder = new Forwarder();
		
		m_subIgnCheckBox.addActionListener(forwarder);
		
		m_noFilterBookmarkCheckBox.addActionListener(forwarder);
		
		m_noFilterPrintedCheckBox.addActionListener(forwarder);
		
		m_noFilterDatesCheckBox.addActionListener(forwarder);	
		
		m_noFilterLinksCheckBox.addActionListener(forwarder);
		
		m_Ok.addActionListener(forwarder);
		
		m_Cancel.addActionListener(forwarder);
		
		m_ClearFiltr.addActionListener(forwarder);
	}
	/**
	 * <p>enum for type of filter operation<p>
	 */
	public enum PaFilterType 
	{		
		ALL_ALBOMS,
		
		SINGLE_ALBOM,
		
		CANCEL;
	}
	
    class MyItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

        }
    }
	
	
	/**
	 * Sets initial values for filter
	 */
	private void setInitialValues() 
	{
		m_oneAlbomRadioBut.setSelected(true);
		
		if(m_infoFilter == null) {
			
			m_infoFilter = new PaFilterInfo();
		}
	
		m_dateTo.setText(dateToString(m_infoFilter.getDateTo(),GUI_DATE_FORMAT));
		
		m_dateFrom.setText( dateToString(m_infoFilter.getDateFrom(),GUI_DATE_FORMAT) );
		
		boolean isIgn = m_infoFilter.isSubjectsIgnored();
		
		m_subIgnCheckBox.setSelected(isIgn);
		
		m_subjectsPanel.setEnabledAll(!isIgn);
		
		m_subjectsPanel.clearAllSelected();
		
		if(!isIgn){
			
			m_subjectsPanel.setSubjectsToBeSelected(m_infoFilter.getSubjectsList() );	
		}
	
		if(m_infoFilter.isBookmarkActiveSelected()) {
			
			m_activeFilterBookmarkCheckBox.setSelected(true);
		}
		else {
			
			m_notActiveFilterBookmarkCheckBox.setSelected(true);
		}
		if(m_infoFilter.isPrintedActiveSelected()) {
			
			m_activeFilterPrintedCheckBox.setSelected(true);
		}
		else {
			
			m_notActiveFilterPrintedCheckBox.setSelected(true);
		}
		
		boolean bI = m_infoFilter.isBookmarkedIgnored();
		
		m_noFilterBookmarkCheckBox.setSelected(bI);
		
		m_activeFilterBookmarkCheckBox.setEnabled(!bI);
		
		m_notActiveFilterBookmarkCheckBox.setEnabled(!bI);
		
		bI = m_infoFilter.isPrintedIgnored();
		
		m_noFilterPrintedCheckBox.setSelected(bI);
		
		m_activeFilterPrintedCheckBox.setEnabled(!bI);
		
		m_notActiveFilterPrintedCheckBox.setEnabled(!bI);
		
		bI = m_infoFilter.isLinksIgnored();
		
		m_noFilterLinksCheckBox.setSelected(bI);
		
		m_activeFilterLinkCheckBox.setEnabled(!bI);
		
		m_notActiveFilterLinkCheckBox.setEnabled(!bI);
		

		bI = m_infoFilter.isDatesIgnored();
		
		m_noFilterDatesCheckBox.setSelected(bI);
		
		m_dateTo.setEnabled(!bI);
		
		m_dateFrom.setEnabled(!bI);
	
	
	}
	
	public ArrayList<PaSubject> getSubjects() {
		
		return m_subjectsPanel.get_temListModelPhoto().get_temsList();
	}

	public String getDateFrom() {
		
		return m_dateFrom.getText();
	}

	public String getDateTo() {
		
		return m_dateTo.getText();
	}

	public PaFilterType getClosedFlagValue () {
		
		return m_closeFlag;
	}
	
	
	public boolean isSubjectsIgnored () {
		
		return m_subIgnCheckBox.isSelected();
		
	}
	/**
	 * Is invoked for Ok button
	 * @param e
	 */
	public void onOK(ActionEvent e) {
		
		if ((m_butGroup_0.getSelection().getActionCommand()).equals("01")) {
			
			m_closeFlag = PaFilterType.ALL_ALBOMS;
			
			PaUtils.get().getMainContainer().getCurrentContainer().setFilter(getFilter());
			
			dispose();
		}
		if ((m_butGroup_0.getSelection().getActionCommand()).equals("02")) {
			
			m_closeFlag = PaFilterType.SINGLE_ALBOM;
			
			PaUtils.get().getMainContainer().getCurrentContainer().setFilter(getFilter());
			
			dispose();
		}
	}
	
	/**
	 * Is called for Cancel button
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		m_closeFlag = PaFilterType.CANCEL;
		
		dispose();
	}	
	
	/**
	 * Clears the filter to the default state; the default constructor of class PaFilterInfo 
	 * determines the default filter info
	 * @param e
	 */
	public void clearFilter(ActionEvent e) {
		
		m_infoFilter = new PaFilterInfo();
		
		setInitialValues();
		
		PaUtils.get().getMainContainer().getCurrentContainer().setFilter(getFilter());
	}
	
	public void onCheckBox(ActionEvent e) {
			/**
			 * 
			 */
	}
		
	/**
	 * 
	 * @return the main panel with all UI components on it
	 */
	private JPanel createGUI() {


		m_subjectsPanel = new PaSubjectsPanel(false, new ArrayList<Integer>(), PaUtils.get().getSubjectsContainer(), 
				getGuiStrs("subjectsForFilterGroupCaption")); 


		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();


		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(12,12,12,12));
		
		JPanel panel_ButR_0 = new JPanel();
		
		panel_ButR_0.setLayout(new GridLayout(1, 2));
		
		panel_ButR_0.add(m_oneAlbomRadioBut);
		
		panel_ButR_0.add(m_allAlbomsRadioBut);
		
		m_butGroup_0.add(m_allAlbomsRadioBut);
		
		m_butGroup_0.add(m_oneAlbomRadioBut);
		
		m_oneAlbomRadioBut.setFont(font);
		
		m_allAlbomsRadioBut.setFont(font);
		
		m_subIgnCheckBox.setFont(font);
		
		//panel of filter ignoring
		JPanel panelIgnore = new JPanel();
		
		panelIgnore.setLayout(new GridLayout(3, 2));
		
		panelIgnore.add(m_subIgnCheckBox);
		
		panelIgnore.add(m_noFilterDatesCheckBox);
		
		panelIgnore.add(m_noFilterBookmarkCheckBox);
		
		panelIgnore.add(m_noFilterPrintedCheckBox);
		
		panelIgnore.add(m_noFilterLinksCheckBox);
		
		Border borderIgnore = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("ignoreFilterOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,font,Color.RED);
		
		panelIgnore.setBorder(borderIgnore);

		JPanel panelBookPrint = new JPanel();
		
		panelBookPrint .setLayout(new GridLayout(3, 2));
		
		panelBookPrint .add(m_activeFilterBookmarkCheckBox);
		
		panelBookPrint .add(m_notActiveFilterBookmarkCheckBox);
		
		panelBookPrint.add(m_activeFilterPrintedCheckBox);
		
		panelBookPrint .add(m_notActiveFilterPrintedCheckBox);
		
		panelBookPrint .add(m_activeFilterLinkCheckBox);
		
		panelBookPrint .add(m_notActiveFilterLinkCheckBox);

		ButtonGroup groupPanelBook = new ButtonGroup();
		
		ButtonGroup groupPanelPrint = new ButtonGroup();
		
		ButtonGroup groupPanelLink = new ButtonGroup();
		
		groupPanelBook.add(m_activeFilterBookmarkCheckBox);
		
		groupPanelBook.add(m_notActiveFilterBookmarkCheckBox);
		
		groupPanelPrint.add(m_activeFilterPrintedCheckBox);
		
		groupPanelPrint.add(m_notActiveFilterPrintedCheckBox);
		
		groupPanelLink.add(m_activeFilterLinkCheckBox );
		
		groupPanelLink.add( m_notActiveFilterLinkCheckBox);

		Border borderBookPrint = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("bookPrintFilterOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,font);
		
		panelBookPrint.setBorder( borderBookPrint);

		JPanel panelFilterOptions = PaGuiTools.createHorizontalPanel();
		
		panelFilterOptions.add(panelIgnore);
		
		panelFilterOptions.add(panelBookPrint);

		m_noFilterBookmarkCheckBox.setFont(font);
		
		m_noFilterPrintedCheckBox.setFont(font);
		
		m_noFilterDatesCheckBox.setFont(font);
		
		m_activeFilterBookmarkCheckBox.setFont(font);
		
		m_activeFilterPrintedCheckBox.setFont(font);
		
		m_notActiveFilterBookmarkCheckBox.setFont(font);
		
		m_notActiveFilterPrintedCheckBox.setFont(font);
		
		m_noFilterLinksCheckBox.setFont(font);
		
		m_notActiveFilterLinkCheckBox.setFont(font);
		
		m_activeFilterLinkCheckBox.setFont(font);
		
		m_subIgnCheckBox.setAlignmentY(LEFT_ALIGNMENT);
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("aLbomsFilterOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,font);

		panel_ButR_0.setBorder(titled_0);
		

		Border etched_1 = BorderFactory.createEtchedBorder();


		JPanel date_1 = PaGuiTools.createHorizontalPanel();
		
		date_1.add(Box.createHorizontalStrut(6));
		
		JLabel jlab_from = new JLabel(getGuiStrs("fromLabelFilterDialog"));

		
		jlab_from.setFont(font);
		
		date_1.add(jlab_from);
		
		date_1.add(Box.createHorizontalStrut(3));
		
		date_1.add(m_dateFrom);
		
		m_dateFrom.setBorder(BorderFactory.createLoweredBevelBorder());
		

		date_1.add(Box.createHorizontalStrut(12));
		
		JLabel jlab_to = new JLabel(getGuiStrs("toLabelFilterDialog"));
		
		jlab_to.setFont(font);
		
		date_1.add(jlab_to);
		
		date_1.add(Box.createHorizontalStrut(3));
		
		date_1.add(m_dateTo);
		
		m_dateTo.setBorder(BorderFactory.createLoweredBevelBorder());

		Border titled_2 = BorderFactory.createTitledBorder(etched_1, getGuiStrs("dateCaptionGroupFilterDialog"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,font);
		
		date_1.setBorder(titled_2);

	
		JPanel date_2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));	
		
		date_2.add(date_1);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
			
		m_Ok = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_Cancel = new JButton(getGuiStrs("buttonCancelCaption"));
		
		m_ClearFiltr = new JButton(getGuiStrs("buttonClearFilterCaption"));
		
		panel_Ok_Cancel.add(m_Ok);
		
		panel_Ok_Cancel.add(m_Cancel);
	
		south_right.add(jlab_0);
		
		south_right.add(m_ClearFiltr);
		
		south_right.add(panel_Ok_Cancel);
						
		PaGuiTools.setGroupAlignmentY(
				new JComponent[] {m_subjectsPanel.getMainPanel(), date_2, south_right,
						panel_MAIN },
									Component.RIGHT_ALIGNMENT);
		
		paglobal.PaGuiTools.createRecommendedMargin(new JButton[] { m_Ok, m_Cancel, m_ClearFiltr } );

		//this removes the endless height of these fields
		paglobal.PaGuiTools.fixTextFieldSize(m_dateFrom);
		
		paglobal.PaGuiTools.fixTextFieldSize(m_dateTo);

		panel_MAIN.add(Box.createVerticalGlue());
		
		panel_MAIN.add(panel_ButR_0);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT/2));
		
		panel_MAIN.add(panelFilterOptions);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT/2));
		
		panel_MAIN.add(date_2);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT/2));	
		
		panel_MAIN.add(m_subjectsPanel.getMainPanel());
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(south_right);		

		setToolTips();
		
		return panel_MAIN;
	}
	/**
	 * Sets all tooltips
	 */
	private void setToolTips() {
		
		m_allAlbomsRadioBut.setToolTipText(getGuiStrs("allAlbomsFilterRadioToolTip"));
		
		m_oneAlbomRadioBut.setToolTipText(getGuiStrs("oneAlbomFilterRadioToolTip"));
		
		m_subIgnCheckBox.setToolTipText(getGuiStrs("noSubFilterCheckToolTip"));
		
		m_dateFrom.setToolTipText(getGuiStrs("dateFromFieldFilterToolTip"));
		
		m_dateTo.setToolTipText(getGuiStrs("dateToFieldFilterToolTip"));
		
		m_noFilterBookmarkCheckBox.setToolTipText(getGuiStrs("noFilterBookmarkCheckBoxToolTip")); 
		
		m_noFilterPrintedCheckBox.setToolTipText(getGuiStrs("noFilterPrintedCheckBoxToolTip"));
		
		m_noFilterDatesCheckBox.setToolTipText(getGuiStrs("noFilterDatesCheckBoxToolTip"));
		
		m_activeFilterBookmarkCheckBox.setToolTipText(getGuiStrs("activeFilterBookmarkCheckBoxToolTip"));
		
		m_activeFilterPrintedCheckBox.setToolTipText(getGuiStrs("activeFilterPrintedToolTip"));
		
		m_notActiveFilterBookmarkCheckBox.setToolTipText(getGuiStrs("notActiveFilterBookmarkCheckBoxToolTip"));
		
		m_notActiveFilterPrintedCheckBox.setToolTipText(getGuiStrs("notActiveFilterPrintedToolTip"));
		
		m_notActiveFilterLinkCheckBox.setToolTipText(getGuiStrs("notActiveFilterLinkCheckBoxToolTip"));
		
		m_activeFilterLinkCheckBox.setToolTipText(getGuiStrs("activeFilterLinkToolTip"));
		
		m_noFilterLinksCheckBox.setToolTipText(getGuiStrs("noFilterLinkCheckBoxToolTip"));
	}
	/**
	 * 
	 * @return the current info object about filter 
	 */
	public PaFilterInfo getFilter() {
		
		return new PaFilterInfo(getSubjects(), 
				PaUtils.stringToDate(m_dateFrom.getText(),GUI_DATE_FORMAT),
				PaUtils.stringToDate(m_dateTo.getText(),GUI_DATE_FORMAT), 
				m_subIgnCheckBox.isSelected(),
				m_noFilterBookmarkCheckBox.isSelected(), 
				m_noFilterPrintedCheckBox.isSelected(), 
				m_noFilterDatesCheckBox.isSelected(), 
				m_activeFilterBookmarkCheckBox.isSelected(),
				m_activeFilterPrintedCheckBox.isSelected(),
				m_noFilterLinksCheckBox.isSelected(),
				m_activeFilterLinkCheckBox.isSelected()) ;
	}
}
