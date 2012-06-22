package com.varankin.brains.jfx;

import com.varankin.biz.action.Результат;
import com.varankin.brains.appl.УдалитьАрхивныйМодуль;
import com.varankin.brains.db.Модуль;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.util.Текст;

import java.util.*;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Каталог модулей архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class CatalogView extends ListView<Модуль>
{
    private final ApplicationView.Context context;

    CatalogView( ApplicationView.Context context )
    {
        this.context = context;
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( context.jfx.getDataBaseModuleMonitor() );
        
        //Текст словарь = Текст.ПАКЕТЫ.словарь( CatalogView.class, context.jfx.контекст.специфика );
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
    
    private class DisableDetector extends BooleanBinding
    {
        final int low, high;

        DisableDetector( int low, int high )
        {
            super.bind( getSelectionModel().getSelectedItems() );
            this.low = low;
            this.high = high;
        }

        DisableDetector( int min )
        {
            this( min, Integer.MAX_VALUE );
        }
        
        @Override
        protected boolean computeValue()
        {
            int size = getSelectionModel().getSelectedItems().size();
            return !( low <= size && size <= high );
        }
    }
    
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
    
    private class ActionDbModulePreview extends Action
    {
        
        ActionDbModulePreview()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModulePreview.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new DisableDetector( 1, 1 ) );
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
            disableProperty().bind( new DisableDetector( 1, 1 ) );
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
            disableProperty().bind( new DisableDetector( 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            List<Модуль> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            new ApplicationActionWorker<Collection<Модуль>>( действие, ceлектор, context.jfx )
            {
                @Override
                protected void succeeded()
                {
                    super.succeeded();
                    Результат результат = getValue();
                    if( результат != null && результат.код() == Результат.НОРМА )
                        jfx.getDataBaseModuleMonitor().getValue().removeAll( контекст() );
                }
            }.execute();
        }
    }
    
    private class ActionDbModuleProperties extends Action
    {
        
        ActionDbModuleProperties()
        {
            super( context.jfx, Текст.ПАКЕТЫ.словарь( ActionDbModuleProperties.class, context.jfx.контекст.специфика ) );
            disableProperty().bind( new DisableDetector( 1, 1 ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            //TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
