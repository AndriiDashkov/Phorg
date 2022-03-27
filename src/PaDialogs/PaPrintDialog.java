/**
 * 
 */
package PaDialogs;

import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.getMessagesStrs;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaImage;
import PaCollection.PaImageContainer;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaGuiTools;
import PaGlobal.PaLog;
import PaGlobal.PaUtils;
import static PaGlobal.PaUtils.*;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


/**
 * 
 * Printing dialog. It can print the multiselection on the one page
 * @author avd
 *
 */
public class PaPrintDialog extends JDialog {
	
	enum Paper {
		A4,
		A3
	};

	private static final long serialVersionUID = 1L;
	
	JLabel m_imageLabel;
	
	JLabel m_infoLabel;
	
	JComboBox<String> m_comboSize;
	/**
	 * Maximum size through images which are for print; width and height for this Dimension can be from different images
	 */
	Dimension m_maxPixelSize;
	
	JComboBox<Integer> m_comboNumber;
	
	ArrayList<PaImage> m_list;
	
	JRadioButton m_landscapeRadio  = new JRadioButton(getGuiStrs("landscapeCropRadioCaption"));
	
	JRadioButton m_portrRadio  = new JRadioButton(getGuiStrs("portrCropRadioCaption"));
	
	ButtonGroup m_butGroupType = new ButtonGroup();
	
	private JButton m_printButton = new JButton( getGuiStrs("buttonPrintCaption") );
	
	private JButton m_cancelButton = new JButton( getGuiStrs("buttonCancelCaption") );	
	
	private JButton m_backButton = new JButton( "<<" );
	
	private JButton m_forwButton = new JButton( ">>" );	
	
	JSpinner m_leftMarginSpinner;
	
	JSpinner m_topMarginSpinner;
	
	JSpinner m_pagesEndSpinner;
	
	JSpinner m_pagesStartSpinner;

	JLabel m_ltop = new JLabel(" " + getGuiStrs("printTopMarginLabel")+ " ");
	
	JLabel m_lleft = new JLabel(getGuiStrs("printLeftMarginLabel")+ " ");
	
	
	private Font m_font = PaUtils.get().getBaseFont();
	
	Dimension m_prinImageSize = new Dimension(400, (400*210)/297);//A4 native size
	
	int MARGIN_DEFAULT = 5;
	
	int MARGIN_MIN = 1;
	
	int MARGIN_MAX = 25;
	
	int m_gapWidth = 4; //gap between images on the page
	
	int m_horMargin = MARGIN_DEFAULT;
	
	int m_vertMargin = MARGIN_DEFAULT;
	/**
	 * viewHorMargin's is only for decorative purpose - never used it to
	 * calculate printable image !!!
	 */
	int m_viewHorMargin = 10;
	
	int m_viewVertMargin = 10;
	
	int m_maxS;
	
	int m_currentViewPage = 0;
	
	int  m_maxNumberOfPages =1;
	
	JLabel m_pageInfoLabel;
	
	CustomListener m_listener;
	


	/**
	 * @param fr - parent frame
	 * @param list - list of images which are selected by user. Only one page will be created,
	 * the maximum of images on the page is 9. if the amount of selected images is < then 9, then
	 * images will be repeated to fill the page
	 */
	public PaPrintDialog(JFrame fr,ArrayList<PaImage> list) {
		
		super(fr);
		
		setTitle(getGuiStrs("printDialogTitle"));
		
		m_list = list;
		
		//the image in the label will be square - in order to fit portrait/landscape without resizing
		int w = m_prinImageSize.width + 2*m_viewHorMargin;
		
		int h = m_prinImageSize.height + 2*m_viewVertMargin;
		
		m_maxS = w > h ? w : h ;
		
		m_maxPixelSize =  maxSize(list);
		
		createGUI();
		
		setBounds(200,200,700,m_maxS+100);
		
		//pack();
		setResizable(false);
		
		repaintViewImage(0);
	}
	/**
	 * Creates all UI components
	 */
	private void createGUI() {
		
		JPanel mainPanel = PaGuiTools.createVerticalPanel();
		
		JPanel hPanel = PaGuiTools.createHorizontalPanel();
		
		JPanel paramPanel = PaGuiTools.createVerticalPanel();
		
		JPanel viewPanel = PaGuiTools.createVerticalPanel();
		
		m_infoLabel = new JLabel(getGuiStrs("infoPrintLabelCaption")+" " + m_list.size());
		
		JPanel labelPanel = PaGuiTools.createHorizontalPanel();
		
		labelPanel.add(m_infoLabel);   labelPanel.add(Box.createHorizontalGlue());
		
		m_imageLabel = new JLabel();
		
		viewPanel.add(m_imageLabel);
		
		viewPanel.setBorder((Border) new SoftBevelBorder(BevelBorder.LOWERED));
		
		//the image in the label will be square - in order to fit portrait/landscape without resizing
		Dimension d = new Dimension(m_maxS,m_maxS);
		
		m_imageLabel.setMaximumSize(d);
		
		m_imageLabel.setMinimumSize(d);
		
		String[] list = {
				
				getGuiStrs("autoPropListItem"),//0
				
				getGuiStrs("x9x13PropListItem"),//1
				
				getGuiStrs("x10x15PropListItem"),//2
				
				getGuiStrs("x13x18PropListItem")//3
				
			
				};


		m_comboSize = new JComboBox<String>(list);
		
		
		Integer[] list1 = { 
				1,//0
				2,//1
				4,//2
				6,//3
				9//4
				};
		
		m_comboNumber = new JComboBox<Integer>(list1);		
		
		PaGuiTools.makeSameSize(new JComponent[] { m_comboNumber,m_comboSize });
		
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new GridLayout(1, 2));
		
		panelButtons.add(m_landscapeRadio);
		
		panelButtons.add(m_portrRadio);
		
		PaGuiTools.fixComponentSize(panelButtons);
		

		Border title_0 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("typeOfImageSizeCropOperation"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		panelButtons.setBorder(title_0);
		
		m_butGroupType.add(m_landscapeRadio);
		
		m_butGroupType.add(m_portrRadio);
		
		m_landscapeRadio.setSelected(true);
		
		JPanel comboPanel1 = PaGuiTools.createHorizontalPanel();
		
		JPanel comboPanel2 = PaGuiTools.createHorizontalPanel();
		
	
		
		JLabel l1 = new JLabel(getGuiStrs("printComboSizeLabel")+" ");
		
		JLabel l2 = new JLabel(getGuiStrs("printComboNumberLabel")+ " ");
		
		comboPanel1.add(l1); comboPanel1.add(m_comboSize); comboPanel1.add(Box.createHorizontalGlue());
		
		comboPanel2.add(l2); comboPanel2.add(m_comboNumber); comboPanel2.add(Box.createHorizontalGlue());
			
		PaGuiTools.makeSameSize(new JComponent[] { l1,l2 });
		 
		JPanel marginsPanel = PaGuiTools.createHorizontalPanel();
		
		SpinnerNumberModel spModel = new SpinnerNumberModel(MARGIN_DEFAULT, MARGIN_MIN,
				MARGIN_MAX, 1);
		
		m_topMarginSpinner = new JSpinner(spModel);
		
		SpinnerNumberModel spModel1 = new SpinnerNumberModel(MARGIN_DEFAULT, MARGIN_MIN,
				 MARGIN_MAX, 1);
		
		m_leftMarginSpinner = new JSpinner(spModel1);

		Border title1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("marginsGroupPrintCaption"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		marginsPanel.setBorder(title1);
		
		marginsPanel.add(m_lleft);	
		
		marginsPanel.add(m_leftMarginSpinner);
		
		marginsPanel.add(m_ltop);
		
		marginsPanel.add(m_topMarginSpinner);
		
		marginsPanel.add(Box.createHorizontalGlue()); 
		
		PaGuiTools.fixComponentSize(marginsPanel);
		
		JPanel pagesPanel = PaGuiTools.createHorizontalPanel();
		
		Border title2 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				getGuiStrs("printPagesLabel"),
				TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,m_font);
		
		 pagesPanel .setBorder(title2);
		
		 
		
		JLabel pagesLabel1 = new JLabel(getGuiStrs("printPagesStartLabel")+" ");
		
		JLabel pagesLabel2 = new JLabel(" " + getGuiStrs("printPagesEndLabel")+" ");
		
		SpinnerNumberModel spModel2 = new SpinnerNumberModel(1, 1,
				 100, 1);
		m_pagesEndSpinner = new JSpinner(spModel2);

		SpinnerNumberModel spModel3 = new SpinnerNumberModel(1, 1,
				 99, 1);
		m_pagesStartSpinner = new JSpinner(spModel3);
		
		PaGuiTools.makeSameSize(new JComponent[] { m_topMarginSpinner,m_leftMarginSpinner,m_pagesEndSpinner,
				m_pagesStartSpinner });
		
		PaGuiTools.makeSameSize(new JComponent[] { pagesLabel1,pagesLabel2,m_ltop,m_lleft, m_ltop});	
		
		setPagesNumber(1);
		
		pagesPanel.add(pagesLabel1);   pagesPanel.add(m_pagesStartSpinner);
		
		pagesPanel.add(pagesLabel2);   pagesPanel.add(m_pagesEndSpinner);  pagesPanel.add(Box.createHorizontalGlue());
		
		PaGuiTools.fixComponentSize(pagesPanel);
		
		JPanel panelButton = PaGuiTools.createHorizontalPanel();
		
		m_pageInfoLabel = new JLabel(); 
		
		panelButton.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panelButton.add(m_backButton);
		
		panelButton.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT)); 
		
		panelButton.add(m_forwButton);
		
		panelButton.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT)); 
		
		panelButton.add(m_pageInfoLabel );
		
		panelButton.add(Box.createHorizontalGlue()); 
		
		panelButton.add(m_printButton);
		
		panelButton.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));  
		
		panelButton.add(m_cancelButton); 
		
		panelButton.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT)); 
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(labelPanel);
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(comboPanel1);
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(comboPanel2);
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(panelButtons);
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(marginsPanel);
		
		paramPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		paramPanel.add(pagesPanel);
		
		paramPanel.add(Box.createVerticalGlue());
		
		hPanel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		hPanel.add(viewPanel);
		
		hPanel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		hPanel.add(paramPanel);
		
		hPanel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		mainPanel.add(hPanel);
		
		mainPanel.add(panelButton);
		
		add(mainPanel);

		setListeners();
		
		setToolTips();
		
		PaUtils.setComponentsFont(this, m_font);
	}
	
	/**
	 * repaints the preview according to current user choices
	 */
	public void repaintViewImage(int pageIndex) {
		//preview is a square region
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			BufferedImage imV = new BufferedImage(m_maxS,m_maxS,BufferedImage.TYPE_INT_RGB);		
			
			Graphics2D g2 = imV.createGraphics();
			
			g2.setColor(this.getBackground());	
			
			g2.fillRect(0, 0, m_maxS, m_maxS);
			
			//this function is used for view purpose only, that's why the last parameter = false 
			//(boost variant of image can be used)
			double[] margins = {m_horMargin, m_vertMargin, m_horMargin, m_vertMargin };
			
			BufferedImage im = getPrintableImage(pageIndex,!m_landscapeRadio.isSelected(), m_prinImageSize,
					margins, true);
		
			int offsetX = (m_maxS - m_prinImageSize.width)/2;
			
			int offsetY = (m_maxS - m_prinImageSize.height)/2;
			
			if(m_portrRadio.isSelected()) {
				
				offsetX = (m_maxS - m_prinImageSize.height)/2;
				
				offsetY = (m_maxS - m_prinImageSize.width)/2;
			}
			
			g2.drawImage(im,null,offsetX,offsetY);
			
			//draw border around visual view page
			g2.setColor(PaUtils.get().getSelectionColor());
			
			if(m_portrRadio.isSelected()) {
				
				g2.drawRect(offsetX,offsetY, m_prinImageSize.height, m_prinImageSize.width);
			}
			else {
				
				g2.drawRect(offsetX,offsetY, m_prinImageSize.width, m_prinImageSize.height);
			}
			
			m_imageLabel.setIcon(new ImageIcon(imV));
			
			m_pageInfoLabel.setText(" " +getGuiStrs("pageNameInfoLabelPrint") + " " + (pageIndex + 1));
		}
		finally {
			
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
		}
	}
	
	/**
	 * Gets the printable image or image for preview for the case of auto location of images on the screen
	 * @param portrait - orientation , true for portrait
	 * @param imagesNumber - number of images on the page
	 * @param size - printable or preview size( the size of area on whixh preview or printing takes place)
	 * @param margins - [0] - left margin, [1] - right , [2] - top, [3] - bottom
	 * @param onlyOriginalImage - true if only the original (no boost) image must be used for operation
	 * @return the image which is drawn in accordance with all parameters
	 */
	public BufferedImage getPrintImage(int pageIndex, boolean portrait, int imagesNumber, Dimension size,
			double[] margins, boolean onlyOriginalImage) {
		
		int horMargin = (int)margins[0];//left margin
		
		int vertMargin = (int)margins[1]; //top margin
		
		int listW = size.width;
		
		int listH = size.height;
		
		if(portrait) {			
			
			listH  = size.width;
			
			listW  = size.height;
		}
		
		BufferedImage im = new BufferedImage(listW,listH,BufferedImage.TYPE_INT_RGB);	
		
		ArrayList<Rectangle> rectList = new ArrayList<Rectangle>();
		
		int[] d = getNumberOfColumnsGaps(imagesNumber, portrait);
	
		int w = (listW - 2*horMargin - d[2]*m_gapWidth)/d[1];
		
		int h = (listH - 2*vertMargin - d[3]*m_gapWidth)/d[0]; 	
		
		Dimension rectDim = new Dimension(w,h);
		
		Point p = new Point(horMargin,vertMargin);
		
		for(int i=0; i < imagesNumber; ++i) {
			
			Rectangle r = new Rectangle(p,rectDim);
			
			rectList.add(r);
			
			p.move(p.x + w + m_gapWidth, p.y);
			
			if((i+1)%d[1] == 0) {
				
				p.move(horMargin, p.y + h + m_gapWidth);
			}
			
		}
		
		Graphics2D g2 = im.createGraphics();
		
	 	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2.setColor(Color.WHITE);
		
		g2.fillRect(0, 0, im.getWidth(), im.getHeight());
		
	   	int startIndex = pageIndex*imagesNumber;
		
		for(int i=0, index = startIndex; i < imagesNumber; ++i, ++index) {
			
			if(index >= (startIndex + imagesNumber) || index >= m_list.size()) { index = startIndex;}
			
			PaImage ph = m_list.get(index);
			
			BufferedImage imPh = getImageToDraw(ph,onlyOriginalImage);
			
			Rectangle r = rectList.get(i);
			
			imPh = fitImageToRectangle(imPh, r);
			
			g2.drawImage(imPh,null,r.x,r.y);
	             
		}
		return im;
		
	}
	
	/**
	 * Gets the printable image or image for preview for standard sizes: 9x13,10x15,13x18 
	 * @param pageIndex - index of the page; it is used for multipage printing
	 * @param portrait - orientation
	 * @param imagesNumber - the number of images on the page
	 * @param size - size of the printable or preview area
	 * @param wSt - standard width
	 * @param hSt - standard height
	 * @param onlyOriginalImage - true if the only original (no boost) image must be used
	 * @param margins - margins of the image (they can be changed by user in the native OS and printer dialog)
	 * @return
	 */
	public BufferedImage getPrintStandardImage(int pageIndex, boolean portrait, int imagesNumber, 
			Dimension size, double[] margins, double wSt, double hSt,boolean onlyOriginalImage) {
		
		int horMargin = (int)margins[0];//left margin
		
		int vertMargin = (int)margins[1]; //top margin
		

		int hSm = (int)(hSt*size.height/21.0); //A4 format
		
		int wSm = (int)(wSt*size.width/29.70); //A4 format
		
		if( hSt > 9 && imagesNumber  == 2) {
			
			hSm = (int)(wSt*size.height/21.0); //A4 format
			
			wSm = (int)(hSt*size.width/29.70); //A4 format
		}
		
		int listW = size.width;
		
		int listH = size.height;
		
		BufferedImage im = new BufferedImage(listW,listH,BufferedImage.TYPE_INT_RGB);	
		
		ArrayList<Rectangle> rectList = new ArrayList<Rectangle>();
			
		int[] d = getNumberOfColumnsGaps(imagesNumber, portrait);
		
		int w = wSm;
		
		int h = hSm; 
		
		Dimension rectDim = new Dimension(w,h);
		
		Point p = new Point(horMargin,vertMargin);
		
		for(int i=0; i < imagesNumber; ++i) {
			
			Rectangle r = new Rectangle(p,rectDim);
			
			rectList.add(r);
			
			p.move(p.x + w + m_gapWidth, p.y);
			
			if((i+1)%d[1] == 0) {
				
				p.move(horMargin, p.y + h + m_gapWidth);
			}
		}
		
		Graphics2D g2 = im.createGraphics();
		
	 	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2.setColor(Color.WHITE);
		
		g2.fillRect(0, 0, im.getWidth(), im.getHeight());
		
    	int startIndex = pageIndex*imagesNumber;
				
		for(int i=0, index = startIndex; i < imagesNumber; ++i, ++index) {
			
			if(index >= (startIndex + imagesNumber) || index >= m_list.size()) { index = startIndex;}
			
			PaImage ph = m_list.get(index);
			
			//fo print purpose we use only original image onlyOriginalImage == true
			BufferedImage imPh = getImageToDraw(ph,onlyOriginalImage);
			
			if(imPh == null) {  continue; }
			
			Rectangle r = rectList.get(i);
			
			boolean reverse = false;
			
			//the case when the choice number = 2 and format 10x15 and 13x18 - in this case we must rotate images to fit two images on the page A4
			//in the specific way; function fitImageToStandardRectangle must know how to do this and uses flag reverse for it
			if( hSt > 9 && imagesNumber  == 2) { reverse = true; }
			
			imPh = fitImageToStandardRectangle(imPh,imagesNumber,size,wSt,hSt,reverse);
			
			g2.drawImage(imPh,null,r.x,r.y);
	             
		}
		
		
		if(portrait && im.getWidth()>im.getHeight()) {	
			
			im =  PaAlgoTransform.getRotatedImage(im,90,true);
		}

		return im;
		
	}
	
	/**
	 * Creates the image for the parameters
	 * @param portrait - true if the paper orientation is portrait
	 * @param realSize - the size of printable area; for preview case this is a size of preview component (label's icon)
	 *  on the screen; for print case this size is a printable size which must be receive for printer side
	 * @param margins - [0] - left margin, [1] - right , [2] - top, [3] - bottom
	 * @param originalImageOnly - true if the original image must be used for creating printable image;
	 * the original image is used for real printing; for preview the boost image can be used, so this
	 * parameter can be false
	 * @return the image which is ready for preview or printing
	 */
	public BufferedImage getPrintableImage(int pageIndex,boolean portrait, Dimension realSize, double[] margins,
			boolean originalImageOnly) {
		
		BufferedImage im = null;
		
		switch(m_comboSize.getSelectedIndex()) {
		
			default :
				
			case 0 : { //Auto
				
				im = getPrintImage(pageIndex,portrait,m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
						realSize, margins, originalImageOnly);
				
				break;
			}
			case 1 : { //9x13
				
				im = getPrintStandardImage(pageIndex,portrait,m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
						realSize,margins, 13,9, originalImageOnly);
				
				break;
			}
			case 2 : { //10x15
				
				im = getPrintStandardImage(pageIndex,portrait,m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
						realSize,margins, 15,10, originalImageOnly);
				
				break;
			}
			case 3 : { //13x18
				
				im = getPrintStandardImage(pageIndex,portrait,m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
						realSize,margins, 18,13, originalImageOnly);
				
				break;
			}
			case 4 : { //215,9  279,4 μμ
				
				im = getPrintStandardImage(pageIndex,portrait,m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
						realSize,margins, 27.94,21.59,originalImageOnly);
				
				break;
			}
		
		}
		
		return im;
	}
	
	/**
	 * 
	 * @param imagesNumber - number of images on the page
	 * @return the number of required gaps; [0] - rows; [1] - columns;  [2] - horizontal direction gaps, [3] - vertical direction  gaps ; all calculation is in the  landscape base
	 */
	private int[] getNumberOfColumnsGaps(int imagesNumber, boolean portrait) {
		
		int[] d = new int[4];
		
		switch(imagesNumber) {
		
			default:
				
			case 1: {
				
				d[0] = 1; 
				
				d[1] = 1;
				
				d[2] = 0; 
				
				d[3] = 0;
			
				break;
			} 
			case 2: {
				
				d[0] = 1; 
				
				d[1] = 2;
				
				d[2] = 0; 
				
				d[3] = 1;
				
				break;
			}
			case 4: {
				
				d[0] = 2; 
				
				d[1] = 2;
				
				d[2] = 1; 
				
				d[3] = 1;
				
				break;
			} 
			case 6: {
				
				if(portrait) {
					
					d[0] = 3;
					
					d[1] = 2;
					
					d[2] = 2; 
					
					d[3] = 1;
					
				} else {
					
					d[0] = 2; 
					
					d[1] = 3;
					
					d[2] = 1; 
					
					d[3] = 2;
				}
				break;
			} 
			case 9: {
				
				d[0] = 3; 
				
				d[1] = 3;
				
				d[2] = 2; 
				
				d[3] = 2;
				break;
			} 
		
		};
		
		return d;
	}

	/**
	 *  If the boost image doesn't exist the we return the full image
	 * @param ph - image object to draw
	 * @param onlyOriginalImage - if this parameter = true then only original image is used 
	 * @return return the image ; null in the case if the image file can't be read
	 */
	private BufferedImage getImageToDraw(PaImage ph, boolean onlyOriginalImage) {
		
		File f; 
		
		if(onlyOriginalImage) {
			
			f = new File(ph.getFullPath());
			
		}
		else {
			
			f = new File(PaUtils.get().getFullPathToBoostImage(ph.getId()));
			
			if(!f.exists()) {
				
				f = new File(ph.getFullPath());
			}
			
		}
		if(!f.exists()) { 
			
			return null;
		}
		
		BufferedImage ic = null;
		
		try {
			
			ic = ImageIO.read(f);
			
		} catch (IOException e) {
			
			return null;
		}
		
		return ic;
	}
	/**
	 * Fits the size of image into rectangle
	 * @param imPh - image to fit into rectangle r
	 * @param r - rectangle to fit in
	 * @return new image
	 */
	private BufferedImage fitImageToRectangle(BufferedImage im, Rectangle  r) {
		
		BufferedImage imPh = null;
		
		if(im == null) {
			
	        PaLog.writeLog("Can't find original image to fit it into printable area", null, true, false, true );
	        
	        imPh = getNonValidImage();
		}
		else {
			imPh = im;
		}
		
		int im_w = imPh.getWidth();
		
		int im_h = imPh.getHeight();
		
		double aspectRatio = ((double)im_w)/im_h;
		
		//PaAlgorithms al = new PaAlgorithms();
		
		if(im_w < im_h) {
		 
			imPh =  PaAlgoTransform.getRotatedImage(imPh,90,false);
			
			im_w = imPh.getWidth();
			
			im_h = imPh.getHeight();
			
			aspectRatio = ((double)im_w)/im_h;
		}
		//double k = r.width/im_w;
		im_w = r.width;
		
		im_h = (int)(im_w/aspectRatio);
		
		if(im_h > r.height ) {
			
			im_h = r.height;
			
			im_w = (int)(im_h*aspectRatio);
		}
		
		return  PaAlgoTransform.resizeImage(imPh,im_w,im_h);
		
	}
	
	/**
	 * Fits the size of image into rectangle for standard sizes 9x13,10x15 etc;
	 *  the function works for A4 scale only
	 * @param imPh - image to fit into rectangle r
	 * @param r - rectangle to fit in
	 * @param wSt - standard width (for 13/9. 15/10 etc
	 * @param hSt -  standard height (for 13/9. 15/10 etc
	 * @return new image
	 */
	private BufferedImage fitImageToStandardRectangle(BufferedImage im, int imagesNumber, 
			Dimension size, double wSt, double hSt, boolean reverse) {
		
		BufferedImage imPh = null;
		
		if(im == null) {
			
	        PaLog.writeLog("Can't find original image to fit it into printable area", null, true, false, true );
	        
	        imPh = getNonValidImage();
		}
		else {
			
			imPh = im;
		}
		
		double etalonRatio = (double)(wSt)/hSt;
		
		int hSm = (int)(hSt*size.height/21.0); //A4 format
		
		int wSm = (int)(wSt*size.width/29.70); //A4 format
		
		if( hSt > 9 && imagesNumber  == 2) {
			
			etalonRatio = (double)(hSt)/wSt;
			
			hSm = (int)(wSt*size.height/21.0); //A4 format
			
			wSm = (int)(hSt*size.width/29.70); //A4 format
		}
	
		int im_w = imPh.getWidth();
		
		int im_h = imPh.getHeight();
		
		double aspectRatio = ((double)im_w)/im_h;
	
		//this trick allows us to switch in the case when standard size chosen by user doesn't fit in A4 
		//usually this the case - 2 images on the list and sizes 10x15 and 13x18 - they must be rotated in opposite way to layout on the page
		if(reverse) { 
			
			reverse = im_w > im_h; 
		}
		else { 
			reverse = im_w < im_h;
		}
		
		if(reverse) {
		 
			imPh =  PaAlgoTransform.getRotatedImage(imPh,90,false);
			
			im_w = imPh.getWidth();
			
			im_h = imPh.getHeight();
			
			aspectRatio = ((double)im_w)/im_h;
		}
		
		if(Math.abs(aspectRatio - etalonRatio) > 0.1) {
			
			int newH = (int)(im_w/etalonRatio);
			
			if(newH < im_h) {
				
				imPh = imPh.getSubimage(0, 0, im_w, newH);
			}
			else {
				imPh = imPh.getSubimage(0, 0, (int)(im_h*etalonRatio), im_h);
			}
			
		}
	
		return  PaAlgoTransform.resizeImage(imPh,wSm,hSm);
		
	}
	/**
	 * Sets tooltips for all components
	 */
	private void setToolTips() {
		
		m_comboSize.setToolTipText(getGuiStrs("comboSizePrintTooltip"));	
		
		m_comboNumber.setToolTipText(getGuiStrs("comboNumPrintTooltip"));
		
		m_landscapeRadio.setToolTipText(getGuiStrs("radioLandPrintTooltip"));
		
		m_portrRadio.setToolTipText(getGuiStrs("radioPortPrintTooltip"));
		
		m_printButton.setToolTipText(getGuiStrs("buttonPrintTooltip"));
		
		m_imageLabel.setToolTipText(getGuiStrs("imageLabelPrintTooltip"));
	
		m_leftMarginSpinner.setToolTipText(getGuiStrs("leftMarginSpinPrintTooltip"));
		
		m_topMarginSpinner.setToolTipText(getGuiStrs("topMarginSpinPrintTooltip"));
		
		m_pagesEndSpinner.setToolTipText(getGuiStrs("pagesSpinEndPrintTooltip"));
		
		m_pagesStartSpinner.setToolTipText(getGuiStrs("pagesSpinStartPrintTooltip"));
		
		m_backButton.setToolTipText(getGuiStrs("backButtonPrintTooltip"));
		
		m_forwButton.setToolTipText(getGuiStrs("forwButtonPrintTooltip"));
		
	}
	
	
	/**
	 * Sets listeners for all components
	 */
	private void setListeners() {
		
		m_listener = new CustomListener();
		
		m_comboSize.addActionListener(m_listener);
		
		m_comboNumber.addActionListener(m_listener);
		
		m_cancelButton.addActionListener(m_listener);
		
		m_printButton.addActionListener(m_listener);
		
		m_backButton.addActionListener(m_listener);
		
		m_forwButton.addActionListener(m_listener);
		
		ButtonsListener l1 = new ButtonsListener();
		
		m_landscapeRadio.addActionListener(l1);
		
		m_portrRadio.addActionListener(l1);
		
		SpinnerListener l2 = new SpinnerListener();
		
		m_leftMarginSpinner.addChangeListener(l2);
		
    	m_topMarginSpinner.addChangeListener(l2);
	}
	
	
   class CustomListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == m_comboSize ) {
				
				changeNumberList();
				
				setPagesNumber(1);   

				repaintViewImage(m_currentViewPage);
				
	    		return;
	    		
	    	}
			if(e.getSource() == m_comboNumber ) {
				
				setPagesNumber(m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex())); 
				
				repaintViewImage(m_currentViewPage);
				
	    		return;
	    	}
			if(e.getSource() == m_cancelButton) {
				
				dispose();
				
	    		return;
	    	}
			if(e.getSource() == m_printButton) {
				
				startPrint();
				
	    		return;
	    	}
			if(e.getSource() == m_backButton) {
				
				--m_currentViewPage;
				
				if(m_currentViewPage < 0 ) { m_currentViewPage = 0; }
				
				repaintViewImage(m_currentViewPage);
				
	    		return;
	    	}
			if(e.getSource() == m_forwButton) {
				
				++m_currentViewPage;
				
	    		if(m_currentViewPage == m_maxNumberOfPages) {m_currentViewPage = m_maxNumberOfPages-1; }
	    		
	    		repaintViewImage(m_currentViewPage);
	    		
	    	}
		}
    } 
   
	/**
	 * 
	 * @author avd
	 * <p>Listener class to listen the radio buttons</p>
	 *
	 */
	class ButtonsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == m_landscapeRadio) {
				
				m_ltop.setText(" " + getGuiStrs("printTopMarginLabel")+ " ");
				
				m_lleft.setText(getGuiStrs("printLeftMarginLabel")+ " ");
				
				m_currentViewPage = 0;
				
				repaintViewImage(m_currentViewPage);
				
	    		return;
	    	}
			if(e.getSource() == m_portrRadio) {
				
				m_ltop.setText(" " + getGuiStrs("printRightMarginLabel")+ " ");
				
				m_lleft.setText(getGuiStrs("printTopMarginLabel")+ " ");
				
				m_currentViewPage = 0;
				
				repaintViewImage(m_currentViewPage);
				
	    		return;
	    	}
		}

	
	}
	/**
	 * Changes the list in the combobox of numbers according to choice in the size's combobox m_comboSize 
	 */
	private void changeNumberList() {
		
		m_comboNumber.removeActionListener(m_listener);
		
		switch(m_comboSize.getSelectedIndex()) {
		
			default :
				
			case 0 : {
				
				m_comboNumber.removeAllItems();
				
				m_comboNumber.addItem(new Integer(1));
				
				m_comboNumber.addItem(new Integer(2));
				
				m_comboNumber.addItem(new Integer(4));
				
				m_comboNumber.addItem(new Integer(6));
				
				m_comboNumber.addItem(new Integer(9));
				
				m_comboNumber.setSelectedIndex(0);
				
				break;
			}
			case 1 : {
				
				m_comboNumber.removeAllItems();
				
				m_comboNumber.addItem(new Integer(1));
				
				m_comboNumber.addItem(new Integer(2));
				
				m_comboNumber.addItem(new Integer(4));
				
				m_comboNumber.setSelectedIndex(0);
				break;
			}
		
			case 2 :
			case 3 : {
				
				m_comboNumber.removeAllItems();
				
				m_comboNumber.addItem(new Integer(1));
				
				m_comboNumber.addItem(new Integer(2));
				
				m_comboNumber.setSelectedIndex(0);
				
				break;
			}
			case 4 : {
				
				m_comboNumber.removeAllItems();
				
				m_comboNumber.addItem(new Integer(1));
				
				break;
			}
		}; 
		
		m_comboNumber.addActionListener(m_listener);
		
	}
	
	
   /**
    * Starts the new print job in the different Thread
    */
    private void startPrint() {
    	
    	if(validateData()) {
    	
    		new Thread(new PaPrintRun()).start(); 
    	}
    	
    }

    /**
     * 
     * @author avd
     * Class which is responsible for start of print process.
     */
    private class PaPrintRun implements Runnable {
    	
    	PrinterJob m_printJob = null;
    	
    	PrintRequestAttributeSet m_printInfo = null;
    	
    	 int[][] m_rangePrintPages;
    	
        public PaPrintRun() {}

        @Override
        public void run() {
        	
            m_printJob = PrinterJob.getPrinterJob();
            
            m_printJob.setPrintable(new PaPrintable(m_printJob));
            
            m_printInfo = new HashPrintRequestAttributeSet();
            
            if(m_landscapeRadio.isSelected()) {
            	
            	m_printInfo.add(OrientationRequested.LANDSCAPE);
            }
            else {
            	
            	m_printInfo.add(OrientationRequested.PORTRAIT);
           
            }
            
         	//A4 hardcoded !!! from MediaPrintableArea documentation:
            //The rectangular printable area is defined thus: The (x,y) origin is positioned at the 
            //top-left of the paper in portrait mode regardless of the orientation specified in the 
            //requesting context
      	   m_printInfo.add(new MediaPrintableArea(m_horMargin,m_vertMargin, 
               		210-2*m_horMargin, 297 - 2*m_vertMargin,MediaPrintableArea.MM));
      	   
            m_printInfo.add(new Copies(1));
            
            m_printInfo.add(new JobName("phorg job", null));
            
            m_printInfo.add(new PageRanges((int)m_pagesStartSpinner.getValue(),(int)m_pagesEndSpinner.getValue()));
            
         

            if (m_printJob.printDialog(m_printInfo)) {
                try {
                	
                    PageRanges range = (PageRanges)m_printInfo.get(PageRanges.class);
                    
                    m_rangePrintPages = range.getMembers();

                	PaLog.writeLog("Start pprinting ...", null, true, false, false ); 
                    m_printJob.print(m_printInfo);
                    
                } catch (PrinterException e) {
                	
                    PaLog.writeInfoOnly(getMessagesStrs("printJobWasNotSucd"));
                    
                    PaLog.writeLog("PrintException: " + e.getMessage(), e, true, 
                			false, true ); 
                }
            }
        }
        /**
         * 
         * @author avd
         *
         */
        private class PaPrintable implements Printable {

  
            private int             orientation;
            
            private BufferedImage   image;

            public PaPrintable(PrinterJob printJob) {
            	
                PageFormat pageFormat = printJob.defaultPage();
  
                if(!m_landscapeRadio.isSelected()) {
                	pageFormat.setOrientation(PageFormat.PORTRAIT);
                }
                else {
                	pageFormat.setOrientation(PageFormat.LANDSCAPE);
                }
                
                this.orientation = pageFormat.getOrientation();
         
            }

            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex)
                    throws PrinterException {
            	
                  if (pageIndex <  m_rangePrintPages[0][1] && 
                		  pageIndex >=  (m_rangePrintPages[0][0]-1)) {
                	  
                	
                    Graphics2D g2d= (Graphics2D)g;
                	
                	g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                       
                    int pWidth = (int)(pageFormat.getImageableWidth());///72 *resolution);
                    
                    int pHeight = (int)(pageFormat.getImageableHeight());///72 *resolution);
                    

                    float[] ds = getPrinterMargins();//result in inch
            
                    //Copy to album
                    double[] margins = {ds[0]*72, ds[1]*72};
                    
                    //the idea here is to receive a big image which contains subimages in their full
                    //pixel sizes; the reason: there is a scale during printing - to avoid the loss of quality 
                    Dimension fullSize = getFullSizeImage(orientation, Paper.A4);
                    
                    Dimension fSize = fullSize;
                    
                    //this reverse of width and height is done because inside call of  getPrintableImage some function can be called
                    //which are used not only for real printing, but for creation of print preview; while this creation some functions can rotate
                    //portrait image - so this is done to correct this rotation
                    if(orientation == PageFormat.PORTRAIT) {
                    	fSize = new Dimension(fullSize.height, fullSize.width);
                    }
                    
                    this.image = getPrintableImage(pageIndex,orientation == PageFormat.PORTRAIT, 
                    		fSize,margins,true);
                    
                    //these coeff's are very essential for right proportions of result image;
                    //we use them to set proportions according to imageable area form PageFormat
                    //
                    double wCoeff = pageFormat.getImageableWidth()/pageFormat.getWidth();
                    
                    double hCoeff = pageFormat.getImageableHeight()/pageFormat.getHeight();
                    
       
                    image = image.getSubimage((int)margins[0], 
                    		(int)margins[1], (int)(fullSize.width*wCoeff), (int)(fullSize.height*hCoeff));
                    
                    
                   g2d.drawImage(image,(int)pageFormat.getImageableX(),(int)pageFormat.getImageableY(),
                		   pWidth, pHeight, null);
                    
                    setPrintedMarkers(pageIndex);
                    
                    PaLog.writeLog(getMessagesStrs("printJobWasDone") +" " + m_list.size(),
                    		null,true, true, false);
                    
                    return PAGE_EXISTS;
                } else {
                    return NO_SUCH_PAGE;
                }
            }
        }
 
        
        private float[] getPrinterMargins( ) {
			
        	MediaPrintableArea res = (MediaPrintableArea)m_printInfo.get(
        			MediaPrintableArea.class);
        	
        	float[] f = res.getPrintableArea(MediaPrintableArea.INCH);
                	
        	return f;
        	
        }
        
        
    }
    
    /**
     * Sets printed marks for printed images
     */
    private void setPrintedMarkers(int pageIndex) {
    	
    	PaImageContainer c =PaUtils.get().getMainContainer().getCurrentContainer();
    	
		if(c == null) { return; }
		
		int n = ((Integer)m_comboNumber.getSelectedItem());//number of images on the page
		
    	if(m_list.size() < n) {
    		
    		for(PaImage im: m_list) {	
    			
				c.getImage(im.getId()).setPrinted(true);
			}
		} else {
			
			int start = pageIndex*n;
			
			int end = start + n;
			
			for(int i = start; (i < end) && (i < m_list.size()); ++i) {	
				
				c.getImage(m_list.get(i).getId()).setPrinted(true);	
			}
		}
    	PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
	} 
    
    

	/**
	 * 
	 * @author avd
	 * <p>Listener class to listen all spinners </p>
	 *
	 */
	class SpinnerListener implements ChangeListener{
		
	    public void stateChanged(ChangeEvent e) {
	    	
	     if(e.getSource() == m_leftMarginSpinner || e.getSource() == m_topMarginSpinner ) {
	    	 
	    	m_horMargin =  (int)m_leftMarginSpinner.getValue();
	    	
	    	m_vertMargin = (int)m_topMarginSpinner.getValue();
	    	
	    	repaintViewImage(m_currentViewPage);
	     }
		    	
	    }
	}
	
	/**
	 * This function calculates the size of the whole printable or view image; the size is calculated to be big enough to contain
	 * all included images with their real full sizes. 
	 * @param format - portrait or landscape case
	 * @param p - A3 or A4 paper
	 * @return the dimension  which is big enough to contain included images which are full-sized
	 */
	Dimension getFullSizeImage(int format, Paper p) {
		

		//[0] - rows; [1] - columns;  [2] - horizontal direction gaps, [3] - vertical direction  gaps
		int[] data = getNumberOfColumnsGaps(m_comboNumber.getItemAt(m_comboNumber.getSelectedIndex()),
				format == PageFormat.PORTRAIT);
		
		int w = m_horMargin;
		
		int h = m_vertMargin;
		
		for(int i =0; i < data[1]; ++i) {
			
			w += m_maxPixelSize.width + m_gapWidth;
		}
		
		for(int i =0; i < data[0]; ++i) {
			
			h += m_maxPixelSize.height + m_gapWidth;
		}
		
		w += m_horMargin;
		
		h += m_vertMargin;
		
		double ratio;
		
		if(p == Paper.A4) {
			
			ratio = 210.0/297;
		}
		else {
			
			ratio = 297.0/420;
		}
		
		if(format == PageFormat.PORTRAIT) {
			
			int w1 = (int) (h*ratio);
			
			if(w1 < w ) {
				
				h = (int)(w /ratio);
			}
		}
		else {
			
			int h1 = (int)(w * ratio);
			
			if(h1 < h) {
				
				w = (int)(h / ratio);
			}
		}	
		
		return new Dimension(w,h);
	}
    
	/**
	 * 
	 * @param list - list of image's object
	 * @return the maximum size of images (this size can be complex - width from one image and height from another)
	 */
	Dimension maxSize(ArrayList<PaImage> list) {
		
		int w = 0 ;
		
		int h = 0;
		
		for(int i=0; i < list.size(); ++i) {
			
			PaImage ph = list.get(i);
			
			BufferedImage im = getImageToDraw(ph, true);
			
			if(im == null) {
				
		        PaLog.writeLog("Can't find image to detect maximum size: " + ph.getFullPath(), null, true, false, true ); 
		        
				continue;
			}
			
			
			int w1 = im.getWidth();
			
			int h1 = im.getHeight();
			
			//for portrait case; these is done because in the process of fitting images into paper
			//images with portrait orientation are rotated to fit better on the list
			if(h1 > w1) {
				
				w1 = im.getHeight();
				
				h1 = im.getWidth();
			}
			
			if(w1 > w) w = w1;
			
			if(h1 > h) h = h1;
	
		}
		//the maximum size can't be 0; in the case when there are problems with image find, we set the maximum size in arbitrary way
		if(w == 0 && h == 0) {
			
			w = 2000;
			
			h = (int)(w * 210/297.0);			
		}
		
		return new Dimension(w,h);
	}
	/**
	 * Sets maximum number of pages for spinner
	 * @param numberOnPage - current user's choice of number of images on a page
	 */
	void setPagesNumber(int numberOnPage) {
		
		int pages = (int) Math.ceil((double)m_list.size()/numberOnPage);
		
		SpinnerNumberModel spModel2 = new SpinnerNumberModel(pages, 1,
				 pages, 1);
		
		SpinnerNumberModel spModel3 = new SpinnerNumberModel(1, 1,
				 pages, 1);
		
		m_maxNumberOfPages = pages;
	
	    m_currentViewPage = 0;

		m_pagesEndSpinner.setModel(spModel2);
		
		m_pagesStartSpinner.setModel(spModel3);
	}
	/**
	 * 
	 * @return the image which represents the situation when an original image can't be found and processed
	 */
	private BufferedImage getNonValidImage() {
   
		int w = 200;
		
		int h = 200;
		
	    BufferedImage im = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    
	    Graphics2D g2 = im.createGraphics();
		
		g2.setColor(Color.WHITE);	
		
		g2.fillRect(0, 0, w, h);
		
		g2.setColor(Color.RED);
		
		g2.drawString(getMessagesStrs("noImageFoundMessage"), 10, h/2);
		
		return im;
	
	}
	/**
	 * 
	 * @return true if the data for print operation are valid
	 */
	private boolean validateData() {
		
		int s = (int)m_pagesStartSpinner.getValue();
		
		int t = (int)m_pagesEndSpinner.getValue();
		
		if(s > t ) {
			
    		JOptionPane.showMessageDialog(PaUtils.get().getMainWindow(),
    				getMessagesStrs("messageWrongPagesrangeItems"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.INFORMATION_MESSAGE);
			
			return false;
		}
		
		return true;
	}
	
	
}
