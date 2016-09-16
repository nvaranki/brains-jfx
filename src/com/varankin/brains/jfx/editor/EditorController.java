package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbБиблиотека;
import com.varankin.brains.db.DbПроект;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.property.PropertyMonitor;
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
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Builder;
import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbПакет;

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
        BorderPane content = new BorderPane( progress );
        
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
    
    @FXML
    protected void onDragOver( DragEvent event )
    {
        Object acceptingObject = event.getAcceptingObject();
        Object gestureSource = event.getGestureSource();
        event.acceptTransferModes( TransferMode.MOVE );
        event.consume();
    }

    @FXML
    protected void onDragDropped( DragEvent event )
    {
        Object acceptingObject = event.getAcceptingObject();
        Object gestureSource = event.getGestureSource();
        event.setDropCompleted( true );
        event.consume();
    }

    public void setContent( DbЭлемент элемент )
    {
        DbАрхив архив = JavaFX.getInstance().контекст.архив;
        Транзакция транзакция = архив.транзакция();
        транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, архив );
        try
        {
            Node content;
            if( элемент instanceof DbПроект )
                content = new EdtПроект( (DbПроект)элемент ).загрузить( true );
            else if( элемент instanceof DbБиблиотека )
                content = new EdtБиблиотека( (DbБиблиотека)элемент ).загрузить( true );
            else
                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
            Platform.runLater( () -> 
            { 
                pane.setContent( content ); 
                pane.setUserData( элемент ); 
                content.setOnDragOver( this::onDragOver );
                content.setOnDragDropped( this::onDragDropped );
            } );
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
