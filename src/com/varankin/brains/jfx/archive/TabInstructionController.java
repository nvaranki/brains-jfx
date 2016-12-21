package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxИнструкция;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров инструкции.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabInstructionController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabInstructionController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabInstruction.css";
    private static final String CSS_CLASS = "properties-tab-instruction";

    static final String RESOURCE_FXML = "/fxml/archive/TabInstruction.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxИнструкция инструкция;
    
    @FXML private TextField code;
    @FXML private TextField proc;
    @FXML private TextArea result;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        code = new TextField();
        code.setId( "code" );
        code.setPrefColumnCount( 6 );
        
        proc = new TextField();
        proc.setId( "proc" );
        proc.setPrefColumnCount( 23 );
        
        result = new TextArea();
        result.setId( "result" );
        
        Button refresh = new Button( LOGGER.text( "properties.tab.refresh" ) );
        refresh.setOnAction( this::refresh );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.tab.instruction.code" ) ), 0, 0 );
        pane.add( code, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.tab.instruction.proc" ) ), 0, 1 );
        pane.add( proc, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.tab.instruction.result" ) ), 0, 2 );
        pane.add( refresh, 1, 2 );
        pane.add( result, 0, 3, 2, 1 );
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
        инструкция.пересчитать();
    }
    
    void set( FxИнструкция инструкция )
    {
        if( this.инструкция != null )
        {
            code.textProperty().unbindBidirectional( this.инструкция.код() );
            proc.textProperty().unbindBidirectional( this.инструкция.процессор() );
            result.textProperty().unbind();
        }
        if( инструкция != null )
        {
            code.textProperty().bindBidirectional( инструкция.код() );
            proc.textProperty().bindBidirectional( инструкция.процессор() );
            result.textProperty().bind( инструкция.выполнить() );
        }
        this.инструкция = инструкция;
    }
    
}
