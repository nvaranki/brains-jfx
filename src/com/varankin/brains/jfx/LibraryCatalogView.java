package com.varankin.brains.jfx;

import com.varankin.brains.appl.УдалитьАрхивныеБиблиотеки;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgБиблиотека;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.logging.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class LibraryCatalogView extends AbstractCatalogView<Библиотека>
{
    private final static Logger LOGGER = Logger.getLogger( LibraryCatalogView.class.getName() );
    
    LibraryCatalogView( ApplicationView.Context context, ObservableList<Node> toolbar )
    {
        super( context );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.библиотеки() ) );
        
        SvgService<Библиотека> svg = new SvgService<Библиотека>() 
        {
            @Override
            public Provider<String> getPainter( Библиотека библиотека )
            {
                return new SvgБиблиотека( библиотека, new Сборка( библиотека ) );
            }
        };
        SelectionDetector blocker_0_0 = new SelectionDetector( selectionModelProperty(), false, 0, 0 );
        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1 );
        УдалитьАрхивныеБиблиотеки действиеУдалить = new УдалитьАрхивныеБиблиотеки( context.jfx.контекст.архив );
        ЭкспортироватьSvg действиеЭкспортироватьSvg = new ЭкспортироватьSvg();
        
        ActionNew actionNew = new ActionNew( blocker_0_0 );
        ActionPreview actionPreview = new ActionPreview( svg, blocker_1_1 );
        ActionEdit actionEdit = new ActionEdit( blocker_1_1 );
        ActionRemove actionRemove = new ActionRemove( действиеУдалить, blocker_1_N );
        ActionExport actionExport = new ActionExport( действиеЭкспортироватьSvg, blocker_1_N );
        ActionProperties actionProperties = new ActionProperties();

        setContextMenu( MenuFactory.createContextMenu( new MenuNode( null,
                new MenuNode( actionNew ), 
                new MenuNode( actionPreview ), 
                new MenuNode( actionEdit ), 
                new MenuNode( actionRemove ), 
                null,
                new MenuNode( actionExport ), 
                null,
                new MenuNode( actionProperties ) ) ) );

        toolbar.addAll( 
                actionNew.makeButton(), 
                actionPreview.makeButton(), 
                actionEdit.makeButton(), 
                actionRemove.makeButton(),
                new Separator( Orientation.HORIZONTAL ),
                actionExport.makeButton(), 
                new Separator( Orientation.HORIZONTAL ),
                actionProperties.makeButton() );
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Библиотека>, ListCell<Библиотека>>
    {
        @Override
        public ListCell<Библиотека> call( ListView<Библиотека> view )
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
    
    private class ActionNew extends AbstractContextJfxAction<ApplicationView.Context>
    {
        
        ActionNew( ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionNew.class ) );
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionProperties extends AbstractContextJfxAction<ApplicationView.Context>
    {
        
        ActionProperties()
        {
            super( context, context.jfx.словарь( ActionProperties.class ) );
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
