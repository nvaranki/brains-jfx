package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.*;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;


/**
 * FXML-контроллер навигатора по архиву. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class ArchiveController implements Builder<TitledPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchiveController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Archive.css";
    private static final String CSS_CLASS = "archive";

    public static final String RESOURCE_FXML  = "/fxml/archive/Archive.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @FXML private ArchiveToolBarController toolbarController;
    @FXML private ArchivePopupController popupController;
    @FXML private TreeView<DbАтрибутный> tree;

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public TitledPane build()
    {
        popupController = new ArchivePopupController();
        toolbarController = new ArchiveToolBarController();
        
        tree = new TreeView<>();
        tree.setShowRoot( false );
        tree.setEditable( false );
        tree.setContextMenu( popupController.build() );
        HBox.setHgrow( tree, Priority.ALWAYS );

        Pane box = new HBox();
        box.getChildren().addAll( toolbarController.build(), tree );
        
        TitledPane pane = new TitledPane( LOGGER.text( "archive.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tree.setCellFactory( view -> new ArchiveTreeCell() );
        tree.setRoot( new TreeItem<>() );
        tree.getRoot().getChildren().add( getLocalArchiveTreeItem() );
        
        ActionProcessor processor = new ActionProcessor( tree.getSelectionModel().getSelectedItems() );
        toolbarController.setProcessor( processor );
        popupController.setProcessor( processor );
    }
    
    private TreeItem<DbАтрибутный> getLocalArchiveTreeItem()
    {
        JavaFX jfx = JavaFX.getInstance();
        DbАрхив архив = jfx.контекст.архив;
        TreeItem<DbАтрибутный> item;
        if( архив != null )
        {
            item = jfx.archiveFoldedTreeItems ?
                    new FoldedTreeItem( архив ) : new MergedTreeItem( архив );
        }
        else
        {
            LOGGER.getLogger().log( Level.CONFIG, "No local database available.");
            item = new TreeItem<DbАтрибутный>() 
            {
                @Override public String toString() { return "No database"; }
            };
        }
        return item;
    }
    
}
