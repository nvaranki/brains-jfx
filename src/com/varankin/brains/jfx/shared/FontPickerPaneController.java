package com.varankin.brains.jfx.shared;

import com.varankin.brains.jfx.*;
import com.varankin.util.LoggerX;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров шрифта.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class FontPickerPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( FontPickerPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/shared/FontPickerPane.css";
    private static final String CSS_CLASS = "font-picker-pane";
    private static final String STYLE_SEPARATOR = " "; //TODO ?
    
    private final ObjectProperty<Font> fontProperty;
    private final ObjectProperty<Double> sizeProperty;
    private final SingleSelectionProperty<String> familyProperty;
    private final SingleSelectionProperty<FontWeight> weightProperty;
    private final SingleSelectionProperty<FontPosture> postureProperty;    
    private final ChangeListener<Font> fontListener;

    @FXML private TextField size;
    @FXML private ComboBox<String> family;
    @FXML private ComboBox<FontWeight> weight;
    @FXML private ComboBox<FontPosture> posture;
    @FXML private Label sample;

    public FontPickerPaneController()
    {
        fontProperty = new SimpleObjectProperty<Font>( this, "font" )
        {
            @Override
            public void setValue( Font f )
            {
                if( f != null )
                {
                    familyProperty.setValue( f.getFamily() );
                    sizeProperty.setValue( f.getSize() );
                    String style = f.getStyle();
                    weightProperty.setValue( findWeightByStyle( style ) );
                    postureProperty.setValue( findPostureByStyle( style ) );
                }
                else
                {
                    familyProperty.setValue( null );
                    sizeProperty.setValue( null );
                    weightProperty.setValue( null );
                    postureProperty.setValue( null );
                }
            }
        };
        familyProperty = new SingleSelectionProperty<>();
        weightProperty = new SingleSelectionProperty<>();
        postureProperty = new SingleSelectionProperty<>();
        sizeProperty = new SimpleObjectProperty<>( this, "size" );
        
        fontProperty.bind( Bindings.createObjectBinding( new FontFinder(), 
            familyProperty, sizeProperty, weightProperty, postureProperty ) );
        fontProperty.addListener( new WeakChangeListener<>( fontListener = new SampleUpdater() ) );
    }
    
    /**
     * Создает панель для выбора и установки параметров шрифта.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public Pane build()
    {
        family = new ComboBox<>();
        family.setId( "family" );
        
        size = new TextField();
        size.setId( "size" );
        size.setPrefColumnCount( 4 );
        
        weight = new ComboBox<>();
        weight.setId( "weight" );
        
        posture = new ComboBox<>();
        posture.setId( "posture" );
        
        sample = new Label();
        sample.setId( "sample" );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.font.family" ) ), 0, 0 );
        pane.add( family, 1, 0, 3, 1 );
        pane.add( new Label( LOGGER.text( "properties.font.weight" ) ), 0, 1 );
        pane.add( weight, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.font.posture" ) ), 2, 1 );
        pane.add( posture, 3, 1 );
        pane.add( new Label( LOGGER.text( "properties.font.size" ) ), 0, 2 );
        pane.add( size, 1, 2 );
        pane.add( new Label( LOGGER.text( "properties.font.sample" ) ), 2, 2 );
        pane.add( sample, 3, 2 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        family.getItems().addAll( Font.getFamilies() );
        weight.getItems().addAll( FontWeight.values() );
        posture.getItems().addAll( FontPosture.values() );
        
        Bindings.bindBidirectional( size.textProperty(), sizeProperty, 
                new PositiveDoubleConverter( size ) );
        
        familyProperty.setModel( family.getSelectionModel() );
        weightProperty.setModel( weight.getSelectionModel() );
        postureProperty.setModel( posture.getSelectionModel() );
        
        sample.setText( "-12345.6789" );
    }
    
    public Property<Font> fontProperty()
    {
        return fontProperty;
    }
    
    public void setSample( String text )
    {
        sample.setText( text );
    }
    
    private FontWeight findWeightByStyle( String style )
    {
        if( style != null )
            for( String name : style.split( STYLE_SEPARATOR ) )
            {
                FontWeight w = FontWeight.findByName( name );
                if( w != null ) return w;
            }
        return FontWeight.NORMAL;
    }

    private FontPosture findPostureByStyle( String style )
    {
        if( style != null )
            for( String name : style.split( STYLE_SEPARATOR ) )
            {
                FontPosture w = FontPosture.findByName( name );
                if( w != null ) return w;
            }
        return FontPosture.REGULAR;
    }

    private class FontFinder implements Callable<Font>
    {
        @Override
        public Font call() throws Exception
        {
            return Font.font( 
                familyProperty.getValue(), 
                weightProperty.getValue(), 
                postureProperty.getValue(), 
                sizeProperty.getValue() );
        }
    }
    
    private class SampleUpdater implements ChangeListener<Font>
    {
        @Override
        public void changed( ObservableValue<? extends Font> observable, 
                            Font oldValue, Font newValue )
        {
            if( newValue != null ) sample.setFont( newValue );
        }
    }
    
}
