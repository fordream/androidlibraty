package com.vnp.core.common;

import java.io.File;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import dalvik.system.DexClassLoader;

public class VnpMultiDexClassLoader extends ClassLoader {
	private static VnpMultiDexClassLoader instance = null;
	private LinkedList<DexClassLoader> classLoaders;

	public static VnpMultiDexClassLoader getInstance() {
		if (instance == null) {
			instance = new VnpMultiDexClassLoader();
		}
		return instance;
	}

	private VnpMultiDexClassLoader() {
		super(ClassLoader.getSystemClassLoader());
		this.classLoaders = new LinkedList<DexClassLoader>();
	}

	public void install(Context context, String jarPath) {
		File dexOutputDir = context.getDir("dex", 0);
		DexClassLoader dexClassLoader = new DexClassLoader(jarPath,
				dexOutputDir.getAbsolutePath(), null, context.getClassLoader());

		if (!classLoaders.contains(dexClassLoader)) {
			this.classLoaders.addLast(dexClassLoader);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public Class<?> findClass(String className) throws ClassNotFoundException {
		Class<?> clazz = null;
		for (DexClassLoader classLoader : this.classLoaders) {
			try {
				clazz = classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				continue;
			}
			if (clazz != null) {
				return clazz;
			}
		}

		throw new ClassNotFoundException(className + " in loader " + this);
	}

	// --------------------------------
	public class LoadExample {
		String jarFile = "sdcard/jar.jar";

		public void main(Context context) {
			VnpMultiDexClassLoader.getInstance().install(context, jarFile);

			String className = "jp.co.xing.utaehon03.songs.VOL3";

			try {
				// Class<Fragment> fragmentClass = (Class<Fragment>)
				// MultiDexClassLoader.getInstance().loadClass(className);
				// Fragment fragment = fragmentClass.newInstance();
				// ft = getSupportFragmentManager().beginTransaction();
				// ft.add(R.id.dynamic_layout, fragment).commit();
				//
				Class<View> fragmentClass = (Class<View>) VnpMultiDexClassLoader
						.getInstance().loadClass(className);
			} catch (Exception e) {
			}
		}
	}

}
