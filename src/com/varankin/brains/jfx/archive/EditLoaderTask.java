package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.brains.jfx.editor.EditorController;
import com.varankin.util.LoggerX;
import java.util.logging.*;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

/**
 * Загрузчик элемента в графический редактор.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
class EditLoaderTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( EditLoaderTask.class );
    
    
    private final FxЭлемент<?> элемент;
    private final StringProperty название;
    private final EditorController controller;
    
    private volatile String текст;

    EditLoaderTask( FxЭлемент элемент, StringProperty название, EditorController controller )
    {
        this.элемент = элемент;
        this.название = название;
        this.controller = controller;
    }

    @Override
    protected Void call() throws Exception
    {
        try( Транзакция т = элемент.getSource().транзакция() )
        {
            //TODO блокировка?
            текст = элемент.название().getValue();
            controller.setContent( элемент );
            т.завершить( true );
        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        название.setValue( текст );
    }

    @Override
    protected void failed()
    {
        Throwable exception = getException();
        if( exception != null )
        {
            Throwable cause = exception.getCause();
            if( cause != null && cause != exception)
                LOGGER.log( Level.SEVERE, "002005007S", текст, cause );
            LOGGER.log( Level.SEVERE, "002005007S", текст, exception );
        }
    }
    
}
