package com.thommil.softbuddy;

public interface SoftBuddyGameAPI {

    void load();
    float getLoadingProgress();
    void onLoaded();

    void startLevel(final int chapter, final int level, final boolean restart);
    void quit();
}
