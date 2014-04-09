package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
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
    
    private final ObjectProperty<Color> colorProperty;
    private final ObjectProperty<int[][]> patternProperty;
    private final ColorPropertyChangeListener colorPropertyChangeListener;
    private final PatternPropertyChangeListener patternPropertyChangeListener;

    private ValuePropertiesStage properties;
    
    @FXML private CheckBox legend;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemShow;
    @FXML private MenuItem menuItemHide;
    @FXML private MenuItem menuItemRemove;
    @FXML private MenuItem menuItemProperties;

    public LegendValueController()
    {
        colorProperty = new SimpleObjectProperty<>();
        patternProperty = new SimpleObjectProperty<>();
        colorPropertyChangeListener = new ColorPropertyChangeListener();
        patternPropertyChangeListener = new PatternPropertyChangeListener();
        colorProperty.addListener( new WeakChangeListener<>( colorPropertyChangeListener ) );
        patternProperty.addListener( new WeakChangeListener<>( patternPropertyChangeListener ) );
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

    ContextMenu getContextMenu() 
    {
        return popup;
    }

    @FXML
    private void onActionShow( ActionEvent __ )
    {
        legend.selectedProperty().setValue( Boolean.TRUE );
    }
    
    @FXML
    private void onActionHide( ActionEvent __ )
    {
        legend.selectedProperty().setValue( Boolean.FALSE );
    }
    
    @FXML
    private void onActionRemove( ActionEvent __ )
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
    private void onActionProperties( ActionEvent __ )
    {
        if( properties == null )
        {
            properties = new ValuePropertiesStage();
            properties.initModality( Modality.NONE );
            properties.initOwner( JavaFX.getInstance().платформа );
            properties.setTitle( LOGGER.text( "properties.value.title", legend.getText() ) );
            ValuePropertiesController controller = properties.getController();
            controller.bindColorProperty( colorProperty );
            controller.bindPatternProperty( patternProperty );
            controller.bindScaleProperty( new SimpleObjectProperty( 3 ) );
        }
        properties.show();
        properties.toFront();
    }
        
    /**
     * @return свойство "цвет рисования шаблона".
     */
    final Property<Color> colorProperty()
    {
        return colorProperty;
    }

    /**
     * @return свойство "шаблон для рисования как массив точек (x,y)".
     */
    final Property<int[][]> patternProperty()
    {
        return patternProperty;
    }

    BooleanProperty selectedProperty()
    {
        return legend.selectedProperty();
    }
    
    private class ColorPropertyChangeListener implements ChangeListener<Color>
    {
        @Override
        public void changed( ObservableValue<? extends Color> observable, 
                            Color oldValue, Color newValue )
        {
            resample( newValue, patternProperty.getValue() );
        }
    }

    private class PatternPropertyChangeListener implements ChangeListener<int[][]>
    {
        @Override
        public void changed( ObservableValue<? extends int[][]> observable, 
                            int[][] oldValue, int[][] newValue )
        {
            resample( colorProperty.getValue(), newValue );
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
