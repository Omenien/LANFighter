package com.Eddie.LANFighter.Utils;

import com.Eddie.LANFighter.Input.InputController;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ScreenButton
{
    public ScreenButton buttonAbove;
    public ScreenButton buttonBelow;
    public ScreenButton buttonRight;
    public ScreenButton buttonLeft;
    
    private String text;
    
    private boolean isActive = true;
    
    private float x;
    private float y;
    
    private float slackTime;

    public ScreenButton(String text, float x, float y)
    {
        this.text = text;
        this.x = x;
        this.y = y;
        isActive = false;
        slackTime = 0;
    }

    public void setActive(boolean active)
    {
        slackTime = 0;
        
        this.isActive = active;
    }

    public void render(SpriteBatch batch, BitmapFont font, float delta)
    {
        if(isActive)
        {
            font.setColor(1, 1, 1, 1);
            font.draw(batch, text, x, y);
        }
        else
        {
            font.setColor(0.5f, 0.5f, 0.5f, 1);
            font.draw(batch, text, x, y);
        }
        
        slackTime += delta;
    }

    public void setButtonAbove(ScreenButton buttonAbove)
    {
        this.buttonAbove = buttonAbove;
        buttonAbove.buttonBelow = this;
    }

    public void setButtonBelow(ScreenButton buttonBelow)
    {
        this.buttonBelow = buttonBelow;
        buttonBelow.buttonAbove = this;
    }

    public void setButtonRight(ScreenButton buttonRight)
    {
        this.buttonRight = buttonRight;
        buttonRight.buttonLeft = this;
    }

    public void setButtonLeft(ScreenButton buttonLeft)
    {
        this.buttonLeft = buttonLeft;
        buttonLeft.buttonRight = this;
    }

    public ScreenButton process()
    {
        ScreenButton pressedButton = null;
        if(slackTime > 0.2f)
        {
            if(InputController.getInstance().axisDown())
            {
                pressedButton = buttonBelow;
            }
            else if(InputController.getInstance().axisUp())
            {
                pressedButton = buttonAbove;
            }
            else if(InputController.getInstance().axisLeft())
            {
                pressedButton = buttonRight;
            }
            else if(InputController.getInstance().axisRight())
            {
                pressedButton = buttonLeft;
            }
            
            if(pressedButton != null)
            {
                isActive = false;
                pressedButton.setActive(true);
                return pressedButton;
            }
        }
        return this;
    }

    public boolean isPressed(Vector2 touchVector, BitmapFont font)
    {
        TextBounds bounds = font.getBounds(text);
        
        if(touchVector.x > x && touchVector.x < x + bounds.width && touchVector.y < y && touchVector.y > y - bounds.height)
        {
            return true;
        }
        
        return false;
    }

    public String getText()
    {
        return text;
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}
