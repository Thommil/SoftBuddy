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

    private Array<LabelDef> labelsDef;
    private Array<ScreenDef> screensDef;
    private Array<WidgetDef> widgetsDef;
    private Configuration configuration;

    public SharedResources() {
        super();
        this.parse(Gdx.files.internal(RESOURCE_FILE));
        this.labelsDef = this.getLabelsDef();
        this.widgetsDef = this.getWidgetsDef();
        this.screensDef = this.getScreensDef();
        this.configuration = this.getConfiguration();
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

    public Configuration getConfiguration(){
        if(this.configuration == null){
            this.configuration = new Configuration();
            if (this.jsonRoot.has("configuration")) {
                if(this.jsonRoot.get("configuration").has("input")){
                    if(this.jsonRoot.get("configuration").get("input").has("keyboard")){
                        configuration.input.keyboard.leftKey = this.jsonRoot.get("configuration").get("input").get("keyboard").getInt("left");
                        configuration.input.keyboard.rightKey = this.jsonRoot.get("configuration").get("input").get("keyboard").getInt("right");
                        configuration.input.keyboard.force = this.jsonRoot.get("configuration").get("input").get("keyboard").getFloat("force");
                    }
                    if(this.jsonRoot.get("configuration").get("input").has("sensor")){
                        configuration.input.sensor.force = this.jsonRoot.get("configuration").get("input").get("sensor").getFloat("force");
                    }
                }
            }
        }

        return this.configuration;
    }

    public Array<LabelDef> getLabelsDef(){
        if(this.labelsDef == null) {
            this.labelsDef = new Array<LabelDef>(false, 8);
            if (this.jsonRoot.has("labels")) {
                for (final JsonValue jsonLabel : this.jsonRoot.get("labels")) {
                    final LabelDef labelDef = new LabelDef();
                    labelDef.name = jsonLabel.name();
                    labelDef.assetName = jsonLabel.name() + ".ttf";
                    labelDef.font = jsonLabel.getString("font");
                    labelDef.size = jsonLabel.getInt("size");
                    labelDef.position = jsonLabel.get("pos").asFloatArray();
                    labelDef.color = jsonLabel.get("color").asFloatArray();
                    this.labelsDef.add(labelDef);
                }
            }
        }
        return this.labelsDef;
    }

    public Array<ScreenDef> getScreensDef(){
        if(this.screensDef == null) {
            this.screensDef = new Array<ScreenDef>(false, 8);
            if (this.jsonRoot.has("screens")) {
                for (final JsonValue jsonScreen : this.jsonRoot.get("screens")) {
                    final ScreenDef screenDef = new ScreenDef();
                    screenDef.name = jsonScreen.name();
                    screenDef.backgroundPath = jsonScreen.getString("background");
                    if (jsonScreen.has("buttons")) {
                        screenDef.buttons = new ButtonDef[jsonScreen.get("buttons").size];
                        for (int index = 0; index < screenDef.buttons.length; index++) {
                            final JsonValue jsonButton = jsonScreen.get("buttons").get(index);
                            screenDef.buttons[index] = new ButtonDef();
                            screenDef.buttons[index].name = jsonButton.name();
                            screenDef.buttons[index].widget = this.getWidgetDef(jsonButton.getInt("widgetId"));
                            screenDef.buttons[index].size = jsonButton.get("size").asFloatArray();
                            screenDef.buttons[index].position = jsonButton.get("pos").asFloatArray();
                        }
                    }
                    this.screensDef.add(screenDef);
                }
            }
        }
        return this.screensDef;
    }

    public Array<WidgetDef> getWidgetsDef(){
        if(this.widgetsDef == null) {
            this.widgetsDef = new Array<WidgetDef>(false, 8);
            if (this.jsonRoot.has("widgets")) {
                for (final JsonValue jsonWidget : this.jsonRoot.get("widgets")) {
                    final WidgetDef widgetDef = new WidgetDef();
                    widgetDef.name = jsonWidget.name();
                    widgetDef.id = jsonWidget.getInt("id");
                    widgetDef.texturePath = jsonWidget.getString("texture");
                    widgetDef.region = jsonWidget.get("region").asIntArray();
                    this.widgetsDef.add(widgetDef);
                }
            }
        }
        return this.widgetsDef;
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

    public static class Configuration{

        public Input input;

        public Configuration(){
            input = new Input();
        }

        public static class Input{

            public Keyboard keyboard;
            public Sensor sensor;

            public Input(){
                keyboard = new Keyboard();
                sensor = new Sensor();
            }

            public static class Keyboard{
                public int leftKey;
                public int rightKey;
                public float force;
            }

            public static class Sensor{
                public float force;
            }
        }
    }
}
