
package paforms;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andrii Dashkov
 *<p>Tree node object which is used in albom's tree as an item. Every item is a separate albom</p>
 */
public class PaAlbumTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;
	
	private int m_id;//here we save albom's id
	
	private String m_name;


	public PaAlbumTreeNode() {
		
	}

	/**
	 * @param arg0
	 */
	public PaAlbumTreeNode(Object arg0) {
		
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PaAlbumTreeNode(Object arg0, boolean arg1) {
		
		super(arg0, arg1);
	}
	
	
	public int getId() { return m_id; }
	
	public void setId(int i) { m_id = i; }
	
	public String getAlbumName() { return m_name; }
	
	public void setAlbumName(String i) { m_name = i; }

}
