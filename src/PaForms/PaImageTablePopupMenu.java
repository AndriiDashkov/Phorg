package PaForms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import PaActions.PaActionsMngr;
import PaActions.PaPhotoActionEnsureVisible;
import PaActions.PaPhotoFormActionFind;


/**
 * @author Andrey Dashkov
 */
public class PaImageTablePopupMenu {

	private JPopupMenu m_menu;

	public PaImageTablePopupMenu ( PaImageTable panel) {

		m_menu = new JPopupMenu();

		m_menu.add(new PaPhotoActionEnsureVisible(panel) );
				
		m_menu.add(new PaPhotoFormActionFind(panel) );
		
		m_menu.addSeparator();
		
		m_menu.add(PaActionsMngr.get().getAction("paBookmarsReset"));
		
		m_menu.add(PaActionsMngr.get().getAction("paSliderMarksReset"));

	}

	public void addMouseAdapter (JComponent compounent) {
		
		compounent.addMouseListener(new PopupMouseAdapter());
	}
	
	public class PopupMouseAdapter extends MouseAdapter {

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