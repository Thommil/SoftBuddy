package com.thommil.softbuddy.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.levels.mountain.Mountain;

public abstract class Chapter implements Disposable{

    protected ChapterResources chapterResources;

    private static Array<Chapter> chapters;
    protected Array<Level> levels;

    public Chapter() {
        super();
        this.chapterResources = new ChapterResources(this.getResourcesPath());
    }

    public static Array<Chapter> getChapters(){
        if(Chapter.chapters == null){
            Chapter.chapters = new Array<Chapter>(true,5);
            Chapter.chapters.add(new Mountain());
        }
        return Chapter.chapters;
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
    public void dispose() {
        if(this.levels != null) {
            for (final Level level : this.levels) {
                level.dispose();
            }
        }
    }
}
