package paglobal;

import static paglobal.PaLog.*;
import static paglobal.PaUtils.NEXT_ROW;
import static paglobal.PaUtils.getMenusStrs;
import static paglobal.PaUtils.getMessagesStrs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import paactions.PaActionsMngr;
import paactions.PaSortAction;
import paactions.PaSortMenuAction;
import padialogs.PaAboutDialog;
import padialogs.PaLicenseDialog;
import paevents.PaEvent;
import paevents.PaEventAlbumResizePanel;
import paevents.PaEventDispatcher;
import paevents.PaEventEnable;
import paevents.PaEventInt;
import paforms.PaAlbumsTreeForm;
import paforms.PaHelpBrowser;
import paforms.PaImageTable;
import paforms.PaSortComboBox;
import paforms.PaSubjectsForm;
import paimage.PaViewPanel;
import paundoredo.PaUndoRedoDeque;


/**
 *  The main class to start the application. Creates inside all inner containers, toolbars, menus, view panel
 * @author Andrii Dashkov
 *
 */
public class PaDesktopPane extends JFrame {
	
	{
		PaEventDispatcher.get().addConnect(PaEventDispatcher.RESIZE_ALBOM_PANEL_EVENT, this, "resizeMainPanel");
	}
	
	private static final long serialVersionUID = 1L;

	private static String icon_path;
	
	static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	
	static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	private JDesktopPane desktopPane;
	
	private PaMainPanel mainPanel;
	
	private JCheckBoxMenuItem albSet;
	
	private JCheckBoxMenuItem viePanel;
	
	private JCheckBoxMenuItem subtSet;
	
	private JCheckBoxMenuItem phoSet;
	
	private PaAlbumsTreeForm albomsForm;
	
	private PaViewPanel viewPanel;
	
	private PaImageTable photosForm;
	
	private PaSubjectsForm temsForm;
	
	private JComboBox<Integer> m_boxSizePhoto;
	
	private JComboBox<Integer> m_boxNumberPhotoColumn;
	
	private PaSortComboBox m_sortCombo; 
	
	public PaDesktopPane () {
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	
		PaUtils.get().setMainWindow(this);
		
		PaUtils.get().initVariable();
		
		initUser();
		
		icon_path = PaUtils.get().getIconsPath();		
		
		try {
			
			PaUtils.get().initContainers ();
			
		} catch (IOException e) {
			
			writeLog("IOException : can't init application " + NEXT_ROW, e, true, false, true);
			
			System.exit(1);
		}
		
		//singletone initiation (the order is essential)
		PaEventDispatcher.get(); //event dispatcher
		
		PaActionsMngr.get().initActions(); //actions manager + init actions
		
		PaUtils.get().initForms(); //utils form initiation
		
		PaUndoRedoDeque.get(); //undo/redo deque
		
		PaLog.get();
		
		albSet = new JCheckBoxMenuItem(PaUtils.getMenusStrs("albomListMenuName"));
		
		viePanel = new JCheckBoxMenuItem(PaUtils.getMenusStrs("mainPanelMenuName"));
		
		subtSet = new JCheckBoxMenuItem(PaUtils.getMenusStrs("subjectsListMenuName"));
		
		phoSet = new JCheckBoxMenuItem(PaUtils.getMenusStrs("imagesListMenuName"));
		
		albomsForm = PaUtils.get().get_albomsForm();
		
		temsForm = PaUtils.get().getSubjectsForm();
		
		photosForm = PaUtils.get().get_photosForm();
		
		viewPanel = PaUtils.get().getViewPanel();

		setTitle("Photo organizer");
		
		setIconImage((new ImageIcon(icon_path + "paappimage.png")).getImage());

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		desktopPane = new JDesktopPane();
		
		desktopPane.setLayout(new BorderLayout()); 
		
		mainPanel = new PaMainPanel(albomsForm, photosForm, temsForm, viewPanel);
		
		mainPanel.setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());

		desktopPane.add(mainPanel.getSplitMain());
		
		add(desktopPane, BorderLayout.CENTER);
		
		add(createMainToolbar(), BorderLayout.NORTH);
		
		add(createToolbarText(), BorderLayout.PAGE_END);

		JMenuBar menuBar = new JMenuBar(); 	
		
		setJMenuBar(menuBar);	
		
		menuBar.add(createMenuAlboms());
		
		menuBar.add(createMenuImage());
		
		menuBar.add(createMenuView());
		
		menuBar.add(createMenuSubjects());
		
		menuBar.add(createMenuDiff());
		
		menuBar.add(createMenuWindows());
		
		menuBar.add(createMenuHelp()); 
		
		menuBar.add(Box.createHorizontalGlue());
		
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				//to give a last chance to save something
				PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT));
				
				if (PaActionsMngr.get().getAction("paactionsave").isEnabled() == true) {
					
					int response = JOptionPane.showConfirmDialog(null, PaUtils.getMessagesStrs("saveChangesQuestion"), 
							PaUtils.getMessagesStrs("programExitDialogCaption"), JOptionPane.YES_NO_CANCEL_OPTION);
					
					switch (response) {			
					
						case JOptionPane.YES_OPTION:
						
							PaEventDispatcher.get().fireCustomEvent( new PaEvent(PaEventDispatcher.SAVE_EVENT));
							
							dispose();
							
							System.exit(0);
				
						case JOptionPane.NO_OPTION:
							
							dispose();
							
							System.exit(0);
							
						case JOptionPane.CLOSED_OPTION:
							
							System.out.print("CLOSED");
							
							break;
					}
				} else {
					
					dispose();
					
					System.exit(0);	
					
				}
			}
		});		
	}
	
	
	public void initUser() {
		
		PaUtils.get().setWorkDir(System.getProperty("user.dir"));
		
		PaUtils.get().setHomeDir(System.getProperty("user.home"));
		
	}
	
	public static int getDefaultWidth() {
		
		return DEFAULT_WIDTH;
	}

	public static int getDefaultHeight() {
		
		return DEFAULT_HEIGHT;
	}
	
	/**
	 * 
	 * @return the main application's toolbar
	 */
	public JToolBar createMainToolbar () {
		
		return new PaMainToolBar(JToolBar.HORIZONTAL, this);
	}

	/**
	 * Initiates the sorting combobox which is on the main toolbar
	 */
	public PaSortComboBox initSortCombo() {
		
		m_sortCombo = new PaSortComboBox();	

		int index = PaUtils.get().getSettings().getInitialSortingIndex();
		
		m_sortCombo.setSelectedIndex(index);
		
		//the starting sorting index is loaded from settings
		
		m_sortCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				 if ( PaUtils.get().getMainContainer().getCurrentContainer() == null) { 
					 
						writeLog( getMessagesStrs("noSortForEmptyContainer"), null, true, true, true);
						return; 
				}
				
				 PaSortAction act = new PaSortAction(m_sortCombo.getSelectedIndex());
				 
				 act.actionPerformed(e);
				 
						 
				 if(m_sortCombo.getSelectedIndex() == PaSortOrderEnum.toInt(PaSortOrderEnum.CUSTOM_ORDER)) {
					 
					 writeInfoOnly(PaUtils.getMessagesStrs("dragAndDropIsAvailable"));
				 }
				 else { writeInfoOnly(""); }
			}
		});
		
		return m_sortCombo; 
	}
	/**
	 * Initiates the combobox which controls the visual image panels size in the main view panel
	 */
	public JComboBox<Integer>  initBoxSizeCombo() {
		
		m_boxSizePhoto = new JComboBox<Integer>( PaUtils.get().getImageSizes());	
		
		m_boxSizePhoto.setMaximumSize(new Dimension(100, 50));
		
		m_boxSizePhoto.setToolTipText(PaUtils.getGuiStrs("comboBoxImageSizesToolTip"));
	
		m_boxSizePhoto.setSelectedItem( PaUtils.get().getSettings().getPhotoScale() );
		
		m_boxSizePhoto.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				
				int sizePhoto =  (Integer) m_boxSizePhoto.getSelectedItem();
				
				PaUtils.get().getSettings().setPhotoScale(sizePhoto);			
	
				if (PaUtils.get().getMainContainer().getCurrentContainer() != null) {
			
					PaEventInt event = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
					
					PaEventDispatcher.get().fireCustomEvent(event);
				}
			}
		});
		
		return m_boxSizePhoto; 
	
	}
	/**
	 * Initiates the combobox whic controls the numer of columns in the main view panel ; this combo is on the main toolbar
	 */
	public  JComboBox<Integer> initColumnNumberCombo() {
		
		m_boxNumberPhotoColumn = new JComboBox<Integer>( PaUtils.get().getNumberImageColumns());
		
		m_boxNumberPhotoColumn.setMaximumSize(new Dimension(100, 50));
		
		m_boxNumberPhotoColumn.setToolTipText(PaUtils.getGuiStrs("comboBoxImageColumnsToolTip"));
	
		m_boxNumberPhotoColumn.setSelectedItem(PaUtils.get().getSettings().getColumnsAmount());
		
		m_boxNumberPhotoColumn.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
	
				int number = (Integer) m_boxNumberPhotoColumn.getSelectedItem();
				
				PaUtils.get().getSettings().setColumnsAmount(number);	
				
				if (PaUtils.get().getMainContainer().getCurrentContainer() != null) {

					PaEventInt event = new PaEvent(PaEventDispatcher.VIEWPANEL_REFRESH_EVENT);
					
					PaEventDispatcher.get().fireCustomEvent(event);
				}
			}
		});	
		
		return m_boxNumberPhotoColumn; 
	
	}
	
	/**
	 * 
	 * @return the bottom toolbar for info row
	 */
	public JToolBar createToolbarText () {
		
		JToolBar toolbar_text = new JToolBar();
		
		toolbar_text.setOrientation(JToolBar.HORIZONTAL);
		
		JTextField textField = PaUtils.get().getMainLabel();
		
		textField.setEditable(false);
		
		textField.setBorder(null);
		
		toolbar_text.add(textField);
		
		toolbar_text.add(Box.createHorizontalGlue());
		
		toolbar_text.add(Box.createHorizontalStrut(112));
			
		return toolbar_text;
	}
	


	/**
	 * <p>Creates menu Instruments for the main menu</p>
	 */
	public JMenu createMenuDiff() 
	{
		JMenu difMenu = new JMenu(PaUtils.getMenusStrs("diffMainMenuCaption")); 
		
		difMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDiffMainMenu").charAt(0)));
		
		JMenuItem itemSettingsAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactionsettings"));
		
		itemSettingsAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDiffSettMenu").charAt(0)));
		
		JMenuItem instrumentsMenu = new JMenuItem (PaActionsMngr.get().getAction("painstrumentsaction"));
		
		instrumentsMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDiffInsMenu").charAt(0)));
		
		JMenuItem roiMenu = new JMenuItem (PaActionsMngr.get().getAction("paroisaction"));
		
		roiMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDiffROIMenu").charAt(0)));
		
		JMenuItem slideMenu = new JMenuItem(PaActionsMngr.get().getAction("paslideshowaction"));
		
		slideMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDiffSlideMenu").charAt(0)));
		
		difMenu.add(slideMenu);
		
		difMenu.add(instrumentsMenu);
		
		difMenu.add(roiMenu);
		
		difMenu.addSeparator();

		difMenu.add(itemSettingsAlbom);
				
		return difMenu;
		
	}
	
	/**
	 * <p>Creates menu Album for the main menu</p>
	 */
	public JMenu createMenuAlboms () {
		
		JMenu albomsMenu = new JMenu(PaUtils.getMenusStrs("albomMainMenuCaption")); 
		
		albomsMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyAlbomMenu").charAt(0)));

		JMenuItem itemNewAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactionnew"));
		
		itemNewAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyNewAlbomMenu").charAt(0)));
		
		JMenuItem itemEditAlbom = new JMenuItem(PaActionsMngr.get().getAction("paalbomactionedit"));
		
		itemEditAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyEditAlbomMenu").charAt(0)));
		
		JMenuItem itemDelAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactiondel"));
		
		itemDelAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDelAlbomMenu").charAt(0)));
		
		JMenuItem itemMoveAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactionmove"));
		
		itemMoveAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyMoveAlbomMenu").charAt(0)));
		
		JMenuItem itemMergeAlbom = new JMenuItem(PaActionsMngr.get().getAction("paalbomactionmerge"));
		
		itemMergeAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyMergeAlbomMenu").charAt(0)));
		
		JMenuItem itemFindAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactionfind"));
		
		itemFindAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyFindAlbomMenu").charAt(0)));
		
		JMenuItem itemPropAlbom = new JMenuItem (PaActionsMngr.get().getAction("paalbomactionprop"));
		
		itemPropAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyPropImageMenu").charAt(0)));
		
		JMenuItem itemLoad = new JMenuItem (PaActionsMngr.get().getAction("paactionload"));
		
		itemLoad.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyLoadMenu").charAt(0)));	
		
		albomsMenu.add(itemLoad);
		
		albomsMenu.addSeparator();
		
		albomsMenu.add(itemNewAlbom);
		
		albomsMenu.add(itemEditAlbom);
		
		albomsMenu.add(itemMoveAlbom);
		
		albomsMenu.add(itemDelAlbom);
		
		albomsMenu.add(itemMergeAlbom);
		
		albomsMenu.add(itemFindAlbom);
		
		albomsMenu.addSeparator();	
		
		albomsMenu.add(itemPropAlbom);

		JMenuItem exit = new JMenuItem(PaActionsMngr.get().getAction("paactionexit"));
		
		exit.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyExitMenu").charAt(0)));
		
		albomsMenu.add(exit);
				
		return albomsMenu;
	}
	
	
	
	  public JMenu createSortMenu() {
		  
		JMenu submenu = new JMenu(PaUtils.getMenusStrs("sortingMenuName"));
		  
		submenu.add( new PaSortMenuAction(0) );
		
		submenu.add( new PaSortMenuAction(1) );
		
		submenu.add( new PaSortMenuAction(2) );
		
		submenu.add( new PaSortMenuAction(3) );
		
		submenu.add( new PaSortMenuAction(4) );
			
		return submenu;
	  }
	
	/**
	 * 
	 * @return the Image menu 
	 */
	public JMenu createMenuImage () {
		
		JMenu imageMenu = new JMenu(getMenusStrs("imagesMainMenuName")); 
		
		imageMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyImagesMenu").charAt(0)));
		
		JMenuItem itemOpenInFolder = new JMenuItem (PaActionsMngr.get().getAction("paactionfolder"));
		
		itemOpenInFolder.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyImagesOpenFolderMenu").charAt(0)));
		
		
		JMenuItem itemCopyInPhoto = new JMenuItem (PaActionsMngr.get().getAction( "paimageactioncopyin"));
		
		itemCopyInPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyImagesCopyInFolderMenu").charAt(0)));
		
		JMenuItem itemAddPhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactionnew"));
		
		itemAddPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyAddImageMenu").charAt(0)));

		JMenuItem itemAddPhotos = new JMenuItem(PaActionsMngr.get().getAction("pactionaddgroup"));
		
		itemAddPhotos.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyAddImageGroupMenu").charAt(0)));
		
		JMenuItem itemEditPhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactionedit"));
		
		itemEditPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyEditImageMenu").charAt(0)));
		
		
		JMenuItem itemDelPhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactiondel"));
		
		itemDelPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDelImageMenu").charAt(0)));
		
		JMenuItem itemCutPhoto = new JMenuItem (PaActionsMngr.get().getAction( "paimageactioncut"));
		
		itemCutPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyCutImageMenu").charAt(0)));

		
		JMenuItem itemCopyPhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactioncopy"));
		
		itemCopyPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyCopyImageMenu").charAt(0)));
		
		
		JMenuItem itemPastePhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactionpaste"));
		
		itemPastePhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyPasteImageMenu").charAt(0)));
		
		JMenuItem disablePhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactiondeselect"));
		
		disablePhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDeSelectAllImageMenu").charAt(0)));
		
		JMenuItem selectAllPhotos = new JMenuItem(PaActionsMngr.get().getAction("paimageactionselect"));
		
		selectAllPhotos.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeySelectAllImageMenu").charAt(0)));
		
		JMenuItem newAlbomForSelect = new JMenuItem(PaActionsMngr.get().getAction("paactionalbomforselection"));
		
		newAlbomForSelect.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyNewAlbomImageMenu").charAt(0)));
		
		JMenuItem moveSelectionToAlbom  = new JMenuItem(PaActionsMngr.get().getAction("paactionmoveselected"));
		
		moveSelectionToAlbom.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyImageSelectioToMenu").charAt(0)));
		
		JMenuItem propertiesPhoto = new JMenuItem (PaActionsMngr.get().getAction("paimageactionproperties"));
		
		propertiesPhoto.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyPropImageMenu").charAt(0)));

		JMenuItem moveToStandard = new JMenuItem (PaActionsMngr.get().getAction("paactionmovelinkstostandselected"));
		
		JMenuItem rotateLeft = new JMenuItem (PaActionsMngr.get().getAction("paimageactionrtleft"));
		
		JMenuItem rotateRight = new JMenuItem (PaActionsMngr.get().getAction("paimageactionrtright"));
		
		JMenuItem printMenu = new JMenuItem (PaActionsMngr.get().getAction("paprintaction"));
		
		JMenuItem syncDatesMenu = new JMenuItem (PaActionsMngr.get().getAction("paimagedatesync"));
				
		imageMenu.add(itemAddPhoto);
		
		imageMenu.add(itemAddPhotos);
		
		imageMenu.add(itemEditPhoto);
		
		imageMenu.add(itemDelPhoto);
		
		imageMenu.addSeparator();
		
		imageMenu.add(itemCutPhoto);
		
		imageMenu.add(itemCopyPhoto);
		
		imageMenu.add(itemPastePhoto);
		
		imageMenu.addSeparator();
		
		imageMenu.add(selectAllPhotos);
		
		imageMenu.add(disablePhoto);
		
		imageMenu.add(newAlbomForSelect);
		
		imageMenu.add(moveSelectionToAlbom);
		
		imageMenu.add(moveToStandard);
		
		imageMenu.add(syncDatesMenu);
		
		imageMenu.addSeparator();
		
		imageMenu.add(rotateLeft);
		
		imageMenu.add(rotateRight);
		
		imageMenu.addSeparator();
		
		imageMenu.add(itemCopyInPhoto);
		
		imageMenu.add(itemOpenInFolder);
		
		imageMenu.addSeparator();
		
		imageMenu.add(propertiesPhoto);	
		
		imageMenu.add(printMenu);

		return imageMenu;
	}
	
	/**
	 * 
	 * @return tne Subject menu
	 */
	public JMenu createMenuSubjects () {
		
		JMenu subMenu = new JMenu(PaUtils.getMenusStrs("subjectMainMenuName")); 
		
		subMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeySubjectMenu").charAt(0)));
		
		JMenuItem itemNewTem = new JMenuItem (PaActionsMngr.get().getAction("pasubactionnew"));
		
		itemNewTem.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyNewSubjectMenu").charAt(0)));
		
		JMenuItem itemEditTem = new JMenuItem (PaActionsMngr.get().getAction("pasunactionedit"));
		
		itemEditTem.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyEditSubjectMenu").charAt(0)));
		
		JMenuItem itemDelTem = new JMenuItem (PaActionsMngr.get().getAction("patemactiondel"));
		
		itemDelTem.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyDelSubjectMenu").charAt(0)));
		
		JMenuItem itemFindTem = new JMenuItem(PaActionsMngr.get().getAction("patemactionfind"));
		
		itemFindTem.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyFindSubjectMenu").charAt(0)));
		
		JMenuItem subjectInsert = new JMenuItem (PaActionsMngr.get().getAction("pasubselectedinsert"));
		
		subMenu.add(itemNewTem);
		
		subMenu.add(itemEditTem);
		
		subMenu.add(itemDelTem);
		
		subMenu.add(itemFindTem);
		
		subMenu.addSeparator();
		
		subMenu.add(subjectInsert);
		
		return subMenu;
	}
	
	/**
	 * 
	 * @return the menu View in the main application menu
	 */
	public JMenu createMenuView() {
		
		JMenu vMenu = new JMenu(PaUtils.getMenusStrs("viewMainMenuName")); 
		
		vMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyViewMenu").charAt(0)));
		
		JMenuItem itemFilter = new JMenuItem (PaActionsMngr.get().getAction("paimageactionfilter"));
		
		itemFilter.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyFilterViewMenu").charAt(0)));
		
		JMenuItem itemClearFilter = new JMenuItem (PaActionsMngr.get().getAction("paimageactionfilterclear"));
		
		itemClearFilter.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyClearFilterViewMenu").charAt(0)));
		
		vMenu.add(createSortMenu());
		
		vMenu.add(itemFilter);
		
		vMenu.add(itemClearFilter);
		
		return vMenu;
	}
	
	/**
	 * 
	 * @return the menu Windows
	 */
	public JMenu createMenuWindows () {
		
		JMenu viewMenu = new JMenu( PaUtils.getMenusStrs("windowsMainMenuName") ); 
		
		viewMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyWindowMenu").charAt(0)));
		
		ButtonGroup butGroup = new ButtonGroup();
		
		JRadioButtonMenuItem view_1 = new JRadioButtonMenuItem (PaUtils.getMenusStrs("leftViewName")); //"standard view 1"
		
		view_1.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyWindowLeftViewMenu").charAt(0)));
		
		JRadioButtonMenuItem view_2 = new JRadioButtonMenuItem (PaUtils.getMenusStrs("topViewName")); //"standard view 2"
		
		view_2.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyWindowTopViewMenu").charAt(0)));
		
		JRadioButtonMenuItem view_3 = new JRadioButtonMenuItem (PaUtils.getMenusStrs("centerViewName") );
		
		view_3.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyWindowCenterViewMenu").charAt(0)));
		
		if (PaUtils.get().getSettings().getOrientation() == 1) {
			
			view_1.setSelected(true);
			
		} else if (PaUtils.get().getSettings().getOrientation() == 0) {
			
			view_2.setSelected(true);			
			
		} else if (PaUtils.get().getSettings().getOrientation() == 2) {
			
			view_3.setSelected(true);			
		}
		
		// standard view 1 has been selected
		view_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				mainPanel.setSplitMainOrientation(1);
				
				PaUtils.get().getSettings().setOrientation(1);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});

		// standard view 2
		view_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				mainPanel.setSplitMainOrientation(0);
				
				PaUtils.get().getSettings().setOrientation(0);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
		// standard view 3
		view_3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				mainPanel.setSplitMainOrientation(2);
				
				PaUtils.get().getSettings().setOrientation(2);
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
				
		view_1.setActionCommand("01");
		
		view_1.setActionCommand("02");
		
		view_1.setActionCommand("03");
		
		butGroup.add(view_1);
		
		butGroup.add(view_2);
		
		butGroup.add(view_3);

		viewMenu.add(view_1);
		
		viewMenu.add(view_2);
		
		viewMenu.add(view_3);
		
		viewMenu.addSeparator();
		
		//switch on/off on the main panel the visibility of album's tree
		albSet.setSelected(PaUtils.get().getSettings().is_alVisible());
		
		albSet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				albomsForm.setVisible(albSet.isSelected());
				
				mainPanel.setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});


		//switch on/off on the main panel the visibility of main view panel
		viePanel.setSelected(PaUtils.get().getSettings().is_viVisible());
		
		viePanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				PaUtils.get().getSettings().set_viVisible(viePanel.isSelected());
				
			
				viewPanel.setVisible(viePanel.isSelected());	
				
				mainPanel.setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
		//switch on/off on the main panel the visibility of subjects table (list)
		subtSet.setSelected(PaUtils.get().getSettings().isSubjectsVisible());
		
		subtSet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				temsForm.setVisible(subtSet.isSelected());
				
				//PaUtils.get().getSettings().setSubjectsVisible(subtSet.isSelected());
				mainPanel.setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});
		
		//switch on/off on the main panel the visibility of images table
		phoSet.setSelected(PaUtils.get().getSettings().is_phVisible());
		
		phoSet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				photosForm.setVisible(phoSet.isSelected());
		
				mainPanel.setSplitMainOrientation(PaUtils.get().getSettings().getOrientation());
				
				PaEventInt enable = new PaEventEnable(PaEventEnable.TYPE.DATA_CHANGED);
				
				PaEventDispatcher.get().fireCustomEvent(enable);
			}
		});

		viewMenu.add(albSet);
		
		viewMenu.add(phoSet);
		
		viewMenu.add(subtSet);
		
		viewMenu.add(viePanel);
					
		return viewMenu;
	}
	
	public void resizeMainPanel (PaEventAlbumResizePanel eventResize) {
		
		int response = eventResize.get_paButton().get_flag();
		
		switch (response) {
			case 0: {
				if (albomsForm.getButtonGroup()==eventResize.get_buttonGrop()) {
					
					albSet.setSelected(false);
					
				} else if (photosForm.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					phoSet.setSelected(false);
					
				} else if (temsForm.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					subtSet.setSelected(false);
					
				} else if (viewPanel.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					viePanel.setSelected(false);
				}
				break;
			}
			
			case 1: {
				
				if (albomsForm.getButtonGroup()==eventResize.get_buttonGrop()) {
					
					phoSet.setSelected(false);
					
					subtSet.setSelected(false);
					
					viePanel.setSelected(false);
					
				} else if (photosForm.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					albSet.setSelected(false);
					
					subtSet.setSelected(false);
					
					viePanel.setSelected(false);
					
				} else if (temsForm.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					albSet.setSelected(false);
					
					phoSet.setSelected(false);
					
					viePanel.setSelected(false);
					
				} else if (viewPanel.get_ButtonGroup()==eventResize.get_buttonGrop()) {
					
					albSet.setSelected(false);
					
					phoSet.setSelected(false);
					
					subtSet.setSelected(false);
				}
				break;
			}
			
			case 2: {
				
				albSet.setSelected(true);
				
				phoSet.setSelected(true);
				
				subtSet.setSelected(true);
				
				viePanel.setSelected(true);
				
				break;
			}
		}

	}
	
	/**
	 * 
	 * @return the Help menu
	 */
	public JMenu createMenuHelp () {
		
		JMenu helpMenu = new JMenu(PaUtils.getMenusStrs("helpMainMenuName"));
		
		helpMenu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyHelpMenu").charAt(0)));
		
		JMenuItem itemUsersManual = new JMenuItem(PaUtils.getMenusStrs("userGuideMainMenuName"));
		
		itemUsersManual.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyHelpManualMenu").charAt(0)));
		
		itemUsersManual.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("helpAccelerator").charAt(0)), 
				ActionEvent.CTRL_MASK));
	
		
		
		JMenuItem itemAboutProgram = new JMenuItem (PaUtils.getMenusStrs("aboutMainMenuName")); 
		
		itemAboutProgram.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyHelpAboutMenu").charAt(0)));
		
		JMenuItem itemLic = new JMenuItem (PaUtils.getMenusStrs("licenseMainMenuName"));
		
		itemLic.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyHelpLicMenu").charAt(0)));
		
		helpMenu.add(itemUsersManual);
		
		helpMenu.add(itemAboutProgram);
		
		helpMenu.add(itemLic);
		
		itemAboutProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				PaAboutDialog dialog = new PaAboutDialog(PaUtils.get().getMainWindow());
				
				dialog.setVisible(true);	
			}
		});
		
		itemUsersManual.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				PaHelpBrowser dialog = new PaHelpBrowser(PaUtils.get().getMainWindow(),
						PaUtils.getGuiStrs("userManualDialogCaption"));
				
				dialog.setVisible(true);
				
			}
		});
		
		itemLic.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				PaLicenseDialog dialog = new PaLicenseDialog(PaUtils.get().getMainWindow());
				dialog.setVisible(true);	
			}
		});
		
		return helpMenu;
	}
	
	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);
		
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		EventQueue.invokeLater(new Runnable() {		
			
			@Override
			public void run() {			
				
				new PaDesktopPane();
			}
		});
	}

}
