package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.RubeLoader;

public class CommonLoader extends RubeLoader {

    public static final String COMMON_RESOURCES_FILE = "chapters/resources.json";

    public CommonLoader() {
        this.parse(Gdx.files.internal(COMMON_RESOURCES_FILE));
    }

    public Vector2 getFonts(){
        if(this.rubeScene.has("fonts")){
            for(final JsonValue jsonCustomProperty : this.rubeScene.get("customProperties")){
                if(jsonCustomProperty.get("name").equals("normalOffset"));
                return new Vector2(jsonCustomProperty.get("vec2").getFloat("x"), jsonCustomProperty.get("vec2").getFloat("y"));
            }
        }
        return null;
    }

    public static class FontDef{
        public String name;
        public String path;
        public int size;
    }
}
