package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.SpriteActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;

public class SplashScreen extends AbstractScreen {

    public static final String SPLASHSCREEN_BACKGROUND_TEXTURE = "screens/splashscreen.png";

    final TextureSet bgTextureSet;
    final SpriteBatchLayer spriteBatchLayer;

    public SplashScreen(Viewport viewport) {
        super(viewport);
        this.bgTextureSet = new TextureSet(new Texture(SPLASHSCREEN_BACKGROUND_TEXTURE));
        this.bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        this.spriteBatchLayer = new SpriteBatchLayer(viewport,1);
        this.spriteBatchLayer.addActor(new SpriteActor(0,this.bgTextureSet, viewport.getWorldWidth(), viewport.getWorldHeight()));
    }

    @Override
    public void show() {
        this.spriteBatchLayer.show();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.spriteBatchLayer.render(delta);
    }

    @Override
    public void dispose() {
        this.spriteBatchLayer.dispose();
        this.bgTextureSet.dispose();
    }
}
