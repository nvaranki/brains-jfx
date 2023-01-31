package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 * FXML-контроллер набора инструментов навигатора по архиву.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public class ArchiveToolBarController implements Builder<ToolBar>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchiveToolBarController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/ArchiveToolBar.css";
    private static final String CSS_CLASS = "archive-toolbar";

    public static final String RESOURCE_FXML  = "/fxml/archive/ArchiveToolBar.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final BooleanProperty disableOpen;
    private final BooleanProperty disableClose;
    private final BooleanProperty disableNew;
    private final BooleanProperty disableLoad;
    private final BooleanProperty disablePreview;
    private final BooleanProperty disableEdit;
    private final BooleanProperty disableMultiply;
    private final BooleanProperty disableRemove;
    private final BooleanProperty disableProperties;
    private final BooleanProperty disableImportFile;
    private final BooleanProperty disableImportNet;
    private final BooleanProperty disableExportXml;
    private final BooleanProperty disableExportPic;
    
    private ActionProcessor processor;

    public ArchiveToolBarController()
    {
        disableOpen = new SimpleBooleanProperty( this, "disableNew" );
        disableClose = new SimpleBooleanProperty( this, "disableNew" );
        disableNew = new SimpleBooleanProperty( this, "disableNew" );
        disableLoad = new SimpleBooleanProperty( this, "disableLoad" );
        disablePreview = new SimpleBooleanProperty( this, "disablePreview" );
        disableEdit = new SimpleBooleanProperty( this, "disableEdit" );
        disableMultiply = new SimpleBooleanProperty( this, "disableMultiply" );
        disableRemove = new SimpleBooleanProperty( this, "disableRemove" );
        disableProperties = new SimpleBooleanProperty( this, "disableProperties" );
        disableImportFile = new SimpleBooleanProperty( this, "disableImportFile" );
        disableImportNet = new SimpleBooleanProperty( this, "disableImportNet" );
        disableExportXml = new SimpleBooleanProperty( this, "disableExportXml" );
        disableExportPic = new SimpleBooleanProperty( this, "disableExportPic" );
    }
    
    /**
     * Создает набор инструментов навигатора по архиву.
     * Применяется в конфигурации без FXML.
     * 
     * @return набор инструментов. 
     */
    @Override
    public ToolBar build()
    {
        Button buttonOpen = new Button();
        buttonOpen.setTooltip( new Tooltip( LOGGER.text( "archive.action.archive-open" ) ) );
        buttonOpen.setGraphic( icon( "icons16x16/archive.png" ) ); //TODO archive-open.png
        buttonOpen.setOnAction( this::onActionOpen );
        buttonOpen.disableProperty().bind( disableOpen );
        
        Button buttonClose = new Button();
        buttonClose.setTooltip( new Tooltip( LOGGER.text( "archive.action.archive-close" ) ) );
        buttonClose.setGraphic( icon( "icons16x16/archive.png" ) ); //TODO archive-close.png
        buttonClose.setOnAction( this::onActionClose );
        buttonClose.disableProperty().bind( disableClose );
        
        Button buttonLoad = new Button();
        buttonLoad.setTooltip( new Tooltip( LOGGER.text( "archive.action.load" ) ) );
        buttonLoad.setGraphic( icon( "icons16x16/load.png" ) );
        buttonLoad.setOnAction( this::onActionLoad );
        buttonLoad.disableProperty().bind( disableLoad );
        
        Button buttonNew = new Button();
        buttonNew.setTooltip( new Tooltip( LOGGER.text( "archive.action.new" ) ) );
        buttonNew.setGraphic( icon( "icons16x16/new-library.png" ) );
        buttonNew.setOnAction( this::onActionNew );
        buttonNew.disableProperty().bind( disableNew );
        
        Button buttonPreview = new Button();
        buttonPreview.setTooltip( new Tooltip( LOGGER.text( "archive.action.preview" ) ) );
        buttonPreview.setGraphic( icon( "icons16x16/preview.png" ) );
        buttonPreview.setOnAction( this::onActionPreview );
        buttonPreview.disableProperty().bind( disablePreview );
        
        Button buttonEdit = new Button();
        buttonEdit.setTooltip( new Tooltip( LOGGER.text( "archive.action.edit" ) ) );
        buttonEdit.setGraphic( icon( "icons16x16/edit.png" ) );
        buttonEdit.setOnAction( this::onActionEdit );
        buttonEdit.disableProperty().bind( disableEdit );
        
        Button buttonMultiply = new Button();
        buttonMultiply.setTooltip( new Tooltip( LOGGER.text( "archive.action.multiply" ) ) );
        buttonMultiply.setGraphic( icon( "icons16x16/multiply.png" ) );
        buttonMultiply.setOnAction( this::onActionMultiply );
        buttonMultiply.disableProperty().bind( disableMultiply );
        
        Button buttonRemove = new Button();
        buttonRemove.setTooltip( new Tooltip( LOGGER.text( "archive.action.remove" ) ) );
        buttonRemove.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemove.setOnAction( this::onActionRemove );
        buttonRemove.disableProperty().bind( disableRemove );
        
        Button buttonImportFile = new Button();
        buttonImportFile.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.file" ) ) );
        buttonImportFile.setGraphic( icon( "icons16x16/file-xml.png" ) );
        buttonImportFile.setOnAction( this::onActionImportFile );
        buttonImportFile.disableProperty().bind( disableImportFile );
        
        Button buttonImportNet = new Button();
        buttonImportNet.setTooltip( new Tooltip( LOGGER.text( "archive.action.import.network" ) ) );
        buttonImportNet.setGraphic( icon( "icons16x16/load-internet.png" ) );
        buttonImportNet.setOnAction( this::onActionImportNet );
        buttonImportNet.disableProperty().bind( disableImportNet );
        
        Button buttonExportXml = new Button();
        buttonExportXml.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.xml" ) ) );
        buttonExportXml.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportXml.setOnAction( this::onActionExportXml );
        buttonExportXml.disableProperty().bind( disableExportXml );
        
        Button buttonExportPic = new Button();
        buttonExportPic.setTooltip( new Tooltip( LOGGER.text( "archive.action.export.pic" ) ) );
        buttonExportPic.setGraphic( icon( "icons16x16/file-export.png" ) );
        buttonExportPic.setOnAction( this::onActionExportPic );
        buttonExportPic.disableProperty().bind( disableExportPic );
        
        Button buttonProperties = new Button();
        buttonProperties.setTooltip( new Tooltip( LOGGER.text( "archive.action.properties" ) ) );
        buttonProperties.setGraphic( icon( "icons16x16/properties.png" ) );
        buttonProperties.setOnAction( this::onActionProperties );
        buttonProperties.disableProperty().bind( disableProperties );
        
        ToolBar toolbar = new ToolBar();
        toolbar.setOrientation( Orientation.VERTICAL );
        toolbar.getItems().addAll
        (
                buttonOpen,
                buttonClose,
                buttonImportFile,
                buttonImportNet,
                buttonExportXml,
                buttonExportPic,
                new Separator( Orientation.HORIZONTAL ),
                buttonLoad,
                new Separator( Orientation.HORIZONTAL ),
                buttonNew,
                buttonRemove,
                buttonEdit,
                buttonMultiply,
                new Separator( Orientation.HORIZONTAL ),
                buttonPreview,
                buttonProperties
        );
        toolbar.getStyleClass().add( CSS_CLASS );
        toolbar.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return toolbar;
    }

    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    private void onActionOpen( ActionEvent event )
    {
        processor.onActionArchiveOpen( event );
        event.consume();
    }
    
    @FXML
    private void onActionClose( ActionEvent event )
    {
        processor.onActionArchiveClose( event );
        event.consume();
    }
    
    @FXML
    private void onActionNew( ActionEvent event )
    {
//        processor.onActionNew( event );
        event.consume();
    }
    
    @FXML
    private void onActionLoad( ActionEvent event )
    {
        processor.onActionLoad( event );
        event.consume();
    }
    
    @FXML
    private void onActionPreview( ActionEvent event )
    {
        processor.onActionPreview( event );
        event.consume();
    }
    
    @FXML
    private void onActionEdit( ActionEvent event )
    {
        processor.onActionEdit( event );
        event.consume();
    }
    
    @FXML
    private void onActionMultiply( ActionEvent event )
    {
        processor.onActionMultiply( event );
        event.consume();
    }
    
    @FXML
    private void onActionRemove( ActionEvent event )
    {
        processor.onActionRemove( event );
        event.consume();
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
    
    @FXML
    private void onActionExportXml( ActionEvent event )
    {
        processor.onActionExportXml( event );
        event.consume();
    }
    
    @FXML
    private void onActionExportPic( ActionEvent event )
    {
        processor.onActionExportPic( event );
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent event )
    {
        processor.onActionProperties( event );
        event.consume();
    }

    public BooleanProperty disableOpenProperty() { return disableOpen; }
    public BooleanProperty disableCloseProperty() { return disableClose; }
    public BooleanProperty disableNewProperty() { return disableNew; }
    public BooleanProperty disableLoadProperty() { return disableLoad; }
    public BooleanProperty disablePreviewProperty() { return disablePreview; }
    public BooleanProperty disableEditProperty() { return disableEdit; }
    public BooleanProperty disableMultiplyProperty() { return disableMultiply; }
    public BooleanProperty disableRemoveProperty() { return disableRemove; }
    public BooleanProperty disablePropertiesProperty() { return disableProperties; }
    public BooleanProperty disableImportFileProperty() { return disableImportFile; }
    public BooleanProperty disableImportNetProperty() { return disableImportNet; }
    public BooleanProperty disableExportXmlProperty() { return disableExportXml; }
    public BooleanProperty disableExportPicProperty() { return disableExportPic; }
    
    public boolean getDisableOpen() { return disableOpen.get(); }
    public boolean getDisableClose() { return disableClose.get(); }
    public boolean getDisableNew() { return disableNew.get(); }
    public boolean getDisableLoad() { return disableLoad.get(); }
    public boolean getDisablePreview() { return disablePreview.get(); }
    public boolean getDisableEdit() { return disableEdit.get(); }
    public boolean getDisableMultiply() { return disableMultiply.get(); }
    public boolean getDisableRemove() { return disableRemove.get(); }
    public boolean getDisableProperties() { return disableProperties.get(); }
    public boolean getDisableImportFile() { return disableImportFile.get(); }
    public boolean getDisableImportNet() { return disableImportNet.get(); }
    public boolean getDisableExportXml() { return disableExportXml.get(); }
    public boolean getDisableExportPic() { return disableExportPic.get(); }

    void setProcessor( ActionProcessor processor )
    {
        this.processor = processor; // helps for onActionXxx()
        
        disableOpen.bind( processor.disableArchiveOpenProperty() );
        disableClose.bind( processor.disableArchiveCloseProperty() );
//        disableNew.bind( processor.disableNewProperty() );
        disableLoad.bind( processor.disableLoadProperty() );
        disablePreview.bind( processor.disablePreviewProperty() );
        disableEdit.bind( processor.disableEditProperty() );
        disableMultiply.bind( processor.disableMultiplyProperty() );
        disableRemove.bind( processor.disableRemoveProperty() );
        disableProperties.bind( processor.disablePropertiesProperty() );
        disableImportFile.bind( processor.disableImportProperty() );
        disableImportNet.bind( processor.disableImportProperty() );
        disableExportXml.bind( processor.disableExportXmlProperty() );
        disableExportPic.bind( processor.disableExportPicProperty() );
        
//        disableProperties.addListener( new ChangeListener<Boolean>()
//        {
//
//            @Override
//            public void changed( ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue )
//            {
//                if(true)return;
//            }
//        } );
    }

}
