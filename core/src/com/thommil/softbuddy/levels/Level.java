package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.events.TouchDispatcher;
import com.thommil.softbuddy.SharedResources;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public abstract class Level implements Disposable, InputProcessor{

    protected ChapterResources chapterResources;
    protected SoftBuddyGameAPI softBuddyGameAPI;
    protected AssetManager assetManager;

    protected static final LevelResources levelResources = new LevelResources();
    protected SharedResources.Configuration globalConfiguration;

    public enum Direction{
        IDLE,
        RIGHT,
        LEFT
    }

    protected Direction currentDirection = Direction.IDLE;
    protected float currentForce = 0.0f;

    public void load(final AssetManager assetManager){
        this.levelResources.load(this.getResourcesPath(), assetManager);
    }

    public void unload(final AssetManager assetManager){
        this.levelResources.unload(assetManager);
    }

    public abstract String getResourcesPath();

    public final void build(final ChapterResources chapterResources, final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager){
        this.globalConfiguration = softBuddyGameAPI.getSharedResources().getConfiguration();
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
        switch(keycode){
            case Input.Keys.BACK :
                this.softBuddyGameAPI.pauseLevel();
                break;
            default :
                if(keycode == this.globalConfiguration.input.keyboard.leftKey){
                    this.currentDirection = Direction.LEFT;
                    this.currentForce = -this.globalConfiguration.input.keyboard.force;
                }
                else if(keycode == this.globalConfiguration.input.keyboard.rightKey){
                    this.currentDirection = Direction.RIGHT;
                    this.currentForce = this.globalConfiguration.input.keyboard.force;
                }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        this.currentDirection = Direction.IDLE;
        this.currentForce = 0;
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        /*switch(Gdx.app.getType()){
                    case Application.ApplicationType.Desktop:
                        break;
                }

                if(Gdx.input.isKeyPressed(this.globalConfiguration.input.keyboard.leftKey)){
                    this.tmpVector.set(-this.globalConfiguration.input.keyboard.force, 0);
                }
                else if(Gdx.input.isKeyPressed(this.globalConfiguration.input.keyboard.rightKey)){
                    this.tmpVector.set(this.globalConfiguration.input.keyboard.force, 0);
                }
                else {
                    this.tmpVector.set(-Gdx.input.getPitch() * this.globalConfiguration.input.sensor.force, 0);
                }
                this.softBuddyActor.getParticleGroup().applyForce(Level01.this.tmpVector);
                break;*/

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.currentDirection = Direction.IDLE;
        this.currentForce = 0;
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
