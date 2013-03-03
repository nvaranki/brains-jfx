package com.varankin.brains.jfx;

import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныйМодуль;
import com.varankin.brains.db.Модуль;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;
import java.util.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * Каталог модулей архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ModuleCatalogView extends ListView<Модуль>
{
    private final ApplicationView.Context context;
    private final ReadOnlyStringProperty title;

    ModuleCatalogView( ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( context.jfx.getDataBaseModuleMonitor() );
        Текст словарь = Текст.ПАКЕТЫ.словарь( ModuleCatalogView.class, context.jfx.контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    private MenuNode popup()
    {
        return new MenuNode( null,
                new MenuNode( new ActionDbModulePreview() ),
                new MenuNode( new ActionDbModuleEdit() ),
                null, 
                new MenuNode( new ActionDbModuleRemove() ),
                null, 
                new MenuNode( new ActionDbModuleProperties() )
                );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Модуль>, ListCell<Модуль>>
    {
        @Override
        public ListCell<Модуль> call( ListView<Модуль> view )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<Модуль>
    {
        @Override
        public void updateItem( Модуль item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.toString() );
        }
    }
    
    private class ActionDbModulePreview extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionDbModulePreview()
        {
            super( context, context.jfx.словарь( ActionDbModulePreview.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            ObservableList<TitledSceneGraph> views = контекст.jfx.getViews().getValue();
            
            for( Модуль модуль : selectionModelProperty().getValue().getSelectedItems() )
                if( модуль != null )
                {
                    TitledSceneGraph tsg;
                    if( ( tsg = isShown( модуль, views ) ) == null )
                        show( модуль, views );
                    else
                        //TODO временный обходной вариант для активации view
                        views.set( views.indexOf( tsg ), new TitledSceneGraph( tsg.node, tsg.title ) );
                }
        }
        
        private TitledSceneGraph isShown( Модуль модуль, Iterable<TitledSceneGraph> views )
        {
            for( TitledSceneGraph tsg : views )
            {
                Object content = tsg.node.getUserData();
                if( tsg.node instanceof WebView && модуль.equals( content ) )
                    return tsg;
            }
            return null;
        }

        private void show( final Модуль модуль, Collection<TitledSceneGraph> views )
        {
            final WebView view = new WebView();
            view.setUserData( модуль );
            views.add( new TitledSceneGraph( view, new SimpleStringProperty( модуль.toString() ) ) );
            
            контекст.jfx.getExecutorService().submit( new Task<String>() 
            {
                @Override
                protected String call() throws Exception
                {
                    return модуль.getImage( Модуль.MIME_SVG );
                }

                @Override
                protected void succeeded()
                {
                    view.getEngine().loadContent( getValue(), Модуль.MIME_SVG );
                }
            } );
        }

    }
    
    private class ActionDbModuleEdit extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionDbModuleEdit()
        {
            super( context, context.jfx.словарь( ActionDbModuleEdit.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO not impl.
        }

    }
    
    private class ActionDbModuleRemove extends AbstractJfxAction<ApplicationView.Context>
    {
        final УдалитьАрхивныйМодуль действие;
        
        ActionDbModuleRemove()
        {
            super( context, context.jfx.словарь( ActionDbModuleRemove.class ) );
            действие = new УдалитьАрхивныйМодуль( context.jfx.контекст.архив );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            List<Модуль> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            new ApplicationActionWorker<Collection<Модуль>>( действие, ceлектор )
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    Результат результат = getValue();
                    if( результат != null && результат.код() == Результат.НОРМА )
                        контекст.jfx.getDataBaseModuleMonitor().getValue().removeAll( контекст() );
                }
            }.execute( контекст.jfx );
        }
    }
    
    private class ActionDbModuleProperties extends AbstractJfxAction<ApplicationView.Context>
    {
        
        ActionDbModuleProperties()
        {
            super( context, context.jfx.словарь( ActionDbModuleProperties.class ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
