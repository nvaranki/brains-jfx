package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Builder;

/**
 * FXML-контроллер элемента управления прорисовкой отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class LegendValueController implements Builder<CheckBox>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LegendValueController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/LegendValue.css";
    private static final String CSS_CLASS = "legend-value";

    static final String RESOURCE_FXML = "/fxml/analyser/LegendValue.fxml";
    static ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ColorPropertyChangeListener colorPropertyChangeListener;
    private final PatternPropertyChangeListener patternPropertyChangeListener;
    private final SelectedPropertyChangeListener selectedPropertyChangeListener;

    private DotPainter painter;
    private ValuePropertiesStage properties;
    
    @FXML private CheckBox legend;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemShow;
    @FXML private MenuItem menuItemHide;
    @FXML private MenuItem menuItemRemove;
    @FXML private MenuItem menuItemProperties;

    public LegendValueController()
    {
        colorPropertyChangeListener = new ColorPropertyChangeListener();
        patternPropertyChangeListener = new PatternPropertyChangeListener();
        selectedPropertyChangeListener = new SelectedPropertyChangeListener();
    }

    /**
     * Создает элемент управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public CheckBox build()
    {
        menuItemShow = new MenuItem();
        menuItemShow.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionShow( event );
            }
        } );
        
        menuItemHide = new MenuItem();
        menuItemHide.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionHide( event );
            }
        } );
        
        menuItemRemove = new MenuItem();
        menuItemRemove.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionRemove( event );
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
        popup.setId( "popup" );
        popup.getItems().addAll( menuItemShow, menuItemHide, menuItemRemove, menuItemProperties );

        legend = new CheckBox();
        legend.setId( "legend" );
        legend.setSelected( false );
        legend.setContextMenu( popup );
        
        legend.getStyleClass().add( CSS_CLASS );
        legend.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return legend;
    }
    
    @FXML
    protected void initialize()
    {
        legend.selectedProperty().addListener( new WeakChangeListener<>( selectedPropertyChangeListener ) );
        
        menuItemShow.textProperty().bind( new TextWithName( "control.popup.show" ) );
        menuItemShow.disableProperty().bind( legend.selectedProperty() );

        menuItemHide.textProperty().bind( new TextWithName( "control.popup.hide" ) );
        menuItemHide.disableProperty().bind( Bindings.not( legend.selectedProperty() ) );
        
        menuItemRemove.textProperty().bind( new TextWithName( "control.popup.remove" ) );
        
        menuItemProperties.setGraphic( JavaFX.icon( "icons16x16/properties.png" ) );
    }
    
    private void resample( Color color, int[][] pattern )
    {
        if( color != null && pattern != null )
        {
            Color outlineColor = Color.LIGHTGRAY;
            WritableImage sample = new WritableImage( 16, 16 );
            PixelWriter pixelWriter = sample.getPixelWriter();
            for( int i = 1; i < 15; i ++ )
            {
                pixelWriter.setColor( i,  0, outlineColor );
                pixelWriter.setColor( i, 15, outlineColor );
                pixelWriter.setColor(  0, i, outlineColor );
                pixelWriter.setColor( 15, i, outlineColor );
            }
            DotPainter.paint( 7, 7, color, pattern, pixelWriter, 16, 16 );
            legend.setGraphic( new ImageView( sample ) );
        }
    }

    void setPainter( DotPainter painter )
    {
        this.painter = painter;
        
        painter.colorProperty().addListener( new WeakChangeListener<>( colorPropertyChangeListener ) );
        painter.patternProperty().addListener( new WeakChangeListener<>( patternPropertyChangeListener ) );
        
        resample( painter.colorProperty().getValue(), painter.patternProperty().getValue() );
    }
    
    ContextMenu getContextMenu() 
    {
        return popup;
    }

    @FXML
    private void onActionShow( ActionEvent _ )
    {
        legend.selectedProperty().setValue( Boolean.TRUE );
    }
    
    @FXML
    private void onActionHide( ActionEvent _ )
    {
        legend.selectedProperty().setValue( Boolean.FALSE );
    }
    
    @FXML
    private void onActionRemove( ActionEvent _ )
    {
        // остановить прорисовку
        legend.selectedProperty().setValue( Boolean.FALSE );
        // убрать с экрана
        Parent parent = legend.getParent();
        if( parent instanceof Pane )
            ((Pane)parent).getChildren().remove( legend );
        else
            LOGGER.log( "001002002W", legend.getText() );
        // TODO what to do with open queue?
    }
        
    @FXML
    private void onActionProperties( ActionEvent _ )
    {
        if( properties == null )
        {
            properties = new ValuePropertiesStage();
            properties.initModality( Modality.NONE );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.setTitle( LOGGER.text( "properties.value.title", legend.getText() ) );
            ValuePropertiesController controller = properties.getController();
            controller.bindColorProperty( painter.colorProperty() );
            controller.bindPatternProperty( painter.patternProperty() );
            controller.bindScaleProperty( new SimpleObjectProperty( 3 ) );
        }
        properties.show();
        properties.toFront();
    }
        
    private class ColorPropertyChangeListener implements ChangeListener<Color>
    {
        @Override
        public void changed( ObservableValue<? extends Color> observable, 
                            Color oldValue, Color newValue )
        {
            resample( newValue, painter.patternProperty().getValue() );
        }
    }

    private class PatternPropertyChangeListener implements ChangeListener<int[][]>
    {
        @Override
        public void changed( ObservableValue<? extends int[][]> observable, 
                            int[][] oldValue, int[][] newValue )
        {
            resample( painter.colorProperty().getValue(), newValue );
        }
    }
        
    private class SelectedPropertyChangeListener implements ChangeListener<Boolean>
    {
        private Future<?> process;
        
        @Override
        public void changed( ObservableValue<? extends Boolean> observable, 
                            Boolean oldValue, Boolean newValue )
        {
            if( newValue != null && newValue )
            {
                // запустить прорисовку отметок
                process = JavaFX.getInstance().getExecutorService().submit( painter );
            }
            else if( process != null )
            {
                // остановить прорисовку отметок
                process.cancel( true );
            }
        }
    }
        
    private class TextWithName extends StringBinding
    {
        final String msg;
        
        TextWithName( String msg )
        {
            super.bind( legend.textProperty() );
            this.msg = msg;
        }

        @Override
        protected String computeValue()
        {
            return LOGGER.text( msg, legend.textProperty().getValue() );
        }
        
    }

}
