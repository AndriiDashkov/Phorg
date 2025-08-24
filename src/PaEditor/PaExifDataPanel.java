
package paeditor;

import static paexif.PaTagParser.getColorSpace;
import static paexif.PaTagParser.getContrast;
import static paexif.PaTagParser.getDoubleString;
import static paexif.PaTagParser.getString;
import static paexif.PaTagParser.getWhiteBalance;
import static paglobal.PaUtils.getGuiStrs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import paexif.PaExifLoader;
import paexif.PaExtendedExf;
import paexif.PaImageExf;

/**
 * @author Andrii Dashkov
 * Data panel to represent Exif information for an image
 *
 */
public class PaExifDataPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel m_modelLabel;
	
	private JLabel m_expTimeLabel;
	
	private JLabel m_flashLabel;
	
	private JLabel m_wBalanceLabel;
	
	private JLabel m_dateTimeLabel;
	
	private JLabel m_size;
	
	private JLabel m_colorSpaceLabel;
	
	private JLabel m_contrastLabel;
	
	public PaExifDataPanel() {
		
		super();
		
		createGui();
	}
	
	public void createGui() {
		
		GridBagLayout grid = new GridBagLayout();
		
		setLayout(grid);
		
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		GridBagConstraints c = new GridBagConstraints();
		
		m_modelLabel = new JLabel(getGuiStrs("noDataLabel"));
		
		m_expTimeLabel  = new JLabel(getGuiStrs("noDataLabel"));
		
		m_flashLabel = new JLabel(getGuiStrs("noDataLabel"));
		
		m_wBalanceLabel = new JLabel(getGuiStrs("noDataLabel"));
		
		m_dateTimeLabel  = new JLabel(getGuiStrs("noDataLabel"));
		
		m_size  = new JLabel(getGuiStrs("noDataLabel"));
		
		m_colorSpaceLabel  = new JLabel(getGuiStrs("noDataLabel"));
		
		m_contrastLabel	 = new JLabel(getGuiStrs("noDataLabel"));
		
		JLabel modelLabelName = new JLabel( getGuiStrs("exifTableCameraRow"));
		
		JLabel expTimeLabelName  = new JLabel( getGuiStrs("exifTableExposureTimeRow"));
		
		JLabel flashLabelName = new JLabel( getGuiStrs("exifTableFlashPixVerRow"));
		
		JLabel wBalanceLabelName = new JLabel( getGuiStrs("exifTableWhiteBalanceRow"));
		
		JLabel dateTimeLabelName  = new JLabel( getGuiStrs("exifTableDateTimeOriginalRow"));
		
		JLabel sizeLabelName  = new JLabel( getGuiStrs("sizeLabelName"));
		
		JLabel contrastLabelName  = new JLabel( getGuiStrs("exifTableContrastRow"));
		
		JLabel colorSpLabelName  = new JLabel( getGuiStrs("exifTableColorSpaceRow"));
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		
		c.gridy = 0;
		
		c.anchor = GridBagConstraints.CENTER;
		
		c.insets = new Insets(5,5,5,5);
		
		grid.setConstraints(modelLabelName, c);
		
		add(modelLabelName);
		
		c.gridx = 1;
		
		c.gridy = 0;
		
		grid.setConstraints(m_modelLabel, c);
		
		add(m_modelLabel);
		
		c.gridx = 0;
		
		c.gridy = 1;
		
		grid.setConstraints(sizeLabelName, c);
		
		add(sizeLabelName);
		
		c.gridx = 1;
		
		c.gridy = 1;
		
		grid.setConstraints(m_size, c);
		
		add(m_size);
		
		c.gridx = 0;
		
		c.gridy = 2;
		
		grid.setConstraints(expTimeLabelName, c);
		
		add(expTimeLabelName);
		
		c.gridx = 1;
		
		c.gridy = 2;
		
		grid.setConstraints(m_expTimeLabel, c);
		
		add(m_expTimeLabel);
		
		c.gridx = 0;
		
		c.gridy = 3;
		
		grid.setConstraints(flashLabelName, c);
		
		add(flashLabelName);
		
		c.gridx = 1;
		
		c.gridy = 3;
		
		grid.setConstraints(m_flashLabel, c);
		
		add(m_flashLabel);
		
		c.gridx = 0;
		
		c.gridy = 4;
		
		grid.setConstraints(wBalanceLabelName, c);
		
		add(wBalanceLabelName);
		
		c.gridx = 1;
		
		c.gridy = 4;
		
		grid.setConstraints(m_wBalanceLabel, c);
		
		add(m_wBalanceLabel);
		
		c.gridx = 0;
		
		c.gridy = 5;
		
		grid.setConstraints(colorSpLabelName, c);
		
		add(colorSpLabelName);
		
		c.gridx = 1;
		
		c.gridy = 5;
		
		grid.setConstraints(m_colorSpaceLabel, c);
		
		add(m_colorSpaceLabel);
		
		c.gridx = 0;
		
		c.gridy = 6;
		
		grid.setConstraints(contrastLabelName, c);
		
		add(contrastLabelName);
		
		c.gridx = 1;
		
		c.gridy = 6;
		
		grid.setConstraints(m_contrastLabel, c);
		
		add(m_contrastLabel);
		
		c.gridx = 0;
		
		c.gridy = 7;
		
		grid.setConstraints(dateTimeLabelName, c);
		
		add(dateTimeLabelName);
		
		c.gridx = 1;
		
		c.gridy = 7;
		
		grid.setConstraints(m_dateTimeLabel, c);
		
		add(m_dateTimeLabel);
		
		Dimension size = getPreferredSize();

		setMaximumSize(size);
	}
	/**
	 * <p>Sets meta data (exif format data) for the image in the histogram</p>
	 * @param mData - meta data object
	 */
	public void setMetaDataInfo( String fullPath, int width, int height ) {
		
		PaExifLoader loader = new  PaExifLoader();

		if(!loader.loadFile(fullPath)) return;
		

		m_modelLabel.setText(getString(loader.getExifElement(PaImageExf.Make),"Make") + " "+getString(loader.getExifElement(PaImageExf.Model),"CameraModel"));
		
		m_expTimeLabel.setText(getDoubleString(loader.getExifElement(PaExtendedExf.ExposureTime),5,"ExposureTime"));
		
		m_wBalanceLabel.setText(getWhiteBalance(loader.getExifElement(PaExtendedExf.WhiteBalance)));
		
		m_dateTimeLabel.setText(getString(loader.getExifElement(PaExtendedExf.DateTimeOriginal),"DateTimeOriginal"));
		
		m_flashLabel.setText(getString(loader.getExifElement(PaExtendedExf.FlashPixVersion),"FlashPix"));
		
		m_size.setText(String.valueOf(width)+" x "+String.valueOf(height));
		
		m_colorSpaceLabel.setText(getColorSpace(loader.getExifElement(PaExtendedExf.ColorSpace)));
		
		m_contrastLabel.setText(getContrast(loader.getExifElement(PaExtendedExf.Contrast)));
		
	}
	/**
	 * Sets the new size information in this panel
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height) {
		
		m_size.setText(String.valueOf(width)+" x "+String.valueOf(height));
	}
	
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        
        int[][] dims = ((GridBagLayout)getLayout()).getLayoutDimensions();
        
        g.setColor(Color.RED);
        
        int x = 0;
       
        for (int add : dims[0])
        {
            x += add;
            
            g.drawLine(x, 0, x, getHeight());
            
        }
        int y = 0;
        
        for (int add : dims[1])
        {
            y += add;
            
            g.drawLine(0, y, getWidth(), y);
           
        }
        
        g.drawLine(0, 0, getWidth(), 0);
        
        g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
        
        g.drawLine(0, 0, 0, getHeight());
        
        g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
        
    }
   
}

