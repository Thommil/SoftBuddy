package com.thommil.softbuddy.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.JSONLoader;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public class ChapterLoader extends JSONLoader {

    public Array<FontDef> getFontsDef(){
        final Array<FontDef> fontDefs = new Array<FontDef>(false,8);
        if(this.jsonRoot.has("fonts")){
            for(final JsonValue jsonFont : this.jsonRoot.get("fonts")){
                    final FontDef fontDef = new FontDef();
                    fontDef.name = jsonFont.getString("name");
                    fontDef.path = jsonFont.getString("path");
                    fontDef.size = convertFontSize(jsonFont.getInt("size"));
                    fontDefs.add(fontDef);
                }
            }
        return fontDefs;
    }

    public FontDef getFontDef(final String name){
        if(this.jsonRoot.has("fonts")){
            for(final JsonValue jsonFont : this.jsonRoot.get("fonts")){
                if(jsonFont.getString("name").equals(name)) {
                    final FontDef fontDef = new FontDef();
                    fontDef.name = jsonFont.getString("name");
                    fontDef.path = jsonFont.getString("path");
                    fontDef.size = convertFontSize(jsonFont.getInt("size"));
                    return fontDef;
                }
            }
        }
        return null;
    }

    private int convertFontSize(final int originalSize){
        return originalSize * Gdx.graphics.getWidth() / SoftBuddyGameAPI.TARGETED_WIDTH;
    }

    public static class FontDef{
        public String name;
        public String path;
        public int size;
    }
}
