package com.thommil.softbuddy.levels;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public abstract class Level implements Disposable, InputProcessor{

    protected static final CommonLoader commonLoader = new CommonLoader();
    protected static final LevelLoader levelLoader = new LevelLoader();

    public abstract void load(final AssetManager assetManager);

    public abstract void unload(final AssetManager assetManager);

    public void build(final SoftBuddyGameAPI softBuddyGameAPI){
        this.buildBackground();
        this.buildBuddy();
        this.buildDynamic();
        this.buildStatic();
        this.buildHUD();
    }

    public abstract void reset();

    public abstract void start();

    public abstract void stop();

    protected abstract void buildBackground();

    protected abstract void buildBuddy();

    protected abstract void buildDynamic();

    protected abstract void buildStatic();

    protected void buildHUD(){

    }
}
