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
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game implements SoftBuddyGameAPI {

	private Viewport viewport;

	private SplashScreen splashScreen;
	private LoadingScreen loadingScreen;
	private MainScreen mainScreen;

	private Array<Chapter> chapters;
	private Chapter currentChapter;
	private Level currentLevel;

	private boolean mustReloadChapter = true;
	private boolean mustReloadLevel = true;
	private boolean mustRebuildLevel = true;
	private boolean mustResetLevel = true;

	@Override
	protected void onCreate(Settings settings) {
		settings.viewport.type = Resources.VIEWPORT_TYPE;
		settings.viewport.width = Resources.WORLD_WIDTH;
		settings.viewport.height = Resources.WORLD_HEIGHT;
		settings.physics.enabled = true;

		SpriteBatchLayer.setGlobalSize(Resources.SPRITE_BATCH_SIZE);
	}

	@Override
	protected void onStart(final Viewport viewport) {
		this.viewport = viewport;
		this.splashScreen = new SplashScreen(viewport);
		this.loadingScreen = new LoadingScreen(viewport, this);
		this.mainScreen = new MainScreen(viewport,this);
		showScreen(this.splashScreen);
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				SoftBuddyGame.this.chapters = Chapter.getChapters();
				showScreen(SoftBuddyGame.this.mainScreen);
				SoftBuddyGame.this.splashScreen.dispose();
			}
		},1);
	}

	@Override
	protected void onShowRuntime() {
		if(this.mustResetLevel){
			this.currentLevel.reset();
			this.mustResetLevel = false;
		}
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(this.currentLevel);
		this.currentLevel.start();
	}

	@Override
	protected void onHideRuntime() {
		if(this.currentLevel != null){
			this.currentLevel.stop();
		}
	}

	@Override
	protected void onResize(int width, int height) {
		this.mainScreen.resize(width, height);
		this.loadingScreen.resize(width, height);
	}

	@Override
	protected void onResume() {
		if(this.mainScreen != null) {
			showScreen(this.mainScreen);
		}
	}

	@Override
	protected void onPause() {
		if(this.mainScreen != null) {
			showScreen(this.mainScreen);
		}
	}

	@Override
	protected void onDispose() {
		if(this.mainScreen != null) this.mainScreen.dispose();
		if(this.loadingScreen != null) this.loadingScreen.dispose();
	}

	@Override
	public void load() {
		if (this.mustReloadChapter) {
			this.currentChapter.load(this.getAssetManager());
			this.mustReloadChapter = false;
		}
		if (this.mustReloadLevel){
			this.currentLevel.load(this.getAssetManager());
			this.mustReloadLevel = false;
		}
		if(this.getAssetManager().getProgress() < 1f){
			this.getAssetManager().update();
		}
		else if(this.mustRebuildLevel) {
			this.currentLevel.build(this);
			this.mustRebuildLevel = false;
		}
	}

	@Override
	public float getLoadingProgress() {
		return this.getAssetManager().getProgress() * 0.9f + (this.mustRebuildLevel ? 0.0f : 0.1f);
	}

	@Override
	public void onLoaded() {
		this.showScreen(Runtime.getInstance());
	}

	@Override
	public void startLevel(final int chapter, final int level, final boolean restart) {
		final Chapter selectedChapter = this.chapters.get(chapter);
		final Level selectedLevel = selectedChapter.getLevels().get(level);
		if(this.currentChapter != selectedChapter){
			this.mustReloadChapter = true;
			if(this.currentChapter != null){
				this.currentChapter.unload(this.getAssetManager());
			}
			this.currentChapter = selectedChapter;
			this.mustReloadLevel = true;
			this.mustRebuildLevel = true;
			if(this.currentLevel != null){
				this.currentLevel.unload(this.getAssetManager());
			}
			this.currentLevel = selectedLevel;
			this.mustResetLevel = true;
			this.showScreen(this.loadingScreen);
		}
		else if(this.currentLevel != selectedLevel){
			this.mustReloadChapter = false;
			this.mustReloadLevel = true;
			this.mustRebuildLevel = true;
			if(this.currentLevel != null){
				this.currentLevel.unload(this.getAssetManager());
			}
			this.currentLevel = selectedLevel;
			this.mustResetLevel = true;
			this.showScreen(this.loadingScreen);
		}
		else{
			this.mustReloadLevel = false;
			this.mustRebuildLevel = true;
			this.mustResetLevel = restart;
			this.showScreen(Runtime.getInstance());
		}
	}

	@Override
	public void quit() {
		Gdx.app.exit();
	}
}
