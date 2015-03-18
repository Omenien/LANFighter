package com.Eddie.LANFighter.Utils;

public class EntityUtils
{
    public enum ActorType
    {
        ERROR, PLAYER, ARROW, BULLET, BOMB
    }

    public static byte actorTypeToByte(ActorType type)
    {
        switch(type)
        {
            case ERROR:
                return 0;
            
            case PLAYER:
                return 1;
            
            case ARROW:
                return 2;
            
            case BULLET:
                return 3;
            
            case BOMB:
                return 4;
        }
        
        return -1;
    }

    public static ActorType ByteToActorType(byte type)
    {
        switch(type)
        {
            case 1:
                return ActorType.PLAYER;
            
            case 2:
                return ActorType.ARROW;
            
            case 3:
                return ActorType.BULLET;
            
            case 4:
                return ActorType.BOMB;
        }
        
        return ActorType.ERROR;
    }

}
