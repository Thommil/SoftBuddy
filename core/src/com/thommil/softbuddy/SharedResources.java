package com.thommil.softbuddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.thommil.libgdx.runtime.tools.JSONLoader;


public class SharedResources extends JSONLoader{

    private static final String RESOURCE_FILE = "resources.json";

    private final Array<LabelDef> labelsDef;
    private final Array<ScreenDef> screensDef;
    private final Array<WidgetDef> widgetsDef;

    public SharedResources() {
        super();
        this.parse(Gdx.files.internal(RESOURCE_FILE));
        this.labelsDef = this.getLabelsDef();
        this.widgetsDef = this.getWidgetsDef();
        this.screensDef = this.getScreensDef();
    }

    public void load(final AssetManager assetManager){
        for(final LabelDef labelDef : this.labelsDef){
            final FreetypeFontLoader.FreeTypeFontLoaderParameter titleFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            titleFontParams.fontFileName = labelDef.font;
            titleFontParams.fontParameters.size = (int)(labelDef.size * Gdx.graphics.getHeight() / SoftBuddyGameAPI.REFERENCE_SCREEN.y);
            assetManager.load(labelDef.assetName, BitmapFont.class, titleFontParams);
        }
        for(final ScreenDef screenDef : this.screensDef){
            assetManager.load(screenDef.backgroundPath, Texture.class);
        }
        for(final WidgetDef widgetDef : this.widgetsDef){
            assetManager.load(widgetDef.texturePath, Texture.class);
        }
        assetManager.finishLoading();
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
    public Array<ScreenDef> getScreensDef(){
        final Array<ScreenDef> screenDefs = new Array<ScreenDef>(false,8);
        if(this.jsonRoot.has("screens")){
            for(final JsonValue jsonScreen : this.jsonRoot.get("screens")){
                final ScreenDef screenDef = new ScreenDef();
                screenDef.name = jsonScreen.name();
                screenDef.backgroundPath = jsonScreen.getString("background");
                if(jsonScreen.has("buttons")) {
                    screenDef.buttons = new ButtonDef[jsonScreen.get("buttons").size];
                    for(int index=0; index < screenDef.buttons.length; index++){
                        final JsonValue jsonButton = jsonScreen.get("buttons").get(index);
                        screenDef.buttons[index] = new ButtonDef();
                        screenDef.buttons[index].name = jsonButton.name();
                        screenDef.buttons[index].widget = this.getWidgetDef(jsonButton.getInt("widgetId"));
                        screenDef.buttons[index].size = jsonButton.get("size").asFloatArray();
                        screenDef.buttons[index].position = jsonButton.get("pos").asFloatArray();
                    }
                }
                screenDefs.add(screenDef);
            }
        }
        return screenDefs;
    }

    public Array<WidgetDef> getWidgetsDef(){
        final Array<WidgetDef> widgetDefs = new Array<WidgetDef>(false,8);
        if(this.jsonRoot.has("widgets")){
            for(final JsonValue jsonWidget : this.jsonRoot.get("widgets")){
                final WidgetDef widgetDef = new WidgetDef();
                widgetDef.name = jsonWidget.name();
                widgetDef.id = jsonWidget.getInt("id");
                widgetDef.texturePath = jsonWidget.getString("texture");
                widgetDef.region = jsonWidget.get("region").asIntArray();
                widgetDefs.add(widgetDef);
            }
        }
        return widgetDefs;
    }

    public LabelDef getLabelDef(final String name){
        for(final LabelDef labelDef : this.labelsDef){
            if(labelDef.name.equals(name)){
                return labelDef;
            }
        }
        return null;
    }

    public ScreenDef getScreenDef(final String name){
        for(final ScreenDef screenDef : this.screensDef){
            if(screenDef.name.equals(name)){
                return screenDef;
            }
        }
        return null;
    }

    public WidgetDef getWidgetDef(final String name){
        for(final WidgetDef widgetDef : this.widgetsDef){
            if(widgetDef.name.equals(name)){
                return widgetDef;
            }
        }
        return null;
    }

    public WidgetDef getWidgetDef(final int id){
        for(final WidgetDef widgetDef : this.widgetsDef){
            if(widgetDef.id == id){
                return widgetDef;
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

    public static class ScreenDef{
        public String name;
        public String backgroundPath;
        public ButtonDef[] buttons;
    }

    public static class WidgetDef{
        public String name;
        public int id;
        public String texturePath;
        public int[] region;
    }

    public static class ButtonDef{
        public String name;
        public WidgetDef widget;
        public float[] size;
        public float[] position;
    }
}
