package paglobal;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import paevents.PaEventAlbumResizePanel;
import paevents.PaEventDispatcher;
import paforms.PaAlbumsTreeForm;
import paforms.PaImageTable;
import paforms.PaSubjectsForm;
import paimage.PaViewPanel;
/**
 * 
 * @author Andrii Dashkov
 *
 */
public class PaMainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.RESIZE_ALBOM_PANEL_EVENT, this, "resizeDesktopPaneComponents");
	}
	
	private JSplitPane splitMain;		// main split panel

	private JSplitPane firstSplit;		// 1st panel included into main split panel
	
	private JSplitPane secondSplit;		// 2nd panel included into main split panel
	
	
	/**
	 * Main albums form with albums tree
	 */
	private PaAlbumsTreeForm m_albumsTree;
	
	/**
	 * Images table; the list of album's images in the form of table
	 */
	private PaImageTable m_imagesTable;
	
	/**
	 * Main list of subjects in the visual form of list
	 */
	private PaSubjectsForm m_subjectsTable;
	
	/**
	 * Main view panel
	 */
	private PaViewPanel m_viewPanel;
	
	
	public PaMainPanel (PaAlbumsTreeForm alForm, PaImageTable phForm, PaSubjectsForm teForm, PaViewPanel viPanel) {
				
		m_albumsTree = alForm;
		
		m_imagesTable = phForm;
		
		m_subjectsTable = teForm;
		
		m_viewPanel = viPanel;
		
		
		m_albumsTree.setVisible(PaUtils.get().getSettings().is_viVisible());
		
		m_imagesTable.setVisible( PaUtils.get().getSettings().is_phVisible());
		
		m_subjectsTable.setVisible(PaUtils.get().getSettings().isSubjectsVisible());
		
		m_viewPanel.setVisible(PaUtils.get().getSettings().is_alVisible());
		
		secondSplit = new JSplitPane();
		
		secondSplit.setOneTouchExpandable(true);
		
		secondSplit.setDividerSize(2);		
		
		secondSplit.setContinuousLayout(true);
		
		firstSplit = new JSplitPane();
		
		firstSplit.setOneTouchExpandable(true);	
		
		firstSplit.setDividerSize(2);		
		
		firstSplit.setContinuousLayout(true);

		splitMain = new JSplitPane();
		
		splitMain.setDividerSize(2);	
		
		splitMain.setOneTouchExpandable(true);	
		
		splitMain.setContinuousLayout(true);
	}
	
	public JSplitPane getSplitMain() {
		
		return splitMain;
	}

	
	public void setSplitMainOrientation(int flag) {
		
		if (flag == 1) {
			
			secondSplit.remove(m_imagesTable);
			
			secondSplit.remove(m_subjectsTable);
			
			firstSplit.remove(secondSplit);
			
			splitMain.remove(firstSplit);
			
			secondSplit.setVisible(false);
			
			firstSplit.setVisible(false);
			
			Dimension dim_11 = new Dimension(PaDesktopPane.getDefaultWidth()/5, PaDesktopPane.getDefaultHeight());
			
			Dimension dim_12 = new Dimension(PaDesktopPane.getDefaultWidth()/5*4-2, PaDesktopPane.getDefaultHeight());
					
			m_albumsTree.setPreferredSize(dim_11);
			
			m_imagesTable.setPreferredSize(dim_11);
			
			m_subjectsTable.setPreferredSize(dim_11);
			
			m_viewPanel.setPreferredSize(dim_12);
			
			secondSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			secondSplit.setTopComponent(m_imagesTable);
			
			secondSplit.setBottomComponent(m_subjectsTable);
			
			if (m_imagesTable.isVisible() == true || m_subjectsTable.isVisible()) {

				secondSplit.setVisible(true);
			}
			
			firstSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			firstSplit.setTopComponent(m_albumsTree);
			
			firstSplit.setBottomComponent(secondSplit);
			
			if (m_albumsTree.isVisible() == true || secondSplit.isVisible() == true) {

				firstSplit.setVisible(true);
			}

			splitMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			splitMain.setRightComponent(firstSplit);
			
			splitMain.setLeftComponent(m_viewPanel);
			
			splitMain.setResizeWeight(0.9);
			
			firstSplit.setResizeWeight(0.33);
			
			secondSplit.setResizeWeight(0.5);
			
		} else if (flag == 0) {

			secondSplit.remove(m_imagesTable);
			
			secondSplit.remove(m_subjectsTable);
			
			firstSplit.remove(secondSplit);
			
			splitMain.remove(firstSplit);
			
			secondSplit.setVisible(false);
			
			firstSplit.setVisible(false);
			
			Dimension dim_01 = new Dimension(PaDesktopPane.getDefaultWidth(), PaDesktopPane.getDefaultHeight()/3);
			
			Dimension dim_02 = new Dimension(PaDesktopPane.getDefaultWidth(), PaDesktopPane.getDefaultHeight()/3*2-2);

			m_albumsTree.setPreferredSize(dim_01);
			
			m_imagesTable.setPreferredSize(dim_01);
			
			m_subjectsTable.setPreferredSize(dim_01);
			
			m_viewPanel.setPreferredSize(dim_02);
		
			secondSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			secondSplit.setLeftComponent(m_imagesTable);
			
			secondSplit.setRightComponent(m_subjectsTable);
			
			if (m_imagesTable.isVisible() == true || m_subjectsTable.isVisible() == true) {
				
				secondSplit.setVisible(true);
			}

			
			firstSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			firstSplit.setLeftComponent(m_albumsTree);
			
			firstSplit.setRightComponent(secondSplit);
			
			if (m_albumsTree.isVisible() == true || secondSplit.isVisible() == true) {

				firstSplit.setVisible(true);
			}
			
			splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			splitMain.setTopComponent(m_viewPanel);
			
			splitMain.setBottomComponent(firstSplit);
			
			splitMain.setResizeWeight(0.75);	
			
			firstSplit.setResizeWeight(0.33);
			
			secondSplit.setResizeWeight(0.5);
			
		}  else if (flag == 2) {

			secondSplit.remove(m_imagesTable);
			
			secondSplit.remove(m_subjectsTable);
			
			firstSplit.remove(secondSplit);
			
			splitMain.remove(firstSplit);
			
			secondSplit.setVisible(false);
			
			firstSplit.setVisible(false);
			
			Dimension dim = new Dimension(PaDesktopPane.getDefaultWidth()/8, PaDesktopPane.getDefaultHeight());
			
			Dimension dim_1 = new Dimension(PaDesktopPane.getDefaultWidth()/6*5-4, PaDesktopPane.getDefaultHeight());

			m_albumsTree.setPreferredSize(dim);
			
			m_imagesTable.setPreferredSize(dim);
			
			m_subjectsTable.setPreferredSize(dim);
			
			m_viewPanel.setPreferredSize(dim_1);
			
			secondSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			secondSplit.setLeftComponent(m_imagesTable);
			
			secondSplit.setRightComponent(m_subjectsTable);
			
			if (m_imagesTable.isVisible() == true || m_subjectsTable.isVisible() == true) {

				secondSplit.setVisible(true);
			}
			
			firstSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			firstSplit.setLeftComponent(m_viewPanel);
			
			firstSplit.setRightComponent(secondSplit);
			
			if (m_viewPanel.isVisible() == true || secondSplit.isVisible() == true) {
			
				firstSplit.setVisible(true);
			}
			
			splitMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			splitMain.setTopComponent(m_albumsTree);			
			
			splitMain.setBottomComponent(firstSplit);
			
			splitMain.setResizeWeight(0.125);	
			
			firstSplit.setResizeWeight(0.9);
			
			secondSplit.setResizeWeight(0.5);	
			
		}
		
		secondSplit.resetToPreferredSizes();
		
		firstSplit.resetToPreferredSizes();
		
		splitMain.resetToPreferredSizes();
	}
	
	public void resizeDesktopPaneComponents (PaEventAlbumResizePanel eventResize) {
		
		int response = eventResize.get_paButton().get_flag();
		
		if (m_albumsTree.getButtonGroup()==eventResize.get_buttonGrop()) {
			
			switch (response) {	
			
				case 0: {
					
					PaUtils.get().getSettings().set_alVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 1: {
					PaUtils.get().getSettings().setSubjectsVisible(false);
					
					PaUtils.get().getSettings().set_phVisible(false);
					
					PaUtils.get().getSettings().set_viVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 2: {
					
					PaUtils.get().getSettings().setSubjectsVisible(true);
					
					PaUtils.get().getSettings().set_phVisible(true);
					
					PaUtils.get().getSettings().set_viVisible(true);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
			}
		}
		
		if (m_imagesTable.get_ButtonGroup() == eventResize.get_buttonGrop()) {
			
			switch (response) {		
			
				case 0: {
					
					PaUtils.get().getSettings().set_phVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 1: {
					
					PaUtils.get().getSettings().set_alVisible(false);
					
					PaUtils.get().getSettings().setSubjectsVisible(false);
					
					PaUtils.get().getSettings().set_viVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 2: {
					
					PaUtils.get().getSettings().set_alVisible(true);
					
					PaUtils.get().getSettings().setSubjectsVisible(true);
					
					PaUtils.get().getSettings().set_viVisible(true);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
			}
		}
		
		if (m_subjectsTable.get_ButtonGroup() == eventResize.get_buttonGrop()) {
			
			switch (response) {		
			
				case 0: {
					
					PaUtils.get().getSettings().setSubjectsVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 1: {
					PaUtils.get().getSettings().set_alVisible(false);
					
					PaUtils.get().getSettings().set_phVisible(false);
					
					PaUtils.get().getSettings().set_viVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 2: {
					
					PaUtils.get().getSettings().set_alVisible(true);
					
					PaUtils.get().getSettings().set_phVisible(true);
					
					PaUtils.get().getSettings().set_viVisible(true);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
			}
			
		}

		if (m_viewPanel.get_ButtonGroup() == eventResize.get_buttonGrop()) {
			
			switch (response) {		
			
				case 0: {
					
					PaUtils.get().getSettings().set_viVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 1: {
					
					PaUtils.get().getSettings().set_alVisible(false);
					
					PaUtils.get().getSettings().set_phVisible(false);
					
					PaUtils.get().getSettings().setSubjectsVisible(false);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
				case 2: {
					
					PaUtils.get().getSettings().set_alVisible(true);
					
					PaUtils.get().getSettings().set_phVisible(true);
					
					PaUtils.get().getSettings().setSubjectsVisible(true);
					
					setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
					
					break;
				}
			}
			
		}
	}

}
