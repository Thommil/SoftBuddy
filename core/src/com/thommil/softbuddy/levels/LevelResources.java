package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.libgdx.runtime.tools.RubeLoader;

public class LevelResources extends RubeLoader {

    public LevelResources(final String resourcesPath){
        this.parse(Gdx.files.internal(resourcesPath));
    }

    public void load(final AssetManager assetManager){

    }

    public void unload(final AssetManager assetManager){

    }
}
