package com.Eddie.LANFighter.Utils;

import com.Eddie.LANFighter.Constants;
import com.Eddie.LANFighter.Entities.Client.*;
import com.Eddie.LANFighter.HUDRenderer;
import com.Eddie.LANFighter.Input.ControlHandler;
import com.Eddie.LANFighter.LANFighter;
import com.Eddie.LANFighter.Network.ControlsSender;
import com.Eddie.LANFighter.Network.Messages.ClientDetailsMessage;
import com.Eddie.LANFighter.Network.Messages.ControlsMessage;
import com.Eddie.LANFighter.Network.Messages.EntityState;
import com.Eddie.LANFighter.Network.Messages.GameStateMessage;
import com.Eddie.LANFighter.Network.StateProcessor;
import com.Eddie.LANFighter.Physics.WorldDebugRenderer;
import com.Eddie.LANFighter.Pools.MessageObjectPool;
import com.Eddie.LANFighter.Screens.MainMenuScreen;
import com.Eddie.LANFighter.Utils.EntityUtils.ActorType;
import com.Eddie.LANFighter.Utils.Event.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Client;

import java.util.concurrent.ConcurrentHashMap;

public class WorldRenderer
{
    private static final boolean DEBUG = false;
    public static int VIEWPORT_WIDTH = 525;
    public static int VIEWPORT_HEIGHT = 375;
    private WorldManager worldManager;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private FitViewport viewport;
    private TiledMap map;
    private boolean isServer;
    private SpriteBatch batch;
    private Client client;
    private ControlsSender controlsSender;
    public StateProcessor stateProcessor;
    private ConcurrentHashMap<Short, ClientEntity> worldMap;
    long previousTime;
    private WorldDebugRenderer debugRenderer;
    private int screenWidth;
    private int screenHeight;
    private short recentId;
    private float screenShakeX;
    private float screenShakeY;
    private float screenShakeTime;
    private LANFighter game;
    public SFXPlayer audioPlayer;
    public HUDRenderer hudRenderer;
    protected ControlHandler controlHandler;
    private byte previousButtonPresses;

    public WorldRenderer(WorldManager worldManager, Client client, LANFighter game)
    {
        worldMap = new ConcurrentHashMap<>();
        this.worldManager = worldManager;
        audioPlayer = new SFXPlayer();
        stateProcessor = new StateProcessor(client, worldMap, audioPlayer);
        
        if(worldManager != null)
        {
            debugRenderer = new WorldDebugRenderer(worldManager.getWorld());
            worldManager.setOutgoingEventListener(stateProcessor);
        }
        else
        {
            this.client = client;
        }
        
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        controlsSender = new ControlsSender();
        recentId = -2;
        screenShakeX = 0;
        screenShakeY = 0;
        screenShakeTime = 0;
        hudRenderer = new HUDRenderer();
        this.game = game;
    }

    public void loadLevel(String level, boolean isServer, String name)
    {
        this.isServer = isServer;
        map = new TmxMapLoader().load(level);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("terrain");
        VIEWPORT_WIDTH = (int) (layer.getTileWidth() * layer.getWidth());
        VIEWPORT_HEIGHT = (int) (layer.getTileHeight() * layer.getHeight());
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        renderer = new OrthogonalTiledMapRenderer(map);
        name = name.trim();
        if(name.length() == 0)
        {
            name = "Bob";
        }
        if(name.length() >= 10)
        {
            name = name.substring(0, 10);
        }
        ClientDetailsMessage clientDetails = new ClientDetailsMessage();
        clientDetails.name = name;
        clientDetails.protocolVersion = Constants.PROTOCOL_VERSION;
        
        if(isServer)
        {
            MapLayer collision = map.getLayers().get("collision");

            for(MapObject object : collision.getObjects())
            {
                worldManager.createWorldObject(object);
            }

            worldManager.addIncomingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, clientDetails));
        }
        else
        {
            client.sendTCP(clientDetails);
        }

        controlHandler = new ControlHandler();
    }

    @SuppressWarnings("unused")
    public void render(float delta)
    {
        if(screenShakeTime > 0)
        {
            screenShakeTime += delta;
            screenShakeX += MathUtils.random(-7, 7);
            screenShakeY += MathUtils.random(-7, 7);
            
            if(Math.abs(screenShakeX) > 10)
            {
                screenShakeX = Math.signum(screenShakeX) * 2;
            }
            
            if(Math.abs(screenShakeY) > 5)
            {
                screenShakeY = Math.signum(screenShakeY) * 1;
            }
            
            if(screenShakeTime > 0.2f)
            {
                screenShakeTime = 0;
                screenShakeX = 0;
                screenShakeY = 0;
            }
        }
        camera.setToOrtho(false, VIEWPORT_WIDTH + screenShakeX, VIEWPORT_HEIGHT + screenShakeY);

        viewport.apply();
        
        renderer.setView(camera);
        renderer.render();
        
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        renderObjects(delta);
        
        batch.end();
        
        if(isServer && DEBUG)
        {
            debugRenderer.render(camera.combined);
        }
        
        processControls();
        
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
    }

    private void renderObjects(float delta)
    {
        long currentTime = TimeUtils.nanoTime();
        float alpha = 0;
        GameStateMessage nextStateMessage;
        
        if(isServer)
        {
            alpha = 1;
            
            nextStateMessage = stateProcessor.stateQueue.get(stateProcessor.stateQueue.size() - 1);
        }
        else
        {
            stateProcessor.processStateQueue(currentTime);
            nextStateMessage = stateProcessor.getNextState();
            long nextTime = nextStateMessage.time;
            currentTime += stateProcessor.timeOffset;
            
            if(nextTime != previousTime)
            {
                alpha = (float) (currentTime - previousTime) / (float) (nextTime - previousTime);
            }
            
            if(currentTime > nextTime)
            {
                alpha = 1;
            }
        }

        for(EntityState state : nextStateMessage.states)
        {
            short id = recentId;
        
            if(!worldMap.containsKey(state.id) && state.id > recentId)
            {
                ClientEntity entity = null;
                
                if(EntityUtils.ByteToActorType(state.type) == ActorType.PLAYER)
                {
                    entity = new ClientPlayer(state.id, state.x, state.y, this);
                }
                else if(EntityUtils.ByteToActorType(state.type) == ActorType.ARROW)
                {
                    entity = new ClientAimArrow(state.id, state.x, state.y, this);
                }
                else if(EntityUtils.ByteToActorType(state.type) == ActorType.BULLET)
                {
                    entity = new ClientBullet(state.id, state.x, state.y, this);
                }
                else if(EntityUtils.ByteToActorType(state.type) == ActorType.BOMB)
                {
                    entity = new ClientBomb(state.id, state.x, state.y, this);
                }
                else
                {
                    Gdx.app.log("Error", "Unable to decode actor type.");
                    Gdx.app.exit();
                }
                
                worldMap.put(state.id, entity);
        
                id = (short) Math.max(id, state.id);
            }
        
            recentId = id;
        }

        for(EntityState state : nextStateMessage.states)
        {
            if(worldMap.get(state.id) != null)
            {
                worldMap.get(state.id).processState(state, alpha);
            }
        }
        
        for(ClientEntity entity : worldMap.values())
        {
            if(entity.destroy && entity instanceof ClientBomb)
            {
                shakeScreen();
            }
            
            if(entity.remove)
            {
                worldMap.remove(entity.id);
                continue;
            }
            
            entity.render(delta, batch);
        }
        
        previousTime = currentTime;
    }

    public void resize(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
        viewport.update(width, height);
        camera.update();
    }

    public void shakeScreen()
    {
        screenShakeTime = 0.01f;
    }

    public void dispose()
    {
        if(client != null)
        {
            client.stop();
        }
        
        batch.dispose();
        hudRenderer.dispose();
        audioPlayer.dispose();
        map.dispose();
        renderer.dispose();
    }

    private void processControls()
    {
        ControlsMessage message = controlsSender.sendControls(controlHandler);

        if(previousButtonPresses != message.buttonPresses)
        {
            if(isServer)
            {
                worldManager.addIncomingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, message));
            }
            else
            {
                client.sendUDP(message);
            }
            
            previousButtonPresses = message.buttonPresses;
        }

        if(controlHandler.closeButton())
        {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
