package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.КлассJava;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер закладки для установки скалярного значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TabScalarController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabScalarController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabScalar.css";
    private static final String CSS_CLASS = "tab-scalar";

    private final AttributeAgent indexAgent, typeAgent, valueAgent;

    private Object скаляр;
    
    @FXML private TextField index;
    @FXML private Label type;
    @FXML private TextArea value;

    public TabScalarController()
    {
        typeAgent = new TypeAgent();
        valueAgent = new CodeAgent();
        indexAgent = new IndexAgent();
    }
    
    /**
     * Создает панель установки скалярного значения.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки скалярного значения.
     */
    @Override
    public GridPane build()
    {
        index = new TextField();
        index.setFocusTraversable( true );
        
        type = new Label();
        
        value = new TextArea();
        value.setFocusTraversable( true );
        value.setPrefColumnCount( 40 );
        value.setPrefRowCount( 15 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.scalar.index" ) ), 0, 0 );
        pane.add( index, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.scalar.type" ) ), 0, 1 );
        pane.add( type, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.scalar.value" ) ), 0, 2 );
        pane.add( value, 0, 3, 2, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
    }
    
    Collection<AttributeAgent> getAgents()
    {
        return Arrays.asList( indexAgent, typeAgent, valueAgent );
    }
    
    void reset( Object скаляр )
    {
        this.скаляр = скаляр;
    }
    
    private class IndexAgent implements AttributeAgent
    {
        volatile int индекс;

        @Override
        public void fromScreen()
        {
            String text = index.getText();
            индекс = text == null || text.trim().isEmpty() ? -1 : Integer.valueOf( text );
        }
        
        @Override
        public void toScreen()
        {
            index.setText( индекс < 0 ? null : Integer.toString( индекс ) );
        }
        
        @Override
        public void fromStorage()
        {
//            индекс = скаляр.индекс();
        }
        
        @Override
        public void toStorage()
        {
//            скаляр.индекс( индекс );
        }

    }
    
    private class TypeAgent implements AttributeAgent
    {
        volatile String класс;

        @Override
        public void fromScreen()
        {
        }
        
        @Override
        public void toScreen()
        {
            type.setText( класс );
        }
        
        @Override
        public void fromStorage()
        {
            StringBuilder текст = new StringBuilder();
//            for( КлассJava к : скаляр.классы() )
//                текст.append( текст.length() > 0 ? ";" : "" ).append( к.название() );
            класс = текст.toString();
        }
        
        @Override
        public void toStorage()
        {
        }

    }
    
    private class CodeAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = value.getText();
        }
        
        @Override
        public void toScreen()
        {
            value.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
//            Object v = скаляр.значение();
//            текст = v != null ? v.toString() : null;
        }
        
        @Override
        public void toStorage()
        {
//            скаляр.значение( текст );
        }

    }
    
}
