package com.varankin.brains.jfx.analyser;

import com.varankin.brains.appl.RatedObservable;
import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.factory.observable.wrapped.НаблюдаемыйЭлемент;
import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров рисования наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ObservableSetupController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableSetupController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableSetup.css";
    private static final String CSS_CLASS = "observable-setup";
    
    static final String RESOURCE_FXML = "/fxml/analyser/ObservableSetup.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private boolean approved;
    private Iterator<Color> colors = new CyclicIterator<>( Arrays.asList( Color.RED, Color.BLUE, Color.GREEN ) );
    private Iterator<int[][]> patterns = new CyclicIterator<>( Arrays.asList( DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX ) );
    
    @FXML private Button buttonOK, buttonCancel;
    @FXML private ObservableMiscPaneController observableMiscPaneController;
    @FXML private ObservableConversionPaneController observableConversionPaneController;
    @FXML private ValuePropertiesPaneController valuePropertiesPaneController;

    @Override
    public Parent build()
    {

        buttonOK = new Button( LOGGER.text( "button.create" ) );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );

        buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel );

        Tab tabName = new Tab();
        observableMiscPaneController = new ObservableMiscPaneController();
        tabName.setContent( observableMiscPaneController.build() );
        tabName.setText( LOGGER.text( "observable.setup.value.title" ) );
        tabName.setClosable( false );
        
        Tab tabConversion = new Tab();
        observableConversionPaneController = new ObservableConversionPaneController();
        tabConversion.setContent( observableConversionPaneController.build() );
        tabConversion.setText( LOGGER.text( "observable.setup.conversion.title" ) );
        tabConversion.setClosable( false );
        
        valuePropertiesPaneController = new ValuePropertiesPaneController();
        Tab tabPainting = new Tab();
        tabPainting.setContent( valuePropertiesPaneController.build() );
        tabPainting.setText( LOGGER.text( "observable.setup.presentation.title" ) );
        tabPainting.setClosable( false );
        
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll( tabName, tabConversion, tabPainting );

        BorderPane pane = new BorderPane();
        pane.setCenter( tabs );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        valuePropertiesPaneController.scaleProperty().setValue( 3 );
        BooleanBinding valid = 
            Bindings.and( 
                Bindings.and( 
                    observableMiscPaneController.validProperty(),
                    observableConversionPaneController.validProperty() ),
                valuePropertiesPaneController.validProperty() ) ;
        buttonOK.disableProperty().bind( Bindings.not( valid ) );
    }
    
    @FXML
    void onActionOK( ActionEvent event )
    {
        approved = true;
        buttonOK.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonOK.getScene().getWindow().hide();
    }

    boolean isApproved()
    {
        return approved;
    }
    
    void setApproved( boolean value )
    {
        approved = value;
    }

    /**
     * Устанавливает монитор наблюдаемого значения.
     * 
     * @param value монитор.
     */
    void setMonitor( НаблюдаемыйЭлемент value )
    {
        observableMiscPaneController.setMonitor( value );
        observableConversionPaneController.setMonitor( value );
        valuePropertiesPaneController.colorProperty().setValue( colors.next() );
        valuePropertiesPaneController.patternProperty().setValue( patterns.next() );
        valuePropertiesPaneController.resetColorPicker();
    }

    /**
     * Создает новое значение, отображаемое на графике.
     */ 
    Value createValueInstance()
    {
        if( !approved ) return null;

        RatedObservable property = observableConversionPaneController.parameterProperty().getValue();
        Ранжируемый convertor = observableConversionPaneController.convertorProperty().getValue();
        int[][] pattern = valuePropertiesPaneController.patternProperty().getValue();
        Color color = valuePropertiesPaneController.colorProperty().getValue();
        String title = observableMiscPaneController.titleProperty().getValue();
        int buffer = observableMiscPaneController.bufferProperty().getValue();
        BlockingQueue<Dot> queue = new LinkedBlockingQueue<>();
        DotPainter painter = buffer > 0 ? new BufferedDotPainter( queue, buffer ) : new DotPainter( queue );
        return new Value( property.свойство(), convertor, painter, pattern, color, title );
    }
    
    private static class CyclicIterator<E> implements Iterator<E>
    {
        private final Iterable<E> source;
        private Iterator<E> it;

        CyclicIterator( Iterable<E> source )
        {
            this.source = source != null ? source : Collections.emptyList();
            this.it = this.source.iterator();
        }
        
        @Override
        public boolean hasNext()
        {
            if( !it.hasNext() ) it = source.iterator();
            return it.hasNext();
        }

        @Override
        public E next()
        {
            if( hasNext() )
                return it.next();
            else
                throw new NoSuchElementException();
        }
        
    }
    
}
