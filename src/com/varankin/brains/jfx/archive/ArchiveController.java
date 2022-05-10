package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
import com.varankin.brains.jfx.archive.popup.ArchivePopupController;
import com.varankin.brains.jfx.*;
import com.varankin.brains.jfx.db.FxАрхив;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.util.LoggerX;

import java.util.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;


/**
 * FXML-контроллер навигатора по архиву. 
 * 
 * @author &copy; 2017 Николай Варанкин
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
    @FXML private TreeView<FxАтрибутный> tree;

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
        tree.setCellFactory( view -> new ArchiveTreeCell( view ) );
        tree.setRoot( new TreeItem<>() );

        ObservableList<FxАрхив> архивы = JavaFX.getInstance().архивы;
        архивы.addListener( this::onArchiveListChanged );
        for( FxАрхив архив : архивы )
            tree.getRoot().getChildren().add( createArchiveTree( архив ) );
        
        ActionProcessor processor = new ActionProcessor( tree.getSelectionModel().getSelectedItems() );
        toolbarController.setProcessor( processor );
        popupController.setProcessor( processor );
    }
    
    private TreeItem createArchiveTree( FxАрхив архив )
    {
        return JavaFX.getInstance().archiveFoldedTreeItems ?
            new FoldedTreeItem( архив ) : new MergedTreeItem( архив );
    }
    
    private void onArchiveListChanged( ListChangeListener.Change<? extends FxАрхив> c )
    {
        while( c.next() )
        {
            if( c.wasPermutated() )
            {
                for( int i = c.getFrom(); i < c.getTo(); ++i )
                {
                    //if(true)continue;//permutate
                }
            }
            else if( c.wasUpdated() )
            {
                //if(true)continue;//update item
            }
            else
            {
                ObservableList<TreeItem<FxАтрибутный>> children = tree.getRoot().getChildren();
                c.getRemoved().forEach( архив -> children.removeIf( i -> i.getValue().equals( архив ) ) );
                c.getAddedSubList().forEach( архив -> children.add( createArchiveTree( архив ) ) );
            }
        }
    }

}
