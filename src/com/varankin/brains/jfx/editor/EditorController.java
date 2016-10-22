package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Translate;
import javafx.util.Builder;
import javafx.util.StringConverter;

/**
 * FXML-контроллер панели редактора. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class EditorController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( EditorController.class );
    private static final String RESOURCE_CSS  = "/fxml/editor/Editor.css";
    private static final String CSS_CLASS = "editor";
    
    public static final String RESOURCE_FXML  = "/fxml/editor/Editor.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @FXML private BorderPane pane;
    @FXML private StackPane stackPane;
    @FXML private Pane grid;
    @FXML private TextField snap;
    @FXML private Label posX;
    @FXML private Label posY;
    @FXML private ContextMenu popup;
    
    private final IntegerProperty snapProperty;
    private int offsetX, offsetY;

    public EditorController()
    {
        snapProperty = new SimpleIntegerProperty( 1 );
        offsetX = offsetY = 50;
    }

    /**
     * Создает панель редактора. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель редактора. 
     */
    @Override
    public Parent build()
    {
        popup = new ContextMenu();
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize( 50, 50 );
        progress.setMaxSize( 150, 150 );
        
        Button buttonMove = new Button( "Move" );
        posX = new Label( "0" );
        posY = new Label( "0" );
        Label labelX = new Label( " X: " );
        HBox pos = new HBox( labelX, posX, new Label( " Y: " ), posY );
        pos.setAlignment( Pos.CENTER_RIGHT );
        pos.setFillHeight( true );
        HBox.setHgrow( pos, Priority.ALWAYS );
        
        snap = new TextField( "1" );
        snap.setPrefColumnCount( 4 );
        snap.setAlignment( Pos.CENTER_RIGHT );
        TextFormatter snapFormatter = new TextFormatter<>( new StringConverter<Integer>() {
            @Override
            public Integer fromString( String string )
            {
                return string != null ? Double.valueOf( string ).intValue() : 0;
            }
            @Override
            public String toString( Integer object )
            {
                return object != null ? object.toString() : "";
            }
        } );
        snapFormatter.valueProperty().bindBidirectional( snapProperty );
        snap.setTextFormatter( snapFormatter );
        
        ToolBar toolBar = new ToolBar( buttonMove, snap, pos );
        
        grid = new Pane();
        
        stackPane = new StackPane( grid, new BorderPane( progress ) );
        stackPane.setBackground( new Background( new BackgroundFill( Color.WHITE, null, null ) ) );
        
        ScrollPane scrollPane = new ScrollPane( stackPane );
        scrollPane.setFitToWidth( true );
        scrollPane.setFitToHeight( true );
        scrollPane.setHbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        scrollPane.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        
        pane = new BorderPane();
        pane.setTop( toolBar );
        pane.setCenter( scrollPane );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        List<Node> children = grid.getChildren();
        int d = 1;
        for( int x = 0; x <= 1000; x += 50 )
            for( int y = 0; y <= 1000; y += 50 )
            {
                children.add( new Line( x-d, y, x+d, y ) );
                children.add( new Line( x, y-d, x, y+d ) );
            }
    }

    private void handleMouseMove( MouseEvent event )
    {
        int round = snapProperty.get();//Integer.valueOf( snap.getText() );
        Integer x = (int)Math.round( event.getX() / round ) * round - offsetX;
        Integer y = (int)Math.round( event.getY() / round ) * round - offsetY;
        posX.setText( x.toString() );
        posY.setText( y.toString() );
    }
    
    @FXML
    private void onDragDetected( MouseEvent event, Node content )
    {
        if( MouseButton.PRIMARY == event.getButton() )
        {
            SnapshotParameters snapParams = new SnapshotParameters();
            snapParams.setFill( Color.TRANSPARENT );
            Dragboard dndb = content.startDragAndDrop( TransferMode.MOVE );
            dndb.setDragView( content.snapshot( snapParams, null ) );
            //dndb.setContent( Collections.singletonMap( DataFormat.PLAIN_TEXT, text.getText() ) );
//            content.setVisible( false );
        }
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
        Транзакция транзакция = элемент.транзакция();
        try
        {
            транзакция.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, элемент );
            Node content = EdtФабрика.getInstance().создать( элемент ).загрузить( true );
            Platform.runLater(() -> 
            { 
                ObservableList<Node> children = stackPane.getChildren();
                children.subList( 1, children.size() ).clear(); // кроме сетки
                Pane pad = new Pane( content );
                pad.setOnMouseMoved( this::handleMouseMove );
                content.getTransforms().add( 0, new Translate( offsetX, offsetY ) );
                children.add( pad );
//                content.setContextMenu( popup );
                pane.setUserData( элемент ); 
                content.setOnDragDetected( e -> onDragDetected( e, content ) );
                stackPane.setOnDragOver( this::onDragOver );
                stackPane.setOnDragDropped( this::onDragDropped );
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
