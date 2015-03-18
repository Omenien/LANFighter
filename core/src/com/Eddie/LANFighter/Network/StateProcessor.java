package com.Eddie.LANFighter.Network;

import com.Eddie.LANFighter.Entities.Client.ClientBomb;
import com.Eddie.LANFighter.Entities.Client.ClientEntity;
import com.Eddie.LANFighter.Network.Messages.AudioMessage;
import com.Eddie.LANFighter.Network.Messages.GameStateMessage;
import com.Eddie.LANFighter.Network.Messages.PlayerNamesMessage;
import com.Eddie.LANFighter.Network.Messages.ServerStatusMessage;
import com.Eddie.LANFighter.Pools.MessageObjectPool;
import com.Eddie.LANFighter.Utils.SFXPlayer;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateProcessor extends Listener
{
    private static final int QUEUE_LENGTH = 6;
    private Client client;
    public ArrayList<GameStateMessage> stateQueue;
    public long timeOffset = 0;
    private GameStateMessage nextState;
    int lag = 0;
    AtomicBoolean wait;
    ConcurrentHashMap<Short, ClientEntity> world;
    public boolean disconnected;
    private SFXPlayer audioPlayer;
    public PlayerNamesMessage playerNames;

    public StateProcessor(Client client, ConcurrentHashMap<Short, ClientEntity> worldMap, SFXPlayer audioPlayer)
    {
        if(client != null)
        {
            this.client = client;
            client.addListener(this);
        }
        
        nextState = MessageObjectPool.getInstance().gameStateMessagePool.obtain();
        nextState.time = 0;
        stateQueue = new ArrayList<>();
        wait = new AtomicBoolean(false);
        this.world = worldMap;
        disconnected = false;
        this.audioPlayer = audioPlayer;
        playerNames = new PlayerNamesMessage();
    }

    @Override
    public void connected(Connection connection)
    {
    }

    @Override
    public void received(Connection connection, Object object)
    {
        if(object instanceof GameStateMessage)
        {
            addNewState((GameStateMessage) object);
        }
        else if(object instanceof Short)
        {
            if(world.get(object) != null)
            {
                world.get(object).destroy = true;
                
                if(!(world.get(object) instanceof ClientBomb))
                {
                    world.get(object).remove = true;
                }
            }
        }
        else if(object instanceof AudioMessage)
        {
            audioPlayer.playAudioMessage((AudioMessage) object);
        }
        else if(object instanceof PlayerNamesMessage)
        {
            this.playerNames = (PlayerNamesMessage) object;
        }
        else if(object instanceof ServerStatusMessage)
        {
            ServerStatusMessage message = (ServerStatusMessage) object;
            Gdx.app.log("LANFighter", message.toastText);
            disconnected = true;
        }
        else if(object instanceof String)
        {
            Gdx.app.log("LANFighter", object.toString());
        }
        super.received(connection, object);
    }

    @Override
    public void disconnected(Connection connection)
    {
        disconnected = true;
    }

    public void addNewState(GameStateMessage state)
    {
        if(wait == null)
        {
            wait = new AtomicBoolean(false);
        }
        
        if(stateQueue == null)
        {
            stateQueue = new ArrayList<>();
        }
        
        while(!wait.compareAndSet(false, true))
        {
        }

        if(stateQueue.size() == 0)
        {
            stateQueue.add(state);
        }
        
        for(int i = stateQueue.size() - 1; i >= 0; i--)
        {
            if(stateQueue.get(i).time < state.time)
            {
                stateQueue.add(i + 1, state);
                
                break;
            }
        }
        
        wait.set(false);
    }

    public void processStateQueue(long currentTime)
    {
        while(!wait.compareAndSet(false, true))
        {
        }
        
        if(stateQueue.size() < QUEUE_LENGTH)
        {
            wait.set(false);
            return;
        }

        while(stateQueue.size() > QUEUE_LENGTH)
        {
            stateQueue.remove(0);
        }

        long currentServerTime = currentTime + timeOffset;
        
        if(currentServerTime < stateQueue.get(0).time)
        {
            lag++;
        
            if(lag > 3)
            {
                lag = 0;
                timeOffset = stateQueue.get(QUEUE_LENGTH - 2).time - currentTime;
                currentServerTime = currentTime + timeOffset;
            }
        }
        else if(currentServerTime > stateQueue.get(QUEUE_LENGTH - 1).time)
        {
            lag++;
        
            if(lag > 3)
            {
                lag = 0;
                timeOffset -= 10000;
                currentServerTime = currentTime + timeOffset;
            }
        }
        else
        {
            lag = 0;
        }
        
        for(GameStateMessage state : stateQueue)
        {
            this.nextState = state;
        
            if(state.time > currentServerTime)
            {
                break;
            }
        }
    
        wait.set(false);
    }

    public GameStateMessage getNextState()
    {
        return nextState;
    }

    @SuppressWarnings("unused")
    public void removeListener()
    {
        client.removeListener(this);
    }
}
