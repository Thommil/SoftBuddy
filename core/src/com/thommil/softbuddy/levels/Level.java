package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.events.TouchDispatcher;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public abstract class Level implements Disposable, InputProcessor{

    protected LevelResources levelResources;
    protected ChapterResources chapterResources;
    protected SoftBuddyGameAPI softBuddyGameAPI;
    protected AssetManager assetManager;

    public Level() {
        super();
        this.levelResources = new LevelResources(this.getResourcesPath());
    }

    public void load(final AssetManager assetManager){
        this.levelResources.load(assetManager);
    }

    public void unload(final AssetManager assetManager){
        this.levelResources.unload(assetManager);
    }

    public abstract String getResourcesPath();

    public final void build(final ChapterResources chapterResources, final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager){
        this.chapterResources = chapterResources;
        this.softBuddyGameAPI = softBuddyGameAPI;
        this.assetManager = assetManager;
        this.build();
    }

    protected abstract void build();

    public abstract void reset();

    public abstract void start();

    public abstract void stop();

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            this.softBuddyGameAPI.pauseLevel();
        }
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
