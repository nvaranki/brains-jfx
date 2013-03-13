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
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyProperty;
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
    private final ReadOnlyProperty<ObservableList<Проект>> ПРОЕКТЫ;

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
        ПРОЕКТЫ = new ProjectListWrapper();
    }
    
    ReadOnlyProperty<ObservableList<Проект>> проекты() 
    {
        return ПРОЕКТЫ;
    }
    
    ObservableValue<ObservableList<Модуль_>> getDataBaseModuleMonitor()
    {
        return dbmm;
    }
    
    ObservableValue<ObservableList<Проект>> getDataBaseProjectMonitor()
    {
        return dbpm;
    }

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
    
    private class ProjectListWrapper extends ReadOnlyObjectPropertyBase<ObservableList<Проект>>
    {
        final int hashCode;
        final ObservableList<Проект> value = FXCollections.<Проект>observableArrayList();
        
        ProjectListWrapper() 
        {
            //super( FXCollections.<Проект>observableArrayList() );
            Коллекция<Проект> проекты = контекст.архив.проекты();
            hashCode = проекты.hashCode() ^ 69 * 7;
            проекты.addPropertyChangeListener( new PropertyChangeListenerImpl() );
            //getValue().addAll( проекты );
        }
        
        @Override
        public final int hashCode()
        {
            return hashCode;
        }
        
//        @Override
//        public final ObservableList<Проект> getValue()
//        {
//            return super.getValue();
//        }

        @Override
        public Object getBean() 
        {
            return ProjectListWrapper.this; //TODO
        }

        @Override
        public String getName() 
        {
            return "projects";//Проект.class.getSimpleName(); //TODO
        }

        @Override
        public ObservableList<Проект> get() 
        {
            return value;
        }
        
//        @Override
//        protected void invalidated()
//        {
//            // GUI part got changed
////            getExecutorService().submit( 
////                    new DataBaseUpdater( ProjectCatalogView.this.itemsProperty().getValue() ) );
//            return;
//        }
        
        private class DataBaseUpdater implements Runnable
        {
            final List<Проект> проектыНаВиду;

            DataBaseUpdater( List<? extends Проект> проекты ) 
            {
                this.проектыНаВиду = new ArrayList<>( проекты );
            }

            @Override
            public void run() 
            {
                Коллекция<Проект> проекты = контекст.архив.проекты();
                List<Проект> проектыАрхива = new ArrayList<>( проекты );
                Collection<Проект> добавлено = new ArrayList<>( проектыНаВиду ); добавлено.removeAll( проектыАрхива );
                Collection<Проект> удалено   = new ArrayList<>( проектыАрхива ); удалено.removeAll( проектыНаВиду );
                проекты.addAll( добавлено );
                проекты.removeAll( удалено );
                
                //TODO if Set<E>
                //проекты.retainAll( проектыНаВиду );
                //проекты.addAll( проектыНаВиду );
            }
            
        }
        
        private class GuiUpdater implements Runnable
        {
            final List<Проект> update;
            final Object action;

            public GuiUpdater( Collection<? extends Проект> update, Object action ) 
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
                    for( Проект проект : update )
                        if( !value.contains( проект ) )
                            value.add( проект );
                }
                ProjectListWrapper.this.fireValueChangedEvent();
            }
        }

        private class PropertyChangeListenerImpl implements PropertyChangeListener
        {
            @Override
            public void propertyChange( PropertyChangeEvent evt ) 
            {
                // database part got changed
                switch( evt.getPropertyName() )
                {
                    case Коллекция.PROPERTY_ADDED:
                        Platform.runLater( new ProjectListWrapper.GuiUpdater( 
                                Arrays.<Проект>asList( (Проект)evt.getNewValue() ), Коллекция.PROPERTY_ADDED ) );
                        break;
                    case Коллекция.PROPERTY_REMOVED:
                        Platform.runLater( new ProjectListWrapper.GuiUpdater( 
                                Arrays.<Проект>asList( (Проект)evt.getOldValue() ), Коллекция.PROPERTY_REMOVED ) );
                        break;
                    case Коллекция.PROPERTY_UPDATED:
                        Platform.runLater( new ProjectListWrapper.GuiUpdater( 
                                контекст.архив.проекты(), Коллекция.PROPERTY_UPDATED ) );
                        break;
                }
            }
        }
        
    }
    
}
