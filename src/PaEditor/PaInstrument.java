package paeditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


/**
 * <p>Abstract instrument class; the visitor pattern is used here; all instruments which are inhereted from this
 * class are visitors for instrument panel class </p>
 * @author Andrii Dashkov
 */
public abstract class PaInstrument {
	 
	
	protected PaComplexButton m_button;
	
	protected Color m_color;
	/**
	 * The name which represents the instrument in the GUI for user
	 */
	protected String m_name;
	/**
	 * the inner name of instrument for inside purpose only
	 */
	protected PaEnumInstrumentNames m_innerName;
	
	/**
	 * Flag which can be used for the case - any instrument was used?
	 */
	public static boolean isAnyInstrumentWasUsed = false;
	/**
	 * Flag for control the state - preview image has been changed but not confirmed ? - then true
	 */
	public static boolean m_previewImageNotConfirmed = false;
	
	/**
	 * Switch on/of of reaction (result image recreation) while mouse draging with this instrument
	 */
	protected boolean m_dragMouseRepaintEnabled = true;
	
	/**
	 * Flag for initiation of an instrument again immediately after using.
	 */
	protected boolean m_keepActivated = false;
	
	/**
	 * Flag for switching to the next image immediately after using.
	 */
	protected boolean m_nextImageAutoSwitch = false;
	
	
	 /**
	  * <p>Constructor of instrument.  </p>
	  * <p>In most cases this function calculates the geometry of instruments shapes in Work area</p>
	  * @param b - button of instrument - it is possible that we will need to control button's state from instrument
	  * @param c - color of instrument (visible color of all shapes, lines and markers of instrument)
	  * @author Andrii Dashkov
	  */
	public PaInstrument(PaComplexButton b, Color c, String name) {
		
		 m_button = b;
		 
		 m_color = c;
		 
		 m_name = name;
	}
	 
	 /**
	  * <p>Function needs for catching mouse event and to recalculate visible instruments shapes (markers, lines, etc) 
	  * Instrument should work with user , should return true if work area repaint needs after catching MouseEvent </p>
	  * <p>In most cases this function calculates the geometry of instruments shapes in Work area</p>
	  * @param e - mouse event that can be captured in order to change instrument geometry.
	  * @return true if we want to redraw the instrument 
	  * @author Andrii Dashkov
	  */
	abstract public boolean filterMouseEvent(MouseEvent e) ;
	 /**
	  * <p>Function needs for catching key event and to recalculate visible instruments shapes (markers, lines, etc) </p>
	  * <p>In most cases this function changes the geometry of the instrument rectangle in the Work area</p>
	  * @param e - a key event that can be captured in order to change the instrument geometry.
	  * @return true if we want to redraw the instrument; for the base class we always return false
	  * in order to make instruments which don't need arrows not to handle key events 
	  * @author Andrii Dashkov
	  */

	public boolean filterKeyEvent(int keyCode) { return false; }
	 /**
	  * <p>Function needs for real drawing of instruments shapes  </p>
	  * @param g2 - Graphics context which is usually from paint() call.
	  */
	abstract public void  drawShape(Graphics2D g2);

	 /**
	  * @author Andrii Dashkov
	  * <p>Function generates result image (main or minor image) In most cases this image is used in Preview area.
	  * This is the main instrument function - it makes all the magic. </p>
	  * <p>getResultView function receives MouseEvent; if we don't want for some events to have repaint image in preview area
	  * we should investigate MouseEvent inside and return null image</p>
	  *
	  * @param e - mouse event that can be captured in order to decide what to do with result image
	  * @param xScale - X coordinate scale value - we need it (not for all instruments) to right coordinate calculations
	  * @param yScale - Y coordinate scale value - we need it (not for all instruments) to right coordinate calculations
	  * @param sourceImage - source image to be changed by instrument
	  * @return image or null if we don't want Preview area to be repainted
	  */
	abstract public Image getResultView(MouseEvent e,float xScale, float yScale,BufferedImage sourceImage);
	
	public Image getResultView(KeyEvent e,float xScale, float yScale,BufferedImage sourceImage) { return null; }
	
	
	public Color getColor() {return m_color; }
	
	public void setColor(Color c) { m_color = c; }
	
	public void reset() {  m_button.m_mainButton.setSelected(false); }
	
	public String getName() { return m_name; }
	
	public void setName(String name) { m_name = name; }
	
	public boolean isRepaintWhileMouseDrag() { return  m_dragMouseRepaintEnabled; }
	/**
	 * <p>Can be overloaded in the children in order to reset some parameters after instrument using is finished</p>
	 */
	public void clearOperation() {};
	
	public void setCursor() {};
	/**
	 * 
	 * @return the cursor for this instrument;
	 */
	public Cursor getCursor() { return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); }
	
	/**
	 * 
	 * used for the setting of additional data in child implementations
	 */
	public void setAdditionalData(Object data) {}
	
	public boolean isKeepActivated() { return m_keepActivated; }
	
	public boolean isAutoSwitchNextImageActivated() { return m_nextImageAutoSwitch; }
	
	public PaComplexButton getInstrumentButton()  { return m_button; }
	
	
 }
