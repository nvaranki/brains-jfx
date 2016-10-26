package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbОператор;
import com.varankin.brains.db.DbУзел;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
    @FXML private Pane board;
    @FXML private TextField snap;
    @FXML private Label posX;
    @FXML private Label posY;
    @FXML private ContextMenu popup;
    
    private final ObjectProperty<EventHandler<? super MouseEvent>> onMouseClickedProperty = 
           new SimpleObjectProperty<>();
    private final IntegerProperty snapProperty;
    private final IntegerProperty xProperty = new SimpleIntegerProperty();
    private final IntegerProperty yProperty = new SimpleIntegerProperty();
    private int offsetX, offsetY;
    private Node content;

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
        
        ToggleButton buttonSelect = new ToggleButton( "Select" );
        buttonSelect.setOnAction( ae -> onMouseClickedProperty.setValue( this::onSelect ) );
        ToggleButton buttonAdd = new ToggleButton( "Add" );
        buttonAdd.setOnAction( ae -> onMouseClickedProperty.setValue( this::onAdd ) );
        ToggleButton buttonDelete = new ToggleButton( "Delete" );
        buttonDelete.setOnAction( ae -> onMouseClickedProperty.setValue( this::onDelete ) );
        ChoiceBox<String> itemsAdd = new ChoiceBox<>();
        itemsAdd.getItems().addAll( "type 1", "type 2" );
        itemsAdd.disableProperty().bind( Bindings.not( buttonAdd.selectedProperty() ) );

        ToggleGroup group = new ToggleGroup();
        buttonSelect.setToggleGroup(group);
        buttonAdd.setToggleGroup(group);
        buttonDelete.setToggleGroup(group);
        buttonSelect.setSelected( true );
        
        posX = new Label( "0" );
        posY = new Label( "0" );
        HBox pos = new HBox( new Label( " X: " ), posX, new Label( " Y: " ), posY );
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
        
        ToolBar toolBar = new ToolBar( buttonSelect, itemsAdd, buttonAdd, buttonDelete, snap, pos );
        
        grid = new Pane();
        
        stackPane = new StackPane( grid, board = new BorderPane( progress ) );
        stackPane.setBackground( new Background( new BackgroundFill( Color.WHITE, null, null ) ) );
        board.setCursor( Cursor.CROSSHAIR );
        
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
        posX.textProperty().bind( Bindings.format( "%d", xProperty ) );
        posY.textProperty().bind( Bindings.format( "%d", yProperty ) );
        List<Node> children = grid.getChildren();
        int d = 1;
        for( int x = 0; x <= 1000; x += 50 )
            for( int y = 0; y <= 1000; y += 50 )
            {
                children.add( new Line( x-d, y, x+d, y ) );
                children.add( new Line( x, y-d, x, y+d ) );
            }
    }

    @FXML
    private void onMouseMove( MouseEvent event )
    {
        updateMousePosition( event );
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
            content = EdtФабрика.getInstance().создать( элемент ).загрузить( true );
            Platform.runLater(() -> 
            { 
                ObservableList<Node> children = stackPane.getChildren();
                children.subList( 1, children.size() ).clear(); // кроме сетки
                board = new Pane( content );
                board.setOnMouseMoved( this::onMouseMove );
                content.getTransforms().add( 0, new Translate( offsetX, offsetY ) );
                children.add( board );
                board.onMouseClickedProperty().bind( onMouseClickedProperty );
//                content.setContextMenu( popup );
                pane.setUserData( элемент ); 
                content.setOnDragDetected( e -> onDragDetected( e, content ) );
                stackPane.setOnDragOver( this::onDragOver );
                stackPane.setOnDragDropped( this::onDragDropped );
                if( content instanceof Group )
                    ((Group)content).getChildren().forEach( 
                            n -> n.onMouseClickedProperty().bind( onMouseClickedProperty ) );
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

    private void updateMousePosition( MouseEvent event )
    {
        int snap = snapProperty.get();
        xProperty.set( (int)Math.round( event.getX() / snap ) * snap - offsetX );
        yProperty.set( (int)Math.round( event.getY() / snap ) * snap - offsetY );
    }
    
    private void onSelect( MouseEvent e )
    {
        updateMousePosition( e );
        LOGGER.getLogger().info( "selection at "+xProperty.get()+","+yProperty.get() );
        e.consume();
    }
        
    private void onAdd( MouseEvent e )
    {
        updateMousePosition( e );
        LOGGER.getLogger().info( "addition at "+xProperty.get()+","+yProperty.get() );
        e.consume();
    }
        
    private void onDelete( MouseEvent e )
    {
        updateMousePosition( e );
        if( MouseButton.PRIMARY == e.getButton() )
            if( e.getSource() instanceof Node )
            {
                Node node = (Node)e.getSource();
                Object userData = node.getUserData();
                if( userData != null && content.getUserData() != userData && content instanceof Group )
                {
                    Bounds bounds = content.getBoundsInParent();
                    boolean contains = bounds.contains( e.getX(), e.getY() );
                    LOGGER.getLogger().info( "deletion at "+xProperty.get()+","+yProperty.get()+" for "+userData+" in bounds="+contains );
                    JavaFX.getInstance().execute( new DeleteTask( node, (Group)content ) );
                }
                else
                {
                    LOGGER.getLogger().info( "deletion at "+xProperty.get()+","+yProperty.get()+" for "+e.getSource() );
                }
            }
        e.consume();
    }
    
    private static class DeleteTask extends Task<Boolean>
    {
        final static DbОператор оператор = (o,c) -> c.remove(o);
        final DbАтрибутный выбор;
        final DbУзел узел;
        final Node node;
        final Group group;

        private DeleteTask( Node node, Group group )
        {
            this.node = node;
            this.group = group;
            Object userDataNode = node.getUserData();
            Object userDataGroup = group.getUserData();
            this.выбор = userDataNode instanceof DbАтрибутный ? (DbАтрибутный)userDataNode : null;
            this.узел = userDataGroup instanceof DbУзел ? (DbУзел)userDataGroup : null;
        }

        @Override
        protected Boolean call() throws Exception
        {
            if( выбор == null | узел == null ) return false;
            try( Транзакция транзакция = узел.транзакция() )
            {
                транзакция.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, узел );
                Boolean удалено = (Boolean)узел.выполнить( оператор, выбор );
                транзакция.завершить( true );
                return удалено;
            }
        }
        
        @Override
        protected void succeeded()
        {
            if( getValue() )
            {
                group.getChildren().remove( node );
                LOGGER.getLogger().info( "deletion of "+узел+"."+выбор+" result="+true );
            }
            else
            {
                LOGGER.getLogger().info( "deletion of "+узел+"."+выбор+" result="+false );
            }
        }
        
        @Override
        protected void failed()
        {
            LogRecord lr = new LogRecord( Level.SEVERE, "task.delete.failed" );
            //lr.setParameters( new Object[]{ поставщик } );
            lr.setResourceBundle( LOGGER.getLogger().getResourceBundle() );
            lr.setThrown( getException() );
            LOGGER.getLogger().log( lr );
        }

    }
        
}
