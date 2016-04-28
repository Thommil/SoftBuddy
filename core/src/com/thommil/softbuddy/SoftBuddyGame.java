package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Game;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.Settings;
import com.thommil.libgdx.runtime.layer.ParticlesBatchLayer;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.tools.RuntimeProfiler;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.mountain.Mountain;
import com.thommil.softbuddy.screens.LoadingScreen;
import com.thommil.softbuddy.screens.MainScreen;
import com.thommil.softbuddy.screens.SplashScreen;


public class SoftBuddyGame extends Game implements SoftBuddyGameAPI {

	private Viewport viewport;

	private SharedResources sharedResources;

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
		settings.graphics.clearScreen = false;
		settings.viewport.type = Settings.Viewport.FILL;
		final float screenRatio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		// 4/3
		if(screenRatio <= (4f/3f)){
			settings.viewport.width = SAFE_WORLD_WIDTH;
			settings.viewport.height = WORLD_HEIGHT;
		}
		// 3/2
		else if(screenRatio <= (3f/2f)){
			settings.viewport.width = SAFE_WORLD_WIDTH;
			settings.viewport.height = SAFE_WORLD_HEIGHT;
		}
		// 16/10
		else if(screenRatio <= (16f/10f)){
			settings.viewport.width = WORLD_WIDTH;
			settings.viewport.height = WORLD_HEIGHT;
		}
		// 16/9
		else{
			settings.viewport.width = WORLD_WIDTH;
			settings.viewport.height = SAFE_WORLD_HEIGHT;
		}
		settings.physics.enabled = true;

		SpriteBatchLayer.setGlobalSize(SPRITE_BATCH_SIZE);
		ParticlesBatchLayer.setGlobalSize(PARTICLES_BATCH_SIZE);

		final FileHandleResolver resolver = new InternalFileHandleResolver();
		this.getAssetManager().setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		this.getAssetManager().setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		//settings.physics.debug = true;
	}

	@Override
	protected void onStart(final Viewport viewport) {
		this.viewport = viewport;
		this.splashScreen = new SplashScreen(viewport);
		showScreen(this.splashScreen);
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				initialize(viewport);
				showScreen(SoftBuddyGame.this.mainScreen);
				SoftBuddyGame.this.splashScreen.dispose();
			}
		},1);
	}

	private void initialize(final Viewport viewport){
		this.sharedResources = new SharedResources();
		this.sharedResources.load(SoftBuddyGame.this.getAssetManager());
		this.loadingScreen = new LoadingScreen(viewport, SoftBuddyGame.this, SoftBuddyGame.this.getAssetManager());
		this.mainScreen = new MainScreen(viewport, SoftBuddyGame.this, SoftBuddyGame.this.getAssetManager());
		this.loadingScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.mainScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.chapters = new Array<Chapter>(true, 5);
		this.chapters.add(new Mountain());
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

		RuntimeProfiler.profile();
	}

	@Override
	protected void onHideRuntime() {
		if(this.currentLevel != null){
			this.currentLevel.stop();
		}
	}

	@Override
	protected void onResize(int width, int height) {
		if(this.mainScreen != null) this.mainScreen.resize(width, height);
		if(this.loadingScreen != null) this.loadingScreen.resize(width, height);
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
			this.currentLevel.build(this.currentChapter.getChapterResources(), this, this.getAssetManager());
			Runtime.getInstance().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
	public SharedResources getSharedResources() {
		return this.sharedResources;
	}

	@Override
	public void startLevel(final int chapter, final int level, final boolean restart) {
		final Chapter selectedChapter = this.chapters.get(chapter);
		final Level selectedLevel = selectedChapter.getLevels().get(level);
		if(this.currentChapter != selectedChapter){
			this.mustReloadChapter = true;
			if(this.currentChapter != null){
				this.currentChapter.unload(this.getAssetManager());
				this.currentChapter.dispose();
			}
			this.currentChapter = selectedChapter;
			this.mustReloadLevel = true;
			this.mustRebuildLevel = true;
			if(this.currentLevel != null){
				this.currentLevel.unload(this.getAssetManager());
				this.currentLevel.dispose();
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
				this.currentLevel.dispose();
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
	public void restartLevel() {
		this.mustResetLevel = true;
		this.showScreen(Runtime.getInstance());
	}

	@Override
	public void pauseLevel() {
		this.onPause();
	}

	@Override
	public void resumeLevel() {
		this.showScreen(Runtime.getInstance());
	}

	@Override
	public void quit() {
		if(this.currentLevel != null){
			this.currentLevel.dispose();
		}
		if(this.currentChapter != null){
			this.currentChapter.dispose();
		}
		this.getAssetManager().dispose();
		Gdx.app.exit();
	}
}
