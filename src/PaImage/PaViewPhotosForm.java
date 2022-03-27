package PaImage;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaImage;
import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaEvents.PaEventSelect;
import PaGlobal.PaGuiTools;
import PaGlobal.PaSortOrderEnum;
import PaGlobal.PaUtils;
import static PaGlobal.PaLog.*;
import static PaGlobal.PaUtils.*;

/**
 * Main visual form to show images
 * @author avd
 *
 */
public class PaViewPhotosForm extends JPanel implements MouseListener,MouseMotionListener,Transferable  {
	
	private static final long serialVersionUID = 1L;
	
	PaViewPanel m_parentPanel =null;
	
	boolean m_mouseIsPressed = false;

	JPanel m_panel = null;
	/**
	 * these panels are for inner icon JLabel ; they are laid out in the form of cross in each other
	 */
	private JPanel m_backIconPanel1 = new JPanel();		

	/**
	 * these panels are for inner icon JLabel ; they are laid out in the form of cross in each other
	 */
	
	private JPanel m_backIconPanel2 = new JPanel();
	/**
	 * Main icon label; this label shows an image
	 */
	
	private JLabel m_iconLabel = new JLabel();	
	
	public int m_widthIcon;						// width of visible frame
	
	public int m_heightIcon;					// Height of visible frame
	
	/**
	 * selection flag
	 */
	
	private boolean m_selected = false;				
	/**
	 * The image which is associated with this form
	 */
	private PaImage m_formImage;
	
	private JTextField m_nameField;
	
	private JLabel m_printedLabel = new JLabel();
	
	private JLabel m_bookLabel = new JLabel( );
	
	private JLabel m_sliderLabel = new JLabel();
	
	private JLabel m_linkLabel = new JLabel( );
	
	private ImageIcon iconPrint= new ImageIcon(PaUtils.get().getIconsPath() + "paprintlabel.png");
	
	private ImageIcon iconPrintBW= new ImageIcon(PaUtils.get().getIconsPath() + "paprintlabelbw.png");
	
	private ImageIcon m_iconBook = new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabel.png");
	
	private ImageIcon m_iconBookBW= new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabelbw.png");
	
	private ImageIcon m_iconLink = new ImageIcon(PaUtils.get().getIconsPath() + "palinklabel.png");
	
	private ImageIcon m_iconLinkBW = new ImageIcon(PaUtils.get().getIconsPath() + "palinklabelbw.png");
	
	private ImageIcon m_iconSliderYes = new ImageIcon(PaUtils.get().getIconsPath() + "paslideryes.png");
	
	private ImageIcon m_iconSliderNo = new ImageIcon(PaUtils.get().getIconsPath() + "pasliderno.png");



	/**
	 * This flag can block the reaction of labels to mouse click: printed, slider, bookmarked
	 * Also it blocks the double click reaction; all these for filter view - this functionality can be dangerous in it
	 */
	private boolean m_blockLabelsReaction = false;


	public PaViewPhotosForm(PaImage photo, int scale) {
		
				
		this.setTransferHandler(new PaTransferHandler());

		m_iconLabel.setBackground(null);
		
		m_formImage = photo;
		
		setLayout(new FlowLayout());

		m_panel = new JPanel (new BorderLayout ());
		
		m_panel.setTransferHandler(new PaTransferHandler());
		
		JLabel lab = new JLabel (" . . . ");
		
		lab.setAlignmentX(SwingConstants.CENTER);
		
		m_printedLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		m_bookLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		m_linkLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		m_sliderLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel topPanel =  PaGuiTools.createHorizontalPanel();	 

		topPanel.add(m_linkLabel);
		
		topPanel.add(Box.createHorizontalStrut(10));
		
		topPanel.add(m_printedLabel);
		
		topPanel.add(Box.createHorizontalStrut(10));
		
		topPanel.add(m_bookLabel);
		
		topPanel.add(Box.createHorizontalStrut(10));
		
		topPanel.add(m_sliderLabel);
		
		Dimension dimension = new Dimension(scale, scale);
		
		int textLength = (int) Math.round(dimension.getWidth()/10) -2;
		
		m_nameField = new JTextField(textLength);
		
		m_nameField.setHorizontalAlignment(SwingConstants.LEFT);
		
		m_nameField.setEditable(false);
		
		m_nameField.setBorder(BorderFactory.createEmptyBorder());
		
		int w = m_formImage.getWidth();
		
		int h = m_formImage.getHeight();
		
		double w_h = (double) w/h;
				
		m_backIconPanel1 = PaGuiTools.createHorizontalPanel();
		
		m_backIconPanel2 = PaGuiTools.createVerticalPanel();
		
		m_backIconPanel1.add(Box.createHorizontalGlue());
		
		m_backIconPanel1.add(m_backIconPanel2);
		
		m_backIconPanel1.add(Box.createHorizontalGlue());
		
		m_backIconPanel2.add(Box.createVerticalGlue());
		
		m_backIconPanel2.add(m_iconLabel);
		
		m_backIconPanel2.add(Box.createVerticalGlue());
		
		if (w > h) {			

			m_widthIcon = scale;
			
			int hC = (int) Math.round(scale/w_h);
			
			m_heightIcon = hC <= 0 ? 2 : hC; 

		} else {
			
			int wC = (int) Math.round(scale*w_h);
			
			m_widthIcon = wC <= 0 ? 2 : wC;
			
			m_heightIcon = (int) Math.round(scale); 
			
		}
		
		m_backIconPanel1.setSize(dimension);
		
		m_backIconPanel1.setMaximumSize(dimension);
		
		m_backIconPanel1.setPreferredSize(dimension);
		
		m_backIconPanel1.setBorder(BorderFactory.createLoweredBevelBorder());
		
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		northPanel.add(topPanel);
		
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		southPanel.add(m_nameField);

		m_panel.add (northPanel, BorderLayout.NORTH);
		
		
		m_panel.add (m_backIconPanel1, BorderLayout.CENTER);
		
		m_panel.add (southPanel, BorderLayout.SOUTH);
		
		m_panel.setBorder(BorderFactory.createEtchedBorder());

		m_panel.addMouseListener(this);	
		
		m_printedLabel.addMouseListener(this);
		
		m_bookLabel.addMouseListener(this);
		
		m_linkLabel.addMouseListener(this);
		
		m_sliderLabel.addMouseListener(this);
		
		m_panel.addMouseMotionListener(this);	

		add (m_panel);
		
		setBackground(Color.WHITE);
		
		setToolTips();
		
		init();
		
	}

	/**
	 * Inits the data in the form
	 */
	public void init() {
		
		setIcons();
		
		//shows the information label; the type of information depends on settings
		if(!PaUtils.get().isDebugView()) {
			
			short mask = PaUtils.get().getSettings().getInfoBitmask();
			
			String s = "";
			
			if((mask & 1) == 1) { s = m_formImage.getName(); }
			
			if((mask & 2) == 2) { s += "  " + PaUtils.getFileNameFromString(
					
					m_formImage.getFullPath()); 
			}
			
			if((mask & 4) == 4) { s += "  " + m_formImage.getDateAsString(DATE_FORMAT); }
			
			if((mask & 8) == 8) { s += "  id = " + m_formImage.getId(); }
			
			m_nameField.setText(s);
		}
		else {		
			
			m_nameField.setText(m_formImage.getName()+" id =" +m_formImage.getId()+" sort Id =" +m_formImage.getSortId());
		}
		
	}

	public JPanel getFormPanel() {
		
		return m_panel;
	}
	/**
	 * 
	 * @return image which is associated with this form
	 */
	public PaImage getImage() {
		
		return m_formImage;
	}
	
	public JLabel getIconLabel() {
		
		return m_iconLabel;
	}

	
	public Image getScaledIm(Image srcImg) {
		
		return PaAlgoTransform.getScaledIm((BufferedImage)srcImg, m_widthIcon, m_heightIcon);
	
	}
	

	public boolean isSelected() {
		
		return m_selected;
	}


	/**
	 * Changes the background color of inner panels; in the form we have 2 panels in the view of cross,
	 * which are laid in each other; this is needed for central alignment and easy change of icon while, for example,
	 * rotate image operation
	 * @param flag- true if the form should be selected
	 */
	public void selectBackgroundPanels(boolean selected) {
		
		m_selected = selected;
		
		if(m_selected) {
			
			Color bg = PaUtils.get().getSelectionColor();
			
			m_backIconPanel1.setBackground(bg);
			
			m_backIconPanel2.setBackground(bg);
		}
		else {
			
			 m_backIconPanel1.setBackground(null);
			 
			 m_backIconPanel2.setBackground(null);
			
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(!m_blockLabelsReaction && e.getClickCount() == 2) {
			
			setSelected(true,e);
			
			switch(PaUtils.get().getSettings().getDblClickReactionType()){
			
				case 0: {
					
					PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.IMAGE_EDIT_EVENT));
					
					break;
				}
				case 1: {
					
					PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.IMAGE_PROP_EVENT));
					
					break;
				}
				case 2: {
					
					PaUtils.get().getViewPanel().openInstrumentsWindow();
					
					break;
				}
				default: {}
			}
			return;
		}

		if (SwingUtilities.isLeftMouseButton(e)) { 
			
			if (!m_blockLabelsReaction) {
				
				if(e.getComponent() == m_printedLabel ) {
					
					m_formImage.setPrinted( ! m_formImage.isPrinted());
				
					setIcons() ;
					
					//the reason of null check - this  can work in other (filter panel)
						if(m_parentPanel != null) {
							
							m_parentPanel.setInfoLabel();
							
							m_parentPanel.setSelectedInTableForm(m_formImage.getId());
						}
						
						PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
						
						return;
				}
				
				if ( e.getComponent() == m_bookLabel ) {
					
					m_formImage.setBookmarked( ! m_formImage.isBookmarked());
					
					setIcons() ;
					
					//the reason of null check - this can work in other (filter panel)
					if(m_parentPanel != null) {
						
						m_parentPanel.setInfoLabel();
						
						m_parentPanel.setSelectedInTableForm(m_formImage.getId());
					}
					
					PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
					
					return;
				
				}
				if ( e.getComponent() == m_sliderLabel ) {
					
					m_formImage.setVisibleInSlider( ! m_formImage.isVisibleInSlider());
					
					setIcons();
					
					//the reason of null check - this  can work in other (filter panel)
				if(m_parentPanel != null) {
					
					//parentPanel.setInfoLabel();
						m_parentPanel.setSelectedInTableForm(m_formImage.getId());
					}
					PaEventDispatcher.get().fireCustomEvent(new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED));
					
					return;
				
				}
			}	
			
			setSelected(!m_selected, e );
		}

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
		if (m_selected == false){
			
			m_backIconPanel1.setBackground(Color.LIGHT_GRAY);
			
			m_backIconPanel2.setBackground(Color.LIGHT_GRAY);
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		
		if (m_selected == false) {
			
			selectBackgroundPanels(false);
		}
	}
	@Override
    public void mousePressed(MouseEvent e) { 

		if (SwingUtilities.isRightMouseButton(e) && m_selected == false) {   
			
    		setSelected( true, e);
    		
    		if(e.getClickCount() == 1) return;
    	}
    }

	@Override
    public void mouseReleased(MouseEvent e) { 

    }
    
	@Override
	public void mouseDragged(MouseEvent e) {
    	
        if(PaUtils.get().getSortCombo().getSelectedIndex() == 
        		PaSortOrderEnum.toInt(PaSortOrderEnum.CUSTOM_ORDER)) {
        	
	        TransferHandler handler = this.getTransferHandler();
	        
	        handler.exportAsDrag(this, e, TransferHandler.MOVE);
        }
        else {    
   	
        	writeInfoOnly(getMessagesStrs("noDragForNonCustomSort"));
        }
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {

	}
    /**
     * Sets all top icons for the form depending on state of the image object 
     */
	public void setIcons( ) {
		
		if (  m_formImage.isPrinted() ) { m_printedLabel.setIcon(iconPrint); }
		
		else { m_printedLabel.setIcon(iconPrintBW); }
		
		if ( m_formImage.isBookmarked()   ) { m_bookLabel.setIcon(m_iconBook);}
		
		else { m_bookLabel.setIcon(m_iconBookBW); }
		
		if ( m_formImage.isLink()   ) { m_linkLabel.setIcon(m_iconLink);}
		
		else { m_linkLabel.setIcon(m_iconLinkBW); }
		
		if ( m_formImage.isVisibleInSlider()   ) { m_sliderLabel.setIcon(m_iconSliderYes);}
		
		else { m_sliderLabel.setIcon(m_iconSliderNo); }	
	}
    
	public void setSelected( boolean flag, InputEvent e) 
	{	
		 PaEventSelect eventSelect = new PaEventSelect();
		 
		 int onmask = InputEvent.SHIFT_DOWN_MASK ;//| MouseEvent.BUTTON1_DOWN_MASK;
		 
		 int offmask = InputEvent.CTRL_DOWN_MASK;
		 
		 if (e != null && ((e.getModifiersEx() & (onmask | offmask)) == onmask)) {
			 
			 eventSelect.setClearOtherSelection(false);
		 }
		 
		if ( flag ) {
			
			PaEventDispatcher.get().fireCustomEvent(eventSelect);

    		PaUtils.get().getSelectedImages().add(this);
    		
    		selectBackgroundPanels(true);
		}
		else {
			
			m_selected = false;
			
			PaEventDispatcher.get().fireCustomEvent(eventSelect);
			
			if (PaUtils.get().getSelectedImages().getList().size() > 0 ) {
				
				PaUtils.get().getSelectedImages().remove(this);
				
				m_backIconPanel1.setBackground(null);
				
				m_backIconPanel2.setBackground(null);
				
			}
			
		}
		
		//the reason of null check - this class can work in other (filter panel)
		if(m_parentPanel != null) m_parentPanel.m_panelGrid.requestFocusInWindow();
		
	}
	
	public void setEnsureSelected() {
		
		PaEventSelect eventSelect = new PaEventSelect();
		
		PaEventDispatcher.get().fireCustomEvent(eventSelect);

		PaUtils.get().getSelectedImages().add(this);
		
		selectBackgroundPanels(true);
		
		m_parentPanel.m_panelGrid.requestFocusInWindow();
	
	}
	
	
	public void setParentViewPanel( PaViewPanel p) {
		
		m_parentPanel =p;
		
	}
	

	
	private String getLinkToolTip()
	{
		if ( m_formImage.isLink()   ) { return getGuiStrs("linkTrueLabelToolTip"); }
		
		else { return getGuiStrs("linkFalseLabelToolTip");  }
	}


	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		
	     DataFlavor thisFlavor = null;

	        try {
	        	
	            thisFlavor = PaViewPanel.getDragAndDropFlavor();
	            
	        } catch (Exception ex) {
	        	
	        	writeLog("Exception for flavor data :  " , ex, true, false, true);
	        	
	            return null;
	        }

	        if (thisFlavor != null && flavor.equals(thisFlavor)) {
	        	
	            return this;
	        }

	        return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = {null};
          
        try {
        	
            flavors[0] = PaViewPanel.getDragAndDropFlavor();
            
        } catch (Exception ex) {
        	
        	writeLog("Exception for flavor data :  " , ex, true, false, true);
        	
            return null;
        }

        return flavors;
	}


	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        
        DataFlavor[] flavors = {null};
        
        try {
        	
            flavors[0] = PaViewPanel.getDragAndDropFlavor();
            
        } catch (Exception ex) {
        	
        	writeLog("Exception for flavor data :  " , ex, true, false, true);
        	
            return false;
        }

        for (DataFlavor f : flavors) {
        	
            if (f.equals(flavor)) {
            	
                return true;
            }
        }

        return false;
	}

	/**
	 * Sets width of the image in the form
	 * @param w - width to set
	 */
	public void setIconWidth(int w) {  m_widthIcon = w;  }
	/**
	 * Sets height of the image in the form
	 * @param h - height to set
	 */
	public void setIconHeight(int h) {  m_heightIcon = h; }
	/**
	 * 
	 * @param block - true if you want to block the reaction of labels to mouse click
	 */
	public void setBlockForAllInfoLabel(boolean block) {  
		
		m_blockLabelsReaction = block; 
		
		if(m_blockLabelsReaction) {
			
			String s = getGuiStrs("noReactionViewLabelToolTip");
			
			m_printedLabel.setToolTipText(s);
			
			m_bookLabel.setToolTipText(s);
			
			m_linkLabel.setToolTipText(s);
			
			m_sliderLabel.setToolTipText(s);
		}
		else {
			
			setToolTips();
		}
		
	}
	/**
	 * Sets tooltips for info labels
	 */
	private void setToolTips() {
		
		m_printedLabel.setToolTipText(getGuiStrs("printLabelToolTip"));
		
		m_bookLabel.setToolTipText(getGuiStrs("bookLabelToolTip"));
		
		m_linkLabel.setToolTipText(getLinkToolTip());
		
		m_sliderLabel.setToolTipText(getGuiStrs("sliderYeNoLabelToolTip"));
	}

}

