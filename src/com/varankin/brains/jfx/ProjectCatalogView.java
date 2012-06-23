package com.varankin.brains.jfx;

import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныйМодуль;
import com.varankin.brains.db.Проект;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;

import java.util.*;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends ListView<Проект>
{
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
                new MenuNode( new ActionDbModulePreview() ),
                new MenuNode( new ActionDbModuleEdit() ),
                null, 
                new MenuNode( new ActionDbModuleRemove() ),
                null, 
                new MenuNode( new ActionDbModuleProperties() )
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
    
    private class ActionDbModulePreview extends Action
    {
        
        ActionDbModulePreview()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModulePreview.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO not impl.
        }
    }
    
    private class ActionDbModuleEdit extends Action
    {
        
        ActionDbModuleEdit()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModuleEdit.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO not impl.
        }
    }
    
    private class ActionDbModuleRemove extends Action
    {
        final УдалитьАрхивныйМодуль действие;
        
        ActionDbModuleRemove()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModuleRemove.class, context.jfx.контекст.специфика ) );
            действие = new УдалитьАрхивныйМодуль( context.jfx.контекст.склад );
            disableProperty().bind( new SelectionDetector( selectionModelProperty(), false, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
//            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
//            new ApplicationActionWorker<Collection<Проект>>( действие, ceлектор, context.jfx )
//            {
//                @Override
//                protected void succeeded()
//                {
//                    super.succeeded();
//                    Результат результат = getValue();
//                    if( результат != null && результат.код() == Результат.НОРМА )
//                        jfx.getDataBaseModuleMonitor().getValue().removeAll( контекст() );
//                }
//            }.execute();
        }
    }
    
    private class ActionDbModuleProperties extends Action
    {
        
        ActionDbModuleProperties()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModuleProperties.class, context.jfx.контекст.специфика ) );
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
