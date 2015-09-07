package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.КлассJava;
import com.varankin.brains.db.Массив;
import com.varankin.util.LoggerX;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер закладки для установки значения массива.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TabArrayController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabArrayController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabArray.css";
    private static final String CSS_CLASS = "tab-array";

    private final AttributeAgent indexAgent, typeAgent, valueAgent;

    private Массив массив;
    
    @FXML private TextField index;
    @FXML private Label type;
    @FXML private TextArea value;

    public TabArrayController()
    {
        indexAgent = new IndexAgent();
        typeAgent = new TypeAgent();
        valueAgent = new CodeAgent();
    }
    
    /**
     * Создает панель установки значения массива.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки значения массива.
     */
    @Override
    public GridPane build()
    {
        index = new TextField();
        index.setFocusTraversable( true );
        
        type = new Label();
        
        value = new TextArea();
        value.setEditable( false );
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
    
    void reset( Массив массив )
    {
        this.массив = массив;
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
            индекс = массив.индекс();
        }
        
        @Override
        public void toStorage()
        {
            массив.индекс( индекс );
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
            for( КлассJava к : массив.классы() )
                текст.append( текст.length() > 0 ? ";" : "" ).append( к.название() );
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
        }
        
        @Override
        public void toScreen()
        {
            value.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            Object v = null;//TODO массив.значение();
            текст = 
                v == null ? null : 
                !v.getClass().isArray() ? v.toString() :
                v.getClass().getComponentType().isPrimitive() ? primitiveArrayToString( v ) :
                Arrays.deepToString( (Object[])v );
        }
        
        private String primitiveArrayToString( Object v )
        {
            Class<?> c = v.getClass().getComponentType();
            assert( c.isPrimitive() );
            String s;
            if( boolean[].class.equals( c ) )  s = Arrays.toString( (boolean[])v ); else
            if( byte[]   .class.equals( c ) )  s = Arrays.toString( (byte[])v    ); else
            if( char[]   .class.equals( c ) )  s = Arrays.toString( (char[])v    ); else
            if( double[] .class.equals( c ) )  s = Arrays.toString( (double[])v  ); else
            if( float[]  .class.equals( c ) )  s = Arrays.toString( (float[])v   ); else
            if( int[]    .class.equals( c ) )  s = Arrays.toString( (int[])v     ); else
            if( long[]   .class.equals( c ) )  s = Arrays.toString( (long[])v    ); else
            if( short[]  .class.equals( c ) )  s = Arrays.toString( (short[])v   ); else
            throw new UnsupportedOperationException( c.getName() );
            return s;
        }
        
        @Override
        public void toStorage()
        {
        }

    }
    
}
