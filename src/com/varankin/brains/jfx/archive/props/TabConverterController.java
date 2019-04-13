package com.varankin.brains.jfx.archive.props;

import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Builder;
import com.varankin.brains.db.DbКонвертер;

/**
 * FXML-контроллер закладки для установки способа конвертирования значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public class TabConverterController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabConverterController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabConverter.css";
    private static final String CSS_CLASS = "tab-converter";

    private final AttributeAgent methodAgent, fromAgent, toAgent;

    private DbКонвертер конвертер;
    
    @FXML private TextField method, from, to;

    public TabConverterController()
    {
        methodAgent = new MethodAgent();
        fromAgent = new FromAgent();
        toAgent = new ToAgent();
    }
    
    /**
     * Создает панель установки способа конвертирования значения.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель установки способа конвертирования значения.
     */
    @Override
    public GridPane build()
    {
        method = new TextField();
        method.setFocusTraversable( true );
        
        to = new TextField();
        to.setFocusTraversable( true );
        
        from = new TextField();
        from.setFocusTraversable( true );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "tab.converter.method" ) ), 0, 0 );
        pane.add( method, 1, 0 );
        pane.add( new Label( LOGGER.text( "tab.converter.from" ) ), 0, 1 );
        pane.add( from, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.converter.to" ) ), 0, 2 );
        pane.add( to, 1, 2 );
        GridPane.setHgrow( method, Priority.ALWAYS );
        GridPane.setHgrow( from, Priority.ALWAYS );
        GridPane.setHgrow( to, Priority.ALWAYS );
        
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
        return Arrays.asList( methodAgent, fromAgent, toAgent );
    }
    
    void reset( DbКонвертер конвертер )
    {
        this.конвертер = конвертер;
    }
    
    private class MethodAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = method.getText();
        }
        
        @Override
        public void toScreen()
        {
            method.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            текст = конвертер.метод();
        }
        
        @Override
        public void toStorage()
        {
            конвертер.метод( текст != null && !текст.trim().isEmpty() ? текст : null );
        }

    }
    
    private class FromAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = from.getText();
        }
        
        @Override
        public void toScreen()
        {
            from.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            текст = конвертер.параметр();
        }
        
        @Override
        public void toStorage()
        {
            конвертер.параметр( текст != null && !текст.trim().isEmpty() ? текст : null );
        }

    }
    
    private class ToAgent implements AttributeAgent
    {
        volatile String текст;

        @Override
        public void fromScreen()
        {
            текст = to.getText();
        }
        
        @Override
        public void toScreen()
        {
            to.setText( текст );
        }
        
        @Override
        public void fromStorage()
        {
            текст = конвертер.результат();
        }
        
        @Override
        public void toStorage()
        {
            конвертер.результат( текст );
        }

    }
    
}
