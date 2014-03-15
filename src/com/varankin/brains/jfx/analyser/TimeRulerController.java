package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * FXML-контроллер панели шкалы по оси времени.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TimeRulerController extends AbstractRulerController
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeRulerController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/TimeRuler.css";
    private static final String CSS_CLASS = "time-ruler";
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    
    private final static Map<TimeUnit,Format> TF;
    static
    {
        TF = new EnumMap<>( TimeUnit.class );
        TF.put( TimeUnit.NANOSECONDS, NumberFormat.getInstance() );
        TF.put( TimeUnit.MICROSECONDS, NumberFormat.getInstance() );
        TF.put( TimeUnit.MILLISECONDS, new SimpleDateFormat( "m:ss.SSS" ) );
        TF.put( TimeUnit.SECONDS, new SimpleDateFormat( "H:mm:ss" ) );
        TF.put( TimeUnit.MINUTES, new SimpleDateFormat( "HH:mm" ) );
        TF.put( TimeUnit.HOURS, new SimpleDateFormat( "d, H:mm" ) );
        TF.put( TimeUnit.DAYS, new SimpleDateFormat( "MMM, d" ) );
    }
    
    private final Property<Long> durationProperty, excessProperty;
    private final Property<TimeUnit> unitProperty;
    private final BooleanProperty relativeProperty;
    private final Property<TimeConvertor> convertorProperty;
    private final ChangeListener<Long> boundChangeListener;
    private final ChangeListener<TimeUnit> unitChangeListener;
    private final ChangeListener<Boolean> relativeChangeListener;
    private final ChangeListener<Number> sizeChangeListener;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    
    private TimeRulerPropertiesStage properties;
    
    @FXML private Pane pane;
    @FXML private ContextMenu popup;
    
    public TimeRulerController()
    {
        convertorProperty = new SimpleObjectProperty<>();
        durationProperty = new SimpleObjectProperty<>( 0L );
        excessProperty = new SimpleObjectProperty<>( 0L );
        unitProperty = new SimpleObjectProperty<>( TimeUnit.SECONDS );
        relativeProperty = new SimpleBooleanProperty();

        boundChangeListener = new BoundChangeListener<>();
        durationProperty.addListener( new WeakChangeListener<>( boundChangeListener ) );
        excessProperty.addListener( new WeakChangeListener<>( boundChangeListener ) );
        
        unitChangeListener = new BoundChangeListener<>();
        unitProperty.addListener( new WeakChangeListener<>( unitChangeListener ) );
        
        relativeChangeListener = new КelativeChangeListener();
        relativeProperty.addListener( new WeakChangeListener<>( relativeChangeListener ) );
        
        sizeChangeListener = new SizeChangeListener();
    }
    
    /**
     * Создает панель шкалы по оси времени.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public Pane build()
    {
        MenuItem menuItemProperties = new MenuItem( LOGGER.text( "control.popup.properties" ) );
        menuItemProperties.setGraphic( JavaFX.icon( "icons16x16/properties.png" ) );
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
        
        pane = new Pane();
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
        pane.widthProperty().addListener( new WeakChangeListener<>( sizeChangeListener ) );
        pane.setMinWidth( 100d );
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
            properties = new TimeRulerPropertiesStage();
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.initModality( Modality.NONE );
            properties.setTitle( LOGGER.text( "properties.ruler.time.title", 0 ) );
            TimeRulerPropertiesController controller = properties.getController();
            controller.bindDurationProperty( durationProperty );
            controller.bindExcessProperty( excessProperty );
            controller.bindUnitProperty( unitProperty );
            controller.bindTickColorProperty( tickColorProperty() );
            controller.bindTextColorProperty( textColorProperty() );
            controller.bindTextFontProperty( fontProperty() );
        }
        properties.show();
        properties.toFront();
    }

    BooleanProperty relativeProperty()
    {
        return relativeProperty;
    }
    
    Property<TimeConvertor> convertorProperty()
    {
        return convertorProperty;
    }
    
    void reset( TimeRulerPropertiesPaneController pattern )
    {
        tickColorProperty().setValue( pattern.tickColorProperty().getValue() );
        textColorProperty().setValue( pattern.textColorProperty().getValue() );
        fontProperty().setValue( pattern.textFontProperty().getValue() );
        durationProperty.setValue( pattern.durationProperty().getValue() );
        excessProperty.setValue( pattern.excessProperty().getValue() );
        unitProperty.setValue( pattern.unitProperty().getValue() );
    }
    
    @Override
    protected void reset()
    {
        long duration = TIME_UNIT.convert( durationProperty.getValue(), unitProperty.getValue() );
        long excess   = TIME_UNIT.convert( excessProperty.getValue(), unitProperty.getValue() );
        TimeConvertor convertor = new TimeConvertor( duration, excess, pane.widthProperty().intValue() );
        TimeConvertor cr = convertorProperty.getValue();
        convertor.setEntry( relativeProperty.get() || cr == null ? System.currentTimeMillis() : cr.getEntry() );
        convertorProperty.setValue( convertor );
        reset( convertor );
    }
    
    private void reset( TimeConvertor convertor )
    {
        // remove ruler
        for( Node node : pane.getChildren() )
            if( node instanceof Line )
            {
                ((Line)node).strokeProperty().unbind();
            }
            else if( node instanceof Text )
            {
                ((Text)node).fillProperty().unbind();
                ((Text)node).fontProperty().unbind();
            }
        pane.getChildren().clear();
        
        // generate ruler
        boolean relative = relativeProperty.get();
        long duration = TIME_UNIT.convert( durationProperty.getValue(), unitProperty.getValue() );
        long excess   = TIME_UNIT.convert( excessProperty.getValue(),   unitProperty.getValue() );
        long u = TIME_UNIT.convert( 1L, unitProperty.getValue() );
        
        int tl, tm;
        if( relative )
        {
            tl = 10; tm = 5;
        }
        else switch( unitProperty.getValue() )
        {
            case DAYS:
                tl = 10; tm = 10; break;
            case HOURS:
            case MINUTES:
                tl = 12; tm = 6; break;
            default:
                tl = 10; tm = 5;
        }
        
        long step = (long)roundToFactor( duration / ( pane.getWidth() / pixelStepMin ), factor );
        if( u / tl > step ) step = u / tl;
        else if( u / tm > step ) step = u / tm;
        else if( u / 2L > step ) step = u / 2L;
        else if( u > step ) step = u;
        else step -= step % u;
        
        if( step > 0L )
          if( relative )
          {
            int stepCountRight = (int)Math.ceil( excess / step );
            int stepCountLeft  = (int)Math.ceil( ( duration - excess ) / step );
            generateAllTickAndTextRel( 1, step, stepCountRight, convertor, relative, tl, tm );
            generateAllTickAndTextRel( 0, -step, stepCountLeft, convertor, relative, tl, tm );
          }
          else
          {
              long start = convertor.getEntry() + excess - duration;
              long offset = start % step;
              if( offset < 0 ) offset += step;
              start -= offset;
              int stepCount = (int)Math.ceil( duration / step );
              generateAllTickAndTextAbs( start, step, stepCount, convertor, relative, tl, tm );
          }
    }
    
    private void generateAllTickAndTextAbs( long start, long step, int count, TimeConvertor convertor,
            boolean relative, int tl, int tm )
    {
        TimeUnit unit = unitProperty.getValue();
        Format df = TF.get( unit );
        
        for( int i = 0; i <= count; i++ )
        {
            long t = start + step * i;
            int x = convertor.timeToImage( t );
            if( 0 <= x && x < pane.getWidth() )
            {
                String text = df.format( t );
                long ic = start / step;
                generateTickAndText( x, text, ic + i, relative, tl, tm );
            }
        }
    }
    
    private void generateAllTickAndTextRel( int start, long step, int count, TimeConvertor convertor, 
            boolean relative, int tl, int tm )
    {
        TimeUnit unit = unitProperty.getValue();
        Format df = TF.get( unit );
        
        for( int i = start; i <= count; i++ )
        {
            long t = step * i;
            int x = convertor.offsetToImage( t );
            if( 0 <= x && x < pane.getWidth() )
            {
                String text = relative ?
                    Long.toString( unit.convert( t, TIME_UNIT ) ) :
                    df.format( convertor.getEntry() + t );
                generateTickAndText( x, text, i, relative, tl, tm );
            }
        }
    }
    
    private void generateTickAndText( int x, String text, long s, boolean relative, int tl, int tm )
    {
        int length = s % tl == 0 ? getTickSizeLarge() : 
                s % tm == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, 0, length );
        tick.strokeProperty().bind( tickColorProperty() );
        tick.relocate( x, 0d );
        pane.getChildren().add( tick );
        if( s % tl == 0 || ( s % tm == 0 && relative ) )
        {
            Text value = new Text( text );
            double maxWidth = value.boundsInLocalProperty().get().getMaxX();
            x -= maxWidth / 2;
            value.relocate( x, 10d );
            value.fillProperty().bind( textColorProperty() );
            value.fontProperty().bind( fontProperty() );
            if( 0 <= x && x + maxWidth < pane.getWidth() )
                pane.getChildren().add( value );
        }
    }
    
    void extendPopupMenu( List<? extends MenuItem> menu )
    {
        JavaFX.copyMenuItems( menu, popup.getItems(), true );
    }
    
    private class КelativeChangeListener implements ChangeListener<Boolean>
    {
        @Override
        public void changed( ObservableValue<? extends Boolean> _, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && !newValue.equals( oldValue ) ) 
                reset( convertorProperty.getValue() );
        }
    }
    
}
