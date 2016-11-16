package com.bomeans.irapi;


import com.bomeans.IRKit.BIRGUIFeature;

public class ACGUIFeatures {

	public enum DisplayMode {
		/**
		 * the remote contains no display panel
		 */
		NoDisplay,
		/**
		 * the panel is switched on when the power is on, switched off when power is off.
		 */
		ValidWhilePoweredOn,
		/**
		 * display always on
		 */
		AlwaysOn;

		public static int getDisplayModeInt(DisplayMode mode) {
			switch (mode) {
				case NoDisplay:
					return 0;
				case ValidWhilePoweredOn:
					return 1;
				case AlwaysOn:
					return 2;
				default:
					return 0;	
			}
		}
		
		public static DisplayMode getDisplayMode(int i) {
			switch (i) {
			case 0:
				return NoDisplay;
			case 1:
				return ValidWhilePoweredOn;
			case 2:
				return AlwaysOn;
			default:
				return NoDisplay;
			}
		}
	}
	
	enum TimerDisplayMode {
		
		/**
		 * Not support timer
		 */
		None,
		
		/**
		 * The timer is set by specifying the exact time stamp.
		 */
		Clock,
		
		/**
		 * The timer is set by counting down (relative to the current time)
		 */
		CountDown;
	}
	
	enum TimerOperationMode {
		/**
		 * unknown mode
		 */
		modeUnknown,
		/**
		 * support only off timer (no matter powered on/off)
		 */
		mode1,
		/**
		 * support on/off timer, can be set only powered on
		 */
		mode2,
		/**
		 * support on/off timer (no matter powered on/off)
		 */
		mode3,
		/**
		 * support either on or off timer, can be set only powered on
		 */
		mode4,
		/**
		 * can set off timer while powered on, off timer while powered off
		 */
		mode5;
		
		public static int getModeInt(TimerOperationMode mode) {
			switch (mode) {
				case mode1:	return 1;
				case mode2:	return 2;
				case mode3:	return 3;
				case mode4:	return 4;
				case mode5:	return 5;
				default:
					return 0;	
			}
		}
		
		public static TimerOperationMode getMode(int i) {
			switch (i) {
			case 1:	return mode1;
			case 2:	return mode2;
			case 3:	return mode3;
			case 4:	return mode4;
			case 5:	return mode5;
			default:
				return modeUnknown;
			}
		}
	}
	
	/**
	 * The display panel mode.
	 */
	public DisplayMode displayMode = DisplayMode.NoDisplay;
	
	/**
	 * if the remote has RTC feature
	 */
	public boolean hasRealTimeClock = false;
	public TimerDisplayMode timerDisplayMode = TimerDisplayMode.CountDown;
	
	public TimerOperationMode timerOperationMode = TimerOperationMode.modeUnknown;
	
	public ACGUIFeatures(BIRGUIFeature guiFeatures) {
		
		this.displayMode = DisplayMode.getDisplayMode(guiFeatures.displayType);
		this.hasRealTimeClock = guiFeatures.RTC;
		
		if (guiFeatures.timerClock) {
			this.timerDisplayMode = TimerDisplayMode.Clock;
		} else if (guiFeatures.timerCountDown) {
			this.timerDisplayMode = TimerDisplayMode.CountDown;
		} else {
			this.timerDisplayMode = TimerDisplayMode.None;
		}
		
		this.timerOperationMode = TimerOperationMode.getMode(guiFeatures.timerMode);
	}
}
