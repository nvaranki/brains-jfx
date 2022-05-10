package com.varankin.brains.jfx.archive.popup;

import com.varankin.brains.jfx.archive.action.ActionProcessor;
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
 * @author &copy; 2019 Николай Варанкин
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
    private final BooleanProperty disableMultiply;
    private final BooleanProperty disableRemove;
    private final BooleanProperty disableRestore;
    private final BooleanProperty disableClose;
    private final BooleanProperty disableProperties;
    private final BooleanProperty disableImport;
    private final BooleanProperty disableExportXml;
    private final BooleanProperty disableExportPic;
    
    private ActionProcessor processor;
    
    @FXML ArchivePopupNewController menuNewController;
    @FXML MenuImportController menuImportController;

    public ArchivePopupController()
    {
        disableNew = new SimpleBooleanProperty( this, "disableNew" );
        disableLoad = new SimpleBooleanProperty( this, "disableLoad" );
        disablePreview = new SimpleBooleanProperty( this, "disablePreview" );
        disableEdit = new SimpleBooleanProperty( this, "disableEdit" );
        disableMultiply = new SimpleBooleanProperty( this, "disableMultiply" );
        disableRemove = new SimpleBooleanProperty( this, "disableRemove" );
        disableRestore = new SimpleBooleanProperty( this, "disableRestore" );
        disableClose = new SimpleBooleanProperty( this, "disableClose" );
        disableProperties = new SimpleBooleanProperty( this, "disableProperties" );
        disableImport = new SimpleBooleanProperty( this, "disableImport" );
        disableExportXml = new SimpleBooleanProperty( this, "disableExportXml" );
        disableExportPic = new SimpleBooleanProperty( this, "disableExportPic" );
    }
    
    /**
     * Создает меню. 
     * Применяется в конфигурации без FXML.
     * 
     * @return меню. 
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
        
        MenuItem menuMultiply = new MenuItem(
                LOGGER.text( "archive.action.multiply" ), icon( "icons16x16/multiply.png" ) );
        menuMultiply.setOnAction( this::onActionMultiply );
        menuMultiply.disableProperty().bind( disableMultiply );
        
        MenuItem menuRemove = new MenuItem(
                LOGGER.text( "archive.action.remove" ), icon( "icons16x16/remove.png" ) );
        menuRemove.setOnAction( this::onActionRemove );
        menuRemove.disableProperty().bind( disableRemove );

        MenuItem menuRestore = new MenuItem(
                LOGGER.text( "archive.action.restore" ), icon( "icons16x16/restore.png" ) );
        menuRestore.setOnAction( this::onActionRestore );
        menuRestore.disableProperty().bind( disableRestore );

        menuNewController = new ArchivePopupNewController();
        Menu menuNew = menuNewController.build();
        
        MenuItem menuClose = new MenuItem(
                LOGGER.text( "archive.action.close" ), icon( "icons16x16/close.png" ) );
        menuClose.setOnAction( this::onActionClose );
        menuClose.disableProperty().bind( disableClose );

        menuImportController = new MenuImportController();
        Menu menuImport = menuImportController.build();
        menuImport.disableProperty().bind( disableImport );
        
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
                menuMultiply,
                menuRemove,
                menuRestore,
                menuNew,
                menuClose,
                menuProperties,
                new SeparatorMenuItem(),
                menuImport,
                menuExportXml,
                menuExportPic
        );
        return menu;
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
    private void onActionRestore( ActionEvent event )
    {
        processor.onActionRestore( event );
        event.consume();
    }
    
    @FXML
    private void onActionClose( ActionEvent event )
    {
        processor.onActionClose( event );
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
    public BooleanProperty disableRestoreProperty() { return disableRestore; }
    public BooleanProperty disablePropertiesProperty() { return disableProperties; }
    public BooleanProperty disableImportProperty() { return disableImport; }
    public BooleanProperty disableExportXmlProperty() { return disableExportXml; }
    public BooleanProperty disableExportPicProperty() { return disableExportPic; }
    
    public boolean getDisableNew() { return disableNew.get(); }
    public boolean getDisableLoad() { return disableLoad.get(); }
    public boolean getDisablePreview() { return disablePreview.get(); }
    public boolean getDisableEdit() { return disableEdit.get(); }
    public boolean getDisableRemove() { return disableRemove.get(); }
    public boolean getDisableRestore() { return disableRestore.get(); }
    public boolean getDisableProperties() { return disableProperties.get(); }
    public boolean getDisableImport() { return disableImport.get(); }
    public boolean getDisableExportXml() { return disableExportXml.get(); }
    public boolean getDisableExportPic() { return disableExportPic.get(); }

    public void setProcessor( ActionProcessor processor )
    {
        this.processor = processor; // helps for onActionXxx()
        menuNewController.setProcessor( processor );
        menuImportController.setProcessor( processor );
        
        if( processor == null ) return;
        disableLoad.bind( processor.disableLoadProperty() );
        disablePreview.bind( processor.disablePreviewProperty() );
        disableEdit.bind( processor.disableEditProperty() );
        disableMultiply.bind( processor.disableMultiplyProperty() );
        disableRemove.bind( processor.disableRemoveProperty() );
        disableRestore.bind( processor.disableRestoreProperty() );
        disableClose.bind( processor.disableCloseProperty() );
        disableProperties.bind( processor.disablePropertiesProperty() );
        disableImport.bind( processor.disableImportProperty() );
        disableExportXml.bind( processor.disableExportXmlProperty() );
        disableExportPic.bind( processor.disableExportPicProperty() );
    }

}
