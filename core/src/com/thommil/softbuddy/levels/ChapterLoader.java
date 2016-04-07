package com.thommil.softbuddy.levels;

import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.JSONLoader;

public class ChapterLoader extends JSONLoader {

    public FontDef getFontDef(final String name){
        if(this.jsonRoot.has("fonts")){
            for(final JsonValue jsonFont : this.jsonRoot.get("fonts")){
                if(jsonFont.getString("name").equals(name)) {
                    final FontDef fontDef = new FontDef();
                    fontDef.name = jsonFont.getString("name");
                    fontDef.path = jsonFont.getString("path");
                    fontDef.size = jsonFont.getInt("size");
                    return fontDef;
                }
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
