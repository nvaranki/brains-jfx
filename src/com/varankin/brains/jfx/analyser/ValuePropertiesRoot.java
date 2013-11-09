package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

/**
 * Панель диалога для выбора и установки параметров прорисовки отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ValuePropertiesRoot extends BorderPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesRoot.class );
    private static final String RESOURCE_CSS  = "/fxml/analyzer/ValuePropertiesRoot.css";
    private static final String CSS_CLASS = "value-properties-root";

    ValuePropertiesRoot( final ValuePropertiesRootController controller )
    {
        ValuePropertiesPaneController propertiesController = new ValuePropertiesPaneController();
        
        ValuePropertiesPane properties = new ValuePropertiesPane( propertiesController );
        properties.setId( "properties" );

        Button buttonOK = new Button( LOGGER.text( "button.ok" ) );
        buttonOK.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                controller.onActionOK( event );
            }
        } );
        buttonOK.setDefaultButton( true );

        Button buttonApply = new Button( LOGGER.text( "button.apply" ) );
        buttonApply.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                controller.onActionApply( event );
            }
        } );
        buttonApply.setId( "buttonApply" );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                controller.onActionCancel( event );
            }
        } );
        buttonCancel.setCancelButton( true );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonApply, buttonCancel );

        setCenter( properties );
        setBottom( buttonBar );

        getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        getStyleClass().add( CSS_CLASS );
        
        controller.propertiesController = propertiesController;
        controller.properties = properties;
        controller.buttonApply = buttonApply;
        controller.initialize();
    }       

}
