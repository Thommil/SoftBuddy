package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Game;
import com.thommil.libgdx.runtime.Settings;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.softbuddy.resources.Global;
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game implements SoftBuddyGameAPI {

	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private MainScreen mainScreen;

	private Viewport viewport;

	@Override
	protected void onCreate(Settings settings) {
		settings.viewport.type = Global.VIEWPORT_TYPE;
		settings.viewport.width = Global.WORLD_WIDTH;
		settings.viewport.height = Global.WORLD_HEIGHT;
		settings.physics.enabled = true;

		SpriteBatchLayer.setGlobalSize(Global.SPRITE_BATCH_SIZE);
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
					mainScreen = new MainScreen(viewport,SoftBuddyGame.this);
					showScreen(mainScreen);
					splashScreen.dispose();
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
		if(mainScreen != null) mainScreen.dispose();
		if(loadingScreen != null) loadingScreen.dispose();
	}

	@Override
	public void newGame() {
		Gdx.app.log("", "newGame");
	}

	@Override
	public void resumeGame() {
		Gdx.app.log("", "resumeGame");
	}

	@Override
	public void quitGame() {
		Gdx.app.exit();
	}
}
