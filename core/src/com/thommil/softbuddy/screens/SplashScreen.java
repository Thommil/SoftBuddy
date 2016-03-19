package com.thommil.softbuddy.screens;

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
    final ViewportLayout viewportLayout;

    public SplashScreen(Viewport viewport) {
        super(viewport);
        bgTextureSet = new TextureSet(new Texture(SPLASHSCREEN_BACKGROUND_TEXTURE));
        bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        spriteBatchLayer = new SpriteBatchLayer(viewport,1);
        viewportLayout = new ViewportLayout(viewport);
        this.create();
    }

    public void create(){
        spriteBatchLayer.addActor(new SpriteActor(0,bgTextureSet));
    }

    @Override
    public void show() {
        spriteBatchLayer.show();
    }

    private void layout(){
        final SpriteActor bgActor = (SpriteActor) spriteBatchLayer.getActor(0);
        final Rectangle rec = bgActor.getBoundingRectangle();
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.CENTER, true, true);
        bgActor.setPosition(rec.x, rec.y);
        bgActor.setSize(rec.width, rec.height);
    }

    @Override
    public void resize(int width, int height) {
        viewportLayout.update(width, height);
        this.layout();
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
}
