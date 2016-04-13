package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.thommil.libgdx.runtime.tools.SceneLoader;

public class LevelResources extends SceneLoader {

    public LevelResources(final String resourcesPath){
        this.parse(Gdx.files.internal(resourcesPath));
    }

    public void load(final AssetManager assetManager){
        for(final ImageDef imageDef : this.getImagesDefinition()){
            assetManager.load(imageDef.path, Texture.class);
        }
    }

    public void unload(final AssetManager assetManager){
        for(final ImageDef imageDef : this.getImagesDefinition()){
            if(assetManager.isLoaded(imageDef.path)) {
                assetManager.unload(imageDef.path);
            }
        }
    }
}
