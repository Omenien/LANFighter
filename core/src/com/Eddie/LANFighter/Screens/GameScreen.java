package com.Eddie.LANFighter.Screens;

import com.Eddie.LANFighter.Constants;
import com.Eddie.LANFighter.LANFighter;
import com.Eddie.LANFighter.Network.NetworkRegistrar;
import com.Eddie.LANFighter.Utils.WorldManager;
import com.Eddie.LANFighter.Utils.WorldRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.BindException;

public class GameScreen extends Screen
{
    private WorldManager world;
    private WorldRenderer renderer;
    private boolean isServer;
    private Server server;
    private Client client;

    public GameScreen(LANFighter game)
    {
        super(game);
        
        show();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(isServer)
        {
            world.update(delta);
        }

        renderer.render(delta);

        if(renderer.stateProcessor.disconnected)
        {
            Gdx.app.log("LANFighter", "Server disconnected.");

            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        renderer.resize(width, height);
    }

    @Override
    public void show()
    {
    }

    public void startServer()
    {
        isServer = true;
        
        server = new Server();
        
        NetworkRegistrar.register(server);
        
        server.start();
        
        try
        {
            server.bind(Constants.GAME_TCP_PORT, Constants.GAME_UDP_PORT);
        }
        catch(BindException e)
        {
            server.stop();
            
            server = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean loadLevel(String level, String host, String name)
    {
        if(isServer)
        {
            world = new WorldManager(server);
        }
        else
        {
            client = new Client();
            NetworkRegistrar.register(client);
            client.start();
            
            try
            {
                client.connect(Constants.TIMEOUT, host, Constants.GAME_TCP_PORT, Constants.GAME_UDP_PORT);
            }
            catch(IOException e)
            {
                Gdx.app.log("LANFighter", "Server not found.");

                e.printStackTrace();
                game.setScreen(new ClientDiscoveryScreen(game));
                return false;
            }
        }

        renderer = new WorldRenderer(world, client, game);
        renderer.loadLevel(level, isServer, name);
        return true;
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
        if(isServer)
        {
            world.dispose();
        }
        if(renderer != null)
        {
            renderer.dispose();
        }
    }
}
