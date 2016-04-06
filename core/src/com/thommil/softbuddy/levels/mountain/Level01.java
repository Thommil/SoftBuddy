package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.actor.graphics.BitmapFontActor;
import com.thommil.libgdx.runtime.layer.BitmapFontBatchLayer;
import com.thommil.libgdx.runtime.layer.Layer;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.common.layers.SkyLayer;

public class Level01 extends Level{

    private static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    private static final String TITLE = "01 - The landing";

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private float TITLE_FADE_START = 2f;
    private float TITLE_FADE_PAUSE = TITLE_FADE_START + 2f;
    private float TITLE_FADE_END = TITLE_FADE_PAUSE + 1f;

    private float SCROLL_DOWN_START = TITLE_FADE_END;
    private float SCROLL_DOWN_STEP = 2f;
    private float SCROLL_DOWN_END = SCROLL_DOWN_START + 4f;

    private float SAUCER_FLY_START = SCROLL_DOWN_END;
    private float SAUCER_FLY_END = SAUCER_FLY_START + 1f;

    private float SUNRISE_START = 11f;
    private float SUNRISE_END = SUNRISE_START + 2f;

    private float SAUCER_CRASH_START = 13f;
    private float SAUCER_CRASH_END = SAUCER_CRASH_START + 0.5f;

    private float PLAY_START = SAUCER_CRASH_END + 1f;

    private Layer ticklayer;

    private SkyLayer skyLayer;

    private BitmapFontBatchLayer bitmapFontBatchLayer;

    @Override
    public void load(AssetManager assetManager) {
        Level.levelLoader.parse(Gdx.files.internal(RESOURCES_FILE));
    }

    @Override
    public void unload(AssetManager assetManager) {

    }

    @Override
    public void reset() {
        this.time = 0;
    }

    @Override
    public void start() {
        this.tick(0);
    }

    @Override
    public void stop() {
        Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
        Runtime.getInstance().getViewport().apply();
    }

    @Override
    public void build(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {
        this.levelWorldWidth = Runtime.getInstance().getSettings().viewport.width;
        this.levelWorldHeight = Runtime.getInstance().getSettings().viewport.height * 2;
        this.ticklayer = new Layer(Runtime.getInstance().getViewport(),0) {
            @Override protected void onShow() {}
            @Override protected void onResize(int w, int h) {}
            @Override protected void onHide() {}

            @Override
            public void render(float deltaTime) {
                if(Level01.this.time < PLAY_START) {
                    tick(deltaTime);
                }
            }

            @Override public void dispose() {}
        };
        Runtime.getInstance().addLayer(this.ticklayer);
        super.build(softBuddyGameAPI,assetManager);
    }

    @Override
    protected void buildBackground(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {
        this.skyLayer = new SkyLayer(Runtime.getInstance().getViewport(), true, SkyLayer.MIDNIGHT, -this.levelWorldWidth/2, -this.levelWorldHeight/2, this.levelWorldWidth, this.levelWorldHeight);
        Runtime.getInstance().addLayer(this.skyLayer);

        final BitmapFontActor titleFontActor = new BitmapFontActor(0, assetManager.get(Mountain.RESOURCES_FONT_TITLE, BitmapFont.class));
        titleFontActor.setText(TITLE);
        titleFontActor.setPosition(200, 50);
        titleFontActor.getBitmapFont().setColor(1,1,1,0);
        this.bitmapFontBatchLayer = new BitmapFontBatchLayer(Runtime.getInstance().getViewport(), 1);
        this.bitmapFontBatchLayer.addActor(titleFontActor);
        Runtime.getInstance().addLayer(this.bitmapFontBatchLayer);
    }

    @Override
    protected void buildBuddy(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {

    }

    @Override
    protected void buildDynamic(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {

    }

    @Override
    protected void buildStatic(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {

    }

    @Override
    protected void buildHUD(final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {

    }

    @Override
    public void dispose() {
        Runtime.getInstance().removeLayer(this.ticklayer);
        Runtime.getInstance().removeLayer(this.skyLayer);
        Runtime.getInstance().removeLayer(this.bitmapFontBatchLayer);
        this.ticklayer.dispose();
        this.skyLayer.dispose();
        this.bitmapFontBatchLayer.dispose();
    }

    /*
    private int TITLE_FADE_START = 1000;
    private int TITLE_FADE_END = 3000;

    private int SCROLL_DOWN_START = 4000;
    private int SCROLL_DOWN_END = 8000;

    private int SAUCER_FLY_START = 9000;
    private int SAUCER_FLY_END = 10000;

    private int SUNRISE_START = 11000;
    private int SUNRISE_END = 13000;

    private int SAUCER_CRASH_START = 11000;
    private int SAUCER_CRASH_END = 12000;
     */

    public void tick(float deltaTime){
        time += deltaTime;
        //Gdx.app.log("",""+time);
        //Intro mode
        if(time < PLAY_START){
            //Scroll state
            if(time < SCROLL_DOWN_START){
                if(this.bitmapFontBatchLayer.isHidden()) {
                    this.bitmapFontBatchLayer.show();
                }
                if(time < TITLE_FADE_START){
                    final float fadeTime = 1-((TITLE_FADE_START - time) / TITLE_FADE_START);
                    ((BitmapFontActor)this.bitmapFontBatchLayer.getActor(0)).getBitmapFont().setColor(1,1,1,fadeTime);
                }
                else if(time > TITLE_FADE_PAUSE){
                    final float fadeTime = ((TITLE_FADE_END - time) / (TITLE_FADE_END - TITLE_FADE_PAUSE));
                    ((BitmapFontActor)this.bitmapFontBatchLayer.getActor(0)).getBitmapFont().setColor(1,1,1,fadeTime);
                }
                Runtime.getInstance().getViewport().getCamera().position.set(0,levelWorldWidth/4,0);
                Runtime.getInstance().getViewport().apply();
            }
            else if(time < SCROLL_DOWN_END){
                final float scrollTime = ((SCROLL_DOWN_END - time) / (SCROLL_DOWN_END - SCROLL_DOWN_START));
                this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()-SCROLL_DOWN_STEP);
                Runtime.getInstance().getViewport().getCamera().position.set(0,(levelWorldWidth/4 * scrollTime),0);
                Runtime.getInstance().getViewport().apply();
            }
            else if(Runtime.getInstance().getViewport().getCamera().position.y != 0){
                if(!this.bitmapFontBatchLayer.isHidden()) {
                    this.bitmapFontBatchLayer.hide();
                }
                Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
                Runtime.getInstance().getViewport().apply();
            }


            //this.skyLayer.setTime(time);
        }
    }

}
