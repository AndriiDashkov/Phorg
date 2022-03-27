package PaDialogs;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import PaForms.PaAlbumsTreeForm;
import PaGlobal.PaGuiTools;
import PaGlobal.PaButtonEnter;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;
/**
 * 
 * @author avd
 * <p>Dialog window for find album operation</p>
 */
public class PaAlbumFindDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private Forwarder forwarder = new Forwarder();
	
	PaButtonEnter m_OkButton = new PaButtonEnter(getGuiStrs("buttonFindCaption"));
	
	PaButtonEnter m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	JLabel m_infoLabel;

	private HashSet<Integer> findResults = new HashSet<Integer>();

	private PaAlbumsTreeForm m_treeForm;
	
	JTextField textF = new JTextField();
	
	Iterator<Integer> findIterator;
	
	private String oldValue = new String();
	
	/**
	 * 
	 * @param jfrm - main application frame
	 * @param form - tree container, where the find operation is required
	 */
	public  PaAlbumFindDialog (JFrame jfrm,  PaAlbumsTreeForm form) {
		
		super (jfrm, getGuiStrs("albomFindDialogFindCaption"), true); 

		
		m_treeForm =  form;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		pack();
		
		setResizable(false);
		
		setToolTips();
		
		 textF.addKeyListener(new EnterListener());
	}
	

	private JPanel createGUI () {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
				
		
		JPanel panel_set = PaGuiTools.createHorizontalPanel();
		
		JLabel jLabel = new JLabel(getGuiStrs("findComboLabelName")+"  ");
		
		panel_set.add(jLabel);
		
		panel_set.add(textF);
		
		textF.setBorder(BorderFactory.createLoweredBevelBorder());
		
		textF.setSize(200, textF.getSize().height);
			
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,3,5,0) );
		
		panel_Ok_Cancel.add(Box.createHorizontalStrut(100));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		JPanel panelInfo = PaGuiTools.createHorizontalPanel();
		
		m_infoLabel = new JLabel(" "); 
		
		panelInfo.add(m_infoLabel);
		
		panelInfo.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_set);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelInfo);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_Ok_Cancel);

		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		return panel_MAIN;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK();
			
			if ( e.getSource() == m_CancelButton ) onCancel();

		}
	}
	
	/**
	 * m_Ok button procedure
	 */
	public void onOK() 
	{
		findOperation();	
	}
	
	/**
	 * 
	 */
	public void onCancel() {

		dispose();
	}	

	/**
	 * <p>Find album operation</p>
	 */
	private void findOperation()
	{
		boolean flag = false;
		
		if ( ! oldValue.equals(  textF.getText() ) ) {
			
			oldValue = textF.getText();
			
			findResults.clear();
			
			m_treeForm.findRows(oldValue,findResults);
			
			Iterator<Integer> it = findResults.iterator();
			
			findIterator = it;
			
			if  ( findIterator.hasNext() ) {
				
				Integer index = findIterator.next();
				
				m_treeForm.setRowSelected(index);
				
				m_treeForm.ensureVisible(index);
				
				flag = true;
			}
	
		}
		else {
			if  ( findIterator.hasNext() ) {
				
				Integer index = findIterator.next();
				
				m_treeForm.setRowSelected(index);
				
				m_treeForm.ensureVisible(index);
				
				flag = true;
			}
			else {
				
				Iterator<Integer> it1 = findResults.iterator();
				
				findIterator = it1;
				
				if  ( findIterator.hasNext() ) {
					
					Integer index = findIterator.next();
					
					m_treeForm.setRowSelected(index);
					
					m_treeForm.ensureVisible(index);
					
					flag = true;
				}
			}
	
		}
		if(flag) {
			
			m_infoLabel.setText(getMessagesStrs("subjectHasBeenFoudMessage"));
			
			m_infoLabel.setVisible(true);
			
			m_infoLabel.setForeground(Color.BLUE);
			
		}
		else {
			
			m_infoLabel.setText(getMessagesStrs("subjectHasNotBeenFoudMessage"));
			
			m_infoLabel.setVisible(true);
			
			m_infoLabel.setForeground(Color.RED);
		}
		
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		textF.setToolTipText(getGuiStrs("findFieldAlbomToolTip"));
				 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 
	 /**
	  * Controls the Enter button press event to start the find operation
	  * @author avd
	  *
	  */
	 private class EnterListener implements KeyListener {


		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent e) {
	
			    int key=e.getKeyCode();
			    
			    if(e.getSource()== textF)
			    {
			        if(key==KeyEvent.VK_ENTER)
			        { 
			        	onOK();                   
			        }
			    }
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {} 
	 }

}

