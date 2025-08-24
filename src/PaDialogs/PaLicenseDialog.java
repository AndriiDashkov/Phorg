/**
 * 
 */
package padialogs;

import static paglobal.PaUtils.getGuiStrs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import paglobal.PaGuiTools;
import paglobal.PaUtils;

/**
 * Just license dialog
 * @author Andrii Dashkov
 *
 */
public class PaLicenseDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	byte[] m_b = {
			4,12,1,5,10,7,1,2,2,
			3,4,9,    
			3,2,11,3,20,5,25,         
			10,2,1,6,1,2,8,
			3,1,17};
	
	JButton m_donBut;
	
	JLabel m_lInfo;
	
	JLabel m_lInfo2;
	
	String m = "PayPal : avdkharkov@gmail.com";

	public  PaLicenseDialog (JFrame f) {
		
		super (f, getGuiStrs("licenseDialogCaption"), true); 
				
		getContentPane().add(createGUI());
		
		setBounds(250, 150, 280, 250);
	
		PaUtils.setComponentsFont (this, PaUtils.get().getBaseFont() );

		pack();
		
		setResizable(false);
		
		m_lInfo.setVisible(false);
		
		m_lInfo2.setVisible(false);
	}
	
	/**
	 * 
	 * @return main Gui panel
	 */
	private JPanel createGUI () {
		
		JPanel panelMain =  PaGuiTools.createVerticalPanel();

		panelMain.setBorder( BorderFactory.createEmptyBorder(0,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
			
		JPanel panel1 = PaGuiTools.createHorizontalPanel();
		
		JLabel label1 = new JLabel(getGuiStrs("licRow1"));
		
		panel1.add(label1); panel1.add(Box.createHorizontalGlue());
		
		JPanel panel2 = PaGuiTools.createHorizontalPanel();
		
		JLabel label2 = new JLabel(getGuiStrs("licRow2"));
		
		panel2.add(label2); panel2.add(Box.createHorizontalGlue());
		
		JPanel panel3 = PaGuiTools.createHorizontalPanel();
		
		JLabel label3 = new JLabel(getGuiStrs("licRow3"));
		
		panel3.add(label3); panel3.add(Box.createHorizontalGlue());

		panelMain.add(panel1);
		
		panelMain.add(panel2);
		
		panelMain.add(panel3);
		
		JPanel panelBut = PaGuiTools.createHorizontalPanel();
		
		m_lInfo = new JLabel(getGuiStrs("helpProjectCaption"));
		
		m_lInfo2 = new JLabel(getStr(m_b));

		m_donBut = new JButton(new ImageIcon(PaUtils.get().getIconsPath() + "palicbutton.png"));
		
		Dimension d = new Dimension(20,20);
		
		m_donBut.setMaximumSize(d);
		
		panelBut.add(m_donBut); panelBut.add(Box.createHorizontalGlue());
		
		JPanel panelInfo = PaGuiTools.createVerticalPanel();
		
		JPanel panelInfo1 = PaGuiTools.createHorizontalPanel();
		
		JPanel panelInfo2 = PaGuiTools.createHorizontalPanel();
		
		panelInfo1.add(m_lInfo); panelInfo1.add(Box.createHorizontalGlue());
		
		panelInfo2.add(m_lInfo2); panelInfo2.add(Box.createHorizontalGlue());
		
		panelInfo.add(panelInfo1);
		
		panelInfo.add(panelInfo2);
	
		panelMain.add(panelBut);
		
		panelMain.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));

		panelMain.add(panelInfo);
	
		panelMain.add(Box.createVerticalGlue());
		
		m_donBut.addActionListener(new ButtonsListener());
	
		
		return panelMain;
	}
	
	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Listener class to listen the button</p>
	 *
	 */
	class ButtonsListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == m_donBut) {
				
				m_lInfo.setVisible(!m_lInfo.isVisible());
				
				m_lInfo2.setVisible(!m_lInfo2.isVisible());
	
	    		return;
	    	}
		}

	
	}
	
	String getStr(byte[] s) {
		
		byte[] b = {
				(byte)(0x4C+s[0]),(byte)(0x6D-s[1]),(byte)(0x7A-s[2]),(byte)(0x4B+s[3]),(byte)(0x6B-s[4]),(byte)(0x65+s[5]),(byte)(0x1F+s[6]),(byte)(0x38+s[7]),(byte)(0x1E+s[8]),
				(byte)(0x5E+s[9]),(byte)(0x7A-s[10]),(byte)(0x6D-s[11]),    
				(byte)(0x6E-s[12]),(byte)(0x6A-s[13]),(byte)(0x56+s[14]),(byte)(0x75-s[15]),(byte)(0x7F-s[16]),(byte)(0x74-s[17]),(byte)(0x5D+s[18]),         
				(byte)(0x4A-s[19]),(byte)(0x65+s[20]),(byte)(0x6C+s[21]),(byte)(0x5B+s[22]),(byte)(0x6A-s[23]),(byte)(0x6A+s[24]),(byte)(0x36-s[25]),
				(byte)(0x66-s[26]),(byte)(0x70-s[27]),(byte)(0x7E-s[28])};
		
		if(m.charAt(9) != 'a' ||  m.charAt(11) != 'd' || 
				m.charAt(14) != 'r')  new String(b);
		

		return new String(b);
	}
	

}
