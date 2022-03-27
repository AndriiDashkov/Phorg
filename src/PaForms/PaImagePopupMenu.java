package PaForms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import PaActions.PaActionsMngr;
import PaEvents.PaEventDispatcher;
import PaEvents.PaEventEnable;
import PaGlobal.PaUtils;


import static PaGlobal.PaUtils.*;
/**
 * Context menu for main view panel
 * @author avd
 *
 */
public class PaImagePopupMenu {

	private JPopupMenu m_popupMenu;
	
	private JMenu m_subMenuOpenIn;

	public PaImagePopupMenu () {
		
		{
			PaEventDispatcher.get().addConnect(PaEventDispatcher.ENABLE_EVENT, this, "enableActions");
			
		}
		
		m_popupMenu = new JPopupMenu();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionnew"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("pactionaddgroup"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionedit"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactiondel"));
		
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactioncut"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactioncopy"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionpaste"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionfilter"));
			
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionselect"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactiondeselect"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paactionalbomforselection"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paactionmoveselected"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paactionmovelinkstostandselected"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimagedatesync"));
		
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionrtleft"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionrtright"));
		
		m_popupMenu.addSeparator();
		
		m_subMenuOpenIn = PaUtils.get().createExternalEditorMenu();
		
		m_popupMenu.add(m_subMenuOpenIn);
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactioncopyin"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paactionfolder"));
		
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(createIconsMenu());
		
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("paimageactionproperties"));
	}
	
	
	  private JMenu createIconsMenu() {
		  
		  JMenu submenu = new JMenu(getMenusStrs("iconsMenuName"));
		 
		  submenu.add( PaActionsMngr.get().getAction("paimageactionicons") );
		  
		  submenu.add( PaActionsMngr.get().getAction("paimagerefreshboosticon") );
		  
		  submenu.addSeparator();
		  
		  submenu.add( PaActionsMngr.get().getAction("paimageactionrefreshicons") );
		  		
		  return submenu;
	  }
	
	
	public void addMouseAdapter (JComponent compounent) {
		
		compounent.addMouseListener(new PopupMouseAdapter());
	}
	
	class PopupMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {

			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {

			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			
			if (e.isPopupTrigger()) {
				
				m_popupMenu.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	}
	
	/**
	 * Enable or disables actions and submenus
	 * @param e enable event
	 */
	public void enableActions(PaEventEnable e) 
	{	
		switch(e.getType()) {
		
			case NO_SELECTED_IMAGES:
				
			case MULTISELECTED_IMAGES :
				
			case IMAGE_LIST_IS_EMPTY :
				
			case SELECT_CONTAINER_IS_EMPTY : {  m_subMenuOpenIn.setEnabled(false); break; }
			
			case  SINLGE_IMAGE_SELECTED : {  m_subMenuOpenIn.setEnabled(true); break; }
			
			default : 
	
		}
	}
}
