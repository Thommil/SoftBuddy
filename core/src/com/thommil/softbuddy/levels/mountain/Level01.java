package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Level;

public class Level01 extends Level{

    public static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    @Override
    public void load(AssetManager assetManager) {
        Level.levelLoader.parse(Gdx.files.internal(RESOURCES_FILE));
    }

    @Override
    public void unload(AssetManager assetManager) {


    }

    @Override
    public void build(final SoftBuddyGameAPI softBuddyGameAPI) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

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
