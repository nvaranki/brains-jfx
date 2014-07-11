package com.varankin.brains.jfx.archive;

import com.varankin.util.LoggerX;
//import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 * FXML-контроллер контекстного меню навигатора по архиву. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ArchivePopupController implements Builder<ContextMenu>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ArchivePopupController.class );
    //private static final String RESOURCE_CSS  = "/fxml/archive/ArchivePopup.css";
    //private static final String CSS_CLASS = "archive-popup";
    
    //public static final String RESOURCE_FXML  = "/fxml/archive/ArchivePopup.fxml";
    //public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final BooleanProperty disableNew;
    private final BooleanProperty disableLoad;
    private final BooleanProperty disablePreview;
    private final BooleanProperty disableEdit;
    private final BooleanProperty disableRemove;
    private final BooleanProperty disableProperties;
    private final BooleanProperty disableImportFile;
    private final BooleanProperty disableImportNet;
    private final BooleanProperty disableExportXml;
    private final BooleanProperty disableExportPic;
    
    private ActionProcessor processor;
    
    @FXML ArchivePopupNewController menuNewController;

    public ArchivePopupController()
    {
        disableNew = new SimpleBooleanProperty( this, "disableNew" );
        disableLoad = new SimpleBooleanProperty( this, "disableLoad" );
        disablePreview = new SimpleBooleanProperty( this, "disablePreview" );
        disableEdit = new SimpleBooleanProperty( this, "disableEdit" );
        disableRemove = new SimpleBooleanProperty( this, "disableRemove" );
        disableProperties = new SimpleBooleanProperty( this, "disableProperties" );
        disableImportFile = new SimpleBooleanProperty( this, "disableImportFile" );
        disableImportNet = new SimpleBooleanProperty( this, "disableImportNet" );
        disableExportXml = new SimpleBooleanProperty( this, "disableExportXml" );
        disableExportPic = new SimpleBooleanProperty( this, "disableExportPic" );
    }
    
    /**
     * Создает панель навигатора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public ContextMenu build()
    {
        MenuItem menuLoad = new MenuItem(
                LOGGER.text( "archive.action.load" ), icon( "icons16x16/load.png" ) );
        menuLoad.setOnAction( this::onActionLoad );
        menuLoad.disableProperty().bind( disableLoad );
        
        MenuItem menuPreview = new MenuItem(
                LOGGER.text( "archive.action.preview" ), icon( "icons16x16/preview.png" ) );
        menuPreview.setOnAction( this::onActionPreview );
        menuPreview.disableProperty().bind( disablePreview );
        
        MenuItem menuEdit = new MenuItem(
                LOGGER.text( "archive.action.edit" ), icon( "icons16x16/edit.png" ) );
        menuEdit.setOnAction( this::onActionEdit );
        menuEdit.disableProperty().bind( disableEdit );
        
        MenuItem menuRemove = new MenuItem(
                LOGGER.text( "archive.action.remove" ), icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemove );

        menuNewController = new ArchivePopupNewController();
        Menu menuNew = menuNewController.build();
        
        MenuItem menuImportFile = new MenuItem(
                LOGGER.text( "archive.action.import.file" ), icon( "icons16x16/file-xml.png" ) );
        menuImportFile.setOnAction( this::onActionImportFile );
        menuImportFile.disableProperty().bind( disableImportFile );
        
        MenuItem menuImportNet = new MenuItem(
                LOGGER.text( "archive.action.import.network" ), icon( "icons16x16/load-internet.png" ) );
        menuImportNet.setOnAction( this::onActionImportNet );
        menuImportNet.disableProperty().bind( disableImportNet );
        
        MenuItem menuExportXml = new MenuItem(
                LOGGER.text( "archive.action.export.xml" ), icon( "icons16x16/file-export.png" ) );
        menuExportXml.setOnAction( this::onActionExportXml );
        menuExportXml.disableProperty().bind( disableExportXml );
        
        MenuItem menuExportPic = new MenuItem(
                LOGGER.text( "archive.action.export.pic" ), icon( "icons16x16/file-export.png" ) );
        menuExportPic.setOnAction( this::onActionExportPic );
        menuExportPic.disableProperty().bind( disableExportPic );
        
        MenuItem menuProperties = new MenuItem(
                LOGGER.text( "archive.action.properties" ), icon( "icons16x16/properties.png" ) );
        menuProperties.setOnAction( this::onActionProperties );
        menuProperties.disableProperty().bind( disableProperties );
        
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll
        (
                menuLoad,
                new SeparatorMenuItem(),
                menuPreview,
                menuEdit,
                menuRemove,
                menuNew,
                menuProperties,
                new SeparatorMenuItem(),
                menuImportFile,
                menuImportNet,
                menuExportXml,
                menuExportPic
        );
        return menu;
    }
    
    @FXML
    private void onActionNew( ActionEvent event )
    {
        processor.onActionNew( event );
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
    private void onActionRemove( ActionEvent event )
    {
        processor.onActionRemove( event );
        event.consume();
    }
    
    @FXML
    private void onActionImportFile( ActionEvent event )
    {
        processor.onActionImportFile( event );
        event.consume();
    }
    
    @FXML
    private void onActionImportNet( ActionEvent event )
    {
        processor.onActionImportNet( event );
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

    public BooleanProperty disableNewProperty() { return disableNew; }
    public BooleanProperty disableLoadProperty() { return disableLoad; }
    public BooleanProperty disablePreviewProperty() { return disablePreview; }
    public BooleanProperty disableEditProperty() { return disableEdit; }
    public BooleanProperty disableRemoveProperty() { return disableRemove; }
    public BooleanProperty disablePropertiesProperty() { return disableProperties; }
    public BooleanProperty disableImportFileProperty() { return disableImportFile; }
    public BooleanProperty disableImportNetProperty() { return disableImportNet; }
    public BooleanProperty disableExportXmlProperty() { return disableExportXml; }
    public BooleanProperty disableExportPicProperty() { return disableExportPic; }
    
    public boolean getDisableNew() { return disableNew.get(); }
    public boolean getDisableLoad() { return disableLoad.get(); }
    public boolean getDisablePreview() { return disablePreview.get(); }
    public boolean getDisableEdit() { return disableEdit.get(); }
    public boolean getDisableRemove() { return disableRemove.get(); }
    public boolean getDisableProperties() { return disableProperties.get(); }
    public boolean getDisableImportFile() { return disableImportFile.get(); }
    public boolean getDisableImportNet() { return disableImportNet.get(); }
    public boolean getDisableExportXml() { return disableExportXml.get(); }
    public boolean getDisableExportPic() { return disableExportPic.get(); }

    void setProcessor( ActionProcessor processor )
    {
        this.processor = processor; // helps for onActionXxx()
        
        disableNew.bind( processor.disableNewProperty() );
        disableLoad.bind( processor.disableLoadProperty() );
        disablePreview.bind( processor.disablePreviewProperty() );
        disableEdit.bind( processor.disableEditProperty() );
        disableRemove.bind( processor.disableRemoveProperty() );
        disableProperties.bind( processor.disablePropertiesProperty() );
        disableImportFile.bind( processor.disableImportFileProperty() );
        disableImportNet.bind( processor.disableImportNetProperty() );
        disableExportXml.bind( processor.disableExportXmlProperty() );
        disableExportPic.bind( processor.disableExportPicProperty() );
        
        //menuNewController.selectionProperty().bind( processor.selectionProperty() );
    }

}
