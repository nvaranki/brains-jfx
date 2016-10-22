package com.varankin.brains.jfx.archive;

import com.varankin.biz.action.Действие;
import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.Импортировать;
import com.varankin.brains.appl.КаталогДействий;
import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import com.varankin.util.HistoryList;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 * {@linkplain Task Задача} импорта пакета в формате XML.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class ImportTask extends Task<Результат>
{
    private static final Logger LOGGER = Logger.getLogger( ImportTask.class.getName(), 
            ImportTask.class.getPackage().getName() + ".text" );
    private static final Действие<Импортировать.Контекст> ИМПОРТИРОВАТЬ = 
            (Действие)JavaFX.getInstance().контекст.действие( КаталогДействий.Индекс.ИмпортироватьXML );
    
    private volatile Импортировать.Контекст контекст;
    private final Provider<InputStream> источник;
    private final HistoryList<Provider<InputStream>> история;

    ImportTask( Provider<InputStream> provider, DbАрхив архив )
    {
        история = JavaFX.getInstance().historyXml;
        источник = provider;
        контекст = new Импортировать.Контекст( provider, архив );
    }

    @Override
    protected Результат call() throws Exception
    {
        return ИМПОРТИРОВАТЬ.выполнить( контекст );
    }

    @Override
    protected void succeeded()
    {
        Результат результат = getValue();
        if( результат.код() == Результат.НОРМА )
        {
            история.advance( источник );
            LOGGER.log( Level.INFO, "task.import.succeeded", источник );
        }
        else
        {
            LOGGER.log( Level.SEVERE, "task.import.failed", источник );
        }
    }

    @Override
    protected void failed()
    {
        LogRecord lr = new LogRecord( Level.SEVERE, "task.import.failed" );
        lr.setParameters( new Object[]{ источник } );
        lr.setResourceBundle( LOGGER.getResourceBundle() );
        lr.setThrown( getException() );
        LOGGER.log( lr );
    }
    
}
