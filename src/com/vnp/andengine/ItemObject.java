package com.vnp.andengine;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.graphics.Point;
import android.os.Handler;

public class ItemObject {
	private Point position = new Point();
	private AnimatedSprite backgroud;
	private AnimatedSprite sprite;
	// type = -1 0 1 2 3 4 5 6 7 8
	private int type = -1;

	public AnimatedSprite getBackgroud() {
		return backgroud;
	}

	public Point getPosition() {
		return position;
	}

	public int getType() {
		return type;
	}

	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			way(false);
		};
	};

	public void way(boolean isWay) {
		if (backgroud != null) {
			backgroud.setCurrentTileIndex(isWay ? 1 : 0);
			if (isWay) {
				handler.sendEmptyMessageAtTime(0, 500);
			}
		}
	}

	public void create(Scene scene, int left, int top, int positionx, int positionY, TiledTextureRegion region) {
		position.x = positionx;
		position.y = positionY;
		int with = region.getWidth() / 2;
		int height = region.getHeight();
		backgroud = new AnimatedSprite(left + positionx * with, top + positionY * height, region.deepCopy());
		scene.attachChild(backgroud);
	}

	public void clear(Scene scene) {
		type = -1;
		isChecked = false;
		if (sprite != null) {
			sprite.setCurrentTileIndex(3, 1);
		}
		update(scene);
	}

	public void randomType(Scene scene, int type, AndEngineSprise bigs) {
		this.type = type;

		int index = type;
		if (index >= 10)
			index = index / 10;
		if (sprite == null) {
			sprite = new AnimatedSprite(backgroud.getX(), backgroud.getY(), bigs.getRegCat().deepCopy());
			scene.attachChild(sprite);
		}
		update(scene);
	}

	public boolean isSelected(int x, int y) {
		return backgroud.getX() < x && x < (backgroud.getX() + backgroud.getWidth()) && backgroud.getY() < y && y < (backgroud.getY() + backgroud.getHeight());
	}

	public void setChecked(boolean isChecked) {
		if (isBig()) {
			this.isChecked = isChecked;
		}
	}

	private boolean isChecked = false;

	public boolean isChecked() {
		return isChecked;
	}

	public boolean isBig() {
		return type <= 9 && type >= 0;
	}

	public void setType(int type2) {
		type = type2;
	}

	public void toBig(Scene getmMainScene, AndEngineSprise bigs) {
		if (getType() >= 10) {
			setType(getType() / 10);
		}

		update(getmMainScene);
	}

	public void update(Scene getmMainScene) {
		if (sprite != null) {
			if (getType() == -1) {
				sprite.stopAnimation();
				sprite.setCurrentTileIndex(2, 0);
			} else if (isBig()) {
				if (isChecked()) {
					sprite.setCurrentTileIndex(0, getType());
					sprite.animate(new long[] { 100, 100, 100 }, (getType() - 1) * 3, (getType() - 1) * 3 + 2, true);
				} else {
					sprite.stopAnimation();
					sprite.setCurrentTileIndex(0, getType() - 1);
				}

			} else {
				sprite.stopAnimation();
				sprite.setCurrentTileIndex(1, (getType() / 10) - 1);
			}
		}
	}
}