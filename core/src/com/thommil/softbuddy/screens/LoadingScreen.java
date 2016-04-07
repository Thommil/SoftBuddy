package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.BitmapFontActor;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.BitmapFontBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public class LoadingScreen extends AbstractScreen {

    final SoftBuddyGameAPI softBuddyGameAPI;
    final AssetManager assetManager;
    final BitmapFontBatchLayer bitmapFontBatchLayer;
    final BitmapFontActor fontActor;
    final String textTemplate = "Loading - PROGRESS%";

    private boolean firstPass = true;

    public LoadingScreen(Viewport viewport, SoftBuddyGameAPI softBuddyGameAPI, AssetManager assetManager) {
        super(viewport);
        this.softBuddyGameAPI = softBuddyGameAPI;
        this.assetManager = assetManager;
        fontActor = new BitmapFontActor(0, assetManager.get(softBuddyGameAPI.getSharedResources().getLabelDef("loading").assetName, BitmapFont.class));
        bitmapFontBatchLayer = new BitmapFontBatchLayer(viewport, 1);
        fontActor.setText(this.textTemplate.replaceAll("PROGRESS", "00"));
        final Vector2 tmpVector = new Vector2();
        tmpVector.set(softBuddyGameAPI.getSharedResources().getLabelDef("loading").position[0], softBuddyGameAPI.getSharedResources().getLabelDef("loading").position[1]);
        ViewportLayout.adaptToScreen(SoftBuddyGameAPI.REFERENCE_SCREEN, tmpVector);
        fontActor.setPosition(tmpVector.x, tmpVector.y);
        this.bitmapFontBatchLayer.addActor(fontActor);
    }

    @Override
    public void show() {
        this.bitmapFontBatchLayer.show();
        firstPass = true;
    }

    @Override
    public void render(float delta) {
        if(!firstPass) {
            this.softBuddyGameAPI.load();
        }
        else{
            firstPass = false;
        }

        final int progress = Math.round(this.softBuddyGameAPI.getLoadingProgress() * 100f);
        if (progress < 10) {
            fontActor.setText(this.textTemplate.replaceAll("PROGRESS", "0" + progress));
        } else {
            fontActor.setText(this.textTemplate.replaceAll("PROGRESS", String.valueOf(progress)));
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        bitmapFontBatchLayer.render(delta);

        if (progress == 100) this.softBuddyGameAPI.onLoaded();
    }

    @Override
    public void resize(int width, int height) {
        bitmapFontBatchLayer.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        this.bitmapFontBatchLayer.hide();
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        fontActor.dispose();
        bitmapFontBatchLayer.dispose();
    }
}
