package com.varankin.brains.jfx;

import com.varankin.brains.appl.Мыслитель;
import com.varankin.brains.appl.Элемент;
import com.varankin.brains.Контекст;
import com.varankin.util.MonitoredSet;
import com.varankin.util.Текст;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;

/**
 * Экранная форма просмотра мыслительных структур.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class BrowserView extends TreeView<Элемент> 
{
    private static final Logger LOGGER = Logger.getLogger( BrowserView.class.getName() );
    
    private final JavaFX JFX;
    private final Контекст КОНТЕКСТ;
    private final BrowserNodeBuilder СТРОИТЕЛЬ;
    private final ReadOnlyStringProperty title;
    private final List<AbstractJfxAction> actions;

    BrowserView( JavaFX jfx )
    {
        JFX = jfx;
        Map<Locale.Category,Locale> специфика = jfx.контекст.специфика;
        КОНТЕКСТ = jfx.контекст;
        СТРОИТЕЛЬ = new BrowserNodeBuilder( (TreeView<Элемент>)this, специфика );
        Текст словарь = Текст.ПАКЕТЫ.словарь( getClass(), КОНТЕКСТ.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
        КОНТЕКСТ.мыслители().addPropertyChangeListener( 
                    new PropertyChangeListenerImpl() );

        setCellFactory( СТРОИТЕЛЬ.фабрика() );
        setRoot( СТРОИТЕЛЬ.узел( Элемент.ВСЕ_МЫСЛИТЕЛИ ) );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( КОНТЕКСТ.параметр( "frame.browser.width.min", "150" ) );
        setMinWidth( w );

        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        actions = new ArrayList<>();
        actions.addAll( Arrays.asList( 
                new ActionStart( blocker_1_1 ), 
                new ActionPause( blocker_1_1 ), 
                new ActionStop( blocker_1_1 ), 
                null,
                new ActionProperties() 
                ) );
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    final List<AbstractJfxAction> getActions()
    {
        return actions;
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class ActionStart extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionStart( ObservableValue<Boolean> blocker )
        {
            super( JFX, JFX.словарь( ActionStart.class ) );
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionStop extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionStop( ObservableValue<Boolean> blocker )
        {
            super( JFX, JFX.словарь( ActionStop.class ) );
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionPause extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionPause( ObservableValue<Boolean> blocker )
        {
            super( JFX, JFX.словарь( ActionPause.class ) );
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionProperties extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionProperties()
        {
            super( JFX, JFX.словарь( ActionProperties.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class PropertyChangeListenerImpl implements PropertyChangeListener
    {
        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
            Мыслитель мыслитель;
            
            switch( evt.getPropertyName() )
            {
                case Мыслитель.PROPERTY_UPDATED:
                    Platform.runLater( new OnPropertyUpdated() );
                    break;

                case Мыслитель.PROPERTY_ADDED:
                    мыслитель = (Мыслитель)evt.getNewValue();
                    Platform.runLater( new OnPropertyAdded( мыслитель ) );
                    мыслитель.поля().addPropertyChangeListener( 
                            new PropertyChangeListenerImpl2( мыслитель, Элемент.ВСЕ_ПОЛЯ ) );
                    мыслитель.функции().addPropertyChangeListener( 
                            new PropertyChangeListenerImpl2( мыслитель, Элемент.ВСЕ_ФУНКЦИИ ) );
                    мыслитель.процессоры().addPropertyChangeListener( 
                            new PropertyChangeListenerImpl2( мыслитель, Элемент.ВСЕ_ПРОЦЕССОРЫ ) );
                    break;

                case Мыслитель.PROPERTY_REMOVED:
                    мыслитель = (Мыслитель)evt.getOldValue();
                    Platform.runLater( new OnPropertyRemoved( мыслитель ) );
                    break;

                default:
                    LOGGER.log( Level.SEVERE, "Unsupported change to мыслители received: {0}", evt.getPropertyName() );
            }
        }
        
        private class OnPropertyUpdated implements Runnable
        {
            @Override
            public void run()
            {
                ObservableList<TreeItem<Элемент>> children
                        = BrowserView.this.getRoot().getChildren();
                children.clear();
                for( Мыслитель мыслитель : КОНТЕКСТ.мыслители() )
                    children.add( СТРОИТЕЛЬ.узел( мыслитель ) );
            }
        };

        private class OnPropertyAdded implements Runnable
        {
            private final Мыслитель УЗЕЛ;

            OnPropertyAdded( Мыслитель узел )
            {
                УЗЕЛ = узел;
            }

            @Override
            public void run()
            {
                ObservableList<TreeItem<Элемент>> children
                        = BrowserView.this.getRoot().getChildren();
                children.add( СТРОИТЕЛЬ.узел( УЗЕЛ ) );
            }
        };

        private class OnPropertyRemoved implements Runnable
        {
            private final Мыслитель УЗЕЛ;

            OnPropertyRemoved( Мыслитель узел )
            {
                УЗЕЛ = узел;
            }

            @Override
            public void run()
            {
                ObservableList<TreeItem<Элемент>> children
                        = BrowserView.this.getRoot().getChildren();
                children.remove( СТРОИТЕЛЬ.узел( УЗЕЛ ) );
            }
        };

    }
        
    private class PropertyChangeListenerImpl2 implements PropertyChangeListener
    {
        private final Мыслитель МЫСЛИТЕЛЬ;
        private final Элемент ЭЛЕМЕНТ;
        private TreeItem<Элемент> НАЧАЛО;
        private TreeItem<Элемент> СПИСОК;

        private PropertyChangeListenerImpl2( Мыслитель мыслитель, Элемент элемент )
        {
            МЫСЛИТЕЛЬ = мыслитель;
            ЭЛЕМЕНТ = элемент;
        }
        
        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
            Элемент элемент;
            
            switch( evt.getPropertyName() )
            {
                case MonitoredSet.PROPERTY_ADDED:
                    элемент = (Элемент)evt.getNewValue();
                    Platform.runLater( new OnPropertyAdded( элемент ) );
                    break;
                
                case MonitoredSet.PROPERTY_REMOVED:
                    элемент = (Элемент)evt.getOldValue();
                    Platform.runLater( new OnPropertyRemoved( элемент ) );
                    break;
                
                default:
                    LOGGER.log( Level.SEVERE, "Unsupported change to мыслители received: {0}", evt.getPropertyName() );
            }            
        }

        private TreeItem<Элемент> начало()
        {
            for( TreeItem<Элемент> item : BrowserView.this.getRoot().getChildren() )
                if( item.getValue().equals( МЫСЛИТЕЛЬ ) )
                    return item;
            return null;
        }
        
        private class OnPropertyAdded implements Runnable
        {
            private final Элемент ЭЛЕМЕНТ;

            OnPropertyAdded( Элемент элемент )
            {
                ЭЛЕМЕНТ = элемент;
            }

            @Override
            public void run()
            {
                if( НАЧАЛО == null ) НАЧАЛО = начало();
                if( СПИСОК == null ) НАЧАЛО.getChildren().add( 
                        СПИСОК = СТРОИТЕЛЬ.узел( PropertyChangeListenerImpl2.this.ЭЛЕМЕНТ ) );
                СПИСОК.getChildren().add( СТРОИТЕЛЬ.узел( ЭЛЕМЕНТ ) );
            }
        };

        private class OnPropertyRemoved implements Runnable
        {
            private final Элемент ЭЛЕМЕНТ;

            OnPropertyRemoved( Элемент элемент )
            {
                ЭЛЕМЕНТ = элемент;
            }

            @Override
            public void run()
            {
                if( НАЧАЛО != null && СПИСОК != null ) 
                {
                    TreeItem<Элемент> found = null;
                    for( TreeItem<Элемент> item : СПИСОК.getChildren() )
                        if( item.getValue().equals( ЭЛЕМЕНТ ) )
                        {
                            found = item;
                            break;
                        }
                    if( found != null )
                    {
                        ObservableList<TreeItem<Элемент>> children = СПИСОК.getChildren();
                        children.remove( found );
                        if( children.isEmpty() )
                        {
                            НАЧАЛО.getChildren().remove( СПИСОК );
                            СПИСОК = null;
                        }
                    }
                }
            }
        };

    }
    
    //</editor-fold>
    
}
