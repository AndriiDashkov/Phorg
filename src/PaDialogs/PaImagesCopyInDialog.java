package PaDialogs;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import PaGlobal.PaGuiTools;
import static PaGlobal.PaUtils.getMessagesStrs;
import static PaGlobal.PaUtils.getGuiStrs;

public class PaImagesCopyInDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private JFileChooser _sourcePhotoFile;
	
	private JLabel m_infoLabel = new JLabel(" ");
	
	private JTextField m_dirField = new JTextField(25);
	
	private int flag =0;
	
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN , 12); 
	
	public JButton SelectPath = new JButton(" . . . ");
	
	public JButton m_Ok, m_Cancel;
	
	public JTextField get_dirPhoto() {
		
		return m_dirField;
	}

	public PaImagesCopyInDialog  (JFrame jfrm, String nameFrame) {
		super (jfrm, nameFrame, true);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		

		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		pack();
		
		setResizable(false);
	}
	

	public int getClosedFlagValue () {
		
		return flag;
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_Ok ) onOK(e);
			
			if ( e.getSource() == m_Cancel ) onCancel(e);
			
			if ( e.getSource() == SelectPath ) onSelectPath(e);
		}
	}
	
	/**
	 * m_Ok button reaction
	 * @param e event to catch
	 */
	public void onOK(ActionEvent e) {
		
		if(!Files.exists(Paths.get(m_dirField.getText()))) {
			
			flag = 0;
			
			m_infoLabel.setText(getMessagesStrs("targetFolderNotExistMessage"));
			
			return;
		}
		if (!m_dirField.getText().equals("")) {
			
			flag = 1;
			
			dispose();
			
		} else {
			
			flag = 0;
			
			m_infoLabel.setText(getMessagesStrs("targetFolderCantBeEmpty"));		
		}
	}
	
	public void onCancel(ActionEvent e) {
		
		flag = 0;
		
		dispose();
	}	
	
	public void onSelectPath(ActionEvent e) {

		_sourcePhotoFile = new JFileChooser();
		
		_sourcePhotoFile.setCurrentDirectory(new File("."));
		
		_sourcePhotoFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int result = _sourcePhotoFile.showOpenDialog(PaImagesCopyInDialog.this);
		

		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = _sourcePhotoFile.getSelectedFile().getPath();
			
			m_dirField.setText(name);		
		}
	}
	
	/**
	 * Creates all gui elements
	 * @return main gui panel
	 */
	private JPanel createGUI() {
		
		JPanel panel_MAIN = PaGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(font);
		
		m_infoLabel.setForeground(Color.RED);
		
		JPanel panel_path = PaGuiTools.createHorizontalPanel();		

		panel_path.add(m_dirField);
		
		panel_path.add(Box.createHorizontalStrut(6));
		
		panel_path.add(SelectPath);
		
		panel_path.add(Box.createHorizontalStrut(5));
		
		panel_path.add(Box.createHorizontalGlue());

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(12,12,12,7));
				
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_Ok = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_Cancel = new JButton(getGuiStrs("buttonCancelCaption"));
		
		Forwarder forwarder = new Forwarder();
		
		m_Ok.addActionListener(forwarder);
		
		m_Cancel.addActionListener(forwarder);
		
		SelectPath.addActionListener(forwarder);
		
		m_dirField.addKeyListener(new EnterListener());
		
		panel_Ok_Cancel.add(m_Ok);
		
		panel_Ok_Cancel.add(m_Cancel);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(12));	
		
		panel_MAIN.add(south_right);
		
		return panel_MAIN;
	}
	
	 /**
	  * Cathes the Enter button press event to start the find operation
	  * @author avd
	  *
	  */
	 private class EnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {}
	
		@Override
		public void keyReleased(KeyEvent e) {
	
			    int key=e.getKeyCode();
			    
			    if(e.getSource()== m_dirField)
			    {
			        if(key==KeyEvent.VK_ENTER)
			        { 
			        	onOK(null);                   
			        }
			    }
		}

		@Override
		public void keyTyped(KeyEvent arg0) {} 
	 }

}
