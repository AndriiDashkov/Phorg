
package PaROI;

import java.awt.event.MouseWheelEvent;
import PaEditor.PaScrollView;

/**
 * @author Andrey Dashkov
 *
 * Inherited scroll panel class, mainly intended for the control of mouse wheel event. All mouse wheel events
 * are controlled here by an instrument resize operations, not by vertical scroll.
 */
public class PaScrollViewRoi extends PaScrollView  {
	
	
	private static final long serialVersionUID = 1L;
	
	private PaInstrumentsRoiPanel m_parent = null;

	public PaScrollViewRoi(PaInstrumentsRoiPanel parent ) {
		
		m_parent = parent;
		
	}
	
	
	/**
	 * Overloading of this function blocks the mouse wheel event for vertical scroll for JScrollPanel and redirects
	 * the wheel event to an instrument, in order to support changing of roi/crop window size by mouse wheel
	 * @param e - event from the mouse wheel
	 */
	protected void processMouseWheelEvent(MouseWheelEvent e) 
	{
		m_parent.processMouseWheelEventForInstrument(e); 

	}

}
