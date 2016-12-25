package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxТочка;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели выбора и установки параметров точки.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabPointController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabPointController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabPoint.css";
    private static final String CSS_CLASS = "properties-tab-point";
    private static final StringToFloatConverter CONVERTER_LONG = new StringToFloatConverter();
    private static final StringToIntegerConverter CONVERTER_INTEGER = new StringToIntegerConverter();

    static final String RESOURCE_FXML = "/fxml/archive/TabPoint.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxТочка точка;
    
    @FXML private TextField index;
    @FXML private TextField threshold;
    @FXML private CheckBox sensor;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        index = new TextField();
        index.setFocusTraversable( true );
        
        threshold = new TextField();
        threshold.setFocusTraversable( true );
        
        sensor = new CheckBox();
        sensor.setFocusTraversable( true );
        
        GridPane pane = new GridPane();
        pane.setId( "point" );
        pane.add( new Label( LOGGER.text( "tab.point.index" ) ), 0, 0 );
        pane.add( index, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.point.threshold" ) ), 0, 1 );
        pane.add( threshold, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.point.sensor" ) ), 0, 2 );
        pane.add( sensor, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    void set( FxТочка точка )
    {
        if( this.точка != null )
        {
            index.textProperty().unbindBidirectional( this.точка.индекс() );
            threshold.textProperty().unbindBidirectional( this.точка.порог() );
            sensor.selectedProperty().unbindBidirectional( this.точка.датчик() );
        }
        if( точка != null )
        {
            index.textProperty().bindBidirectional( точка.индекс(), CONVERTER_INTEGER );
            threshold.textProperty().bindBidirectional( точка.порог(), CONVERTER_LONG );
            sensor.selectedProperty().bindBidirectional( точка.датчик() );
        }
        this.точка = точка;
    }
    
    private static class StringToFloatConverter extends StringConverter<Float>
    {

        @Override
        public String toString( Float object )
        {
            return Float.toString( object );
        }

        @Override
        public Float fromString( String string )
        {
            try
            {
                return Float.valueOf( string );
            }
            catch( NumberFormatException e )
            {
                LOGGER.getLogger().warning( "Not a long number: " + string );
                return 0F; //TODO null; for unboxed classes
            }
        }
        
    }
    
    private static class StringToIntegerConverter extends StringConverter<Integer>
    {

        @Override
        public String toString( Integer object )
        {
            return Integer.toString( object );
        }

        @Override
        public Integer fromString( String string )
        {
            try
            {
                return Integer.valueOf( string );
            }
            catch( NumberFormatException e )
            {
                LOGGER.getLogger().warning( "Not an integer number: " + string );
                return 0; //TODO null; for unboxed classes
            }
        }
        
    }
    
}
