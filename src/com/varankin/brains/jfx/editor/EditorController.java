package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Архив;
import com.varankin.brains.db.Библиотека;
import com.varankin.brains.db.Проект;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.Элемент;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
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
    
    public static final String RESOURCE_FXML  = "/fxml/editor/Editor.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @FXML private ScrollPane pane;
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
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize( 50, 50 );
        progress.setMaxSize( 150, 150 );
        BorderPane content = new BorderPane( progress);
        
        pane = new ScrollPane();
        pane.setContent( content );
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
    
    public void setContent( Элемент элемент )
    {
        Архив архив = JavaFX.getInstance().контекст.архив;
        Транзакция транзакция = архив.транзакция();
        транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, архив );
        try
        {
            Node content;
            if( элемент instanceof Проект )
                content = new EdtПроект( (Проект)элемент ).загрузить( true );
            else if( элемент instanceof Библиотека )
                content = new EdtБиблиотека( (Библиотека)элемент ).загрузить( true );
            else
                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
            Platform.runLater( () -> { pane.setContent( content ); pane.setUserData( элемент ); } );
        }
        catch( Exception ex )
        {
            LOGGER.log( Level.SEVERE, "failure to setup editor for " + элемент.название(), ex );
            LOGGER.log( Level.SEVERE, "Exception: {0}", ex.getMessage() );
        }
        finally
        {
            транзакция.завершить( true );
        }
    }

}
