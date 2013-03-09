package com.varankin.brains.jfx;

import com.varankin.brains.db.Модуль_;
import com.varankin.brains.db.Проект;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import javafx.application.Platform;
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
    private final ObservableObjectList<Модуль_> dbmm;
    private final ObservableObjectList<Проект> dbpm;
    private final ObservableObjectList<TitledSceneGraph> views;
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
        dbmm = new ObservableObjectList<>( new ArrayList<Модуль_>() );
        dbpm = new ObservableObjectList<>( new ArrayList<Проект>() );
        views = new ObservableObjectList<>( new ArrayList<TitledSceneGraph>() );
    }
    
    ObservableValue<ObservableList<Модуль_>> getDataBaseModuleMonitor()
    {
        return dbmm;
    }
    
    ObservableValue<ObservableList<Проект>> getDataBaseProjectMonitor()
    {
        return dbpm;
    }

    ObservableValue<ObservableList<TitledSceneGraph>> getViews()
    {
        return views;
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
//        Platform.runLater( new Task<Void>() //TODO re-design
//        {
//            @Override
//            protected Void call() throws Exception
//            {
                dbmm.getValue().addAll( контекст.архив.модули() );
                for( Проект проект : контекст.архив.проекты() )
                    dbpm.getValue().add( проект );
//                return null;
//            }
//        } );
    }

    void стоп()
    {
        es.shutdown();
        //TODO контекст.стоп(); //мыслитель.стоп();
    }
    
    Текст словарь( Class<?> c )
    {
        return Текст.ПАКЕТЫ.словарь( c, контекст.специфика );
    }

    File getCurrentLocalDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }

    private class ObservableObjectList<T> extends ObservableValueBase<ObservableList<T>>
    {
        final ObservableList<T> value;

        ObservableObjectList( List<T> storage )
        {
            value = FXCollections.observableList( storage );
        }
        
        @Override
        public final ObservableList<T> getValue()
        {
            return value;
        }

    }
    
}
