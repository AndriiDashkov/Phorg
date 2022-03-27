package PaEvents;

import PaGlobal.PaButton;
import PaGlobal.PaButtonsGroup;



public class PaEventAlbumResizePanel extends PaEvent {


	private PaButton _button;
	
	private PaButtonsGroup _buttonGrop;


	public PaEventAlbumResizePanel (PaButtonsGroup buttonGrop, PaButton button) {
		
		eventType = PaEventDispatcher.RESIZE_ALBOM_PANEL_EVENT;
		
		_button = button;
		
		_buttonGrop = buttonGrop;
	} 


	public PaButton get_paButton() {
		
		return _button;
	}
	

	public PaButtonsGroup get_buttonGrop() {
		
		return _buttonGrop;
	}
}
