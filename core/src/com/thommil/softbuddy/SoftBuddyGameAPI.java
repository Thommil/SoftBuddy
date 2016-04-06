package com.thommil.softbuddy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

public interface SoftBuddyGameAPI {

    /**
     * Settings
     */
    Vector2 REFERENCE_SCREEN = new Vector2(1024, 640);

    float WORLD_WIDTH = 16;
    float WORLD_HEIGHT = 10;
    float SAFE_WORLD_WIDTH = 13;
    float SAFE_WORLD_HEIGHT = 9;

    int SPRITE_BATCH_SIZE = 1000;

    /**
     * Screens
     */
    String SCREENS_RESOURCES_FILE = "screens/resources.json";

    /**
     * API
     */
    void load();
    float getLoadingProgress();
    void onLoaded();

    void startLevel(final int chapter, final int level, final boolean restart);
    void quit();
}
