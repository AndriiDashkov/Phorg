
package PaEditor;


import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMenusStrs;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;


/**
 * @author avd
 * <p>This class determines the complex button for red eye removing  operation</p>
 */
public class PaRedEyeButton extends PaComplexButton {

	private static final long serialVersionUID = 1L;

	PaInstrumentsPanel m_instPanel;

	Color m_color = Color.BLACK;

	/**
	 * @param icon
	 * @param d
	 */
	public PaRedEyeButton(PaInstrumentsWindow parent,Dimension d, 
			HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> hash) {
		
		super(PaEnumInstrumentNames.INST_RED_EYE, parent,new ImageIcon(PaUtils.get().getIconsPath() 
				 + "paredeyeinst.png"), d, true, hash); // true - toggle button
		
		m_mainButton.setToolTipText("<html>"+getMenusStrs("redeyeToolTip")+ "<br/>"
				+ getMenusStrs("colorREButtonToolTip1") +"<br/>" + 
				getMenusStrs("colorREButtonToolTip2")+"</html>");
		
		m_menuButton.setEnabled(false);
		
		m_menuButton.setVisible(false);
		
	}
	
	
	/**
	 * <p>Creates frame which starts when the menu button is pushed - special parameters panel</p>
	 */
	protected JDialog createSpecialPanel() {	
		
		return new SpecialPanel();		
	}
	/**
	 * 
	 * @return the color
	 */
	public Color  getColor() {	
		
		return 	m_color;
	}

	/**
	 * 
	 * @author avd
	 * <p>Special parameter panel class with all components; the link with button is perfomed through m_filterType and 
	 * m_postProcType members of PaSharpButton.
	 * Special parameters frame appears next to the instrument button while use the small menu button on the right side</p>
	 */
	private class SpecialPanel extends JDialog {

		private static final long serialVersionUID = 1L;
		
		JButton m_button;
		
		private BufferedImage m_image;
		
		public SpecialPanel() {
			
			super();
		
			createGui();
			
			setListeners();
			
			setToolTips();
			
			pack();
			
			setResizable(false);
		}
		
		private void createGui() {
			
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			m_image = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
			
			m_button = new JButton(new ImageIcon(m_image));
			
			Color c = PaUtils.get().getSettings().getRedEyeColor();
			
			fillButtonImageIcon(c,m_image);
			
			m_color = c;
 
			JLabel l1 = new JLabel(getGuiStrs("colorChooseredEyeLabel")+" ");
					
			JPanel panel = PaGuiTools.createHorizontalPanel();
			
			panel.add(l1);
			
			panel.add(m_button);
			
			panel.setBorder(BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT,
					PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
			
			add(panel);
		
		}
		
		private void setToolTips() {
			
			m_button.setToolTipText(getGuiStrs("buttonRedEyeSpecPanelTooltip"));
		}
		
		/**
		 * <p>Sets listener for the radion buttons</p>
		 */
		private void setListeners() {
			
			ButtonsListener l = new ButtonsListener();
		
			m_button.addActionListener(l);				
		}
		
		/**
		 * 
		 * @author avd
		 * <p>Listener class to listen all radio buttons</p>
		 *
		 */
		private class ButtonsListener implements ActionListener {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == m_button ) {
					
					 Color newColor = JColorChooser.showDialog(
							 null,
			                 getGuiStrs("chooseColorInstrLabel"), Color.BLACK);
					 
					 if (newColor != null ) {
						 
						 fillButtonImageIcon(newColor,m_image);
						 
						 m_color = newColor;
					 }	
				}				
			}
		}
		
	
		private void fillButtonImageIcon(Color c, BufferedImage img) {
			
		    Graphics2D g = (Graphics2D) img.getGraphics();
		    
			g.setColor(c);
			
			g.fillRect(0, 0, img.getWidth(),img.getHeight());
		}
		
		
	}
	
	/**
	 * <p>Starts red eye instrument</p>
	 */
	protected void startInstrumentImpl() {
		
		if ( m_mainButton.isSelected() ) {
			
			writeLog("Instruments window: red eye instrument operation started.", null, 
					true, false, false );
			
			m_parent.getWorkPanel().setInstrument(PaInstrumentTypeEnum.RedEye,this);
			
		}
		else {
			
			m_parent.resetInstrument();
		}
	}
}
