package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Game;
import com.thommil.libgdx.runtime.Settings;
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game {

	public static final float WORLD_WIDTH = 16;
	public static final float WORLD_HEIGHT = 9;

	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private MainScreen mainScreen;

	@Override
	protected void onCreate(Settings settings) {
		settings.viewport.type = Settings.Viewport.STRECTCH;
		settings.viewport.width = WORLD_WIDTH;
		settings.viewport.height = WORLD_HEIGHT;
		settings.physics.enabled = true;
	}

	@Override
	protected void onStart(Viewport viewport) {
		splashScreen = new SplashScreen(viewport);
		loadingScreen = new LoadingScreen(viewport);
		mainScreen = new MainScreen(viewport);
		Gdx.input.setInputProcessor(mainScreen);
		showScreen(splashScreen);
	}

	@Override
	protected void onShowScreen(Screen screen) {
		if(screen == splashScreen){
			Timer.schedule(new Timer.Task() {
				@Override
				public void run() {
					showScreen(loadingScreen);
					splashScreen.dispose();
					//LOAD MainSCreen assets here
					showScreen(mainScreen);
				}
			},5);
		}
	}

	@Override
	protected void onResize(int width, int height) {

	}

	@Override
	protected void onResume() {
		showScreen(mainScreen);
	}

	@Override
	protected void onPause() {
		showScreen(mainScreen);
	}

	@Override
	protected void onDispose() {
		mainScreen.dispose();
		loadingScreen.dispose();
	}
}
