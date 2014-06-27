package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Атрибутный;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import javafx.util.Callback;

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
        навигатор.setCellFactory( new CellFactoryАтрибутный() );

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
        MenuItem menuNew = new MenuItem( 
                LOGGER.text( "archive.popup.new" ), icon( "icons16x16/new-library.png" ) );
        MenuItem menuPreview = new MenuItem( 
                LOGGER.text( "archive.popup.preview" ), icon( "icons16x16/preview.png" ) );
        MenuItem menuEdit = new MenuItem( 
                LOGGER.text( "archive.popup.edit" ), icon( "icons16x16/edit.png" ) );
        MenuItem menuRemove = new MenuItem( 
                LOGGER.text( "archive.popup.remove" ), icon( "icons16x16/remove.png" ) );
        MenuItem menuImportFile = new MenuItem( 
                LOGGER.text( "archive.popup.import.file" ), icon( "icons16x16/file-xml.png" ) );
        MenuItem menuImportNet = new MenuItem( 
                LOGGER.text( "archive.popup.import.network" ), icon( "icons16x16/load-internet.png" ) );
        MenuItem menuExport = new MenuItem( 
                LOGGER.text( "archive.popup.export" ), icon( "icons16x16/file-export.png" ) );
        MenuItem menuProperties = new MenuItem( 
                LOGGER.text( "archive.popup.properties" ), icon( "icons16x16/properties.png" ) );
        
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
        Button buttonNew = new Button();
        buttonNew.setTooltip( new Tooltip( LOGGER.text( "archive.popup.new" ) ) );
        buttonNew.setGraphic( icon( "icons16x16/new-library.png" ) );
        Button buttonPreview = new Button();
        buttonPreview.setTooltip( new Tooltip( LOGGER.text( "archive.popup.preview" ) ) );
        buttonPreview.setGraphic( icon( "icons16x16/preview.png" ) );
        Button buttonEdit = new Button();
        buttonEdit.setTooltip( new Tooltip( LOGGER.text( "archive.popup.edit" ) ) );
        buttonEdit.setGraphic( icon( "icons16x16/edit.png" ) );
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( LOGGER.text( "archive.popup.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        Button buttonImportFile = new Button();
        buttonImportFile.setTooltip( new Tooltip( LOGGER.text( "archive.popup.import.file" ) ) );
        buttonImportFile.setGraphic( icon( "icons16x16/file-xml.png" ) );
        Button buttonImportNet = new Button();
        buttonImportNet.setTooltip( new Tooltip( LOGGER.text( "archive.popup.import.network" ) ) );
        buttonImportNet.setGraphic( icon( "icons16x16/load-internet.png" ) );
        Button buttonExport = new Button();
        buttonExport.setTooltip( new Tooltip( LOGGER.text( "archive.popup.export" ) ) );
        buttonExport.setGraphic( icon( "icons16x16/file-export.png" ) );
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( LOGGER.text( "archive.popup.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        
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
        Атрибутный архив = JavaFX.getInstance().контекст.архив;
        навигатор.getRoot().getChildren().add( new TreeItem<>( архив ) );
    }
    
}
