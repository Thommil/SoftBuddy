package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.StaticActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.SoftBuddyGame;

public class SplashScreen extends AbstractScreen {

    final TextureSet textureSet;
    final SpriteBatchLayer spriteBatchLayer;

    public SplashScreen(Viewport viewport) {
        super(viewport);
        textureSet = new TextureSet(new Texture("textures/splashscreen.png"));
        textureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        spriteBatchLayer = new SpriteBatchLayer(viewport,1);
    }

    @Override
    public void show() {
        spriteBatchLayer.addActor(new StaticActor(0, textureSet,
                -SoftBuddyGame.WORLD_WIDTH/2, -SoftBuddyGame.WORLD_HEIGHT/2,
                SoftBuddyGame.WORLD_WIDTH, SoftBuddyGame.WORLD_HEIGHT,
                0,1,1,0,
                Color.WHITE.toFloatBits()));
    }

    @Override
    public void render(float delta) {
        spriteBatchLayer.render(delta);
    }

    @Override
    public void dispose() {
        spriteBatchLayer.dispose();
        textureSet.dispose();
    }
}
