package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.PaneWithPopup;
import com.varankin.util.LoggerX;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
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
    @FXML private MenuItem menuItemProperties;
    @FXML private ContextMenu popup;
    
    public TimeRulerController()
    {
//        TimeUnit convertorUnits = TimeUnit.SECONDS;
//        TimeConvertor convertor = new TimeConvertor( 60, 2, convertorUnits );
        convertorProperty = new SimpleObjectProperty<>();// convertor );
        durationProperty = new SimpleObjectProperty<>( 0L );// convertor.getSize() );
        excessProperty = new SimpleObjectProperty<>( 0L );// convertor.getExcess() );
        unitProperty = new SimpleObjectProperty<>( TimeUnit.SECONDS );// convertorUnits );
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
        TimeConvertor convertor = new TimeConvertor( 
                durationProperty.getValue(), excessProperty.getValue(), unitProperty.getValue() );
        convertor.reset( pane.widthProperty().doubleValue(), relativeProperty.get() ?
                            System.currentTimeMillis() : convertor.getEntry() );
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
        DateFormat formatter = DateFormat.getTimeInstance();
        long step = (long)roundToFactor( convertor.getSize() / ( pane.getWidth() / pixelStepMin ), factor );
        if( step > 0L )
        {
            int stepCountRight = (int)Math.ceil( convertor.getExcess() / step );
            int stepCountLeft  = (int)Math.ceil( ( convertor.getSize() - convertor.getExcess() ) / step );
            generateAllTickAndText( 1, step, stepCountRight, convertor, relative, formatter );
            generateAllTickAndText( 0, -step, stepCountLeft, convertor, relative, formatter );
        }
    }
    
    private void generateAllTickAndText( int start, long step, int count, TimeConvertor convertor,
            boolean relative, DateFormat formatter )
    {
        for( int i = start; i <= count; i++ )
        {
            long t = step * i;
            int x = convertor.timeToImage( convertor.getEntry() + t );
            if( 0 <= x && x < pane.getWidth() )
                generateTickAndText( x, relative ?
                        Long.toString( t/1000L/*new Date( t ).getSeconds()*/ ) :
                        formatter.format( new Date( convertor.getEntry() + t ) ), i, relative );
        }
    }
    
    private void generateTickAndText( int x, String text, long s, boolean relative )
    {
        int length = s % 10 == 0 ? getTickSizeLarge() : 
                s % 5 == 0 ? getTickSizeMedium() : getTickSizeSmall();
        Line tick = new Line( 0, 0, 0, length );
        tick.strokeProperty().bind( tickColorProperty() );
        tick.relocate( x, 0d );
        pane.getChildren().add( tick );
        if( s % 10 == 0 || ( s % 5 == 0 && relative ) )
        {
            Text value = new Text( text );
            value.relocate( x, 10d );
            value.fillProperty().bind( textColorProperty() );
            value.fontProperty().bind( fontProperty() );
            if( x + value.boundsInLocalProperty().get().getMaxX() < pane.getWidth() )
                pane.getChildren().add( value );
        }
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

    void extendPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        JavaFX.copyMenuItems( parentPopupMenu, popup.getItems(), true );
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
