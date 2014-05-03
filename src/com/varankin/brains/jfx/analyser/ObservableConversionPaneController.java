package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Ранжировщик;
import com.varankin.brains.jfx.ObjectBindings;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров конверсии наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ObservableConversionPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableConversionPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableConversionPane.css";
    private static final String CSS_CLASS = "observable-conversion-pane";

    private final SingleSelectionProperty<String> parameterProperty;
    private final ReadOnlyObjectWrapper<Ранжировщик> convertorProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    @FXML private ComboBox<String> parameter;

    public ObservableConversionPaneController()
    {
        parameterProperty = new SingleSelectionProperty<>();
        convertorProperty = new ReadOnlyObjectWrapper<>(
        //TODO DEBUG START
                new Value.РанжировщикImpl()
//                                (Float value, long timestamp) ->
//        {
//            return new Dot( value, timestamp );
//        }
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
        parameter = new ComboBox<>();
        parameter.setEditable( true );
        parameter.setId( "property" );
        parameter.setFocusTraversable( true );
        parameter.setVisibleRowCount( 5 );
        
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.conversion.parameter" ) ), 0, 0 );
        pane.add( parameter, 1, 0 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        parameterProperty.setModel( parameter.getSelectionModel() );
        BooleanBinding validBinding = 
            Bindings.and( 
                ObjectBindings.isNotNull( parameterProperty ), 
                Bindings.isNotNull( convertorProperty ) );
        validProperty.bind( validBinding );
    }
    
    
    ReadOnlyProperty<String> parameterProperty()
    {
        return parameterProperty;
    }

    ReadOnlyProperty<Ранжировщик> convertorProperty()
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
     * @param monitor монитор.
     */
    void setMonitor( PropertyMonitor monitor )
    {
        parameter.getItems().clear();
        parameter.getItems().addAll( suggestParameters( monitor ) );
        parameter.selectionModelProperty().getValue().select( 0 );
//        monitor = value;
    }

    private Collection<String> suggestParameters( PropertyMonitor value )
    {
        List<String> titles = new ArrayList<>();
        
        if( value instanceof Процесс )
        {
            titles.add( Процесс.СОСТОЯНИЕ ); //"Состояние"
        }
        return titles;
    }
    
}