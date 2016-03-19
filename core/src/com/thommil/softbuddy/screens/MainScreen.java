package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.actor.graphics.SpriteActor;
import com.thommil.libgdx.runtime.events.TouchDispatcher;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.ViewportLayout;
import com.thommil.libgdx.runtime.layer.SpriteBatchLayer;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.Resources;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public class MainScreen extends AbstractScreen {

    final SoftBuddyGameAPI softBuddyGameAPI;
    final JsonValue config;

    SpriteBatchLayer currentBatchLayer = null;

    //Main menu
    SpriteBatchLayer mainMenuBatchLayer;
    TextureSet bgTextureSet;
    TextureSet fgTextureSet;


    ViewportLayout viewportLayout;
    TouchDispatcher touchDispatcher;

    public MainScreen(Viewport viewport, SoftBuddyGameAPI softBuddyGameAPI) {
        super(viewport);
        this.config = new JsonReader().parse(Gdx.files.internal(Resources.SCREENS_RESOURCES_FILE)).get("mainscreen");
        this.softBuddyGameAPI = softBuddyGameAPI;
        this.viewportLayout = new ViewportLayout(viewport);
        this.touchDispatcher = new TouchDispatcher(viewport);
        this.build();
    }

    protected void build(){
        bgTextureSet = new TextureSet(new Texture(config.getString("background")));
        bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        fgTextureSet = new TextureSet(new Texture(config.getString("foreground")));
        fgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        //Main menu
        mainMenuBatchLayer = new SpriteBatchLayer(viewport,4);
        mainMenuBatchLayer.addActor(new SpriteActor(0,bgTextureSet));
        mainMenuBatchLayer.addActor(new ButtonActor(config.get("main").get("buttons").get("new").getInt("id"), fgTextureSet, config.get("main").get("buttons").get("new").get("region").asIntArray()){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.newGame();
                return true;
            }
        });
        mainMenuBatchLayer.addActor(new ButtonActor(config.get("main").get("buttons").get("resume").getInt("id"), fgTextureSet, config.get("main").get("buttons").get("resume").get("region").asIntArray()){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.resumeGame();
                return true;
            }
        });
        mainMenuBatchLayer.addActor(new ButtonActor(config.get("main").get("buttons").get("quit").getInt("id"), fgTextureSet, config.get("main").get("buttons").get("quit").get("region").asIntArray()){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.quitGame();
                return true;
            }
        });

        currentBatchLayer = mainMenuBatchLayer;
    }

    public void showMainMenu(){
        if(currentBatchLayer != null) currentBatchLayer.hide();
        touchDispatcher.clear();
        touchDispatcher.bind();
        touchDispatcher.addListener((SpriteActor) mainMenuBatchLayer.getActor(config.get("main").get("buttons").get("new").getInt("id")));
        touchDispatcher.addListener((SpriteActor) mainMenuBatchLayer.getActor(config.get("main").get("buttons").get("resume").getInt("id")));
        touchDispatcher.addListener((SpriteActor) mainMenuBatchLayer.getActor(config.get("main").get("buttons").get("quit").getInt("id")));
        currentBatchLayer.show();
    }

    @Override
    public void show() {
        if(currentBatchLayer == null || currentBatchLayer == mainMenuBatchLayer) {
            showMainMenu();
        }
    }

    private void layout(){
        //Background
        SpriteActor actor = (SpriteActor) currentBatchLayer.getActor(0);
        Rectangle rec = actor.getBoundingRectangle();
        viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.CENTER, true, true);
        actor.setPosition(rec.x, rec.y);
        actor.setSize(rec.width, rec.height);

        //Main menu
        if(currentBatchLayer == mainMenuBatchLayer) {
            //Buttons
            final Vector2 buttonPos = new Vector2();
            for (JsonValue button : config.get("main").get("buttons")) {
                actor = (SpriteActor) mainMenuBatchLayer.getActor(button.getInt("id"));
                buttonPos.set(button.get("pos").getFloat(0), button.get("pos").getFloat(1));
                viewportLayout.adapt(buttonPos);
                actor.setSize(button.get("size").getFloat(0), button.get("size").getFloat(1));
                actor.setCenter(buttonPos.x, buttonPos.y);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewportLayout.update(width, height);
        this.layout();
    }

    @Override
    public void render(float delta) {
        currentBatchLayer.render(delta);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        mainMenuBatchLayer.dispose();
        bgTextureSet.dispose();
        fgTextureSet.dispose();
    }

    public static abstract class ButtonActor extends SpriteActor{
        public ButtonActor(int id, TextureSet textureSet, final int[] buttonData) {
            super(id, textureSet,
                    buttonData[0],buttonData[1],
                    buttonData[2],buttonData[3]);
        }
    }

}
