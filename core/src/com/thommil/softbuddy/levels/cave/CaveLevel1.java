package com.thommil.softbuddy.levels.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.libgdx.runtime.tools.RuntimeProfiler;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Level;

public class CaveLevel1 extends Level{

    @Override
    public void build(final AssetManager assetManager) {
        Gdx.app.log("","build");
    }

    @Override
    public void start(final SoftBuddyGameAPI softBuddyGameAPI) {
        Gdx.app.log("","start");
        RuntimeProfiler.profile();
    }

    @Override
    public void reset() {
        Gdx.app.log("","reset");
    }

    protected void buildBackground(final AssetManager assetManager){

    }

    protected void buildBuddy(final AssetManager assetManager){

    }

    protected void buildDynamic(final AssetManager assetManager){

    }

    protected void buildStatic(final AssetManager assetManager){

    }

    @Override
    public void dispose() {
        Gdx.app.log("","dispose");
    }

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
