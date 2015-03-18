package com.Eddie.LANFighter.Utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoader
{
    public static final AssetLoader instance = new AssetLoader();
    public AssetManager manager;

    public AssetLoader()
    {
        manager = new AssetManager();
    }

    public void loadAll()
    {
//        manager.load("Sprites/Player.png", Texture.class);
        manager.load("Sprites/Cowboy2.png", Texture.class);
        manager.load("Sprites/Arrow.png", Texture.class);
        manager.load("Sprites/Bullet.png", Texture.class);
        manager.load("Sprites/Bomb.png", Texture.class);
        manager.load("Sprites/Explosion.png", Texture.class);
    }

    public Texture getTexture(String path)
    {
        manager.finishLoading();
        return manager.get(path, Texture.class);
    }
    
    public static AssetLoader getInstance()
    {
        return instance;
    }
}
