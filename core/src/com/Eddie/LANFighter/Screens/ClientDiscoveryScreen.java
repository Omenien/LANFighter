package com.Eddie.LANFighter.Screens;

import com.Eddie.LANFighter.Constants;
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
import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientDiscoveryScreen extends Screen
{
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ArrayList<ScreenButton> buttons;
    private ScreenButton currentButton;
    private ScreenButton backButton;
    private ScreenButton refreshButton;
    private ScreenButton manualIPButton;
    private ArrayList<ScreenButton> ipAddresses;
    private boolean markForDispose;
    private Client client;
    private boolean pressedButton;

    public ClientDiscoveryScreen(LANFighter game)
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
        
        renderButtons(delta);
        
        batch.end();
        
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
        client = new Client();
        client.start();
        font = game.getFont(75);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(960, 640, camera);
        camera.setToOrtho(false, 960, 640);
        buttons = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        markForDispose = false;
        addAllButtons();
        addIpButtons();
        pressedButton = false;
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
        ipAddresses.clear();
        buttons.clear();
        batch.dispose();
        font.dispose();
        ipAddresses = null;
        buttons = null;
        font = null;
    }

    public void processInput()
    {
        currentButton = currentButton.process();
        
        if(InputController.getInstance().buttonA() || markForDispose)
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
        if(currentButton == backButton)
        {
            game.setScreen(new MainMenuScreen(game));
        }
        else if(currentButton == refreshButton)
        {
            addIpButtons();
        }
        else if(currentButton == manualIPButton)
        {
            if(pressedButton)
            {
                return;
            }
            
            pressedButton = true;

            Gdx.input.getTextInput(new TextInputListener()
            {
                @Override
                public void input(String text)
                {
                    joinGame(text);
                }

                @Override
                public void canceled()
                {
                    pressedButton = false;
                }
            }, "IP Address", "");
        }
        else
        {
            if(!pressedButton)
            {
                pressedButton = true;
                
                joinGame(currentButton.getText());
            }
        }
    }

    private void processTouched()
    {
        Vector2 touchVector = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        
        for(ScreenButton button : buttons)
        {
            if(button.isPressed(touchVector, font))
            {
                currentButton = button;
                processButton();
                return;
            }
        }
        
        for(ScreenButton button : ipAddresses)
        {
            if(button.isPressed(touchVector, font))
            {
                currentButton = button;
                processButton();
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
        for(ScreenButton button : buttons)
        {
            button.render(batch, font, delta);
        }
        
        for(ScreenButton button : ipAddresses)
        {
            button.render(batch, font, delta);
        }
    }

    private void addAllButtons()
    {
        refreshButton = addButton("Refresh", 50, 600);
        backButton = addButton("Back", 700, 60);
        manualIPButton = addButton("Enter IP", 50, 250);
        currentButton = refreshButton;
        currentButton.setActive(true);
        refreshButton.setButtonLeft(backButton);
        manualIPButton.setButtonAbove(backButton);
        manualIPButton.setButtonAbove(refreshButton);
    }

    private void addIpButtons()
    {
        Gdx.app.log("LANFighter", "Searching for servers...");
        manualIPButton.buttonAbove = null;
        ipAddresses.clear();
        ScreenButton previousButton = refreshButton;
        float y = 360;
        
        List<InetAddress> tempAddresses = client.discoverHosts(Constants.DISCOVERY_UDP_PORT, Constants.TIMEOUT);
        
        if(tempAddresses.size() == 0)
        {
            Gdx.app.log("LANFighter", "No servers found.");
        }

        for(InetAddress address : tempAddresses)
        {
            ScreenButton button = new ScreenButton(address.getHostName(), 300, y);
            ipAddresses.add(button);
            previousButton.setButtonBelow(button);
            previousButton = button;
            y -= 150;
        }
        
        manualIPButton.setPosition(300, y);
        manualIPButton.setButtonAbove(previousButton);
    }

    private void joinGame(final String host)
    {
        GameScreen gameScreen = new GameScreen(game);
        
        final Preferences prefs = Gdx.app.getPreferences("profile");
        
        if(gameScreen.loadLevel("Maps/newbricks.tmx", host, prefs.getString("name")))
        {
            game.setScreen(gameScreen);
        }
        else
        {
            gameScreen.dispose();
        }
    }
}
