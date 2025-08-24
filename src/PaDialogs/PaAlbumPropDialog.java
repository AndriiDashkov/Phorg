/**
 * 
 */
package padialogs;

import static paglobal.PaUtils.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;

import pacollection.PaAlbum;
import pacollection.PaImageContainer;
import paglobal.PaGuiTools;
import paglobal.PaUtils;
import palong.PaAlbumImageControl;

/**
 * @author Andrii Dashkov
 *
 */
public class PaAlbumPropDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PaAlbum m_album;
	
	private int m_childs;
	
	private int m_images;
	
	/**
	 * number of images in standard folder 
	 */
	private int m_imagesInStand;
	
	private int m_imagesBooked;
	
	private int m_imagesPrinted;
	
	Set<String> m_subList;
	
	ImageIcon m_icon;
	
	JScrollPane m_scrollAList;
	
	JButton m_anlsButton;
	
	JList<String>  m_subjectView;

	/**
	 * @param mainF - parent frame for this dialog
	 * @param al - album object to show its properties
	 * @param childs - children (albums) number of album al
	 * @param images - number of images in the album
	 * @param subList - the list of subjects which are used in the album al
	 * @param pathToIcon - path to the album al icon
	 */

	public PaAlbumPropDialog(JFrame mainF,PaAlbum al, int childs, Set<String> subList)
	{
		super (mainF, getGuiStrs("albomCaptionPropDialog"), true); 

		m_album = al;
		
		m_childs = childs;
		
		PaImageContainer cont =  PaUtils.get().getMainContainer().getContainer(al.getId()); 
		
		m_images = cont.getSize();
		
		m_imagesInStand =  cont.getStandNumber();
		
		int[] ar = cont.getPrintedPhotos();
		
		m_imagesBooked = ar[1];
		
		m_imagesPrinted = ar[0];
		
		m_subList = subList;
		
		m_icon = m_album.getIcon();
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
		add(createGUI());
		
		setBounds(250, 150, 200, 220);

		pack();
		//setResizable(false);
	}
	
	/**
	 * @return main panel of the dialog
	 *
	 */
	private JPanel createGUI () {
		
		Font f = PaUtils.get().getBaseFont();
		
		setFont(f);
		
		JPanel mainPanel = PaGuiTools.createHorizontalPanel();

		mainPanel.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,VERT_STRUT,
				VERT_STRUT));
		
	
		JPanel gridPanel = PaGuiTools.createVerticalPanel();

		JLabel nameAlbumLabel = new JLabel(getGuiStrs("nameAlbomPropDialog")+  "  "); 
		
		JLabel nameAlbum = new JLabel(m_album.getName());
		
		nameAlbumLabel.setFont(f);
		
		nameAlbum.setFont(f);
		
		JPanel panel1 = PaGuiTools.createHorizontalPanel();
		
		panel1.add(nameAlbumLabel);  panel1.add(nameAlbum); panel1.add(Box.createHorizontalGlue());
		
		JLabel dateAlbumLabel = new JLabel(getGuiStrs("dateAlbomPropDialog")+  "  ");
		
		JLabel dateAlbom = new JLabel(dateToString(m_album.getDate(),GUI_DATE_FORMAT)); 
		
		dateAlbumLabel.setFont(f);
		
		dateAlbom.setFont(f);
		
		JPanel panel2 = PaGuiTools.createHorizontalPanel();
		
		panel2.add(dateAlbumLabel);  panel2.add(dateAlbom); panel2.add(Box.createHorizontalGlue());
		
		
		JLabel childsLabel = new JLabel(getGuiStrs("childsCountAlbomPropDialog")+  "  ");
		
		JLabel childs = new JLabel(Integer.toString(m_childs)); 
		
		childsLabel.setFont(f);
		
		childs.setFont(f);
		
		JPanel panel3 = PaGuiTools.createHorizontalPanel();
		
		panel3.add(childsLabel);  panel3.add(childs); panel3.add(Box.createHorizontalGlue());
		
		JLabel numberOfImagesLabel = new JLabel(getGuiStrs("imagesNumAlbomPropDialog")+  "  ");
		
		JLabel numberOfImages = new JLabel(Integer.toString(m_images)); 
		
		numberOfImagesLabel.setFont(f);
		
		numberOfImages.setFont(f);
		
		JPanel panel4 = PaGuiTools.createHorizontalPanel();
		
		panel4.add(numberOfImagesLabel);  panel4.add(numberOfImages); panel4.add(Box.createHorizontalGlue());
		
		JLabel numberOfStandLabel = new JLabel(getGuiStrs("imagesNumStandAlbomPropDialog")+  "  ");
		
		JLabel numberOfStand = new JLabel(Integer.toString(m_imagesInStand)); 
		
		numberOfStandLabel.setFont(f);
		
		numberOfStand.setFont(f);
		
		JPanel panel5 = PaGuiTools.createHorizontalPanel();
		
		panel5.add(numberOfStandLabel);  panel5.add(numberOfStand); panel5.add(Box.createHorizontalGlue());
		
		JLabel pathLabel = new JLabel(getGuiStrs("albomPathPropDialog") +  "  "); 
		
		JLabel path = new JLabel(m_album.getFullStandardPath());
		
		pathLabel.setFont(f);
		
		path.setFont(f);
		
		JPanel panel6 = PaGuiTools.createHorizontalPanel();
		
		panel6.add(pathLabel);  panel6.add(path); panel6.add(Box.createHorizontalGlue());
		
		JLabel numberOfBookmarkLabel = new JLabel(getGuiStrs("bookNumAlbomPropDialog")+  "  ");
		
		JLabel numberOfBookmark = new JLabel(Integer.toString(m_imagesBooked)); 
		
		numberOfBookmarkLabel.setFont(f);
		
		numberOfBookmark.setFont(f);
		
		JPanel panel7 = PaGuiTools.createHorizontalPanel();
		
		panel7.add(numberOfBookmarkLabel);  panel7.add(numberOfBookmark); panel7.add(Box.createHorizontalGlue());
		
		JLabel numberOfPrintLabel = new JLabel(getGuiStrs("printedNumAlbomPropDialog") + "  ");
		
		JLabel numberOfPrint = new JLabel(Integer.toString(m_imagesPrinted)); 
		
		numberOfPrintLabel.setFont(f);
		
		numberOfPrint.setFont(f);
		
		JPanel panel8 = PaGuiTools.createHorizontalPanel();
		
		panel8.add(numberOfPrintLabel);  panel8.add(numberOfPrint); panel8.add(Box.createHorizontalGlue());
		
		JLabel commentLabel = new JLabel(getGuiStrs("commentsAlbomPropDialog")+ "  "); 
		
		JLabel comment = new JLabel(m_album.getComment());
		
		commentLabel.setFont(f);
		
		comment.setFont(f);
		
		JPanel panel9 = PaGuiTools.createHorizontalPanel();
		
		panel9.add(commentLabel);  panel9.add(comment); panel9.add(Box.createHorizontalGlue());
		
		JLabel usedSubjectsLabel = new JLabel(getGuiStrs("usedSubAlbomPropDialog"));
		
		PaUtils.get().setFixedSizeBehavior(usedSubjectsLabel);
		
		usedSubjectsLabel.setFont(f);
		
		JPanel panel10 = PaGuiTools.createHorizontalPanel();
		
		panel10.add(usedSubjectsLabel);  panel10.add(Box.createHorizontalGlue());
		
		 PaGuiTools.makeSameSize(new JComponent[] { nameAlbumLabel,childsLabel ,numberOfImagesLabel,
				 numberOfStandLabel, pathLabel, numberOfBookmarkLabel, numberOfPrintLabel, commentLabel,
				 usedSubjectsLabel, dateAlbumLabel});
		
		gridPanel.add(panel1);
		
		gridPanel.add(panel2);
		
		gridPanel.add(panel3);
		
		gridPanel.add(panel4);
		
		gridPanel.add(panel5);
		
		gridPanel.add(panel6);
		
		gridPanel.add(panel7);
		
		gridPanel.add(panel8);
		
		gridPanel.add(panel9);
		
		gridPanel.add(panel10);
				
		JPanel vertPanel = PaGuiTools.createVerticalPanel();
		
		m_subjectView = new JList<String>(m_subList.toArray(new String[0]));
		
		m_subjectView.setFont(f);
		
		m_subjectView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrol = new JScrollPane(m_subjectView);

		scrol.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		
		scrol.setBorder(BorderFactory.createEmptyBorder());

		vertPanel.add(gridPanel);
		
		//vertPanel.add(usedSubjectsLabel);
		vertPanel.add(scrol);
		
		JPanel anlsPanel = PaGuiTools.createVerticalPanel();
		
		JPanel anlsHorPanel = PaGuiTools.createHorizontalPanel();
		
		m_anlsButton = new JButton(getGuiStrs("analisysButtonName")); 
	
		anlsHorPanel.add(m_anlsButton);
		
		anlsHorPanel.add(Box.createHorizontalGlue());
		
		m_anlsButton.addActionListener(new ButtonListener());
		
		JList<String>  m_aList = new JList<String>();
		
		m_aList.setFont(f);
		
		m_scrollAList = new JScrollPane(m_aList);

		m_scrollAList.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		
		m_scrollAList.setBorder(BorderFactory.createEmptyBorder());

		m_scrollAList.setVisible(false);
		
		anlsPanel.add(Box.createVerticalStrut(VERT_STRUT));
		
		anlsPanel.add(anlsHorPanel);
		
		anlsPanel.add(Box.createVerticalStrut(VERT_STRUT));
		anlsPanel.add(m_scrollAList);
		
		vertPanel.add(anlsPanel);
		
		vertPanel.add(Box.createVerticalGlue());
		
		JPanel imagePanel = PaGuiTools.createVerticalPanel();
		
		JLabel iconLabel = new JLabel();
		
		iconLabel.setIcon(m_icon);
		
		imagePanel.add(iconLabel);
		
		imagePanel.add(Box.createVerticalGlue());
		
		imagePanel.setBorder( BorderFactory.createEmptyBorder(VERT_STRUT,VERT_STRUT,
				VERT_STRUT,VERT_STRUT));
		
		mainPanel.add(imagePanel);
		
		mainPanel.add(vertPanel);
	
		//mainPanel.add(Box.createVerticalGlue());
		
		setTooltips();
		
		return mainPanel;
	}
	
	private void setTooltips() {
		
		m_anlsButton.setToolTipText(getGuiStrs("checkAlbomButtonTooltip"));
		
		m_subjectView.setToolTipText(getGuiStrs("subjectUsedAlbomPropTooltip"));
	}
	/**
	 * Listens the check button for deleted images
	 * @author Andrii Dashkov
	 *
	 */
	private class ButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_anlsButton ) {
				
				int n = JOptionPane.showConfirmDialog(
					    PaUtils.get().getMainWindow(),
					    getMessagesStrs("startAlbumCheckMessage") + " " + m_album.getName() + "?",
					    getMessagesStrs("messageAnswerCaption"),
					    JOptionPane.YES_NO_OPTION);
			
				if ( n == JOptionPane.YES_OPTION) {
					
					//checkAlbomOperationMessageNote
					ProgressMonitor progressMonitor = new ProgressMonitor(PaAlbumPropDialog.this,
							getMessagesStrs("checkAlbomOperationMessageNote") + " " + m_album.getName(),
							getMessagesStrs("checkAlbomOperationNote"), 0, 100);
					
					progressMonitor.setMillisToDecideToPopup(0);
					
					progressMonitor.setMillisToPopup(0);
					
					
					PaAlbumImageControl ts = new PaAlbumImageControl(progressMonitor, m_album.getId(),
							m_album.getName());
					
					ts.execute();
				}

			}
		}
	}

}
