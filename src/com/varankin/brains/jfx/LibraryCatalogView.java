package com.varankin.brains.jfx;

import com.varankin.brains.appl.УдалитьАрхивныеБиблиотеки;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgService;
import com.varankin.brains.artificial.io.svg.SvgБиблиотека;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.Arrays;
import java.util.logging.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Каталог библиотек архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class LibraryCatalogView extends AbstractCatalogView<Библиотека>
{
    private final static Logger LOGGER = Logger.getLogger( LibraryCatalogView.class.getName() );
    
    LibraryCatalogView( JavaFX jfx )
    {
        super( jfx, jfx.контекст.архив.библиотеки() );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setCellFactory( new RowBuilder() );
        
        SvgService<Библиотека> svg = new SvgService<Библиотека>() 
        {
            @Override
            public Provider<String> генератор( Библиотека библиотека )
            {
                return new SvgБиблиотека( библиотека, new Сборка( библиотека ) );
            }
        };
        УдалитьАрхивныеБиблиотеки действиеУдалить = new УдалитьАрхивныеБиблиотеки( jfx.контекст.архив );
        ЭкспортироватьSvg действиеЭкспортироватьSvg = new ЭкспортироватьSvg();
        
        ActionNew actionNew = new ActionNew();
        ActionPreview actionPreview = new ActionPreview( svg );
        ActionEdit actionEdit = new ActionEdit();
        ActionRemove actionRemove = new ActionRemove( действиеУдалить );
        ActionExport actionExport = new ActionExport( действиеЭкспортироватьSvg );
        ActionProperties actionProperties = new ActionProperties();

        ThresholdChecker blocker_0_0 = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 0, 0);
        ThresholdChecker blocker_1_1 = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 1, 1);
        ThresholdChecker blocker_1_N = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 1, Integer.MAX_VALUE);

        actionNew       .disableProperty().bind( blocker_0_0 );
        actionPreview   .disableProperty().bind( blocker_1_1 );
        actionEdit      .disableProperty().bind( blocker_1_1 );
        actionRemove    .disableProperty().bind( blocker_1_N );
        actionExport    .disableProperty().bind( blocker_1_N );
        actionProperties.disableProperty().bind( blocker_1_1 );

        setContextMenu( MenuFactory.createContextMenu( 
            new MenuNode( null,
                new MenuNode( actionNew ), 
                new MenuNode( actionPreview ), 
                new MenuNode( actionEdit ), 
                new MenuNode( actionRemove ), 
                null,
                new MenuNode( actionExport ), 
                null,
                new MenuNode( actionProperties ) 
                ) ) );

        actions.addAll( Arrays.asList( 
                actionNew, 
                actionPreview, 
                actionEdit, 
                actionRemove,
                null,
                actionExport, 
                null,
                actionProperties 
                ) );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Библиотека>, ListCell<Библиотека>>
    {
        @Override
        public ListCell<Библиотека> call( ListView<Библиотека> __ )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<Библиотека>
    {
        @Override
        public void updateItem( Библиотека item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.название() );
        }
    }
    
    private class ActionNew extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionNew()
        {
            super( jfx, jfx.словарь( ActionNew.class ) );
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionProperties extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionProperties()
        {
            super( jfx, jfx.словарь( ActionProperties.class ) );
        }
        
        @Override
        public void handle( ActionEvent __ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
