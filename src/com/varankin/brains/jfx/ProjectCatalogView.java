package com.varankin.brains.jfx;

import com.varankin.biz.action.Действие;
import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.ДействияПоПорядку.Приоритет;
import com.varankin.brains.appl.ЗагрузитьАрхивныйПроект;
import com.varankin.brains.appl.УдалитьАрхивныеПроекты;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.artificial.io.svg.SvgService;
import com.varankin.brains.artificial.io.svg.SvgПроект;
import com.varankin.brains.db.XmlNameSpace;

import static com.varankin.brains.artificial.io.xml.XmlBrains.XMLNS_BRAINS;
import static com.varankin.brains.artificial.io.xml.XmlBrains.XML_NAME;
import static com.varankin.brains.artificial.io.xml.XmlBrains.XML_PROJECT;

import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Пакет;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.factory.IФабрикаБазовыхЭлементов;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.util.Callback;

import static com.varankin.brains.artificial.io.xml.XmlBrains.XMLNS_BRAINS;
import static com.varankin.brains.artificial.io.xml.XmlBrains.XML_BRAINS;

/**
 * Каталог проектов архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class ProjectCatalogView extends AbstractCatalogView<Проект>
{
    private final static Logger LOGGER = Logger.getLogger( ProjectCatalogView.class.getName() );
    
    private final Image iconEditor;
    @Deprecated private final Пакет пакет = null;
    
    ProjectCatalogView( JavaFX jfx )
    {
        super( jfx, null );//jfx.контекст.архив.проекты() );
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
        Действие<Проект> действиеЗагрузить = new ЗагрузитьАрхивныйПроект( jfx.контекст );
        Действие действиеУдалить = new УдалитьАрхивныеПроекты( null );//jfx.контекст.архив );
        ЭкспортироватьSvg действиеЭкспортироватьSvg = new ЭкспортироватьSvg( jfx.контекст );
        
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
        actionEdit      .disableProperty().bind( blocker_1_N );
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
        
        iconEditor = actionEdit.getIconImage();
    }
    
        //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<Проект>, ListCell<Проект>>
    {
        @Override
        public ListCell<Проект> call( ListView<Проект> __ )
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
        final Callback<Void,Проект> фабрика;
        
        ActionNew()
        {
            super( jfx, jfx.словарь( ActionNew.class ) );
            фабрика = ( Void v ) ->
            {
                Архив архив = jfx.контекст.архив;
                Транзакция транзакция = архив.транзакция();
                транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив );
                XmlNameSpace ns = архив.определитьПространствоИмен( XMLNS_BRAINS, XML_BRAINS );
                Проект элемент = (Проект)архив.создатьНовыйЭлемент( XML_PROJECT, ns, null );
                элемент.определить( XML_NAME, null, null, "New project #" + пакет.проекты().size() );
                пакет.проекты().add( элемент );
                транзакция.завершить( true );
                
                транзакция = архив.транзакция();
                транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, архив );
                архив.setPropertyValue( Коллекция.PROPERTY_UPDATED, true );
                транзакция.завершить( true );
                
                return элемент;
            };
        }
        
        @Override
        public void handle( ActionEvent event )
        {
            handleEditElement( фабрика, iconEditor );
        }
    }
    
    private class ActionLoad extends AbstractContextJfxAction<JavaFX>
    {
        Действие<Iterable<? extends Проект>> ДЕЙСТВИЕ;
        
        ActionLoad( Действие<Проект> действие )
        {
            super( jfx, jfx.словарь( ActionLoad.class ) );
            ДЕЙСТВИЕ = new ДействияПоПорядку<>( Приоритет.КОНТЕКСТ, действие );
        }
        
        @Override
        public void handle( ActionEvent __ )
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
        public void handle( ActionEvent __ )
        {
            LOGGER.info( "Sorry, the command is not implemented." );//TODO not impl.
        }
    }
    
    //</editor-fold>
    
}
