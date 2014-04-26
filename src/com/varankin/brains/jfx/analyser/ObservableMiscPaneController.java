package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.IntegerConverter;
import com.varankin.brains.jfx.PositiveIntegerConverter;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров отображения наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ObservableMiscPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableMiscPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableMiscPane.css";
    private static final String CSS_CLASS = "observable-misc-pane";

    private final ReadOnlyObjectWrapper<Integer> bufferProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    //TODO DEBUG START
    @Deprecated int count = 0;
    //TODO DEBUG END

    @FXML private TextField title;
    @FXML private TextField buffer;
    
    public ObservableMiscPaneController()
    {
        bufferProperty = new ReadOnlyObjectWrapper<>(
        //TODO DEBUG START
                1000
        //TODO DEBUG END
        );
        validProperty = new ReadOnlyBooleanWrapper();
    }

    /**
     * Создает панель для выбора и установки параметров отображения наблюдаемого значения.
     * Применяется в конфигурации без FXML.
     * 
     * @return созданная панель.
     */
    @Override
    public Pane build()
    {
        title = new TextField(
        //TODO DEBUG START
                "DEBUG"
        //TODO DEBUG END
        );
        title.setId( "title" );
        title.setFocusTraversable( true );
        title.setPrefColumnCount( 25 );
        
        buffer = new TextField(
        //TODO DEBUG START
                "1000"
        //TODO DEBUG END
        );
        buffer.setId( "buffer" );
        buffer.setFocusTraversable( true );
        buffer.setPrefColumnCount( 6 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.value.name" ) ), 0, 0 );
        pane.add( title, 1, 0 );
        pane.add( new Label( LOGGER.text( "observable.setup.buffer.name" ) ), 0, 1 );
        pane.add( buffer, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        Bindings.bindBidirectional( buffer.textProperty(), bufferProperty, 
                new IntegerConverter( buffer ) );
        IntegerBinding bpb = Bindings.createIntegerBinding( () ->
            {
                Integer value = bufferProperty.getValue();
                return value != null ? value : Integer.MIN_VALUE;
            }, 
            bufferProperty );
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.greaterThanOrEqual( bpb, 0 ), 
                Bindings.isNotEmpty( title.textProperty() ) );
        validProperty.bind( validBinding );
    }
    
    ReadOnlyProperty<String> titleProperty()
    {
        return title.textProperty();
    }

    ReadOnlyProperty<Integer> bufferProperty()
    {
        return bufferProperty.getReadOnlyProperty();
    }

    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }

    /**
     * Устанавливает монитор наблюдаемого значения.
     * 
     * @param value монитор.
     */
    void setMonitor( PropertyMonitor value )
    {
//        monitor = value;
        //TODO DEBUG START
        title.textProperty().setValue( value.getClass().getSimpleName() + count++ );
        //TODO DEBUG END
    }

}
