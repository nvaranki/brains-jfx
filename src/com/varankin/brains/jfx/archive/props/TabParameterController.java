package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.db.type.DbПараметр;
import com.varankin.brains.db.type.DbТекстовыйБлок;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.db.FxПараметр;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели выбора и установки параметра.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
public final class TabParameterController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabParameterController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabParameter.css";
    private static final String CSS_CLASS = "properties-tab-parameter";
    private static final StringConverter<Integer> CONVERTER_INTEGER 
            = new ToStringConverter<>( s -> Integer.valueOf( s ) );

    static final String RESOURCE_FXML = "/fxml/archive/TabParameter.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private final ChangeListener<String> CL_INDEX = (v,o,n) -> Platform.runLater( () -> refresh( null ) );
    
    private FxПараметр параметр;
    
    @FXML private TextField index;
    @FXML private TextField priority;
    @FXML private TextArea preview;

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
        
        priority = new TextField();
        priority.setId( "priority" );
        
        preview = new TextArea();
        preview.setId( "preview" );
        preview.setEditable( false );
        
        Button refresh = new Button( LOGGER.text( "properties.tab.refresh" ) );
        refresh.setOnAction( this::refresh );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.parameter.index" ) ), 0, 0 );
        pane.add( index, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.parameter.priority" ) ), 0, 1 );
        pane.add( priority, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.parameter.preview" ) ), 0, 2 );
        pane.add( refresh, 1, 2 );
        pane.add( preview, 0, 3, 2, 1 );
        GridPane.setHalignment( refresh, HPos.RIGHT );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }

    @FXML
    protected void initialize()
    {
    }
    
    @FXML
    private void refresh( ActionEvent event )
    {
        DbПараметр элемент = параметр != null ? параметр.getSource() : null;
        String текст = null;
        if( элемент != null )
            try( final Транзакция т = элемент.транзакция() )
            {
                текст = значение( элемент );
                т.завершить( true );
            }
            catch( Exception e )
            {
                LOGGER.getLogger().log( Level.SEVERE, "", e );
            }
        preview.setText( текст );
    }
            
    void set( FxПараметр параметр )
    {
        if( this.параметр != null )
        {
            index.textProperty().removeListener( CL_INDEX );
            index.textProperty().unbindBidirectional( this.параметр.индекс() );
            priority.textProperty().unbindBidirectional( this.параметр.приоритет() );
        }
        if( параметр != null )
        {
            index.textProperty().addListener( CL_INDEX );
            index.textProperty().bindBidirectional( параметр.индекс() );
            priority.textProperty().bindBidirectional( параметр.приоритет(), CONVERTER_INTEGER );
        }
        this.параметр = параметр;
    }
    
    private static String значение( DbПараметр элемент )
    {
        StringBuilder sb = new StringBuilder();
        String индекс = элемент.индекс();
        if( индекс != null )
            sb.append( индекс ).append( " -> " );
        if( элемент.параметры().isEmpty() )
            sb.append( DbТекстовыйБлок.текст( элемент.тексты(), "\n" ) );
        else
        {
            sb.append( "{" );
            for( DbПараметр п : элемент.параметры() )
                sb.append( ' ' ).append( значение( п ) ).append( "," );
            sb.append( " }" );
        }
        return sb.toString();
    }

}
