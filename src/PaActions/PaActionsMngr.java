package paactions;


import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * <p>The actions manager class. All actions must be registered here. Any access to action must be implemented via this singletone class</p>
 * @author Andrii Dashkov
 *
 */
public class PaActionsMngr {
	
	private static PaActionsMngr _instance;
	
	
	HashMap<String,AbstractAction> m_map = new HashMap<String,AbstractAction>();	
	
	private PaActionsMngr() {};
	
	public static PaActionsMngr get() {
		
		if (_instance == null) {
			
			_instance = new PaActionsMngr();
		}
		return _instance;
	}
	
	public void initActions () {
		
		register(new PaAlbumActionProp());
	
		register(new PaAlbumActionNew());
		 
		register(new PaAlbumActionMove());
		
		register(new PaAlbumActionMerge());
		
		register(new PaAlbumActionFind());
		
		register(new PaAlbumActionEdit());
		
		register(new PaAlbumActionDel());
		
		register(new PaActionMoveLinksToStandard());
		
		register(new PaActionLoad());
		
		register(new PaPhotoActionEdit());
		
		register(new PaActionExit());
		
		register(new PaPhotoActionFilter());
		
		register(new PaSlideShowAction());
		
		register(new PaPhotoActionFilterClear());
		
		register(new PaPhotoActionProperties());
		
		register(new PaPhotoActionDeSelection());
		
		register(new PaPhotoActionSelect());
		
		register(new PaInstrumentsAction());
		
		register(new PaRoiAction());
		
		register(new PaActionAlbomForSelection());
		
		register(new PaActionFolder());
		
		register(new PaActionSave());
		
		register(new PaPhotoActionAddGroup());
		
		register(new  PaAlbumActionSettings());
		
		//register(new  PaPhotoActionOpenIn());
		
		register(new  PaPhotoActionCopyIn());
		
		register(new  PaPhotoActionNew());
		
		register(new  PaPhotoActionDel());
		
		register(new  PaPhotoActionCut());
		
		register(new  PaPhotoActionCopy());
		
		register(new  PaPhotoActionPaste());
		
		//register(new  PaSortAction());
		register(new PaActionMoveSelected());
		
		register(new PaSubjectsSelectedInsert());
		
		register(new PaPhotoActionIcons());
		
		register(new PaPhotoActionRefreshIcons());
		
		register(new PaSubjectActionNew());
		
		register(new PaSubjectActionEdit());
		
		register(new PaSubjectActionDel());
		
		register(new PaSubjectActionFind());
		
		register(new PaUndoAction());
		
		register(new PaRedoAction());
		
		register(new PaActionRefreshBoostIcon());
		
		register(new PaImageRotateLeft());
		
		register(new PaImageRotateRight());
		
		register(new PaActionMoveLinksToStandSelected());
		
		register(new PaPrintAction());
		
		register(new PaBookmarksResetAction());
		
		register(new PaSliderMarksReset());
		
		register(new PaActionDatesSync());
		
		register(new PaSubjectsSelectedRemove());
		
	}
	
	/**
	 * Registers action
	 * @param action - action to register
	 */
	public void register(PaAction action){
		
		m_map.put(action.getActName(), action);
	}
	/**
	 * First this function invokes the disconnect method for action
	 * @param action - action to unregister
	 */
	public void unregister(PaAction action){
		
		action.disconnect();
		
		m_map.remove(action.getActName());
		
	}
	
	public AbstractAction getAction(String name){
		
		return m_map.get(name);
	}
}
