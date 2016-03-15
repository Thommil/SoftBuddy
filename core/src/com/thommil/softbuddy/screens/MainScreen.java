package com.thommil.softbuddy.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.StaticActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.SoftBuddyGame;

public class MainScreen extends AbstractScreen implements InputProcessor {

    final TextureSet bgTextureSet;
    final SpriteBatchLayer spriteBatchLayer;

    public MainScreen(Viewport viewport) {
        super(viewport);
        bgTextureSet = new TextureSet(new Texture("textures/mainscreen_bg.png"));
        bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        spriteBatchLayer = new SpriteBatchLayer(viewport,1);
    }

    @Override
    public void show() {
        spriteBatchLayer.addActor(new StaticActor(0, bgTextureSet,
                -SoftBuddyGame.WORLD_WIDTH/2, -SoftBuddyGame.WORLD_HEIGHT/2,
                SoftBuddyGame.WORLD_WIDTH, SoftBuddyGame.WORLD_HEIGHT,
                0,1,1,0,
                Color.WHITE.toFloatBits()));

        spriteBatchLayer.show();
    }

    @Override
    public void render(float delta) {
        spriteBatchLayer.render(delta);
    }


    @Override
    public void dispose() {
        spriteBatchLayer.dispose();
        bgTextureSet.dispose();
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
