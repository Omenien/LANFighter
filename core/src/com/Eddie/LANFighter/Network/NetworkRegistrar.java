package com.Eddie.LANFighter.Network;

import com.Eddie.LANFighter.Network.Messages.*;
import com.Eddie.LANFighter.Pools.MessageObjectPool;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryonet.EndPoint;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkRegistrar
{
    static public void register(EndPoint endPoint)
    {
        Registration registration;
        Kryo kryo = endPoint.getKryo();
        registration = kryo.register(ConnectMessage.class);
        registration.setInstantiator(new ObjectInstantiator()
        {
            @Override
            public Object newInstance()
            {
                return MessageObjectPool.getInstance().connectMessagePool.obtain();
            }
        });

        registration = kryo.register(ControlsMessage.class);
        registration.setInstantiator(new ObjectInstantiator()
        {
            @Override
            public Object newInstance()
            {
                return MessageObjectPool.getInstance().controlsMessagePool.obtain();
            }
        });

        registration = kryo.register(EntityState.class);
        registration.setInstantiator(new ObjectInstantiator()
        {
            @Override
            public Object newInstance()
            {
                return MessageObjectPool.getInstance().entityStatePool.obtain();
            }
        });

        registration = kryo.register(GameStateMessage.class);
        registration.setInstantiator(new ObjectInstantiator()
        {
            @Override
            public Object newInstance()
            {
                return MessageObjectPool.getInstance().gameStateMessagePool.obtain();
            }
        });

        registration = kryo.register(AudioMessage.class);
        registration.setInstantiator(new ObjectInstantiator()
        {
            @Override
            public Object newInstance()
            {
                return MessageObjectPool.getInstance().audioMessagePool.obtain();
            }
        });

        kryo.register(PlayerNamesMessage.class);
        kryo.register(ClientDetailsMessage.class);
        kryo.register(ServerStatusMessage.class);
        kryo.register(ServerStatusMessage.Status.class);
        kryo.register(ArrayList.class);
        kryo.register(Vector2.class);
        kryo.register(String.class);
        kryo.register(HashMap.class);
    }
}