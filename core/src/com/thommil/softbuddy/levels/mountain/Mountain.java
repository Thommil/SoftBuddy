package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;

public class Mountain extends Chapter {

    public static final String RESOURCES_FILE = "chapters/mountain/resources.json";

    @Override
    public void load(AssetManager assetManager) {


    }

    @Override
    public void unload(AssetManager assetManager) {

    }

    @Override
    public Array<Level> getLevels() {
        if(this.levels == null) {
            this.levels = new Array<Level>(false,1);
            this.levels.add(new Level01());
        }
        return this.levels;
    }
}
