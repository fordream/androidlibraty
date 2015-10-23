package com.vnp.andengine;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.vnp.core.common.LogUtils;

import android.content.Context;

public class AndEngineSprise {
	public TiledTextureRegion regCat;
	public AnimatedSprite sprCat;

	public void onCreateResources(Engine engine, Context context, String name, int colums, int rows) {
		BitmapTextureAtlas atlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		regCat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(atlas, context, name, 0, 0, colums, rows);
		engine.getTextureManager().loadTexture(atlas);
	}

	public TiledTextureRegion getRegCat() {
		return regCat;
	}

	public void onCreateScene(Scene scene) {
		sprCat = new AnimatedSprite(0, 0, regCat);
	}

	public void onloadSucess(Scene scene) {
	}

	public AnimatedSprite getSprCat() {
		return sprCat;
	}
}