package PaDialogs;

import PaCollection.PaAlbumContainer;
import PaGlobal.*;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import PaGlobal.PaGuiTools;
import static PaGlobal.PaUtils.*;
/**
 * <p>Dialog class for choosing of merge operation options</p>
 */
public class PaAlbumsMergeDialog extends JDialog {


	private static final long serialVersionUID = 1L;
	
	private PaCloseFlag m_flag = PaCloseFlag.CANCEL;
	
	Integer m_resId;
	
	private Forwarder forwarder = new Forwarder();
	
	private Font m_font = new Font(Font.SANS_SERIF, Font.PLAIN , 12); 
	
	
	public PaButtonEnter m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption") );
	
	public PaButtonEnter m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption") );

	private JRadioButton m_radioCopy = new JRadioButton(getGuiStrs("copyImagesMergeAlbomRadioButtonLabel"));
	
	private JRadioButton m_radioNoCopy = new JRadioButton(getGuiStrs("copyOnlyLinksMergeAlbomRadioButtonLabel"));
	
	
	private ButtonGroup butGroup_0 = new ButtonGroup(); 
	
	private JComboBox<String> m_comboAlbums;
	
	private HashMap<String,Integer> m_comboMap;
	
	private String m_albomsNames = new String();
	
	private ArrayList<Integer> m_listIds;
	
	private PaAlbumContainer m_rContainer;
	
	
	public PaAlbumsMergeDialog (JFrame jfrm, PaAlbumContainer cont, ArrayList<Integer> list) {
		
		super (jfrm, getGuiStrs("mergeAlbomsDialogCaption"), true); 
		
		 m_listIds = list;
		 
		 m_rContainer = cont;
	
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();

			}
		});
		
		init();

		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		pack();
		
		setResizable(false);
	}
	/**
	 * <p>Inits the dialog with info for merge operation</p>
	 */
	private void init() {
		
		m_comboMap = new HashMap<String,Integer>();
		
		Iterator<Integer> it = m_listIds.iterator();
		
		String[] ar = new String[ m_listIds.size()];
		
		int k=0;
		
		int k1=0;
		
		while( it.hasNext()) {
			
			Integer i= it.next();
			
			String name = m_rContainer.getAlbum(i).getName();
			
			m_albomsNames =  m_albomsNames + name + ",";
			
			k1++;
			
			if ( k1 == 3 ) {
				
				m_albomsNames += NEXT_ROW;
				
				k1=0;
			}
			
			m_comboMap.put(name,i);
			
			ar[k] = name; 
			
			k++;
		}
		
		m_albomsNames = m_albomsNames.substring(0,m_albomsNames.length()-1);
		
		m_comboAlbums =new JComboBox<String>(ar);
		
		m_radioNoCopy.setSelected(true);
		
		setResultId();
		
	}
	/**
	 * <p>Creates and layouts all gui elements</p>
	 * @return main panel
	 */
	private JPanel createGUI () {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,
				VERT_STRUT,VERT_STRUT));
				
		
		JPanel panel_set = PaGuiTools.createHorizontalPanel();
		
		JLabel jrez = new JLabel(getGuiStrs("chooseResultAlbomMergeAlbomsCaption"));//Âûבמנ טעמדמגמדמ אכüבמלא:  ");
		
		jrez.setFont(m_font);
		
		m_radioCopy.setFont(m_font);
		
		m_radioNoCopy.setFont(m_font);
		
		panel_set.add(jrez);
		
		panel_set.add(m_comboAlbums);
		
		m_comboAlbums.setBorder(BorderFactory.createLoweredBevelBorder());
			
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new GridLayout(2, 1));
		
		panelButtons.add(m_radioNoCopy);
		
		panelButtons.add(m_radioCopy);
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("mergeOperationOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		panelButtons.setBorder(titled_0);
		
		butGroup_0.add(m_radioCopy);
		
		butGroup_0.add(m_radioNoCopy);
		
		JPanel panel_center = PaGuiTools.createHorizontalPanel();
		
		JLabel jpath = new JLabel(getMessagesStrs("mergeAlbomsMessage") + NEXT_ROW +
				m_albomsNames); 
		
		jpath.setFont(m_font);
		
		panel_center.add(jpath);
		
		panel_center.add(Box.createHorizontalGlue());

		JPanel south = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		//listeners
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_comboAlbums.addActionListener(forwarder);
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
		
		south.add(panel_Ok_Cancel);
		
		panel_MAIN.add(Box.createVerticalStrut(12));
		
		panel_MAIN.add(panel_set);
		
		panel_MAIN.add(Box.createVerticalStrut(12));
		
		panel_MAIN.add(panelButtons);
		
		panel_MAIN.add(Box.createVerticalStrut(12));
		
		panel_MAIN.add(panel_center);
		
		panel_MAIN.add(Box.createVerticalStrut(6));

		panel_MAIN.add(south);
		
		setToolTips();
		
		jpath.setForeground(PaUtils.get().getSelectionColor() );
		
		return panel_MAIN;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() ==  m_comboAlbums ) setResultId();

		}
	}
	/**
	 * <p>Performs reaction for m_Ok button</p>
	 * @param e - action event
	 */
	public void onOK(ActionEvent e) 
	{
		String selectedName = (String) m_comboAlbums.getSelectedItem();
		
		int n = JOptionPane.showConfirmDialog(
			    PaUtils.get().getMainWindow(),
			    getMessagesStrs("startMergeOperationQuestion") + NEXT_ROW +
			    getMessagesStrs("targetAlbomCaptionMessage")+" "+ selectedName,
			    getMessagesStrs("messageAnswerCaption"),
			    JOptionPane.YES_NO_OPTION);
		
		if ( n == JOptionPane.YES_OPTION) {

			m_flag = PaCloseFlag.OK;
			
			dispose();
		}
	}
	
	/**
	 * <p>Performs reaction for m_Cancel button</p>
	 * @param e - action event
	 */
	public void onCancel(ActionEvent e) {
		
		m_flag = PaCloseFlag.CANCEL;
		
		dispose();
	}	
	
	/**
	 * <p>the target album id (all selected albums will be merged with it)</p>
	 * @return the target albom id (all selected albums will be merged with it)
	 */
	public Integer resultId()
	{
		return m_resId;
	}
	
	/**
	 * <p>Sets result albom id using the data from albums combobox</p>
	 */
	public void setResultId()
	{
		String selectedName = (String) m_comboAlbums.getSelectedItem();
		
		m_resId =  m_comboMap.get( selectedName );
	}
	
	/**
	 * <p>Returns the value of copy flag. </p>
	 * @return if it is true then images from selected albom will be copied to standard path of target albom
	 */
	public boolean isCopyFiles()
	{
		return m_radioCopy.isSelected();
	}
	
	/**
	 * <p>Gets the list of ids of merged alboms</p>
	 */
	public ArrayList<Integer> getMergedIdsList()
	{
		return m_listIds;
	}
	
	/**
	 * <p>Returns of state of dialog disposing</p>
	 * @return close flag for this dialog (OK or CANCEL)
	 */
	public PaCloseFlag getFlag() { return m_flag; }
	
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	private void setToolTips() {
		 
		m_radioCopy.setToolTipText(getGuiStrs("copyImagesMergeAlbomRadioToolTip"));
		
		m_radioNoCopy.setToolTipText(getGuiStrs("noCopyImagesMergeAlbomRadiToolTip")); 
			
		m_comboAlbums.setToolTipText(getGuiStrs("mergedAlbomsComboBoxListToolTip"));
			 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	}

}
