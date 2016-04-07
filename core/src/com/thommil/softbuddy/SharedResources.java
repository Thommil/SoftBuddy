package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.JSONLoader;


public class SharedResources extends JSONLoader{

    private static final String RESOURCE_FILE = "resources.json";

    private final Array<LabelDef> labelsDef;

    public SharedResources() {
        super();
        this.parse(Gdx.files.internal(RESOURCE_FILE));
        this.labelsDef = this.getLabelsDef();
    }

    public void load(final AssetManager assetManager){
        for(final LabelDef labelDef : labelsDef){
            final FreetypeFontLoader.FreeTypeFontLoaderParameter titleFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            titleFontParams.fontFileName = labelDef.font;
            titleFontParams.fontParameters.size = (int)(labelDef.size * Gdx.graphics.getHeight() / SoftBuddyGameAPI.REFERENCE_SCREEN.y);
            assetManager.load(labelDef.assetName, BitmapFont.class, titleFontParams);
        }
    }

    public Array<LabelDef> getLabelsDef(){
        final Array<LabelDef> labelDefs = new Array<LabelDef>(false,8);
        if(this.jsonRoot.has("labels")){
            for(final JsonValue jsonLabel : this.jsonRoot.get("labels")){
                final LabelDef labelDef = new LabelDef();
                labelDef.name = jsonLabel.name();
                labelDef.assetName = jsonLabel.name() + ".ttf";
                labelDef.font = jsonLabel.getString("font");
                labelDef.size = jsonLabel.getInt("size");
                labelDef.position = jsonLabel.get("pos").asFloatArray();
                labelDef.color = jsonLabel.get("color").asFloatArray();
                labelDefs.add(labelDef);
            }

        }
        return labelDefs;
    }

    public LabelDef getLabelDef(final String name){
        for(final LabelDef labelDef : this.labelsDef){
            if(labelDef.name.equals(name)){
                return labelDef;
            }
        }
        return null;
    }

    public static class LabelDef{
        public String name;
        public String assetName;
        public String font;
        public int size;
        public float[] position;
        public float[] color;
    }
}

/*

{
  "screens" :{
     "main": {
        "background": "screens/mainscreen_bg.png",
        "foreground": "screens/mainscreen_fg.png",
        "buttons": {
          "start": {
            "id": 1,
            "size": [4.4, 1.0],
            "pos": [0, 2],
            "region": [0, 0, 250, 56]
          },
          "quit": {
            "id": 2,
            "size": [4.4, 1.0],
            "pos": [0.0, -2.0],
            "region": [0, 116, 250, 62]
          }
        }
    }
  },

}

 */
