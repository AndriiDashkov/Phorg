
package PaGlobal;

import static PaGlobal.PaUtils.getGuiStrs;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import PaActions.PaActionsMngr;



/**
 * Creates main toolbar of application
 * @author avd
 *
 */
public class PaMainToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<PaInnerToolBar> m_toolbars = new ArrayList<PaInnerToolBar>();  
	
	private PaDesktopPane m_mainPanel;
	
	private	JPopupMenu m_menu; 
	
	private PopupMouseAdapter m_mouseAdapter;
	
	private class PaInnerToolBar extends JToolBar {
		
		private static final long serialVersionUID = 1L;
		
		int m_id;
		
		@SuppressWarnings("unused")
		int m_sortId;

		
		/**
		 * @param orientation
		 * @param id - initial default number of this toobar
		 * @param sortId - the position of this toolbar after realocation by user
		 */
		public PaInnerToolBar(int orientation, int id, int sortId) {
			
			super(orientation);
			
			m_id = id;
			
			m_sortId = sortId;
		}
		
	};
	


	/**
	 * @param orientation
	 */
	public PaMainToolBar(int orientation,  PaDesktopPane mainPanel) {
		
		super(orientation);
		
		m_mainPanel = mainPanel;
		
		m_mouseAdapter = new PopupMouseAdapter();
		
		createToolbars();
		
		initToolbars();
		
		m_menu  = createPopupMenu();

		setListeners();
	}


	
	/**
	 * Creates all inner toolbars; puts in in the container
	 */
	private void createToolbars() {
		

		setFloatable(false);
		
		PaInnerToolBar toolbar0 = new PaInnerToolBar(JToolBar.HORIZONTAL,0, 0);
		
		toolbar0.add(PaActionsMngr.get().getAction("paactionsave"));
		
		m_toolbars.add(toolbar0);

		PaInnerToolBar toolbar1 = new PaInnerToolBar(JToolBar.HORIZONTAL,1, 1);
		
		toolbar1.add(PaActionsMngr.get().getAction("paactionload"));
		
		toolbar1.addSeparator();
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionnew"));
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionedit"));
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactiondel")); 
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionmove"));
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionmerge"));
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionfind"));
		
		toolbar1.add(PaActionsMngr.get().getAction("paalbomactionprop"));

		m_toolbars.add(toolbar1);
		
		PaInnerToolBar toolbar2 = new PaInnerToolBar(JToolBar.HORIZONTAL,2, 2);
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactionnew"));
		
		toolbar2.add(PaActionsMngr.get().getAction("pactionaddgroup"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactionedit"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactiondel"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactioncut"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactioncopy"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paimageactionpaste"));
		
		toolbar2.add(PaActionsMngr.get().getAction("paprintaction"));
		
		m_toolbars.add(toolbar2);
		
		PaInnerToolBar toolbar3 = new PaInnerToolBar(JToolBar.HORIZONTAL,3, 3);
		
		toolbar3.add(PaActionsMngr.get().getAction("paundoaction"));
		
		toolbar3.add(PaActionsMngr.get().getAction("paredoaction"));

		m_toolbars.add(toolbar3);
		
		PaInnerToolBar toolbar4 = new PaInnerToolBar(JToolBar.HORIZONTAL,4, 4);
		
		toolbar4.add(PaActionsMngr.get().getAction("paimageactionfilter"));
		
		toolbar4.add(PaActionsMngr.get().getAction("paimageactionfilterclear"));
		
		toolbar4.addSeparator();
		
		toolbar4.add(PaActionsMngr.get().getAction("paimageactionselect"));
		
		toolbar4.add(PaActionsMngr.get().getAction("paimageactiondeselect"));

		m_toolbars.add(toolbar4);
		
		PaInnerToolBar toolbar5 = new PaInnerToolBar(JToolBar.HORIZONTAL,5, 5);
		
		toolbar5.add(PaActionsMngr.get().getAction("paimageactionrtleft"));
		
		toolbar5.add(PaActionsMngr.get().getAction("paimageactionrtright"));
		
		toolbar5.addSeparator();
		
		toolbar5.add(PaActionsMngr.get().getAction("paslideshowaction"));
		
		toolbar5.add(PaActionsMngr.get().getAction("painstrumentsaction"));
		
		toolbar5.add(PaActionsMngr.get().getAction("paroisaction"));

		m_toolbars.add(toolbar5);
		
		PaInnerToolBar toolbar6 = new PaInnerToolBar(JToolBar.HORIZONTAL,6, 6);
		
		toolbar6.add(PaActionsMngr.get().getAction("pasubactionnew"));
		
		toolbar6.add(PaActionsMngr.get().getAction("pasunactionedit"));
		
		toolbar6.add(PaActionsMngr.get().getAction("patemactiondel"));
		
		toolbar6.add(PaActionsMngr.get().getAction("patemactionfind"));

		m_toolbars.add(toolbar6);
	
		PaInnerToolBar toolbar7 = new PaInnerToolBar(JToolBar.HORIZONTAL,7, 7);
		
		JLabel sortLabel = new JLabel(" " + getGuiStrs("sortLabelToolbarCaption") + " ");
		
		toolbar7.add(m_mainPanel.initBoxSizeCombo());
		
		toolbar7.add(m_mainPanel.initColumnNumberCombo());
		
		toolbar7.add(sortLabel);
		
		toolbar7.add(m_mainPanel.initSortCombo());
		
		m_toolbars.add(toolbar7);
		
	}
	/**
	 * Sets listeners for all toolbars and children
	 */
	private void setListeners() {
		
		addMouseListener( m_mouseAdapter);
		
		for(PaInnerToolBar t: m_toolbars) {
			
			t.addMouseListener( m_mouseAdapter);
			
			Component[] c = t.getComponents();
			
			for(Component c1: c) {
				
				c1.addMouseListener( m_mouseAdapter);
			}
		}
	}
	
	
	
	/**
	 * Initiates toolbars from container
	 */
	private void  initToolbars() {
		
		for(PaInnerToolBar t: m_toolbars) {
			
			add(t);
		}
		
		add(Box.createHorizontalGlue());
	}
	
	/**
	 * 
	 * @return the popup menu for toolbar with checkboxes which control the visibility of toolbars
	 */
	public JPopupMenu createPopupMenu() {
		
		JPopupMenu viewMenu = new JPopupMenu(  ); 

		final  JCheckBoxMenuItem tlb0 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("saveToolbarCheckBoxMenu"));
		
		tlb0.setSelected(true);
		
		final  JCheckBoxMenuItem tlb1 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("albumToolbarCheckBoxMenu"));
		
		tlb1.setSelected(true);
		
		final  JCheckBoxMenuItem tlb2 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("imageToolbarCheckBoxMenu"));
		
		tlb2.setSelected(true);
		
		final  JCheckBoxMenuItem tlb3 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("undoredoToolbarCheckBoxMenu"));
		
		tlb3.setSelected(true);
		
		final  JCheckBoxMenuItem tlb4 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("filterToolbarCheckBoxMenu"));
		
		tlb4.setSelected(true);
		
		final  JCheckBoxMenuItem tlb5 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("leftRightToolbarCheckBoxMenu"));
		
		tlb5.setSelected(true);
		
		final  JCheckBoxMenuItem tlb6 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("subjectToolbarCheckBoxMenu"));
		
		tlb6.setSelected(true);
		
		final  JCheckBoxMenuItem tlb7 = new  JCheckBoxMenuItem(PaUtils.getMenusStrs("sortToolbarCheckBoxMenu"));
		
		tlb7.setSelected(true);
		
		tlb0.addActionListener(new ActionListener() {		
			
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(0,tlb0.isSelected());}
		});
		
		tlb1.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(1,tlb1.isSelected());}
		});
		
		tlb2.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(2,tlb2.isSelected());}
		});
		
		tlb3.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(3,tlb3.isSelected());}
		});
		
		tlb4.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(4,tlb4.isSelected());}
		});
		
		tlb5.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(5,tlb5.isSelected());}
		});
		
		tlb6.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(6,tlb6.isSelected());}
		});
		
		tlb7.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent act) { changeToolbarVisibility(7,tlb7.isSelected());}
		});

		viewMenu.add(tlb0);
		
		viewMenu.add(tlb1);
		
		viewMenu.add(tlb2);
		
		viewMenu.add(tlb3);
		
		viewMenu.add(tlb4);
		
		viewMenu.add(tlb5);
		
		viewMenu.add(tlb6);
		
		viewMenu.add(tlb7);
						
		return viewMenu;
	}
	/**
	 * 
	 * @param id - id of inner toolbar
	 * @return the inner toolbar for id or null
	 */
	private PaInnerToolBar findToolbar(int id) {
		
		for(PaInnerToolBar t: m_toolbars) {	
			
			if(t.m_id == id) {
				
				return t;
			}
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param id - id of inner toolbar
	 * @param visible - true to set visible
	 */
	private void changeToolbarVisibility(int id,boolean visible) {
		
		PaInnerToolBar t = findToolbar(id);
		
		if(t != null) {
			
			t.setVisible(visible);
		}
		
	}
	/**
	 * Adapter to show toolbarpopup menu
	 * @author avd
	 *
	 */
	class PopupMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {

			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {

			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			
			if (e.isPopupTrigger()) {
				
				m_menu.show(e.getComponent(),
                   e.getX(), e.getY());
			}
		}
	}
	
}
