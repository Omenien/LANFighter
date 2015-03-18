package com.Eddie.LANFighter;

import com.Eddie.LANFighter.Screens.SplashScreen;
import com.Eddie.LANFighter.Utils.AssetLoader;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class LANFighter extends ApplicationAdapter
{
    private Screen nextScreen;
    private Screen currentScreen;
    private int width;
    private int height;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontParameter fontParameter;

    public LANFighter()
    {
        super();
    }

    @Override
    public void create()
    {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Font/UI Font.ttf"));
        fontParameter = new FreeTypeFontParameter();

        AssetLoader.getInstance().loadAll();
        currentScreen = new SplashScreen(this);
        Gdx.audio.getClass();
    }

    @Override
    public void render()
    {
        float delta = Gdx.graphics.getDeltaTime();
        currentScreen.render(delta);
    }

    @Override
    public void resize(int width, int height)
    {
        currentScreen.resize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause()
    {
        currentScreen.pause();
    }

    @Override
    public void resume()
    {
        currentScreen.resume();
    }

    @Override
    public void dispose()
    {
        nextScreen.dispose();
        currentScreen.dispose();
        fontGenerator.dispose();
    }

    public void setScreen(Screen screen)
    {
        nextScreen = screen;
        currentScreen.dispose();
        currentScreen = nextScreen;
        currentScreen.resize(width, height);
    }

    public BitmapFont getFont(int size)
    {
        fontParameter.size = size;
        
        return fontGenerator.generateFont(fontParameter);
    }
}
