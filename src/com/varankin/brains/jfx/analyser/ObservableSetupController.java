package com.varankin.brains.jfx.analyser;

import com.varankin.property.PropertyMonitor;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Builder;

/**
 *
 * @author Николай
 */
public final class ObservableSetupController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableSetupController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableSetup.css";
    private static final String CSS_CLASS = "observable-setup";
    
    static final String RESOURCE_FXML = "/fxml/analyser/ObservableSetup.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private boolean approved;
    
    @FXML private Button buttonOK, buttonCancel;

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

        
        TabPane tabs = new TabPane();
//        tabs.getTabs().addAll( tabValueRuler, tabTimeRuler, tabGraph );

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
//        BooleanBinding valid = 
//            Bindings.and( 
//                Bindings.and( 
//                    timeRulerPropertiesPaneController.validProperty(),
//                    valueRulerPropertiesPaneController.validProperty() ),
//                graphPropertiesPaneController.validProperty() ) ;
//        buttonOK.disableProperty().bind( Bindings.not( valid ) );
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
     * Создает новое значение, отображаемое на графике.
     * 
     * @param monitor монитор значения.
     */ 
    Value createValueInstance( PropertyMonitor monitor )
    {
        if( monitor == null || !approved ) return null;
        
        //TODO DEBUG START
        @Deprecated int i = 0;//observables.size();
        // first tab
        int buffer = 1000;
        String name = monitor.getClass().getSimpleName() + i;
        String property = "DEBUG"; 
        Value.Convertor<Float> convertor = (Float value, long timestamp) -> new Dot( value, timestamp );
        // next tab
        @Deprecated Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        @Deprecated int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
        int[][] pattern = patterns[i%patterns.length];
        Color color = colors[i%colors.length];
        //TODO DEBUG END
        DotPainter painter = new BufferedDotPainter( new LinkedBlockingQueue<>(), buffer );
//        painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
//        painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
//        painter.writableImageProperty().bind( graphController.writableImageProperty() );
        return new Value( monitor, property, convertor, painter, pattern, color, name );
    }
    
}
