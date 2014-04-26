package com.varankin.brains.jfx.analyser;

import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
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

    private final ReadOnlyStringWrapper titleProperty;
    private final ReadOnlyIntegerWrapper bufferProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    //TODO DEBUG START
    @Deprecated int count = 0;
    //TODO DEBUG END

    public ObservableMiscPaneController()
    {
        titleProperty = new ReadOnlyStringWrapper(
        //TODO DEBUG START
                "DEBUG"
        //TODO DEBUG END
        );
        bufferProperty = new ReadOnlyIntegerWrapper(
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
        
        GridPane pane = new GridPane();
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.greaterThanOrEqual( bufferProperty, 0 ), 
                Bindings.isNotEmpty( titleProperty ) );
        validProperty.bind( validBinding );
    }
    
    ReadOnlyProperty<String> titleProperty()
    {
        return titleProperty.getReadOnlyProperty();
    }

    ReadOnlyProperty<Number> bufferProperty()
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
        titleProperty.setValue( value.getClass().getSimpleName() + count++ );
        //TODO DEBUG END
    }

}
