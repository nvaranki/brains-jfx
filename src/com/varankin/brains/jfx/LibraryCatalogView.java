package com.varankin.brains.jfx;

import com.varankin.brains.appl.УдалитьАрхивныеБиблиотеки;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgБиблиотека;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class LibraryCatalogView extends AbstractCatalogView<Библиотека>
{
    private final static Logger LOGGER = Logger.getLogger( LibraryCatalogView.class.getName() );
    
    LibraryCatalogView( ApplicationView.Context context, HBox toolbar )
    {
        super( context );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setContextMenu( context.menuFactory.createContextMenu( popup() ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.библиотеки() ) );

        toolbar.getChildren().addAll( new Button( "Preview" ) );
        toolbar.getChildren().addAll( new Button( "Edit" ) );
        //toolbar.getChildren().addAll( new Button( "Load" ) );
        toolbar.getChildren().addAll( new Button( "Remove" ) );
        toolbar.getChildren().addAll( new Button( "Export" ) );
        toolbar.getChildren().addAll( new Button( "Properties" ) );
    }
    
    private MenuNode popup()
    {
        SvgService<Библиотека> svg = new SvgService<Библиотека>() 
        {
            @Override
            public Provider<String> getPainter( Библиотека библиотека )
            {
                return new SvgБиблиотека( библиотека, new Сборка( библиотека ) );
            }
        };
        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1 );
        return new MenuNode( null,
                new MenuNode( new ActionPreview( svg, blocker_1_1 ) ),
                new MenuNode( new ActionEdit( blocker_1_1 ) ),
                new MenuNode( new ActionRemove( new УдалитьАрхивныеБиблиотеки( context.jfx.контекст.архив ), blocker_1_N ) ),
                null, 
                new MenuNode( new ActionExport( new ЭкспортироватьSvg(), blocker_1_N ) ),
                null, 
                new MenuNode( new ActionProperties() )
                );
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
    
    private class ActionProperties extends AbstractJfxAction<ApplicationView.Context>
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
