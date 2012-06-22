package com.varankin.brains.jfx;

import com.varankin.brains.db.Модуль;
import com.varankin.brains.Контекст;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.Stage;

/**
 * Контекст среды JavaFX.
 *
 * @author &copy; 2012 Николай Варанкин
 */
final class JavaFX 
{
    public final Контекст контекст;
    public final Stage платформа;
    //public final Позиционер позиционер;
    private final ObservableDataBaseModuleList dbmm;
    private final ExecutorService es;

    /**
     * @param платформа первичная платформа приложения JavaFX.
     * @param контекст  контекст приложения.
     */
    JavaFX( Stage платформа, Контекст контекст )
    {
        this.платформа = платформа;
        this.контекст = контекст;
        //this.позиционер = new Позиционер();
        es = new ThreadPoolExecutor( //TODO load preferences
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>() );
        dbmm = new ObservableDataBaseModuleList( new ArrayList<Модуль>() );
    }
    
    ObservableValue<ObservableList<Модуль>> getDataBaseModuleMonitor()
    {
        return dbmm;
    }
    
    /**
     * Открывает HTML-ссылку в стандартном браузере операционной системы.
     * 
     * @param href HTML-ссылка.
     * 
     * @throws IOException при невозможности вызвать браузер операционной системы.
     * @throws MalformedURLException при некорректной ссылке.
     * @throws URISyntaxException при некорректной ссылке.
     * @throws UnsupportedOperationException если десктоп не поддерживается.
     */
    void browse( String href ) throws MalformedURLException, URISyntaxException, IOException
    {
        if( Desktop.isDesktopSupported() )
        {
            Desktop.getDesktop().browse( new URL( href ).toURI() );
        }
        else
        {
            throw new UnsupportedOperationException( "No desktop supported." );
        }
    }

    double getDefaultGap()
    {
        return 10; //TODO params
    }

    /**
     * @return сервис для запуска {@linkplain Task задач}.
     */
    ExecutorService getExecutorService()
    {
        return es;
    }

    void старт()
    {
        es.submit( new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                dbmm.getValue().addAll( контекст.склад.модули() );
                return null;
            }
        } );
    }

    void стоп()
    {
        es.shutdown();
        //TODO контекст.стоп(); //мыслитель.стоп();
    }
    
    File getCurrentLocalDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }

    private class ObservableDataBaseModuleList extends ObservableValueBase<ObservableList<Модуль>>
    {
        final ObservableList<Модуль> value;

        ObservableDataBaseModuleList( List<Модуль> storage )
        {
            value = FXCollections.observableList( storage );
        }
        
        @Override
        public ObservableList<Модуль> getValue()
        {
            return value;
        }

    }
    
}
