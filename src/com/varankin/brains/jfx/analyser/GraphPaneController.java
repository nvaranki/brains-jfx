package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    
    private final long id;
    private final DoubleProperty widthProperty, heightProperty;
    private final ReadOnlyObjectWrapper<WritableImage> writableImageProperty;
    private final SimpleBooleanProperty dynamicProperty;
    private final ObjectProperty<Long> rateValueProperty;
    private final ObjectProperty<TimeUnit> rateUnitProperty;
    private final BooleanProperty borderDisplayProperty, zeroDisplayProperty;
    private final ObjectProperty<Color> borderColorProperty, zeroColorProperty;
    private final ImageChanger imageChanger;
    private final DynamicSwitch dynamicSwitch;
    private final DynamicParametersChanger dynamicChanger;

    private TimeConvertor timeConvertor;
    private ValueConvertor valueConvertor;
    private GraphPropertiesStage properties;
    
    @FXML private Pane pane;
    @FXML private ImageView imageView;
    @FXML private MenuItem menuItemResume;
    @FXML private MenuItem menuItemStop;
    @FXML private MenuItem menuItemProperties;
    @FXML private ContextMenu popup;
    
    public GraphPaneController()
    {
        id = ++idCounter;

        //TODO appl params
        widthProperty = new SimpleDoubleProperty();
        heightProperty = new SimpleDoubleProperty();
        writableImageProperty = new ReadOnlyObjectWrapper<>();
        
        rateValueProperty = new SimpleObjectProperty<>( 100L );
        rateUnitProperty = new SimpleObjectProperty<>( TimeUnit.MILLISECONDS );
        borderDisplayProperty = new SimpleBooleanProperty( false );
        borderColorProperty = new SimpleObjectProperty<>( Color.GRAY );
        zeroDisplayProperty = new SimpleBooleanProperty( true );
        zeroColorProperty = new SimpleObjectProperty<>( Color.GRAY );
        
        imageChanger = new ImageChanger();
        widthProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        heightProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        borderDisplayProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        borderColorProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        zeroDisplayProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        zeroColorProperty.addListener( new WeakChangeListener<>( imageChanger ) );
        
        dynamicProperty = new SimpleBooleanProperty( false );
        dynamicSwitch = new DynamicSwitch();
        dynamicChanger = new DynamicParametersChanger();
        dynamicProperty.addListener( new WeakChangeListener<>( dynamicSwitch ) );
        rateValueProperty.addListener( new WeakChangeListener<>( dynamicChanger ) );
        rateUnitProperty.addListener( new WeakChangeListener<>( dynamicChanger ) );
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
        imageView.setMouseTransparent( true );

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
        
        menuItemResume.disableProperty().bind( dynamicProperty );
        menuItemStop.disableProperty().bind( Bindings.not( dynamicProperty ) );
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
            // заменить, т.к. другие размеры или орнамент
            WritableImage newWritableImage = new WritableImage( width, height );
            // нарисовать оси 
            drawAxes( newWritableImage.getPixelWriter(), width, height );
            drawBorders( newWritableImage.getPixelWriter(), width, height );
            dynamicSwitch.imageFlowService.snapShotBlankPatterns( newWritableImage, height, 2, 1, 0 );
            // готово
            writableImageProperty.setValue( newWritableImage );
        }
    }
    
    private void drawBorders( PixelWriter writer, int width, int height )
    {
        if( borderDisplayProperty.get() )
        {
            Color color = borderColorProperty.getValue();
            for( int i = 0; i < width; i++ )
            {
                writer.setColor( i, 0, color );
                writer.setColor( i, height - 1, color );
            }
            for( int i = 0; i < height; i++ )
            {
                writer.setColor( 0, i, color );
                writer.setColor( width - 1, i, color );
            }
        }
    }
    
    private void drawAxes( PixelWriter writer, int width, int height )
    {
        int zero = valueConvertor.valueToImage( 0.0F );
        if( zeroDisplayProperty.get() && 0 < zero && zero < heightProperty.intValue() )
        {
            Color color = zeroColorProperty.getValue();
            for( int i = 2; i < width; i += 2 ) 
                writer.setColor( i, zero, color ); // zero value
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="классы">
    
    /**
     * Сервис движения временной шкалы.
     */
    private class ImageFlowService implements Runnable
    {
        final WritablePixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
        
        int[][] blank = new int[3][];
        int timeAxisOffset;
        
        @Override
        public void run()
        {
            Platform.runLater( new Adopter( System.currentTimeMillis() ) );
        }
        
        private void snapShotBlankPatterns( WritableImage writableImage, int height, int... xs )
        {
            blank = new int[xs.length][];
            PixelReader reader = writableImage.getPixelReader();
            for( int i = 0; i < blank.length; i++ )
                reader.getPixels( xs[i], 0, 1, height, pixelFormat, blank[i] = new int[height], 0, 1 );
            timeAxisOffset = 0;
        }
        
        private void shiftImage( int shift )
        {
            WritableImage image = writableImageProperty.get();
            PixelWriter writer = image.getPixelWriter();
            int pixelWidth = widthProperty.intValue();
            int pixelHeight = heightProperty.intValue();
            int border = borderDisplayProperty.get() ? 1 : 0;
            int width = pixelWidth - shift - border*2;
            if( width > 0 )
                writer.setPixels( border, 0, width, pixelHeight, image.getPixelReader(), border + shift, 0 );
            // очистить справа от скопированной зоны
            timeAxisOffset += shift;
            timeAxisOffset %= 2;
            for( int r = pixelWidth - border, i = Math.max( border, r - shift ); i < r; i++ )
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
                int shift = timeConvertor.reset( widthProperty().doubleValue(), moment );
                if( shift > 0 )
                    shiftImage( shift );
            }
            
        }
    }
    
    /**
     * Контроллер замены графической зоны.
     */
    private class ImageChanger implements ChangeListener
    {
        @Override
        public void changed( ObservableValue observable, Object oldValue, Object newValue )
        {
            replaceImage( widthProperty.intValue(), heightProperty.intValue() );
        }
    }
    
    /**
     * Контроллер параметров движения графической зоны.
     */
    private class DynamicParametersChanger implements ChangeListener
    {
        @Override
        public void changed( ObservableValue observable, Object oldValue, Object newValue )
        {
            if( newValue != null )
            {
                // остановить движение графической зоны
                dynamicProperty.setValue( Boolean.FALSE );
                // возобновить движение графической зоны с новыми параметрами
                dynamicProperty.setValue( Boolean.TRUE );
            }
            else
            {
                // остановить движение графической зоны
                dynamicProperty.setValue( Boolean.FALSE );
            }
        }
    }
    
    /**
     * Контроллер движения графической зоны.
     */
    private class DynamicSwitch implements ChangeListener<Boolean>
    {
        final ImageFlowService imageFlowService = new ImageFlowService();
        Future<?> process;
        
        @Override
        public void changed( ObservableValue<? extends Boolean> observable,
        Boolean oldValue, Boolean newValue )
        {
            if( newValue != null )
                if( newValue )
                {
                    // возобновить движение графической зоны
                    process = JavaFX.getInstance().getScheduledExecutorService().scheduleAtFixedRate(
                            imageFlowService, 0L, rateValueProperty.getValue(), rateUnitProperty.getValue() );
                }
                else
                {
                    // остановить движение графической зоны
                    if( process != null )
                        process.cancel( true );
                }
        }
    }
    
    //</editor-fold>
    
}
