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
    private final SimpleBooleanProperty relativeProperty;
    private final SimpleObjectProperty<TimeConvertor> convertorProperty;
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    
    private TimeRulerPropertiesStage properties;
    
    @FXML private Pane pane;
    @FXML private MenuItem menuItemProperties;
    @FXML private ContextMenu popup;
    
    public TimeRulerController()
    {
        TimeUnit convertorUnits = TimeUnit.SECONDS;
        TimeConvertor convertor = new TimeConvertor( 60, 2, convertorUnits );
        convertorProperty = new SimpleObjectProperty<>( convertor );
        durationProperty = new SimpleObjectProperty<>( convertor.getSize() );
        excessProperty = new SimpleObjectProperty<>( convertor.getExcess() );
        unitProperty = new SimpleObjectProperty<>( convertorUnits );
        relativeProperty = new SimpleBooleanProperty();
        relativeProperty.addListener( new ChangeListener<Boolean>() 
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue )
            {
                if( pane.widthProperty().intValue() > 0 ) 
                {
                    removeRuler();
                    generateRuler();
                }
            }
        } );
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
        pane.widthProperty().addListener( new SizeChangeListener() );
        pane.setMinWidth( 100d );
/*
        BooleanBinding reconfigure = Bindings.createBooleanBinding( 
                new Callable<Boolean>()
                {
                    @Override
                    public Boolean call() throws Exception
                    {
                        return true;
                    }
                }, 
                sizeProperty, excessProperty, unitProperty );
        reconfigure.addListener( new InvalidationListener() 
        {
            @Override
            public void invalidated( Observable _ )
            {
                Long duration = sizeProperty.getValue();
                Long excess = excessProperty.getValue();
                TimeUnit unit = unitProperty.getValue();
                int width = widthProperty().intValue(); 
                if( width > 0 
                        && duration != null && duration > 0L 
                        && excess != null && unit != null ) 
                {
                    convertor.reset( duration, excess, unit );
                    generateRuler();
                }
            }
        } );
 */        
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
    
    ObjectProperty<TimeConvertor> convertorProperty()
    {
        return convertorProperty;
    }
    
    @Override
    protected void reset( int size )
    {
        TimeConvertor convertor = convertorProperty.get();
        convertor.reset( size, relativeProperty.get() ?
                            System.currentTimeMillis() : convertor.getEntry() );
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
    protected void removeRuler()
    {
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
    }
    
    @Override
    protected void generateRuler()
    {
        TimeConvertor convertor = convertorProperty.get();
        boolean relative = relativeProperty.get();
        DateFormat formatter = DateFormat.getTimeInstance();
        long step = (long)roundToFactor( convertor.getSize() / ( pane.getWidth() / pixelStepMin ), factor );
        for( int i = 1, count = (int)Math.ceil( convertor.getExcess() / step ); i <= count; i++ )
        {
            long t = step * i;
            int x = convertor.timeToImage( convertor.getEntry() + t );
            if( 0 <= x && x < pane.getWidth() )
                generateTickAndText( x, relative ?
                        Long.toString( t/1000L/*new Date( t ).getSeconds()*/ ) :
                        formatter.format( new Date( convertor.getEntry() + t ) ), i, relative );
        }
        for( int i = 0, count = (int)Math.ceil( ( convertor.getSize() - convertor.getExcess() ) / step ); i <= count; i++ )
        {
            long t = - step * i;
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

    void setParentPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        JavaFX.copyMenuItems( parentPopupMenu, popup.getItems(), true );
    }
    
}
