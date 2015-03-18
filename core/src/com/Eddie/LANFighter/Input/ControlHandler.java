package com.Eddie.LANFighter.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ControlHandler extends InputController
{
    private Stage stage;

    public ControlHandler()
    {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public boolean axisLeft()
    {
        return (InputController.getInstance().axisLeft());
    }

    @Override
    public boolean axisRight()
    {
        return (InputController.getInstance().axisRight());
    }

    public boolean axisUp()
    {
        return (InputController.getInstance().axisUp());
    }

    @Override
    public boolean axisDown()
    {
        return false;
    }

    @Override
    public boolean buttonA()
    {
        return controllerEnabled() ? InputController.getInstance().buttonA() : InputController.getInstance().axisUp();
    }

    @Override
    public boolean buttonB()
    {
        return (InputController.getInstance().buttonB());
    }

    @Override
    public boolean closeButton()
    {
        return (InputController.getInstance().closeButton());
    }

    @Override
    public boolean buttonX()
    {
        return (InputController.getInstance().buttonX());
    }

    public void render()
    {
        stage.draw();
    }

    public void dispose()
    {
    }
}