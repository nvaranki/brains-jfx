package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javafx.concurrent.Task;

/**
 *
 * @author &copy; 2014 Николай Варанкин
 */
class ScreenToStorageTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ScreenToStorageTask.class );
    
    private final Атрибутный lock;
    private final Collection<AttributeAgent> agents;

    ScreenToStorageTask( Атрибутный lock, Collection<? extends AttributeAgent> agents )
    {
        this.lock = lock;
        this.agents = new ArrayList<>( agents );
        for( AttributeAgent agent : agents )
            agent.fromScreen();
    }

    @Override
    protected Void call() throws Exception
    {
        try( final Транзакция т = lock.транзакция() )
        {
            for( AttributeAgent agent : agents )
                agent.toStorage();
            т.завершить( true );
        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        //applied.setValue( true );
    }
    
    @Override
    protected void failed()
    {
        Throwable t = getException();
        LOGGER.getLogger().log( Level.SEVERE, "Failure to upload archive data.", t );
    }
    
}
