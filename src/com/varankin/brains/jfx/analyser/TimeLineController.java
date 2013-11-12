package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.nio.IntBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 *
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeLineController
{
    private final DoubleProperty widthProperty, heightProperty;
    private final SimpleBooleanProperty dynamicProperty;
    private final TimeConvertor timeConvertor;
    private final ValueConvertor valueConvertor;
    private Color timeAxisColor, valueAxisColor;
//    private final Map<Object,Future<?>> flowProcesses;

    public TimeLineController( JavaFX jfx, TimeConvertor timeRuler, ValueConvertor valueRuler )
    {
        widthProperty = new SimpleDoubleProperty();
        widthProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                timeConvertor.reset( newValue.doubleValue(), dynamicProperty.get() ? 
                            System.currentTimeMillis() : timeConvertor.getEntry() );
//                replaceImage( newValue.intValue(), heightProperty.intValue() );
            }
        } );
        heightProperty = new SimpleDoubleProperty();
        heightProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                valueConvertor.reset( newValue.intValue() );
//                replaceImage( widthProperty.intValue(), newValue.intValue() );
            }
        } );
        dynamicProperty = new SimpleBooleanProperty();
        //DEBUG START
        timeAxisColor = Color.BLACK;
        valueAxisColor = Color.BLACK;
        //DEBUG END
        this.timeConvertor = timeRuler;
        this.valueConvertor = valueRuler;
//        flowProcesses = new IdentityHashMap<>(); //TODO DEBUG Identity        
    }

    DoubleProperty widthProperty()
    {
        return widthProperty;
    }
    
    DoubleProperty heightProperty()
    {
        return heightProperty;
    }
    
    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }

    TimeConvertor getTimeConvertor()
    {
        return timeConvertor;
    }

    ValueConvertor getValueConvertor()
    {
        return valueConvertor;
    }
    
//    Future<?> startFlow( Object o, DotPainter painter )
//    {
//        Future<?> process = executorService.submit( painter );
//        flowProcesses.put( o, process );
//        return process;
//    }
//    
//    boolean stopFlow( Object o )
//    {
//        Future<?> process = flowProcesses.get( o );
//        return process == null || process.cancel( true );
//    }
//    
//    void removeFlow( Object o )
//    {
//        flowProcesses.remove( o );
//    }
    
    public Color getTimeAxisColor()
    {
        return timeAxisColor;
    }

    public void setTimeAxisPaint( Color color )
    {
        timeAxisColor = color;
    }

    public Color getValueAxisColor()
    {
        return valueAxisColor;
    }

    public void setValueAxisPaint( Color color )
    {
        valueAxisColor = color;
    }

}
