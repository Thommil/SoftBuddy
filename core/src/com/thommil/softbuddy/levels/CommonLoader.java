package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.thommil.libgdx.runtime.tools.RubeLoader;

public class CommonLoader extends RubeLoader {

    public static final String COMMON_RESOURCES_FILE = "chapters/resources.json";

    public CommonLoader() {
        this.parse(Gdx.files.internal(COMMON_RESOURCES_FILE));
    }
}
