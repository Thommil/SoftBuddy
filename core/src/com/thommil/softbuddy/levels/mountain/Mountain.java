package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.ChapterResources;
import com.thommil.softbuddy.levels.Level;

public class Mountain extends Chapter {

    private static final String RESOURCES_FILE = "chapters/mountain/resources.json";

    @Override
    public String getResourcesPath() {
        return RESOURCES_FILE;
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
