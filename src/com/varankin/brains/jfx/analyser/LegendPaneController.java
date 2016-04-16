package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;

/**
 * FXML-контроллер панели управления прорисовкой отметок.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class LegendPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( LegendPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/LegendPane.css";
    private static final String CSS_CLASS = "legend-pane";
    
    private final ReadOnlyListWrapper<Value> valuesProperty;
    private final ObjectProperty<TimeUnit> unitProperty;
    private final DynamicPropertyChangeListener dynamicPropertyChangeListener;

    private List<? extends MenuItem> parentPopupMenu;
    
    @FXML private CheckBox time;
    @FXML private FlowPane values;
    @FXML private Label label;
    @FXML private MenuItem menuItemResume;
    @FXML private MenuItem menuItemStop;
    @FXML private ContextMenu timePopup;

    public LegendPaneController()
    {
        valuesProperty = new ReadOnlyListWrapper<>();
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
        
        label = new Label();
        label.setId( "label" );

        values = new FlowPane();
        values.setId( "values" );
        values.getChildren().add( label );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        cc0.setHalignment( HPos.LEFT );
        cc0.setMinWidth( 110d );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.NEVER );
        cc1.setHalignment( HPos.RIGHT );
        cc1.setMinWidth( 100d );
        
        RowConstraints rc0 = new RowConstraints();
        rc0.setFillHeight( true );
        rc0.setValignment( VPos.TOP );
        rc0.setVgrow( Priority.NEVER );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().add( rc0 );
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
        time.textProperty().bind( Bindings.createStringBinding( new TimeAxisText(), unitProperty ) );
        time.selectedProperty().addListener( new WeakChangeListener<>( dynamicPropertyChangeListener ) );
        
        menuItemResume.disableProperty().bind( time.selectedProperty() );
        menuItemStop.disableProperty().bind( Bindings.not( time.selectedProperty() ) );

        values.setMinHeight( time.getMinHeight() );
        values.setPrefHeight( time.getPrefHeight() );
        
        valuesProperty.setValue( new ValueList( values.getChildrenUnmodifiable() ) );
    }
    
    @FXML
    private void onActionResume( ActionEvent e )
    {
        time.selectedProperty().setValue( Boolean.TRUE );
        e.consume();
    }

    @FXML
    private void onActionStop( ActionEvent e )
    {
        time.selectedProperty().setValue( Boolean.FALSE );
        e.consume();
    }

    @FXML
    private void onActionRemoveAll( ActionEvent e )
    {
        List<Node> cbl = values.getChildren().stream().filter( n -> n instanceof CheckBox )
                .collect( Collectors.toList() );
        values.getChildren().removeAll( cbl );
        e.consume();
    }

    StringProperty labelProperty()
    {
        return label.textProperty();
    }

    ReadOnlyListProperty<Value> valuesProperty()
    {
        return valuesProperty.getReadOnlyProperty();
    }

    BooleanProperty dynamicProperty()
    {
        return time.selectedProperty();
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
    
    void applyProperties( GraphPropertiesPaneController pattern )
    {
        label.textProperty().setValue( pattern.labelProperty().getValue() );
    }

    /**
     * Создает новый элемент управления отображаемым значением в ленте элементов управления.
     * 
     * @param value дескриптор значения.
     */
    void addLegendItem( Value value )
    {
        BuilderFX<CheckBox,LegendValueController> builder = new BuilderFX<>();
        builder.init( LegendValueController.class, 
                LegendValueController.RESOURCE_FXML, LegendValueController.RESOURCE_BUNDLE );
        LegendValueController legendValueController = builder.getController();
        
        JavaFX.copyMenuItems( parentPopupMenu, legendValueController.getContextMenu().getItems(), true );
        
        CheckBox node = builder.getNode();
        node.setUserData( value ); // --> valuesProperty
        
        values.getChildren().add( node );
    }
    
    void clear()
    {
        onActionRemoveAll( new ActionEvent() );
    }

    //<editor-fold defaultstate="collapsed" desc="классы">
    
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
    
    /**
     * {@linkplain ObservableList Список} отображаемых {@linkplain Value значений} графика.
     */
    private static class ValueList extends TransformationList<Value,Node>
    {
        final List<Value> lv;
        
        ValueList( ObservableList<? extends Node> source )
        {
            super( source );
            lv = new ArrayList<>();
        }
        
        @Override
        protected void sourceChanged( ListChangeListener.Change<? extends Node> c )
        {
            while( c.next() )
                if( c.wasPermutated() )
                {
                    LOGGER.getLogger().fine( "Permutated list" );
                }
                else if( c.wasUpdated() )
                {
                    LOGGER.getLogger().fine( "Updated list" );
                }
                else
                {
                    for( Node node : c.getRemoved() )
                    {
                        Object userData = node.getUserData();
                        if( userData instanceof Value )
                        {
                            Value value = (Value)userData;
                            beginChange();
                            int i = lv.indexOf( value );
                            nextRemove( i, value );
                            endChange();
                            lv.remove( value );
                        }
                    }
                    for( Node node : c.getAddedSubList() )
                    {
                        Object userData = node.getUserData();
                        if( userData instanceof Value )
                        {
                            Value value = (Value)userData;
                            lv.add( value );
                            beginChange();
                            int i = lv.indexOf( value );
                            nextAdd( i, i+1 );
                            endChange();
                        }
                    }
                }
        }
        
        @Override
        public int getSourceIndex( int index )
        {
            Value test = lv.get( index );
            List<? extends Node> source = getSource();
            for( int i = 0, max = source.size(); i < max; i++ )
                if( source.get( i ).getUserData() == test )
                    return i;
            return -1;
        }
        
        @Override
        public Value get( int index )
        {
            return lv.get( index );
        }
        
        @Override
        public int size()
        {
            return lv.size();
        }
        
    }
    
    //</editor-fold>
    
}
