package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.*;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;


/**
 * FXML-контроллер навигатора по архиву. 
 * 
 * @author &copy; 2014 Николай Варанкин
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

    public ArchiveController()
    {
    }

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

        Pane box = new HBox();// spacing );
        //box.setPrefWidth( 250d );
        HBox.setHgrow( tree, Priority.ALWAYS );
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
        DbАрхив архив = JavaFX.getInstance().контекст.архив;
//        архив.пакеты().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
//        архив.namespaces().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tree.setCellFactory( ( TreeView<DbАтрибутный> view ) -> new CellАтрибутный() );
        tree.setRoot( new TreeItem<>() );
        if( архив != null )
        {
            TreeItem<DbАтрибутный> item = new TitledTreeItem<>( архив );
            tree.getRoot().getChildren().add( item );
        }
        else
            LOGGER.getLogger().log( Level.CONFIG, "No local database available.");
        
        ActionProcessor processor = new ActionProcessor( new SelectionListBinding<>( tree.getSelectionModel() ) );
        toolbarController.setProcessor( processor );
        popupController.setProcessor( processor );


//        tree.addEventHandler( TreeItem.<Атрибутный>branchExpandedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
//        tree.addEventHandler( TreeItem.<Атрибутный>branchCollapsedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
    }
    
}
