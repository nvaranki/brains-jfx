package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.brains.jfx.editor.EditorController;
import com.varankin.util.LoggerX;
import java.util.logging.*;
import javafx.concurrent.Task;

/**
 * Загрузчик элемента в графический редактор.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class EditLoaderTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( EditLoaderTask.class );
    
    
    private final FxЭлемент<?> элемент;
    private final EditorController controller;
    
    EditLoaderTask( FxЭлемент<?> элемент, EditorController controller )
    {
        this.элемент = элемент;
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception
    {
        controller.setContent( элемент );
        return null;
    }

    @Override
    protected void succeeded()
    {
    }

    @Override
    protected void failed()
    {
        Throwable exception = getException();
        if( exception != null )
        {
            String текст = элемент.название().getValue();
            Throwable cause = exception.getCause();
            if( cause != null && cause != exception)
                LOGGER.log( Level.SEVERE, "002005007S", текст, cause );
            LOGGER.log( Level.SEVERE, "002005007S", текст, exception );
        }
    }
    
}
