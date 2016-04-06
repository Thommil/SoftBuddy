package com.thommil.softbuddy.levels;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public abstract class Level implements Disposable, InputProcessor{

    protected static final LevelLoader levelLoader = new LevelLoader();

    public abstract void load(final AssetManager assetManager);

    public abstract void unload(final AssetManager assetManager);

    public void build(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager){
        this.buildBackground(softBuddyGameAPI, assetManager);
        this.buildBuddy(softBuddyGameAPI, assetManager);
        this.buildDynamic(softBuddyGameAPI, assetManager);
        this.buildStatic(softBuddyGameAPI, assetManager);
        this.buildHUD(softBuddyGameAPI, assetManager);
    }

    public abstract void reset();

    public abstract void start();

    public abstract void stop();

    protected abstract void buildBackground(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager);

    protected abstract void buildBuddy(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager);

    protected abstract void buildDynamic(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager);

    protected abstract void buildStatic(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager);

    protected abstract void buildHUD(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager);

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
