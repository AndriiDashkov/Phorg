
package PaForms;

import static PaGlobal.PaLog.writeLog;
import static PaGlobal.PaUtils.getGuiStrs;
import PaGlobal.PaGuiTools;
import PaGlobal.PaUtils;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author avd
 *
 */

/**
 * <p>User manual dialog
 * The main idea of user manual system: folder Help has sub-folders which are called according to 
 * used languages (en,ru etc). Each folder has the file content.txt which is an xml file with the 
 * structure (contents) of whole text of manual. every item in this structure has the following info:
 * name of contents item and appropriative file name which has the manual text for it
 * The actions in order to support new language:
 * 1)appropriative lang should be supported by application in Settings menu
 * 2)Appropriative sub-folder should be created in the Help folder
 * 3)This sub-folder should have the contents.xml file and manual texts files for all items
 * 4)The application will load them automatically after choosing of desirebale language through settings menu  
 * </p>
 */
public class PaHelpBrowser extends JDialog 
{

	private static final long serialVersionUID = 1L;
	
	private JTree m_tree; //main tree for contents info
	
	private JLabel m_textArea; //field for manual texts
	
	private PaAlbumTreeNode m_root; //root node of the tree
	
	private final String ATTR_NAME = "name";
	
	private final String ATTR_FILE_NAME = "fileName";
	
	private Map<String, String> m_map = new HashMap<String,String>();
		
	public PaHelpBrowser(JFrame jf, String caption) {
		
		super (jf, caption, true); 
		
		createGui();
		
		init();
		
		setBounds(300, 100, 1000, 700);
		
		setResizable(true);
		
 		
 		for (int i = 0; i < m_tree.getRowCount(); i++) {
 			
 		    m_tree.expandRow(i);
 		}
	}
	
	/**
	 * <p>Creates gui elements for user manual dialog</p>
	 */
	private void createGui(){
		
		JPanel panel_MAIN = PaGuiTools.createHorizontalPanel();
		
		JPanel panelTree = PaGuiTools.createVerticalPanel();
	
		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(PaUtils.VERT_STRUT,PaUtils.VERT_STRUT
				,PaUtils.VERT_STRUT,PaUtils.VERT_STRUT));
		
		add(panel_MAIN);
		
		m_root = new PaAlbumTreeNode(getGuiStrs("rootCaptionHelpTree"));
		
		m_tree = new JTree(m_root);
		
		JScrollPane scrollPanel = new JScrollPane(m_tree);
		
		Dimension size = scrollPanel.getPreferredSize();
		
		size.width = 250;
		
		scrollPanel.setPreferredSize(size);
		
		size.height = 700;
		
		scrollPanel.setMaximumSize(size);
		
		panelTree.add(scrollPanel);
		
		panelTree.add(Box.createVerticalGlue());
		
		m_textArea = new JLabel();
		
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN , 13);

		m_textArea.setFont(f);
		
		m_textArea.setVerticalAlignment(SwingConstants.TOP);
		
		JScrollPane scrollPanel2 = new JScrollPane(m_textArea);
		
		m_textArea.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
	
		panel_MAIN.add(panelTree);
		
		panel_MAIN.add(Box.createHorizontalStrut(PaUtils.HOR_STRUT));
		
		panel_MAIN.add(scrollPanel2);
		
	}
	/**
	 * <p>Initiates the tree nodes and sets the listener for selection operations</p>
	 */
	private void init(){
		
		m_map.clear();
		
		loadTreeItems();
		
		m_tree.addTreeSelectionListener(new TreeSelectionListener() {
			
		    public void valueChanged(TreeSelectionEvent e) {
		    	
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		                           m_tree.getLastSelectedPathComponent();
	
		        if (node == null) return;
	
		        String s  = m_map.get(node.getUserObject());
		        
				if(s != null && !s.isEmpty()) {
					
					String sText = null;
					
					try {
						
						 byte[] b = Files.readAllBytes(Paths.get(PaUtils.get().getPathToHelpFiles()+s));
						 
						 sText = new String(b,"windows-1251");
						 
					} catch(IOException  ex) {
						
						writeLog(PaUtils.getMessagesStrs("cantReadHelpFile")+" "+s,ex,true,true,true);					
					}
					if(sText != null) {
						
						m_textArea.setText(sText);
						
					} else {
						
						m_textArea.setText("");
					}
				}
		    }
		});
	
	}
	
	/**
	 * <p>Loads contents file and then parses it with creation of tree nodes</p>
	 */
	public void loadTreeItems() {
		
		Document doc = null;
		
		try {
			
			File fl = new File(PaUtils.get().getFullPathToHelpFile());
		
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			doc = dbFactory.newDocumentBuilder().parse(fl);
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			
			writeLog("Parser error for help files ",e,true,false,true);
		}
		
		doc.getDocumentElement().normalize();
		
		NodeList rootList = doc.getDocumentElement().getChildNodes();
		
		for (int i = 0; i < rootList.getLength(); i++) {
		
			Node nNode = rootList.item(i);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				
				PaAlbumTreeNode treeNode = new PaAlbumTreeNode(eElement.getAttribute(ATTR_NAME));
				
				m_map.put(eElement.getAttribute(ATTR_NAME), eElement.getAttribute(ATTR_FILE_NAME));
		
				m_root.add(treeNode);
				
				createNodeChilds(eElement,treeNode);
				
			}
		}
	}
	/**
	 * <p>Creates children tree nodes according to children into rootElement. Function is intend for recursive call</p>
	 * @param rootElement - root node which should be parent for all found items
	 * @param treeNode - tree node which should be parent for all created tree nodes
	 */
	private void createNodeChilds(Element rootElement, PaAlbumTreeNode treeNode)
	{	
		NodeList rootList = rootElement.getChildNodes();
		
		for (int i = 0; i < rootList.getLength(); i++) {
		
			Node nNode = rootList.item(i);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				
				PaAlbumTreeNode item = new PaAlbumTreeNode(eElement.getAttribute(ATTR_NAME));
				
				m_map.put(eElement.getAttribute(ATTR_NAME), eElement.getAttribute(ATTR_FILE_NAME));
				
				treeNode.add(item);
				
				createNodeChilds(eElement,item);
			}
		}
	}
}