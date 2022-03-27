package PaEnums;

/**
 *  <p>'Pair' classs</p>
 * @author avd
 * @param <F> - first element of pair
 * @param <S> - second element of pair
 */
public class PaPair<F,S> {

	private F m_first;
	
	private S m_second;
	
	public PaPair(F f1,S s2) {
		
		super();
		
		m_first = f1;
		
		m_second = s2;
	}
	
	public F first() { return m_first; }
	
	public S second() { return m_second; }
	
	
	public void setFirst(F f1) { m_first = f1; }
	
	public void setSecond(S s1) { m_second = s1; }
	
	/**
	 * <p>Reloaded equals operator</p>
	 */
	public boolean equals(Object other) 
	{
		if (other instanceof PaPair<?,?>) {
			
			@SuppressWarnings("unchecked")
			PaPair<F,S> otherPair = (PaPair<F,S>) other;
			
			return 
				(
					(  this.m_first == otherPair.m_first ||
						( this.m_first != null && otherPair.m_first != null &&
							this.m_first.equals(otherPair.m_first)
						)
					) &&
					(	this.m_second == otherPair.m_second ||
						( this.m_second != null && otherPair.m_second != null &&
							this.m_second.equals(otherPair.m_second)
						)
					) 
			    );
		}
		
		return false;
	}
}
