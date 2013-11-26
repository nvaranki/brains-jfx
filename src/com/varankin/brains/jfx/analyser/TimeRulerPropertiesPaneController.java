package com.varankin.brains.jfx.analyser;

import com.varankin.util.LoggerX;
import java.util.concurrent.TimeUnit;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров оси времени.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public class TimeRulerPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeRulerPropertiesPane.css";
    private static final String CSS_CLASS = "time-ruler-properties-pane";

    @FXML private TextField duration, excess;
    @FXML private ComboBox<TimeUnit> unit;
    @FXML private ColorPicker textColor, tickColor;
    @FXML private Control textFont;
    
    public TimeRulerPropertiesPaneController()
    {
    }
    
    /**
     * Создает панель выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        duration = new TextField();
        duration.setId( "duration" );
        duration.setPrefColumnCount( 6 );
        duration.setFocusTraversable( true );
        
        excess = new TextField();
        excess.setId( "valueMax" );
        excess.setPrefColumnCount( 6 );
        excess.setFocusTraversable( true );
        
        unit = new ComboBox<>();
        unit.setId( "unit" );
        
        textColor = new ColorPicker();
        textColor.setId( "textColor" );
        textColor.setFocusTraversable( false ); //TODO true RT-21549
        
        textFont = new TextField("Helvetica"); //TODO
        textFont.setId( "textFont" );
        textFont.setFocusTraversable( true );
        
        tickColor = new ColorPicker();
        tickColor.setId( "tickColor" );
        tickColor.setFocusTraversable( false ); //TODO true RT-21549
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.ruler.time.duration" ) ), 0, 0 );
        pane.add( duration, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.time.excess" ) ), 0, 1 );
        pane.add( excess, 1, 1 );
        pane.add( unit, 3, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.color" ) ), 0, 2 );
        pane.add( textColor, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.font" ) ), 2, 2 );
        pane.add( textFont, 3, 2 );
        pane.add( new Label( LOGGER.text( "properties.ruler.tick.color" ) ), 0, 3 );
        pane.add( tickColor, 1, 3 );
        
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
