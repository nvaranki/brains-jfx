package com.varankin.brains.jfx;

import com.varankin.brains.appl.УдалитьАрхивныеБиблиотеки;
import com.varankin.brains.appl.ЭкспортироватьSvg;
import com.varankin.brains.io.svg.SvgService;
import com.varankin.brains.io.svg.SvgБиблиотека;
import com.varankin.brains.db.XmlNameSpace;

import static com.varankin.brains.io.xml.XmlBrains.XMLNS_BRAINS;
import static com.varankin.brains.io.xml.XmlBrains.XML_LIBRARY;
import static com.varankin.brains.io.xml.XmlBrains.XML_NAME;

import com.varankin.brains.db.Архив;
import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.db.Коллекция;
import com.varankin.brains.db.Пакет;
import com.varankin.brains.db.Сборка;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.MenuFactory.MenuNode;
import com.varankin.io.container.Provider;
import java.util.Arrays;
import java.util.logging.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.util.Callback;

import static com.varankin.brains.io.xml.XmlBrains.XML_BRAINS;

/**
 * Каталог библиотек архива.
 *
 * @author &copy; 2013 Николай Варанкин
 */
class LibraryCatalogView extends AbstractCatalogView<DbБиблиотека>
{
    private final static Logger LOGGER = Logger.getLogger( LibraryCatalogView.class.getName() );
    
    private final Image iconEditor;
    @Deprecated private final Пакет пакет = null;
    
    LibraryCatalogView( JavaFX jfx )
    {
        super( jfx, null );//jfx.контекст.архив.библиотеки() );
        getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        setCellFactory( new RowBuilder() );
        
        SvgService<DbБиблиотека> svg = new SvgService<DbБиблиотека>() 
        {
            @Override
            public Provider<String> генератор( DbБиблиотека библиотека )
            {
                return new SvgБиблиотека( библиотека, new Сборка( библиотека ) );
            }
        };
        УдалитьАрхивныеБиблиотеки действиеУдалить = null;//new УдалитьАрхивныеБиблиотеки( jfx.контекст.архив );
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
        actionEdit      .disableProperty().bind( blocker_1_N );
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
        
        iconEditor = actionEdit.getIconImage();
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">

    private class RowBuilder implements Callback<ListView<DbБиблиотека>, ListCell<DbБиблиотека>>
    {
        @Override
        public ListCell<DbБиблиотека> call( ListView<DbБиблиотека> __ )
        {
            return new VisibleRow();
        }
    }

    static private class VisibleRow extends ListCell<DbБиблиотека>
    {
        @Override
        public void updateItem( DbБиблиотека item, boolean empty ) 
        {
            super.updateItem( item, empty );
            setText( empty ? null : item.название() );
        }
    }
    
    private class ActionNew extends AbstractContextJfxAction<JavaFX>
    {
        final Callback<Void,DbБиблиотека> фабрика;
        
        ActionNew()
        {
            super( jfx, jfx.словарь( ActionNew.class ) );
            фабрика = ( Void v ) ->
            {
                Архив архив = jfx.контекст.архив;
                Транзакция транзакция = архив.транзакция();
                транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, архив );
                XmlNameSpace ns = архив.определитьПространствоИмен( XMLNS_BRAINS, XML_BRAINS );
                DbБиблиотека элемент = (DbБиблиотека)архив.создатьНовыйЭлемент( XML_LIBRARY, ns, null );
                элемент.определить( XML_NAME, null, null, "New library #" + пакет.библиотеки().size() );
                пакет.библиотеки().add( элемент );
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
