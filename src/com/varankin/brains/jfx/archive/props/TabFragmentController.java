package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.jfx.db.FxФрагмент;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров фрагмента.
 * 
 * @author &copy; 2020 Николай Варанкин
 */
public final class TabFragmentController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabFragmentController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabFragment.css";
    private static final String CSS_CLASS = "properties-tab-fragment";

    static final String RESOURCE_FXML = "/fxml/archive/TabFragment.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxФрагмент фрагмент;
    
    @FXML private TextField processor;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        processor = new TextField();
        processor.setFocusTraversable( true );
        
        GridPane pane = new GridPane();
        pane.setId( "fragment" );
        pane.add( new Label( LOGGER.text( "tab.fragment.processor" ) ), 0, 0 );
        pane.add( processor, 1, 0 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    void set( FxФрагмент фрагмент )
    {
        if( this.фрагмент != null )
        {
            processor.textProperty().unbindBidirectional( this.фрагмент.процессор() );
        }
        if( фрагмент != null )
        {
            processor.textProperty().bindBidirectional( фрагмент.процессор() );
        }
        this.фрагмент = фрагмент;
    }

}
