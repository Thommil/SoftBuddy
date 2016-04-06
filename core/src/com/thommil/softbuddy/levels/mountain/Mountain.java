package com.thommil.softbuddy.levels.mountain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.Array;
import com.thommil.softbuddy.levels.Chapter;
import com.thommil.softbuddy.levels.ChapterLoader;
import com.thommil.softbuddy.levels.Level;

public class Mountain extends Chapter {

    private static final String RESOURCES_FILE = "chapters/mountain/resources.json";
    public static final String RESOURCES_FONT_TITLE = "title.ttf";

    private final ChapterLoader chapterLoader = new ChapterLoader();

    @Override
    public void load(AssetManager assetManager) {
        this.chapterLoader.parse(Gdx.files.internal(RESOURCES_FILE));
        final ChapterLoader.FontDef titleFontDef = this.chapterLoader.getFontDef(RESOURCES_FONT_TITLE);
        final FreetypeFontLoader.FreeTypeFontLoaderParameter titleFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        titleFontParams.fontFileName = titleFontDef.path;
        titleFontParams.fontParameters.size = titleFontDef.size;
        assetManager.load(RESOURCES_FONT_TITLE, BitmapFont.class, titleFontParams);
    }

    @Override
    public void unload(AssetManager assetManager) {
        assetManager.unload(RESOURCES_FONT_TITLE);
    }

    @Override
    public Array<Level> getLevels() {
        if(this.levels == null) {
            this.levels = new Array<Level>(false,1);
            this.levels.add(new Level01());
        }
        return this.levels;
    }
}
