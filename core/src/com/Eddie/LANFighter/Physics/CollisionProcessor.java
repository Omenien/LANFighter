package com.Eddie.LANFighter.Physics;

import com.Eddie.LANFighter.Entities.EntityType.ExplodingWeaponEntityType;
import com.Eddie.LANFighter.Entities.EntityType.LivingEntityType;
import com.Eddie.LANFighter.Entities.EntityType.WeaponEntityType;

public class CollisionProcessor
{
    public static boolean jumpOn(Body body1, Body body2)
    {
        processWeapons(body1, body2);
        processWeapons(body2, body1);
        
        return false;
    }

    public static boolean jumpedOn(Body body1, Body body2)
    {
        processWeapons(body1, body2);
        processWeapons(body2, body1);
        
        return false;

    }

    public static boolean touchLeft(Body body1, Body body2)
    {
        processWeapons(body1, body2);
        processWeapons(body2, body1);
        
        return false;
    }

    public static boolean touchRight(Body body1, Body body2)
    {
        processWeapons(body1, body2);
        processWeapons(body2, body1);
        
        return false;
    }

    public static void processWeapons(Body body1, Body body2)
    {
        if(body1.getUserData() instanceof ExplodingWeaponEntityType)
        {
            if(body2.bodyType == Body.BodyType.DynamicBody)
            {
                if(body1.toDestroy)
                {
                    return;
                }
                
                ((ExplodingWeaponEntityType) body1.getUserData()).explode();
            }
        }
        else if(body1.getUserData() instanceof WeaponEntityType)
        {
            if(body2.toDestroy)
            {
                return;
            }
            if(body2.getUserData() instanceof LivingEntityType)
            {
                if(((LivingEntityType) body2.getUserData()).kill() && body2 != body1.getUserData().body)
                {
                    ((WeaponEntityType) body1.getUserData()).getShooter().addKill();
                }
            }
            
            body1.getUserData().dispose();
        }

        if(body2.getUserData() instanceof ExplodingWeaponEntityType)
        {
            if(body1.bodyType == Body.BodyType.DynamicBody)
            {
                if(body2.toDestroy)
                {
                    return;
                }

                ((ExplodingWeaponEntityType) body2.getUserData()).explode();
            }
        }
        else if(body2.getUserData() instanceof WeaponEntityType)
        {
            if(body1.toDestroy)
            {
                return;
            }
            
            if(body1.getUserData() instanceof LivingEntityType)
            {
                if(((LivingEntityType) body1.getUserData()).kill() && body1 != body2.getUserData().body)
                {
                    ((WeaponEntityType) body2.getUserData()).getShooter().addKill();
                }
            }

            body2.getUserData().dispose();
        }
    }
}

