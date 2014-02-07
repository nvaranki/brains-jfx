package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.PaneWithPopup;
import com.varankin.util.LoggerX;
import java.util.List;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

/**
 * FXML-контроллер панели шкалы по оси значений.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ValueRulerController extends AbstractRulerController
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ValueRulerController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ValueRuler.css";
    private static final String CSS_CLASS = "value-ruler";

    private final Property<Float> valueMinProperty, valueMaxProperty;
    private final SimpleObjectProperty<ValueConvertor> convertorProperty;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 5; // min 5 pixels in between ticks
    private final double tickShift = 35d;
    
    private ValueRulerPropertiesStage properties;
    
    @FXML private Pane pane;
    @FXML private MenuItem menuItemProperties;
    @FXML private ContextMenu popup;

    public ValueRulerController()
    {
        ValueConvertor convertor = new ValueConvertor( -1.0F, +1.0F );
        convertorProperty = new SimpleObjectProperty<>( convertor );
        valueMinProperty = new SimpleObjectProperty<>( convertor.getMin() );
        valueMaxProperty = new SimpleObjectProperty<>( convertor.getMax() );
    }
    
    /**
     * Создает панель шкалы по оси времени.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public Pane build()
    {
        menuItemProperties = new MenuItem( LOGGER.text( "control.popup.properties" ) );
        menuItemProperties.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionProperties( event );
            }
        } );
        
        popup = new ContextMenu();
        popup.getItems().addAll( menuItemProperties );
        
        pane = new PaneWithPopup();
        pane.setOnContextMenuRequested( new EventHandler<ContextMenuEvent>() 
        {
            @Override
            public void handle( ContextMenuEvent event )
            {
                onContextMenuRequested( event );
            }
        } );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        pane.heightProperty().addListener( new SizeChangeListener() );
    }

    @FXML
    private void onContextMenuRequested( ContextMenuEvent event )
    {
        popup.show( pane, event.getScreenX(), event.getScreenY() );
        event.consume();
    }
    
    @FXML
    private void onActionProperties( ActionEvent _ )
    {
        if( properties == null )
        {
            properties = new ValueRulerPropertiesStage();
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.initModality( Modality.NONE );
            properties.setTitle( LOGGER.text( "properties.ruler.value.title", 0 ) );
            ValueRulerPropertiesController controller = properties.getController();
            controller.bindValueMinProperty( valueMinProperty );
            controller.bindValueMaxProperty( valueMaxProperty );
            controller.bindTickColorProperty( tickColorProperty() );
            controller.bindTextColorProperty( textColorProperty() );
            controller.bindTextFontProperty( fontProperty() );
        }
        properties.show();
        properties.toFront();
    }

    ObjectProperty<ValueConvertor> convertorProperty()
    {
        return convertorProperty;
    }
    
    @Override
    protected void reset( int size )
    {
        convertorProperty.get().reset( size );
    }
    
    @Override
    protected void removeRuler()
    {
        pane.getChildren().clear();
    }
    
    @Override
    protected void generateRuler()
    {
        ValueConvertor convertor = convertorProperty.get();
        float size = convertor.getSize();
        float step = (float)roundToFactor( size / ( pane.getHeight() / pixelStepMin ), factor );
        float vStart = convertor.getMin();
        float offset = vStart % step;
        if( offset < 0 ) offset += step; // float!!! step = 0.1 => offset = -0.099999999
        vStart -= offset;
        int stepCount = (int)Math.ceil( size / step );
        for( int i = 0; i <= stepCount; i++ )
        {
            float v = vStart + step * i;
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
    
    @Deprecated
    void appendToPopup( List<MenuItem> items ) 
    {
        if( items != null && !items.isEmpty() )
        {
            popup.getItems().add( new SeparatorMenuItem() );
            JavaFX.copyMenuItems( items, popup.getItems() );
        }
    }

    void setParentPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        JavaFX.copyMenuItems( parentPopupMenu, popup.getItems(), true );
    }
        
}
