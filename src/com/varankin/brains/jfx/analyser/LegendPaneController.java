package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели управления прорисовкой отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
public final class LegendPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LegendPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/LegendPane.css";
    private static final String CSS_CLASS = "legend-pane";
    
    private final ReadOnlyListWrapper<Value> valuesProperty;
    private final ListChangeListener<Value> valueListListener;
    private final BooleanProperty dynamicProperty;
    private final ObjectProperty<TimeUnit> unitProperty;
    private final DynamicPropertyChangeListener dynamicPropertyChangeListener;

    private List<? extends MenuItem> parentPopupMenu;
    
    @FXML private CheckBox time;
    @FXML private FlowPane values;
    @FXML private MenuItem menuItemResume;
    @FXML private MenuItem menuItemStop;
    @FXML private ContextMenu timePopup;

    public LegendPaneController()
    {
        valuesProperty = new ReadOnlyListWrapper<>( FXCollections.<Value>observableArrayList() );
        valueListListener = new ValueListListener();
        valuesProperty.addListener( new WeakListChangeListener<>( valueListListener ) );
        dynamicProperty = new SimpleBooleanProperty();
        dynamicPropertyChangeListener = new DynamicPropertyChangeListener();
        unitProperty = new SimpleObjectProperty<>();
        parentPopupMenu = Collections.emptyList();
    }
    
    /**
     * Создает панель управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель управления прорисовкой отметок.
     */
    @Override
    public GridPane build()
    {
        menuItemResume = new MenuItem( LOGGER.text( "control.popup.start" ) );
        menuItemResume.setOnAction( this::onActionResume );
        
        menuItemStop = new MenuItem( LOGGER.text( "control.popup.stop" ) );
        menuItemStop.setOnAction( this::onActionStop );
        
        timePopup = new ContextMenu();
        timePopup.getItems().addAll( menuItemResume, menuItemStop );
        
        time = new CheckBox();
        time.setId( "time" );
        time.setSelected( false );
        time.setContextMenu( timePopup );
        
        values = new FlowPane();
        values.setId( "values" );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        cc0.setHalignment( HPos.CENTER );
        cc0.setMinWidth( 110d );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.NEVER );
        cc1.setHalignment( HPos.RIGHT );
        cc1.setMinWidth( 100d );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.add( values, 0, 0 );
        pane.add( time, 1, 0 );

//        new MenuItem( LOGGER.text( "control.popup.add" ) ), 
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
        time.selectedProperty().bindBidirectional( dynamicProperty );
        time.textProperty().bind( Bindings.createStringBinding( new TimeAxisText(), unitProperty ) );
        
        menuItemResume.disableProperty().bind( time.selectedProperty() );
        menuItemStop.disableProperty().bind( Bindings.not( time.selectedProperty() ) );

        values.setMinHeight( time.getMinHeight() );
        values.setPrefHeight( time.getPrefHeight() );

        dynamicProperty.addListener( new WeakChangeListener<>( dynamicPropertyChangeListener ) );
    }
    
    @FXML
    private void onActionResume( ActionEvent __ )
    {
        time.selectedProperty().setValue( Boolean.TRUE );
    }

    @FXML
    private void onActionStop( ActionEvent __ )
    {
        time.selectedProperty().setValue( Boolean.FALSE );
    }

    ReadOnlyListProperty<Value> valuesProperty()
    {
        return valuesProperty.getReadOnlyProperty();
    }

    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }
    
    Property<TimeUnit> unitProperty()
    {
        return unitProperty;
    }
    
    void extendPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        this.parentPopupMenu = parentPopupMenu;
        JavaFX.copyMenuItems( parentPopupMenu, timePopup.getItems(), true );
    }

    //    EventHandler<ActionEvent> createActionStartAllFlows()
//    {
//        return new ActionStartAllFlows();
//    }
//
//    EventHandler<ActionEvent> createActionStopAllFlows()
//    {
//        return new ActionStopAllFlows();
//    }
//
//    private class ActionStartAllFlows implements EventHandler<ActionEvent>
//    {
//        @Override
//        public void handle( ActionEvent event )
//        {
//            timeLineController.dynamicProperty().setValue( true );
//        }
//    }
//    
//    private class ActionStopAllFlows implements EventHandler<ActionEvent>
//    {
//        @Override
//        public void handle( ActionEvent event )
//        {
//            timeLineController.dynamicProperty().setValue( false );
//        }
//    }
    
    private class DynamicPropertyChangeListener implements ChangeListener<Boolean>
    {
        private final Map<Object,Boolean> flowState = new IdentityHashMap<>(); //TODO DEBUG Identity
        
        @Override
        public void changed( ObservableValue<? extends Boolean> observable, 
                            Boolean oldValue, Boolean newValue )
        {
            boolean resume = newValue != null && newValue;
            for( Node node : values.getChildren() )
                if( node instanceof CheckBox )
                {
                    CheckBox vcb = (CheckBox)node;
                    Object key = vcb;//.getUserData();
                    if( resume )
                    {
                        // возобновить рисование отметки
                        vcb.selectedProperty().setValue( getFlowState( key ) );
                        // разрешить индивидуальные установки
                        vcb.disableProperty().setValue( false );
                    }
                    else
                    {
                        // остановить рисование отметки
                        setFlowState( key, vcb.selectedProperty().get() );
                        vcb.selectedProperty().setValue( false );
                        // заблокировать индивидуальные установки
                        vcb.disableProperty().setValue( true );
                    }
                }
            
        }

        boolean getFlowState( Object o )
        {
            Boolean flows = flowState.get( o );
            return flows != null ? flows : true;
        }

        void setFlowState( Object o, boolean flows )
        {
            flowState.put( o, flows );
        }

    }
    
    private class ValueListListener implements ListChangeListener<Value>
    {
        @Override
        public void onChanged( ListChangeListener.Change<? extends Value> c )
        {
            while( c.next() ) 
             if( c.wasPermutated() ) 
                 for( int i = c.getFrom(); i < c.getTo(); ++i ) 
                 {
                      //permutate
                 }
             else if( c.wasUpdated() ) 
             {
                      //update item
             } 
             else 
             {
                 for( Value value : c.getRemoved() ) 
                 {
                     value.stopMonitoring();
                 }
                 for( Value value : c.getAddedSubList() ) 
                 {
                    values.getChildren().add( createValueControl( value ) );
                    //TODO values.getParent().requestLayout();
                    value.startMonitoring();
                 }
             }
        }

        /**
         * Создает элемент управления отображаемым значением.
         * 
         * @param value дескриптор значения.
         */
        Node createValueControl( Value value )
        {
            BuilderFX<CheckBox,LegendValueController> builder = new BuilderFX<>();
            builder.init( LegendValueController.class, 
                    LegendValueController.RESOURCE_FXML, LegendValueController.RESOURCE_BUNDLE );
            LegendValueController legendValueController = builder.getController();
            legendValueController.colorProperty().setValue( value.color );
            legendValueController.patternProperty().setValue( value.pattern );
            legendValueController.selectedProperty().setValue( true ); // запуск прорисовки

            value.painter.colorProperty().bind( legendValueController.colorProperty() );
            value.painter.patternProperty().bind( legendValueController.patternProperty() );
            value.painter.enabledProperty().bind( legendValueController.selectedProperty() );

            CheckBox label = builder.getNode();
            label.setText( value.title );
            JavaFX.copyMenuItems( parentPopupMenu, legendValueController.getContextMenu().getItems(), true );
            label.parentProperty().addListener( new LifeCycleListener( value ) );
            return label;
        }
        
        class LifeCycleListener implements ChangeListener<Parent>
        {
            final Value value;

            LifeCycleListener( Value value )
            {
                this.value = value;
            }
            
            @Override
            public void changed( ObservableValue<? extends Parent> __, Parent ov, Parent nv )
            {
                if( nv != null )
                {
                    
                }
                else if( ov != null )
                {
                    valuesProperty.getValue().remove( value );
                }
            }
        }

    }
    
    private class TimeAxisText implements Callable<String>
    {
        @Override
        public String call() throws Exception 
        {
            TimeUnit unitValue = unitProperty.getValue();
            String key = "legend.TimeUnit." + ( unitValue != null ? unitValue.name() : "" );
            ResourceBundle rb = LOGGER.getLogger().getResourceBundle();
            String text = "";
            if( rb.containsKey( key ) )
                text = rb.getString( key );
            return LOGGER.text( "axis.time.name", text );
        }
    }
    
}
