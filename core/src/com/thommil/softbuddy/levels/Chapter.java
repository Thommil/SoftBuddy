package com.thommil.softbuddy.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thommil.softbuddy.levels.mountain.Mountain;

public abstract class Chapter implements Disposable{

    protected static final ChapterLoader chapterLoader = new ChapterLoader();

    private static Array<Chapter> chapters;
    protected Array<Level> levels;

    public static Array<Chapter> getChapters(){
        if(Chapter.chapters == null){
            Chapter.chapters = new Array<Chapter>(true,5);
            Chapter.chapters.add(new Mountain());
        }
        return Chapter.chapters;
    }

    public abstract void load(final AssetManager assetManager);

    public abstract void unload(final AssetManager assetManager);

    public abstract Array<Level> getLevels();

    @Override
    public void dispose() {
        if(this.levels != null) {
            for (final Level level : this.levels) {
                level.dispose();
            }
        }
    }
}
