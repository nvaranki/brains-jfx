package com.varankin.brains.jfx.analyser;

import com.varankin.brains.appl.RatedObservable;
import com.varankin.brains.artificial.rating.КаталогРанжировщиков;
import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.factory.observable.wrapped.НаблюдаемыйЭлемент;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.brains.jfx.shared.AutoComboBoxSelector;
import com.varankin.characteristic.Именованный;
import com.varankin.characteristic.НаблюдаемоеСвойство;
import com.varankin.characteristic.Свойственный;
import com.varankin.characteristic.Свойство;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
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

    private final SingleSelectionProperty<RatedObservable> parameterProperty;
    private final SingleSelectionProperty<Ранжируемый> convertorProperty;
    private final ReadOnlyBooleanWrapper validProperty;
    
    private AutoComboBoxSelector<Ранжируемый> convertorAutoSelector;

    @FXML private ComboBox<RatedObservable> parameter;
    @FXML private ComboBox<Ранжируемый> convertor;

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
    
    ReadOnlyProperty<RatedObservable> parameterProperty()
    {
        return parameterProperty;
    }

    ReadOnlyProperty<Ранжируемый> convertorProperty()
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
    void setMonitor( НаблюдаемыйЭлемент monitor )
    {
        parameter.getItems().clear();
        convertor.getItems().clear();
        parameter.selectionModelProperty().getValue().clearSelection();
        convertor.selectionModelProperty().getValue().clearSelection();
        parameter.getItems().addAll( observables( monitor ) );
        if( !parameter.getItems().isEmpty() )
            parameter.selectionModelProperty().getValue().select( 0 );
    }

    /**
     * Генератор списка доступных вариантов конвертеров.
     */
    private class ConvertorBinding extends ListBinding<Ранжируемый>
    {

        ConvertorBinding()
        {
            bind( parameterProperty );
        }
        
        @Override
        protected ObservableList<Ранжируемый> computeValue()
        {
            ObservableList<Ранжируемый> list = FXCollections.observableArrayList();
            RatedObservable observable = parameterProperty.getValue();
            if( observable != null ) 
                list.addAll( observable.ранжировщики() );
            return list;
        }
        
    }
    
    private static class ObservableCellFactory 
        implements Callback<ListView<RatedObservable>,ListCell<RatedObservable>>
    {
        @Override
        public ListCell<RatedObservable> call( final ListView<RatedObservable> param )
        {
            return new ListCell<RatedObservable>()
            {
                @Override
                protected void updateItem( RatedObservable item, boolean empty )
                {
                    // calling super here is very important - don't skip this!
                    super.updateItem( item, empty );
                    setText( empty || item == null ? null : item.название() );
                }
            };
        }       
    }
    
    private static class ConvertorCellFactory 
        implements Callback<ListView<Ранжируемый>,ListCell<Ранжируемый>>
    {
        @Override
        public ListCell<Ранжируемый> call( final ListView<Ранжируемый> param )
        {
            return new ListCell<Ранжируемый>()
            {
                @Override
                protected void updateItem( Ранжируемый item, boolean empty )
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

    private static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
   /**
     * Создает список доступных наблюдаемых свойств объекта.
     * 
     * @param object объект.
     * @return список доступных параметров. 
     */
    private static Collection<RatedObservable> observables( Object object )
    {
        Collection<RatedObservable> items = new ArrayList<>();
        if( object instanceof Свойственный )
            for( Map.Entry<String,Свойство> e : ((Свойственный<String>)object).свойства().entrySet() )
            {
                Свойство свойство = e.getValue();
                String index = e.getKey();
                if( свойство instanceof НаблюдаемоеСвойство )
                    items.add( new RatedObservable( (НаблюдаемоеСвойство)свойство, 
                            КаталогРанжировщиков.getInstance().get( index ), RESOURCE_BUNDLE.getString( index ) ) );
            }
/*        
        object = Вложенный.извлечь( НаблюдаемыйЭлемент.class, object );
        
        if( object instanceof НаблюдаемыйПроцесс )
        {
            НаблюдаемыйПроцесс наблюдаемый = (НаблюдаемыйПроцесс)object;
            items.add( new RatedObservable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
        }
        if( object instanceof НаблюдаемыйПроцессор )
        {
            НаблюдаемыйПроцессор наблюдаемый = (НаблюдаемыйПроцессор)object;
            items.add( new RatedObservable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
            items.add( new RatedObservable( наблюдаемый.свойствоПауза(), ПроцессорПауза ) );
            items.add( new RatedObservable( наблюдаемый.свойствоРестарт(), ПроцессорРестарт ) );
            items.add( new RatedObservable( наблюдаемый.свойствоСтратегия(), ПроцессорСтратегия ) );
        }
        if( object instanceof НаблюдаемыйПроект )
        {
            НаблюдаемыйПроект наблюдаемый = (НаблюдаемыйПроект)object;
            items.add( new RatedObservable( наблюдаемый.свойствоСостояние(), ПроцессСостояние ) );
        }
        else if( object instanceof НаблюдаемоеЗначение )
        {
            НаблюдаемоеЗначение наблюдаемый = (НаблюдаемоеЗначение)object;
            items.add( new RatedObservable( наблюдаемый.свойствоВыход(), ЗначениеВыход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемаяВетвь )
        {
            НаблюдаемаяВетвь наблюдаемый = (НаблюдаемаяВетвь)object;
            items.add( new RatedObservable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйАргумент )
        {
            НаблюдаемыйАргумент наблюдаемый = (НаблюдаемыйАргумент)object;
            items.add( new RatedObservable( наблюдаемый.свойствоВход(), АргументВход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоРанг(), АргументРанг ) );
            items.add( new RatedObservable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйЗначимый )
        {
            НаблюдаемыйЗначимый наблюдаемый = (НаблюдаемыйЗначимый)object;
            items.add( new RatedObservable( наблюдаемый.свойствоЗначение(), ЗначимыйЗначение ) );
        }
        else if( object instanceof НаблюдаемыйРазветвитель )
        {
            НаблюдаемыйРазветвитель наблюдаемый = (НаблюдаемыйРазветвитель)object;
            items.add( new RatedObservable( наблюдаемый.свойствоВход(), ПриемникВход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоВыход(), ИсточникВыход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоПринято(), ПриемникПринято ) );
            items.add( new RatedObservable( наблюдаемый.свойствоПередано(), ИсточникПередано ) );
        }
        else if( object instanceof НаблюдаемыйПриемник )
        {
            НаблюдаемыйПриемник наблюдаемый = (НаблюдаемыйПриемник)object;
            items.add( new RatedObservable( наблюдаемый.свойствоВход(), ПриемникВход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоПринято(), ПриемникПринято ) );
        }
        else if( object instanceof НаблюдаемыйИсточник )
        {
            НаблюдаемыйИсточник наблюдаемый = (НаблюдаемыйИсточник)object;
            items.add( new RatedObservable( наблюдаемый.свойствоВыход(), ИсточникВыход ) );
            items.add( new RatedObservable( наблюдаемый.свойствоПередано(), ИсточникПередано ) );
        }
        else if( object instanceof НаблюдаемоеПоле )
        {
            //НаблюдаемоеПоле наблюдаемый = (НаблюдаемоеПоле)object;
        }
        else if( object instanceof НаблюдаемыйСенсор )
        {
            //НаблюдаемыйСенсор наблюдаемый = (НаблюдаемыйСенсор)object;
        }
        else if( object instanceof НаблюдаемыйЭлемент )
        {
            //НаблюдаемыйЭлемент наблюдаемый = (НаблюдаемыйЭлемент)object;
        }
        else if( object != null )
        {
            LOGGER.log( Level.FINE, "observable.unknown", object );
        }
*/
        return items;
    }
    
}
