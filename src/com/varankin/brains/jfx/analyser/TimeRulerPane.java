package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeRulerPane extends AbstractRulerPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TimeRulerPane.class );
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    private final TimeConvertor convertor;
    private final SimpleBooleanProperty relativeProperty;
    @Deprecated private final ContextMenu popup;
    private TimeRulerPropertiesStage properties;
    private final Property<Long> sizeProperty, excessProperty;
    private final Property<TimeUnit> unitProperty;
    
    @FXML private MenuItem menuItemProperties;

    TimeRulerPane( final TimeConvertor convertor )
    {
        this.convertor = convertor;
        sizeProperty = new SimpleObjectProperty<>();
        excessProperty = new SimpleObjectProperty<>();
        unitProperty = new SimpleObjectProperty<>();
        relativeProperty = new SimpleBooleanProperty();
        relativeProperty.addListener( new ChangeListener<Boolean>() 
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue )
            {
                if( widthProperty().intValue() > 0 ) 
                    generateRuler();
            }
        } );
        
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
        popup.getItems().addAll( new MenuItem("TimeRuler action"), menuItemProperties );
        
        setMinWidth( 100d );
        setOnMouseClicked( new ContextMenuRaiser( popup, TimeRulerPane.this ) );
        widthProperty().addListener( new SizeChangeListener() );
        sizeProperty.setValue( convertor.getSize() );
        excessProperty.setValue( convertor.getExcess() );
        unitProperty.setValue( TimeUnit.MILLISECONDS );
        tickColorProperty().setValue( Color.BLACK );
        textColorProperty().setValue( Color.BLACK );
        fontProperty().setValue( new Text().getFont() );
        //ChangeListener
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
            controller.bindDurationProperty( sizeProperty );
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
    
    @Deprecated
    void appendToPopup( List<MenuItem> items ) 
    {
        if( items != null && !items.isEmpty() )
        {
            popup.getItems().add( new SeparatorMenuItem() );
            JavaFX.copyMenuItems( items, popup.getItems() );
        }
    }
    
    @Override
    protected void generateRuler()
    {
        for( Node node : getChildren() )
            if( node instanceof Line )
            {
                ((Line)node).strokeProperty().unbind();
            }
            else if( node instanceof Text )
            {
                ((Text)node).fillProperty().unbind();
                ((Text)node).fontProperty().unbind();
            }
        getChildren().clear();
        
        boolean relative = relativeProperty.get();
        DateFormat formatter = DateFormat.getTimeInstance();
        long step = (long)roundToFactor( convertor.getSize() / ( getWidth() / pixelStepMin ), factor );
        for( int i = 1, count = (int)Math.ceil( convertor.getExcess() / step ); i <= count; i++ )
        {
            long t = step * i;
            int x = convertor.timeToImage( convertor.getEntry() + t );
            if( 0 <= x && x < getWidth() )
                generateTickAndText( x, relative ?
                        Long.toString( t/1000L/*new Date( t ).getSeconds()*/ ) :
                        formatter.format( new Date( convertor.getEntry() + t ) ), i, relative );
        }
        for( int i = 0, count = (int)Math.ceil( ( convertor.getSize() - convertor.getExcess() ) / step ); i <= count; i++ )
        {
            long t = - step * i;
            int x = convertor.timeToImage( convertor.getEntry() + t );
            if( 0 <= x && x < getWidth() )
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
        getChildren().add( tick );
        if( s % 10 == 0 || ( s % 5 == 0 && relative ) )
        {
            Text value = new Text( text );
            value.relocate( x, 10d );
            value.fillProperty().bind( textColorProperty() );
            value.fontProperty().bind( fontProperty() );
            if( x + value.boundsInLocalProperty().get().getMaxX() < getWidth() )
                getChildren().add( value );
        }
    }
    
}
