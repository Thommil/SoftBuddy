package com.thommil.softbuddy;

public interface SoftBuddyGameAPI {

    /**
     * Settings
     */
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
