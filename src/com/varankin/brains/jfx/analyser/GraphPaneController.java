package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.InverseBooleanBinding;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер графической зоны для рисования отметок. 
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class GraphPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( GraphPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/GraphPane.css";
    private static final String CSS_CLASS = "graph-pane";
    
    private static long idCounter;
    
    private final DoubleProperty widthProperty, heightProperty;
    private final ReadOnlyObjectWrapper<WritableImage> writableImageProperty;
    private final SimpleBooleanProperty dynamicProperty;
    private final ObjectProperty<Long> rateValueProperty;
    private final ObjectProperty<TimeUnit> rateUnitProperty;
    private final BooleanProperty borderDisplayProperty, zeroDisplayProperty;
    private final ObjectProperty<Color> borderColorProperty, zeroColorProperty;
    private final ImageFlowService refreshService;

//    private long refreshRate;
//    private TimeUnit refreshRateUnit;
    private TimeConvertor timeConvertor;
    private ValueConvertor valueConvertor;
//    private Color zeroValueAxisColor;
    private GraphPropertiesStage properties;
    private long id;
    
    @FXML private Pane pane;
    @FXML private ImageView imageView;
    @FXML private MenuItem menuItemResume;
    @FXML private MenuItem menuItemStop;
    @FXML private MenuItem menuItemProperties;
    @FXML private ContextMenu popup;
    
    public GraphPaneController()
    {
        widthProperty = new SimpleDoubleProperty();
        heightProperty = new SimpleDoubleProperty();
        writableImageProperty = new ReadOnlyObjectWrapper<>();
        dynamicProperty = new SimpleBooleanProperty();
        rateValueProperty = new SimpleObjectProperty<>();
        rateUnitProperty = new SimpleObjectProperty<>();
        borderDisplayProperty = new SimpleBooleanProperty();
        borderColorProperty = new SimpleObjectProperty<>();
        zeroDisplayProperty = new SimpleBooleanProperty();
        zeroColorProperty = new SimpleObjectProperty<>();
        
        refreshService = new ImageFlowService();
        id = ++idCounter;
        
        //TODO appl param
        rateValueProperty.set( 100L );
        rateUnitProperty.set( TimeUnit.MILLISECONDS );
        borderDisplayProperty.set( false );
        borderColorProperty.set( Color.GRAY );
        zeroDisplayProperty.set( true );
        zeroColorProperty.set( Color.GRAY );
    }

    /**
     * Создает графическую зону для рисования отметок. 
     * Применяется в конфигурации без FXML.
     */
    @Override
    public Pane build()
    {
        
        menuItemResume = new MenuItem( LOGGER.text( "control.popup.start" ) );
        menuItemResume.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionResume( event );
            }
        } );
        
        menuItemStop = new MenuItem( LOGGER.text( "control.popup.stop" ) );
        menuItemStop.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionStop( event );
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
        popup.getItems().addAll( menuItemResume, menuItemStop, menuItemProperties );
        
        imageView = new ImageView();
        imageView.setPreserveRatio( true );

        pane = new Pane();
        pane.setOnContextMenuRequested( new EventHandler<ContextMenuEvent>() 
        {
            @Override
            public void handle( ContextMenuEvent event )
            {
                onContextMenuRequested( event );
            }
        } );
        
        pane.getChildren().add( imageView );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        imageView.imageProperty().bind( writableImageProperty );
        
        dynamicProperty.addListener( new ChangeListener<Boolean>() 
        {
            private Future<?> process;

            @Override
            public void changed( ObservableValue<? extends Boolean> observable, 
                                Boolean oldValue, Boolean newValue ) 
            {
                if( newValue != null )
                    if( newValue )
                    {
                        // возобновить движение графической зоны
                        process = JavaFX.getInstance().getScheduledExecutorService().scheduleAtFixedRate( 
                            refreshService, 0L, rateValueProperty.getValue(), rateUnitProperty.getValue() );
                    }
                    else
                    {
                    // остановить движение графической зоны
                    if( process != null )
                        process.cancel( true );
                    }
            }
        } );
        
        widthProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                replaceImage( newValue.intValue(), heightProperty.intValue() );
            }
        } );
        
        heightProperty.addListener( new ChangeListener<Number>() 
        {
            @Override
            public void changed( ObservableValue<? extends Number> ov, Number oldValue, Number newValue )
            {
                replaceImage( widthProperty.intValue(), newValue.intValue() );
            }
        } );

        menuItemResume.disableProperty().bind( dynamicProperty );
        menuItemStop.disableProperty().bind( new InverseBooleanBinding( dynamicProperty ) );
        menuItemProperties.setGraphic( JavaFX.icon( "icons16x16/properties.png" ) );
    }
    
    @FXML
    private void onContextMenuRequested( ContextMenuEvent event )
    {
        popup.show( pane, event.getScreenX(), event.getScreenY() );
        event.consume();
    }
    
    @FXML
    private void onActionResume( ActionEvent _ )
    {
        dynamicProperty.setValue( Boolean.TRUE );
    }

    @FXML
    private void onActionStop( ActionEvent _ )
    {
        dynamicProperty.setValue( Boolean.FALSE );
    }
        
    @FXML
    private void onActionProperties( ActionEvent _ )
    {
        if( properties == null )
        {
            properties = new GraphPropertiesStage( 
                    rateValueProperty, rateUnitProperty,
                    borderDisplayProperty, borderColorProperty, 
                    zeroDisplayProperty, zeroColorProperty, 
                    dynamicProperty );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.setTitle( LOGGER.text( "properties.graph.title", Long.toString( id ) ) );
        }
        properties.show();
        properties.toFront();
    }

    ReadOnlyObjectProperty<WritableImage> writableImageProperty()
    {
        return writableImageProperty.getReadOnlyProperty();
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

    void setParentPopupMenu( List<MenuItem> parentPopupMenu )
    {
        JavaFX.copyMenuItems( parentPopupMenu, popup.getItems(), true );
    }

    long getRefreshRate()
    {
        return rateValueProperty.getValue();
    }

    void setRefreshRate( long rate )
    {
        rateValueProperty.setValue( rate );
    }

    TimeUnit getRefreshRateUnit()
    {
        return rateUnitProperty.getValue();
    }

    void setRefreshRateUnit( TimeUnit unit )
    {
        rateUnitProperty.setValue( unit );
    }

    void setTimeConvertor( TimeConvertor convertor )
    {
        timeConvertor = convertor;
    }

    void setValueConvertor( ValueConvertor convertor )
    {
        valueConvertor = convertor;
    }
    
    private void replaceImage( int width, int height )
    {
        if( width > 0 && height > 0 )
        {
            // заменить, т.к. другие размеры
            WritableImage newWritableImage = new WritableImage( width, height );
            // нарисовать оси 
            int zero = valueConvertor.valueToImage( 0.0F );
            drawAxes( newWritableImage, width, height, zero );
            refreshService.snapshotBlankPatterns( newWritableImage, height, 2, 1 );
            //setImage( null );
//            getTransforms().clear();
//            getChildren().clear();
//            view.setImage( writableImage = newWritableImage );
//            view.setPreserveRatio( true );
//            getTransforms().add( new Translate( 0, height ) );
//            getTransforms().add( new Scale( 1, -1 ) );
////            setImage( writableImage = newWritableImage );
//            getChildren().add( view );
            writableImageProperty.setValue( newWritableImage );
        }
    }
    
    private void drawAxes( WritableImage writableImage, int width, int height, int zero )
    {
        PixelWriter writer = writableImage.getPixelWriter();
        //TODO option BORDER t,r,b,l
//        for( int i = 0; i < width; i++ ) 
//            writer.setColor( i, 0, getTimeAxisColor() ); // timeline
//        for( int i = 0; i < height; i++ ) 
//            writer.setColor( 0, i, getValueAxisColor() ); // value
        if( 0 < zero && zero < heightProperty.intValue() )
        {
            Color color = zeroColorProperty.getValue();
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, color ); // zero value
        }
    }
    
    /**
     * Сервис движения временной шкалы.
     */
    private class ImageFlowService implements Runnable
    {
        final WritablePixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
        final int blank[][] = new int[2][];
        
        int timeAxisOffset;
        
        @Override
        public void run()
        {
            Platform.runLater( new Adopter( System.currentTimeMillis() ) ); 
        }

        private void snapshotBlankPatterns( WritableImage writableImage, int height, int... ys )
        {
            PixelReader reader = writableImage.getPixelReader();
            for( int i = 0; i < blank.length && i < ys.length; i++ )
                reader.getPixels( ys[i], 0, 1, height, pixelFormat, blank[i] = new int[height], 0, 1 );
            timeAxisOffset = 0;
        }

        private void shiftImage( int shift, WritableImage image )
        {
            PixelWriter writer = image.getPixelWriter();
            int pixelWidth = widthProperty.intValue();
            int pixelHeight = heightProperty.intValue();
            int width = pixelWidth - shift - 1;
            if( width > 0 )
                writer.setPixels( 1, 0, width, pixelHeight, image.getPixelReader(), shift + 1, 0 );
            // очистить справа от скопированной зоны
            timeAxisOffset += shift;
            timeAxisOffset %= 2;
            for( int i = Math.max( 1, pixelWidth - shift ); i < pixelWidth; i++ )
                writer.setPixels( i, 0, 1, pixelHeight, pixelFormat, blank[timeAxisOffset^(i%2)], 0, 1 );
        }

        /**
         * Контроллер сдвига временной шкалы.
         */
        private class Adopter implements Runnable
        {
            private final long moment;

            Adopter( long moment )
            {
                this.moment = moment;
            }

            @Override
            public void run()
            {
                int shift = timeConvertor.reset( widthProperty().doubleValue(), moment );//timeConvertor.timeToImageShift( moment );
                if( shift > 0 )
                {
    //                System.out.println( "time shift=" +(moment -timeConvertor.getEntry()));
    //                System.out.println( "area shift=" +(shift));
    //                System.out.println( "time shift adj=" +(timeConvertor.imageToTimeShift(shift)));
                    // подправить расчет
                    //timeConvertor.reset( widthProperty().doubleValue(), moment );
                    // смещение всей зоны; +-1 для сохранения оси значений
                    shiftImage( shift, writableImageProperty.get() );
                }
            }

        }
    }

}
