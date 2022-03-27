package PaDialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import PaCollection.PaSubject;
import PaGlobal.PaGuiTools;
import PaGlobal.PaButtonEnter;
import PaGlobal.PaCloseFlag;
import PaGlobal.PaTokenizer;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.*;
import java.awt.event.KeyAdapter;
/**
 * 
 * @author avd
 * <p>Dialog for add new subject</p>
 */
public class PaSubjectAddDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JLabel m_errorLabel;
	
	private PaButtonEnter m_OkButton, m_CancelButton, m_AddButton;

	private JTextField m_subjectField;
	
	private JPanel m_panel_MAIN;

	private PaCloseFlag m_flag = PaCloseFlag.CANCEL;
	
	

	public PaSubjectAddDialog (JFrame jf, String nameFrame, String keywordName ) { 
		
		super(jf, nameFrame, true);
		
		add(createGUI(keywordName));
		
		setFocusTraversalPolicy(new TabOrder());
		
		setListeners();
		
		PaUtils.setComponentsFont (this, PaUtils.get().getBaseFont() );
		
		setBounds(450, 350, 420, 136);
		
		setResizable(true);
		
		pack();
		
		m_errorLabel.setVisible(false);
		
	}
	/**
	 * <p>Creates UI of the dialog</p>
	 * @param keywordName - is used for 'edit' variant of this dialog -initial value of the name in the text field
	 * @return main dialog JPanel
	 */
	private JPanel createGUI(String keywordName) {
		
		m_panel_MAIN = PaGuiTools.createVerticalPanel();

		m_panel_MAIN.setBorder(BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
		
		setFont(PaUtils.get().getBaseFont());
		
		JPanel panel_buttons = PaGuiTools.createHorizontalPanel();
	
		panel_buttons.add(Box.createHorizontalGlue());
		
		panel_buttons.add(m_AddButton = new PaButtonEnter(getGuiStrs("addPlusButtonCaption"))); 

		panel_buttons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_buttons.add(m_OkButton = new PaButtonEnter(getGuiStrs("buttonOkCaption")));
		
		panel_buttons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_buttons.add(m_CancelButton = new PaButtonEnter(getGuiStrs("buttonCancelCaption")));
		
		JPanel panel_error =  PaGuiTools.createHorizontalPanel();
		
		m_errorLabel = new JLabel(" ");
		
		m_errorLabel.setForeground(Color.RED);
		
		panel_error.add(m_errorLabel);
		
		panel_error.add(Box.createHorizontalGlue());
		
		JPanel panel_text =  PaGuiTools.createHorizontalPanel();
		
		panel_text.add(new JLabel(getGuiStrs("termDialogEditLabelName")+" "));
		
		panel_text.add(m_subjectField = new JTextField(30)); 
		
		PaUtils.get().setFixedSizeBehavior(m_subjectField);
		
		m_subjectField.setText(keywordName);
		
		m_subjectField.setBorder(BorderFactory.createLoweredBevelBorder());

		m_panel_MAIN.add(panel_text);
		
		m_panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		m_panel_MAIN.add(panel_error);
		
		m_panel_MAIN.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		m_panel_MAIN.add(panel_buttons);	
		
		setToolTips();

		return m_panel_MAIN;
	}
	/**
	 * <p>Sets all listeners for this component</p>
	 */
	private void setListeners() {
		
		ActListener forwarder = new ActListener();
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_AddButton.addActionListener(forwarder);
		
		ButtonEnterListener  butList =  new ButtonEnterListener();

		m_subjectField.addKeyListener(butList);
		     
	
	}
	/**
	 * 
	 * @author avd
	 * <p>Listener for Enter button event </p>
	 */
	private class ButtonEnterListener extends KeyAdapter {
		
	    public void keyPressed(KeyEvent e) {
	    	
	         if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	        	 
	        	 Component c = (Component) e.getSource();
	        
	        	 if(c == m_subjectField) {
	        		 
	        	 	 Component cNext = getFocusTraversalPolicy().getComponentAfter(m_panel_MAIN,m_subjectField );
	        	 	 
		        	 cNext.requestFocus();
	        	 }
			}
	    } 
	}
	/**
	 * 
	 * @author avd
	 * <p>Listener for all actions in the dialog</p>
	 */
	private class ActListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) {
				
				m_flag = PaCloseFlag.CANCEL;
				
				dispose();
			}
			if ( e.getSource() == m_AddButton ) onAdd(e);
		}
	}
	
	public void setAddButtonVisible(boolean flag) {
		
		m_AddButton.setVisible(flag);
	}

	/**
	 * 
	 * @param e event
	 */
	public void onOK(ActionEvent e) {
		
		if (!validateName()) {
			
			m_flag = PaCloseFlag.CANCEL;
			
		} else {
			
			m_flag = PaCloseFlag.OK;
			
			dispose();
		}
	}

	public String getSubjectName() {
		
		return m_subjectField.getText();
	}
	
	public PaCloseFlag getClosedFlag() {
		
		return m_flag;
	}
	
	/**
	 * <p>Only checks the name and setss the appropriative flag for this dialog</p>
	 * @param e action event
	 */
	public void onAdd(ActionEvent e) {
		
		writeLog("Subject add operation started with name " + m_subjectField.getText(), null, true, true, true);
		
		if (!validateName()) {
			
			m_flag = PaCloseFlag.CANCEL;
			
		} else {
			
			m_flag = PaCloseFlag.MULTI_ADD;
			
			dispose();
		}
	}
	/**
	 * 
	 * @return true if the subject name string is ok
	 */
	private boolean validateName() {
		
		String s = m_subjectField.getText();
		
		if (m_subjectField.getText().equals("")) {
			
			m_errorLabel.setVisible(true);
			
			String m = getMessagesStrs("noSubjectSymbolsMessage");
			
			m_errorLabel.setText(m); 
			
			writeLog(m, null, true, true, true);
			
			return false;
		}
		if (s.length() > 50) {
			
			m_errorLabel.setVisible(true);
			
			String m = getMessagesStrs("subjectNameTooLongMessage");
			
			m_errorLabel.setText(m); 
			
			writeLog(m, null, true, true, true);
			
			return false;
		} 
		if (!PaTokenizer.isValidate(s)) {
			
			m_errorLabel.setVisible(true);
			
			String m = getMessagesStrs("allowedSymbolsMessage");
			
			m_errorLabel.setText(m); 
			
			writeLog(m, null, true, true, true);
			
			return false;
		} 
		//validation of name duplication
		if(PaUtils.get().getSubjectsContainer().nameExist(new PaSubject(s))){
			
			m_errorLabel.setVisible(true);
			
			String m = getMessagesStrs("subjectWithNameExistMessage");
			
			m_errorLabel.setText(m); 
			
			writeLog(m, null, true, true, true);
			
			return false;
		}
		
		m_errorLabel.setText("");
		
		m_errorLabel.setVisible(false);
		
		return true;
		
	}
	
	/**
	 * 
	 * @author avd
	 * <p>Sets tab order for all elements<p>
	 */
	 private class TabOrder extends FocusTraversalPolicy {
		 
	       public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
	    	   
	           if(aComponent.equals(m_subjectField)) return m_AddButton;
	           
	           else if(aComponent.equals(m_AddButton)) return 	m_OkButton;
	           
	           else if(aComponent.equals(m_OkButton)) return m_CancelButton;
	           
	           else if(aComponent.equals(m_CancelButton)) return m_subjectField;
	           
	           return m_subjectField;
	       }
	      
	       public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
	    	   
	           if(aComponent.equals(m_subjectField)) return m_CancelButton;
	           
	           else if(aComponent.equals(m_CancelButton)) return 	m_OkButton;
	           
	           else if(aComponent.equals(m_OkButton)) return  m_AddButton;
	           
	           else if(aComponent.equals( m_AddButton)) return m_subjectField;
	           
	           return m_subjectField;
	       }
	      
	       public Component getDefaultComponent(Container focusCycleRoot) {
	    	   
	           return m_subjectField;
	       }
	      
	       public Component getFirstComponent(Container focusCycleRoot) {
	    	   
	           return m_subjectField;
	       }
	      
	       public Component getLastComponent(Container focusCycleRoot) {
	    	   
	           return m_CancelButton;
	       }
	   }
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		 m_subjectField.setToolTipText(getGuiStrs("subjectFieldEnterToolTip"));
		 
		 m_AddButton.setToolTipText(getGuiStrs("subjectAddButtonToolTip"));
		 
         m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
         
         m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
}
