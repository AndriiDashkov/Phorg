package paforms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import paactions.PaActionsMngr;

/**
 * @author Andrii Dashkov
 *
 */
public class PaAlbumPopupMenu {

	private JPopupMenu m_menu;

	public PaAlbumPopupMenu () {

		m_menu = new JPopupMenu();
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionnew"));
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionedit"));
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactiondel"));
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionmove"));
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionmerge"));
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionfind"));
		
		m_menu.add(PaActionsMngr.get().getAction("paactionload"));
		
		m_menu.addSeparator();
		
		m_menu.add(PaActionsMngr.get().getAction("paactionmovelinkstostand"));
		
		m_menu.addSeparator();
		
		m_menu.add(PaActionsMngr.get().getAction("paalbomactionprop"));
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
				
				m_menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
