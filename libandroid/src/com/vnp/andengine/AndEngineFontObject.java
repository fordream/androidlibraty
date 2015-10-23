package com.vnp.andengine;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.graphics.Color;
import android.graphics.Typeface;

public class AndEngineFontObject {
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private ChangeableText score;

	public ChangeableText getScore() {
		return score;
	}

	public Font getmFont() {
		return mFont;
	}

	public void onLoadResources(Engine mEngine) {
		mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 25, true, Color.BLACK);
		mEngine.getTextureManager().loadTexture(mFontTexture);
		mEngine.getFontManager().loadFont(mFont);
	}

	public void onLoadScene(Camera camera) {
		score = new ChangeableText(0, 0, mFont, "00000001111111111111");
		score.setPosition(camera.getWidth() - score.getWidth() - 5, 5);
	}

	public void attachChild(Scene mainScene) {
		mainScene.attachChild(score);
	}

	public void setText(String text) {
		score.setText(text);

	}

	public void setPosition(int pX, int pY) {
		score.setPosition(pX, pY);
	}
}