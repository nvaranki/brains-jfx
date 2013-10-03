package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.УдалитьАрхивныеПроекты;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgService;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.factory.ФабрикаБазовыхЭлементов;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ProjectCatalogView extends AbstractCatalogView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    ProjectCatalogView( JavaFX jfx )
    {
        super( jfx, jfx.контекст.архив.проекты() );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setCellFactory( new RowBuilder() );

        SvgService<Проект> svg = new SvgService<Проект>() 
        {
            @Override
            public Provider<String> генератор( Проект проект )
            {
                return new SvgПроект( проект, new Сборка( проект ) );
            }
        };
        Действие<Проект> действиеЗагрузить = 
            new ЗагрузитьАрхивныйПроект( jfx.контекст, ФабрикаБазовыхЭлементов.class );
        УдалитьАрхивныеПроекты действиеУдалить = new УдалитьАрхивныеПроекты( jfx.контекст.архив );
        ЭкспортироватьSvg действиеЭкспортироватьSvg = new ЭкспортироватьSvg();
        
        ActionLoad actionLoad = new ActionLoad( действиеЗагрузить );
        ActionNew actionNew = new ActionNew();
        ActionPreview actionPreview = new ActionPreview( svg );
        ActionEdit actionEdit = new ActionEdit();
        ActionRemove actionRemove = new ActionRemove( действиеУдалить );
        ActionExport actionExport = new ActionExport( действиеЭкспортироватьSvg );
        ActionProperties actionProperties = new ActionProperties();

        ThresholdChecker blocker_0_0 = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 0, 0);
        ThresholdChecker blocker_1_1 = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 1, 1);
        ThresholdChecker blocker_1_N = new ThresholdChecker( selectionModelProperty().getValue().getSelectedItems(), false, 1, Integer.MAX_VALUE);

        actionLoad      .disableProperty().bind( blocker_1_N );
        actionNew       .disableProperty().bind( blocker_0_0 );
        actionPreview   .disableProperty().bind( blocker_1_1 );
        actionEdit      .disableProperty().bind( blocker_1_1 );
        actionRemove    .disableProperty().bind( blocker_1_N );
        actionExport    .disableProperty().bind( blocker_1_N );
        actionProperties.disableProperty().bind( blocker_1_1 );
        
        setContextMenu( MenuFactory.createContextMenu( 
            new MenuNode( null,
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
        
        actions.addAll( Arrays.asList( 
                actionLoad, 
                null,
                actionNew, 
                actionPreview, 
                actionEdit, 
                actionRemove, 
                null,
                actionExport, 
                null,
                actionProperties ) );
    }
    
        //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Проект>, ListCell<Проект>>
    {
        @Override
        public ListCell<Проект> call( ListView<Проект> _ )
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
    
    private class ActionNew extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionNew()
        {
            super( jfx, jfx.словарь( ActionNew.class ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    private class ActionLoad extends AbstractContextJfxAction<JavaFX>
    {
        Действие<Iterable<? extends Проект>> ДЕЙСТВИЕ;
        
        ActionLoad( Действие<Проект> действие )
        {
            super( jfx, jfx.словарь( ActionLoad.class ) );
            ДЕЙСТВИЕ = new ДействияПоПорядку<>( jfx.контекст, Приоритет.КОНТЕКСТ, действие );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            // собрать выделенные элементы немедленно, ибо список может затем измениться другими процессами
            List<Проект> ceлектор = new ArrayList<>( getSelectionModel().getSelectedItems() );
            //TODO confirmation dialog
            new ApplicationActionWorker<>( ДЕЙСТВИЕ, ceлектор ).execute( контекст );
        }
    }
    
    private class ActionProperties extends AbstractContextJfxAction<JavaFX>
    {
        
        ActionProperties()
        {
            super( jfx, jfx.словарь( ActionProperties.class ) );
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
