
package PaImage;


import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;


/**
 * <p>This class is for drop operation only</p>
 * @author avd
 *
 */
public class PaDropTargetListener implements DropTargetListener {

	  /**
     * <p>The reference to the root panel where the drag and drop is doing</p>
     */
    private final PaViewPanel m_rootPanel;
    
    private static final Cursor dropCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public PaDropTargetListener(PaViewPanel p) {
    	
        m_rootPanel = p;
    }
    
    @Override()
    public void dragEnter(DropTargetDragEvent dtde) {
    	
    	
    }
    
    @Override()
    public void dragOver(DropTargetDragEvent dtde) {
    	
        if (!this.m_rootPanel.getCursor().equals(dropCursor)) {
        	
            this.m_rootPanel.setCursor(dropCursor);
        }
    }
    
    @Override()
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    /**
     * this function implement the boundary behavior of drag and drop - if we are on the boundary then the scroll bar should be moved
     */
    @Override()
    public void dragExit(DropTargetEvent e) {
    	
        this.m_rootPanel.setCursor(Cursor.getDefaultCursor());
        
        Point cursorLocation = MouseInfo.getPointerInfo().getLocation();
        
    	Point pBoundary = m_rootPanel.getScrollPaneLocationOnScreen();
    	 	
    	if(cursorLocation.y < pBoundary.y) {
    		
    		m_rootPanel.incrementVerticalScrollbar(-(PaViewPanel.VERT_SCROLL_UNIT_INC*3));
    		
		    Robot robot;
		    
			try {
				
				robot = new Robot();
				
				robot.mouseMove(cursorLocation.x, pBoundary.y + 20);
				
			} catch (AWTException e1) {}
    	} 
    	else 
    	if(cursorLocation.y > (pBoundary.y + m_rootPanel.getScrollPaneSize().getHeight()-15)) {
    		
    		m_rootPanel.incrementVerticalScrollbar((PaViewPanel.VERT_SCROLL_UNIT_INC*3));
    		
		    Robot robot;
		    
			try {
				
				robot = new Robot();
				
				robot.mouseMove(cursorLocation.x, (int)(pBoundary.y + m_rootPanel.getScrollPaneSize().getHeight() - 35));
				
			} catch (AWTException e1) {}
    	}
    	else
    	if(cursorLocation.x > (pBoundary.x + m_rootPanel.getScrollPaneSize().getWidth()-15)) {
    		
    		m_rootPanel.incrementHorizontalScrollbar((PaViewPanel.VERT_SCROLL_UNIT_INC*3));
    		
		    Robot robot;
		    
			try {
				
				robot = new Robot();
				
				robot.mouseMove((int)(pBoundary.x + m_rootPanel.getScrollPaneSize().getWidth()-35),
						cursorLocation.y);
				
			} catch (AWTException e1) {}
    	}
    	else
    	if(cursorLocation.x < (pBoundary.x + 25)) {
    		
    		m_rootPanel.incrementHorizontalScrollbar(-(PaViewPanel.VERT_SCROLL_UNIT_INC*3));
    		
		    Robot robot;
		    
			try {
				
				robot = new Robot();
				
				robot.mouseMove(pBoundary.x+35, cursorLocation.y);
				
			} catch (AWTException e1) {}
    	}
    
    }
    


    /**
     * <p>starts drop operation after the flavor control</p>
     * @param e - drop event
     */
    public void drop(DropTargetDropEvent e) {
        
        this.m_rootPanel.setCursor(Cursor.getDefaultCursor());
        
        DataFlavor dnDFlavor = null;
        
        Object trObj = null;
        
        Transferable transferable = null;
        
        try {
         
            dnDFlavor = PaViewPanel.getDragAndDropFlavor();
            
            transferable = e.getTransferable();
            
            if (transferable.isDataFlavorSupported(dnDFlavor)) {
            	
                trObj = e.getTransferable().getTransferData(dnDFlavor);
            } 
            
        } catch (Exception ex) {  }
        
        if (trObj == null) {
        	
            return;
        }
        
        // doing the drop
		if( trObj instanceof PaViewPhotosForm) {
			
			PaViewPhotosForm form = (PaViewPhotosForm)trObj;
			
			m_rootPanel.doDrop(e.getLocation(), form.getImage().getId());
		}
	
    }
}