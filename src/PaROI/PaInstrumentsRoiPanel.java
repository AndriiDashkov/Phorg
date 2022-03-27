package PaROI;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import PaCollection.PaImage;
import PaEditor.PaComplexButton;
import PaEditor.PaInstrument;
import PaEditor.PaInstrumentsPanel;
import PaEnums.PaInstrumentTypeEnum;
import PaGlobal.PaUtils;

/**
 * @author Andrey Dashkov
 * The main panel of ROI window which contains all instruments and image filed 
 * Inherited from  PaInstrumentsPanel
 * It performs all control over the process of drawing ROIs: input event interception, instruments control, getting the drawings from
 * instruments, etc
 * The drawing process is performed by instruments, this panel just received the ready image.
 *
 */
public class PaInstrumentsRoiPanel extends  PaInstrumentsPanel 
{
	
	private static final long serialVersionUID = 1L;
	
	public   PaInstrumentsRoiPanel(PaRoiWindow parent, PaImage p, String titleStr, String toolTipTest) {
			
		super(parent, p, titleStr, toolTipTest);
		
	}
	
	protected void createUI(String titleStr, String toolTipTest)
	{
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		
		labelPanel.setLayout(new BoxLayout(labelPanel , BoxLayout.X_AXIS));
		
		n_label = new JLabel(titleStr);
		
		n_label.setToolTipText(toolTipTest);
		
		labelPanel.add(n_label);	
		
		labelPanel.add(Box.createHorizontalGlue());

		m_scrollPane = new PaScrollViewRoi(this);//new PaScrollView();

		m_roiPanel = new PaRoiPanel(m_parent);
			
		m_roiPanel.setPreferredSize( new Dimension(
				
	            Integer.MAX_VALUE,
	            200
	    ) );
		
		m_roiPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,200));
	
		add(labelPanel);
		
		add(m_scrollPane);
		
		add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		add (m_roiPanel );
		
		addComponentListener(this);

		m_scrollPane.addMouseMotionListener(new PaMouseMoveListener());
		
		m_scrollPane.addMouseListener(new PaMouseListener());
		
		addMouseListener(this);

		registerKeys_W_A_S_D_Action();
		
	}
	
	public void setNewImageNameForRoi()
	{
		m_roiPanel.setNewImage(m_current_image_base_name);
			
	}
	
	/**
	 * 
	 * It is used inside instruments to set newly drawn image.
	 *
	 */
	public BufferedImage setImageWithRoi()
	{
		ArrayList<PaRectangle> list = m_roiPanel.getCurrentRoiList();
				
		int selectedRoiIndex = m_roiPanel.getSelectedRoiIndex();
		
		if(list == null) { return null; }
		
		BufferedImage targetImage = PaUtils.deepCopy(m_loadedImage);
		
		if(targetImage == null) {
			
			 writeLog("Can't find loaded image to paint the ROI" , null, true, false, true);
		}

	
		Graphics2D g2 = targetImage.createGraphics();
		
		for(int i = 0; i < list.size(); ++i) {
			
			PaRectangle r = list.get(i);
			
			if (i == selectedRoiIndex) {
				
				g2.setColor(Color.BLUE);
				
				g2.draw(new Rectangle(r.x, r.y, r.w, r.h));
				
				int markerWidth = r.w/5;
				
				//size of marker according to smaller dimension
				if(r.w > r.h) { markerWidth = r.h/5;   }
				
				g2.fill(new Rectangle(r.x, r.y, markerWidth, markerWidth));
				
				g2.fill(new Rectangle(r.x + r.w - markerWidth, r.y + r.h - markerWidth, markerWidth, markerWidth));
				
				g2.fill(new Rectangle(r.x + r.w - markerWidth, r.y, markerWidth, markerWidth));
				
				g2.fill(new Rectangle(r.x, r.y + r.h - markerWidth, markerWidth, markerWidth));
				
			}
			else {
				
				g2.setColor(Color.GREEN);
				
				g2.draw(new Rectangle(r.x, r.y, r.w, r.h));
				
			}

		}
		
		 m_currentImage = targetImage;
		 
		 m_roiPanel.roi_table_model.fireTableDataChanged();
		 
		 return m_currentImage;
		 
		
	}
	
	
	/**
	 * Sets the instrument 
	 * @param but - instrument's button
	 */
	public void setInstrument(PaInstrumentTypeEnum type, PaComplexButton but) 
	{
		
		resetInstrument();	
		
		setStartPointForInstrument();

		m_instrument = m_instrumentsFactory.getInstrument(type, but);
		
		if(type == PaInstrumentTypeEnum.ROI_CHANGE) {
			
			((PaRoiChangeInstrument) m_instrument).setRoiData(m_roiPanel.getCurrentRoiList(), 
					m_roiPanel.getSelectedRoiIndex(),  m_roiPanel.getTypesList()); 
			
			
		}
		
		if(type == PaInstrumentTypeEnum.ROI_CUT) {
			
			checkRoiDataForCurrentImage();
			
			((PaCutRoiInstrument) m_instrument).setRoiData(m_roiPanel.getCurrentRoiList(), 
					m_roiPanel.getTypesList()); 
			
			
		}
		
		if(type == PaInstrumentTypeEnum.ROI_PIXELS_SAVE) {
			
		}
		
		
		setCursor(m_instrument.getCursor());
		
		repaint();
		
	}
	
	void checkRoiDataForCurrentImage()
	{
		
		m_roiPanel.checkRoiDataForCurrentImage(m_current_image_base_name);
		
	}
	
	
	/**
	 * Sets a new result image in preview area
	 * @param img - new result image to set 
	 */
	protected void setResultView(Image img) {
		
		if ( img != null ) {	
			
			m_parent.setResultView(img,m_instrument.getName());	
			
		}
		else {
			
			writeLog("Instruments window: can't set the result view image: null", null,
					true, false, false );
		}
		
	}
	

	/**
	 * Confirms the changes
	 */
	public void confirmChanges() {
		
		if (PaInstrument.isAnyInstrumentWasUsed ) {
			
			m_parent.setSaveButtonEnabled(true);
			
			m_currentImage = PaUtils.deepCopy((BufferedImage) m_parent.getResultViewImage());
	 	
		 	m_scrollPane.refresh(m_currentImage);
	
		 	
		 	resetInstrument();
		 		
			repaint();
			
			m_parent.setPreviewCurrentImageChanged(true);
			
			PaInstrument.m_previewImageNotConfirmed = false;
		}
		else {
			
			String mS = getMessagesStrs("messageNoChangesInInstrImage");
			
			JOptionPane.showMessageDialog(null, mS, 
					getMessagesStrs("messageInfoCaption"),
					JOptionPane.INFORMATION_MESSAGE);
			writeLog(mS,null,true,true,true);
			
		}
	

	}
	
	public String getRoiFilePath()
	{
		return m_roiPanel.getRoiFilePath();
	}
	
	public void  setRoiFilePath(String p)
	{
		m_roiPanel.setRoiFilePath(p);
	}
	
	
	public void saveRoiFile( String filePath)
	{
		
		HashMap<String,ArrayList<PaRectangle>> roi_map = m_roiPanel.getRoiMap();
		
		checkRectanglesGeometry(roi_map,  ((PaRoiWindow) m_parent).getImageSizesMap());
		 
		ArrayList<String> types_list = m_roiPanel.getTypesList();
		
		PaRoiFileParser.saveRoiFile(roi_map, types_list, filePath);
		
		m_roiPanel.refreshTypesList();
	}
	
	private void checkRectanglesGeometry(HashMap<String,ArrayList<PaRectangle>> roi_map, HashMap<String,Dimension> sizes_map)
	{
		
		for (Entry<String, ArrayList<PaRectangle>> entry : roi_map.entrySet()) {
			
			
			String imageName = entry.getKey();
			
			Dimension im_size = sizes_map.get(imageName);
			
			if(null != im_size) {
				
				ArrayList<PaRectangle> roi_list = entry.getValue();
				
		
				for(int j = 0; j < roi_list.size(); ++j) {
					
					PaRectangle rect = roi_list.get(j);
					
					//additional check of valid ROI geometry
					if (rect.x >= im_size.width || rect.y >= im_size.height) {
						
						roi_list.set(j, null);
					}
				}
				
				 for (Iterator<PaRectangle> itr = roi_list.iterator(); itr.hasNext();) {
					 
				      if (itr.next() == null) { itr.remove(); }
				 }
			}
		}
	}
	
	
	public void refreshViewImage( ) {
		
	 
		m_scrollPane.refresh(m_currentImage);
			
		
	}
	
	
	public void refreshRoi( ) 
	{
		
		m_roiPanel.refreshRoiListForImage();
	}
	
	public boolean isRoiListFileLoaded() 
	{
		
		return m_roiPanel.isRoiListFileLoaded();
	}
	
	public PaRectangle getCurrentROI()
	{
		return m_roiPanel.getSelectedRoi();
		
	}
	
	public void processMouseWheelEventForInstrument(MouseWheelEvent e) 
	{
		if (  m_instrument != null && m_instrument.filterMouseEvent(e) ) {
			
				repaint(); 
				
		}
	}
	
	
	PaRoiPanel getRoiPanel() {
		
		return m_roiPanel;
	}
}






 