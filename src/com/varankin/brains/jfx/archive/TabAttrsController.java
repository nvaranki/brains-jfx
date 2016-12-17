package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.jfx.db.FxProperty;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.util.LoggerX;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Builder;
import javafx.util.StringConverter;

import static com.varankin.brains.jfx.JavaFX.icon;
import static javafx.beans.binding.Bindings.*;

/**
 * FXML-контроллер панели дополнительных параметров.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class TabAttrsController implements Builder<BorderPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabAttrsController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabAttrs.css";
    private static final String CSS_CLASS = "properties-tab-attrs";
    private static final StringConverter<Object> КОНВЕРТЕР_OBJECT = new ObjectToStringConverter();
    private static final StringConverter<String> КОНВЕРТЕР_STRING = new StringToStringConverter();

    static final String RESOURCE_FXML = "/fxml/archive/TabAttrs.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxАтрибутный<?> атрибутный;
    
    @FXML private TableView<FxProperty> table;
    @FXML private MenuItem delete; 

    /**
     * Создает панель дополнительных параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель дополнительных параметров.
     */
    @Override
    public BorderPane build()
    {
        TableColumn<FxProperty,String> name = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.name" ) );
        name.setEditable( true );
        name.setCellValueFactory( this::nameCellValueFactory );
        name.setCellFactory( this::textCellFactory );
        name.setOnEditCommit( this::onCommitName );

        TableColumn<FxProperty,Object> value = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.value" ) );
        value.setEditable( true );
        value.setCellValueFactory( this::valueCellValueFactory );
        value.setCellFactory( this::valueCellFactory );
        value.setOnEditCommit( this::onCommitValue );

        TableColumn<FxProperty,String> scope = new TableColumn<>( LOGGER.text( "properties.tab.attrs.col.scope" ) );
        scope.setEditable( true );
        scope.setCellValueFactory( this::scopeCellValueFactory );
        scope.setCellFactory( this::textCellFactory );
        scope.setOnEditCommit( this::onCommitScope );
        
        MenuItem create = new MenuItem( LOGGER.text( "properties.tab.attrs.menu.create" ) );
        create.setOnAction( this::onCreateProperty );
        
        delete = new MenuItem( LOGGER.text( "properties.tab.attrs.menu.delete" ), 
                icon( "icons16x16/remove.png" ) );
        delete.setOnAction( this::onDeleteProperty );
        
        table = new TableView<>();
        table.getColumns().addAll( name, value, scope );
        table.setPlaceholder( new Text( LOGGER.text( "properties.tab.attrs.empty" ) ) );
        table.setEditable( true );
        table.setContextMenu( new ContextMenu( create, delete ) );
        
        BorderPane pane = new BorderPane();
        pane.setCenter( table );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        ObservableList<TablePosition> selected = table.getSelectionModel().getSelectedCells();
        delete.disableProperty().bind( Bindings.createBooleanBinding( 
            () -> selected.isEmpty(), selected ) );
    }
    
    @FXML
    private void onCreateProperty( ActionEvent event )
    {
        атрибутный.атрибут( "Новый параметр", "http://server/namespace" ).setValue( "Значение" );
        event.consume();
    }
    
    @FXML
    private void onDeleteProperty( ActionEvent event )
    {
        List<FxProperty> properties = table.getSelectionModel().getSelectedCells().stream()
                .map( tp -> table.getItems().get( tp.getRow() ) )
                .collect( Collectors.toList() );
        properties.forEach( p -> p.setValue( null ) );
        атрибутный.атрибуты().removeAll( properties );
        event.consume();
    }
    
    @FXML
    private ObservableValue<String> nameCellValueFactory( TableColumn.CellDataFeatures<FxProperty, String> cdf )
    {
        return createStringBinding( () -> cdf.getValue().getName(), cdf.getValue() );
    }

    @FXML
    private ObservableValue<Object> valueCellValueFactory( TableColumn.CellDataFeatures<FxProperty, Object> cdf )
    {
        return cdf.getValue();
    }
    
    @FXML
    private ObservableValue<String> scopeCellValueFactory( TableColumn.CellDataFeatures<FxProperty, String> cdf )
    {
        return createStringBinding( () -> cdf.getValue().getScope(), cdf.getValue() );
    }
    
    @FXML
    private TableCell<FxProperty, Object> valueCellFactory( TableColumn<FxProperty, Object> tc )
    {
        return new TextFieldTableCell<>( КОНВЕРТЕР_OBJECT );
    }
    
    @FXML
    private TableCell<FxProperty,String> textCellFactory( TableColumn<FxProperty,String> tc )
    {
        return new TextFieldTableCell<>( КОНВЕРТЕР_STRING );
    }
    
    @FXML
    private void onCommitName( TableColumn.CellEditEvent<FxProperty,String> event )
    {
        FxProperty property = event.getRowValue();
        String oldScope = property.getScope();
        Object oldValue = property.getValue();
        String newName = event.getNewValue();
        ListProperty атрибуты = атрибутный.атрибуты();
        int oldIndex = атрибуты.indexOf( property );
        атрибуты.remove( property );
        property.setValue( null );
        property = атрибутный.атрибут( newName, oldScope );
        property.setValue( oldValue ); 
        int newIndex = атрибуты.indexOf( property );
        if( oldIndex != newIndex )
           атрибуты.add( oldIndex, атрибуты.remove( newIndex ) );
    }
    
    @FXML
    private void onCommitValue( TableColumn.CellEditEvent<FxProperty, Object> event )
    {
        FxProperty property = event.getRowValue();
        Object newValue = event.getNewValue();
        property.setValue( newValue );
    }
    
    @FXML
    private void onCommitScope( TableColumn.CellEditEvent<FxProperty,String> event )
    {
        FxProperty property = event.getRowValue();
        String oldName = property.getName();
        Object oldValue = property.getValue();
        String newScope = event.getNewValue();
        ListProperty атрибуты = атрибутный.атрибуты();
        int oldIndex = атрибуты.indexOf( property );
        атрибуты.remove( property );
        property.setValue( null );
        property = атрибутный.атрибут( oldName, newScope );
        property.setValue( oldValue ); 
        int newIndex = атрибуты.indexOf( property );
        if( oldIndex != newIndex )
           атрибуты.add( oldIndex, атрибуты.remove( newIndex ) );
    }

    void set( FxАтрибутный<?> атрибутный )
    {
        if( this.атрибутный != null )
        {
            table.itemsProperty().unbind();
            table.itemsProperty().getValue().clear();
        }
        if( атрибутный != null )
        {
            table.itemsProperty().bind( атрибутный.атрибуты() );
        }
        this.атрибутный = атрибутный;
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    private static class StringToStringConverter extends StringConverter<String>
    {
        @Override
        public String toString( String object )
        {
            return object;
        }
        
        @Override
        public String fromString( String string )
        {
            return string;
        }
    }
    
    private static class ObjectToStringConverter extends StringConverter<Object>
    {
        @Override
        public String toString( Object object )
        {
            return DbАтрибутный.toStringValue( object );
        }
        
        @Override
        public Object fromString( String string )
        {
            return string == null || string.trim().isEmpty() ? null : string.toCharArray();
        }
    }
    
    //</editor-fold>
}
