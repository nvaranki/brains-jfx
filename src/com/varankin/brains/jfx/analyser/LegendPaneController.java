package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
    
    //private final TimeLineController timeLineController;
    private final BooleanProperty dynamicProperty = new SimpleBooleanProperty();
    private final DynamicPropertyChangeListener dynamicPropertyChangeListener;
    
    @FXML private CheckBox labelTime;
    @FXML private FlowPane valuesPane;

    public LegendPaneController()
    {
        dynamicPropertyChangeListener = new DynamicPropertyChangeListener();
    }
    
    /**
     * Создает панель управления прорисовкой отметок.
     * Применяется в конфигурации без FXML.
     */
    @Override
    public GridPane build()
    {
        labelTime = new CheckBox( LOGGER.text( "ControlBar.axis.time.name" ) );
        labelTime.setId( "labelTime" );
        labelTime.setSelected( false );
        
        valuesPane = new FlowPane();
        valuesPane.setId( "valuesPane" );
////        valuesPane.setHgap( 30 );
//        valuesPane.setPadding( new Insets( 0, 5, 0, 5 ) );
////        valuesPane.setAlignment( Pos.CENTER );
        
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
        pane.add( valuesPane, 0, 0 );
        pane.add( labelTime, 1, 0 );

        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {   
        labelTime.setContextMenu( new ContextMenu( 
                new MenuItem( "Возобновить движение" ), new MenuItem( "Остановить движение" ) ) );
        labelTime.selectedProperty().bindBidirectional( dynamicProperty );

        valuesPane.setMinHeight( labelTime.getMinHeight() );
        valuesPane.setPrefHeight( labelTime.getPrefHeight() );

        dynamicProperty.addListener( new WeakChangeListener<>( dynamicPropertyChangeListener ) );
    }
    
    BooleanProperty dynamicProperty()
    {
        return dynamicProperty;
    }
    
    /**
     * Добавляет отображаемое значение.
     * 
     * @param name    название значения.
     * @param painter сервис рисования отметок.
     */
    void addValueControl( final String name, final DotPainter painter, List<MenuItem> parentPopupMenu )
    {
        LegendValueController legendValueController = new LegendValueController();
        CheckBox label = legendValueController.build();
        label.setText( name );
        legendValueController.setPainter( painter ); //TODO why after .build() ?!
        label.setSelected( true ); // запуск прорисовки
        // TODO bind painter color and pattern to sample replacement
        valuesPane.getChildren().add( label );
        
        MenuItem menuItemProperties = new MenuItem( LOGGER.text( "control.popup.properties" ) );
        menuItemProperties.setOnAction( new EventHandler<ActionEvent>() 
        {
            ValuePropertiesStage properties;
            
            @Override
            public void handle( ActionEvent event )
            {
                if( properties == null )
                {
                    properties = new ValuePropertiesStage( painter );
                    properties.initOwner( JavaFX.getInstance().платформа );
                    properties.setTitle( LOGGER.text( "properties.value.title", name ) );
                }
                properties.show();
                properties.toFront();
            }
        } );
        ContextMenu popup = new ContextMenu( 
                new MenuItem( LOGGER.text( "control.popup.add" ) ), 
                new MenuItem( LOGGER.text( "control.popup.remove", name ) ), 
                menuItemProperties );
        JavaFX.copyMenuItems( parentPopupMenu, popup.getItems(), true );
        label.setContextMenu( popup);
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
            for( Node node : valuesPane.getChildren() )
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
