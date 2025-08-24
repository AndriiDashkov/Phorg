
package paundoredo;

import java.util.ArrayDeque;

import paactions.PaActionsMngr;
import paactions.PaRedoAction;
import paactions.PaUndoAction;
import paevents.PaEvent;
import paevents.PaEventDispatcher;


/**
 * <p>This is singletone to deal with undo and redo operations</p>
 * @author Andrii Dashkov
 *
 */
public class PaUndoRedoDeque {
	
	private static PaUndoRedoDeque m_instance;
	
	private ArrayDeque<PaUndoRedoCommand> m_undo = null; 
	
	private ArrayDeque<PaUndoRedoCommand> m_redo = null;
	
	private PaUndoAction m_undoAction = (PaUndoAction)PaActionsMngr.get().getAction("paundoaction");
	
	private PaRedoAction m_redoAction = (PaRedoAction)PaActionsMngr.get().getAction("paredoaction");
	
	{	
		PaEventDispatcher.get().addConnect(PaEventDispatcher.UNDO_EVENT, this, "undo");
		
		PaEventDispatcher.get().addConnect(PaEventDispatcher.REDO_EVENT, this, "redo");
	}
	
	private PaUndoRedoDeque() {
		
		m_undo = new ArrayDeque<PaUndoRedoCommand>(); 
		
		m_redo = new ArrayDeque<PaUndoRedoCommand>();	
	
	};
	
	public static PaUndoRedoDeque get() {
		
		if (m_instance == null) {
			
			m_instance = new PaUndoRedoDeque();
		}
		
		return m_instance;
	}
	
	public void undo(PaEvent e) {
		
		if(m_undo.isEmpty()) {
			
			return;
		}  
		
		PaUndoRedoCommand com = m_undo.poll();
		
		com.undo();
		
		addRedo(com);
		
		if(m_undo.isEmpty()) {
			
			m_undoAction.setEnabled(false);
		}
	}
	
	public void redo(PaEvent e) {
		
		if(m_redo.isEmpty()) {
			
			return;
		}  
		
		PaUndoRedoCommand com = m_redo.poll();
		
		com.redo();
		
		addUndo(com);
		
		if(m_redo.isEmpty()) {
			
			m_redoAction.setEnabled(false);
		}	
	}
	
	public void addUndo(PaUndoRedoCommand com) 
	{
		m_undo.addFirst(com);	
		
		m_undoAction.setEnabled(true);
		
	}
	
	public void addRedo(PaUndoRedoCommand com) 
	{
		m_redo.addFirst(com);	
		
		m_redoAction.setEnabled(true);
		
	}
	
	public boolean isUndoEmpty() {  return m_undo.isEmpty(); }
	
	public boolean isRedoEmpty() {  return m_redo.isEmpty(); }
	
	public void clearAll() {
		
		m_undo.clear();
		
		m_redo.clear();
		
		m_redoAction.setEnabled(false);
		
		m_undoAction.setEnabled(false);
		
	}
}
