package padialogs;


import static paglobal.PaUtils.*;

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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paforms.PaImageTabModel;
import paglobal.*;


/**
 * <p>Find image dialog class</p>
 * @author Andrii Dashkov
 *
 */
public class PaImageFindDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JButton m_OkButton = new JButton(getGuiStrs("buttonFindCaption"));
	
	private JButton m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
	
	private JLabel m_infoLabel = new JLabel(" ");

	/**
	 * here we have results of find operation in order to get possibility to move through them
	 */
	private HashSet<Integer> m_findResults = new HashSet<Integer>();

	
	private PaImageTabModel m_refModel;
	
	JTextField m_textF = new JTextField();
	
	Iterator<Integer> m_findIterator;
	
	private String m_oldValue = new String();
	
	
	public  PaImageFindDialog (JFrame jfrm,  PaImageTabModel model) {
		
		super (jfrm, getGuiStrs("findPhotoDialogCaption"), true); 
		
		m_refModel =  model;
		
		add(createGUI());
		
		setBounds(250, 150, 225, 240);

		pack();
		
		setResizable(false);
	}
	
	/**
	 * 
	 * @return main UI panel with all components constructed
	 */
	private JPanel createGUI () {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
				PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
						
		JPanel panel_set = PaGuiTools.createHorizontalPanel();
		
		JLabel jLabel = new JLabel(getGuiStrs("findComboLabelName")+" ");
		
		panel_set.add(jLabel);
		
		panel_set.add(m_textF);
		
		m_textF.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_textF.setSize(200, m_textF.getSize().height);
			
	
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,3,5,0) );
		
		panel_Ok_Cancel.add(Box.createHorizontalStrut(100));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		JPanel panelInfo = PaGuiTools.createHorizontalPanel();
		
		panelInfo.add(m_infoLabel);
		
		panelInfo.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_set);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panelInfo);
		
		panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_Ok_Cancel);

		setListeners();
		
		return panel_MAIN;
	}
	/**
	 * <p>Sets all listeners in the dialog</p>
	 */
	private void setListeners() {
		
		Forwarder forwarder = new Forwarder();
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		EnterListener l = new EnterListener();
		
		m_textF.addKeyListener(l);
		
		m_OkButton.addKeyListener(l);
		
		m_CancelButton.addKeyListener(l);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e){
	
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);

		}
	}

	public void onOK(ActionEvent e) 
	{
		findOperation();	
	}
	
	public void onCancel(ActionEvent e) {
		
		dispose();
	}	

	/**
	 * <p>Main find algorithm</p>
	 */
	private void findOperation()
	{
		boolean flag = false;
		
		if ( ! m_oldValue.equals(  m_textF.getText() ) ) {
			
			m_oldValue = m_textF.getText();
			
			m_findResults.clear();
			
			m_refModel.findRows(m_oldValue,m_findResults);
			
			Iterator<Integer> it = m_findResults.iterator();
			
			m_findIterator = it;
			
			if  ( m_findIterator.hasNext() ) {
				
				m_refModel.setRowSelected(m_findIterator.next());
				
				flag = true;
			}
	
		}
		else {
			if  ( m_findIterator.hasNext() ) {
				
				m_refModel.setRowSelected(m_findIterator.next());
				
				flag = true;
			}
			else {
				
				Iterator<Integer> it1 = m_findResults.iterator();
				
				m_findIterator = it1;
				
				if  ( m_findIterator.hasNext() ) {
					
					m_refModel.setRowSelected(m_findIterator.next());
					
					flag = true;
				}
			}
	
		}
		if(flag) {
			
			m_infoLabel.setText(getMessagesStrs("subjectHasBeenFoudMessage"));
			
			m_infoLabel.setForeground(Color.BLUE);
		}
		else {
			
			m_infoLabel.setText(getMessagesStrs("subjectHasNotBeenFoudMessage"));
			
			m_infoLabel.setForeground(Color.RED);
		}
	}
	
	 /**
	  * Catches the Enter button press event to start the find operation
	  * @author Andrii Dashkov
	  *
	  */
	 private class EnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
		
		}

		@Override
		public void keyReleased(KeyEvent e) {
	
			    int key=e.getKeyCode();
			    
		        if(key==KeyEvent.VK_ENTER)
		        { 
				    if(e.getSource()== m_textF) {  onOK(null);   }
				    
				    if ( e.getSource() == m_OkButton ) {  onOK(null);  }
				    
				    if ( e.getSource() == m_CancelButton ) { onCancel(null); }
		        }
			
		}


		@Override
		public void keyTyped(KeyEvent arg0) {
		
	
		} 
	 }

}