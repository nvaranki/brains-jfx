package com.varankin.brains.jfx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 *
 * @author Николай
 */
@Deprecated
public class StageCloseHandler<T extends Event> implements EventHandler<T>
{
    private final Stage stage;
    private final boolean dispose;

    public StageCloseHandler( Stage stage, boolean dispose )
    {
        this.stage = stage;
        this.dispose = dispose;
    }

    @Override
    public void handle( T __ )
    {
        if( dispose )
            stage.close();
        else
            stage.hide();
    }
    
}
