package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.SpriteActor;
import com.thommil.libgdx.runtime.events.TouchDispatcher;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.SoftBuddyGameAPI;
import com.thommil.softbuddy.resources.Screens;

public class MainScreen extends AbstractScreen {

    final SoftBuddyGameAPI softBuddyGameAPI;

    TextureSet bgTextureSet;
    TextureSet buttonsTextureSet;
    SpriteBatchLayer spriteBatchLayer;
    ViewportLayout viewportLayout;
    TouchDispatcher touchDispatcher;

    public MainScreen(Viewport viewport, SoftBuddyGameAPI softBuddyGameAPI) {
        super(viewport);
        this.softBuddyGameAPI = softBuddyGameAPI;
        this.create();
    }

    public void create(){
        bgTextureSet = new TextureSet(new Texture(Screens.MAINSCREEN_BACKGROUND_TEXTURE));
        bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        buttonsTextureSet = new TextureSet(new Texture(Screens.MAINSCREEN_BUTTONS_TEXTURE));
        buttonsTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        spriteBatchLayer = new SpriteBatchLayer(viewport,4);
        viewportLayout = new ViewportLayout(viewport);

        //Background
        spriteBatchLayer.addActor(new SpriteActor(Screens.MAINSCREEN_BACKGROUND_ACTOR_ID,bgTextureSet));

        //Buttons
        spriteBatchLayer.addActor(new ButtonActor(Screens.MAINSCREEN_NEW_BUTTON_ACTOR_ID,buttonsTextureSet, Screens.MAINSCREEN_NEW_BUTTON_REGION){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.newGame();
                return true;
            }
        });
        spriteBatchLayer.addActor(new ButtonActor(Screens.MAINSCREEN_RESUME_BUTTON_ACTOR_ID,buttonsTextureSet, Screens.MAINSCREEN_RESUME_BUTTON_REGION){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.resumeGame();
                return true;
            }
        });
        spriteBatchLayer.addActor(new ButtonActor(Screens.MAINSCREEN_QUIT_BUTTON_ACTOR_ID,buttonsTextureSet, Screens.MAINSCREEN_QUIT_BUTTON_REGION){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.quitGame();
                return true;
            }
        });

        //events
        touchDispatcher = new TouchDispatcher(viewport);
        touchDispatcher.addListener((SpriteActor)spriteBatchLayer.getActor(Screens.MAINSCREEN_NEW_BUTTON_ACTOR_ID));
        touchDispatcher.addListener((SpriteActor)spriteBatchLayer.getActor(Screens.MAINSCREEN_RESUME_BUTTON_ACTOR_ID));
        touchDispatcher.addListener((SpriteActor)spriteBatchLayer.getActor(Screens.MAINSCREEN_QUIT_BUTTON_ACTOR_ID));
    }

    @Override
    public void show() {
        spriteBatchLayer.show();
    }

    private void layout(){
        //Background
        SpriteActor actor = (SpriteActor) spriteBatchLayer.getActor(Screens.MAINSCREEN_BACKGROUND_ACTOR_ID);
        Rectangle rec = actor.getBoundingRectangle();
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.CENTER, true, true);
        actor.setPosition(rec.x, rec.y);
        actor.setSize(rec.width, rec.height);

        //Buttons
        actor = (SpriteActor) spriteBatchLayer.getActor(Screens.MAINSCREEN_NEW_BUTTON_ACTOR_ID);
        rec = actor.getBoundingRectangle();
        rec.setSize(Screens.MAINSCREEN_BUTTONS_SIZE[0],Screens.MAINSCREEN_BUTTONS_SIZE[1]);
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.NONE);
        actor.setPosition(rec.x, viewportLayout.height/8);
        actor.setSize(rec.width, rec.height);

        actor = (SpriteActor) spriteBatchLayer.getActor(Screens.MAINSCREEN_RESUME_BUTTON_ACTOR_ID);
        rec = actor.getBoundingRectangle();
        rec.setSize(Screens.MAINSCREEN_BUTTONS_SIZE[0],Screens.MAINSCREEN_BUTTONS_SIZE[1]);
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.CENTER);
        actor.setPosition(rec.x, rec.y);
        actor.setSize(rec.width, rec.height);

        actor = (SpriteActor) spriteBatchLayer.getActor(Screens.MAINSCREEN_QUIT_BUTTON_ACTOR_ID);
        rec = actor.getBoundingRectangle();
        rec.setSize(Screens.MAINSCREEN_BUTTONS_SIZE[0],Screens.MAINSCREEN_BUTTONS_SIZE[1]);
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.NONE);
        actor.setPosition(rec.x, -viewportLayout.height/8 - rec.height);
        actor.setSize(rec.width, rec.height);
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
        Gdx.input.setInputProcessor(null);
        spriteBatchLayer.dispose();
        bgTextureSet.dispose();
        buttonsTextureSet.dispose();
    }

    public static abstract class ButtonActor extends SpriteActor{
        public ButtonActor(int id, TextureSet textureSet, final int[] buttonData) {
            super(id, textureSet,
                    buttonData[0],buttonData[1],
                    buttonData[2],buttonData[3]);
        }
    }

}
