package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.*;
import javafx.concurrent.Task;
import com.varankin.brains.db.DbАтрибутный;

/**
 *
 * @author &copy; 2014 Николай Варанкин
 */
class StorageToScreenTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( StorageToScreenTask.class );
    
    private final DbАтрибутный lock;
    private final Collection<AttributeAgent> agents;

    StorageToScreenTask( DbАтрибутный lock, Collection<? extends AttributeAgent> agents )
    {
        this.lock = lock;
        this.agents = new ArrayList<>( agents );
    }

    @Override
    protected Void call() throws Exception
    {
        try( final Транзакция т = lock.транзакция() )
        {
            for( AttributeAgent agent : agents )
                agent.fromStorage();
            т.завершить( true );
        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        for( AttributeAgent agent : agents )
            agent.toScreen();
    }

    @Override
    protected void failed()
    {
        Throwable t = getException();
        LOGGER.getLogger().log( Level.SEVERE, "Failure to retreive archive data.", t );
    }
    
}
