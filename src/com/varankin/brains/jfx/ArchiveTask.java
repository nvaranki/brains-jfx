package com.varankin.brains.jfx;

import com.varankin.brains.db.DbАрхив;
import com.varankin.io.container.Provider;

import java.util.logging.*;
import javafx.concurrent.Task;

/**
 * {@linkplain Task Задача} получения доступа к архиву.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class ArchiveTask extends Task<DbАрхив>
{
    private static final Logger LOGGER = Logger.getLogger( ArchiveTask.class.getName(), 
            ArchiveTask.class.getPackage().getName() + ".text" );
    
    private final Provider<DbАрхив> поставщик;

    public ArchiveTask( Provider<DbАрхив> provider )
    {
        поставщик = provider;
    }

    @Override
    protected DbАрхив call() throws Exception
    {
        return поставщик.newInstance();
    }
    
    @Override
    protected void scheduled()
    {
        LOGGER.log( Level.INFO, "task.archive.open.scheduled", поставщик );
    }
    
    @Override
    protected void succeeded()
    {
        DbАрхив архив = getValue();
        if( архив != null )
        {
            JavaFX jfx = JavaFX.getInstance();
            jfx.архивы.add( архив );
            jfx.historyArchive.advance( поставщик );
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
