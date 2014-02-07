package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.io.IOException;
import java.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.scene.Node;
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
    private static final String RESOURCE_FXML_VALUE = "/fxml/analyser/LegendValue.fxml";
    
    private final BooleanProperty dynamicProperty;
    private final DynamicPropertyChangeListener dynamicPropertyChangeListener;

    private List<? extends MenuItem> parentPopupMenu;
    
    @FXML private CheckBox time;
    @FXML private FlowPane values;
    @FXML private MenuItem menuItemResume;
    @FXML private MenuItem menuItemStop;
    @FXML private ContextMenu timePopup;

    public LegendPaneController()
    {
        dynamicProperty = new SimpleBooleanProperty();
        dynamicPropertyChangeListener = new DynamicPropertyChangeListener();
        parentPopupMenu = Collections.emptyList();
    }
    
    /**
     * Создает панель управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        menuItemResume = new MenuItem( LOGGER.text( "control.popup.start" ) );
        menuItemResume.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionResume( event );
            }
        } );
        
        menuItemStop = new MenuItem( LOGGER.text( "control.popup.stop" ) );
        menuItemStop.setOnAction( new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle( ActionEvent event )
            {
                onActionStop( event );
            }
        } );
        
        timePopup = new ContextMenu();
        timePopup.getItems().addAll( menuItemResume, menuItemStop );
        
        time = new CheckBox();
        time.setId( "time" );
        time.setText( LOGGER.text( "axis.time.name" ) );
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
        
        menuItemResume.disableProperty().bind( time.selectedProperty() );
        menuItemStop.disableProperty().bind( Bindings.not( time.selectedProperty() ) );

        values.setMinHeight( time.getMinHeight() );
        values.setPrefHeight( time.getPrefHeight() );

        dynamicProperty.addListener( new WeakChangeListener<>( dynamicPropertyChangeListener ) );
    }
    
    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }
    
    void setParentPopupMenu( List<? extends MenuItem> parentPopupMenu )
    {
        this.parentPopupMenu = parentPopupMenu;
        JavaFX.copyMenuItems( parentPopupMenu, timePopup.getItems(), true );
    }
    
    /**
     * Добавляет отображаемое значение.
     * 
     * @param name    название значения.
     * @param painter сервис рисования отметок.
     */
    void addValueControl( String name, DotPainter painter )
    {
        LegendValueController legendValueController;
        CheckBox label;
        if( JavaFX.getInstance().useFxmlLoader() )
            try
            {
                java.net.URL location = getClass().getResource( RESOURCE_FXML_VALUE );
                ResourceBundle resources = LOGGER.getLogger().getResourceBundle();
                FXMLLoader fxmlLoader = new FXMLLoader( location, resources );
                label = (CheckBox)fxmlLoader.load();
                legendValueController = fxmlLoader.getController();
            }
            catch( IOException ex )
            {
                throw new RuntimeException( ex );
            }
        else
        {
            legendValueController = new LegendValueController();
            label = legendValueController.build();
        }
        legendValueController.setPainter( painter );
        label.setText( name );
        label.setSelected( true ); // запуск прорисовки
        JavaFX.copyMenuItems( parentPopupMenu, legendValueController.getContextMenu().getItems(), true );
        
        values.getChildren().add( label );
    }

    @FXML
    private void onActionResume( ActionEvent _ )
    {
        time.selectedProperty().setValue( Boolean.TRUE );
    }

    @FXML
    private void onActionStop( ActionEvent _ )
    {
        time.selectedProperty().setValue( Boolean.FALSE );
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
    
}
