package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.thommil.libgdx.runtime.tools.JSONLoader;
import com.thommil.libgdx.runtime.tools.SceneLoader;

public class ChapterResources extends SceneLoader {

    protected ChapterDef chapterDef;

    public ChapterDef getChapterDef(){
        if(this.chapterDef == null){
            this.chapterDef = new ChapterDef();
            this.chapterDef.title = this.jsonRoot.getString("title");
        }
        return this.chapterDef;
    }

    public void load(final String resourcesPath, final AssetManager assetManager){
        this.parse(Gdx.files.internal(resourcesPath));
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

    public static class ChapterDef{
        public String title;
    }
}
