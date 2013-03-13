package com.varankin.brains.jfx;

import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Модуль_;
import com.varankin.brains.db.Проект;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;

import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private final ObservableObjectList<Библиотека> dblm;
    private final ObservableObjectList<TitledSceneGraph> views;
    private final ExecutorService es;
    private final ObservableValue<ObservableList<Проект>> ПРОЕКТЫ;
    private final ObservableValue<ObservableList<Библиотека>> БИБЛИОТЕКИ;

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
        dblm = new ObservableObjectList<>( new ArrayList<Библиотека>() );
        views = new ObservableObjectList<>( new ArrayList<TitledSceneGraph>() );
        ПРОЕКТЫ = new ValueListWrapper<>( контекст.архив.проекты() );
        БИБЛИОТЕКИ = new ValueListWrapper<>( контекст.архив.библиотеки() );
    }
    
    ObservableValue<ObservableList<Проект>> проекты() 
    {
        return ПРОЕКТЫ;
    }
    
    ObservableValue<ObservableList<Библиотека>> библиотеки() 
    {
        return БИБЛИОТЕКИ;
    }
    
    @Deprecated
    ObservableValue<ObservableList<Модуль_>> getDataBaseModuleMonitor()
    {
        return dbmm;
    }
    
    @Deprecated
    ObservableValue<ObservableList<Проект>> getDataBaseProjectMonitor()
    {
        return dbpm;
    }

    @Deprecated
    ObservableValue<ObservableList<Библиотека>> getDataBaseLibraryMonitor()
    {
        return dblm;
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
        ПРОЕКТЫ.getValue().addAll( контекст.архив.проекты() );
        БИБЛИОТЕКИ.getValue().addAll( контекст.архив.библиотеки() );
//        Platform.runLater( new Task<Void>() //TODO re-design
//        {
//            @Override
//            protected Void call() throws Exception
//            {
//                dbmm.getValue().addAll( контекст.архив.модули() );
//                Object xxx = контекст.архив.проекты();
//                for( Проект проект : контекст.архив.проекты() )
//                    dbpm.getValue().add( проект );
//                for( Библиотека библиотека : контекст.архив.библиотеки() )
//                    dblm.getValue().add( библиотека );
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
    
    private static class ValueListWrapper<T> extends ObservableValueBase<ObservableList<T>>
    {
        final ObservableList<T> value = FXCollections.<T>observableArrayList();
        
        ValueListWrapper( Коллекция<T> элементы )
        {
            элементы.addPropertyChangeListener( new PropertyChangeListenerImpl( элементы ) );
        }
        
        @Override
        public ObservableList<T> getValue()
        {
            return value;
        }
        
        private class PropertyChangeAction implements Runnable
        {
            //final List<T> value;
            final List<T> update;
            final Object action;
            
            public PropertyChangeAction( Collection<? extends T> update, Object action )
            {
                this.update = new ArrayList<>( update ); // snapshot
                this.action = action;
            }
            
            @Override
            public void run()
            {
                //TODO sort?!
                if( Коллекция.PROPERTY_ADDED.equals( action ) )
                    value.addAll( update );
                else if( Коллекция.PROPERTY_REMOVED.equals( action ) )
                    value.removeAll( update );
                else if( Коллекция.PROPERTY_UPDATED.equals( action ) )
                {
                    value.retainAll( update );
                    for( T t : update )
                        if( !value.contains( t ) )
                            value.add( t );
                }
                ValueListWrapper.this.fireValueChangedEvent();
            }
        }
        
        private class PropertyChangeListenerImpl implements PropertyChangeListener
        {
            final Collection<T> элементы;
            
            public PropertyChangeListenerImpl( Collection<T> элементы )
            {
                this.элементы = элементы;
            }
            
            @Override
            public void propertyChange( PropertyChangeEvent evt )
            {
                // database part got changed
                switch( evt.getPropertyName() )
                {
                    case Коллекция.PROPERTY_ADDED:
                        Platform.runLater( new PropertyChangeAction(
                                Arrays.<T>asList( (T)evt.getNewValue() ), Коллекция.PROPERTY_ADDED ) );
                        break;
                    case Коллекция.PROPERTY_REMOVED:
                        Platform.runLater( new PropertyChangeAction(
                                Arrays.<T>asList( (T)evt.getOldValue() ), Коллекция.PROPERTY_REMOVED ) );
                        break;
                    case Коллекция.PROPERTY_UPDATED:
                        Platform.runLater( new PropertyChangeAction(
                                элементы, Коллекция.PROPERTY_UPDATED ) );
                        break;
                }
            }
        }
        
    }
    
    //</editor-fold>
    
}
