package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.property.PropertyMonitor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

/**
 *
 * @author Николай
 */
@Deprecated // DEBUG
class MenuItemSimulatePropertyMonitor extends MenuItem
{
    private final List<Value> legend;
    private final Фабрика<Void,DotPainter> фабрика;
    
    MenuItemSimulatePropertyMonitor( List<Value> legend, Фабрика<Void,DotPainter> фабрика )
    {
        super( "Simulate" );
        this.legend = legend;
        this.фабрика = фабрика;
        setOnAction( new EventHandler<ActionEvent>() 
        {
            @Deprecated private int id;
            
            @Override
            public void handle( ActionEvent __ )
            {
                simulate( "Value A"+id++, "Value B"+id++, "Value C"+id++ );
            }
        } );
    }
        
    void simulate( String... values )
    {
        JavaFX jfx = JavaFX.getInstance();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
        int i = 0;
        final List<PropertyMonitorImpl> monitors = new ArrayList<>();
        for( String value : values )
        {
            PropertyMonitorImpl monitor = new PropertyMonitorImpl();
            monitors.add( monitor );
            DotPainter painter = фабрика.создать( null );
            Value v = new Value( monitor, PropertyMonitorImpl.PROPERTY, null, 
                    painter, patterns[i%patterns.length], colors[i%colors.length], value );
            legend.add( v );
            i++;
        }
        
        Runnable observerService = new Runnable()
        {
            @Override
            public void run()
            {
                for( PropertyMonitorImpl monitor : monitors )
                    monitor.fire();
            }
        };
        jfx.getScheduledExecutorService().scheduleAtFixedRate( observerService, 0L, 1000L, TimeUnit.MILLISECONDS );
    }
    
    private final static class PropertyMonitorImpl implements PropertyMonitor
    {
        static final String PROPERTY = "value";
        final Collection<PropertyChangeListener> listeners = new ArrayList<>();

        @Override
        public Collection<PropertyChangeListener> наблюдатели()
        {
            return listeners;
        }

        void fire()
        {
            for( PropertyChangeListener listener : listeners )
                listener.propertyChange( new PropertyChangeEvent( 
                        PropertyMonitorImpl.this, PROPERTY, 
                        null, (float)Math.random() * 2f - 1f ) );
        }
    }

}
