package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.util.LoggerX;
import java.util.logging.Level;
import javafx.concurrent.Task;

/**
 * Задача по закрытию доступа к архиву и освобождения связанных ресурсов.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
class TaskCloseArchive extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger(TaskCloseArchive.class );
    
    private final FxАрхив архив;
    private final String расположение;

    TaskCloseArchive( FxАрхив архив ) 
    {
        this.архив = архив;
        this.расположение = архив.расположение().getValue();
    }

    @Override
    protected Void call() throws Exception 
    {
        архив.getSource().закрыть();
        LOGGER.log( Level.WARNING, "task.archive.closed", расположение );
        return null;
    }

    @Override
    protected void failed()
    {
        LOGGER.log( Level.SEVERE, LOGGER.text( "task.archive.close.failed", расположение ), getException() );
    }

}
