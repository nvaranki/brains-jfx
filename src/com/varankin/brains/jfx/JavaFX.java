package com.varankin.brains.jfx;

import com.varankin.brains.artificial.factory.ФабрикаЭлементовImpl;
import com.varankin.brains.artificial.factory.ФабрикаЭлементов;
import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.artificial.factory.ПассивныйРазветвитель;
import com.varankin.brains.artificial.factory.СтандартныйПроцессорРасчета;
import com.varankin.brains.artificial.factory.СтандартныйСенсор;
import com.varankin.brains.artificial.factory.СенсорноеПолеImpl;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.Контекст;
import com.varankin.util.MonitoredList;
import com.varankin.util.MonitoredSet;
import com.varankin.util.Текст;
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
        views = new ObservableObjectList<>( new ArrayList<TitledSceneGraph>() );
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
        new GuiBuilder( this ).createView( платформа );
        платформа.show();
        es.execute( new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                //TODO should not be called!!!
                контекст.архив.проекты().setPropertyValue( Коллекция.PROPERTY_UPDATED, Boolean.TRUE );
                контекст.архив.библиотеки().setPropertyValue( Коллекция.PROPERTY_UPDATED, Boolean.TRUE );
                return null;
            }
        } );
    }

    void стоп()
    {
        es.shutdown();
        платформа.hide();
    }
    
    Текст словарь( Class<?> c )
    {
        return Текст.ПАКЕТЫ.словарь( c, контекст.специфика );
    }

    File getCurrentLocalDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }

    //<editor-fold defaultstate="collapsed" desc="классы">
    
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
    
    //</editor-fold>

}
