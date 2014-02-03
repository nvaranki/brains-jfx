package com.varankin.brains.jfx.analyser;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 *
 * @author Николай
 */
public class TimeLineSetupController implements Builder<Parent>
{

    @Override
    public Parent build()
    {
        return new Pane();
    }

    boolean isApproved()
    {
        return true; //TODO
    }
    
}
