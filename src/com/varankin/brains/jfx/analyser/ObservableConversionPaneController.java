package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.Ранжировщик;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.brains.jfx.shared.AutoComboBoxSelector;
import com.varankin.characteristic.Именованный;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;
import javafx.util.Callback;

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

    private final SingleSelectionProperty<Observable> parameterProperty;
    private final SingleSelectionProperty<Ранжировщик> convertorProperty;
    private final ReadOnlyBooleanWrapper validProperty;
    
    private AutoComboBoxSelector<Ранжировщик> convertorAutoSelector;

    @FXML private ComboBox<Observable> parameter;
    @FXML private ComboBox<Ранжировщик> convertor;

    public ObservableConversionPaneController()
    {
        parameterProperty = new SingleSelectionProperty<>();
        convertorProperty = new SingleSelectionProperty<>();
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
        parameter.setEditable( false );
        parameter.setId( "property" );
        parameter.setFocusTraversable( true );
        parameter.setVisibleRowCount( 5 );
        
        convertor = new ComboBox<>();
        convertor.setEditable( false );
        convertor.setId( "convertor" );
        convertor.setFocusTraversable( true );
        convertor.setVisibleRowCount( 5 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.conversion.parameter" ) ), 0, 0 );
        pane.add( parameter, 1, 0 );
        pane.add( new Label( LOGGER.text( "observable.setup.conversion.convertor" ) ), 0, 1 );
        pane.add( convertor, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        parameterProperty.setModel( parameter.getSelectionModel() );
        convertorProperty.setModel( convertor.getSelectionModel() );
        ObservableCellFactory ocf = new ObservableCellFactory();
        parameter.setCellFactory( ocf );
        parameter.setButtonCell( ocf.call( null ) );
        ConvertorCellFactory ccf = new ConvertorCellFactory();
        convertor.setCellFactory( ccf );
        convertor.setButtonCell( ccf.call( null ) );
        convertorAutoSelector = new AutoComboBoxSelector<>( convertor, 0 );
        convertor.itemsProperty().addListener( new WeakChangeListener<>( convertorAutoSelector ) );
        convertor.itemsProperty().bind( new ConvertorBinding() );
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.isNotNull( parameterProperty ), 
                Bindings.isNotNull( convertorProperty ) );
        validProperty.bind( validBinding );
    }
    
    ReadOnlyProperty<Observable> parameterProperty()
    {
        return parameterProperty;
    }

    ReadOnlyProperty<Ранжировщик> convertorProperty()
    {
        return convertorProperty;
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
        convertor.getItems().clear();
        parameter.selectionModelProperty().getValue().clearSelection();
        convertor.selectionModelProperty().getValue().clearSelection();
        parameter.getItems().addAll( Observable.observables( monitor ) );
        if( !parameter.getItems().isEmpty() )
            parameter.selectionModelProperty().getValue().select( 0 );
    }

    /**
     * Генератор списка доступных вариантов конвертеров.
     */
    private class ConvertorBinding extends ListBinding<Ранжировщик>
    {

        ConvertorBinding()
        {
            bind( parameterProperty );
        }
        
        @Override
        protected ObservableList<Ранжировщик> computeValue()
        {
            ObservableList<Ранжировщик> list = FXCollections.observableArrayList();
            Observable observable = parameterProperty.getValue();
            if( observable != null ) 
                list.addAll( observable.РАНЖИРОВЩИКИ );
            return list;
        }
        
    }
    
    private static class ObservableCellFactory 
        implements Callback<ListView<Observable>,ListCell<Observable>>
    {
        @Override
        public ListCell<Observable> call( final ListView<Observable> param )
        {
            return new ListCell<Observable>()
            {
                @Override
                protected void updateItem( Observable item, boolean empty )
                {
                    // calling super here is very important - don't skip this!
                    super.updateItem( item, empty );
                    setText( empty || item == null ? null : item.НАЗВАНИЕ );
                }
            };
        }       
    }
    
    private static class ConvertorCellFactory 
        implements Callback<ListView<Ранжировщик>,ListCell<Ранжировщик>>
    {
        @Override
        public ListCell<Ранжировщик> call( final ListView<Ранжировщик> param )
        {
            return new ListCell<Ранжировщик>()
            {
                @Override
                protected void updateItem( Ранжировщик item, boolean empty )
                {
                    // calling super here is very important - don't skip this!
                    super.updateItem( item, empty );
                    
                    if( empty || item == null )
                    {
                        setText( null );
                    }
                    else if( item instanceof Именованный )
                    {
                        setText( ((Именованный)item).название() );
                    }
                    else
                    {
                        setText( item.getClass().getName() );
                    }
                }
            };
        }
    }

}
