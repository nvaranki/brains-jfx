package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.jfx.ChangedTrigger;
import com.varankin.brains.jfx.IntegerConverter;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.brains.jfx.shared.AutoComboBoxSelector;
import com.varankin.characteristic.*;
import com.varankin.util.LoggerX;
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
 * @author &copy; 2016 Николай Варанкин
 */
public final class ObservableConversionPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableConversionPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableConversionPane.css";
    private static final String CSS_CLASS = "observable-conversion-pane";

    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final SingleSelectionProperty<RatedObservable> parameterProperty;
    private final SingleSelectionProperty<Ранжируемый> convertorProperty;
    private final ObjectProperty<Integer> bufferProperty;
    private final ReadOnlyBooleanWrapper validProperty;
    private final BooleanProperty changedProperty;
    private final ChangedTrigger changedFunction;

    private BooleanBinding changedBinding;
    
    private AutoComboBoxSelector<Ранжируемый> convertorAutoSelector;

    @FXML private ComboBox<RatedObservable> parameter;
    @FXML private ComboBox<Ранжируемый> convertor;
    @FXML private TextField buffer;

    public ObservableConversionPaneController()
    {
        parameterProperty = new SingleSelectionProperty<>();
        convertorProperty = new SingleSelectionProperty<>();
        bufferProperty = new SimpleObjectProperty<>();
        validProperty = new ReadOnlyBooleanWrapper();
        changedProperty = new SimpleBooleanProperty();
        changedFunction = new ChangedTrigger();
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
        
        buffer = new TextField();
        buffer.setId( "buffer" );
        buffer.setFocusTraversable( true );
        buffer.setPrefColumnCount( 6 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.conversion.parameter" ) ), 0, 0 );
        pane.add( parameter, 1, 0 );
        pane.add( new Label( LOGGER.text( "observable.setup.conversion.convertor" ) ), 0, 1 );
        pane.add( convertor, 1, 1 );
        pane.add( new Label( LOGGER.text( "observable.setup.buffer.name" ) ), 0, 2 );
        pane.add( buffer, 1, 2 );
        
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
        convertor.itemsProperty().bind( new ConvertorBinding() ); // <-- parameterProperty
        Bindings.bindBidirectional( buffer.textProperty(), bufferProperty, new IntegerConverter( buffer ) );
        IntegerBinding bpb = Bindings.createIntegerBinding( () ->
            {
                Integer value = bufferProperty.getValue();
                return value != null ? value : Integer.MIN_VALUE;
            }, 
            bufferProperty );
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.and( 
                    Bindings.isNotNull( parameterProperty ), 
                    Bindings.isNotNull( convertorProperty ) ),
                Bindings.greaterThanOrEqual( bpb, 0 ) );
        validProperty.bind( validBinding );

        changedProperty.bind( changedBinding = Bindings.createBooleanBinding( changedFunction, 
                parameterProperty,
                convertorProperty,
                bufferProperty ) );
    }
    
    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }
    
    BooleanProperty changedProperty()
    {
        return changedProperty;
    }

    void copyOptions( Value value )
    {
        parameter.getItems().clear();
        parameter.getItems().addAll( value.observables );
        SingleSelectionModel<RatedObservable> psm = parameter.getSelectionModel();
        psm.select( value.observableProperty().getValue() );
        if( psm.getSelectedItem() == null )
            psm.selectFirst();
        SingleSelectionModel<Ранжируемый> csm = convertor.getSelectionModel();
        csm.select( value.convertorProperty().getValue() );
        if( csm.getSelectedItem() == null )
            csm.selectFirst();
        bufferProperty.setValue( value.bufferProperty().getValue() );
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    void applyOptions( Value value )
    {
        value.observableProperty().setValue( parameterProperty.getValue() );
        value.convertorProperty().setValue( convertorProperty.getValue() );
        value.bufferProperty().setValue( bufferProperty.getValue() );
        changedFunction.setValue( false );
        changedBinding.invalidate();
    }

    //<editor-fold defaultstate="collapsed" desc="классы">
    
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
    
    //</editor-fold>
    
}
