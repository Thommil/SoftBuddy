package com.thommil.softbuddy.levels.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Level;

public class CaveLevel01 extends Level{

    public static final String RESOURCES_FILE = "chapters/cave/level01/resources.json";

    @Override
    public void load(AssetManager assetManager) {
        Gdx.app.log("","load cave 01");
    }

    @Override
    public void unload(AssetManager assetManager) {
        Gdx.app.log("","unload cave 01");
    }

    @Override
    public void build(final SoftBuddyGameAPI softBuddyGameAPI) {
        Gdx.app.log("", "build cave 01");

    }

    @Override
    public void reset() {
        Gdx.app.log("","reset cave 01");
    }

    @Override
    public void start() {
        Gdx.app.log("","start cave 01");
    }

    @Override
    public void stop() {
        Gdx.app.log("","stop cave 01");
    }

    @Override
    protected void buildBackground() {

    }

    @Override
    protected void buildBuddy() {

    }

    @Override
    protected void buildDynamic() {

    }

    @Override
    protected void buildStatic() {

    }

    @Override
    protected void buildHUD() {
        super.buildHUD();
    }

    @Override
    public void dispose() {
        Gdx.app.log("","dispose cave 01");
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
