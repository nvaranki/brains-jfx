package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.jfx.db.*;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.util.Builder;

import com.varankin.brains.jfx.archive.ArchiveResourceFactory;

/**
 * FXML-контроллер диалога свойств элемента архива.
 * 
 * @author &copy; 2020 Николай Варанкин
 */
public class PropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Properties.css";
    private static final String CSS_CLASS = "properties";
    
    public static final String RESOURCE_FXML = "/fxml/archive/Properties.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ReadOnlyStringWrapper titleProperty;
    
    @FXML private TabPane pane;
    @FXML private Button buttonOK, buttonApply;

    public PropertiesController()
    {
        titleProperty = new ReadOnlyStringWrapper();
    }
    
    /**
     * Создает панель диалога свойств элемента архива.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель диалога.
     */
    @Override
    public TabPane build()
    {
        buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setId( "buttonOK" );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );

        buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setId( "buttonApply" );
        buttonApply.setOnAction( this::onActionApply );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel, buttonApply );

        pane = new TabPane();
        pane.setId( "pane" );
        pane.setTabClosingPolicy( TabPane.TabClosingPolicy.UNAVAILABLE );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        EventHandler<ActionEvent> handler = buttonApply.getOnAction();
        if( handler != null ) handler.handle( event );
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        event.consume();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonApply.getScene().getWindow().hide();
        event.consume();
    }

    public ReadOnlyStringProperty titleProperty()
    {
        return titleProperty.getReadOnlyProperty();
    }
    
    public void reset( FxАтрибутный<?> value )
    {
        if( value != null )
        {
            pane.getTabs().setAll( new PropertiesTabFactory().collectTabList( value ) );
            String тип = ArchiveResourceFactory.метка( value.тип().getValue() );
            Property<String> название = ArchiveResourceFactory.название( value, "" );
            titleProperty.bind( Bindings.createStringBinding( () ->
            {
                return String.format(
                    RESOURCE_BUNDLE.getString( "properties.title" ),
                    тип, название.getValue() );
            }, 
                    название ) );
        }
        else
        {
            pane.getTabs().forEach( t -> t.getOnCloseRequest().handle( null ) );
            titleProperty.unbind();
            titleProperty.setValue( null );
        }
    }
    
}
