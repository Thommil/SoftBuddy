package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.libgdx.runtime.tools.JSONLoader;

public class ChapterResources extends JSONLoader {

    protected ChapterDef chapterDef;

    public ChapterResources(final String resourcesPath){
        this.parse(Gdx.files.internal(resourcesPath));
    }

    public ChapterDef getChapterDef(){
        if(this.chapterDef == null){
            this.chapterDef = new ChapterDef();
            this.chapterDef.title = this.jsonRoot.getString("title");
        }
        return this.chapterDef;
    }

    public void load(final AssetManager assetManager){

    }

    public void unload(final AssetManager assetManager){

    }

    public static class ChapterDef{
        public String title;
    }
}
