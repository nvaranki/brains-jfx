package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.db.*;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.collections.WeakMapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер диалога свойств элемента архива.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class PropertiesController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Properties.css";
    private static final String CSS_CLASS = "properties";
    
    static final String RESOURCE_FXML = "/fxml/archive/Properties.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ReadOnlyStringWrapper titleProperty;
    
    private BooleanBinding changedBinding;
    
    @FXML private BorderPane pane;
    @FXML private Pane properties;
    @FXML private Button buttonOK, buttonApply;
    //@FXML private PropertiesPaneController propertiesController;

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
    public BorderPane build()
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

        pane = new BorderPane();
        pane.setId( "pane" );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }
    private PaneChanger paneChanger;

    @FXML
    protected void initialize()
    {
        paneChanger = new PaneChanger( pane );
        pane.getProperties().addListener( new WeakMapChangeListener<>( paneChanger ) );
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

    ReadOnlyStringProperty titleProperty()
    {
        return titleProperty.getReadOnlyProperty();
    }

    void reset(  )
    {
        // сбросить прежние значения и установить текущие значения
        //titleProperty.setValue( "Properties ?" ); //LOGGER.text( "properties.title", legend.getText() )
    }
    
    private class PaneChanger implements MapChangeListener<Object,Object>
    {
        final BorderPane pane;

        PaneChanger( BorderPane pane )
        {
            this.pane = pane;
        }
        
        @Override
        public void onChanged( MapChangeListener.Change<? extends Object, ? extends Object> change )
        {
            if( change.wasAdded() )
            {
                Object value = change.getValueAdded();
                if( value instanceof FxNameSpace )
                {
                    PropertiesXmlNameSpaceController controller = new PropertiesXmlNameSpaceController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.namespace" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxNameSpace)value).getSource() );
                }
                else if( value instanceof FxПроцессор )
                {
                    PropertiesProcessorController controller = new PropertiesProcessorController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.processor" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxПроцессор)value).getSource() );
                }
                else if( value instanceof FxКлассJava )
                {
                    PropertiesClassJavaController controller = new PropertiesClassJavaController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.class.java" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxКлассJava)value).getSource() );
                }
                else if( value instanceof FxКонвертер )
                {
                    PropertiesConverterController controller = new PropertiesConverterController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.converter" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxКонвертер)value).getSource() );
                }
//                else if( value instanceof DbСкаляр )
//                {
//                    PropertiesScalarController controller = new PropertiesScalarController();
//                    pane.setCenter( controller.build() );
//                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.scalar" ) ) );
//                    buttonApply.setOnAction( controller::onActionApply );
//                    controller.reset( (DbСкаляр)value );
//                }
//                else if( value instanceof DbМассив )
//                {
//                    PropertiesArrayController controller = new PropertiesArrayController();
//                    pane.setCenter( controller.build() );
//                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.array" ) ) );
//                    buttonApply.setOnAction( controller::onActionApply );
//                    controller.reset( (DbМассив)value );
//                }
                else if( value instanceof FxТекстовыйБлок )
                {
                    TextController controller = new TextController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.text" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxТекстовыйБлок)value).getSource() );
                }
                else if( value instanceof FxЭлемент )
                {
                    PropertiesElementController controller = new PropertiesElementController();
                    pane.setCenter( controller.build() );
                    titleProperty.setValue( LOGGER.text( "properties.title", LOGGER.text( "cell.element" ) ) );
                    buttonApply.setOnAction( controller::onActionApply );
                    controller.reset( ((FxЭлемент<? extends DbЭлемент>)value).getSource() );
                }
                else
                {
                    pane.setCenter( null );
                    titleProperty.setValue( LOGGER.text( "properties.title", "" ) );
                    buttonApply.setOnAction( null );
                }
            }
        }
    }
    
}
