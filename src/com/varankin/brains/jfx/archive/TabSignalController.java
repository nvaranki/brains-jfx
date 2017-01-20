package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbСигнал.Приоритет;
import com.varankin.brains.jfx.db.FxСигнал;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров сигнала.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class TabSignalController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabSignalController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabSignal.css";
    private static final String CSS_CLASS = "properties-tab-signal";

    static final String RESOURCE_FXML = "/fxml/archive/TabSignal.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxСигнал сигнал;
    
    @FXML private ChoiceBox<Приоритет> priority;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        priority = new ChoiceBox<>();
        priority.setId( "priority" );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.signal.priority" ) ), 0, 0 );
        pane.add( priority, 1, 0 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
        priority.getItems().addAll( Приоритет.values() );
        priority.getItems().add( 0, null );
        priority.setConverter( new StringToEnumConverter<>( Приоритет.values(), "tab.signal.priority." ) );
    }
    
    void set( FxСигнал сигнал )
    {
        if( this.сигнал != null )
        {
            priority.valueProperty().unbindBidirectional( this.сигнал.приоритет() );
        }
        if( сигнал != null )
        {
            priority.valueProperty().bindBidirectional( сигнал.приоритет() );
        }
        this.сигнал = сигнал;
    }
    
}
