package com.varankin.brains.jfx;

import com.varankin.brains.artificial.factory.structured.Структурный;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.util.*;
import java.util.logging.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
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

        setCellFactory( СТРОИТЕЛЬ.фабрика() );
        setRoot( СТРОИТЕЛЬ.узел( new ВсеПроекты() ) );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( КОНТЕКСТ.параметр( "frame.browser.width.min", "150" ) );
        setMinWidth( w );

        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1, Integer.MAX_VALUE );

        ActionStart actionStart = new ActionStart();
        ActionPause actionPause = new ActionPause();
        ActionStop actionStop = new ActionStop();
        ActionProperties actionProperties = new ActionProperties();

        actionStart     .disableProperty().bind( blocker_1_N );
        actionPause     .disableProperty().bind( blocker_1_N );
        actionStop      .disableProperty().bind( blocker_1_N );
        actionProperties.disableProperty().bind( blocker_1_1 );
        
        actions = new ArrayList<>();
        actions.addAll( Arrays.asList( actionStart, actionPause, actionStop, null, actionProperties ) );
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

    private class ВсеПроекты implements Структурный, Элемент
    {

        @Override
        public Структурный вхождение()
        {
            return null;
        }

        @Override
        public Collection<Элемент> элементы()
        {
            return (Collection)КОНТЕКСТ.проекты();
        }
        
    }
    
    private class ActionStart extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionStart()
        {
            super( JFX, JFX.словарь( ActionStart.class ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionStop extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionStop()
        {
            super( JFX, JFX.словарь( ActionStop.class ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionPause extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionPause()
        {
            super( JFX, JFX.словарь( ActionPause.class ) );
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
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
