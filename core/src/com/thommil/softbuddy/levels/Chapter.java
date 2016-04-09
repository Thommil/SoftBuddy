package com.thommil.softbuddy.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.levels.mountain.Mountain;

public abstract class Chapter implements Disposable{

    public static final String TITLE_LABEL = "chapter_title";

    protected ChapterResources chapterResources;

    protected Array<Level> levels;

    public Chapter() {
        super();
        this.chapterResources = new ChapterResources(this.getResourcesPath());
    }

    public void load(final AssetManager assetManager){
        this.chapterResources.load(assetManager);
    }

    public void unload(final AssetManager assetManager){
        this.chapterResources.unload(assetManager);
    }

    public abstract String getResourcesPath();

    public abstract Array<Level> getLevels();

    public ChapterResources getChapterResources(){
        return this.chapterResources;
    }

    @Override
    public abstract void dispose();
}
