package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.history.LocalNeo4jProvider;
import com.varankin.brains.jfx.history.RemoteNeo4jProvider;
import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.container.Provider;
import com.varankin.util.LoggerX;

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
 * FXML-контроллер меню открытия архива, с историей.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class MenuArchiveController implements Builder<Menu>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( MenuArchiveController.class );
    private static final String ICON_NET  = "icons16x16/load-internet.png";
    private static final String ICON_FILE = "icons16x16/file-xml.png";
    
    public static final String RESOURCE_FXML  = "/fxml/archive/PopupArchive.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

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
        MenuItem menuArchiveFile = new MenuItem(
                LOGGER.text( "menu.archive.file" ), 
                icon( ICON_FILE ) );
        menuArchiveFile.setOnAction( this::onArchiveFromFile );
        
        MenuItem menuArchiveNet = new MenuItem(
                LOGGER.text( "menu.archive.network" ), 
                icon( ICON_NET ) );
        menuArchiveNet.setOnAction( this::onArchiveFromNet );
        
        menu = new Menu( LOGGER.text( "menu.archive" ) );
        menu.getItems().addAll( menuArchiveFile, menuArchiveNet );

        initialize();
        
        return menu;
    }

    @FXML
    protected void initialize()
    {
        JavaFX jfx = JavaFX.getInstance();
        ObservableList<MenuItem> items = menu.getItems();
        for( int i = 1; i <= jfx.history.historyArchiveSize; i++ )
        {
            MenuItem item = new MenuItem();
            item.setUserData( i );
            item.setOnAction( (e) -> onArchiveFromHistory( (Integer)item.getUserData(), e ) );
            item.setMnemonicParsing( false );
            //item.setAccelerator( KeyCombination.valueOf( "Ctrl+" + Integer.toString( i ) ) );
            onInvalidatedHistory( item, jfx.history.archive );
            jfx.history.archive.addListener( (o) -> onInvalidatedHistory( item, o ) );
            if( i == 1 ) items.add( new SeparatorMenuItem() );
            items.add( item );
        }
    }
    
    @FXML
    private void onArchiveFromFile( ActionEvent event )
    {
        ActionProcessor.onArchiveFromFile( event );
        event.consume();
    }
    
    @FXML
    private void onArchiveFromNet( ActionEvent event )
    {
        ActionProcessor.onArchiveFromNet( event );
        event.consume();
    }
    
    private void onArchiveFromHistory( int позиция, ActionEvent event )
    {
        ActionProcessor.onArchiveFromHistory( позиция, event );
        event.consume();
    }
    
    private void onInvalidatedHistory( MenuItem menuItem, Observable observable )
    {
        int позиция = (Integer)menuItem.getUserData();
        Provider<DbАрхив> элемент = JavaFX.getInstance().history.archive.get( позиция );
        
        menuItem.disableProperty().setValue( элемент == null );
        
        String название = элемент != null ? элемент.toString() : "";
        menuItem.textProperty().setValue( Integer.toString( позиция ) + ' ' + название );
        
        String path = 
                элемент instanceof LocalNeo4jProvider ? ICON_FILE :
                элемент instanceof RemoteNeo4jProvider ? ICON_NET :
                null;
        menuItem.graphicProperty().setValue( icon( path ) );
    }

}
