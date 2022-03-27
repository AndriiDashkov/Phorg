
package PaEnums;

/**
 * @author Andrey Dashkov
 * Struct with 3 values, it is ready to put into ArrayList, etc
 */ 
public class PaTriple<F,S, K> {

	private F m_first;
	
	private S m_second;
	
	private K m_third;
	
	public PaTriple(F f1,S s2, K s3) {
		
		super();
		
		m_first = f1;
		
		m_second = s2;
		
		m_third = s3;
	}
	
	public F first() { return m_first; }
	
	public S second() { return m_second; }
	
	public K third() { return m_third; }
	
	
	public void setFirst(F f1) { m_first = f1; }
	
	public void setSecond(S s1) { m_second = s1; }
	
	public void setThird(K s2) { m_third = s2; }
	
	/**
	 * <p>Reloaded equals operator</p>
	 */
	public boolean equals(Object other) 
	{
		if (other instanceof PaTriple<?,?, ?>) {
			
			@SuppressWarnings("unchecked")
			PaTriple<F,S, K> otherPair = (PaTriple<F,S, K>) other;
			
			return 
				(
					(  this.m_first == otherPair.m_first ||
					
						( this.m_first != null && otherPair.m_first != null &&
						
							this.m_first.equals(otherPair.m_first)
						)
					)
					
					&&
					
					(	this.m_second == otherPair.m_second ||
					
						( this.m_second != null && otherPair.m_second != null &&
						
							this.m_second.equals(otherPair.m_second)
						)
					) 
					
					&&
					
					(	this.m_third == otherPair.m_third ||
					
						( this.m_third != null && otherPair.m_third != null &&
						
							this.m_third.equals(otherPair.m_third)
						)
					) 
			    );
		}	
		
		return false;
	}
}