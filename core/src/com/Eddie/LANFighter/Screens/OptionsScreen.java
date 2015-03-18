package com.Eddie.LANFighter.Screens;

import com.Eddie.LANFighter.Input.InputController;
import com.Eddie.LANFighter.LANFighter;
import com.Eddie.LANFighter.Utils.ScreenButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class OptionsScreen extends Screen
{
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ArrayList<ScreenButton> buttons;
    private ScreenButton currentButton;
    private ScreenButton backButton;
    private ScreenButton changeNameButton;
    private boolean buttonPressed;

    public OptionsScreen(LANFighter game)
    {
        super(game);
        show();
    }

    @Override
    public void render(float delta)
    {
        renderButtons(delta);
        processInput();
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
        font = game.getFont(75);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(960, 640, camera);
        camera.setToOrtho(false, 960, 640);
        buttons = new ArrayList<>();
        addAllButtons();
        buttonPressed = false;
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
        batch.dispose();
        font.dispose();
    }

    public void processInput()
    {
        currentButton = currentButton.process();
        
        if(InputController.getInstance().buttonA())
        {
            processButton();
            return;
        }
        
        if(Gdx.input.isTouched())
        {
            processTouched();
        }
    }

    private void processButton()
    {
        if(buttonPressed)
        {
            return;
        }
        
        if(currentButton == changeNameButton)
        {
            final Preferences prefs = Gdx.app.getPreferences("profile");
            buttonPressed = true;
            Gdx.input.getTextInput(new TextInputListener()
            {
                @Override
                public void input(String text)
                {
                    prefs.putString("name", text);
                    prefs.flush();
                    buttonPressed = false;
                }

                @Override
                public void canceled()
                {
                    buttonPressed = false;
                }
            }, "Username", prefs.getString("name"));
        }
        else if(currentButton == backButton)
        {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void processTouched()
    {
        Vector2 touchVector = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        for(ScreenButton button : buttons)
        {
            if(button.isPressed(touchVector, font))
            {
                if(currentButton == button)
                {
                    processButton();
                    return;
                }
                currentButton.setActive(false);
                currentButton = button;
                currentButton.setActive(true);
                renderButtons(0.01f);
                return;
            }
        }
    }

    public ScreenButton addButton(String text, float x, float y)
    {
        ScreenButton button = new ScreenButton(text, x, y);
        buttons.add(button);
        return button;
    }

    private void renderButtons(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(ScreenButton button : buttons)
        {
            button.render(batch, font, delta);
        }
        batch.end();
    }

    private void addAllButtons()
    {
        changeNameButton = addButton("Change Name", 50, 450);
        backButton = addButton("Back", 50, 250);
        changeNameButton.setActive(true);
        currentButton = changeNameButton;
        backButton.setButtonAbove(changeNameButton);
    }
}
