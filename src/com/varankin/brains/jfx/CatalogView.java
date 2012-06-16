package com.varankin.brains.jfx;

import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Модуль;
import com.varankin.brains.jfx.MenuFactory.MenuNode;

import java.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 *
 *
 * @author &copy; 2011 Николай
 */
class CatalogView extends ListView<Модуль>
{
    private final Архив склад;

    CatalogView( ApplicationView.Context context )
    {
        getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
        setContextMenu( context.menuFactory.createContextMenu( popup( context ) ) );
        Popup contextMenu = new Popup();
        getSelectionModel().selectedItemProperty().addListener( contextMenu.getChangeListener() );//getSelectionListener() );
        //this.getSelectionModel().
        //setContextMenu( contextMenu );
        setCellFactory( new Callback<ListView<Модуль>, ListCell<Модуль>>() 
        {
            @Override
            public ListCell<Модуль> call( ListView<Модуль> view )
            {
                return new VisibleRow();
            }
        } );
        
        склад = context.jfx.контекст.склад;
        populate( ); //TODO sep. thread
    }
    
    private void populate()
    {
        List<Модуль> модули = new ArrayList<>( склад.модули() );
        //TODO Collections.sort( модули );
        getItems().addAll( модули );
        
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
    
    static MenuNode popup( ApplicationView.Context context )
    {
        ActionFactory actions = context.actions;
        return new MenuNode( null,
                new MenuNode( actions.getAbout() ),
                new MenuNode( actions.getAbout() ),
                null, 
                new MenuNode( actions.getDbRemoveModule() )
                );
    }
    
    private class Popup extends ContextMenu
    {
        private final ChangeListener<MultipleSelectionModel<Модуль>> selectionListener;
        private final ChangeListener<Модуль> changeListener;
        private final MenuItem menuItemEdit;
        private final MenuItem menuItemProperties;
        private final MenuItem menuItemView;
        private final MenuItem menuItemDelete;
        
        Popup()
        {
            menuItemView = new MenuItem( "View" );
            menuItemEdit = new MenuItem( "Edit" );
            menuItemDelete = new MenuItem( "Delete" );
            menuItemProperties = new MenuItem( "Properties" );
            selectionListener = new SelectionListener();
            changeListener = new ItemChangeListener();
            getItems().addAll( 
                    menuItemView, menuItemEdit, new SeparatorMenuItem(), 
                    menuItemDelete, new SeparatorMenuItem(), 
                    menuItemProperties );
        }
        
        ChangeListener<MultipleSelectionModel<Модуль>> getSelectionListener()
        {
            return selectionListener;
        }
        
        ChangeListener<Модуль> getChangeListener() 
        {
            return changeListener;
        }
        
        class SelectionListener implements ChangeListener<MultipleSelectionModel<Модуль>>
        {

            @Override
            public void changed( 
                    ObservableValue<? extends MultipleSelectionModel<Модуль>> observable, 
                    MultipleSelectionModel<Модуль> oldValue, 
                    MultipleSelectionModel<Модуль> newValue )
            {
                ObservableList<Модуль> selectedItems = observable.getValue().getSelectedItems();
                System.out.println( selectedItems );
                int numSelected = CatalogView.this.selectionModelProperty().get().getSelectedIndices().size();
                Popup.this.menuItemView.setDisable( numSelected != 1 );
                Popup.this.menuItemEdit.setDisable( numSelected != 1 );
                Popup.this.menuItemDelete.setDisable( numSelected > 0 );
                Popup.this.menuItemProperties.setDisable( numSelected != 1 );
            }
            
        }
        
        class ItemChangeListener implements ChangeListener<Модуль>
        {

            @Override
            public void changed( 
                    ObservableValue<? extends Модуль> observable, 
                    Модуль oldValue, 
                    Модуль newValue )
            {
                //ObservableList<String> selectedItems = observable.getValue().getSelectedItems();
                int numSelected = CatalogView.this.selectionModelProperty().getValue().getSelectedIndices().size();
                System.out.println( numSelected );
                System.out.println( CatalogView.this.getSelectionModel().getSelectedItems() );
                Popup.this.menuItemView.setDisable( numSelected != 1 );
                Popup.this.menuItemEdit.setDisable( numSelected != 1 );
                Popup.this.menuItemProperties.setDisable( numSelected != 1 );
            }
            
        }
    }

}
