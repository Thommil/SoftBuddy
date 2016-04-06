package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.layer.Layer;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.levels.common.layers.SkyLayer;

public class Level01 extends Level{

    public static final String RESOURCES_FILE = "chapters/mountain/level01/resources.json";

    private float levelWorldWidth;
    private float levelWorldHeight;

    private float time;

    private float SCROLL_DOWN_START = 5 * 1000;

    private float END_TICK = 10 * 1000;

    private Layer ticklayer;
    private SkyLayer skyLayer;

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

    }

    @Override
    public void stop() {
        Runtime.getInstance().getViewport().getCamera().position.set(0,0,0);
        Runtime.getInstance().getViewport().apply();
    }

    @Override
    public void build(SoftBuddyGameAPI softBuddyGameAPI) {
        this.levelWorldWidth = Runtime.getInstance().getSettings().viewport.width;
        this.levelWorldHeight = Runtime.getInstance().getSettings().viewport.height * 2;
        this.ticklayer = new Layer(Runtime.getInstance().getViewport(),0) {
            @Override protected void onShow() {}
            @Override protected void onResize(int w, int h) {}
            @Override protected void onHide() {}

            @Override
            public void render(float deltaTime) {
                tick(deltaTime);
            }

            @Override public void dispose() {}
        };
        Runtime.getInstance().addLayer(this.ticklayer);
        super.build(softBuddyGameAPI);
    }

    @Override
    protected void buildBackground() {
        this.skyLayer = new SkyLayer(Runtime.getInstance().getViewport(), true, SkyLayer.MIDNIGHT, -this.levelWorldWidth/2, -this.levelWorldHeight/2, this.levelWorldWidth, this.levelWorldHeight);
        Runtime.getInstance().addLayer(this.skyLayer);
    }

    @Override
    protected void buildBuddy() {

    }

    @Override
    protected void buildDynamic() {

    }

    @Override
    protected void buildStatic() {

    }

    @Override
    public void dispose() {
        Runtime.getInstance().removeLayer(this.ticklayer);
        Runtime.getInstance().removeLayer(this.skyLayer);
        this.ticklayer.dispose();
        this.skyLayer.dispose();
    }

    public void tick(float deltaTime){
        time += deltaTime;
        this.skyLayer.setTime(time);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
