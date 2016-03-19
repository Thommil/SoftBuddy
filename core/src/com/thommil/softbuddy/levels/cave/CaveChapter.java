package com.thommil.softbuddy.levels.cave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;
import com.thommil.softbuddy.resources.Cave;

public class CaveChapter extends Chapter {

    @Override
    public void load(AssetManager assetManager) {
        Gdx.app.log("","load");
        assetManager.load(Cave.SPLASHSCREEN_BACKGROUND_TEXTURE, Texture.class);
    }

    @Override
    public Array<Level> getLevels() {
        this.levels.add(new CaveLevel1());
        return super.getLevels();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
