package com.Eddie.LANFighter.Utils;

import com.Eddie.LANFighter.Network.Messages.AudioMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SFXPlayer
{
    private Sound jump;
    private Sound shoot;
    private Sound explode;
    private Sound hurt;

    public SFXPlayer()
    {
        jump = Gdx.audio.newSound(Gdx.files.internal("Sounds/Jump.ogg"));
        shoot = Gdx.audio.newSound(Gdx.files.internal("Sounds/Shoot.ogg"));
//        explode = Gdx.audio.newSound(Gdx.files.internal("Sounds/Explode.ogg"));
        explode = Gdx.audio.newSound(Gdx.files.internal("Sounds/Destroy Everything.ogg"));
//        hurt = Gdx.audio.newSound(Gdx.files.internal("Sounds/Hurt.ogg"));
        hurt = Gdx.audio.newSound(Gdx.files.internal("Sounds/Hellscream.ogg"));
    }

    public void jump()
    {
        jump.play();
    }

    public void shoot()
    {
        shoot.play();
    }

    public void hurt()
    {
        hurt.play();
    }

    public void explode()
    {
        explode.play();
    }

    public void dispose()
    {
        jump.dispose();
        shoot.dispose();
        hurt.dispose();
        explode.dispose();
    }

    public void playAudioMessage(AudioMessage message)
    {
        if(message.getJump())
        {
            jump();
        }
        
        if(message.getShoot())
        {
            shoot();
        }
        
        if(message.getHurt())
        {
            hurt();
        }
        
        if(message.getExplode())
        {
            explode();
        }
    }
}
