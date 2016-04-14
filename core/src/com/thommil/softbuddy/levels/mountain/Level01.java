package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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

    public static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    public static class Config{

        public String FLYING_SAUCER_ID = "flying_saucer";
        public String CRASHED_SAUCER_ID = "crashed_saucer";

        public float TITLE_FADE_START = 2f;
        public float TITLE_FADE_PAUSE = TITLE_FADE_START + 1f;
        public float TITLE_FADE_END = TITLE_FADE_PAUSE + 1f;

        public float SCROLL_DOWN_START = TITLE_FADE_END;
        public float SCROLL_DOWN_STEP = 3f;
        public float SCROLL_DOWN_END = SCROLL_DOWN_START + 4f;

        public float SAUCER_FLY_START = SCROLL_DOWN_END + 2f;
        public float[] SAUCER_FLY_AMBIENT_COLOR = new float[]{0.3f, 0.3f, 0.3f, 1f};
        public float[] SAUCER_FLY_SPOT_COLOR = new float[]{0,0,1f,1f};
        public float[] SAUCER_FLY_HALO_OFFSET = new float[]{-3f, 0f};
        public float[] SAUCER_FLY_REACTOR_OFFSET = new float[]{-0.5f, 0.5f, 0.01f};
        public float[] SAUCER_FLY_LEFT = new float[]{-30f, 3f};
        public float[] SAUCER_FLY_RIGHT = new float[]{30f, 3f};
        public float[] SAUCER_FLY_SPOT_FALLOFF = new float[]{0.01f,0.1f,1f};
        public float SAUCER_FLY_MIDDLE = SAUCER_FLY_START + 3f;
        public float SAUCER_FLY_END = SAUCER_FLY_MIDDLE + 3f;

        public float SUNRISE_START = SAUCER_FLY_END + 2f;
        public float[] SUNRISE_AMBIENT_COLOR_START = new float[]{0.3f, 0.3f, 0.3f, 1f};
        public float[] SUNRISE_AMBIENT_COLOR_END = new float[]{0.6f, 0.6f, 0.6f, 1f};
        public float[] SUNRISE_SPOT_COLOR = new float[]{1f,1f,1f,1f};
        public float[] SUNRISE_BOTTOM = new float[]{30f, -10f};
        public float[] SUNRISE_TOP = new float[]{-40f, 30f};
        public float[] SUNRISE_SPOT_FALLOFF = new float[]{1f,0f,0f};
        public float SUNRISE_END = SUNRISE_START + 10f;

        public float SAUCER_CRASH_START = SUNRISE_END;
        public float[] SAUCER_CRASH_TOP = new float[]{-7f, 8f};
        public float[] SAUCER_CRASH_BOTTOM = new float[]{-5.5f, -1f};
        public float SAUCER_CRASH_SMOKE = SAUCER_CRASH_START + 0.1f;
        public float[] SAUCER_CRASH_PARTICLES = new float[]{-5f, 1f, 0.01f, 0f, 20f};
        public float SAUCER_CRASH_END = SAUCER_CRASH_START + 0.2f;

        public Interpolation flyInterpolation = new Interpolation() {
            @Override
            public float apply(float a) {
                return (float) (1 - Math.pow(1 - (2*a),5))/2;
            }
        };
    }

    private static final int STATE_INIT = 0;
    private static final int STATE_TITLE = 1;
    private static final int STATE_SCROLL_DOWN = 2;
    private static final int STATE_SAUCER_FLY = 3;
    private static final int STATE_SUNRISE = 4;
    private static final int STATE_SAUCER_CRASH = 5;
    private static final int STATE_PLAY = 6;

    private Config config;

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private int state;

    private Vector2 tmpVector;

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
    private SpriteActor crashedSaucerActor;

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
        this.config = new Config();
        this.tmpVector = new Vector2();
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
                if(state != STATE_PLAY) {
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
        this.saucerLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1);
        final SceneLoader.ImageDef flyingSaucerImageDef = this.levelResources.getImageDefinition(config.FLYING_SAUCER_ID);
        this.flyingSaucerActor = new SpriteActor(config.FLYING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(flyingSaucerImageDef.path, Texture.class))
                ,flyingSaucerImageDef.regionX
                ,flyingSaucerImageDef.regionY
                ,flyingSaucerImageDef.regionWidth
                ,flyingSaucerImageDef.regionHeight);
        flyingSaucerActor.setSize(flyingSaucerImageDef.width, flyingSaucerImageDef.height);
        flyingSaucerActor.setOriginCenter();
        final SceneLoader.ImageDef crashedSaucerImageDef = this.levelResources.getImageDefinition(config.CRASHED_SAUCER_ID);
        this.crashedSaucerActor = new SpriteActor(config.CRASHED_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(crashedSaucerImageDef.path, Texture.class))
                ,crashedSaucerImageDef.regionX
                ,crashedSaucerImageDef.regionY
                ,crashedSaucerImageDef.regionWidth
                ,crashedSaucerImageDef.regionHeight);
        crashedSaucerActor.setSize(crashedSaucerImageDef.width, crashedSaucerImageDef.height);
        crashedSaucerActor.setOriginCenter();
        Runtime.getInstance().addLayer(this.saucerLayer);

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

        this.particlesEffectBatchLayer = new ParticlesEffectBatchLayer(Runtime.getInstance().getViewport(),1);
        this.flyingSaucerParticlesEffect = new ParticleEffect();
        this.flyingSaucerParticlesEffect.load(Gdx.files.internal(this.levelResources.getParticlesEffectDefinition(config.FLYING_SAUCER_ID).path), this.levelResources.getTextureAtlas(this.assetManager));
        this.flyingSaucerParticlesActor = new ParticleEffectActor(config.FLYING_SAUCER_ID.hashCode(), this.flyingSaucerParticlesEffect,100);
        this.crashedSaucerParticlesEffect = new ParticleEffect();
        this.crashedSaucerParticlesEffect.load(Gdx.files.internal(this.levelResources.getParticlesEffectDefinition(config.CRASHED_SAUCER_ID).path), this.levelResources.getTextureAtlas(this.assetManager));
        this.crashedSaucerParticlesActor = new ParticleEffectActor(config.CRASHED_SAUCER_ID.hashCode(), this.crashedSaucerParticlesEffect,100);
        Runtime.getInstance().addLayer(this.particlesEffectBatchLayer);
    }

    private void buildHUD(){
        final SharedResources.LabelDef titleLableDef = this.softBuddyGameAPI.getSharedResources().getLabelDef(Chapter.TITLE_LABEL);
        this.titleFontActor = new BitmapFontActor(0, this.assetManager.get(titleLableDef.assetName, BitmapFont.class));
        titleFontActor.setText(this.chapterResources.getChapterDef().title);
        tmpVector.set(titleLableDef.position[0], titleLableDef.position[1]);
        ViewportLayout.adaptToScreen(SoftBuddyGameAPI.REFERENCE_SCREEN, tmpVector);
        titleFontActor.setPosition(tmpVector.x, tmpVector.y);
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
        this.crashedSaucerActor.dispose();
        this.flyingSaucerParticlesActor.dispose();
        this.flyingSaucerParticlesEffect.dispose();
        this.crashedSaucerParticlesActor.dispose();
        this.crashedSaucerParticlesEffect.dispose();
        this.introRenderer.dispose();

        this.ticklayer = null;
        this.skyLayer = null;
        this.backgroundLayer = null;
        this.foregroundLayer = null;
        this.saucerLayer = null;
        this.bitmapFontBatchLayer = null;

        this.flyingSaucerActor = null;
        this.crashedSaucerActor = null;
        this.flyingSaucerParticlesActor = null;
        this.flyingSaucerParticlesEffect = null;
        this.crashedSaucerParticlesActor = null;
        this.crashedSaucerParticlesEffect = null;
        this.introRenderer = null;
        this.tmpVector = null;
        this.config = null;
    }

    public void tick(float deltaTime){
        time += deltaTime;
        switch(state){
            case STATE_INIT :
                this.introRenderer.setAmbiantColor(config.SAUCER_FLY_AMBIENT_COLOR[0],config.SAUCER_FLY_AMBIENT_COLOR[1],config.SAUCER_FLY_AMBIENT_COLOR[2]);
                this.introRenderer.setLightColor(config.SAUCER_FLY_SPOT_COLOR[0],config.SAUCER_FLY_SPOT_COLOR[1],config.SAUCER_FLY_SPOT_COLOR[2]);
                this.titleFontActor.getBitmapFont().getColor().a = 0f;
                this.bitmapFontBatchLayer.addActor(this.titleFontActor);
                state = STATE_TITLE;
                this.tick(0);
                break;
            case STATE_TITLE :
                if(time < config.TITLE_FADE_START){
                    final float fadeTime = 1f - ((config.TITLE_FADE_START - time) / config.TITLE_FADE_START);
                    this.titleFontActor.getBitmapFont().getColor().a = fadeTime;
                    Runtime.getInstance().getViewport().getCamera().position.set(0,levelWorldWidth/2,0);
                    Runtime.getInstance().getViewport().apply();
                }
                else if(time >= config.TITLE_FADE_PAUSE){
                    if(time < config.SCROLL_DOWN_START) {
                        final float fadeTime = ((config.TITLE_FADE_END - time) / (config.TITLE_FADE_END - config.TITLE_FADE_PAUSE));
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
                if(time < config.SCROLL_DOWN_END){
                    final float scrollTime = ((config.SCROLL_DOWN_END - time) / (config.SCROLL_DOWN_END - config.SCROLL_DOWN_START));
                    this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()-config.SCROLL_DOWN_STEP);
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
                    this.introRenderer.setFallOff(config.SAUCER_FLY_SPOT_FALLOFF[0], config.SAUCER_FLY_SPOT_FALLOFF[1], config.SAUCER_FLY_SPOT_FALLOFF[2]);
                    this.flyingSaucerParticlesEffect = this.flyingSaucerParticlesActor.spawn(true, -1000, -1000, false, false, config.SAUCER_FLY_REACTOR_OFFSET[2]);
                    state = STATE_SAUCER_FLY;
                    this.tick(0);
                }
                break;
            case STATE_SAUCER_FLY :
                if(time <= config.SAUCER_FLY_END) {
                    if (time > config.SAUCER_FLY_START) {
                        this.introRenderer.switchLight(true);
                        if (time < config.SAUCER_FLY_MIDDLE) {
                            final float delta = 1f - ((config.SAUCER_FLY_MIDDLE - time) / (config.SAUCER_FLY_MIDDLE - config.SAUCER_FLY_START));
                            this.tmpVector.x = config.flyInterpolation.apply(config.SAUCER_FLY_LEFT[0], config.SAUCER_FLY_RIGHT[0], delta) - this.flyingSaucerActor.width/2;
                            this.tmpVector.y = config.flyInterpolation.apply(config.SAUCER_FLY_LEFT[1], config.SAUCER_FLY_RIGHT[1], delta);
                            this.flyingSaucerActor.setPosition(this.tmpVector.x, this.tmpVector.y);
                            this.flyingSaucerParticlesEffect.setPosition(this.tmpVector.x + config.SAUCER_FLY_REACTOR_OFFSET[0], this.tmpVector.y + config.SAUCER_FLY_REACTOR_OFFSET[1]);
                            this.tmpVector.add(config.SAUCER_FLY_HALO_OFFSET[0], config.SAUCER_FLY_HALO_OFFSET[1]);
                            Runtime.getInstance().getViewport().project(this.tmpVector);
                            this.introRenderer.setLightPosition((int) this.tmpVector.x, (int) this.tmpVector.y);
                        }
                        else{
                            this.flyingSaucerActor.setFlip(true, false);
                            this.flyingSaucerParticlesEffect.setFlip(true, false);
                            final float delta = 1f - ((config.SAUCER_FLY_END - time) / (config.SAUCER_FLY_END - config.SAUCER_FLY_MIDDLE));
                            this.tmpVector.x = config.flyInterpolation.apply(config.SAUCER_FLY_RIGHT[0], config.SAUCER_FLY_LEFT[0], delta) - this.flyingSaucerActor.width/2;
                            this.tmpVector.y = config.flyInterpolation.apply(config.SAUCER_FLY_RIGHT[1], config.SAUCER_FLY_LEFT[1], delta);
                            this.flyingSaucerActor.setPosition(this.tmpVector.x, this.tmpVector.y);
                            this.flyingSaucerParticlesEffect.setPosition(this.tmpVector.x + this.flyingSaucerActor.width - config.SAUCER_FLY_REACTOR_OFFSET[0], this.tmpVector.y + config.SAUCER_FLY_REACTOR_OFFSET[1]);
                            this.tmpVector.add(-config.SAUCER_FLY_HALO_OFFSET[0] + this.flyingSaucerActor.width, config.SAUCER_FLY_HALO_OFFSET[1]);
                            Runtime.getInstance().getViewport().project(this.tmpVector);
                            this.introRenderer.setLightPosition((int) this.tmpVector.x, (int) this.tmpVector.y);
                        }
                    }
                }
                else{
                    this.saucerLayer.removeActor(this.flyingSaucerActor);
                    this.flyingSaucerParticlesActor.release(this.flyingSaucerParticlesEffect);
                    this.particlesEffectBatchLayer.removeActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.switchLight(false);
                    this.introRenderer.setFallOff(config.SUNRISE_SPOT_FALLOFF[0], config.SUNRISE_SPOT_FALLOFF[1], config.SUNRISE_SPOT_FALLOFF[2]);
                    state = STATE_SUNRISE;
                    this.tick(0);
                }
                break;
            case STATE_SUNRISE :
                if(time <= config.SUNRISE_END) {
                    if(time > config.SUNRISE_START) {
                        this.introRenderer.switchLight(true);
                        final float delta = 1 - ((config.SUNRISE_END - time) / (config.SUNRISE_END - config.SUNRISE_START));
                        this.tmpVector.x = Interpolation.linear.apply(config.SUNRISE_BOTTOM[0], config.SUNRISE_TOP[0], delta);
                        this.tmpVector.y = Interpolation.linear.apply(config.SUNRISE_BOTTOM[1], config.SUNRISE_TOP[1], delta);
                        this.introRenderer.setLightColor(config.SUNRISE_SPOT_COLOR[0] * delta, config.SUNRISE_SPOT_COLOR[1] * delta, config.SUNRISE_SPOT_COLOR[2] * delta);
                        this.introRenderer.setAmbiantColor(config.SUNRISE_AMBIENT_COLOR_START[0] + ((config.SUNRISE_AMBIENT_COLOR_END[0] - config.SUNRISE_AMBIENT_COLOR_START[0]) * delta)
                                , config.SUNRISE_AMBIENT_COLOR_START[1] + ((config.SUNRISE_AMBIENT_COLOR_END[1] - config.SUNRISE_AMBIENT_COLOR_START[1]) * delta)
                                , config.SUNRISE_AMBIENT_COLOR_START[2] + ((config.SUNRISE_AMBIENT_COLOR_END[2] - config.SUNRISE_AMBIENT_COLOR_START[2]) * delta));
                        Runtime.getInstance().getViewport().project(this.tmpVector);
                        this.introRenderer.setLightPosition((int) this.tmpVector.x, (int) this.tmpVector.y);
                        this.skyLayer.setTime(delta);
                    }
                }
                else{
                    this.saucerLayer.addActor(this.crashedSaucerActor);
                    this.crashedSaucerActor.setPosition(-5.5f,-0.5f);
                    this.particlesEffectBatchLayer.addActor(this.crashedSaucerParticlesActor);
                    config.SAUCER_CRASH_PARTICLES[3] = 0f;
                    state = STATE_SAUCER_CRASH;
                    this.tick(0);
                }
                break;
            case STATE_SAUCER_CRASH :
                if(time <= config.SAUCER_CRASH_END) {
                    if (time > config.SAUCER_CRASH_START) {
                        final float delta = 1f - ((config.SAUCER_CRASH_END - time) / (config.SAUCER_CRASH_END - config.SAUCER_CRASH_START));
                        this.tmpVector.x = Interpolation.linear.apply(config.SAUCER_CRASH_TOP[0], config.SAUCER_CRASH_BOTTOM[0], delta);
                        this.tmpVector.y = Interpolation.linear.apply(config.SAUCER_CRASH_TOP[1], config.SAUCER_CRASH_BOTTOM[1], delta);
                        if(time >= config.SAUCER_CRASH_SMOKE && config.SAUCER_CRASH_PARTICLES[3] == 0f ){
                            for(int i= 0; i < config.SAUCER_CRASH_PARTICLES[4] ; i++) {
                                this.crashedSaucerParticlesActor.spawn(true, config.SAUCER_CRASH_PARTICLES[0], config.SAUCER_CRASH_PARTICLES[1], false, false, config.SAUCER_CRASH_PARTICLES[2]);
                            }
                            config.SAUCER_CRASH_PARTICLES[3] = 1f;
                        }
                        this.crashedSaucerActor.setPosition(this.tmpVector.x, this.tmpVector.y);
                    }
                }
                else{
                    this.crashedSaucerActor.setPosition(config.SAUCER_CRASH_BOTTOM[0], config.SAUCER_CRASH_BOTTOM[1]);
                    state = STATE_PLAY;
                }
                break;
        }
    }
}
