package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ВсеПроекты;
import com.varankin.brains.appl.ВыгрузитьПроект;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.УправлениеПроцессом;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
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
        СТРОИТЕЛЬ = new BrowserNodeBuilder( (TreeView<Элемент>)this, специфика, 
                new ВсеПроекты( КОНТЕКСТ ) );
        Текст словарь = Текст.ПАКЕТЫ.словарь( getClass(), КОНТЕКСТ.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );

        setCellFactory( СТРОИТЕЛЬ.фабрика() );
        setRoot( СТРОИТЕЛЬ.узел() );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( КОНТЕКСТ.параметр( "frame.browser.width.min", "150" ) );
        setMinWidth( w );

        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1, Integer.MAX_VALUE );

        ActionStart actionStart = new ActionStart();
        ActionPause actionPause = new ActionPause();
        ActionStop actionStop = new ActionStop();
        ActionRemove actionRemove = new ActionRemove();//( действиеУдалить );
        ActionProperties actionProperties = new ActionProperties();

        actionStart     .disableProperty().bind( blocker_1_N );
        actionPause     .disableProperty().bind( blocker_1_N );
        actionStop      .disableProperty().bind( blocker_1_N );
        actionRemove    .disableProperty().bind( blocker_1_N );
        actionProperties.disableProperty().bind( blocker_1_1 );
        
        actions = new ArrayList<>();
        actions.addAll( Arrays.asList( 
                actionStart, actionPause, actionStop, null, 
                actionRemove, null, 
                actionProperties ) );
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

    private abstract class ActionProcessControl<T extends Процесс> extends AbstractContextJfxAction<JavaFX>
    {
        private final Действие<List<T>> ДЕЙСТВИЕ;
        
        ActionProcessControl( Текст словарь, Действие... действия )
        {
            super( JFX, словарь );
            ДЕЙСТВИЕ = new ДействияПоПорядку( JFX.контекст, Приоритет.КОНТЕКСТ, действия );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            new ApplicationActionWorker<>( ДЕЙСТВИЕ, ceлектор() ).execute( контекст );
        }
        
        private List<T> ceлектор()
        {
            List<T> список = new ArrayList<>();
            for( TreeItem<Элемент> ti : getSelectionModel().getSelectedItems() )
            {
                T элемент = convert( ti.getValue() );
                if( элемент != null )
                    список.add( элемент );
            }
            return список;
        }
        
        abstract T convert( Элемент элемент ); //TODO Фильтр
        
    }
    
    private class ActionStart extends ActionProcessControl<Процесс>
    {
        ActionStart()
        {
            super( JFX.словарь( ActionStart.class ), 
                new УправлениеПроцессом( JFX.контекст, УправлениеПроцессом.Команда.СТАРТ ) );
        }
        
        @Override
        Процесс convert( Элемент элемент )
        {
            return элемент instanceof Процесс ? (Процесс)элемент : null;
        }
        
    }
    
    private class ActionStop extends ActionProcessControl<Процесс>
    {
        ActionStop()
        {
            super( JFX.словарь( ActionStop.class ), 
                new УправлениеПроцессом( JFX.контекст, УправлениеПроцессом.Команда.СТОП ) );
        }
        
        @Override
        Процесс convert( Элемент элемент )
        {
            return элемент instanceof Процесс ? (Процесс)элемент : null;
        }
        
    }
    
    private class ActionPause extends ActionProcessControl<Процесс>
    {
        ActionPause()
        {
            super( JFX.словарь( ActionPause.class ), 
                new УправлениеПроцессом( JFX.контекст, УправлениеПроцессом.Команда.ПАУЗА ) );
        }
        
        @Override
        Процесс convert( Элемент элемент )
        {
            return элемент instanceof Процесс ? (Процесс)элемент : null;
        }
        
    }
    
    private class ActionRemove extends ActionProcessControl<Проект>
    {
        ActionRemove()
        {
            super( JFX.словарь( ActionRemove.class ),
                new УправлениеПроцессом( JFX.контекст, УправлениеПроцессом.Команда.СТОП ),
                new ВыгрузитьПроект( JFX.контекст ) );
        }
        
        @Override
        Проект convert( Элемент элемент )
        {
            return элемент instanceof Проект ? (Проект)элемент : null;
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
