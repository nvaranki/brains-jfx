package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Транзакция;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javafx.concurrent.Task;
import com.varankin.brains.db.DbАтрибутный;

/**
 *
 * @author &copy; 2014 Николай Варанкин
 */
class ScreenToStorageTask extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ScreenToStorageTask.class );
    
    private final DbАтрибутный lock;
    private final Collection<AttributeAgent> agents;

    ScreenToStorageTask( DbАтрибутный lock, Collection<? extends AttributeAgent> agents )
    {
        this.lock = lock;
        this.agents = new ArrayList<>( agents );
        for( AttributeAgent agent : agents )
            agent.fromScreen();
    }

    @Override
    protected Void call() throws Exception
    {
        try( Транзакция т = lock.транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, lock );
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
