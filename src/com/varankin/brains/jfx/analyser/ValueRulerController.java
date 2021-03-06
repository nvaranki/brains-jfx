package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.List;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import static com.varankin.brains.jfx.Utilities.*;

/**
 * FXML-контроллер панели шкалы по оси значений.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class ValueRulerController extends AbstractRulerController
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValueRulerController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueRuler.css";
    private static final String CSS_CLASS = "value-ruler";

    private final Property<Float> valueMinProperty, valueMaxProperty;
    private final Property<ValueConvertor> convertorProperty;
    private final ChangeListener<Float> boundChangeListener;
    private final ChangeListener<Number> sizeChangeListener;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 5; // min 5 pixels in between ticks
    private final double tickShift = 35d;
    
    private ValueRulerPropertiesStage properties;
    
    @FXML private Pane pane;
    @FXML private ContextMenu popup;

    public ValueRulerController()
    {
        convertorProperty = new SimpleObjectProperty<>();
        valueMinProperty = new SimpleObjectProperty<>( Float.NaN );
        valueMaxProperty = new SimpleObjectProperty<>( Float.NaN );
        boundChangeListener = new BoundChangeListener<>();
        valueMaxProperty.addListener( new WeakChangeListener<>( boundChangeListener ) );
        valueMinProperty.addListener( new WeakChangeListener<>( boundChangeListener ) );
        sizeChangeListener = new SizeChangeListener();
    }
    
    /**
     * Создает панель шкалы по оси времени.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель.
     */
    @Override
    public Pane build()
    {
        MenuItem menuItemProperties = new MenuItem( LOGGER.text( "control.popup.properties" ) );
        menuItemProperties.setGraphic( JavaFX.icon( "icons16x16/properties.png" ) );
        menuItemProperties.setOnAction( this::onActionProperties );
        
        popup = new ContextMenu();
        popup.getItems().addAll( menuItemProperties );
        
        pane = new Pane();
        pane.setOnContextMenuRequested( this::onContextMenuRequested );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        pane.heightProperty().addListener( new WeakChangeListener<>( sizeChangeListener ) );
    }
    
    @FXML
    private void onContextMenuRequested( ContextMenuEvent event )
    {
        popup.show( pane, event.getScreenX(), event.getScreenY() );
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent e )
    {
        if( properties == null )
        {
            properties = new ValueRulerPropertiesStage( this::displayProperties, this::applyProperties );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.initStyle( StageStyle.DECORATED );
            properties.initModality( Modality.NONE );
            properties.setTitle( LOGGER.text( "properties.ruler.value.title", 0 ) );
        }
        properties.show();
        properties.toFront();
    }

    Property<ValueConvertor> convertorProperty()
    {
        return convertorProperty;
    }

    void extendPopupMenu( List<? extends MenuItem> menu )
    {
        JavaFX.copyMenuItems( menu, popup.getItems(), true );
    }
        
    private void displayProperties( ValueRulerPropertiesController controller )
    {
        ValueRulerPropertiesPaneController pc = controller.propertiesController();
        // скопировать в форму текущие значения
        copy( valueMaxProperty, pc.valueMaxProperty() );
        copy( valueMinProperty, pc.valueMinProperty() );
        copy( tickColorProperty(), pc.tickColorProperty() );
        copy( textColorProperty(), pc.textColorProperty() );
        copy( fontProperty(), pc.textFontProperty() );
        pc.resetColorPicker();
        controller.setModified( false );
    }

    private void applyProperties( ValueRulerPropertiesController controller )
    {
        ValueRulerPropertiesPaneController pc = controller.propertiesController();
        // установить текущие значения, если они отличаются
        applyProperties( pc );
        controller.setModified( false );
    }
    
    void applyProperties( ValueRulerPropertiesPaneController pc )
    {
        applyDistinct( pc.valueMaxProperty(), valueMaxProperty );
        applyDistinct( pc.valueMinProperty(), valueMinProperty );
        applyDistinct( pc.tickColorProperty(), tickColorProperty() );
        applyDistinct( pc.textColorProperty(), textColorProperty() );
        applyDistinct( pc.textFontProperty(), fontProperty() );
    }
    
    @Override
    protected void reset()
    {
        ValueConvertor convertor = new ValueConvertor( 
                valueMinProperty.getValue(), valueMaxProperty.getValue(),
                pane.heightProperty().intValue() );
        convertorProperty.setValue( convertor );
        // remove ruler
        pane.getChildren().clear();
        // generate ruler
        float size = valueMaxProperty.getValue() - valueMinProperty.getValue();
        float step = (float)roundToFactor( size / ( pane.getHeight() / pixelStepMin ), factor );
        if( step > 0F )
        {
            float start = valueMinProperty.getValue();
            float offset = start % step;
            if( offset < 0 ) offset += step; // float!!! step = 0.1 => offset = -0.099999999
            start -= offset;
            int stepCount = (int)Math.ceil( size / step );
            generateAllTickAndText( start, step, stepCount, convertor );
        }
    }
    
    private void generateAllTickAndText( float start, float step, int count, ValueConvertor convertor )
    {
        for( int i = 0; i <= count; i++ )
        {
            float v = start + step * i;
            int y = convertor.valueToImage( v );
            long f = Math.round( (double)v / step );
            if( 0 <= y && y < pane.getHeight() )
                generateTickAndText( y, Float.toString( f * step ), f );
        }
    }

    private void generateTickAndText( int y, String text, long s )
    {
        int length = s % 10 == 0 ? getTickSizeLarge() : 
                s % 5 == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, length, 0 );
        tick.getTransforms().add( new Translate( getTickSizeLarge(), 0 ) );
        tick.getTransforms().add( new Scale( -1, 1 ) );
        tick.relocate( tickShift, y );
        tick.strokeProperty().bind( tickColorProperty() );
        pane.getChildren().add( tick );
        if( s % 10 == 0 || s % 5 == 0 )
        {
            Text value = new Text( text );
            value.relocate( shiftToRightAlign( value ), y );
            value.fillProperty().bind( textColorProperty() );
            value.fontProperty().bind( fontProperty() );
            if( y + value.boundsInLocalProperty().get().getMaxY() < pane.getHeight() )
                pane.getChildren().add( value );
        }
    }

    private double shiftToRightAlign( Text value )
    {
        Bounds tickBounds = pane.getBoundsInLocal();
        double tickRight = tickBounds.getMaxX();
        Bounds valueBounds = value.getBoundsInLocal();
        double valueRight = valueBounds.getMaxX();
        double blank = valueBounds.getMaxY(); //TODO verify approach
        double shift = tickRight - 10d - valueRight - blank;
        return shift;
    }
    
}
