
package PaEditor;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaAlgorithms.PaAlgoTransform;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;


/**
 * @author avd
 *
 */
public class PaTabPreview extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTabbedPane m_tabPanel = new JTabbedPane();
	
	private ArrayList<JLabel> m_labels = new ArrayList<JLabel>();
	
	private ArrayList<BufferedImage> m_images = new ArrayList<BufferedImage>();
	
	PaHistogramPanel m_histPanel = new PaHistogramPanel(PaHistogramPanel.VARIANTS.SECOND);
	
	/**
	 * <p>visible size of the image</p>
	 */
	private Dimension m_size = new Dimension(700,700);
	
	private final int TAB_AMOUNT = 2;
	
	/**
	 * <p>list of all used instruments for all tabs</p>
	 */
	ArrayList<ArrayList<String>> m_instNames = new ArrayList<ArrayList<String>>();
	

	public PaTabPreview() {
		
		super();
		
		createGui();
	}


	/**
	 * <p>Creates gui<p>
	 */
	private void createGui() 
	{
		
		setLayout((new BoxLayout(this, BoxLayout.Y_AXIS)));
		
		for(int i=0; i < TAB_AMOUNT; ++i) {
			
			JPanel panel = PaGuiTools.createHorizontalPanel();
			
			panel.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			panel.setAlignmentY(Component.CENTER_ALIGNMENT);
			
			JLabel l = new JLabel();
	
			panel.add(Box.createHorizontalGlue());
			
			panel.add(l);
			
			panel.add(Box.createHorizontalGlue());
			
			m_labels.add(l);
			
			m_tabPanel.addTab(Integer.toString(i), panel);
			
			m_images.add(null);
			
			m_instNames.add(new ArrayList<String>());
		}
		
		JPanel panelInfo = PaGuiTools.createHorizontalPanel();
		
		JLabel infLabel = new JLabel(getGuiStrs("previewAreaLabel"));
		
		panelInfo.add(infLabel);
		
		infLabel.setToolTipText(getGuiStrs("previewAreaLabelToolTip"));
		
		panelInfo.add(Box.createHorizontalGlue());
		
		add(panelInfo);
		
		add(m_tabPanel);
		
		add(Box.createVerticalStrut(PaUtils.VERT_STRUT));
		
		add(m_histPanel);
		
		Dimension d = PaUtils.get().getSettings().getPrevAreaFixedSize();

		m_size = d;
		
		//refresh histogram for tab switch
		m_tabPanel.addChangeListener(new ChangeListener() {
			
		    public void stateChanged(ChangeEvent e) {
		    	
		    	int index = getSelectedIndex();
		    	
		    	if(index < TAB_AMOUNT){		
		    		
		    		BufferedImage im = m_images.get(index);
		    		
		    		if(im != null) {
		    			
		    			m_histPanel.setNewData(im );
		    			
		    			m_histPanel.setChanelInfo();
		    			
		    		} else {
		    			
		    			m_histPanel.resetImage();
		    		}
		    	}
		    }
		});
		
		int histH = PaUtils.get().getSettings().getHistoPanelHeight();
		
		m_histPanel.setPreferredSize(new Dimension(d.width,histH));
		
		m_histPanel.setMaximumSize(new Dimension(d.width,histH));
	}
	/**
	 * 
	 * @return index of selected tab
	 */
	public int getSelectedIndex() { return m_tabPanel.getSelectedIndex(); }
	/**
	 * 
	 * @param im image to set into label (we use Jlabel to reproduce image on the screen)
	 */
	public void setImage(Image im) {
		
		BufferedImage bIm = (BufferedImage) im;
		
		Dimension newSize = PaUtils.get().getWidthHeight(bIm, (int)m_size.getWidth(),
				m_tabPanel.getHeight() -4*PaUtils.VERT_STRUT/*(int)m_size.getHeight()*/);
		
		BufferedImage newImage =  (BufferedImage) PaAlgoTransform.getScaledImage (bIm, (int)newSize.getWidth(), 
				 (int)newSize.getHeight());
		
		m_histPanel.setNewData(bIm);
		
		m_histPanel.setChanelInfo();
		
		m_images.set(getSelectedIndex(), bIm);
		
		m_labels.get(getSelectedIndex()).setIcon(new ImageIcon(newImage));
	}
	/**
	 * 
	 * @param index of the tab
	 * @return image which is for tab with index
	 */
	public BufferedImage getImage(int index) {
		
		if(index >= m_images.size()) {
			
			writeLog("Images index is out of range index = " + index + NEXT_ROW +
					"size of images stack - " + m_images.size() , null, true, false, true);
			
			return null;
		}
		
		return m_images.get(index);
	}
	
	/**
	 * 
	 * @return current preview image
	 */
	public BufferedImage getCurrentImage() {
		
		return m_images.get(getSelectedIndex());
	}
	
	public void setVisibleSize(Dimension s) {
		m_size = s;	
	}
	
	public Dimension getVisibleSize() { return m_size;}
	
	/**
	 * <p>Adds instruments name to the list of used instruments for current tab</p>
	 * @param name  - instruments name
	 */
	public void setInstrumentName(String name) {
		
		int index = getSelectedIndex();
		
		//in the case of too long tooltip
		if(m_instNames.get(index).size() > 25) { m_instNames.get(index).clear(); }
		
		m_instNames.get(index).add(name);
		
		m_tabPanel.setToolTipTextAt(getSelectedIndex(), getToolTip(index));
	}
	
	/**
	 * 
	 * @param index of tab
	 * @return complex tooltip which has all used operations for this tab
	 */
	private String  getToolTip(int index) {
		
		ArrayList<String> list = m_instNames.get(index);
		
		String s = "<html>" + getGuiStrs("operationsInstWinName") + "<br>";
		
		for(int k =0; k < list.size(); ++k) {
			
				s +=  list.get(k)+"<br>";
		}
		
		return s+"</html>";
	}
}
