package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Управление прорисовкой отметок.
 * 
 * @author &copy; 2013 Николай Варанкин
 */
final class ControlBarPane extends GridPane
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ControlBarPane.class );
    
    private final FlowPane valuesPane;
    private final TimeLineController controller;
    
    ControlBarPane( TimeLineController controller )
    {
        this.controller = controller;
        
        CheckBox labelTime = new CheckBox( LOGGER.text( "ControlBar.axis.time.name" ) );
        labelTime.setSelected( false );
        labelTime.setContextMenu( new ContextMenu( 
                new MenuItem( "Возобновить движение" ), new MenuItem( "Остановить движение" ) ) );
        labelTime.selectedProperty().bindBidirectional( controller.dynamicProperty() );

        controller.dynamicProperty().addListener( new ChangeListener<Boolean>() 
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> observable, 
                                Boolean oldValue, Boolean newValue )
            {
                if( newValue != null && newValue )
                    resumeAllFlows();
                else
                    stopAllFlows();
            }
        } );
        
        valuesPane = new FlowPane();
        valuesPane.setHgap( 30 );
        valuesPane.setPadding( new Insets( 0, 5, 0, 5 ) );
        valuesPane.setAlignment( Pos.CENTER );
        valuesPane.setMinHeight( labelTime.getMinHeight() );
        valuesPane.setPrefHeight( labelTime.getPrefHeight() );

        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow( Priority.ALWAYS );
        cc0.setHalignment( HPos.CENTER );
        cc0.setMinWidth( 110d );
        
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.NEVER );
        cc1.setHalignment( HPos.RIGHT );
        cc1.setMinWidth( 100d );
        
        getColumnConstraints().addAll( cc0, cc1 );
        add( valuesPane, 0, 0 );
        add( labelTime, 1, 0 );
    }
    
    /**
     * Добавляет отображаемое значение.
     * 
     * @param name    название значения.
     * @param painter сервис рисования отметок.
     */
    void addValueControl( final String name, final DotPainter painter, List<MenuItem> parentPopupMenu )
    {
        final CheckBox label = new CheckBox( name );
        label.setUserData( name ); //TODO DEBUG
        label.setGraphic( new ImageView( painter.sample() ) );
        label.setGraphicTextGap( 3 );
        label.setSelected( false );
        label.selectedProperty().addListener( new ChangeListener<Boolean>() 
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> observable, 
                                Boolean oldValue, Boolean newValue )
            {
                Object id = label.getUserData();
                if( newValue != null && newValue )
                    controller.startFlow( id, painter );
                else
                    controller.stopFlow( id );
            }
        } );
        label.setSelected( true );
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

    private void resumeAllFlows()
    {
        // возобновить рисование отметок
        for( Node node : valuesPane.getChildren() )
            if( node instanceof CheckBox )
            {
                CheckBox vcb = (CheckBox)node;
                vcb.selectedProperty().setValue( controller.getFlowState( vcb.getUserData() ) );
                vcb.disableProperty().setValue( false );
            }
    }
    
    private void stopAllFlows()
    {
        // остановить рисование отметок
        for( Node node : valuesPane.getChildren() )
            if( node instanceof CheckBox )
            {
                CheckBox vcb = (CheckBox)node;
                controller.setFlowState( vcb.getUserData(), vcb.selectedProperty().get() );
                vcb.selectedProperty().setValue( false );
                vcb.disableProperty().setValue( true );
            }
    }
    
    EventHandler<ActionEvent> createActionStartAllFlows()
    {
        return new ActionStartAllFlows();
    }

    EventHandler<ActionEvent> createActionStopAllFlows()
    {
        return new ActionStopAllFlows();
    }

    private class ActionStartAllFlows implements EventHandler<ActionEvent>
    {
        @Override
        public void handle( ActionEvent event )
        {
            controller.dynamicProperty().setValue( true );
        }
    }
    
    private class ActionStopAllFlows implements EventHandler<ActionEvent>
    {
        @Override
        public void handle( ActionEvent event )
        {
            controller.dynamicProperty().setValue( false );
        }
    }
    
}
