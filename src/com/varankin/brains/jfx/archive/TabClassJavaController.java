package com.varankin.brains.jfx.archive;

import com.varankin.brains.appl.NativeJavaClasses;
import com.varankin.brains.jfx.db.FxКлассJava;
import com.varankin.util.LoggerX;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели установки параметров класса Java.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public class TabClassJavaController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabClassJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabClassJava.css";
    private static final String CSS_CLASS = "tab-class-java";

    static final String RESOURCE_FXML = "/fxml/archive/TabClassJava.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxКлассJava класс;
    private IndeterminateHelper mainHelper;
    
    @FXML private TextField name;
    @FXML private CheckBox main;
    @FXML private TextArea code;

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
        name.setFocusTraversable( true );
        
        main = new CheckBox();
        main.setFocusTraversable( true );
        main.setAllowIndeterminate( true );
        
        code = new TextArea();
        code.setFocusTraversable( true );
        code.setPrefColumnCount( 40 );
        code.setPrefRowCount( 15 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.class.name" ) ), 0, 0 );
        pane.add( name, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.class.main" ) ), 0, 1 );
        pane.add( main, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.class.code" ) ), 0, 2 );
        pane.add( code, 0, 3, 2, 1 );
        
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
    }
    
    void set( FxКлассJava класс )
    {
        if( this.класс != null )
        {
            name.textProperty().unbindBidirectional( this.класс.название() );
            code.textProperty().unbindBidirectional( this.класс.код() );
            main.selectedProperty().unbindBidirectional( this.класс.основной() );
            mainHelper.removeListeners();
        }
        if( класс != null )
        {
            name.textProperty().bindBidirectional( класс.название() );
            code.textProperty().bindBidirectional( класс.код() );
            main.selectedProperty().bindBidirectional( класс.основной() );
            mainHelper = new IndeterminateHelper( main.indeterminateProperty(), класс.основной() );
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
