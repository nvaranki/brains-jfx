package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * Управление прорисовкой отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ControlBar extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ControlBar.class );
    
    private final FlowPane valuesPane;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Runnable refreshService;
    private final SimpleBooleanProperty dynamicProperty;

    private long refreshRate;
    private TimeUnit refreshRateUnit;
    
    ControlBar( JavaFX jfx, Runnable service )
    {
        //TODO appl param
        refreshRate = 100L; // ms
        refreshRateUnit = TimeUnit.MILLISECONDS;
        
        refreshService = service;
        executorService = jfx.getExecutorService();
        scheduledExecutorService = jfx.getScheduledExecutorService();
        
        CheckBox labelTime = new CheckBox( LOGGER.text( "ControlBar.axis.time.name" ) );
        labelTime.setSelected( true );
        labelTime.setOnAction( new Holder( labelTime, scheduledExecutorService.scheduleAtFixedRate( 
                refreshService, 0L, refreshRate, refreshRateUnit ) ) );
        
        dynamicProperty = new SimpleBooleanProperty(); //TODO ReadOnlyBooleanWrapper().;
        dynamicProperty.bind( labelTime.selectedProperty() );
        
        valuesPane = new FlowPane();
        valuesPane.setHgap( 30 );
        valuesPane.setPadding( new Insets( 0, 5, 0, 5 ) );
        valuesPane.setAlignment( Pos.CENTER );
        valuesPane.setMinHeight( labelTime.getMinHeight() );
        valuesPane.setPrefHeight( labelTime.getPrefHeight() );

        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        cc0.setHalignment( HPos.CENTER );
        cc0.setMinWidth( 110d );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.NEVER );
        cc1.setHalignment( HPos.RIGHT );
        cc1.setMinWidth( 100d );
        
        getColumnConstraints().addAll( cc0, cc1 );
        add( valuesPane, 0, 0 );
        add( labelTime, 1, 0 );
    }

    /**
     * Добавляет отображаемое значение.
     * 
     * @param name    название значения.
     * @param painter сервис рисования отметок.
     */
    void addValueControl( String name, DrawAreaPainter painter )
    {
        WritableImage sample = new WritableImage( 16, 16 );
        Color outlineColor = Color.LIGHTGRAY;
        for( int i = 1; i < 15; i ++ )
        {
            sample.getPixelWriter().setColor( i,  0, outlineColor );
            sample.getPixelWriter().setColor( i, 15, outlineColor );
            sample.getPixelWriter().setColor(  0, i, outlineColor );
            sample.getPixelWriter().setColor( 15, i, outlineColor );
        }
        painter.paint( 7, 7, sample );

        CheckBox label = new CheckBox( name );
        label.setGraphic( new ImageView( sample ) );
        label.setGraphicTextGap( 3 );
        label.setSelected( true );
        label.setOnAction( new Selector( label, painter, executorService.submit( painter ) ) );
        
        valuesPane.getChildren().add( label );
    }

    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }
    
    long getRefreshRate()
    {
        return refreshRate;
    }

    void setRefreshRate( long rate )
    {
        refreshRate = rate;
    }

    TimeUnit getRefreshRateUnit()
    {
        return refreshRateUnit;
    }

    void setRefreshRateUnit( TimeUnit unit )
    {
        refreshRateUnit = unit;
    }
    
    /**
     * Контроллер движения временной шкалы.
     */
    private class Holder implements EventHandler<ActionEvent>
    {
        private final CheckBox cb;
        private final Map<CheckBox,Boolean> state;
        private Future<?> process;

        Holder( CheckBox cb, Future<?> process )
        {
            this.cb = cb;
            this.process = process;
            state = new HashMap<>();
        }
        
        @Override
        public void handle( ActionEvent _ )
        {
            if( cb.selectedProperty().get() )
            {
                // возобновить движение графической зоны
                process = scheduledExecutorService.scheduleAtFixedRate( 
                        refreshService, 0L, refreshRate, refreshRateUnit );
                // возобновить рисование отметок
                for( Node node : valuesPane.getChildren() )
                    if( node instanceof CheckBox )
                    {
                        CheckBox vcb = (CheckBox)node;
                        Boolean selected = state.get( vcb );
                        vcb.selectedProperty().setValue( selected != null ? selected : true );
                        vcb.disableProperty().setValue( false );
                        // имитировать клик
                        vcb.fireEvent( new ActionEvent( this, null ) ); // DO NOT vcb.fire()!
                    }
            }
            else
            {
                // остановить движение графической зоны
                process.cancel( true );
                // остановить рисование отметок
                for( Node node : valuesPane.getChildren() )
                    if( node instanceof CheckBox )
                    {
                        CheckBox vcb = (CheckBox)node;
                        state.put( vcb, vcb.selectedProperty().getValue() );
                        vcb.selectedProperty().setValue( false );
                        vcb.disableProperty().setValue( true );
                        // имитировать клик
                        vcb.fireEvent( new ActionEvent( this, null ) ); // DO NOT vcb.fire()!
                    }
            }
        }
        
    }
    
    /**
     * Контроллер видимости значений.
     */
    private class Selector implements EventHandler<ActionEvent>
    {
        private final CheckBox cb;
        private final DrawAreaPainter painter;
        private Future<?> process;

        Selector( CheckBox cb, DrawAreaPainter painter, Future<?> process ) 
        {
            this.cb = cb;
            this.painter = painter;
            this.process = process;
        }
        
        @Override
        public void handle( ActionEvent _ ) 
        {
            if( cb.selectedProperty().get() )
                process = executorService.submit( painter );
            else
                process.cancel( true );
        }
    }

}
