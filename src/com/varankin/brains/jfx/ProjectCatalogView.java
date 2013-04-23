package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.УдалитьАрхивныеПроекты;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * @author &copy; 2012 Николай Варанкин
 */
class ProjectCatalogView extends AbstractCatalogView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    ProjectCatalogView( ApplicationView.Context context, ObservableList<Node> toolbar )
    {
        super( context );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setCellFactory( new RowBuilder() );
        itemsProperty().bind( new ObservableValueList<>( context.jfx.контекст.архив.проекты() ) );

        SvgService<Проект> svg = new SvgService<Проект>() 
        {
            @Override
            public Provider<String> getPainter( Проект проект )
            {
                return new SvgПроект( проект, new Сборка( проект ) );
            }
        };
        SelectionDetector blocker_0_0 = new SelectionDetector( selectionModelProperty(), false, 0, 0 );
        SelectionDetector blocker_1_1 = new SelectionDetector( selectionModelProperty(), false, 1, 1 );
        SelectionDetector blocker_1_N = new SelectionDetector( selectionModelProperty(), false, 1 );
        ЗагрузитьАрхивныйПроект действиеЗагрузить = new ЗагрузитьАрхивныйПроект( context.jfx.контекст );
        УдалитьАрхивныеПроекты действиеУдалить = new УдалитьАрхивныеПроекты( context.jfx.контекст.архив );
        ЭкспортироватьSvg действиеЭкспортироватьSvg = new ЭкспортироватьSvg();
        
        ActionLoad actionLoad = new ActionLoad( действиеЗагрузить, blocker_1_1 );
        ActionNew actionNew = new ActionNew( blocker_0_0 );
        ActionPreview actionPreview = new ActionPreview( svg, blocker_1_1 );
        ActionEdit actionEdit = new ActionEdit( blocker_1_1 );
        ActionRemove actionRemove = new ActionRemove( действиеУдалить, blocker_1_N );
        ActionExport actionExport = new ActionExport( действиеЭкспортироватьSvg, blocker_1_N );
        ActionProperties actionProperties = new ActionProperties();
        
        setContextMenu( context.menuFactory.createContextMenu( new MenuNode( null,
                new MenuNode( actionLoad ), 
                null,
                new MenuNode( actionNew ), 
                new MenuNode( actionPreview ), 
                new MenuNode( actionEdit ), 
                new MenuNode( actionRemove ), 
                null,
                new MenuNode( actionExport ), 
                null,
                new MenuNode( actionProperties ) ) ) );
        
        toolbar.addAll( 
                actionLoad.makeButton(), 
                new Separator( Orientation.HORIZONTAL ),
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
    
    private class ActionLoad extends AbstractContextJfxAction<ApplicationView.Context>
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
