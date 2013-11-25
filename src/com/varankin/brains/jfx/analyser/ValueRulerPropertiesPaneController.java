package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров оси значений.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class ValueRulerPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueRulerPropertiesPane.css";
    private static final String CSS_CLASS = "value-ruler-properties-pane";

    public ValueRulerPropertiesPaneController()
    {
    }
    
    /**
     * Создает панель выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        GridPane pane = new GridPane();
        pane.add( new Label( "Not implemented" ), 0, 0 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
    }

    
}
