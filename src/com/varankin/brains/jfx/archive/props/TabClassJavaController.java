package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.db.type.DbКлассJava.Назначение;
import com.varankin.brains.jfx.db.FxКлассJava;
import com.varankin.util.LoggerX;
import com.varankin.util.NativeJavaClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;

/**
 * FXML-контроллер панели установки параметров класса Java.
 * 
 * @author &copy; 2023 Николай Варанкин
 */
public class TabClassJavaController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabClassJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabClassJava.css";
    private static final String CSS_CLASS = "tab-class-java";

    static final String RESOURCE_FXML = "/fxml/archive/TabClassJava.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxКлассJava класс;
    
    @FXML private TextField name;
    @FXML private ChoiceBox<Назначение> purpose;
    @FXML private TextArea code;
    @FXML private Button compile;

    /**
     * Создает панель установки параметров класса Java.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки параметров.
     */
    @Override
    public GridPane build()
    {
        name = new TextField();
        name.setId( "name" );
        name.setFocusTraversable( true );
        
        purpose = new ChoiceBox<>();
        purpose.setId( "purpose" );
        purpose.setFocusTraversable( true );
        
        code = new TextArea();
        code.setId( "code" );
        code.setFocusTraversable( true );
        
        compile = new Button( LOGGER.text( "properties.tab.compile" ) );
        compile.setOnAction( this::compile );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();
        RowConstraints rc3 = new RowConstraints();
        rc3.setVgrow( Priority.ALWAYS );
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2, rc3 );
        pane.add( new Label( LOGGER.text( "tab.class.name" ) ), 0, 0 );
        pane.add( name, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.class.purpose" ) ), 0, 1 );
        pane.add( purpose, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.class.code" ) ), 0, 2 );
        pane.add( compile, 1, 2 );
        pane.add( code, 0, 3, 2, 1 );
        GridPane.setHalignment( compile, HPos.RIGHT );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        code.disableProperty().bind( Bindings.createBooleanBinding( 
                () -> name.getText() != null && isNativeClass( name.getText().trim() ),
                name.textProperty() ) );
        purpose.getItems().addAll( Назначение.values() );
        purpose.getItems().add( 0, null );
        purpose.setConverter( new StringToEnumConverter<>( 
                Назначение.values(), "properties.tab.class.purpose." ) );
        purpose.disableProperty().bind( code.disableProperty() );
        compile.disableProperty().bind( code.disableProperty() );
    }
    
    @FXML
    private void compile( ActionEvent event )
    {
        LOGGER.log( Level.WARNING, "Not implemented" );
        //TODO NOT IMPL.
    }
    
    void set( FxКлассJava класс )
    {
        if( this.класс != null )
        {
            name.textProperty().unbindBidirectional( this.класс.название() );
            code.textProperty().unbindBidirectional( this.класс.код() );
            purpose.valueProperty().unbindBidirectional( this.класс.назначение() );
        }
        if( класс != null )
        {
            name.textProperty().bindBidirectional( класс.название() );
            code.textProperty().bindBidirectional( класс.код() );
            purpose.valueProperty().bindBidirectional( класс.назначение() );
        }
        this.класс = класс;
    }
    
    private static final List<String> NATIVE_CLASS_NAMES 
        = new ArrayList<>( NativeJavaClasses.getInstance().keySet() );
    
    private static boolean isNativeClass( String name )
    {
        return name.startsWith( "java.lang." ) 
            || NATIVE_CLASS_NAMES.contains( name )
            || NATIVE_CLASS_NAMES.contains( name.split( "\\[", 2 )[0] );
    }
    
}
