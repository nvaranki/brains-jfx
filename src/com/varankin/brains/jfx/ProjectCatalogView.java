package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныйМодуль;
import com.varankin.brains.appl.УдалитьАрхивныйПроект;
import com.varankin.brains.db.Проект;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.util.logging.*;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends ListView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    private final ApplicationView.Context context;
    private final ReadOnlyStringProperty title;

    ProjectCatalogView( ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( context.jfx.getDataBaseProjectMonitor() );
        Текст словарь = Текст.ПАКЕТЫ.словарь( ProjectCatalogView.class, context.jfx.контекст.специфика );
        title = new ReadOnlyStringWrapper( словарь.текст( "Name" ) );
    }
    
    final ReadOnlyStringProperty titleProperty()
    {
        return title;
    }
    
    private MenuNode popup()
    {
        return new MenuNode( null,
                new MenuNode( new ActionPreview() ),
                new MenuNode( new ActionEdit() ),
                new MenuNode( new ActionRun() ),
                null, 
                new MenuNode( new ActionRemove() ),
                null, 
                new MenuNode( new ActionExport() ),
                null, 
                new MenuNode( new ActionProperties() )
                );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Проект>, ListCell<Проект>>
    {
        @Override
        public ListCell<Проект> call( ListView<Проект> view )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<Проект>
    {
        @Override
        public void updateItem( Проект item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.toString() );
        }
    }
    
    private class ActionPreview extends Action
    {
        
        ActionPreview()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionPreview.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionEdit extends Action
    {
        
        ActionEdit()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionEdit.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionRun extends Action
    {
        
        ActionRun()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionRun.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionExport extends Action
    {
        
        ActionExport()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionExport.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionRemove extends Action
    {
        final Действие<Collection<Проект>> действие;
        
        ActionRemove()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionRemove.class, context.jfx.контекст.специфика ) );
            действие = new УдалитьАрхивныйПроект( context.jfx.контекст.архив );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
//            LOGGER.info( "Sorry, the command is not implemented." );
            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            new ApplicationActionWorker<Collection<Проект>>( действие, ceлектор, context.jfx )
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    Результат результат = getValue();
                    if( результат != null && результат.код() == Результат.НОРМА )
                        jfx.getDataBaseProjectMonitor().getValue().removeAll( контекст() );
                }
            }.execute();
        }
    }
    
    private class ActionProperties extends Action
    {
        
        ActionProperties()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionProperties.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
