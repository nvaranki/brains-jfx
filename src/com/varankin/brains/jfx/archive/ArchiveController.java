package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.ДействияПоПорядку;
import com.varankin.brains.appl.СоздатьНовыйПакет;
import com.varankin.brains.appl.УдалитьИзАрхива;
import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.db.Пакет;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.ApplicationActionWorker;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;

import static com.varankin.brains.artificial.io.xml.XmlBrains.*;
import static com.varankin.brains.jfx.JavaFX.icon;

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
    
    public static final String RESOURCE_FXML  = "/fxml/editor/Archive.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @FXML private TreeView<Атрибутный> навигатор;


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
        навигатор = new TreeView<>( new TreeItem<Атрибутный>() );
        навигатор.setShowRoot( false );
        навигатор.setEditable( false );
        навигатор.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        навигатор.setContextMenu( buildContextMenu() );
        навигатор.setCellFactory( ( TreeView<Атрибутный> view ) -> new CellАтрибутный() );
//        навигатор.addEventHandler( TreeItem.<Атрибутный>branchExpandedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });
//        навигатор.addEventHandler( TreeItem.<Атрибутный>branchCollapsedEvent(), 
//                new EventHandler<TreeItem.TreeModificationEvent<Атрибутный>>()
//        {
//
//            @Override
//            public void handle( TreeItem.TreeModificationEvent<Атрибутный> event )
//            {
//                event.getTreeItem();
//            }
//        });

        Pane box = new HBox();// spacing );
        box.setPrefWidth( 250d );
        HBox.setHgrow( навигатор, Priority.ALWAYS );
        box.getChildren().addAll( buildToolBar(), навигатор );
        
        TitledPane pane = new TitledPane( LOGGER.text( "archive.title" ), box );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    private ContextMenu buildContextMenu()
    {
        MenuItem menuLoad = new MenuItem( 
                LOGGER.text( "archive.popup.load" ), icon( "icons16x16/load.png" ) );
        menuLoad.setOnAction( this::onActionLoad );
        
        MenuItem menuNew = new MenuItem( 
                LOGGER.text( "archive.popup.new" ), icon( "icons16x16/new-library.png" ) );
        menuNew.setOnAction( this::onActionNew );
        
        MenuItem menuPreview = new MenuItem( 
                LOGGER.text( "archive.popup.preview" ), icon( "icons16x16/preview.png" ) );
        menuPreview.setOnAction( this::onActionPreview );
        
        MenuItem menuEdit = new MenuItem( 
                LOGGER.text( "archive.popup.edit" ), icon( "icons16x16/edit.png" ) );
        menuEdit.setOnAction( this::onActionEdit );
        
        MenuItem menuRemove = new MenuItem( 
                LOGGER.text( "archive.popup.remove" ), icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        
        MenuItem menuImportFile = new MenuItem( 
                LOGGER.text( "archive.popup.import.file" ), icon( "icons16x16/file-xml.png" ) );
        menuImportFile.setOnAction( this::onActionImportFile );
        
        MenuItem menuImportNet = new MenuItem( 
                LOGGER.text( "archive.popup.import.network" ), icon( "icons16x16/load-internet.png" ) );
        menuImportNet.setOnAction( this::onActionImportNet );
        
        MenuItem menuExport = new MenuItem( 
                LOGGER.text( "archive.popup.export" ), icon( "icons16x16/file-export.png" ) );
        menuExport.setOnAction( this::onActionExport );
        
        MenuItem menuProperties = new MenuItem( 
                LOGGER.text( "archive.popup.properties" ), icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll
        ( 
                menuLoad,
                new SeparatorMenuItem(),
                menuPreview,
                menuEdit,
                menuRemove,
                new SeparatorMenuItem(),
                menuNew,
                menuImportFile,
                menuImportNet,
                menuExport,
                new SeparatorMenuItem(),
                menuProperties
        );
        return menu;
    }
    
    private ToolBar buildToolBar()
    {
        Button buttonLoad = new Button();
        buttonLoad.setTooltip( new Tooltip( LOGGER.text( "archive.popup.load" ) ) );
        buttonLoad.setGraphic( icon( "icons16x16/load.png" ) );
        buttonLoad.setOnAction( this::onActionLoad );
        
        Button buttonNew = new Button();
        buttonNew.setTooltip( new Tooltip( LOGGER.text( "archive.popup.new" ) ) );
        buttonNew.setGraphic( icon( "icons16x16/new-library.png" ) );
        buttonNew.setOnAction( this::onActionNew );
        
        Button buttonPreview = new Button();
        buttonPreview.setTooltip( new Tooltip( LOGGER.text( "archive.popup.preview" ) ) );
        buttonPreview.setGraphic( icon( "icons16x16/preview.png" ) );
        buttonPreview.setOnAction( this::onActionPreview );
        
        Button buttonEdit = new Button();
        buttonEdit.setTooltip( new Tooltip( LOGGER.text( "archive.popup.edit" ) ) );
        buttonEdit.setGraphic( icon( "icons16x16/edit.png" ) );
        buttonEdit.setOnAction( this::onActionEdit );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( LOGGER.text( "archive.popup.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        
        Button buttonImportFile = new Button();
        buttonImportFile.setTooltip( new Tooltip( LOGGER.text( "archive.popup.import.file" ) ) );
        buttonImportFile.setGraphic( icon( "icons16x16/file-xml.png" ) );
        buttonImportFile.setOnAction( this::onActionImportFile );
        
        Button buttonImportNet = new Button();
        buttonImportNet.setTooltip( new Tooltip( LOGGER.text( "archive.popup.import.network" ) ) );
        buttonImportNet.setGraphic( icon( "icons16x16/load-internet.png" ) );
        buttonImportNet.setOnAction( this::onActionImportNet );
        
        Button buttonExport = new Button();
        buttonExport.setTooltip( new Tooltip( LOGGER.text( "archive.popup.export" ) ) );
        buttonExport.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExport.setOnAction( this::onActionExport );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( LOGGER.text( "archive.popup.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        toolbar.getItems().addAll
        ( 
                buttonLoad,
                new Separator( Orientation.HORIZONTAL ),
                buttonPreview,
                buttonEdit,
                buttonRemove,
                new Separator( Orientation.HORIZONTAL ),
                buttonNew,
                buttonImportFile,
                buttonImportNet,
                buttonExport,
                new Separator( Orientation.HORIZONTAL ),
                buttonProperties
        );
        return toolbar;
    }
    
    @FXML
    protected void initialize()
    {
        Архив архив = JavaFX.getInstance().контекст.архив;
        TreeItem<Атрибутный> item = new TreeItem<>( архив );
//        архив.пакеты().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
//        архив.namespaces().наблюдатели().add( new МониторКоллекции( item.getChildren() ) );
        навигатор.getRoot().getChildren().add( item );
    }
    
    @FXML
    private void onActionNew( ActionEvent event )
    {
        Архив архив = JavaFX.getInstance().контекст.архив; //TODO other object types?
        JavaFX.getInstance().execute( new СоздатьНовыйПакет(), архив );
        event.consume();
    }
    
    @FXML
    private void onActionLoad( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionPreview( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionEdit( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionRemove( ActionEvent event )
    {
        List<TreeItem<Атрибутный>> ceлектор = навигатор.getSelectionModel().getSelectedItems();
        Collection<Атрибутный> элементы = new ArrayList<>( ceлектор.size() );
        ceлектор.forEach( ( TreeItem<Атрибутный> t ) -> элементы.add( t.getValue() ) );
        //TODO confirmation dialog
        JavaFX.getInstance().execute( new ДействияПоПорядку<Атрибутный>( 
                ДействияПоПорядку.Приоритет.КОНТЕКСТ, new УдалитьИзАрхива() ), элементы );
        event.consume();
    }
    
    @FXML
    private void onActionImportFile( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionImportNet( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionExport( ActionEvent event )
    {
        
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent event )
    {
        
        event.consume();
    }
    
}
