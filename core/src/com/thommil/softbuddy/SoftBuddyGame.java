package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Game;
import com.thommil.libgdx.runtime.Settings;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game {

	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = 10;

	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private MainScreen mainScreen;

	private Viewport viewport;

	@Override
	protected void onCreate(Settings settings) {
		settings.viewport.type = Settings.Viewport.FILL;
		settings.viewport.width = WORLD_WIDTH;
		settings.viewport.height = WORLD_HEIGHT;
		settings.physics.enabled = true;

		SpriteBatchLayer.setGlobalSize(10);
	}

	@Override
	protected void onStart(Viewport viewport) {
		this.viewport = viewport;
		splashScreen = new SplashScreen(viewport);
		showScreen(splashScreen);
	}

	@Override
	protected void onShowScreen(Screen screen) {
		if(screen == splashScreen){
			Timer.schedule(new Timer.Task() {
				@Override
				public void run() {
					loadingScreen = new LoadingScreen(viewport);
					mainScreen = new MainScreen(viewport);
					showScreen(mainScreen);
				}
			},1);
		}
	}

	@Override
	protected void onResize(int width, int height) {

	}

	@Override
	protected void onResume() {
		if(mainScreen != null) {
			showScreen(mainScreen);
		}
	}

	@Override
	protected void onPause() {
		if(mainScreen != null) {
			showScreen(mainScreen);
		}
	}

	@Override
	protected void onDispose() {
		mainScreen.dispose();
		loadingScreen.dispose();
	}
}
