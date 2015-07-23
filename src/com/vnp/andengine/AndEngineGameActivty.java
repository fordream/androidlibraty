package com.vnp.andengine;

import java.util.HashMap;
import java.util.Set;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;

public abstract class AndEngineGameActivty extends BaseGameActivity implements IOnSceneTouchListener {

	/**
	 * 
	 */
	@Override
	public void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

	}

	/**
	 * create main scene
	 */
	private Scene mMainScene;

	public Scene getmMainScene() {
		return mMainScene;
	}

	@Override
	public Scene onLoadScene() {
		if (mMainScene == null) {
			mMainScene = new Scene();
			mMainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
			mMainScene.setOnSceneTouchListener(this);
			mEngine.registerUpdateHandler(new FPSLogger());
		}

		return mMainScene;
	}

	/**
	 * Camera
	 */
	private Camera mCamera = new Camera(0, 0, 960, 640);

	public Camera getmCamera() {
		return mCamera;
	}

	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth();
		int cameraHeight = display.getHeight();
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera).setNeedsSound(true).setNeedsMusic(true));
	}
}
