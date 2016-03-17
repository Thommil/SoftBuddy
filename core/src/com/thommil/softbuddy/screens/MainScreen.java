package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.Runtime;
import com.thommil.libgdx.runtime.actor.graphics.SpriteActor;
import com.thommil.libgdx.runtime.actor.graphics.StaticActor;
import com.thommil.libgdx.runtime.events.TouchDispatcher;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.SoftBuddyGame;

public class MainScreen extends AbstractScreen implements InputProcessor {

    final TextureSet bgTextureSet;
    final TextureSet buttonsTextureSet;
    final SpriteBatchLayer spriteBatchLayer;
    final TouchDispatcher touchDispatcher;

    public MainScreen(Viewport viewport) {
        super(viewport);
        bgTextureSet = new TextureSet(new Texture("textures/mainscreen_bg.png"));
        bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        buttonsTextureSet = new TextureSet(new Texture("ui/buttons.png"));
        buttonsTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        spriteBatchLayer = new SpriteBatchLayer(viewport,1);
        touchDispatcher = new TouchDispatcher(viewport, this);
    }

    @Override
    public void show() {
        final Vector2 scalingVector = Scaling.fit.apply(viewport.getWorldWidth(),viewport.getWorldHeight(), 10,10);
        spriteBatchLayer.addActor(new StaticActor(0, bgTextureSet,
                -scalingVector.x/2, -scalingVector.y/2,
                scalingVector.x, scalingVector.y,
                0f,1f,1f,0f,
                Color.WHITE.toFloatBits()));
        spriteBatchLayer.addActor(new PlayButton(1, buttonsTextureSet,
                -3f, this.viewport.getWorldHeight()/2 - 1.5f,
                6f, 1.5f,
                0, 1/3f, 1f, 0f,
                Color.WHITE.toFloatBits()));

        touchDispatcher.addListener((PlayButton)spriteBatchLayer.getActor(1));

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
        buttonsTextureSet.dispose();
    }

    public static class PlayButton extends StaticActor{
        public PlayButton(int id, TextureSet textureSet, float x, float y, float width, float height, float u, float v, float u2, float v2, float color) {
            super(id, textureSet, x, y, width, height, u, v, u2, v2, color);
        }

        @Override
        public boolean onTouchDown(float worldX, float worldY, int button) {
            Gdx.app.log("","TOUCH");
            return super.onTouchDown(worldX, worldY, button);
        }
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
