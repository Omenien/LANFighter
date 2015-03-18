package com.Eddie.LANFighter.Utils;

import com.Eddie.LANFighter.Constants;
import com.Eddie.LANFighter.Entities.Server.ServerEntity;
import com.Eddie.LANFighter.Entities.Server.ServerPlayer;
import com.Eddie.LANFighter.Network.Messages.*;
import com.Eddie.LANFighter.Network.Messages.ServerStatusMessage.Status;
import com.Eddie.LANFighter.Physics.Body;
import com.Eddie.LANFighter.Physics.World;
import com.Eddie.LANFighter.Pools.MessageObjectPool;
import com.Eddie.LANFighter.Utils.Event.State;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManager
{
    private World world;
    private Server server;
    public ConcurrentHashMap<Integer, ServerPlayer> playerList;
    public ArrayList<ServerEntity> entities;
    private WorldManager worldManager = this;
    private ArrayList<Event> incomingEventQueue;
    private ArrayList<Event> outgoingEventQueue;
    private Listener serverListener;
    private Listener outgoingEventListener;
    private WorldBodyUtils worldBodyUtils;
    public CustomConnection dummyConnection;
    public short id;
    public AudioMessage audio;
    ArrayList<Vector2> playerPositions;

    public int i;

    public WorldManager(final Server server)
    {
        i = 0;

        playerPositions = new ArrayList<>();
        playerPositions.add(new Vector2(50, 85));
        playerPositions.add(new Vector2(395, 85));
        playerPositions.add(new Vector2(50, 230));
        playerPositions.add(new Vector2(395, 230));

        playerList = new ConcurrentHashMap<>();

        world = new World(new Vector2(0, -500f));

        serverListener = new WorldManagerServerListener();

        if(server != null)
        {
            server.addListener(serverListener);
        }

        this.server = server;

        entities = new ArrayList<>();

        incomingEventQueue = new ArrayList<>();
        outgoingEventQueue = new ArrayList<>();
        dummyConnection = new CustomConnection();
        incomingEventQueue.add(MessageObjectPool.getInstance().eventPool.obtain().set(State.CONNECTED, null));
        outgoingEventQueue.add(MessageObjectPool.getInstance().eventPool.obtain().set(State.CONNECTED, null));
        audio = new AudioMessage();

        worldBodyUtils = new WorldBodyUtils(worldManager);

        id = 0;
    }

    public void setOutgoingEventListener(Listener listener)
    {
        outgoingEventListener = listener;
    }

    public void update(float delta)
    {
        audio.reset();

        for(ServerEntity entity : entities)
        {
            entity.update(delta);
        }
        world.step(delta, 1, this);

        GameStateMessage gameStateMessage = MessageObjectPool.getInstance().gameStateMessagePool.obtain();
        for(ServerEntity entity : entities)
        {
            EntityState state = MessageObjectPool.getInstance().entityStatePool.obtain();
            entity.updateState(state);
            gameStateMessage.addNewState(state);
        }

        entities.addAll(worldBodyUtils.entities);
        worldBodyUtils.entities.clear();

        gameStateMessage.time = TimeUtils.nanoTime();
        if(server != null)
        {
            server.sendToAllTCP(gameStateMessage);
            if(audio.audio != 0)
            {
                server.sendToAllUDP(audio);
            }
        }
        
        addOutgoingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, gameStateMessage));
        addOutgoingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, audio));

        processEvents(serverListener, incomingEventQueue);
        processEvents(outgoingEventListener, outgoingEventQueue);
    }


    public World getWorld()
    {
        return world;
    }


    public ArrayList<ServerEntity> getEntities()
    {
        return entities;
    }

    private class WorldManagerServerListener extends Listener
    {

        @Override
        public void connected(com.esotericsoftware.kryonet.Connection connection)
        {
        }

        @Override
        public void received(Connection connection, Object object)
        {
            try
            {
                if(object instanceof ControlsMessage)
                {
                    playerList.get(connection.getID()).setCurrentControls((ControlsMessage) object);
                }
                if(object instanceof ClientDetailsMessage)
                {
                    updateClientDetails(connection, object);
                }
            }
            catch(Exception e)
            {
                ServerPlayer player = new ServerPlayer(id++, playerPositions.get(i).x, playerPositions.get(i).y, worldBodyUtils);
                i++;
                i %= playerPositions.size();
                playerList.put(connection.getID(), player);
                entities.add(player);
                if(object instanceof ClientDetailsMessage)
                {
                    updateClientDetails(connection, object);
                }
            }
        }

        @Override
        public void disconnected(Connection connection)
        {
            ServerPlayer player = playerList.get(connection.getID());
            
            if(player != null)
            {
                player.dispose();
                playerList.remove(connection.getID());
                entities.remove(player);
                if(server != null)
                {
                    server.sendToAllTCP(player.id);
                }
                addOutgoingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, player.id));
            }
        }
    }

    private void updateClientDetails(Connection connection, Object object)
    {
        int version = ((ClientDetailsMessage) object).protocolVersion;
        if(version != Constants.PROTOCOL_VERSION)
        {
            ServerStatusMessage message = new ServerStatusMessage();
            message.status = Status.DISCONNECT;
            if(version > Constants.PROTOCOL_VERSION)
            {
                message.toastText = "Please update server";
            }
            else if(version < Constants.PROTOCOL_VERSION)
            {
                message.toastText = "Please update client";
            }
        
            server.sendToTCP(connection.getID(), message);
        
            return;
        }

        playerList.get(connection.getID()).setName(((ClientDetailsMessage) object).name);
        
        PlayerNamesMessage players = new PlayerNamesMessage();
        
        for(ServerPlayer tempPlayer : playerList.values())
        {
            players.players.put(tempPlayer.id, tempPlayer.getName());
        }
        
        if(server != null)
        {
            server.sendToAllTCP(players);
        }
        
        addOutgoingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, players));
    }

    private void processEvents(Listener listener, ArrayList<Event> queue)
    {
        for(Event event : queue)
        {
            if(event.state == State.CONNECTED)
            {
                listener.connected(dummyConnection);
            }
            else if(event.state == State.RECEIVED)
            {
                listener.received(dummyConnection, event.object);
            }
            else if(event.state == State.DISCONNECTED)
            {
                listener.disconnected(dummyConnection);
            }
        }
        queue.clear();
    }

    public void addIncomingEvent(Event event)
    {
        incomingEventQueue.add(event);
    }

    public void addOutgoingEvent(Event event)
    {
        outgoingEventQueue.add(event);
    }

    public void createWorldObject(MapObject object)
    {
        worldBodyUtils.createWorldObject(object);
    }

    public void destroyBody(Body body)
    {
        entities.remove(body.getUserData());
        
        if(server != null)
        {
            server.sendToAllTCP(body.getUserData().id);
        }
        
        addOutgoingEvent(MessageObjectPool.getInstance().eventPool.obtain().set(State.RECEIVED, body.getUserData().id));
    }

    public void dispose()
    {
        if(server != null)
        {
            server.stop();
        }
    }
}

