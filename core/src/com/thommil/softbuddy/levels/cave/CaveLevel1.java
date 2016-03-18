package com.thommil.softbuddy.levels.cave;

import com.badlogic.gdx.Gdx;
import com.thommil.softbuddy.levels.Level;

public class CaveLevel1 extends Level{

    @Override
    public void buildBackground() {
        Gdx.app.log("","buildBackground");
    }

    @Override
    public void buildBuddy() {
        Gdx.app.log("","buildBuddy");
    }

    @Override
    public void buildStatic() {
        Gdx.app.log("","buildStatic");
    }

    @Override
    public void buildObjects() {
        Gdx.app.log("","buildObjects");
    }

    @Override
    public void start() {
        Gdx.app.log("","start");
    }

    @Override
    public void restart() {
        Gdx.app.log("","restart");
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
