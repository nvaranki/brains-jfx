package com.varankin.brains.jfx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 *
 * @author Николай
 */
class StageCloseHandler<T extends Event> implements EventHandler<T>
{
    private final Stage stage;
    private final boolean dispose;

    StageCloseHandler( Stage stage, boolean dispose )
    {
        this.stage = stage;
        this.dispose = dispose;
    }

    @Override
    public void handle( T _ )
    {
        if( dispose )
            stage.close();
        else
            stage.hide();
    }
    
}
