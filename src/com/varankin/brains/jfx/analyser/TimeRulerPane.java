package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Линейка по горизонтальной оси времени. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class TimeRulerPane extends AbstractRulerPane
{
    private final double factor = 1d; // 1, 2, 5
    private final int pixelStepMin = 10; // min 10 pixels in between ticks
    private final TimeConvertor convertor;
    private final SimpleBooleanProperty relativeProperty;
    @Deprecated private final ContextMenu popup;

    TimeRulerPane( TimeConvertor convertor )
    {
        this.convertor = convertor;
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
        
        popup = new ContextMenu();
        popup.getItems().addAll( new MenuItem("TimeRuler action") );
        
        setMinWidth( 100d );
        setOnMouseClicked( new ContextMenuRaiser( popup, TimeRulerPane.this ) );
        widthProperty().addListener( new SizeChangeListener() );
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
        tick.setStroke( getTickPaint() );
        tick.relocate( x, 0d );
        getChildren().add( tick );
        if( s % 10 == 0 || ( s % 5 == 0 && relative ) )
        {
            Text value = new Text( text );
            value.relocate( x, 10d );
            value.setFill( getValuePaint() );
            if( x + value.boundsInLocalProperty().get().getMaxX() < getWidth() )
                getChildren().add( value );
        }
    }
    
}