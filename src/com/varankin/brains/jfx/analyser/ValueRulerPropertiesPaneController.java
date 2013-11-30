package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.*;
import com.varankin.util.LoggerX;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров оси значений.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class ValueRulerPropertiesPaneController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValuePropertiesPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueRulerPropertiesPane.css";
    private static final String CSS_CLASS = "value-ruler-properties-pane";

    private final ObjectProperty<Float> valueMinProperty, valueMaxProperty;
    private final ObjectProperty<Font> textFontProperty;
    
    @FXML private TextField valueMin, valueMax;
    @FXML private ColorPicker textColor, tickColor;
    @FXML private Control textFont;
    
    public ValueRulerPropertiesPaneController()
    {
        valueMinProperty = new SimpleObjectProperty<>();
        valueMaxProperty = new SimpleObjectProperty<>();
        textFontProperty = new SimpleObjectProperty<>();
    }
    
    /**
     * Создает панель выбора и установки параметров оси значений.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        valueMin = new TextField();
        valueMin.setId( "valueMin" );
        valueMin.setPrefColumnCount( 9 );
        valueMin.setFocusTraversable( true );
        
        valueMax = new TextField();
        valueMax.setId( "valueMax" );
        valueMax.setPrefColumnCount( 9 );
        valueMax.setFocusTraversable( true );
        
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
        pane.add( new Label( LOGGER.text( "properties.ruler.value.min" ) ), 0, 0 );
        pane.add( valueMin, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.value.max" ) ), 2, 0 );
        pane.add( valueMax, 3, 0 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.color" ) ), 0, 1 );
        pane.add( textColor, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.ruler.text.font" ) ), 2, 1 );
        pane.add( textFont, 3, 1 );
        pane.add( new Label( LOGGER.text( "properties.ruler.tick.color" ) ), 0, 2 );
        pane.add( tickColor, 1, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
        Bindings.bindBidirectional( valueMin.textProperty(), valueMinProperty, 
                new RelativeFloatConverter( valueMin, valueMaxProperty, true ) );
        Bindings.bindBidirectional( valueMax.textProperty(), valueMaxProperty, 
                new RelativeFloatConverter( valueMax, valueMinProperty, false ) );
    }
    
    Property<Float> valueMinProperty()
    {
        return valueMinProperty;
    }

    Property<Float> valueMaxProperty()
    {
        return valueMaxProperty;
    }

    Property<Color> textColorProperty()
    {
        return textColor.valueProperty();
    }
    
    Property<Font> textFontProperty()
    {
        return textFontProperty;
    }

    Property<Color> tickColorProperty()
    {
        return tickColor.valueProperty();
    }

    /**
     * @deprecated RT-34098
     */
    void resetColorPicker()
    {
    }

    private static class RelativeFloatConverter extends FloatConverter
    {
        final Property<Float> counterpart;
        final boolean less;

        RelativeFloatConverter( Node node, Property<Float> counterpart, boolean less )
        {
            super( node );
            this.counterpart = counterpart;
            this.less = less;
        }

        @Override
        public Float fromString( String string )
        {
            Float value = super.fromString( string );
            Float counterValue = counterpart.getValue();
            if( value != null && counterValue != null 
                    && ( less ? value > counterValue : value < counterValue ) )
            {
                if( less )
                    LOGGER.log( "001003004W", value, counterValue );
                else
                    LOGGER.log( "001003004W", counterValue, value );
                highlight( value = null );
            }
            return value;
        }
    }
    
}
