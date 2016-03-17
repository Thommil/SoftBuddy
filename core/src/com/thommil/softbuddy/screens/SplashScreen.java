package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
        final Vector2 scalingVector = Scaling.fill.apply(640,400, 10,10);
        
        spriteBatchLayer.show();
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
