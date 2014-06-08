package com.varankin.brains.jfx.editor;

import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели редактора. 
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class EditorController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( EditorController.class );
    private static final String RESOURCE_CSS  = "/fxml/editor/Editor.css";
    private static final String CSS_CLASS = "editor";
    
    static final String RESOURCE_FXML  = "/fxml/editor/Editor.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @FXML private ScrollPane pane;
    @FXML private Pane box;
    @FXML private ContextMenu popup;

    public EditorController()
    {
    }
    
    /**
     * Создает панель редактора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель редактора. 
     */
    @Override
    public ScrollPane build()
    {
        popup = new ContextMenu();
        
        box = new Pane();
        box.setId( "box" );
        
        pane = new ScrollPane();
        pane.setContent( box );
        pane.setContextMenu( popup );
        pane.setFitToWidth( true );
        pane.setFitToHeight( true );
        pane.setHbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        pane.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
    }
    
    
}
