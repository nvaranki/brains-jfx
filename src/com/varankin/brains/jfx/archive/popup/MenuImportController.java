package com.varankin.brains.jfx.archive.popup;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import com.varankin.brains.jfx.history.LocalInputStreamProvider;
import com.varankin.brains.jfx.history.RemoteInputStreamProvider;
import com.varankin.util.LoggerX;

import java.io.InputStream;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 * FXML-контроллер меню импорта пакета в архив, с историей.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class MenuImportController implements Builder<Menu>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( MenuImportController.class );
    private static final String ICON_NET  = "icons16x16/load-internet.png";
    private static final String ICON_FILE = "icons16x16/file-xml.png";
    
    public static final String RESOURCE_FXML  = "/fxml/archive/MenuImport.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private ActionProcessor processor;
    
    @FXML private Menu menu;

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public Menu build()
    {
        MenuItem menuImportFile = new MenuItem(
                LOGGER.text( "menu.import.file" ), 
                icon( ICON_FILE ) );
        menuImportFile.setOnAction( this::onActionImportFile );
        
        MenuItem menuImportNet = new MenuItem(
                LOGGER.text( "menu.import.network" ), 
                icon( ICON_NET ) );
        menuImportNet.setOnAction( this::onActionImportNet );
        
        menu = new Menu( LOGGER.text( "menu.import" ) );
        menu.getItems().addAll( menuImportFile, menuImportNet );

        initialize();
        
        return menu;
    }

    @FXML
    protected void initialize()
    {
        JavaFX jfx = JavaFX.getInstance();
        ObservableList<MenuItem> items = menu.getItems();
        for( int i = 1; i <= jfx.history.historyXmlSize; i++ )
        {
            MenuItem item = new MenuItem();
            item.setUserData( i );
            item.setOnAction( (e) -> onImportFromHistory( (Integer)item.getUserData(), e ) );
            item.setMnemonicParsing( false );
            //item.setAccelerator( KeyCombination.valueOf( "Ctrl+" + Integer.toString( i ) ) );
            onInvalidatedHistory( item, jfx.history.xml );
            jfx.history.xml.addListener( (o) -> onInvalidatedHistory( item, o ) );
            if( i == 1 ) items.add( new SeparatorMenuItem() );
            items.add( item );
        }
    }
    
    @FXML
    private void onActionImportFile( ActionEvent event )
    {
        processor.onPackageFromFile( event );
        event.consume();
    }
    
    @FXML
    private void onActionImportNet( ActionEvent event )
    {
        processor.onPackageFromNet( event );
        event.consume();
    }
    
    private void onImportFromHistory( int позиция, ActionEvent event )
    {
        processor.onPackageFromHistory( позиция, event );
        event.consume();
    }
    
    private void onInvalidatedHistory( MenuItem menuItem, Observable observable )
    {
        int позиция = (Integer)menuItem.getUserData();
        Provider<InputStream> элемент = JavaFX.getInstance().history.xml.get( позиция );
        
        menuItem.disableProperty().setValue( элемент == null );
        
        String название = элемент != null ? элемент.toString() : "";
        menuItem.textProperty().setValue( Integer.toString( позиция ) + ' ' + название );
        
        String path = 
                элемент instanceof LocalInputStreamProvider ? ICON_FILE :
                элемент instanceof RemoteInputStreamProvider ? ICON_NET :
                null;
        menuItem.graphicProperty().setValue( icon( path ) );
    }

    void setProcessor( ActionProcessor processor )
    {
        this.processor = processor;
    }

}
