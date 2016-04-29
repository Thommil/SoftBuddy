package com.thommil.softbuddy.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.levels.mountain.Mountain;

public abstract class Chapter implements Disposable{

    public static final String TITLE_LABEL = "chapter_title";

    protected static final ChapterResources chapterResources = new ChapterResources();

    protected Array<Level> levels;

    public void load(final AssetManager assetManager){
        this.chapterResources.load(this.getResourcesPath(), assetManager);
    }

    public void unload(final AssetManager assetManager){
        Chapter.chapterResources.unload(assetManager);
    }

    public abstract String getResourcesPath();

    public abstract Array<Level> getLevels();

    public ChapterResources getChapterResources(){
        return Chapter.chapterResources;
    }

    @Override
    public abstract void dispose();
}
