package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.factory.Proxy;
import com.varankin.brains.artificial.rating.КаталогРанжировщиков;
import com.varankin.brains.artificial.Ранжировщик;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.brains.jfx.shared.AutoComboBoxSelector;
import com.varankin.characteristic.Именованный;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

    private final SingleSelectionProperty<String> parameterProperty;
    private final SingleSelectionProperty<Ранжировщик> convertorProperty;
    private final ReadOnlyBooleanWrapper validProperty;
    
    private AutoComboBoxSelector<Ранжировщик> convertorAutoSelector;

    @FXML private ComboBox<String> parameter;
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
        parameter.setEditable( true );
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
        convertorAutoSelector = new AutoComboBoxSelector<>( convertor, 0 );
        convertor.itemsProperty().addListener( new WeakChangeListener<>( convertorAutoSelector ) );
        convertor.itemsProperty().bind( new ConvertorBinding() );
        convertor.setCellFactory( null );
        ConvertorCellFactory ccf = new ConvertorCellFactory();
        convertor.setCellFactory( ccf );
        convertor.setButtonCell( ccf.call( null ) );
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.isNotNull( parameterProperty ), 
                Bindings.isNotNull( convertorProperty ) );
        validProperty.bind( validBinding );
    }
    
    ReadOnlyProperty<String> parameterProperty()
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
        parameter.selectionModelProperty().getValue().clearSelection();
        parameter.getItems().addAll( suggestParameters( monitor ) );
        if( !parameter.getItems().isEmpty() )
            parameter.selectionModelProperty().getValue().select( 0 );
    }

    /**
     * Создает список доступных параметров монитора.
     * 
     * @param value монитор.
     * @return список доступных параметров. 
     */
    private Collection<String> suggestParameters( PropertyMonitor value )
    {
        Collection<String> items = new ArrayList<>();
        
        if( value instanceof Процесс )
        {
            items.add( Процесс.СОСТОЯНИЕ ); //"Состояние"
        }
        else if( value instanceof Proxy )
        {
            Элемент элемент = ((Proxy)value).оригинал();
            if( элемент instanceof Процесс )
            {
                items.add( Процесс.СОСТОЯНИЕ ); //"Состояние"
            }
        }
        return items;
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
            String p = parameterProperty.getValue();
            Collection<Ранжировщик> options = КаталогРанжировщиков.getInstance().get( p );
            if( options != null ) list.addAll( options );
            if( p != null ) list.add( new Value.РанжировщикImpl() );
            return list;
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
