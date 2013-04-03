package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.УдалитьАрхивныеПроекты;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends AbstractCatalogView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    ProjectCatalogView( ApplicationView.Context context, HBox toolbar )
    {
        super( context );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        SvgService<Проект> svg = new SvgService<Проект>() 
        {
            @Override
            public Provider<String> getPainter( Проект проект )
            {
                return new SvgПроект( проект, new Сборка( проект ) );
            }
        };
        setContextMenu( context.menuFactory.createContextMenu( popup( svg ) ) );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.проекты() ) );

        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        toolbar.getChildren().addAll( new Button( "Load" ) );
        toolbar.getChildren().addAll( makeButton( new ActionPreview( svg, blocker_1_1 ) ) );
        toolbar.getChildren().addAll( new Button( "Edit" ) );
        toolbar.getChildren().addAll( new Button( "Remove" ) );
        toolbar.getChildren().addAll( new Button( "Export" ) );
        toolbar.getChildren().addAll( new Button( "Properties" ) );
    }
    
    private static Button makeButton( com.varankin.util.jfx.AbstractJfxAction node )
    {
        Button item = new Button();
        item.setMnemonicParsing( false );
        item.textProperty().bind( node.textProperty() );
        //item.setAccelerator( node.shortcut );
        item.setGraphic( node.icon );
        item.setOnAction( node );
        item.disableProperty().bind( node.disableProperty() );
        return item; 
    }
    
    private MenuNode popup( SvgService<Проект> svg )
    {
        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1 );
        return new MenuNode( null,
                new MenuNode( new ActionLoad( new ЗагрузитьАрхивныйПроект( context.jfx.контекст ), blocker_1_1 ) ),
                null, 
                new MenuNode( new ActionPreview( svg, blocker_1_1 ) ),
                new MenuNode( new ActionEdit( blocker_1_1 ) ),
                new MenuNode( new ActionRemove( new УдалитьАрхивныеПроекты( context.jfx.контекст.архив ), blocker_1_N ) ),
                null, 
                new MenuNode( new ActionExport( new ЭкспортироватьSvg(), blocker_1_N ) ),
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
            setText( empty ? null : item.название() );
        }
    }
    
    private class ActionLoad extends AbstractJfxAction<ApplicationView.Context>
    {
        Действие<Collection<Проект>> ДЕЙСТВИЕ;
        
        ActionLoad( Действие<Collection<Проект>> действие, ObservableValue<Boolean> blocker )
        {
            super( context, context.jfx.словарь( ActionLoad.class ) );
            ДЕЙСТВИЕ = действие;
            disableProperty().bind( blocker );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            //TODO confirmation dialog
            new ApplicationActionWorker<>( ДЕЙСТВИЕ, ceлектор ).execute( context.jfx );
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
