package com.thommil.softbuddy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.screen.AbstractScreen;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.Level;

public class LoadingScreen extends AbstractScreen implements com.thommil.libgdx.runtime.screen.LoadingScreen {

    public LoadingScreen(Viewport viewport) {
        super(viewport);
    }

    @Override
    public void onLoadProgress(float progress) {
        Gdx.app.log("","onLoadProgress "+progress);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

    }
}
