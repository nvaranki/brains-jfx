package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.*;
import com.varankin.util.LoggerX;
import java.util.*;
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

    private final SelectionListBinding selection;
    
    @FXML private ArchiveToolBarController toolbarController;
    @FXML private ArchivePopupController popupController;
    @FXML private TreeView<Атрибутный> tree;

    public ArchiveController()
    {
        selection = new SelectionListBinding();
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
        Архив архив = JavaFX.getInstance().контекст.архив;
        TreeItem<Атрибутный> item = new TreeItem<>( архив );
//        архив.пакеты().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
//        архив.namespaces().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
        tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        tree.setCellFactory( ( TreeView<Атрибутный> view ) -> new CellАтрибутный() );
        tree.setRoot( new TreeItem<>() );
        tree.getRoot().getChildren().add( item );
        
        selection.bind( tree.getSelectionModel().getSelectedItems() );
        
        ActionProcessor processor = new ActionProcessor();
        processor.selectionProperty().bind( selection );
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
    
    /**
     * Список выбранных {@linkplain Атрибутный элементов} архива.
     */
    private class SelectionListBinding extends ListBinding<Атрибутный>
    {
        final ObservableList<Атрибутный> LIST = FXCollections.<Атрибутный>observableArrayList();
        
        /**
         * Связывает данный список со списком выбора в {@link TreeView}.
         * 
         * @param list список выбора в {@link TreeView}.
         */
        void bind( ObservableList<TreeItem<Атрибутный>> list )
        {
            super.bind( list );
        }
        
        @Override
        protected ObservableList<Атрибутный> computeValue()
        {
            List<Атрибутный> value = tree == null ? Collections.emptyList() :
                    tree.getSelectionModel().getSelectedItems().stream()
                    .flatMap( ( TreeItem<Атрибутный> i ) -> Stream.of( i.getValue() ) )
                    .collect( Collectors.toList() );
            LIST.setAll( value );
            return LIST;
        }
        
    }
    
}
