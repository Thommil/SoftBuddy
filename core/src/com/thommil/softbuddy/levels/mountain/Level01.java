package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
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
import com.thommil.libgdx.runtime.graphics.animation.ColorAnimation;
import com.thommil.libgdx.runtime.graphics.animation.TranslateAnimation;
import com.thommil.libgdx.runtime.graphics.renderer.buffer.OffScreenRenderer;
import com.thommil.libgdx.runtime.graphics.renderer.particles.TexturedParticlesBatchRenderer;
import com.thommil.libgdx.runtime.layer.*;
import com.thommil.libgdx.runtime.tools.SceneLoader;
import com.thommil.softbuddy.SharedResources;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.common.layers.SkyLayer;
import com.thommil.softbuddy.levels.common.renderers.SoftBuddyRenderer;
import com.thommil.softbuddy.levels.mountain.actors.SoftBuddyActor;
import com.thommil.softbuddy.levels.mountain.renderers.IntroRenderer;

public class Level01 extends Level{

    public static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    public static class Config{

        public String BACKGROUND_ID = "background";
        public String FOREGROUND_ID = "foreground";
        public String TITLE_ID = "title";
        public String SCROLL_DOWN_ID = "scroll_down";
        public String AMBIENT_ID = "ambient";
        public String SUNRISE_SPOT_ID = "sunrise_spot";
        public String FLYING_SAUCER_ID = "flying_saucer";
        public String REACTOR_SPOT_ID = "reactor_spot";
        public String SUNRISE_ID = "sunrise";
        public String LANDING_SAUCER_ID = "landing_saucer";
        public String SOFTBUFFY_PARTICLE_ID = "softbuddy_particle";

        public float SCROLL_DOWN_STARS_STEP = 3f;

        public float[] SAUCER_FLY_START_POSITON = new float[]{-30f, 3f};
        public float[] REACTOR_SPOT_FALLOFF = new float[]{0.01f,0.1f,1f};
        public float[] REACTOR_OFFSET = new float[]{-0.5f, 0.5f, 0.01f};
        public float[] REACTOR_SPOT_OFFSET = new float[]{-3f, 0f};

        public float[] SUNRISE_SPOT_FALLOFF = new float[]{1f,0f,0f};
        public float[] SUNRISE_START_POSITION = new float[]{30f, -20f};

        public float[] SAUCER_LANDING_START_POSITION = new float[]{0f, 6f};

        public float[] SOFTBUDDY_GROUP_START_OFFSET = new float[]{1.5f, 1.5f};
        public float SOFTBUDDY_GROUP_START_WIDTH = 2f;

        public Interpolation flyInterpolation = new Interpolation() {
            @Override
            public float apply(float a) {
                return (float) (1 - Math.pow(1 - (2*a),5))/2;
            }
        };
    }

    private static final int STATE_TITLE = 0;
    private static final int STATE_SCROLL_DOWN = 1;
    private static final int STATE_SAUCER_FLY = 2;
    private static final int STATE_SUNRISE = 3;
    private static final int STATE_SAUCER_LANDING = 4;
    private static final int STATE_SOFTBUDDY_IN = 5;
    private static final int STATE_PLAY = 6;

    private Config config;

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private int state;

    private Vector2 tmpVector;
    private Vector2 screenVector;

    //Tick
    private Layer ticklayer;

    //Sky
    private SkyLayer skyLayer;

    //Background & Foreground
    private SpriteBatchLayer backgroundLayer;
    private OffScreenLayer<SpriteBatchLayer> backgroundOffScreenLayer;
    private SpriteBatchLayer foregroundLayer;
    private OffScreenLayer<SpriteBatchLayer> foregroundOffScreenLayer;
    private OffScreenRenderer backgroundOffScreenRenderer;
    private OffScreenRenderer foregroundOffScreenRenderer;
    private IntroRenderer introRenderer;

    //Sprites
    private SpriteBatchLayer saucerLayer;
    private SpriteActor flyingSaucerActor;
    private SpriteActor landingSaucerActor;

    //ParticleSystem
    private ParticlesBatchLayer softBuddyLayer;
    private OffScreenLayer<ParticlesBatchLayer> softBuddyOffScreenLayer;
    private TexturedParticlesBatchRenderer softBuddyParticlesRenderer;
    private SoftBuddyRenderer softBuddyRenderer;
    private SoftBuddyActor softBuddyActor;

    //Particles
    private ParticlesEffectBatchLayer particlesEffectBatchLayer;
    private ParticleEffectActor flyingSaucerParticlesActor;
    private ParticleEffect flyingSaucerParticlesEffect;

    //Animations
    private ColorAnimation titleAnimation;
    private TranslateAnimation scrollDownAnimation;
    private ColorAnimation reactorSpotAnimation;
    private ColorAnimation sunSpotAnimation;
    private ColorAnimation ambientColorAnimation;
    private TranslateAnimation flyingSaucerAnimation;
    private TranslateAnimation sunriseAnimation;
    private TranslateAnimation landingSaucerAnimation;

    //HUD
    private BitmapFontBatchLayer bitmapFontBatchLayer;
    private BitmapFontActor titleFontActor;

    @Override
    public String getResourcesPath() {
        return RESOURCES_FILE;
    }

    @Override
    public void reset() {
        this.time = 0;

        //Sky
        this.skyLayer.setTime(SkyLayer.MIDNIGHT);

        //Background
        this.introRenderer.setAmbiantColor(this.ambientColorAnimation.getKeyFrame(0));
        this.introRenderer.setLightColor(this.reactorSpotAnimation.getKeyFrame(0));
        this.backgroundOffScreenLayer.setOffScreenRendering(false);
        this.introRenderer.switchLight(false);

        //Foreground
        this.foregroundOffScreenLayer.setOffScreenRendering(false);
        this.softBuddyOffScreenLayer.setOffScreenRendering(false);
        this.saucerLayer.removeActor(this.landingSaucerActor);
        this.saucerLayer.removeActor(this.flyingSaucerActor);
        if(this.flyingSaucerActor.isFlipX()) this.flyingSaucerActor.flip(true, false);
        this.particlesEffectBatchLayer.removeActor(this.flyingSaucerParticlesActor);
        if(this.flyingSaucerParticlesEffect != null) this.flyingSaucerParticlesActor.release(this.flyingSaucerParticlesEffect);
        this.softBuddyActor.reset();

        //HUD
        this.titleFontActor.getBitmapFont().setColor(this.titleAnimation.getKeyFrame(0));
        this.bitmapFontBatchLayer.addActor(this.titleFontActor);
        this.bitmapFontBatchLayer.show();

        //Animations
        this.titleAnimation.reset();
        this.scrollDownAnimation.reset();
        this.reactorSpotAnimation.reset();
        this.sunSpotAnimation.reset();
        this.ambientColorAnimation.reset();
        this.flyingSaucerAnimation.reset();
        this.sunriseAnimation.reset();
        this.landingSaucerAnimation.reset();

        state = STATE_TITLE;
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
        this.screenVector = new Vector2();
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
                graphicsTick(deltaTime);
            }

            @Override
            public void step(float deltaTime) {
                physicsTick(deltaTime);
            }

            @Override public void dispose() {}
        };
        Runtime.getInstance().addLayer(this.ticklayer);

        this.buildBackground();
        this.buildForeground();
        this.buildHUD();
    }

    protected void buildBackground() {
        this.introRenderer = new IntroRenderer(1);
        this.skyLayer = new SkyLayer(Runtime.getInstance().getViewport(), true, SkyLayer.MIDNIGHT, -this.levelWorldWidth/2, -this.levelWorldHeight/4, this.levelWorldWidth, this.levelWorldHeight,this.levelWorldHeight/4);
        this.scrollDownAnimation = (TranslateAnimation)this.levelResources.getAnimation(config.SCROLL_DOWN_ID, this.assetManager);
        this.scrollDownAnimation.getKeyFrame(0).y = this.levelWorldHeight/2;
        this.sunSpotAnimation = (ColorAnimation) this.levelResources.getAnimation(config.SUNRISE_SPOT_ID, this.assetManager);
        this.ambientColorAnimation = (ColorAnimation) this.levelResources.getAnimation(config.AMBIENT_ID, this.assetManager);
        this.sunriseAnimation = (TranslateAnimation) this.levelResources.getAnimation(config.SUNRISE_ID, this.assetManager);
        Runtime.getInstance().addLayer(this.skyLayer);

        this.backgroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1,this.introRenderer);
        final SceneLoader.ImageDef backgroundImageDef = this.levelResources.getImageDefinition(config.BACKGROUND_ID);
        final Texture backgroundTexture = this.assetManager.get(backgroundImageDef.path, Texture.class);
        this.introRenderer.setNormalOffset(backgroundImageDef.normalOffset.x/backgroundTexture.getWidth(),backgroundImageDef.normalOffset.y/backgroundTexture.getHeight());
        final StaticActor backgroundActor = new StaticActor(config.BACKGROUND_ID.hashCode()
                ,new TextureSet(backgroundTexture)
                ,backgroundImageDef.x - backgroundImageDef.width/2
                ,backgroundImageDef.y - backgroundImageDef.height/2
                ,backgroundImageDef.width
                ,backgroundImageDef.height
                ,backgroundImageDef.regionX
                ,backgroundImageDef.regionY
                ,backgroundImageDef.regionWidth
                ,backgroundImageDef.regionHeight
                ,Color.WHITE.toFloatBits());

        this.backgroundLayer.addActor(backgroundActor);
        this.backgroundOffScreenRenderer = new OffScreenRenderer(Runtime.getInstance().getViewport(), Pixmap.Format.RGBA8888,true,true);
        this.backgroundOffScreenLayer = new OffScreenLayer<SpriteBatchLayer>(Runtime.getInstance().getViewport(),this.backgroundLayer, this.backgroundOffScreenRenderer);
        Runtime.getInstance().addLayer(this.backgroundOffScreenLayer);
    }

    protected void buildForeground() {
        final SceneLoader.ImageDef softBuddyParticleImageDef = this.chapterResources.getImageDefinition(config.SOFTBUFFY_PARTICLE_ID);
        this.softBuddyParticlesRenderer = new TexturedParticlesBatchRenderer(SoftBuddyGameAPI.PARTICLES_BATCH_SIZE);
        this.softBuddyLayer = new ParticlesBatchLayer(Runtime.getInstance().getViewport(),1, this.softBuddyParticlesRenderer);
        this.softBuddyActor = new SoftBuddyActor(config.SOFTBUFFY_PARTICLE_ID.hashCode(), new TextureSet(new Texture(Gdx.files.internal(softBuddyParticleImageDef.path))));
        this.softBuddyLayer.addActor(this.softBuddyActor);
        this.softBuddyLayer.setScaleFactor(SoftBuddyActor.DEFAULT_PARTICLES_SCALEFACTOR);
        this.softBuddyRenderer = new SoftBuddyRenderer(Runtime.getInstance().getViewport());
        this.softBuddyOffScreenLayer = new OffScreenLayer<ParticlesBatchLayer>(Runtime.getInstance().getViewport(),this.softBuddyLayer,this.softBuddyRenderer);
        Runtime.getInstance().addLayer(this.softBuddyOffScreenLayer);

        this.saucerLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1);
        final SceneLoader.ImageDef flyingSaucerImageDef = this.levelResources.getImageDefinition(config.FLYING_SAUCER_ID);
        this.flyingSaucerActor = new SpriteActor(config.FLYING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(flyingSaucerImageDef.path, Texture.class))
                ,flyingSaucerImageDef.regionX
                ,flyingSaucerImageDef.regionY
                ,flyingSaucerImageDef.regionWidth
                ,flyingSaucerImageDef.regionHeight
                ,flyingSaucerImageDef.width
                ,flyingSaucerImageDef.height);
        this.flyingSaucerAnimation = (TranslateAnimation) this.levelResources.getAnimation(config.FLYING_SAUCER_ID, this.assetManager);
        this.flyingSaucerAnimation.getKeyFrame(0).interpolation = config.flyInterpolation;
        this.flyingSaucerAnimation.getKeyFrame(1).interpolation = config.flyInterpolation;
        this.reactorSpotAnimation = (ColorAnimation) this.levelResources.getAnimation(config.REACTOR_SPOT_ID, this.assetManager);
        final SceneLoader.ImageDef landingSaucerImageDef = this.levelResources.getImageDefinition(config.LANDING_SAUCER_ID);
        this.landingSaucerActor = new SpriteActor(config.LANDING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(landingSaucerImageDef.path, Texture.class))
                ,landingSaucerImageDef.regionX
                ,landingSaucerImageDef.regionY
                ,landingSaucerImageDef.regionWidth
                ,landingSaucerImageDef.regionHeight
                ,landingSaucerImageDef.width
                ,landingSaucerImageDef.height);
        this.landingSaucerAnimation = (TranslateAnimation) this.levelResources.getAnimation(config.LANDING_SAUCER_ID, this.assetManager);
        Runtime.getInstance().addLayer(this.saucerLayer);

        this.foregroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1, this.introRenderer);
        final SceneLoader.BodyDef foregroundBodyDef = this.levelResources.getBodyDefintion(config.FOREGROUND_ID);
        final SceneLoader.ImageDef foregroundImageDef = this.levelResources.getImageDefinition(config.FOREGROUND_ID);
        final StaticBodyActor foregroundStaticBodyActor = new StaticBodyActor(config.FOREGROUND_ID.hashCode()
                ,new TextureSet(this.assetManager.get(foregroundImageDef.path, Texture.class))
                ,foregroundBodyDef.position.x + foregroundImageDef.x - foregroundImageDef.width/2
                ,foregroundBodyDef.position.y + foregroundImageDef.y - foregroundImageDef.height/2
                ,foregroundImageDef.width
                ,foregroundImageDef.height
                ,foregroundImageDef.regionX
                ,foregroundImageDef.regionY
                ,foregroundImageDef.regionWidth
                ,foregroundImageDef.regionHeight
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
        this.foregroundOffScreenRenderer = new OffScreenRenderer(Runtime.getInstance().getViewport(), Pixmap.Format.RGBA8888,true,true);
        this.foregroundOffScreenLayer = new OffScreenLayer<SpriteBatchLayer>(Runtime.getInstance().getViewport(),this.foregroundLayer, this.foregroundOffScreenRenderer);
        Runtime.getInstance().addLayer(this.foregroundOffScreenLayer);

        this.particlesEffectBatchLayer = new ParticlesEffectBatchLayer(Runtime.getInstance().getViewport(),1);
        this.particlesEffectBatchLayer.setAdditive(true);
        this.flyingSaucerParticlesEffect = this.levelResources.getParticleEffect(config.FLYING_SAUCER_ID, this.assetManager);
        this.flyingSaucerParticlesActor = new ParticleEffectActor(config.FLYING_SAUCER_ID.hashCode(), this.flyingSaucerParticlesEffect,100);
        this.flyingSaucerParticlesEffect = null;
        Runtime.getInstance().addLayer(this.particlesEffectBatchLayer);
    }

    private void buildHUD(){
        final SharedResources.LabelDef titleLabelDef = this.softBuddyGameAPI.getSharedResources().getLabelDef(Chapter.TITLE_LABEL);
        this.titleFontActor = new BitmapFontActor(0, this.assetManager.get(titleLabelDef.assetName, BitmapFont.class));
        this.titleFontActor.setText(this.chapterResources.getChapterDef().title);
        this.screenVector.set(titleLabelDef.position[0], titleLabelDef.position[1]);
        ViewportLayout.adaptToScreen(SoftBuddyGameAPI.REFERENCE_SCREEN, screenVector);
        this.titleFontActor.setPosition(screenVector.x, screenVector.y);
        this.bitmapFontBatchLayer = new BitmapFontBatchLayer(Runtime.getInstance().getViewport(), 1);
        this.titleAnimation = (ColorAnimation) this.levelResources.getAnimation(config.TITLE_ID, this.assetManager);
        Runtime.getInstance().addLayer(this.bitmapFontBatchLayer);
    }

    @Override
    public void dispose() {
        Runtime.getInstance().removeLayer(this.ticklayer);
        this.ticklayer.dispose(true);
        this.ticklayer = null;

        this.disposeHUD();
        this.disposeForeground();
        this.disposeBackground();

        this.config = null;
        this.tmpVector = null;
        this.screenVector = null;
    }

    private void disposeHUD(){
        Runtime.getInstance().removeLayer(this.bitmapFontBatchLayer);
        this.bitmapFontBatchLayer.dispose(true);
        this.bitmapFontBatchLayer = null;
        this.titleFontActor.dispose();
        this.titleFontActor = null;
        this.titleAnimation = null;
    }

    private void disposeForeground(){
        Runtime.getInstance().removeLayer(this.particlesEffectBatchLayer);
        this.particlesEffectBatchLayer.dispose(true);
        this.particlesEffectBatchLayer = null;
        this.flyingSaucerParticlesActor.dispose();
        this.flyingSaucerParticlesActor = null;
        this.flyingSaucerParticlesEffect.dispose();
        this.flyingSaucerParticlesEffect = null;

        Runtime.getInstance().removeLayer(this.foregroundOffScreenLayer);
        this.foregroundOffScreenLayer.dispose(true);
        this.foregroundOffScreenLayer = null;
        this.foregroundOffScreenRenderer.dispose();
        this.foregroundOffScreenRenderer = null;
        this.foregroundLayer.dispose(true);
        this.foregroundLayer = null;

        Runtime.getInstance().removeLayer(this.saucerLayer);
        this.saucerLayer.dispose(true);
        this.saucerLayer = null;
        this.landingSaucerActor.dispose();
        this.landingSaucerActor = null;
        this.flyingSaucerActor.dispose();
        this.flyingSaucerActor = null;
        this.reactorSpotAnimation = null;
        this.flyingSaucerAnimation = null;
        this.landingSaucerAnimation = null;

        Runtime.getInstance().removeLayer(this.softBuddyOffScreenLayer);
        this.softBuddyOffScreenLayer.dispose(true);
        this.softBuddyOffScreenLayer = null;
        this.softBuddyRenderer.dispose();
        this.softBuddyRenderer = null;
        this.softBuddyLayer.dispose(true);
        this.softBuddyLayer = null;
        this.softBuddyActor.dispose();
        this.softBuddyActor = null;
        this.softBuddyParticlesRenderer.dispose();
        this.softBuddyParticlesRenderer = null;
    }

    private void disposeBackground(){
        Runtime.getInstance().removeLayer(this.backgroundOffScreenLayer);
        this.backgroundOffScreenLayer.dispose(true);
        this.backgroundOffScreenLayer = null;
        this.backgroundOffScreenRenderer.dispose();
        this.backgroundOffScreenRenderer = null;
        this.backgroundLayer.dispose(true);
        this.backgroundLayer = null;
        this.sunSpotAnimation = null;
        this.ambientColorAnimation = null;
        this.sunriseAnimation = null;

        Runtime.getInstance().removeLayer(this.skyLayer);
        this.skyLayer.dispose(true);
        this.skyLayer = null;

        this.introRenderer.dispose();
        this.introRenderer = null;
    }



    public void graphicsTick(float deltaTime){
        this.time += deltaTime;
        switch(this.state){
            case STATE_TITLE :
                if(!this.titleAnimation.isAnimationFinished(time)){
                    this.titleFontActor.playAnimation(this.titleAnimation,time);
                    Runtime.getInstance().getViewport().getCamera().position.set(0,levelWorldHeight/2,0);
                    Runtime.getInstance().getViewport().apply();
                }
                else{
                    this.bitmapFontBatchLayer.removeActor(this.titleFontActor);
                    this.bitmapFontBatchLayer.hide();
                    this.time=0;
                    state = STATE_SCROLL_DOWN;
                }
                break;
            case STATE_SCROLL_DOWN :
                if(!this.scrollDownAnimation.isAnimationFinished(time)){
                    this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()-config.SCROLL_DOWN_STARS_STEP);
                    Runtime.getInstance().getViewport().getCamera().position.add(0,-this.scrollDownAnimation.getKeyFrame(time).y,0);
                    Runtime.getInstance().getViewport().apply();
                }
                else{
                    Runtime.getInstance().getViewport().getCamera().position.set(0, 0, 0);
                    Runtime.getInstance().getViewport().apply();
                    this.flyingSaucerActor.setPosition(config.SAUCER_FLY_START_POSITON[0],config.SAUCER_FLY_START_POSITON[1]);
                    this.saucerLayer.addActor(this.flyingSaucerActor);
                    this.particlesEffectBatchLayer.addActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.switchLight(true);
                    this.introRenderer.setFallOff(config.REACTOR_SPOT_FALLOFF[0], config.REACTOR_SPOT_FALLOFF[1], config.REACTOR_SPOT_FALLOFF[2]);
                    this.flyingSaucerParticlesEffect = this.flyingSaucerParticlesActor.spawn(true, config.SAUCER_FLY_START_POSITON[0], config.SAUCER_FLY_START_POSITON[1], false, false, config.REACTOR_OFFSET[2]);
                    this.time = 0;
                    this.state = STATE_SAUCER_FLY;
                    this.graphicsTick(0);
                }
                break;
            case STATE_SAUCER_FLY :
                if(!this.flyingSaucerAnimation.isAnimationFinished(this.time)){
                    this.flyingSaucerActor.playAnimation(this.flyingSaucerAnimation, time);
                    if(!this.flyingSaucerActor.isFlipX()) {
                        this.tmpVector.set(this.flyingSaucerActor.x, this.flyingSaucerActor.y);
                        this.tmpVector.add(config.REACTOR_SPOT_OFFSET[0], config.REACTOR_SPOT_OFFSET[1]);
                        this.flyingSaucerParticlesEffect.setPosition(this.flyingSaucerActor.x + config.REACTOR_OFFSET[0], this.flyingSaucerActor.y + config.REACTOR_OFFSET[1]);
                    }
                    else {
                        this.tmpVector.set(this.flyingSaucerActor.x, this.flyingSaucerActor.y);
                        this.tmpVector.add(-config.REACTOR_SPOT_OFFSET[0] + this.flyingSaucerActor.width, config.REACTOR_SPOT_OFFSET[1]);
                        this.flyingSaucerParticlesEffect.setPosition(this.flyingSaucerActor.x + this.flyingSaucerActor.width - config.REACTOR_OFFSET[0], this.flyingSaucerActor.y + config.REACTOR_OFFSET[1]);
                    }
                    this.screenVector.set(this.tmpVector);
                    Runtime.getInstance().getViewport().project(this.screenVector);
                    this.introRenderer.setLightPosition((int) this.screenVector.x, (int) this.screenVector.y);
                }
                else{
                    this.saucerLayer.removeActor(this.flyingSaucerActor);
                    this.flyingSaucerParticlesActor.release(this.flyingSaucerParticlesEffect);
                    this.particlesEffectBatchLayer.removeActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.setFallOff(config.SUNRISE_SPOT_FALLOFF[0], config.SUNRISE_SPOT_FALLOFF[1], config.SUNRISE_SPOT_FALLOFF[2]);
                    this.introRenderer.setLightColor(this.sunSpotAnimation.getKeyFrame(0));
                    this.tmpVector.set(config.SUNRISE_START_POSITION[0], config.SUNRISE_START_POSITION[1]);
                    this.screenVector.set(config.SUNRISE_START_POSITION[0], config.SUNRISE_START_POSITION[1]);
                    Runtime.getInstance().getViewport().project(this.screenVector);
                    this.introRenderer.setLightPosition((int) this.screenVector.x, (int) this.screenVector.y);
                    this.time = 0;
                    this.state = STATE_SUNRISE;
                    this.graphicsTick(0);
                }
                break;
            case STATE_SUNRISE :
                if(!this.sunriseAnimation.isAnimationFinished(time)){
                    this.introRenderer.setLightColor(this.sunSpotAnimation.getKeyFrame(time));
                    this.introRenderer.setAmbiantColor(this.ambientColorAnimation.getKeyFrame(time));
                    this.tmpVector.add(this.sunriseAnimation.getKeyFrame(time));
                    this.screenVector.set(this.tmpVector);
                    Runtime.getInstance().getViewport().project(this.screenVector);
                    this.introRenderer.setLightPosition((int) this.screenVector.x, (int) this.screenVector.y);
                    this.skyLayer.setTime(time/this.sunriseAnimation.getAnimationDuration());
                }
                else{
                    this.backgroundOffScreenLayer.setOffScreenRendering(true);
                    this.foregroundOffScreenLayer.setOffScreenRendering(true);
                    this.landingSaucerActor.setPosition(config.SAUCER_LANDING_START_POSITION[0], config.SAUCER_LANDING_START_POSITION[1]);
                    this.saucerLayer.addActor(this.landingSaucerActor);
                    this.time = 0;
                    state = STATE_SAUCER_LANDING;
                    this.graphicsTick(0);
                }
                break;
            case STATE_SAUCER_LANDING :
                if(!this.landingSaucerAnimation.isAnimationFinished(time)){
                    this.landingSaucerActor.playAnimation(this.landingSaucerAnimation, time);
                }
                else{
                    this.softBuddyOffScreenLayer.setOffScreenRendering(true);
                    state = STATE_SOFTBUDDY_IN;
                }
                break;

        }
    }

    public void physicsTick(float deltaTime){
        switch (state){
            case STATE_SOFTBUDDY_IN :
                this.softBuddyActor.createGroup(this.landingSaucerActor.x + config.SOFTBUDDY_GROUP_START_OFFSET[0], this.landingSaucerActor.y + config.SOFTBUDDY_GROUP_START_OFFSET[1],SoftBuddyActor.DEFAULT_MAX_PARTICLES,config.SOFTBUDDY_GROUP_START_WIDTH);
                state = STATE_PLAY;
                break;
            case STATE_PLAY:
                this.tmpVector.set(-Gdx.input.getPitch(), 0);
                this.softBuddyActor.getParticleGroup().applyForce(Level01.this.tmpVector);
                break;
        }

    }
}
