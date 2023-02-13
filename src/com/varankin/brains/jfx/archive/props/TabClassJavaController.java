package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.compiler.InlineJavaCompiler;
import com.varankin.brains.db.type.DbКлассJava.Назначение;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxКлассJava;
import com.varankin.util.LoggerX;
import com.varankin.util.NativeJavaClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Builder;
import javax.tools.JavaCompiler;

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

    private final SimpleBooleanProperty compiling = new SimpleBooleanProperty( false );
    private FxКлассJava класс;
    
    @FXML private TextField name;
    @FXML private ChoiceBox<Назначение> purpose;
    @FXML private TextArea code;
    @FXML private Label caret;
    @FXML private TextField options;
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
        
        caret = new Label();
        caret.setId( "caret" );
        caret.setMinWidth( 40 );
        BorderPane.setAlignment( caret, Pos.CENTER );

        options = new TextField();
        options.setPromptText( InlineJavaCompiler.JAVAC_O );
        options.setId( "options" );
        options.setFocusTraversable( true );
        BorderPane.setMargin( options, new Insets( 0, 10, 0, 10 ) );
        
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
        rc3.setVgrow( Priority.ALWAYS ); //TODO CSS
        
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.getRowConstraints().addAll( rc0, rc1, rc2, rc3 );
        pane.add( new Label( LOGGER.text( "tab.class.name" ) ), 0, 0 );
        pane.add( name, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.class.purpose" ) ), 0, 1 );
        pane.add( purpose, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.class.code" ) ), 0, 2 );
        pane.add( new BorderPane( options, null, compile, null, caret ), 1, 2 );
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
        caret.textProperty().bind( Bindings.createStringBinding( this::textCaretPosition, code.caretPositionProperty() ) );
        caret.visibleProperty().bind( code.disableProperty().not() );
        purpose.getItems().addAll( Назначение.values() );
        purpose.getItems().add( 0, null );
        purpose.setConverter( new StringToEnumConverter<>( 
                Назначение.values(), "properties.tab.class.purpose." ) );
        purpose.disableProperty().bind( code.disableProperty() );
        options.visibleProperty().bind( code.disableProperty().not() );
        compile.disableProperty().bind( Bindings.createBooleanBinding( 
                () -> code.disableProperty().get() || compiling.get(),
                code.disableProperty(), compiling ) );
        compile.visibleProperty().bind( code.disableProperty().not() );
    }
    
    @FXML
    private void compile( ActionEvent event )
    {
        String javaCode  = code.textProperty().getValueSafe();
        String javaClass = name.textProperty().getValueSafe();
        compiling.set( true );
        LOGGER.log( Level.INFO, "Compiling " + javaClass );
        JavaFX.getInstance().execute( new CompileTask( javaClass, javaCode, options.getText(), класс.положение().getValue() ) );
    }
    
    void set( FxКлассJava класс )
    {
        if( this.класс != null )
        {
            name.textProperty().unbindBidirectional( this.класс.название() );
            code.textProperty().unbindBidirectional( this.класс.код() );
            purpose.valueProperty().unbindBidirectional( this.класс.назначение() );
            options.textProperty().unbindBidirectional( this.класс.опции() );
        }
        if( класс != null )
        {
            name.textProperty().bindBidirectional( класс.название() );
            code.textProperty().bindBidirectional( класс.код() );
            purpose.valueProperty().bindBidirectional( класс.назначение() );
            options.textProperty().bindBidirectional( класс.опции() );
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
    
    private String textCaretPosition()
    {
        int cp = code.getCaretPosition();
        int row = 0, col = 0;
        String[] javaCode  = code.textProperty().getValueSafe().split( "\\n" );
        for( String line : javaCode )
            if( cp <= line.length() ) // '=' для сохранения номера строки в ее конце
            {
                col = cp;
                break;
            }
            else
            {
                cp -= 1 + line.length(); // +1 для \n
                row++;
            }
        return Integer.toString( 1 + row ) + ':' + Integer.toString( 1 + col );
    }
    
    private class CompileTask extends Task<Boolean>
    {
        final InlineJavaCompiler ijc;
        final JavaCompiler.CompilationTask task;

        public CompileTask( String javaClass, String javaCode, String options, String path )
        {
            ijc = new InlineJavaCompiler();
            task = ijc.prepare(options, javaClass, javaCode, path );
        }

        @Override
        protected Boolean call() throws Exception
        {
            try
            {
                return task != null && ijc.compile( task );
            }
            finally
            {
                ijc.close();
            }
        }
        
        @Override
        protected void succeeded()
        {
            boolean r = Objects.requireNonNullElse( getValue(), Boolean.FALSE );
            LOGGER.log( Level.INFO, "Compilation " + ( r ? "finished" : "failed" ) + '.' );
            compiling.set( false );
        }
        
        @Override
        protected void cancelled()
        {
            compiling.set( false );
        }
        
        @Override
        protected void failed()
        {
            Throwable exception = getException();
            String msg = exception == null ? null : exception.getMessage() != null ? exception.getMessage() : 
                    exception.getClass().getSimpleName();
            LOGGER.log( Level.INFO, "Compilation failed" + ( msg != null ? ": " + msg : "" ) + '.' );
            compiling.set( false );
        }
        
    }
    
}
