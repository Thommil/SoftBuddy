package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.actor.graphics.BitmapFontActor;
import com.thommil.libgdx.runtime.actor.graphics.StaticActor;
import com.thommil.libgdx.runtime.actor.physics.StaticBodyActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.BitmapFontBatchLayer;
import com.thommil.libgdx.runtime.layer.Layer;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.tools.RubeLoader;
import com.thommil.softbuddy.SharedResources;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.ChapterResources;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.common.layers.SkyLayer;
import com.thommil.softbuddy.levels.mountain.renderers.IntroRenderer;

public class Level01 extends Level{

    private static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    private Vector2 tmpVector = new Vector2();

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private float TITLE_FADE_START = 2f;
    private float TITLE_FADE_PAUSE = TITLE_FADE_START + 2f;
    private float TITLE_FADE_END = TITLE_FADE_PAUSE + 1f;

    private float SCROLL_DOWN_START = TITLE_FADE_END;
    private float SCROLL_DOWN_STEP = 3f;
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
    private SpriteBatchLayer backgroundLayer;
    private SpriteBatchLayer foregroundLayer;

    private IntroRenderer introRenderer;

    @Override
    public String getResourcesPath() {
        return RESOURCES_FILE;
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
    public void build(final ChapterResources chapterResources, final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {
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
        this.introRenderer = new IntroRenderer(1);
        this.buildBackground(chapterResources, softBuddyGameAPI, assetManager);
        this.buildForeground(chapterResources, softBuddyGameAPI, assetManager);
    }

    protected void buildBackground(final ChapterResources chapterResources, final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {
        this.skyLayer = new SkyLayer(Runtime.getInstance().getViewport(), true, SkyLayer.MIDNIGHT, -this.levelWorldWidth/2, -this.levelWorldHeight/4, this.levelWorldWidth, this.levelWorldHeight,this.levelWorldHeight/4);
        Runtime.getInstance().addLayer(this.skyLayer);

        final SharedResources.LabelDef titleLableDef = softBuddyGameAPI.getSharedResources().getLabelDef(Chapter.TITLE_LABEL);
        final BitmapFontActor titleFontActor = new BitmapFontActor(0, assetManager.get(titleLableDef.assetName, BitmapFont.class));
        titleFontActor.setText(chapterResources.getChapterDef().title);
        tmpVector.set(titleLableDef.position[0], titleLableDef.position[1]);
        ViewportLayout.adaptToScreen(SoftBuddyGameAPI.REFERENCE_SCREEN, tmpVector);
        titleFontActor.setPosition(tmpVector.x, tmpVector.y);
        titleFontActor.getBitmapFont().setColor(titleLableDef.color[0],titleLableDef.color[1],titleLableDef.color[2],0);
        this.bitmapFontBatchLayer = new BitmapFontBatchLayer(Runtime.getInstance().getViewport(), 1);
        this.bitmapFontBatchLayer.addActor(titleFontActor);
        Runtime.getInstance().addLayer(this.bitmapFontBatchLayer);

        this.backgroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1,this.introRenderer);
        final RubeLoader.ImageDef backgroundImageDef = this.levelResources.getImageDefinition(BACKGROUND_IMAGE_NAME);
        final StaticActor backgroundActor = new StaticActor(0
                ,new TextureSet(assetManager.get(backgroundImageDef.path, Texture.class))
                ,backgroundImageDef.center.x - backgroundImageDef.width/2
                ,backgroundImageDef.center.y - backgroundImageDef.height/2
                ,backgroundImageDef.width
                ,backgroundImageDef.height
                ,backgroundImageDef.regionX
                ,backgroundImageDef.regionY
                ,backgroundImageDef.regionX + backgroundImageDef.regionWidth
                ,backgroundImageDef.regionY + backgroundImageDef.regionHeight
                ,Color.WHITE.toFloatBits());

        this.backgroundLayer.addActor(backgroundActor);
        Runtime.getInstance().addLayer(this.backgroundLayer);
    }

    protected void buildForeground(final ChapterResources chapterResources, final SoftBuddyGameAPI softBuddyGameAPI, final AssetManager assetManager) {
        this.foregroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1, this.introRenderer);
        final RubeLoader.BodyDef foregroundBodyDef = this.levelResources.getBodyDefintion(FOREGROUND_BODY_NAME);
        final RubeLoader.ImageDef foregroundImageDef = this.levelResources.getImageDefinition(FOREGROUND_IMAGE_NAME);
        final StaticBodyActor foregroundStaticBodyActor = new StaticBodyActor(0
                ,new TextureSet(assetManager.get(foregroundImageDef.path, Texture.class))
                ,foregroundBodyDef.position.x + foregroundImageDef.center.x - foregroundImageDef.width/2
                ,foregroundBodyDef.position.y + foregroundImageDef.center.y - foregroundImageDef.height/2
                ,foregroundImageDef.width
                ,foregroundImageDef.height
                ,foregroundImageDef.regionX
                ,foregroundImageDef.regionY
                ,foregroundImageDef.regionX + foregroundImageDef.regionWidth
                ,foregroundImageDef.regionY + foregroundImageDef.regionHeight
                ,Color.WHITE.toFloatBits()) {
            @Override
            public BodyDef getDefinition() {
                return foregroundBodyDef;
            }

            @Override
            public Array<FixtureDef> getFixturesDefinition() {
                return Level01.this.levelResources.getFixturesDefinition(foregroundBodyDef.index);
            }
        };

        this.foregroundLayer.addActor(foregroundStaticBodyActor);
        Runtime.getInstance().addLayer(this.foregroundLayer);
    }


    @Override
    public void dispose() {
        Runtime.getInstance().removeLayer(this.foregroundLayer);
        Runtime.getInstance().removeLayer(this.backgroundLayer);
        Runtime.getInstance().removeLayer(this.bitmapFontBatchLayer);
        Runtime.getInstance().removeLayer(this.skyLayer);
        Runtime.getInstance().removeLayer(this.ticklayer);
        this.ticklayer.dispose();
        this.skyLayer.dispose();
        this.bitmapFontBatchLayer.dispose();
        this.backgroundLayer.dispose();
        this.foregroundLayer.dispose();
        this.introRenderer.dispose();
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
                    final float fadeTime = 1- ((TITLE_FADE_START - time) / TITLE_FADE_START);
                    ((BitmapFontActor)this.bitmapFontBatchLayer.getActor(0)).getBitmapFont().getColor().a = fadeTime;
                }
                else if(time > TITLE_FADE_PAUSE){
                    final float fadeTime = ((TITLE_FADE_END - time) / (TITLE_FADE_END - TITLE_FADE_PAUSE));
                    ((BitmapFontActor)this.bitmapFontBatchLayer.getActor(0)).getBitmapFont().getColor().a = fadeTime;
                }
                Runtime.getInstance().getViewport().getCamera().position.set(0,levelWorldWidth/2,0);
                Runtime.getInstance().getViewport().apply();
            }
            else if(time < SCROLL_DOWN_END){
                if(!this.bitmapFontBatchLayer.isHidden()) {
                    this.bitmapFontBatchLayer.hide();
                }
                final float scrollTime = ((SCROLL_DOWN_END - time) / (SCROLL_DOWN_END - SCROLL_DOWN_START));
                this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()-SCROLL_DOWN_STEP);
                Runtime.getInstance().getViewport().getCamera().position.set(0,(levelWorldWidth/2 * scrollTime),0);
                Runtime.getInstance().getViewport().apply();
            }
            else if(Runtime.getInstance().getViewport().getCamera().position.y != 0){
                Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
                Runtime.getInstance().getViewport().apply();
            }

            //this.skyLayer.setTime(time/10);
        }
    }

}
