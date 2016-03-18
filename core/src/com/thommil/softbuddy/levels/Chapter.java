package com.thommil.softbuddy.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public abstract class Chapter implements Disposable{

    final protected Array<Level> levels = new Array<Level>(false,10);

    public abstract void loadResources(final AssetManager assetManager);

    public Array<Level> getLevels(){
        return this.levels;
    }

    @Override
    public void dispose() {
        for(final Level level : this.levels){
            level.dispose();
        }
    }
}
