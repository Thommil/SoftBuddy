package com.thommil.softbuddy.levels;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;

public abstract class Level implements Disposable, InputProcessor{

    public abstract void buildBackground();

    public abstract void buildBuddy();

    public abstract void buildStatic();

    public abstract void buildObjects();

    public abstract void start();

    public abstract void restart();
}
