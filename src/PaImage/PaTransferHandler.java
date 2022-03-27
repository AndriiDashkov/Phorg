
package PaImage;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * <p>Transfer handler to use in drag and drop operation.</p>
 * @author avd
 *
 */
public class PaTransferHandler extends TransferHandler implements DragSourceMotionListener 
{

	private static final long serialVersionUID = 1L;

	public PaTransferHandler() {
		
		super();
	}

    @Override()
    public Transferable createTransferable(JComponent c) 
    {
        if (c instanceof PaViewPhotosForm) {
        	
            Transferable tip = (PaViewPhotosForm) c;
            
            return tip;
            
        }
        return null;
    }

    
    /**
     * <p>Always MOVE for our drag and drop. There is no sense in COPY type of drag and drop for the application</p>
     * @param c
     * @return
     */
    @Override()
    public int getSourceActions(JComponent c)
    {          
        if (c instanceof PaViewPhotosForm) {
        	
            return TransferHandler.MOVE;
        }
        
        return TransferHandler.MOVE;
    }

    public void dragMouseMoved(DragSourceDragEvent e) {}
}


