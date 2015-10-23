package com.vnp.andengine;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;

import android.content.Context;

public abstract class AndEngineMusic {
	private Object backgroundMusic;

	public abstract void onLoadResources(org.anddev.andengine.engine.Engine mEngine, Context context);

	public final void onLoadResources(boolean isMusicManager, org.anddev.andengine.engine.Engine mEngine, Context context, String file, boolean loop) {
		MusicFactory.setAssetBasePath("mfx/");
		SoundFactory.setAssetBasePath("mfx/");
		try {
			if (isMusicManager) {
				backgroundMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), context, file);
				((Music) backgroundMusic).setLooping(loop);
			} else {
				backgroundMusic = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), context, file);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (backgroundMusic instanceof Music) {
			((Music) backgroundMusic).play();
		} else {
			((Sound) backgroundMusic).play();
		}
	}

	public void pause() {

		if (backgroundMusic instanceof Music) {
			if (((Music) backgroundMusic).isPlaying())
				((Music) backgroundMusic).pause();
		} else {
			
			// if (((Sound) backgroundMusic).isPlaying())
			((Sound) backgroundMusic).pause();
		}

	}

	public void resume() {

		if (backgroundMusic instanceof Music) {
			if (((Music) backgroundMusic).isPlaying())
				((Music) backgroundMusic).resume();
		} else {
			// if (((Sound) backgroundMusic).isPlaying())
			((Sound) backgroundMusic).resume();
		}
	}

	public boolean isPlaying() {

		if (backgroundMusic instanceof Music && ((Music) backgroundMusic).isPlaying()) {
			return ((Music) backgroundMusic).isPlaying();
		} else {
			// if (((Sound) backgroundMusic).isPlaying())
		}

		return false;
	}
}
