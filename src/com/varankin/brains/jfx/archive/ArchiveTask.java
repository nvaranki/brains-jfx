package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.type.DbАрхив;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.brains.jfx.db.FxФабрика;
import com.varankin.brains.jfx.history.SerializableProvider;

import java.util.logging.*;
import javafx.concurrent.Task;

/**
 * {@linkplain Task Задача} получения доступа к архиву.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
public final class ArchiveTask extends Task<FxАрхив>
{
    private static final Logger LOGGER = Logger.getLogger( ArchiveTask.class.getName(), 
            ArchiveTask.class.getPackage().getName() + ".text" );
    
    private final SerializableProvider<DbАрхив> поставщик;

    public ArchiveTask( SerializableProvider<DbАрхив> provider )
    {
        поставщик = provider;
    }

    @Override
    protected FxАрхив call() throws Exception
    {
        return FxФабрика.getInstance().создать( поставщик.newInstance() );
    }
    
    @Override
    protected void scheduled()
    {
        LOGGER.log( Level.INFO, "task.archive.open.scheduled", поставщик );
    }
    
    @Override
    protected void succeeded()
    {
        FxАрхив архив = getValue();
        if( архив != null )
        {
            JavaFX jfx = JavaFX.getInstance();
            jfx.архивы.add( архив );
            jfx.history.archive.advance( поставщик );
            LOGGER.log( Level.INFO, "task.archive.open.succeeded", поставщик );
        }
        else
        {
            LOGGER.log( Level.SEVERE, "task.archive.open.failed", поставщик );
        }
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, "task.archive.open.failed" );
        lr.setParameters( new Object[]{ поставщик } );
        lr.setResourceBundle( LOGGER.getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.log( lr );
    }
    
}
