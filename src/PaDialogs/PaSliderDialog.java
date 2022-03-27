package PaDialogs;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.NEXT_ROW;
import static PaGlobal.PaUtils.getGuiStrs;
import static PaGlobal.PaUtils.DATE_FORMAT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PaAlgorithms.PaAlgoTransform;
import PaCollection.PaImage;

import PaEvents.PaEvent;
import PaEvents.PaEventDispatcher;
import PaGlobal.PaDesktopPane;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

/**
 * <p>Slider window to show images statically or dynamically w</p>
 * @author avd
 *
 */
public class PaSliderDialog extends JFrame  {
	
	private static final long serialVersionUID = 1L;
	
	 private JPanel m_visualPanel = null;
	 
	 private JLabel m_imageLabel= null;
	 
	 private JButton m_backtoStartBut = null;
	 
	 private JToggleButton m_bookBut = null;
	 
	 private JToggleButton m_sliderShowBut = null;
	 
	 private JButton m_pauseBut = null;
	 
	 private JButton m_startBut = null;
	 
	 private JButton m_forwardBut = null;
	 
	 private JButton m_backBut = null;
	 
	 private JScrollPane m_scrollPane = null;
	 
	 private JPanel m_buttonPanel = null;
	 
	 private JPanel m_complexPanel = null;
	 
	 private JPanel	m_infoPanel = null;
	 
	 private JLabel m_infoLabel = null;
	 
	 private JCheckBox m_infoCheckBox = null;
	 
	 private  ImageIcon m_bookIconBw = new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabelbw.png");
	 
	 private  ImageIcon m_bookIcon = new ImageIcon(PaUtils.get().getIconsPath() + "pabooklabel.png");
	 
	 private  ImageIcon m_sliderNoIcon = new ImageIcon(PaUtils.get().getIconsPath() + "pasliderno.png");
	 
	 private  ImageIcon m_sliderYesIcon = new ImageIcon(PaUtils.get().getIconsPath() + "paslideryes.png");
	 
	 /**
	  * Current image
	  */
	 PaImage m_current = null;
	
	 private Timer m_timer = new Timer();
	
	 private PaTimerTask  m_task = new PaTimerTask();
	/**
	 * <p>Main queue to hold set of images</p>
	 */
	 private PaImageQueue m_Queue = null;
	
	 private int m_height =600;
	 
	 private int m_width = 600;
     
     private int m_period = 3000;
	
     
 	Forwarder m_forwarder = new Forwarder();
 	
 	
 	private int SLMIN = 0;
 	
 	private int SLMAX = 10;
 	
 	private int SLINIT = 3;    //initial frames per second
 
 	JSlider m_delaySlider = new JSlider(JSlider.HORIZONTAL,SLMIN, SLMAX, SLINIT);
 	


 	public PaSliderDialog(JFrame frame, String title, ArrayList<PaImage> cont) {
		
		super();
		
		this.setTitle(title);
			
		m_Queue = new PaImageQueue(cont);
		
		createUI();
		
	 	m_delaySlider.addChangeListener(new  Changer());

	 	//Turn on labels at major tick marks.
	 	m_delaySlider.setMajorTickSpacing(10);
	 	
	 	m_delaySlider.setMinorTickSpacing(1);
	 	
	 	m_delaySlider.setPaintTicks(true);
	 	
	 	m_delaySlider.setPaintLabels(true);
		
		init();
	}

	
	public void init() {
		
		m_Queue.getNextImage() ;
		
		
		SLMIN = PaUtils.get().getSettings().getMinSliderTimerDelay();
		
		SLMAX = PaUtils.get().getSettings().getMaxSliderTimerDelay();
		
		SLINIT = PaUtils.get().getSettings().getMCurrentSliderTimerDelay();
		
		m_pauseBut.setEnabled(false);
		
		m_startBut.setEnabled(true);
	
	}
	
	/**
	 * <p>Creates main UI components</p>
	 */
	private void createUI() {
		
		
		setLayout(new BorderLayout()); 
			
		m_visualPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		
		m_visualPanel.setBackground(Color.WHITE);
		
		m_complexPanel = PaGuiTools.createVerticalPanel();
		
		m_infoPanel = PaGuiTools.createHorizontalPanel();
		
		m_infoLabel = new JLabel("infoLabel");
		
		m_infoLabel.setFont(new Font(Font.DIALOG, Font.PLAIN , 24));
		
		m_infoPanel.add(m_infoLabel);
		
		m_buttonPanel = new JPanel (new FlowLayout());
		
		m_complexPanel.add(m_infoPanel);
		
		m_complexPanel.add(m_buttonPanel);
		
		
		m_bookBut =  new  JToggleButton(m_bookIconBw);
		
		m_bookBut.setToolTipText(getGuiStrs("bookButtonSliderToolTip"));
		
		m_sliderShowBut =  new  JToggleButton(m_sliderYesIcon);
		
		m_sliderShowBut.setToolTipText(getGuiStrs("sliderOnOffButSliderToolTip"));
		
		m_backtoStartBut =  new  JButton(getGuiStrs("toFirstButtonCaption"), new ImageIcon(PaUtils.get().getIconsPath() + "pabackfirst.png"));
		
		m_pauseBut = new  JButton(getGuiStrs("stopButtonCaption"), new ImageIcon(PaUtils.get().getIconsPath() + "pastop.png"));
		
		m_forwardBut =  new  JButton(getGuiStrs("forwardButtonCaption"), new ImageIcon(PaUtils.get().getIconsPath() + "paforward.png"));
		
		m_backBut =  new  JButton(getGuiStrs("prevButtonCaption"), new ImageIcon(PaUtils.get().getIconsPath() + "paback.png"));
		
		m_startBut =  new  JButton(getGuiStrs("startButtonCaption"), new ImageIcon(PaUtils.get().getIconsPath() + "pastart.png"));
		
		m_infoCheckBox  = new JCheckBox(getGuiStrs("sliderInfoCheckBox"));
		
		m_infoCheckBox.setSelected(true);
	
		setListeners();
		
		m_buttonPanel.add(m_bookBut);
		
		m_buttonPanel.add(m_sliderShowBut);
		
		m_buttonPanel.add(m_backtoStartBut);

		m_buttonPanel.add(m_startBut);
		
		m_buttonPanel.add(m_pauseBut);
		
		m_buttonPanel.add(m_forwardBut);
		
		m_buttonPanel.add(m_backBut);
		
		JLabel delayLabel = new JLabel(getGuiStrs("delayLabelName"));
		
		m_buttonPanel.add( delayLabel);
		
		m_buttonPanel.add( m_delaySlider);
		
		m_buttonPanel.add( m_infoCheckBox);
		
		 
		m_imageLabel = new JLabel();
			
		m_visualPanel.add( m_imageLabel);
		 
		m_scrollPane = new JScrollPane(m_visualPanel);
		 
		add (m_scrollPane, BorderLayout.CENTER);
		
		add( m_complexPanel,BorderLayout.SOUTH);
		
		m_height =  PaDesktopPane.getDefaultHeight() - 50;
		
        m_width = PaDesktopPane.getDefaultWidth() - 50;
		
		setBounds(32, 32,  m_width, m_height);
			
	}
	
	/**
	 * Sets all listeners
	 */
	void setListeners() {
		
		m_sliderShowBut.addActionListener(m_forwarder);
		
		m_bookBut.addActionListener(m_forwarder);
		
		m_pauseBut.addActionListener(m_forwarder);
		
		m_forwardBut.addActionListener(m_forwarder);
		
		m_backBut.addActionListener(m_forwarder);
		
		m_startBut.addActionListener(m_forwarder);
		
		m_backtoStartBut.addActionListener(m_forwarder);
		
		m_infoCheckBox.addActionListener(m_forwarder);
	}
	
 
	 class Changer implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			
			if ( e.getSource() == m_delaySlider ) {
				
				m_pauseBut.setEnabled(false);
				
				m_startBut.setEnabled(true);
					
				if ( m_task != null )  { m_task.cancel(); m_task =null; }
				
				m_period = (int) ( m_delaySlider.getValue()*1000 );
				
			}
			
		}
		 
	 
	 }

	private class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_forwardBut ) {
				
				m_Queue.getNextImage() ;
			}
			if ( e.getSource() == m_backBut ) {
				
				m_Queue.getPreviousImage();
			}
			
			
			if ( e.getSource() == m_startBut ) {
					
				m_pauseBut.setEnabled(true);
				
				m_startBut.setEnabled(false);
				
				startTimer();
			}
			 	
			if ( e.getSource() == m_pauseBut ) {
				
				m_pauseBut.setEnabled(false);
				
				m_startBut.setEnabled(true);
				
				m_task.cancel(); 
			}
			
			if ( e.getSource() == m_backtoStartBut ) {
				
				m_pauseBut.setEnabled(false);
				
				m_startBut.setEnabled(true);
				
				m_task.cancel(); 
				
				m_Queue.getFirstImage();
				
			}
			if ( e.getSource() == m_infoCheckBox ) {
				
				if(m_infoCheckBox.isSelected()) {	
					
					setInfoText(m_current);
				}
				
				m_infoPanel.setVisible(m_infoCheckBox.isSelected());
				
			}
			if ( e.getSource() == m_bookBut ) {
				
				if(m_bookBut.isSelected()) {	
					
					PaEvent ev = new PaEvent(PaEventDispatcher.SET_BOOKMARKED_EVENT);
					
					ev.setId(m_current.getId());
					
					PaEventDispatcher.get().fireCustomEvent(ev);
					
					m_bookBut.setIcon(m_bookIcon);
					
				}
				else {
					PaEvent ev = new PaEvent(PaEventDispatcher.SET_UN_BOOKMARKED_EVENT);
					
					ev.setId(m_current.getId());
					
					PaEventDispatcher.get().fireCustomEvent( ev);	
					
					m_bookBut.setIcon(m_bookIconBw);
				}				
			}
			
		if ( e.getSource() == m_sliderShowBut ) {
				
				if(m_sliderShowBut.isSelected()) {			
					
					PaEvent ev = new PaEvent(PaEventDispatcher.SET_SLIDER_HIDE_EVENT);
					
					ev.setId(m_current.getId());
					
					PaEventDispatcher.get().fireCustomEvent(ev);
					
					m_sliderShowBut.setIcon(m_sliderNoIcon);
			
					m_Queue.nextIt.remove();
					
				}
				else {
					PaEvent ev = new PaEvent(PaEventDispatcher.SET_SLIDER_SHOW_EVENT);
					
					ev.setId(m_current.getId());
					
					PaEventDispatcher.get().fireCustomEvent( ev);	
					
					m_sliderShowBut.setIcon(m_sliderYesIcon);
				}				
			}

		}
	}
	/**
	 * Sets info text for image object p
	 * @param p - image object
	 */
	void setInfoText(PaImage p) {
		
		m_infoLabel.setText(p.getDateAsString(DATE_FORMAT)+ "  " + p.getName()+ "  " + p.getComments());
		
	}
	
	private void startTimer() {
		
		if ( m_task != null ) m_task.cancel(); 
		
		m_task= new PaTimerTask();
		
		m_timer.schedule(m_task, 0, m_period) ;
	}
	/**
	 * <p>Class provides the navigation functionality: previous, next, fist, last image</p>
	 * @author avd
	 *
	 */
	private class PaImageQueue {
			
		ArrayList<PaImage> list = null;
		
		ListIterator<PaImage> nextIt = null;
		
		 public PaImageQueue(ArrayList<PaImage> l) {
			 
			// list = cont.get_images();
			 list = l;
			 nextIt = list.listIterator();
	
		 }
		 /**
		  * Gets next image in queue
		  */
		 public  void getNextImage() {
			 
			
			if ( ! nextIt.hasNext() ) {
				 return;
			}
			
			PaImage p =  nextIt.next();
			 
			 if(!p.isVisibleInSlider()) {
				 
				 getNextImage();
			 }
			 else  {
			
				 changeImage(p);
			 }
	
			
		 }
		 /**
		  * Gets previous image in queue; if the next image is invisible in slider, then go to the next previous image
		  */
		 public void getPreviousImage() {
			 
			 if ( ! nextIt.hasPrevious() ) return;
			 
			 nextIt.previous();
			 
			 if ( nextIt.hasPrevious() ) nextIt.previous();
			 
			 PaImage p =  nextIt.next();
			 
			 if(!p.isVisibleInSlider()) {
				 
				 getPreviousImage();
			 }
			 else {
				 changeImage(p);
			 }
			 
		 }
		 
		 /**
		  * Gets first image in queue; if the first image is invisible in slider, then go to the next image
		  */
		 public void getFirstImage() {
			  
			 while(nextIt.hasPrevious()) nextIt.previous();
			 
			 getNextImage();
			 
		 }
		 /**
		  * Changes the current image in the slider
		  * @param p - image object
		  */
		 public  void changeImage( PaImage p) {
			 
			 int height =   m_height - 100;
			 
             int width = m_width;
			
			 
			 BufferedImage srcImg;
			 
				try {
					
					srcImg = ImageIO.read(new File(p.getFullPath()));
					
				} catch (IOException e) {
					
					writeLog("IOException :"+NEXT_ROW,e, true, false, true) ;
					
					srcImg = new BufferedImage(width, height , BufferedImage.TYPE_INT_RGB);
				}
			 
			 float aspectRatio =  (float) srcImg.getWidth() / (float) srcImg.getHeight();
			 
			 if ( aspectRatio > 1.0 ) {
				 
				 int h = (int) ( ( (float) width ) / aspectRatio );
				 
				 if ( h > height ) {
					 
					 width = (int) ( ( (float) height ) * aspectRatio );
				 }
			 }
			 else {
				 
				 width = (int) ( ( (float) height ) * aspectRatio );
			 }
			 
			 Image image =  PaAlgoTransform.getScaledImage (srcImg, width, height);
			 
			 m_imageLabel.setIcon( new ImageIcon(image) );
			 
			 m_bookBut.setSelected(p.isBookmarked());
			 
			 if(p.isBookmarked()) {
				 
				 m_bookBut.setIcon(m_bookIcon);
				 
			 } else {
				 
				 m_bookBut.setIcon(m_bookIconBw);
			 }
			 
			 m_sliderShowBut.setSelected(!p.isVisibleInSlider());
			 
			 if(!p.isVisibleInSlider()) {
				 
				 m_sliderShowBut.setIcon(m_sliderNoIcon);
				 
			 } else {
				 
				 m_sliderShowBut.setIcon(m_sliderYesIcon);
			 }
			 
			 if(m_infoCheckBox.isSelected()) {
				 
				 setInfoText(p);
			 }
			 
			 m_current = p;
			 
			 m_visualPanel.repaint();
		 }
		
	}
	
	/**
	 * <p>This timer task makes images to be changed in the period of time</p>
	 * @author avd
	 *
	 */
	private class PaTimerTask extends TimerTask {

		@Override
		public void run() {
			
			m_Queue.getNextImage() ;
		}
			
	}

}
