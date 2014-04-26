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
 * FXML-контроллер выбора параметров преобразования наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ObservableConversionPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableConversionPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableConversionPane.css";
    private static final String CSS_CLASS = "observable-conversion-pane";

    private final ReadOnlyStringWrapper propertyProperty;
    private final ReadOnlyObjectWrapper<Value.Convertor<Float>> convertorProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    public ObservableConversionPaneController()
    {
        propertyProperty = new ReadOnlyStringWrapper(
        //TODO DEBUG START
                "DEBUG"
        //TODO DEBUG END
        );
        convertorProperty = new ReadOnlyObjectWrapper<>(
        //TODO DEBUG START
                (Float value, long timestamp) -> new Dot( value, timestamp )
        //TODO DEBUG END
        );
        validProperty = new ReadOnlyBooleanWrapper();
    }

    /**
     * Создает панель для выбора и установки параметров преобразования наблюдаемого значения.
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
                Bindings.isNotEmpty( propertyProperty ), 
                Bindings.isNotNull( convertorProperty ) );
        validProperty.bind( validBinding );
    }
    
    
    ReadOnlyProperty<String> propertyProperty()
    {
        return propertyProperty.getReadOnlyProperty();
    }

    ReadOnlyProperty<Value.Convertor<Float>> convertorProperty()
    {
        return convertorProperty.getReadOnlyProperty();
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
    }

}
