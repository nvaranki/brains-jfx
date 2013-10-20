package com.varankin.brains.jfx.browser;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ВыгрузитьПроект;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.УправлениеПроцессом;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.jfx.AbstractContextJfxAction;
import com.varankin.brains.jfx.AbstractJfxAction;
import com.varankin.brains.jfx.ApplicationActionWorker;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.MenuFactory;
import com.varankin.brains.jfx.ObservableListMirror;
import com.varankin.brains.jfx.ThresholdChecker;
import com.varankin.brains.Контекст;
import com.varankin.util.Текст;
import java.util.*;
import java.util.logging.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

/**
 * Экранная форма просмотра мыслительных структур.
 *
 * @author &copy; 2013 Николай Варанкин
 */
public class BrowserView extends TreeView<Элемент> 
{
    private static final Logger LOGGER = Logger.getLogger( BrowserView.class.getName() );
    
    private final JavaFX JFX;
    private final Контекст КОНТЕКСТ;
    private final ReadOnlyStringProperty title;
    private final List<AbstractJfxAction> actions;

    public BrowserView( JavaFX jfx )
    {
        JFX = jfx;
        Map<Locale.Category,Locale> специфика = jfx.контекст.специфика;
        КОНТЕКСТ = jfx.контекст;
        Текст словарь = Текст.ПАКЕТЫ.словарь( getClass(), КОНТЕКСТ.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );

        BrowserNodeBuilder строитель = new BrowserNodeBuilder( (TreeView<Элемент>)this, специфика );
        setCellFactory( строитель.фабрика() );
        BrowserNode узел = строитель.узел( КОНТЕКСТ.мыслитель );
        setRoot( узел );
        узел.expand( строитель );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( КОНТЕКСТ.параметр( Контекст.Параметры.BROWSER_WIDTH ) );
        setMinWidth( w );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

        ObservableList<TreeItem<Элемент>> selection = selection();
        ThresholdChecker процесс_1_N = new ThresholdChecker( new ObservableListMirror<>( selection, 
                new ExclusiveTreeItemCopier( Процесс.class ) ), false, 1, Integer.MAX_VALUE );
        ThresholdChecker проект_1_N  = new ThresholdChecker( new ObservableListMirror<>( selection, 
                new ExclusiveTreeItemCopier( Проект.class ) ), false,  1, Integer.MAX_VALUE );
        ThresholdChecker элемент_1_1 = new ThresholdChecker( new ObservableListMirror<>( selection, 
                new ExclusiveTreeItemCopier( Элемент.class ) ), false, 1, 1 );

        ActionStart actionStart = new ActionStart( процесс_1_N );
        ActionPause actionPause = new ActionPause( процесс_1_N );
        ActionStop actionStop = new ActionStop( процесс_1_N );
        ActionRemove actionRemove = new ActionRemove( проект_1_N );
        ActionProperties actionProperties = new ActionProperties( элемент_1_1 );

        setContextMenu( MenuFactory.createContextMenu( 
            new MenuFactory.MenuNode( null,
                new MenuFactory.MenuNode( actionStart ), 
                new MenuFactory.MenuNode( actionPause ), 
                new MenuFactory.MenuNode( actionStop ), 
                null,
                new MenuFactory.MenuNode( actionRemove ), 
                null,
                new MenuFactory.MenuNode( actionProperties ) ) ) );
        
        actions = new ArrayList<>();
        actions.addAll( Arrays.asList( 
                actionStart, actionPause, actionStop, null, 
                actionRemove, null, 
                actionProperties ) );
    }
    
    private ObservableList<TreeItem<Элемент>> selection()
    {
        return selectionModelProperty().getValue().getSelectedItems();
    }
    
    public final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    public final List<AbstractJfxAction> getActions()
    {
        return actions;
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private abstract class ActionProcessControl<T extends Процесс> extends AbstractContextJfxAction<JavaFX>
    {
        private final Действие<List<T>> ДЕЙСТВИЕ;
        
        ActionProcessControl( Текст словарь, ThresholdChecker монитор, Действие... действия )
        {
            super( JFX, словарь );
            ДЕЙСТВИЕ = new ДействияПоПорядку( JFX.контекст, Приоритет.КОНТЕКСТ, действия );
            ActionProcessControl.this.disableProperty().bind( монитор );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            new ApplicationActionWorker<>( ДЕЙСТВИЕ, ceлектор() ).execute( контекст );
        }
        
        private List<T> ceлектор()
        {
            List<T> список = new ArrayList<>();
            for( TreeItem<Элемент> ti : BrowserView.this.getSelectionModel().getSelectedItems() )
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
        ActionStart( ThresholdChecker монитор )
        {
            super( JFX.словарь( ActionStart.class ), монитор, 
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
        ActionStop( ThresholdChecker монитор )
        {
            super( JFX.словарь( ActionStop.class ), монитор, 
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
        ActionPause( ThresholdChecker монитор )
        {
            super( JFX.словарь( ActionPause.class ), монитор,
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
        ActionRemove( ThresholdChecker монитор )
        {
            super( JFX.словарь( ActionRemove.class ), монитор,
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
        
        ActionProperties( ThresholdChecker монитор )
        {
            super( JFX, JFX.словарь( ActionProperties.class ) );
            ActionProperties.this.disableProperty().bind( монитор );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
