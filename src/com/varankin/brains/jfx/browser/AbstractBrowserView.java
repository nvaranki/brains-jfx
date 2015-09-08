package com.varankin.brains.jfx.browser;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ВыгрузитьПроект;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.УправлениеПроцессом;
import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.jfx.*;
import com.varankin.brains.Контекст;
import com.varankin.filter.Фильтр;
import com.varankin.util.Текст;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Основа экранной формы просмотра мыслительных структур.
 *
 * @author &copy; 2015 Николай Варанкин
 */
abstract class AbstractBrowserView<E> extends TreeView<E>
{
    private static final Logger LOGGER = Logger.getLogger( AbstractBrowserView.class.getName() );
    
    private final ReadOnlyStringProperty title;
    private final List<AbstractJfxAction> actions;

    AbstractBrowserView( E root, Фильтр<E> фПроект, Фильтр<E> фПроцесс, Фильтр<E> фЭлемент )
    {
        Контекст контекст = JavaFX.getInstance().контекст;
        
        Текст словарь = Текст.ПАКЕТЫ.словарь( getClass(), контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );

        BrowserNodeBuilder<E> строитель = new BrowserNodeBuilder( (TreeView<E>)this );
        setCellFactory( строитель.фабрика() );
        BrowserNode<E> узел = строитель.узел( root );
        setRoot( узел );
        узел.expand( строитель );
        setShowRoot( true );
        setEditable( false );
        int w = Integer.valueOf( контекст.параметр( Контекст.Параметры.BROWSER_WIDTH ) );
        setMinWidth( w );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

        ObservableList<TreeItem<E>> selection = selection();
        ThresholdChecker процесс_1_N = new ThresholdChecker( new ObservableListMirror( selection, 
                new ExclusiveTreeItemCopier<>( фПроцесс ) ), false, 1, Integer.MAX_VALUE );
        ThresholdChecker проект_1_N  = new ThresholdChecker( new ObservableListMirror( selection, 
                new ExclusiveTreeItemCopier<>( фПроект ) ), false,  1, Integer.MAX_VALUE );
        ThresholdChecker элемент_1_1 = new ThresholdChecker( new ObservableListMirror( selection, 
                new ExclusiveTreeItemCopier<>( фЭлемент ) ), false, 1, 1 );

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

    public final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    public final List<AbstractJfxAction> getActions()
    {
        return actions;
    }
    
    protected abstract <T> T convert( E элемент, Class<T> cls );
    
    private ObservableList<TreeItem<E>> selection()
    {
        return selectionModelProperty().getValue().getSelectedItems();
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private abstract class ActionProcessControl<T extends Процесс> extends AbstractContextJfxAction<JavaFX>
    {
        private final Действие<List<T>> ДЕЙСТВИЕ;
        
        ActionProcessControl( Текст словарь, ThresholdChecker монитор, Действие... действия )
        {
            super( JavaFX.getInstance(), словарь );
            ДЕЙСТВИЕ = new ДействияПоПорядку( ДействияПоПорядку.Приоритет.КОНТЕКСТ, действия );
            ActionProcessControl.this.disableProperty().bind( монитор );
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            new ApplicationActionWorker<>( ДЕЙСТВИЕ, ceлектор() ).execute( контекст );
        }
        
        private List<T> ceлектор()
        {
            List<T> список = new ArrayList<>();
            for( TreeItem<E> ti : getSelectionModel().getSelectedItems() )
            {
                T элемент = convert( ti.getValue() );
                if( элемент != null )
                    список.add( элемент );
            }
            return список;
        }
        
        abstract T convert( E элемент ); //TODO Фильтр
        
    }
    
    private class ActionStart extends ActionProcessControl<Процесс>
    {
        ActionStart( ThresholdChecker монитор )
        {
            super( JavaFX.getInstance().словарь( ActionStart.class ), монитор, 
                new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.СТАРТ ) );
        }
        
        @Override
        Процесс convert( E элемент )
        {
            return AbstractBrowserView.this.convert( элемент, Процесс.class );
        }
        
    }
    
    private class ActionStop extends ActionProcessControl<Процесс>
    {
        ActionStop( ThresholdChecker монитор )
        {
            super( JavaFX.getInstance().словарь( ActionStop.class ), монитор, 
                new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.СТОП ) );
        }
        
        @Override
        Процесс convert( E элемент )
        {
            return AbstractBrowserView.this.convert( элемент, Процесс.class );
        }
        
    }
    
    private class ActionPause extends ActionProcessControl<Процесс>
    {
        ActionPause( ThresholdChecker монитор )
        {
            super( JavaFX.getInstance().словарь( ActionPause.class ), монитор,
                new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.ПАУЗА ) );
        }
        
        @Override
        Процесс convert( E элемент )
        {
            return AbstractBrowserView.this.convert( элемент, Процесс.class );
        }
        
    }
    
    private class ActionRemove extends ActionProcessControl<Проект>
    {
        ActionRemove( ThresholdChecker монитор )
        {
            super( JavaFX.getInstance().словарь( ActionRemove.class ), монитор,
                new УправлениеПроцессом( JavaFX.getInstance().контекст, УправлениеПроцессом.Команда.СТОП ),
                new ВыгрузитьПроект( JavaFX.getInstance().контекст ) );
        }
        
        @Override
        Проект convert( E элемент )
        {
            return AbstractBrowserView.this.convert( элемент, Проект.class );
        }
        
    }
    
    private class ActionProperties extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionProperties( ThresholdChecker монитор )
        {
            super( JavaFX.getInstance(), JavaFX.getInstance().словарь( ActionProperties.class ) );
            ActionProperties.this.disableProperty().bind( монитор );
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
