/**
 * 
 */
package PaDialogs;


import static PaGlobal.PaUtils.getGuiStrs;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * @author avd
 * This is a special dialog which can "forget" itself; it has special check box "don't show any more"
 * The list of hidden dialogs is in the PaSettings class; the id parameter in the constructor of the dialog is for identification
 * of different dialogs. IMPORTANT: this id must be different for every new constructed dialog. So before to use another one,
 * find all usages and chose the next unique id.
 * This class does  control of visibility in the reloaded function setVisible()
 */
public class PaSpecialDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public enum DialogType {
		YES_NO_CANCEL_OPTION,
		YES_NO_OPTION,
		OK_CANCEL_OPTION,
		OK_OPTION
	};
	
	JButton m_butYes = new JButton(getGuiStrs("buttonYesCaption"));
	
	JButton m_butNo = new JButton(getGuiStrs("buttonNoCaption"));
	
	JButton m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
	
	JButton m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
	
	JCheckBox m_checkBox = new JCheckBox(getGuiStrs("dontShowThisDialogCaption"));

	private int m_closeFlag = JOptionPane.YES_OPTION;
	
	private DialogType m_type = DialogType.YES_NO_CANCEL_OPTION;
	
	String m_message;
	
	int m_message_id = -1;
	
	private boolean m_saveChoiceFlag = false;
	
	/**
	 * @param parent - parent of the dialog
	 * @param modal - modal or not
	 * @param title - title of the dialog
	 * @param message - main text of the dialog.
	 * @param  defaultOption - default button  to select
	 * @param toolTipCheck - the tooltip for the control check box
	 * @param id must be unique throughout the code for the every new constructed dialog.
	 */
	public PaSpecialDialog(Frame parent, DialogType type, String title, String message, 
			boolean modal, int defaultOption, String toolTipCheck, int id ) {
		
		super(parent, modal);

		m_closeFlag = defaultOption;
		
		setTitle(title);
		
		m_message_id = id;
		
		m_message = message;
		
		m_type = type;
		
		createGui();
		
		setListeners();
		
		m_checkBox.setSelected(false);
		
		m_checkBox.setToolTipText(toolTipCheck);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		double width = screenSize.getWidth();
		
		double height = screenSize.getHeight();

		setBounds((int)(width/2 -200), (int)(height/2-110), 400, 220);
		
		
		//just to set the closing flag in the right way
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {}

			@Override
			public void windowClosed(WindowEvent arg0) {  m_closeFlag = JOptionPane.CANCEL_OPTION; }

			@Override
			public void windowClosing(WindowEvent arg0) {  m_closeFlag = JOptionPane.CANCEL_OPTION; }

			@Override
			public void windowDeactivated(WindowEvent arg0) {}

			@Override
			public void windowDeiconified(WindowEvent arg0) {}

			@Override
			public void windowIconified(WindowEvent arg0) {}

			@Override
			public void windowOpened(WindowEvent arg0) {}

        });

		
	
		
		pack();
		
		setResizable(false);
	}
	public PaSpecialDialog(Frame parent, DialogType type, String title, String message, 
			boolean modal, String toolTipCheck, int id ) {
		
		this(parent, type, title, message, modal,JOptionPane.YES_OPTION,toolTipCheck, id);
	}
	/**
	 * This constructor creates the dialog which can remember the user's choice
	 * @param parent - parent component of the dialog
	 * @param type - type of the dialog - JOptionPane.YES_OPTION, etc
	 * @param title - title of the dialog
	 * @param message - message of the dialog
	 * @param modal - modal flag of the dialog
	 * @param saveChoiceFlag - true if you want this dialog to be able to remember the choice
	 * @param id - must be unique throughout the code for the every new constructed dialog.
	 */
	public PaSpecialDialog(Frame parent, DialogType type, String title, String message, 
			boolean modal, boolean saveChoiceFlag, int id ) {
		
		this(parent, type, title, message, modal,JOptionPane.YES_OPTION,
				getGuiStrs("dontShowSaveThisDialogTooltip"), id);
		
		m_saveChoiceFlag = saveChoiceFlag;
		
		m_checkBox.setText(getGuiStrs("dontShowSaveThisDialogCaption"));
		

	
	}

	/*
	 * Creates all UI elements
	 */
	private void createGui() {

		JPanel panelLabel = PaGuiTools.createHorizontalPanel();
		
		JPanel panelButtons =PaGuiTools.createHorizontalPanel();
		
		JPanel panelCheckBox =PaGuiTools.createHorizontalPanel();
		
		JLabel label = new JLabel(m_message);
		
		panelLabel.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelLabel.add(label);
		
		panelLabel.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		
		switch(m_type) {
		
			case YES_NO_CANCEL_OPTION : {
				
				m_OkButton.setVisible(false);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				panelButtons.add(m_butYes);
				
				panelButtons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
				
				panelButtons.add(m_butNo);
				
				panelButtons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
				
				panelButtons.add(m_CancelButton);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				break;
			}
			case YES_NO_OPTION : {
				
				m_OkButton.setVisible(false);
				
				m_CancelButton.setVisible(false);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				panelButtons.add(m_butYes);
				
				panelButtons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
				
				panelButtons.add(m_butNo);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				break;
			}
			case OK_OPTION : {
				
				m_butYes.setVisible(false);
				
				m_butNo.setVisible(false);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				panelButtons.add(m_OkButton);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				break;
			}
			default:
			case OK_CANCEL_OPTION : {
				
				m_butYes.setVisible(false);
				
				m_butNo.setVisible(false);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				panelButtons.add(m_OkButton);
				
				panelButtons.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
				
				panelButtons.add(m_CancelButton);
				
				panelButtons.add(Box.createHorizontalGlue());
				
				break;
			}

		};	
		
		panelCheckBox.add(m_checkBox);
		
		panelCheckBox.add(Box.createHorizontalGlue());
		
		JPanel mainPanel = PaGuiTools.createVerticalPanel();
		
		add(mainPanel);
		
		mainPanel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		mainPanel.add(panelLabel);
		
		mainPanel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		mainPanel.add(panelButtons);
		
		mainPanel.add(panelCheckBox);
		
	}
	
	class ButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton )  	{ 
				
				m_closeFlag = JOptionPane.OK_OPTION; 
				
				dispose();
			}
			if ( e.getSource() == m_CancelButton )  { 
				
				m_closeFlag = JOptionPane.CANCEL_OPTION; 
				
				saveChoice();
				
				dispose();
			}
			if ( e.getSource() == m_butYes )  		{ 
				
				m_closeFlag = JOptionPane.YES_OPTION; 
				
				saveChoice();
				
				dispose();
			}
			if ( e.getSource() == m_butNo )  		{ 
				
				m_closeFlag = JOptionPane.NO_OPTION; 
				
				saveChoice();
				
				dispose();
			}
			if ( e.getSource() == m_checkBox ) 		{ 
				
				if(m_checkBox.isSelected()) {
					
					ArrayList<Integer> no_visible_list = PaUtils.get().getSettings().getNonVisibleInfoMessages();
					
					no_visible_list.add(m_message_id);
					
					HashMap<Integer,Integer> map = PaUtils.get().getSettings().getNonVisibleInfoMessagesCh();
					
					map.put(m_message_id, m_closeFlag);
					
					PaUtils.get().getSettings().setReqSavingFlag();
					
				}
				else {
					
					ArrayList<Integer> no_visible_list = PaUtils.get().getSettings().getNonVisibleInfoMessages();
					
					no_visible_list.remove(m_message_id);
					
					HashMap<Integer,Integer> map = PaUtils.get().getSettings().getNonVisibleInfoMessagesCh();
					
					map.remove(m_message_id);
		
					PaUtils.get().getSettings().setReqSavingFlag();
				}
			}
			
		}
	}
	/**
	 * Save the choice of the user to the special list
	 */
	private void saveChoice() {
		
		if(m_saveChoiceFlag) {
			

			HashMap<Integer,Integer> map = PaUtils.get().getSettings().getNonVisibleInfoMessagesCh();
			
			map.put(m_message_id, m_closeFlag);
		}
	}
	
	/**
	 * Sets all listeners
	 */
	private void setListeners() {
		
		ButtonListener l =new  ButtonListener();
		
		m_OkButton.addActionListener(l);
		
		m_CancelButton.addActionListener(l);
		
		m_butYes.addActionListener(l); 
		
		m_butNo.addActionListener(l);
		
		m_checkBox.addActionListener(l);
		
	}
	
	/**
	 * The return value is the same as for JOptionPane class
	 * @return the user's choice info - OK_OPTION; CANCEL_OPTION; YES_OPTION; NO_OPTION
	 */
	public int getCloseFlag() { return m_closeFlag; }
	/**
	 * The idea - to block visibility if the dialog is in the hidden list
	 * (the  m_checkBox has been activated to true)
	 */
	public void setVisible(boolean flag) {
		
	
		ArrayList<Integer> no_visible_list = PaUtils.get().getSettings().getNonVisibleInfoMessages();
		
		if(flag && no_visible_list.contains(m_message_id)) {
			
			if(m_saveChoiceFlag) {
				
				HashMap<Integer,Integer> map = PaUtils.get().getSettings().getNonVisibleInfoMessagesCh();
				
				Integer ch =map.get(m_message_id);
				
				if(ch != null) {
					
					m_closeFlag = ch;
				}
			}
			
			return;
		}
		
		
		super.setVisible(flag);
	}
}
