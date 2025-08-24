
package paeditor;

import static paglobal.PaUtils.getGuiStrs;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import paglobal.PaUtils;

/**
 * Panel with two radio buttons: use for the whole image and a region only; it is used for a lot  of instruments in the Instruments window.
 * @author Andrii Dashkov
 *
 */
public class PaOperationTypePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JRadioButton m_wholeImage;
	
	private JRadioButton m_regionImage;

	private ButtonGroup m_butGroupType = new ButtonGroup();
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	private boolean  isWholeImage = true;

	/**
	 * 
	 */
	public PaOperationTypePanel() {
		
		createGui();
		
		setToolTips();
		
		m_wholeImage.setSelected(true);
		
		m_regionImage.setSelected(false);
		
		ButtonsListener l = new ButtonsListener();
		
		m_wholeImage.addChangeListener(l);
		
		m_regionImage.addChangeListener(l);
	}

	
	private void createGui() {
		

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("typeOfOperation"),TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, m_font));
		
		
		m_wholeImage = new JRadioButton(getGuiStrs("wholeImageBlurCheckBoxName"));
		
		m_regionImage = new JRadioButton(getGuiStrs("regionImageBlurCheckBoxName"));
		
		
		m_butGroupType.add(m_wholeImage);
		
		m_butGroupType.add(m_regionImage);
		
	    add( m_regionImage);  
	    
	    add(m_wholeImage); 
	    
	    add(Box.createHorizontalGlue());
	    
	    m_wholeImage.setFont( m_font);
	    
	    m_regionImage.setFont( m_font);
		
	}

	/**
	 * Sets tooltips
	 */
	private void setToolTips() {
		
		m_wholeImage.setToolTipText(getGuiStrs("wholeImageCheckBoxBlurTotalTooltip"));
		
		m_regionImage.setToolTipText(getGuiStrs("regionImageCheckBoxBlurTotalTooltip"));

	}
	
	/**
	 * 
	 * @author Andrii Dashkov
	 * <p>Listener class to listen the slider</p>
	 *
	 */
	class ButtonsListener implements ChangeListener {
		

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			 isWholeImage = m_wholeImage.isSelected();
		}
	}
	/**
	 * 
	 * @return the flag of 'whole image' option. it is mainly used function in this class
	 */
	public boolean isWholeImage() { return isWholeImage;}
	
	
	public void setWholeImageSelected(boolean flag) {
		
		m_wholeImage.setSelected(flag);
		
		m_regionImage.setSelected(!flag);
		
		isWholeImage = flag;
		
	}
	
	
	public void setControlsEnabled(boolean flag)
	{
		
		m_wholeImage.setEnabled(flag);
		
		m_regionImage.setEnabled(flag);
		
		setEnabled(flag);
		
	}
	

}
