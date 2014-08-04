package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.Элемент;
import com.varankin.util.LoggerX;
import java.util.Arrays;
import java.util.Collection;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки общих параметров.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class TabElementController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabElementController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabElement.css";
    private static final String CSS_CLASS = "properties-tab-element";

    private final AttributeAgent pathAgent, nameAgent;

    private Элемент элемент;
    
    @FXML private Label path;
    @FXML private TextField name;

    public TabElementController()
    {
        nameAgent = new NameAgent();
        pathAgent = new PathAgent();
    }
    
    /**
     * Создает панель выбора и установки общих параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        path = new Label();
        
        name = new TextField();
        name.setFocusTraversable( true );
        name.setId( "name" );
        
        GridPane pane = new GridPane();
        pane.setId( "element" );
        pane.add( new Label( LOGGER.text( "properties.element.name" ) ), 0, 0 );
        pane.add( path, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.element.name" ) ), 0, 1 );
        pane.add( name, 1, 1 );
        
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
        return Arrays.asList( pathAgent, nameAgent );
    }

    ReadOnlyStringProperty nameProperty()
    {
        return name.textProperty();
    }

    void reset( Элемент элемент )
    {
        this.элемент = элемент;
    }
    
    private class PathAgent implements AttributeAgent
    {
        volatile String значение;

        @Override
        public void fromScreen()
        {
        }
        
        @Override
        public void toScreen()
        {
            path.setText( значение );
        }
        
        @Override
        public void fromStorage()
        {
            значение = элемент.название( "", "/" );
        }
        
        @Override
        public void toStorage()
        {
        }

    }
    
    private class NameAgent implements AttributeAgent
    {
        volatile String значение;

        @Override
        public void fromScreen()
        {
            значение = name.getText();
        }
        
        @Override
        public void toScreen()
        {
            name.setText( значение );
        }
        
        @Override
        public void fromStorage()
        {
            значение = элемент.название();
        }
        
        @Override
        public void toStorage()
        {
            элемент.название( значение );
        }
        
    }
    
}
