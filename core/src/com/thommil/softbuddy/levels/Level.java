package com.thommil.softbuddy.levels;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public abstract class Level implements Disposable, InputProcessor{

    public void build(final AssetManager assetManager){
        this.buildBackground(assetManager);
        this.buildBuddy(assetManager);
        this.buildDynamic(assetManager);
        this.buildStatic(assetManager);
        this.buildHUD(assetManager);
    }

    public abstract void reset();

    public abstract void start(final SoftBuddyGameAPI softBuddyGameAPI);

    public abstract void stop();

    protected abstract void buildBackground(final AssetManager assetManager);

    protected abstract void buildBuddy(final AssetManager assetManager);

    protected abstract void buildDynamic(final AssetManager assetManager);

    protected abstract void buildStatic(final AssetManager assetManager);

    protected void buildHUD(final AssetManager assetManager){

    }
}
