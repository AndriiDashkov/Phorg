
package PaEditor;

import static PaGlobal.PaUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * This panel class has three button to control the circle buffer and choose
 * next data from buffer, save data to buffer, clear buffer. It usually appears on most right
 * side of parameters panel of complex button
 * The main purpose of this class is to support different set of parameters
 * for every instrument in Editor. Data are saveâ in special container outside the Editor
 * in order to save the information.
 * See use of  HashMap<PaEnumInstrumentNames, ArrayBlockingQueue<Object>> m_instrumnetsData
 * in PaViewPanel class. This class always works with examples of PaComplexButton which
 * controls instruments.
 */
public class PaQueuePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private PaComplexButton m_button;
	
	private JButton m_buttonNext;
	
	private JButton m_buttonSave;
	
	private JButton m_buttonClear;
	
	private ArrayBlockingQueue<Object> m_queue = null;
 

	/**
	 * @param bt - complex button to use with
	 * @param queue - queue with data to control, this class provides the circle functionality for this queue
	 */
	public PaQueuePanel(PaComplexButton bt, ArrayBlockingQueue<Object> queue ) {
		
		m_button = bt;
		
		m_queue = queue;
		
		createGui();
	}
	
	
	private void createGui() {
		
		JPanel panel = PaGuiTools.createVerticalPanel();
		
		m_buttonNext = new JButton(new ImageIcon(PaUtils.get().getIconsPath() + "pafrombuffer.png"));
		
		m_buttonSave = new JButton(new ImageIcon(PaUtils.get().getIconsPath() + "patobuffer.png"));
		
		m_buttonClear = new JButton(new ImageIcon(PaUtils.get().getIconsPath() + "paclearbuffer.png"));
		
		PaGuiTools.setFixedSize(m_buttonNext, 20);
		
		PaGuiTools.setFixedSize(m_buttonSave, 20);
		
		PaGuiTools.setFixedSize(m_buttonClear, 20);

		panel.add(m_buttonNext);
		
		panel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel.add(m_buttonSave);
		
		panel.add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		panel.add(m_buttonClear);
		
		panel.add(Box.createVerticalGlue());
		
		add(panel);
		
		CustomListener l = new CustomListener();
		
		m_buttonNext.addActionListener(l);
		
		m_buttonSave.addActionListener(l);
		
		m_buttonClear.addActionListener(l);
		
		setToolTips();
		
	}
	/**
	 * 
	 * @author Andrey Dashkov
	 * Custom listener to control buttons in the circle way
	 *
	 */
    class CustomListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				if(m_queue == null) return;
				
				if(e.getSource() == m_buttonNext) {
					
					if(!m_queue.isEmpty()) {
						
						Object ob  = m_queue.take();
					
						m_button.setData(ob);
						
						m_queue.put(ob);
					}
					
		    		return;
		    	}
				
				if(e.getSource() == m_buttonSave) {
					
					if(m_queue.remainingCapacity() == 0) {
						
						m_queue.take();
					}
					
					m_queue.put(m_button.getData());
					
		    		return;
		    	}
				
				if(e.getSource() == m_buttonClear) {
					
					m_queue.clear();
				}
				
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
		}
	   }

		/**
		 * Sets tooltips for buttons
		 */
		private void setToolTips() {
			
			m_buttonNext.setToolTipText(getGuiStrs("queueBufferNextToolTip"));	
			
			m_buttonSave.setToolTipText(getGuiStrs("queueBufferOutToolTip"));
			
			m_buttonClear.setToolTipText(getGuiStrs("queueBufferClearToolTip"));	
		}
}
