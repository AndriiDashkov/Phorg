package PaDialogs;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
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
import PaForms.PaSubjectsTabModel;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;

/**
 * @author avd
 *
 */
public class PaSubjectFindDialog extends JDialog {


	private static final long serialVersionUID = 1L;
	
	private Forwarder forwarder = new Forwarder();
	
	JButton m_OkButton = new JButton(getGuiStrs("buttonFindCaption"));
	
	JButton m_cancel = new JButton(getGuiStrs("buttonCancelCaption"));
	
	JLabel m_infoLabel;

	private HashSet<Integer> m_findResults = new HashSet<Integer>();
	
	private PaSubjectsTabModel m_refModel;
	
	JTextField m_textF = new JTextField();
	
	Iterator<Integer> m_findIterator;
	
	private String m_oldValue = new String();
	
	/**
	 * @param jfrm
	 * @param model
	 */
	public  PaSubjectFindDialog (JFrame jfrm,  PaSubjectsTabModel model) {
		
		super (jfrm, getGuiStrs("findTermDialogCaption"), true);
		
		m_refModel =  model;
		
		//close operation
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		setFocusTraversalPolicy(new TabOrder());
		
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);

		PaUtils.setComponentsFont (this, PaUtils.get().getBaseFont() );
		
		pack();
		
		setResizable(false);
	}
	

	private JPanel createGUI () {
		
		JPanel mainPanel = PaGuiTools.createVerticalPanel();

		mainPanel.setBorder( BorderFactory.createEmptyBorder(12,12,12,12));
				
		JPanel panel_set = PaGuiTools.createHorizontalPanel();
		
		JLabel jLabel = new JLabel(getGuiStrs("findComboLabelName")+" "); 
		
		panel_set.add(jLabel);
		
		panel_set.add(m_textF);
		
		m_textF.setBorder(BorderFactory.createLoweredBevelBorder());
		
		m_textF.setSize(200, m_textF.getSize().height);
			
	
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,3,5,0) );
		
		panel_Ok_Cancel.add(Box.createHorizontalStrut(100));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_cancel);
		
		JPanel panelInfo = PaGuiTools.createHorizontalPanel();
		
		m_infoLabel = new JLabel(" "); 
		
		panelInfo.add(m_infoLabel);
		
		panelInfo.add(Box.createHorizontalGlue());
	
		
		mainPanel.add(Box.createVerticalStrut(12));
		
		mainPanel.add(panel_set);
		
		mainPanel.add(Box.createVerticalStrut(12));
		
		mainPanel.add(panelInfo);
		
		mainPanel.add(Box.createVerticalStrut(12));
		
		mainPanel.add(panel_Ok_Cancel);

		m_OkButton.addActionListener(forwarder);
		
		m_cancel.addActionListener(forwarder);
		
		m_textF.addKeyListener(new EnterListener());
		
		return mainPanel;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK();
			
			if ( e.getSource() == m_cancel ) onCancel();

		}
	}

	/**
	 * Starts the find operation 
	 */
	public void onOK() 
	{
		findOperation();	
	}
	
	/**
	 * Closes the dilog
	 */
	public void onCancel() {

		dispose();
	}	

	
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
				
				Integer index = m_findIterator.next();
				
				m_refModel.setRowSelected(index);
				
				PaUtils.get().getSubjectsForm().ensureVisible(index);
				
				flag = true;
			}
	
		}
		else {
			if  ( m_findIterator.hasNext() ) {
				
				Integer index = m_findIterator.next();
				
				m_refModel.setRowSelected(index);
				
				PaUtils.get().getSubjectsForm().ensureVisible(index);
				
				flag = true;
			}
			else {
				
				Iterator<Integer> it1 = m_findResults.iterator();
				
				m_findIterator = it1;
				
				if  ( m_findIterator.hasNext() ) {
					
					Integer index = m_findIterator.next();
					
					m_refModel.setRowSelected(index);
					
					PaUtils.get().getSubjectsForm().ensureVisible(index);
					
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
	  * Cathes the Enter button press event to start the find operation
	  * @author avd
	  *
	  */
	 private class EnterListener implements KeyListener {


		@Override
		public void keyPressed(KeyEvent arg0) {
		
		}

		@Override
		public void keyReleased(KeyEvent e) {
	
			    int key=e.getKeyCode();
			    
			    if(e.getSource()== m_textF)
			    {
			        if(key==KeyEvent.VK_ENTER)
			        { 
			        	onOK();                   
			        }
			    }
			
		}


		@Override
		public void keyTyped(KeyEvent arg0) {
			
	
		} 
	 }
	 
		/**
		 * 
		 * @author avd
		 * <p>Sets tab order for all elements<p>
		 */
		 private class TabOrder extends FocusTraversalPolicy {
						
		       public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
		    	   
		           if(aComponent.equals( m_textF)) return m_OkButton;
		           
		           else if(aComponent.equals(m_OkButton)) return 	m_cancel;
		           
		           else if(aComponent.equals(m_cancel)) return m_textF;
		           
		           return m_textF;
		       }
		      
		       public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
		    	   
		           if(aComponent.equals(m_textF)) return m_cancel;
		           
		           else if(aComponent.equals(m_cancel)) return 	m_OkButton;
		           
		           else if(aComponent.equals(m_OkButton)) return  m_textF;
		           return  m_textF;
		       }
		      
		       public Component getDefaultComponent(Container focusCycleRoot) {
		    	   
		           return  m_textF;
		       }
		      
		       public Component getFirstComponent(Container focusCycleRoot) {
		    	   
		           return m_textF;
		       }
		      
		       public Component getLastComponent(Container focusCycleRoot) {
		    	   
		           return m_cancel;
		       }
		  }
}








