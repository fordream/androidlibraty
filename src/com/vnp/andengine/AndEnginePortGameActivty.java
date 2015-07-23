package com.vnp.andengine;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.input.touch.TouchEvent;

public class AndEnginePortGameActivty extends AndEngineGameActivty {

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
		return false;
	}

	@Override
	public void onLoadComplete() {

	}
	@Override
	public boolean isLandscape() {
		return false;
	}
}
