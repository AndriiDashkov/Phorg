package paforms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import paactions.PaActionsMngr;
/**
 * 
 * @author Andrii Dashkov
 *<p>Popup menu for subjects table  </p>
 */
public class PaSubjectsPopupMenu {

	private JPopupMenu m_popupMenu;
	
	public PaSubjectsPopupMenu () {

		m_popupMenu = new JPopupMenu();
		m_popupMenu.add(PaActionsMngr.get().getAction("pasubactionnew"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("pasunactionedit"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("patemactiondel"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("patemactionfind"));
		
		m_popupMenu.addSeparator();
		
		m_popupMenu.add(PaActionsMngr.get().getAction("pasubselectedinsert"));
		
		m_popupMenu.add(PaActionsMngr.get().getAction("pasubselectedremove"));
	}
	
	public void add_mouseAdapter (JComponent compounent) {
		
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
}
