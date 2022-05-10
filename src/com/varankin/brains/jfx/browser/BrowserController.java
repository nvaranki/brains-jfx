package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.OneToOneListBinding;
import java.util.*;
import java.util.logging.*;
import javafx.beans.binding.ListBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер навигатора по проектам. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class BrowserController implements Builder<TitledPane>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserController.class.getName(),
            BrowserController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/Browser.css";
    private static final String CSS_CLASS = "browser";

    public static final String RESOURCE_FXML  = "/fxml/browser/Browser.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();

    @FXML private BrowserToolBarController toolbarController;
    @FXML private BrowserPopupController popupController;
    @FXML private ToolBar toolbar;
    @FXML private TreeView<Элемент> tree;
    @FXML private ContextMenu popup;

    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public TitledPane build()
    {
        popupController = new BrowserPopupController();
        toolbarController = new BrowserToolBarController();
        
        toolbar = toolbarController.build();
        popup = popupController.build();
        
        tree = new TreeView<>();
        tree.setShowRoot( true );
        tree.setEditable( false );
        tree.setContextMenu( popup );
        HBox.setHgrow( tree, Priority.ALWAYS );

        Pane box = new HBox();
        box.getChildren().addAll( toolbar, tree );
        
        TitledPane pane = new TitledPane( RESOURCE_BUNDLE.getString( "browser.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        tree.setCellFactory( treeView -> new BrowserTreeCell<>( treeView ) );
        tree.setRoot( new DelayedNamedTreeItem( JavaFX.getInstance().контекст.мыслитель ) );
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
       
        ListBinding<Элемент> selectionListBinding = new OneToOneListBinding<>( 
                tree.getSelectionModel().getSelectedItems(), TreeItem::getValue );
        toolbarController.selectionProperty().bind( selectionListBinding );
        popupController.selectionProperty().bind( selectionListBinding );
    }
    
}
