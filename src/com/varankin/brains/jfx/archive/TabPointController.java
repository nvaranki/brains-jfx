package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbТочка;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxТочка;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели выбора и установки параметров точки.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabPointController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabPointController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabPoint.css";
    private static final String CSS_CLASS = "properties-tab-point";
    private static final StringConverter<Float> CONVERTER_LONG 
            = new ToStringConverter<>( s -> Float.valueOf( s ) );
    private static final StringConverter<Integer> CONVERTER_INTEGER 
            = new ToStringConverter<>( s -> Integer.valueOf( s ) );

    static final String RESOURCE_FXML = "/fxml/archive/TabPoint.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxТочка точка;
    private IndeterminateHelper sensorHelper;
    
    @FXML private TextField index, pin;
    @FXML private TextField threshold;
    @FXML private CheckBox sensor;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        index = new TextField();
        index.setId( "index" );
        index.setFocusTraversable( true );
        
        threshold = new TextField();
        threshold.setId( "threshold" );
        threshold.setFocusTraversable( true );
        
        sensor = new CheckBox();
        sensor.setId( "sensor" );
        sensor.setFocusTraversable( true );
        sensor.setAllowIndeterminate( true );
        
        pin = new TextField();
        pin.setId( "pin" );
        pin.setFocusTraversable( true );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.add( new Label( LOGGER.text( "tab.point.index" ) ), 0, 0 );
        pane.add( index, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.point.threshold" ) ), 0, 1 );
        pane.add( threshold, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.point.sensor" ) ), 0, 2 );
        pane.add( sensor, 1, 2 );
        pane.add( new Label( LOGGER.text( "tab.point.pin" ) ), 0, 3 );
        pane.add( pin, 1, 3 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    void set( FxТочка точка )
    {
        if( this.точка != null )
        {
            index.textProperty().unbindBidirectional( this.точка.индекс() );
            threshold.textProperty().unbindBidirectional( this.точка.порог() );
            sensor.selectedProperty().unbindBidirectional( this.точка.датчик() );
            sensorHelper.removeListeners();
            pin.textProperty().unbindBidirectional( this.точка.контакт() );
        }
        if( точка != null )
        {
            index.textProperty().bindBidirectional( точка.индекс(), CONVERTER_INTEGER );
            threshold.textProperty().bindBidirectional( точка.порог(), CONVERTER_LONG );
            sensor.selectedProperty().bindBidirectional( точка.датчик() );
            sensorHelper = new IndeterminateHelper( sensor.indeterminateProperty(), точка.датчик() );
            pin.textProperty().bindBidirectional( точка.контакт() );
            JavaFX.getInstance().execute( new TaskDisable( точка ) );
        }
        this.точка = точка;
    }
    
    private class TaskDisable extends Task<Boolean[]>
    {
        final FxТочка точка;
        
        TaskDisable( FxТочка точка )
        {
            this.точка = точка;
        }

        @Override
        protected Boolean[] call() throws Exception
        {
            DbАтрибутный архив = точка.архив().getSource();
            try( final Транзакция т = архив.транзакция() )
            {
                т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, архив );
                DbАтрибутный предок = точка.getSource().предок();
                Boolean[] значение = new Boolean[]
                {
                    предок instanceof DbТочка, // не корень
                    точка.getSource().точки().isEmpty() // лист
                };
                т.завершить( true );
                return значение;
            }
        }
        
        @Override
        protected void failed() 
        { 
            Throwable exception = this.getException();
            if( exception != null )
                LOGGER.getLogger().log( Level.SEVERE, "TaskDisable failed:", exception );
            else
                LOGGER.getLogger().log( Level.SEVERE, "TaskDisable failed" );
        }
        
        @Override
        protected void succeeded() 
        { 
            Boolean[] значение = getValue();
            index.setDisable( !значение[0] ); // корень
            pin.setDisable( значение[0] & !значение[1] ); // промежуточный, т.е. не корень и не лист
            sensor.setDisable( !значение[0] | значение[1] ); // корень или лист
        }    
    }
    
}
