package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.actor.graphics.BitmapFontActor;
import com.thommil.libgdx.runtime.actor.graphics.ParticleEffectActor;
import com.thommil.libgdx.runtime.actor.graphics.SpriteActor;
import com.thommil.libgdx.runtime.actor.graphics.StaticActor;
import com.thommil.libgdx.runtime.actor.physics.StaticBodyActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.BitmapFontBatchLayer;
import com.thommil.libgdx.runtime.layer.Layer;
import com.thommil.libgdx.runtime.layer.ParticlesEffectBatchLayer;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.tools.SceneLoader;
import com.thommil.softbuddy.SharedResources;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.common.layers.SkyLayer;
import com.thommil.softbuddy.levels.mountain.renderers.IntroRenderer;

public class Level01 extends Level{

    private static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    protected final static String FLYING_SAUCER_ID = "flying_saucer";

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private final static int STATE_INIT = 0;
    private final static int STATE_TITLE = 1;
    private final static int STATE_SCROLL_DOWN = 2;
    private final static int STATE_SAUCER_FLY = 3;
    private final static int STATE_SUNRISE = 4;
    private final static int STATE_SAUCER_CRASH = 5;
    private final static int STATE_PLAY = 6;

    private int state = STATE_TITLE;

    private static final float TITLE_FADE_START = 2f;
    private static final float TITLE_FADE_PAUSE = TITLE_FADE_START + 1f;
    private static final float TITLE_FADE_END = TITLE_FADE_PAUSE + 1f;

    private static final float SCROLL_DOWN_START = TITLE_FADE_END;
    private static final float SCROLL_DOWN_STEP = 3f;
    private static final float SCROLL_DOWN_END = SCROLL_DOWN_START + 4f;

    private static final float SAUCER_FLY_START = SCROLL_DOWN_END + 2f;
    private static final float[] SAUCER_FLY_AMBIENT_COLOR = new float[]{0.3f, 0.3f, 0.3f, 1f};
    private static final float[] SAUCER_FLY_SPOT_COLOR = new float[]{0,0,1f,1f};
    private static final float[] SAUCER_FLY_LEFT = new float[]{-20f, 3f};
    private static final float[] SAUCER_FLY_RIGHT = new float[]{20f, 3f};
    private static final float[] SAUCER_FLY_SPOT_FALLOFF = new float[]{0.01f,0.1f,1f};
    private static final float SAUCER_FLY_END = SAUCER_FLY_START + 5f;

    private static final float SUNRISE_START = SAUCER_FLY_END + 2f;
    private static final float[] SUNRISE_AMBIENT_COLOR_START = new float[]{0.3f, 0.3f, 0.3f, 1f};
    private static final float[] SUNRISE_AMBIENT_COLOR_END = new float[]{0.6f, 0.6f, 0.6f, 1f};
    private static final float[] SUNRISE_SPOT_COLOR = new float[]{1f,1f,1f,1f};
    private static final float[] SUNRISE_BOTTOM = new float[]{40f, -10f};
    private static final float[] SUNRISE_TOP = new float[]{-40f, 30f};
    private static final float[] SUNRISE_SPOT_FALLOFF = new float[]{1f,0f,0f};
    private static final float SUNRISE_END = SUNRISE_START + 10f;

    private static final float SAUCER_CRASH_START = 100f;
    private static final float SAUCER_CRASH_END = SAUCER_CRASH_START + 0.5f;

    private static final float PLAY_START = SAUCER_CRASH_END + 1f;

    private Vector2 tmpVectorFrom;
    private Vector2 tmpVectorTo;

    //Tick
    private Layer ticklayer;

    //Sky
    private SkyLayer skyLayer;

    //Background & Foreground
    private SpriteBatchLayer backgroundLayer;
    private SpriteBatchLayer foregroundLayer;
    private IntroRenderer introRenderer;

    //Sprites
    private SpriteBatchLayer saucerLayer;
    private SpriteActor flyingSaucerActor;

    //Particles
    private ParticlesEffectBatchLayer particlesEffectBatchLayer;
    private ParticleEffectActor flyingSaucerParticlesActor;
    private ParticleEffect flyingSaucerParticlesEffect;
    private ParticleEffectActor crashedSaucerParticlesActor;
    private ParticleEffect crashedSaucerParticlesEffect;

    //HUD
    private BitmapFontBatchLayer bitmapFontBatchLayer;
    private BitmapFontActor titleFontActor;

    @Override
    public String getResourcesPath() {
        return RESOURCES_FILE;
    }

    @Override
    public void reset() {
        this.state = STATE_INIT;
        this.time = 0;
        this.tick(0);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
        Runtime.getInstance().getViewport().apply();
    }

    @Override
    protected void build() {
        this.tmpVectorFrom = new Vector2();
        this.tmpVectorTo = new Vector2();
        this.levelWorldWidth = Runtime.getInstance().getSettings().viewport.width;
        this.levelWorldHeight = Runtime.getInstance().getSettings().viewport.height * 2;
        this.ticklayer = new Layer(Runtime.getInstance().getViewport(),0) {
            @Override protected void onShow() {}
            @Override protected void onResize(int width, int height) {
                Level01.this.introRenderer.setScreenSize(width, height);
            }
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
        this.buildBackground();
        this.buildForeground();
        this.buildHUD();
    }

    protected void buildBackground() {
        this.skyLayer = new SkyLayer(Runtime.getInstance().getViewport(), true, SkyLayer.MIDNIGHT, -this.levelWorldWidth/2, -this.levelWorldHeight/4, this.levelWorldWidth, this.levelWorldHeight,this.levelWorldHeight/4);
        Runtime.getInstance().addLayer(this.skyLayer);

        this.backgroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1,this.introRenderer);
        final SceneLoader.ImageDef backgroundImageDef = this.levelResources.getImageDefinition(BACKGROUND_IMAGE_NAME);
        final Texture backgroundTexture = this.assetManager.get(backgroundImageDef.path, Texture.class);
        this.introRenderer.setNormalOffset(backgroundImageDef.normalOffset.x/backgroundTexture.getWidth(),backgroundImageDef.normalOffset.y/backgroundTexture.getHeight());
        final StaticActor backgroundActor = new StaticActor(0
                ,new TextureSet(backgroundTexture)
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

    protected void buildForeground() {
        this.foregroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1, this.introRenderer);
        final SceneLoader.BodyDef foregroundBodyDef = this.levelResources.getBodyDefintion(FOREGROUND_BODY_NAME);
        final SceneLoader.ImageDef foregroundImageDef = this.levelResources.getImageDefinition(FOREGROUND_IMAGE_NAME);
        final StaticBodyActor foregroundStaticBodyActor = new StaticBodyActor(0
                ,new TextureSet(this.assetManager.get(foregroundImageDef.path, Texture.class))
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

        this.saucerLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1);
        final SceneLoader.ImageDef flyingSaucerImageDef = this.levelResources.getImageDefinition(FLYING_SAUCER_ID);
        this.flyingSaucerActor = new SpriteActor(FLYING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(flyingSaucerImageDef.path, Texture.class))
                ,flyingSaucerImageDef.regionX
                ,flyingSaucerImageDef.regionY
                ,flyingSaucerImageDef.regionWidth
                ,flyingSaucerImageDef.regionHeight);
        flyingSaucerActor.setSize(flyingSaucerImageDef.width, flyingSaucerImageDef.height);
        flyingSaucerActor.setOriginCenter();
        Runtime.getInstance().addLayer(this.saucerLayer);

        this.particlesEffectBatchLayer = new ParticlesEffectBatchLayer(Runtime.getInstance().getViewport(),1);
        this.flyingSaucerParticlesEffect = new ParticleEffect();
        this.flyingSaucerParticlesEffect.load(Gdx.files.internal(this.levelResources.getParticlesEffectDefinition(FLYING_SAUCER_ID).path), this.levelResources.getTextureAtlas(this.assetManager));
        this.flyingSaucerParticlesActor = new ParticleEffectActor(FLYING_SAUCER_ID.hashCode(), this.flyingSaucerParticlesEffect,1);
        Runtime.getInstance().addLayer(this.particlesEffectBatchLayer);
    }

    private void buildHUD(){
        final SharedResources.LabelDef titleLableDef = this.softBuddyGameAPI.getSharedResources().getLabelDef(Chapter.TITLE_LABEL);
        this.titleFontActor = new BitmapFontActor(0, this.assetManager.get(titleLableDef.assetName, BitmapFont.class));
        titleFontActor.setText(this.chapterResources.getChapterDef().title);
        tmpVectorFrom.set(titleLableDef.position[0], titleLableDef.position[1]);
        ViewportLayout.adaptToScreen(SoftBuddyGameAPI.REFERENCE_SCREEN, tmpVectorFrom);
        titleFontActor.setPosition(tmpVectorFrom.x, tmpVectorFrom.y);
        titleFontActor.getBitmapFont().setColor(titleLableDef.color[0],titleLableDef.color[1],titleLableDef.color[2],0);
        this.bitmapFontBatchLayer = new BitmapFontBatchLayer(Runtime.getInstance().getViewport(), 1);
        Runtime.getInstance().addLayer(this.bitmapFontBatchLayer);
    }

    @Override
    public void dispose() {
        Runtime.getInstance().removeLayer(this.ticklayer);
        Runtime.getInstance().removeLayer(this.skyLayer);
        Runtime.getInstance().removeLayer(this.foregroundLayer);
        Runtime.getInstance().removeLayer(this.backgroundLayer);
        Runtime.getInstance().removeLayer(this.saucerLayer);
        Runtime.getInstance().removeLayer(this.particlesEffectBatchLayer);
        Runtime.getInstance().removeLayer(this.bitmapFontBatchLayer);

        this.ticklayer.dispose(true);
        this.skyLayer.dispose(true);
        this.backgroundLayer.dispose(true);
        this.foregroundLayer.dispose(true);
        this.saucerLayer.dispose(true);
        this.particlesEffectBatchLayer.dispose(true);
        this.bitmapFontBatchLayer.dispose(true);

        this.flyingSaucerActor.dispose();
        this.flyingSaucerParticlesActor.dispose();
        this.flyingSaucerParticlesEffect.dispose();
        //this.crashedSaucerParticlesActor.dispose();
        //this.crashedSaucerParticlesEffect.dispose();
        this.introRenderer.dispose();

        this.ticklayer = null;
        this.skyLayer = null;
        this.backgroundLayer = null;
        this.foregroundLayer = null;
        this.saucerLayer = null;
        this.bitmapFontBatchLayer = null;

        this.flyingSaucerActor = null;
        this.flyingSaucerParticlesActor = null;
        this.flyingSaucerParticlesEffect = null;
        //this.crashedSaucerParticlesActor = null;
        //this.crashedSaucerParticlesEffect = null;
        this.introRenderer = null;
        this.tmpVectorFrom = null;
        this.tmpVectorTo = null;
    }

    public void tick(float deltaTime){
        time += deltaTime;
        switch(state){
            case STATE_INIT :
                this.introRenderer.setAmbiantColor(SAUCER_FLY_AMBIENT_COLOR[0],SAUCER_FLY_AMBIENT_COLOR[1],SAUCER_FLY_AMBIENT_COLOR[2]);
                this.introRenderer.setLightColor(SAUCER_FLY_SPOT_COLOR[0],SAUCER_FLY_SPOT_COLOR[1],SAUCER_FLY_SPOT_COLOR[2]);
                this.titleFontActor.getBitmapFont().getColor().a = 0f;
                this.bitmapFontBatchLayer.addActor(this.titleFontActor);
                state = STATE_TITLE;
                this.tick(0);
                break;
            case STATE_TITLE :
                if(time < TITLE_FADE_START){
                    final float fadeTime = 1f - ((TITLE_FADE_START - time) / TITLE_FADE_START);
                    this.titleFontActor.getBitmapFont().getColor().a = fadeTime;
                    Runtime.getInstance().getViewport().getCamera().position.set(0,levelWorldWidth/2,0);
                    Runtime.getInstance().getViewport().apply();
                }
                else if(time >= TITLE_FADE_PAUSE){
                    if(time < SCROLL_DOWN_START) {
                        final float fadeTime = ((TITLE_FADE_END - time) / (TITLE_FADE_END - TITLE_FADE_PAUSE));
                        this.titleFontActor.getBitmapFont().getColor().a = fadeTime;
                        Runtime.getInstance().getViewport().getCamera().position.set(0, levelWorldWidth / 2, 0);
                        Runtime.getInstance().getViewport().apply();
                    }
                    else{
                        this.titleFontActor.getBitmapFont().getColor().a = 0;
                        this.bitmapFontBatchLayer.removeActor(this.titleFontActor);
                        state = STATE_SCROLL_DOWN;
                    }
                }
                break;
            case STATE_SCROLL_DOWN :
                if(time < SCROLL_DOWN_END){
                    final float scrollTime = ((SCROLL_DOWN_END - time) / (SCROLL_DOWN_END - SCROLL_DOWN_START));
                    this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()-SCROLL_DOWN_STEP);
                    Runtime.getInstance().getViewport().getCamera().position.set(0,(levelWorldWidth/2 * scrollTime),0);
                    Runtime.getInstance().getViewport().apply();
                }
                else{
                    Runtime.getInstance().getViewport().getCamera().position.set(0, 0, 0);
                    Runtime.getInstance().getViewport().apply();
                    flyingSaucerActor.setPosition(-1000,-1000);
                    this.saucerLayer.addActor(this.flyingSaucerActor);
                    this.particlesEffectBatchLayer.addActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.switchLight(false);
                    this.introRenderer.setFallOff(SAUCER_FLY_SPOT_FALLOFF[0], SAUCER_FLY_SPOT_FALLOFF[1], SAUCER_FLY_SPOT_FALLOFF[2]);
                    this.flyingSaucerParticlesEffect = this.flyingSaucerParticlesActor.spawn(true, -1000, -1000, false, false, 0.1f);
                    state = STATE_SAUCER_FLY;
                }
                break;
            case STATE_SAUCER_FLY :
                if(time <= SAUCER_FLY_END) {
                    if (time > SAUCER_FLY_START) {
                        this.introRenderer.switchLight(true);
                        final float delta = 1f - ((SAUCER_FLY_END - time) / (SAUCER_FLY_END - SAUCER_FLY_START));
                        this.tmpVectorFrom.set(SAUCER_FLY_LEFT[0], SAUCER_FLY_LEFT[1]);
                        this.tmpVectorTo.set(SAUCER_FLY_RIGHT[0], SAUCER_FLY_RIGHT[1]);
                        this.tmpVectorFrom.lerp(this.tmpVectorTo, delta);
                        this.flyingSaucerActor.setPosition(this.tmpVectorFrom.x + 3f, this.tmpVectorFrom.y);
                        this.flyingSaucerParticlesEffect.setPosition(this.tmpVectorFrom.x, this.tmpVectorFrom.y);
                        Runtime.getInstance().getViewport().project(this.tmpVectorFrom);
                        this.introRenderer.setLightPosition((int)this.tmpVectorFrom.x, (int)this.tmpVectorFrom.y);
                    }
                }
                else{
                    this.saucerLayer.removeActor(this.flyingSaucerActor);
                    this.flyingSaucerParticlesActor.release(this.flyingSaucerParticlesEffect);
                    this.particlesEffectBatchLayer.removeActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.switchLight(false);
                    this.introRenderer.setFallOff(SUNRISE_SPOT_FALLOFF[0], SUNRISE_SPOT_FALLOFF[1], SUNRISE_SPOT_FALLOFF[2]);
                    state = STATE_SUNRISE;
                }
                break;
            case STATE_SUNRISE :
                if(time <= SUNRISE_END) {
                    if(time > SUNRISE_START) {
                        this.introRenderer.switchLight(true);
                        final float delta = 1 - ((SUNRISE_END - time) / (SUNRISE_END - SUNRISE_START));
                        this.tmpVectorFrom.set(SUNRISE_BOTTOM[0], SUNRISE_BOTTOM[1]);
                        this.tmpVectorTo.set(SUNRISE_TOP[0], SUNRISE_TOP[1]);
                        this.tmpVectorFrom.lerp(this.tmpVectorTo, delta);
                        this.introRenderer.setLightColor(SUNRISE_SPOT_COLOR[0] * delta, SUNRISE_SPOT_COLOR[1] * delta, SUNRISE_SPOT_COLOR[2] * delta);
                        this.introRenderer.setAmbiantColor(SUNRISE_AMBIENT_COLOR_START[0] + ((SUNRISE_AMBIENT_COLOR_END[0] - SUNRISE_AMBIENT_COLOR_START[0]) * delta)
                                , SUNRISE_AMBIENT_COLOR_START[1] + ((SUNRISE_AMBIENT_COLOR_END[1] - SUNRISE_AMBIENT_COLOR_START[1]) * delta)
                                , SUNRISE_AMBIENT_COLOR_START[2] + ((SUNRISE_AMBIENT_COLOR_END[2] - SUNRISE_AMBIENT_COLOR_START[2]) * delta));
                        Runtime.getInstance().getViewport().project(this.tmpVectorFrom);
                        this.introRenderer.setLightPosition((int) this.tmpVectorFrom.x, (int) this.tmpVectorFrom.y);
                        this.skyLayer.setTime(delta);
                    }
                }
                break;
            case STATE_SAUCER_CRASH :
                break;
        }
    }
}
