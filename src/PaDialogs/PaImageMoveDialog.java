package padialogs;

import static paglobal.PaUtils.*;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pacollection.PaAlbum;
import pacollection.PaAlbumContainer;
import paglobal.*;



/**
 * Dialog for moving image 
 * @author Andrii Dashkov
 *
 */
public class PaImageMoveDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private PaCloseFlag m_flag = PaCloseFlag.CANCEL;
	
	private Forwarder forwarder = new Forwarder();
	
	private Font m_font =PaUtils.get().getBaseFont();
	
	public JButton Ok = new JButton(getGuiStrs("buttonOkCaption") );
	
	public JButton Cancel = new JButton(getGuiStrs("buttonCancelCaption") );

	private JRadioButton m_radioBut1 = new JRadioButton(getGuiStrs("copyImagesMergeAlbomRadioButtonLabel"));

	private JRadioButton m_radioBut2 = new JRadioButton(getGuiStrs("copyOnlyLinksMergeAlbomRadioButtonLabel"));

	private ButtonGroup butGroup_0 = new ButtonGroup(); 
	
	private JComboBox<String> m_comboAlboms;
	
	private HashMap<String,Integer> comboMap;

	private PaAlbumContainer rContainer = null;
	
	public PaImageMoveDialog (JFrame jfrm, PaAlbumContainer cont) {
		
		super (jfrm, getGuiStrs("movePhotoDialogCaption"), true); 
		
		rContainer = cont;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		init();
	
		add(createGUI());
		
		setBounds(350, 250, 300, 280);
	
		pack();
		
		setResizable(false);
	}
	
	private void init() {
		
		comboMap = new HashMap<String,Integer>();
		
		Iterator<PaAlbum> it = rContainer.getAlbums().iterator();
		
		String[] ar = new String[ rContainer.getAlbums().size() ];
		
	    int k=0;
	    
		while( it.hasNext()) {
			
			PaAlbum al = it.next(); 
			
			Integer i= al.getId();
			
			String name = al.getName();

			comboMap.put(name,i);
			
			ar[k] = name; 
			
			k++;
			
		}
		
		
		m_comboAlboms =new JComboBox<String>(ar);
		
		m_radioBut2.setSelected(true);
		
	}
	private JPanel createGUI () {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
				
		
		JPanel panel_set = PaGuiTools.createHorizontalPanel();
		
		JLabel jrez = new JLabel(getGuiStrs("chooseResultAlbomToMoveImageCaption")+" :  ");
		
		jrez.setFont(m_font);
		
		m_radioBut1.setFont(m_font);
		
		m_radioBut2.setFont(m_font);
		
		panel_set.add(jrez);
		
		panel_set.add(m_comboAlboms);
		
		m_comboAlboms.setBorder(BorderFactory.createLoweredBevelBorder());
			
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new GridLayout(2, 1));
		
		panelButtons.add(m_radioBut1);
		
		panelButtons.add(m_radioBut2);
		
		Border etched_0 = BorderFactory.createEtchedBorder();
		
		Border titled_0 = BorderFactory.createTitledBorder(etched_0, getGuiStrs("moveImageOperationOptionsCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		panelButtons.setBorder(titled_0);
		
		butGroup_0.add(m_radioBut1);
		
		butGroup_0.add(m_radioBut2);
			
		JPanel south = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		Ok.addActionListener(forwarder);
		
		Cancel.addActionListener(forwarder);
		
		panel_Ok_Cancel.add(Ok);
		
		panel_Ok_Cancel.add(Cancel);
		
		south.add(panel_Ok_Cancel);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_set);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelButtons);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));

		panel_MAIN.add(south);

		setToolTips();
		
		return panel_MAIN;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == Ok ) onOK(e);
			
			if ( e.getSource() == Cancel ) onCancel(e);

		}
	}
	/**
	* <p>Removes the album with name from combo box. Usually this is useful for the case of current album</p>
	* @param name - name of the album
	*/
	public void removeAlbumFromList(String name)
	{		
		m_comboAlboms.removeItem(name);	
	}

	public void onOK(ActionEvent e) 
	{
		String selectedName = (String) m_comboAlboms.getSelectedItem();
		
		int n = JOptionPane.showConfirmDialog(
			    PaUtils.get().getMainWindow(),
			    getMessagesStrs("startMoveImagesOperationQuestion") + NEXT_ROW +
			    getMessagesStrs("targetAlbomCaptionMessage")+" "+ selectedName,
			    getMessagesStrs("messageAnswerCaption"),
			    JOptionPane.YES_NO_OPTION); 
		
		if ( n == JOptionPane.YES_OPTION) {
			
			m_flag = PaCloseFlag.OK;
			
			dispose();
		}
	}
	
	/**
	 *  Cancel event listener
	 * @param e - event
	 */
	public void onCancel(ActionEvent e) {
		
		m_flag = PaCloseFlag.CANCEL;
		
		dispose();
	}	
	/**
	* @return the selected album in the combo box
	*/
	public String getSelectedName() { return (String) m_comboAlboms.getSelectedItem(); }
	
	/**
	* @return true if the user selected the case 'copy files to standard folder'
	*/
	public boolean isCopySelected() { return m_radioBut1.isSelected(); }
	
	/**
	* @return the id of selected album in the combo box
	*/
	public Integer getResultAlbomId() { return comboMap.get( (String) m_comboAlboms.getSelectedItem() );}
	
	/**
	* @return the case of dialog closing  1 - confirm operation; 0 - cancel operation
	*/
	public PaCloseFlag getFlag() { return m_flag; }
	
	/**
	 * Sets all tooltips
	 */
	private void setToolTips() {
		
		m_radioBut1.setToolTipText(getGuiStrs("copyImagesStandRadioButtonToolTip"));
		
		m_radioBut2.setToolTipText(getGuiStrs("copyOnlyLinksRadioButtonToolTip"));
		
		m_comboAlboms.setToolTipText(getGuiStrs("targetAlbomComboCopyToolTip"));
			
	}	
}