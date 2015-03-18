package com.Eddie.LANFighter.Screens;

import com.Eddie.LANFighter.LANFighter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen extends Screen
{
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private float totalTime = 0;
    
    public SplashScreen(LANFighter game)
    {
        super(game);
        show();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        font.draw(batch, "LAN", viewport.getWorldWidth() / 2 - 450, viewport.getWorldHeight() - 120);
        font.draw(batch, "Fighter", viewport.getWorldWidth() / 2 - 250, viewport.getWorldHeight() - 380);
        
        batch.end();
        
        if(totalTime < 2f)
        {
            totalTime += delta;
        }
        else
        {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void show()
    {
        font = game.getFont(150);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(960, 640, camera);
        camera.setToOrtho(false, 960, 640);
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
        font.dispose();
        batch.dispose();
    }
}
