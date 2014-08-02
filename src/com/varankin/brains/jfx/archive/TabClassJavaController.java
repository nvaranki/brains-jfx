package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.КлассJava;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели установки параметров класса Java.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TabClassJavaController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabClassJavaController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabClassJava.css";
    private static final String CSS_CLASS = "properties-tab-class-java";

    private final AttributeAgent mainAgent, codeAgent;

    private КлассJava класс;
    
    @FXML private CheckBox main;
    @FXML private TextArea code;
    @FXML private Label name;

    public TabClassJavaController()
    {
        mainAgent = new MainAgent();
        codeAgent = new CodeAgent();
    }
    
    /**
     * Создает панель установки параметров класса Java.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки параметров.
     */
    @Override
    public GridPane build()
    {
        name = new Label();
        
        main = new CheckBox();
        main.setFocusTraversable( true );
        
        code = new TextArea();
        code.setFocusTraversable( true );
        code.setPrefColumnCount( 40 );
        code.setPrefRowCount( 15 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "properties.class.name" ) ), 0, 0 );
        pane.add( name, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.class.main" ) ), 0, 1 );
        pane.add( main, 1, 1 );
        pane.add( new Label( LOGGER.text( "properties.class.code" ) ), 0, 2 );
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
                () -> name.getText() != null && name.getText().trim().startsWith( "java.lang" ),
                name.textProperty() ) );
        //TODO NullPointerException on click into empty TextArea when enabled
    }
    
    Collection<AttributeAgent> getAgents()
    {
        return Arrays.asList( mainAgent, codeAgent );
    }
    
    StringProperty classNameProperty()
    {
        return name.textProperty();
    }

    void reset( КлассJava класс )
    {
        this.класс = класс;
    }
    
    private class MainAgent implements AttributeAgent
    {
        volatile boolean основной;

        @Override
        public void fromScreen()
        {
            основной = main.selectedProperty().get();
        }
        
        @Override
        public void toScreen()
        {
            main.setSelected( основной );
        }
        
        @Override
        public void fromStorage()
        {
            основной = класс.основной();
        }
        
        @Override
        public void toStorage()
        {
            класс.основной( основной );
        }

    }
    
    private class CodeAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = code.getText();
        }
        
        @Override
        public void toScreen()
        {
            code.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            текст = класс.код();
        }
        
        @Override
        public void toStorage()
        {
            класс.код( текст );
        }

    }
    
}
