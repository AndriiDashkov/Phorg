
package paeditor;

import static paglobal.PaUtils.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Andrii Dashkov
 *
 */
public class PaStatInfoPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	double [][] m_br = null;
	
	JLabel m_labelRedBr; 
	
	JLabel m_labelGreenBr; 
	
	JLabel m_labelBlueBr;
	
	JLabel m_labelRedDs; 
	
	JLabel m_labelGreenDs; 
	
	JLabel m_labelBlueDs;

	/**
	 * 
	 */
	public PaStatInfoPanel(double [][] br) {
		
		super();
		
		m_br = br;
		
		createGui();
	}
	
	public PaStatInfoPanel() {
		
		super();
		
		createGui();
	}
	
	public void createGui() {
					
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		//setLayout(new SpringLayout());
		JLabel labelRed = new JLabel(getGuiStrs("red"));
		
		JLabel labelGreen = new JLabel(getGuiStrs("green"));
		
		JLabel labelBlue = new JLabel(getGuiStrs("blue"));
		
		JLabel labelAvBr = new JLabel(getGuiStrs("brightness")+" ");
		
		JLabel labelDisp = new JLabel(getGuiStrs("dispersion")+ " ");
		
		m_labelRedBr = new JLabel(); 
		
		m_labelGreenBr = new JLabel();
		
		m_labelBlueBr = new JLabel();
		
		m_labelRedDs = new JLabel();
		
		m_labelGreenDs = new JLabel();
		
		m_labelBlueDs = new JLabel();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		
		c.gridy = 0;
		
		c.anchor = GridBagConstraints.CENTER;
		
		c.insets = new Insets(5,5,5,5);
		
		add(new JLabel(),c);
		
		c.gridx = 1;
		
		c.gridy = 0;
	
		add(labelRed,c);
		
		c.gridx = 2;
		
		c.gridy = 0;
		
		add(labelGreen,c);
		
		c.gridx = 3;
		
		c.gridy = 0;

		add(labelBlue,c);
		
		c.gridx = 0;
		
		c.gridy = 1;
		
		add(labelAvBr,c);

		c.gridx = 1;
		
		c.gridy = 1;

		add(m_labelRedBr,c);
		
		c.gridx = 2;
		
		c.gridy = 1;

		add(m_labelGreenBr,c);
		
		c.gridx = 3;
		
		c.gridy = 1;

		add(m_labelBlueBr,c);
		
		c.gridx = 0;
		
		c.gridy = 2;

		add(labelDisp,c);

		c.gridx = 1;
		
		c.gridy = 2;

		add(m_labelRedDs,c);

		c.gridx = 2;
		
		c.gridy = 2;
		
		add(m_labelGreenDs,c);
		
		c.gridx = 3;
		
		c.gridy = 2;

		add(m_labelBlueDs,c);
		
		Dimension size = getPreferredSize();
		
		setMaximumSize(size);
		
	}
	
	/**
	 * 
	 * @param br data for info panel [brightness,disp][red,green,blue]
	 */
	public void setData(double [][] br) {
		
		m_br = br;
	}
	/**
	 * 
	 */
	public void refreshView() {
		
		if(m_br != null) {
			
			m_labelRedBr.setText(Integer.toString((int)m_br[0][0])); 
			
			m_labelGreenBr.setText(Integer.toString((int)m_br[0][1]));
			
			m_labelBlueBr.setText(Integer.toString((int)m_br[0][2]));
			
			m_labelRedDs.setText(Integer.toString((int)m_br[1][0]));
			
			m_labelGreenDs.setText(Integer.toString((int)m_br[1][1]));
			
			m_labelBlueDs.setText(Integer.toString((int)m_br[1][2]));
		}
	}
	
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        
        int[][] dims = ((GridBagLayout)getLayout()).getLayoutDimensions();
        
        g.setColor(Color.RED);
        
        int x = 0;
       
        for (int add : dims[0])
        {
            x += add;
            
            g.drawLine(x, 0, x, getHeight());
        }
        
        int y = 0;
        
        for (int add : dims[1])
        {
            y += add;
            
            g.drawLine(0, y, getWidth(), y);
           
        }
        
        g.drawLine(0, 0, getWidth(), 0);
        
        g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
        
        g.drawLine(0, 0, 0, getHeight());
        
        g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
    }
}
