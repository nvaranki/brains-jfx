package com.varankin.brains.jfx.archive.action;

import com.varankin.biz.action.Результат;
import com.varankin.biz.action.РезультатТипа;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.db.type.DbПакет;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.brains.jfx.db.FxПакет;
import com.varankin.brains.jfx.history.SerializableProvider;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 * {@linkplain Task Задача} импорта пакета в формате XML.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
class ImportTask extends Task<Boolean>
{
    private static final Logger LOGGER = Logger.getLogger( ImportTask.class.getName(), 
            ImportTask.class.getPackage().getName() + ".text" );
    private static final Импортировать ИМПОРТИРОВАТЬ = new Импортировать();
    
    private final Импортировать.Контекст контекст;
    private final SerializableProvider<InputStream> поставщик;
    private final FxАрхив архив;

    ImportTask( SerializableProvider<InputStream> provider, FxАрхив архив )
    {
        поставщик = provider;
        this.архив = архив;
        контекст = new Импортировать.Контекст( provider, архив.getSource() );
    }

    @Override
    protected Boolean call() throws Exception
    {
        try( final Транзакция транзакция = архив.getSource().транзакция() )
        {
            транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив.getSource() );
            РезультатТипа<DbПакет> результат = ИМПОРТИРОВАТЬ.выполнить( контекст );
            DbПакет пакет = результат.значение();
            boolean фиксация = результат.код() == Результат.НОРМА && пакет != null 
                    && архив.пакеты().add( new FxПакет( пакет ) );
            транзакция.завершить( фиксация );
            return фиксация;
        }
    }

    @Override
    protected void scheduled()
    {
        LOGGER.log( Level.INFO, "task.import.scheduled", поставщик );
    }
    
    @Override
    protected void succeeded()
    {
        if( getValue() )
        {
            JavaFX.getInstance().history.xml.advance( поставщик );
            LOGGER.log( Level.INFO, "task.import.succeeded", поставщик );
        }
        else
        {
            LOGGER.log( Level.SEVERE, "task.import.failed", поставщик );
        }
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, "task.import.failed" );
        lr.setParameters( new Object[]{ поставщик } );
        lr.setResourceBundle( LOGGER.getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.log( lr );
    }
    
}
