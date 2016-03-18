package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Game;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.Settings;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.cave.CaveChapter;
import com.thommil.softbuddy.resources.Global;
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game implements SoftBuddyGameAPI {

	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private MainScreen mainScreen;

	private Chapter currentChapter;
	private int currentLevel;
	private final Array<Chapter> chapters = new Array<Chapter>(false,6);
	private boolean rebuildLevel = true;

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
	protected void onStart(final Viewport viewport) {
		this.viewport = viewport;
		splashScreen = new SplashScreen(viewport);
		showScreen(splashScreen);
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				loadingScreen = new LoadingScreen(viewport);
				mainScreen = new MainScreen(viewport,SoftBuddyGame.this);
				chapters.add(new CaveChapter());
				//TODO others
				showScreen(mainScreen);
				splashScreen.dispose();
			}
		},1);
	}

	@Override
	protected void onShowRuntime() {
		final Level level = this.currentChapter.getLevels().get(this.currentLevel);
		if(rebuildLevel) {
			level.buildBackground();
			level.buildBuddy();
			level.buildStatic();
			level.buildObjects();
			rebuildLevel = false;
		}
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(level);
		level.start();
	}

	@Override
	protected void onHideRuntime() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	protected void onResize(int width, int height) {}

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
		this.showScreen(loadingScreen);
		this.setPlayerProgression(chapters.first(), 0);
		this.showScreen(Runtime.getInstance());
	}

	@Override
	public void resumeGame() {
		Gdx.app.log("", "resumeGame");
		//TODO find current chapter
		//this.showScreen(loadingScreen);
		//this.setPlayerProgression(chapters.first(), 0);
		this.showScreen(Runtime.getInstance());
	}

	public void setPlayerProgression(final Chapter chapter, final int level){
		Gdx.app.log("","setPlayerProgression");
		if(this.currentChapter != null){
			if(currentChapter != chapter){
				this.currentChapter.getLevels().get(this.currentLevel).dispose();
				this.currentChapter.dispose();
				this.currentChapter = chapter;
				this.currentLevel = level;
				this.currentChapter.loadResources(this.getAssetManager());
				rebuildLevel = true;
			}
			else if(currentLevel != level){
				this.currentChapter.getLevels().get(this.currentLevel).dispose();
				this.currentLevel = level;
				rebuildLevel = true;
			}
			else{
				this.currentChapter.getLevels().get(this.currentLevel).restart();
				rebuildLevel = false;
			}
		}
		else{
			this.currentChapter = chapter;
			this.currentLevel = level;
			this.currentChapter.loadResources(this.getAssetManager());
			rebuildLevel = true;
		}
	}

	@Override
	public void quitGame() {
		Gdx.app.exit();
	}
}
