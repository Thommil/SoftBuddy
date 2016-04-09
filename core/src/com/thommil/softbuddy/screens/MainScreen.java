package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
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
import com.thommil.softbuddy.SharedResources;
import com.thommil.softbuddy.SoftBuddyGameAPI;

public class MainScreen extends AbstractScreen implements InputProcessor{

    final SoftBuddyGameAPI softBuddyGameAPI;
    final AssetManager assetManager;
    final SharedResources.ScreenDef screenDef;


    SpriteBatchLayer currentBatchLayer = null;

    //Main menu
    SpriteBatchLayer mainMenuBatchLayer;
    TextureSet bgTextureSet;
    TextureSet fgTextureSet;

    private int selectedChapter;
    private int selectedLevel;
    private boolean restartLevel;


    ViewportLayout viewportLayout;
    TouchDispatcher touchDispatcher;

    public MainScreen(Viewport viewport, SoftBuddyGameAPI softBuddyGameAPI, AssetManager assetManager) {
        super(viewport);
        this.screenDef = softBuddyGameAPI.getSharedResources().getScreenDef("main");
        this.softBuddyGameAPI = softBuddyGameAPI;
        this.assetManager = assetManager;
        this.viewportLayout = new ViewportLayout(viewport);
        this.touchDispatcher = new TouchDispatcher(viewport);
        this.build();
    }

    protected void build(){
        this.bgTextureSet = new TextureSet(this.assetManager.get(this.screenDef.backgroundPath, Texture.class));
        this.bgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        this.fgTextureSet = new TextureSet(this.assetManager.get(this.screenDef.buttons[0].widget.texturePath, Texture.class));
        this.fgTextureSet.setWrapAll(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        //Main menu
        this.mainMenuBatchLayer = new SpriteBatchLayer(this.viewport,3);
        this.mainMenuBatchLayer.addActor(new SpriteActor(0,this.bgTextureSet));
        this.mainMenuBatchLayer.addActor(new ButtonActor(this.screenDef.buttons[0].name.hashCode(), this.fgTextureSet, this.screenDef.buttons[0].widget.region){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.startLevel(selectedChapter, selectedChapter, restartLevel);
                return true;
            }
        });
        this.mainMenuBatchLayer.addActor(new ButtonActor(this.screenDef.buttons[1].name.hashCode(), this.fgTextureSet, this.screenDef.buttons[1].widget.region){
            @Override
            public boolean onTouchDown(float worldX, float worldY, int button) {
                softBuddyGameAPI.quit();
                return true;
            }
        });

        this.selectedChapter = 0;
        this.selectedLevel = 0;
        this.currentBatchLayer = this.mainMenuBatchLayer;
    }

    public void showMainMenu(){
        if(this.currentBatchLayer != null) this.currentBatchLayer.hide();
        this.touchDispatcher.clear();
        this.touchDispatcher.bind();
        this.touchDispatcher.addListener((SpriteActor) this.mainMenuBatchLayer.getActor(this.screenDef.buttons[0].name.hashCode()));
        this.touchDispatcher.addListener((SpriteActor) this.mainMenuBatchLayer.getActor(this.screenDef.buttons[1].name.hashCode()));
        this.currentBatchLayer.show();
    }

    @Override
    public void show() {
        if(this.currentBatchLayer == null || this.currentBatchLayer == this.mainMenuBatchLayer) {
            showMainMenu();
        }
    }

    private void layout(){
        //Background
        SpriteActor actor = (SpriteActor) this.currentBatchLayer.getActor(0);
        Rectangle rec = actor.getBoundingRectangle();
        this.viewportLayout.layout(rec, ViewportLayout.Align.CENTER, ViewportLayout.Align.CENTER, true, true);
        actor.setPosition(rec.x, rec.y);
        actor.setSize(rec.width, rec.height);

        //Main menu
        if(this.currentBatchLayer == this.mainMenuBatchLayer) {
            //Buttons
            final Vector2 buttonPos = new Vector2();
            for(SharedResources.ButtonDef button : this.screenDef.buttons){
                actor = (SpriteActor) this.mainMenuBatchLayer.getActor(button.name.hashCode());
                buttonPos.set(button.position[0], button.position[1]);
                this.viewportLayout.adapt(buttonPos);
                actor.setSize(button.size[0], button.size[1]);
                actor.setCenter(buttonPos.x, buttonPos.y);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        this.viewportLayout.update(width, height);
        this.layout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.currentBatchLayer.render(delta);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            this.softBuddyGameAPI.quit();
        }
        return false;
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        this.mainMenuBatchLayer.dispose();
        this.bgTextureSet.dispose();
        this.fgTextureSet.dispose();
    }

    public static abstract class ButtonActor extends SpriteActor{
        public ButtonActor(int id, TextureSet textureSet, final int[] buttonData) {
            super(id, textureSet,
                    buttonData[0],buttonData[1],
                    buttonData[2],buttonData[3]);
        }
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
