package com.thommil.softbuddy.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.RubeLoader;

public class LevelLoader extends RubeLoader {

    public Vector2 getNormalOffset(){
        if(this.jsonRoot.has("customProperties")){
            for(final JsonValue jsonCustomProperty : this.jsonRoot.get("customProperties")){
                if(jsonCustomProperty.get("name").equals("normalOffset"));
                return new Vector2(jsonCustomProperty.get("vec2").getFloat("x"), jsonCustomProperty.get("vec2").getFloat("y"));
            }
        }
        return null;
    }
}
