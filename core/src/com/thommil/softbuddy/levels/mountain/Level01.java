package com.thommil.softbuddy.levels.mountain;

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
import com.badlogic.gdx.utils.JsonValue;
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
import com.thommil.softbuddy.levels.common.actors.SoftBuddyActor;
import com.thommil.softbuddy.levels.mountain.renderers.IntroRenderer;

public class Level01 extends Level{

    public static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    public static class LevelConfiguration {

        public final String BACKGROUND_ID = "background";
        public final String FOREGROUND_ID = "foreground";
        public final String TITLE_ID = "title";
        public final String SCROLL_DOWN_ID = "scroll_down";
        public final String AMBIENT_ID = "ambient";
        public final String SUNRISE_SPOT_ID = "sunrise_spot";
        public final String FLYING_SAUCER_ID = "flying_saucer";
        public final String REACTOR_SPOT_ID = "reactor_spot";
        public final String SUNRISE_ID = "sunrise";
        public final String LANDING_SAUCER_ID = "landing_saucer";
        public final String SOFTBUFFY_ID = "softbuddy";

        public final float scrollDownStarsStep;
        public final float[] saucerFlyStartPositon;
        public final float[] reactorSpotFalloff;
        public final float[] reactorOffset;
        public final float[] reactorSpotOffset;
        public final float reactorScaleFactor;
        public final float[] sunriseSpotFalloff;
        public final float[] sunriseStartPosition;
        public final float[] saucerLandingStartPosition;
        public final float[] softbuddyGroupStartOffset;
        public final float softbuddyGroupStartWidth;
        public final Interpolation flyInterpolation;

        public LevelConfiguration(SceneLoader levelResources){
            final JsonValue jsonConfig = levelResources.getJsonRoot().get("configuration");
            this.scrollDownStarsStep = jsonConfig.get(SCROLL_DOWN_ID).getFloat("step");
            this.saucerFlyStartPositon = jsonConfig.get(FLYING_SAUCER_ID).get("start_position").asFloatArray();
            this.reactorSpotFalloff = jsonConfig.get(FLYING_SAUCER_ID).get("reactor_spot_falloff").asFloatArray();
            this.reactorOffset = jsonConfig.get(FLYING_SAUCER_ID).get("reactor_offset").asFloatArray();
            this.reactorScaleFactor = jsonConfig.get(FLYING_SAUCER_ID).getFloat("reactor_scale_factor");
            this.reactorSpotOffset = jsonConfig.get(FLYING_SAUCER_ID).get("reactor_spot_offset").asFloatArray();
            this.sunriseStartPosition = jsonConfig.get(SUNRISE_ID).get("start_position").asFloatArray();
            this.sunriseSpotFalloff = jsonConfig.get(SUNRISE_ID).get("spot_falloff").asFloatArray();
            this.saucerLandingStartPosition = jsonConfig.get(LANDING_SAUCER_ID).get("start_position").asFloatArray();
            this.softbuddyGroupStartOffset = jsonConfig.get(SOFTBUFFY_ID).get("start_position_offset").asFloatArray();
            this.softbuddyGroupStartWidth = jsonConfig.get(SOFTBUFFY_ID).getFloat("start_width");
            this.flyInterpolation = new Interpolation() {
                @Override
                public float apply(float a) {
                    return (float) (1 - Math.pow(1 - (2*a),5))/2;
                }
            };
        }
    }

    private static final int STATE_TITLE = 0;
    private static final int STATE_SCROLL_DOWN = 1;
    private static final int STATE_SAUCER_FLY = 2;
    private static final int STATE_SUNRISE = 3;
    private static final int STATE_SAUCER_LANDING = 4;
    private static final int STATE_SOFTBUDDY_IN = 5;
    private static final int STATE_PLAY = 6;

    private LevelConfiguration levelConfiguration;

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
        switch(this.state){
            case STATE_SCROLL_DOWN:
                Runtime.getInstance().getViewport().getCamera().position.set(0,this.tmpVector.y,0);
                Runtime.getInstance().getViewport().apply();
                break;
        }

        this.levelConfiguration.softbuddyGroupStartOffset[1] = 5;
        state = STATE_SAUCER_LANDING;
    }

    @Override
    public void stop() {
        switch(this.state){
            case STATE_SCROLL_DOWN:
                this.tmpVector.set(0, Runtime.getInstance().getViewport().getCamera().position.y);
                break;
        }
        Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
        Runtime.getInstance().getViewport().apply();
    }

    @Override
    protected void build() {
        this.levelConfiguration = new LevelConfiguration(Level.levelResources);
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
        this.scrollDownAnimation = (TranslateAnimation)Level.levelResources.getAnimation(levelConfiguration.SCROLL_DOWN_ID, this.assetManager);
        this.scrollDownAnimation.getKeyFrame(0).y = this.levelWorldHeight/2;
        this.sunSpotAnimation = (ColorAnimation) Level.levelResources.getAnimation(levelConfiguration.SUNRISE_SPOT_ID, this.assetManager);
        this.ambientColorAnimation = (ColorAnimation) Level.levelResources.getAnimation(levelConfiguration.AMBIENT_ID, this.assetManager);
        this.sunriseAnimation = (TranslateAnimation) Level.levelResources.getAnimation(levelConfiguration.SUNRISE_ID, this.assetManager);
        Runtime.getInstance().addLayer(this.skyLayer);

        this.backgroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1,this.introRenderer);
        final SceneLoader.ImageDef backgroundImageDef = Level.levelResources.getImageDefinition(levelConfiguration.BACKGROUND_ID);
        final Texture backgroundTexture = this.assetManager.get(backgroundImageDef.path, Texture.class);
        this.introRenderer.setNormalOffset(backgroundImageDef.normalOffset.x/backgroundTexture.getWidth(),backgroundImageDef.normalOffset.y/backgroundTexture.getHeight());
        final StaticActor backgroundActor = new StaticActor(levelConfiguration.BACKGROUND_ID.hashCode()
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
        this.softBuddyParticlesRenderer = new TexturedParticlesBatchRenderer(SoftBuddyGameAPI.PARTICLES_BATCH_SIZE);
        this.softBuddyLayer = new ParticlesBatchLayer(Runtime.getInstance().getViewport(),1, this.softBuddyParticlesRenderer);
        this.softBuddyActor = new SoftBuddyActor(0, this.softBuddyGameAPI.getSharedResources().getSoftBuddyDef(), this.assetManager);
        this.softBuddyLayer.addActor(this.softBuddyActor);
        this.softBuddyLayer.setScaleFactor(this.softBuddyGameAPI.getSharedResources().getSoftBuddyDef().particlesScaleFactor);
        this.softBuddyRenderer = new SoftBuddyRenderer(Runtime.getInstance().getViewport());
        this.softBuddyOffScreenLayer = new OffScreenLayer<ParticlesBatchLayer>(Runtime.getInstance().getViewport(),this.softBuddyLayer,this.softBuddyRenderer);
        Runtime.getInstance().addLayer(this.softBuddyOffScreenLayer);

        this.saucerLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1);
        final SceneLoader.ImageDef flyingSaucerImageDef = Level.levelResources.getImageDefinition(levelConfiguration.FLYING_SAUCER_ID);
        this.flyingSaucerActor = new SpriteActor(levelConfiguration.FLYING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(flyingSaucerImageDef.path, Texture.class))
                ,flyingSaucerImageDef.regionX
                ,flyingSaucerImageDef.regionY
                ,flyingSaucerImageDef.regionWidth
                ,flyingSaucerImageDef.regionHeight
                ,flyingSaucerImageDef.width
                ,flyingSaucerImageDef.height);
        this.flyingSaucerAnimation = (TranslateAnimation) Level.levelResources.getAnimation(levelConfiguration.FLYING_SAUCER_ID, this.assetManager);
        this.flyingSaucerAnimation.getKeyFrame(0).interpolation = levelConfiguration.flyInterpolation;
        this.flyingSaucerAnimation.getKeyFrame(1).interpolation = levelConfiguration.flyInterpolation;
        this.reactorSpotAnimation = (ColorAnimation) Level.levelResources.getAnimation(levelConfiguration.REACTOR_SPOT_ID, this.assetManager);
        final SceneLoader.ImageDef landingSaucerImageDef = Level.levelResources.getImageDefinition(levelConfiguration.LANDING_SAUCER_ID);
        this.landingSaucerActor = new SpriteActor(levelConfiguration.LANDING_SAUCER_ID.hashCode(), new TextureSet(this.assetManager.get(landingSaucerImageDef.path, Texture.class))
                ,landingSaucerImageDef.regionX
                ,landingSaucerImageDef.regionY
                ,landingSaucerImageDef.regionWidth
                ,landingSaucerImageDef.regionHeight
                ,landingSaucerImageDef.width
                ,landingSaucerImageDef.height);
        this.landingSaucerAnimation = (TranslateAnimation) Level.levelResources.getAnimation(levelConfiguration.LANDING_SAUCER_ID, this.assetManager);
        Runtime.getInstance().addLayer(this.saucerLayer);

        this.foregroundLayer = new SpriteBatchLayer(Runtime.getInstance().getViewport(),1, this.introRenderer);
        final SceneLoader.BodyDef foregroundBodyDef = Level.levelResources.getBodyDefintion(levelConfiguration.FOREGROUND_ID);
        final SceneLoader.ImageDef foregroundImageDef = Level.levelResources.getImageDefinition(levelConfiguration.FOREGROUND_ID);
        final StaticBodyActor foregroundStaticBodyActor = new StaticBodyActor(levelConfiguration.FOREGROUND_ID.hashCode()
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
                return Level.levelResources.getFixturesDefinition(foregroundBodyDef.index);
            }
        };

        this.foregroundLayer.addActor(foregroundStaticBodyActor);
        this.foregroundOffScreenRenderer = new OffScreenRenderer(Runtime.getInstance().getViewport(), Pixmap.Format.RGBA8888,true,true);
        this.foregroundOffScreenLayer = new OffScreenLayer<SpriteBatchLayer>(Runtime.getInstance().getViewport(),this.foregroundLayer, this.foregroundOffScreenRenderer);
        Runtime.getInstance().addLayer(this.foregroundOffScreenLayer);

        this.particlesEffectBatchLayer = new ParticlesEffectBatchLayer(Runtime.getInstance().getViewport(),1);
        this.particlesEffectBatchLayer.setAdditive(true);
        this.flyingSaucerParticlesEffect = Level.levelResources.getParticleEffect(levelConfiguration.FLYING_SAUCER_ID, this.assetManager);
        this.flyingSaucerParticlesActor = new ParticleEffectActor(levelConfiguration.FLYING_SAUCER_ID.hashCode(), this.flyingSaucerParticlesEffect,1);
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
        this.titleAnimation = (ColorAnimation) Level.levelResources.getAnimation(levelConfiguration.TITLE_ID, this.assetManager);
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

        this.levelConfiguration = null;
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
                    this.skyLayer.setStarsOffset(0,this.skyLayer.getStarsYOffset()- levelConfiguration.scrollDownStarsStep);
                    Runtime.getInstance().getViewport().getCamera().position.add(0,-this.scrollDownAnimation.getKeyFrame(time).y,0);
                    Runtime.getInstance().getViewport().apply();
                }
                else{
                    Runtime.getInstance().getViewport().getCamera().position.set(0, 0, 0);
                    Runtime.getInstance().getViewport().apply();
                    this.flyingSaucerActor.setPosition(levelConfiguration.saucerFlyStartPositon[0], levelConfiguration.saucerFlyStartPositon[1]);
                    this.saucerLayer.addActor(this.flyingSaucerActor);
                    this.introRenderer.switchLight(true);
                    this.introRenderer.setFallOff(levelConfiguration.reactorSpotFalloff[0], levelConfiguration.reactorSpotFalloff[1], levelConfiguration.reactorSpotFalloff[2]);
                    this.particlesEffectBatchLayer.addActor(this.flyingSaucerParticlesActor);
                    this.flyingSaucerParticlesEffect = this.flyingSaucerParticlesActor.spawn(true, levelConfiguration.saucerFlyStartPositon[0], levelConfiguration.saucerFlyStartPositon[1], false, false, levelConfiguration.reactorScaleFactor);
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
                        this.tmpVector.add(levelConfiguration.reactorSpotOffset[0], levelConfiguration.reactorSpotOffset[1]);
                        this.flyingSaucerParticlesEffect.setPosition(this.flyingSaucerActor.x + levelConfiguration.reactorOffset[0], this.flyingSaucerActor.y + levelConfiguration.reactorOffset[1]);
                    }
                    else {
                        this.tmpVector.set(this.flyingSaucerActor.x, this.flyingSaucerActor.y);
                        this.tmpVector.add(-levelConfiguration.reactorSpotOffset[0] + this.flyingSaucerActor.width, levelConfiguration.reactorSpotOffset[1]);
                        this.flyingSaucerParticlesEffect.setPosition(this.flyingSaucerActor.x + this.flyingSaucerActor.width - levelConfiguration.reactorOffset[0], this.flyingSaucerActor.y + levelConfiguration.reactorOffset[1]);
                    }
                    this.screenVector.set(this.tmpVector);
                    Runtime.getInstance().getViewport().project(this.screenVector);
                    this.introRenderer.setLightPosition((int) this.screenVector.x, (int) this.screenVector.y);
                }
                else{
                    this.saucerLayer.removeActor(this.flyingSaucerActor);
                    this.particlesEffectBatchLayer.removeActor(this.flyingSaucerParticlesActor);
                    this.introRenderer.setFallOff(levelConfiguration.sunriseSpotFalloff[0], levelConfiguration.sunriseSpotFalloff[1], levelConfiguration.sunriseSpotFalloff[2]);
                    this.introRenderer.setLightColor(this.sunSpotAnimation.getKeyFrame(0));
                    this.tmpVector.set(levelConfiguration.sunriseStartPosition[0], levelConfiguration.sunriseStartPosition[1]);
                    this.screenVector.set(levelConfiguration.sunriseStartPosition[0], levelConfiguration.sunriseStartPosition[1]);
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
                    this.landingSaucerActor.setPosition(levelConfiguration.saucerLandingStartPosition[0], levelConfiguration.saucerLandingStartPosition[1]);
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
                this.softBuddyActor.createGroup(this.landingSaucerActor.x + levelConfiguration.softbuddyGroupStartOffset[0], this.landingSaucerActor.y + levelConfiguration.softbuddyGroupStartOffset[1], levelConfiguration.softbuddyGroupStartWidth);
                state = STATE_PLAY;
                break;
            case STATE_PLAY:
                switch(currentDirection){
                    case LEFT:
                        this.tmpVector.set(-this.softBuddyActor.moveForce, 0);
                        break;
                    case RIGHT:
                        this.tmpVector.set(this.softBuddyActor.moveForce, 0);
                        break;
                    default :
                        this.tmpVector.set(0, 0);
                }
                this.softBuddyActor.getParticleGroup().applyForce(this.tmpVector);
        }
    }
}
