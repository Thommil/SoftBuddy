package com.thommil.softbuddy.levels.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;

public class CaveChapter extends Chapter {

    public static final String RESOURCES_FILE = "chapters/cave/resources.json";

    @Override
    public void load(AssetManager assetManager) {
        Gdx.app.log("","load");

    }

    @Override
    public void unload(AssetManager assetManager) {
        Gdx.app.log("","unload");

    }

    @Override
    public Array<Level> getLevels() {
        if(this.levels == null) {
            this.levels = new Array<Level>(false,1);
            this.levels.add(new CaveLevel01());
        }
        return this.levels;
    }
}
