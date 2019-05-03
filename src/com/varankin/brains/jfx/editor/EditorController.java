package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный.Ключ;
import com.varankin.brains.io.xml.XmlBrains;
import com.varankin.brains.io.xml.XmlSvg;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
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
    @FXML private Group zero;
    @FXML private ContextMenu popup;
    @FXML private ChoiceBox<Ключ> itemsAdd;
    @FXML private ToggleButton buttonBase;
    @FXML private ToggleButton buttonSelect;
    @FXML private ToggleButton buttonAdd;
    @FXML private ToggleButton buttonDelete;
    
    private final ObjectProperty<EventHandler<? super MouseEvent>> onMouseClickedProperty = 
           new SimpleObjectProperty<>();
    private final IntegerProperty snapProperty;
    private final IntegerProperty xProperty = new SimpleIntegerProperty();
    private final IntegerProperty yProperty = new SimpleIntegerProperty();
    private int offsetX, offsetY;
    private final LinkedList<int[]> clicks;
    private Node content;
    private Polyline path;

    public EditorController()
    {
        snapProperty = new SimpleIntegerProperty( 1 );
        offsetX = offsetY = 50;
        clicks = new LinkedList<>();
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
        
        buttonBase = new ToggleButton( "Base" );
        buttonBase.setOnAction( this::onBaseMode );
        buttonSelect = new ToggleButton( "Select" );
        buttonSelect.setOnAction( this::onSelectMode );
        buttonAdd = new ToggleButton( "Add" );
        buttonAdd.setOnAction( this::onAddMode );
        buttonDelete = new ToggleButton( "Delete" );
        buttonDelete.setOnAction( this::onDeleteMode );
        
        itemsAdd = new ChoiceBox<>();
        itemsAdd.setMinWidth( 120 );

        ToggleGroup group = new ToggleGroup();
        buttonBase.setToggleGroup(group);
        buttonSelect.setToggleGroup(group);
        buttonAdd.setToggleGroup(group);
        buttonDelete.setToggleGroup(group);
        buttonSelect.setSelected( true );
        
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
        
        posX = new Label( "0" );
        posY = new Label( "0" );
        HBox pos = new HBox( snap, new Label( " X: " ), posX, new Label( " Y: " ), posY );
        pos.setAlignment( Pos.CENTER_RIGHT );
        pos.setFillHeight( true );
        posX.setMinWidth( 40 );
        posY.setMinWidth( 40 );
        HBox.setHgrow( pos, Priority.ALWAYS );
        
        ToolBar toolBar = new ToolBar( 
                buttonBase, buttonSelect, 
                new Separator( Orientation.VERTICAL ), 
                buttonAdd, itemsAdd, new Button("Color"), buttonDelete, pos );
        
        zero = new Group();
        
        grid = new Pane();
        grid.getChildren().add( zero );
        
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
        itemsAdd.setConverter( new ConverterКлюч() );
        itemsAdd.disableProperty().bind( Bindings.not( buttonAdd.selectedProperty() ) );
        List<Node> children = grid.getChildren();
        Line line;
        Polyline pline;
        Paint paint = Color.GRAY;
        int d = 0;
        for( int x = 0; x <= 1000; x += 50 )
            for( int y = 0; y <= 1000; y += 50 )
            {
                line = new Line( x-d, y, x+d, y ); line.setStroke( paint ); children.add( line );
                //line = new Line( x, y-d, x, y+d ); line.setStroke( paint ); children.add( line );
            }
        zero.setTranslateX( offsetX );
        zero.setTranslateY( offsetY );
        children = zero.getChildren();
        d = 10;
        line = new Line( -d/2, 0, +d, 0 ); line.setStroke( paint ); children.add( line );
        line = new Line( 0, -d/2, 0, +d ); line.setStroke( paint ); children.add( line );
        int a = 3;
        pline = new Polyline( +d-a, -a, +d, 0, +d-a, +a ); pline.setStroke( paint ); children.add( pline );
        pline = new Polyline( -a, +d-a, 0, +d, +a, +d-a ); pline.setStroke( paint ); children.add( pline );
    }

    @FXML
    private void onBaseMode( ActionEvent event )
    {
        board.getChildren().remove( path );
        clicks.clear();
        onMouseClickedProperty.setValue( this::onBase );
    }
    
    @FXML
    private void onSelectMode( ActionEvent event )
    {
        board.getChildren().remove( path );
        clicks.clear();
        onMouseClickedProperty.setValue( this::onSelect );
    }
    
    @FXML
    private void onAddMode( ActionEvent event )
    {
        board.getChildren().remove( path );
        clicks.clear();
        onMouseClickedProperty.setValue( this::onAdd );
    }
    
    @FXML
    private void onDeleteMode( ActionEvent event )
    {
        board.getChildren().remove( path );
        clicks.clear();
        onMouseClickedProperty.setValue( this::onDelete );
    }
    
    @FXML
    private void onMouseMove( MouseEvent event )
    {
        updateMousePosition( event );
        if( path != null )
        {
            ObservableList<Double> points = path.getPoints();
            points.subList( points.size() - 2, points.size() ).clear();
            points.addAll( event.getX(), event.getY() );
        }
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

    public void setContent( FxЭлемент элемент )
    {
        try
        {
            NodeBuilder builder = EdtФабрика.getInstance().создать( элемент );
            content = builder.загрузить( true );
            Platform.runLater( () -> 
            { 
                itemsAdd.getItems().setAll( builder.компоненты() );
                ObservableList<Node> children = stackPane.getChildren();
                children.subList( 1, children.size() ).clear(); // кроме сетки
                board = new Pane( content );
                children.add( board );
                board.setOnMouseMoved( this::onMouseMove );
                content.getTransforms().add( 0, new Translate( offsetX, offsetY ) );
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
            LOGGER.log( Level.SEVERE, "failure to setup editor for " + элемент.название().getValue(), ex );
            String message = ex.getMessage();
            LOGGER.log(Level.SEVERE, "Exception: {0}", message != null ? message : ex.getClass().getName() );
            ex.printStackTrace();
        }
    }

    private void updateMousePosition( MouseEvent event )
    {
        int snap = snapProperty.get();
        xProperty.set( (int)Math.round( event.getX() / snap ) * snap - offsetX );
        yProperty.set( (int)Math.round( event.getY() / snap ) * snap - offsetY );
    }
    
    private void onBase( MouseEvent e )
    {
        updateMousePosition( e );
        EventTarget target = e.getTarget();
        if( target == board || target == content )
        {
            offsetX = offsetY = 50;
            zero.setTranslateX( offsetX );
            zero.setTranslateY( offsetY );
            LOGGER.getLogger().info( "rebase at board "+offsetX+","+offsetX );
        }
        else 
        {
            for( Node node = target instanceof Node ? (Node)target : null; 
                    node != null & node != content; node = node.getParent() )
            {
                Object userData = node.getUserData();
                if( userData != null )
                {
                    if( MouseButton.PRIMARY == e.getButton() )
                    {
//                        try {
//                            double layoutX = node.getLayoutX();
//                            double translateX = node.getTranslateX();
                            Transform localToSceneTransform = node.getLocalToSceneTransform();
                            Point2D sceneAnchor = localToSceneTransform.transform( 0, 0 );
                            Point2D boardAnchor = board.getLocalToSceneTransform().transform( 50, 50 );
                            Point2D offset = sceneAnchor.subtract( boardAnchor );
                            offsetX = 50 + (int)Math.round( offset.getX() );
                            offsetY = 50 + (int)Math.round( offset.getY() );
                            zero.setTranslateX( offsetX );
                            zero.setTranslateY( offsetY );
//                            Point2D d = board.getLocalToSceneTransform().inverseTransform( offset );
//                            double dx = d.getX();
//                            double dy = d.getY();
//                            double mouseX = e.getX();
//                            double mouseSceneX = e.getSceneX();
                            LOGGER.getLogger().info( "rebase at "+xProperty.get()+","+yProperty.get()+" for "+userData+"" );
                            //JavaFX.getInstance().execute( new DeleteTask( node ) );
//                        }
//                        catch( NonInvertibleTransformException ex ) {
//                            LOGGER.getLogger().log( Level.SEVERE, null, ex );
//                        }
                    }
                    break;
                }
            }
            e.consume();
            //LOGGER.getLogger().info( "rebase at "+xProperty.get()+","+yProperty.get() );
        }
        e.consume();
    }
    
    private void onSelect( MouseEvent e )
    {
        updateMousePosition( e );
        LOGGER.getLogger().info( "selection at "+xProperty.get()+","+yProperty.get() );
        board.getChildren().forEach( c -> {
            boolean touch = touch( c, e.getX(), e.getY() );
        } );
        e.consume();
    }
    
    private boolean touch( Node n, double x, double y )
    {
        boolean touched = false;
        if( n instanceof Group )
            for( Node c : ((Group)n).getChildren() )
                touched |= touch( c, x, y );
        else
        {
            int snap = snapProperty.get();
            touched = n.intersects( x - snap/2, y - snap/2, snap, snap );
            if( touched )
                LOGGER.getLogger().log( Level.INFO, "touch of {0}={1}", 
                        new Object[]{n.getClass().getSimpleName(), n.getUserData()} );
        }
        return touched;
    }
        
    private void onAdd( MouseEvent e )
    {
        updateMousePosition( e );
        if( MouseButton.PRIMARY == e.getButton() )
            if( e.getSource() == board && content instanceof Group )
            {
                int[] xy = new int[]{ xProperty.get(), yProperty.get() };
                clicks.add( xy );
                Ключ ключ = itemsAdd.getValue();
                Integer goal = limit.get( ключ.название() );
                if( goal != null && clicks.size() == goal || e.isControlDown() )
                {
                    LOGGER.getLogger().info( "addition at "+xProperty.get()+","+yProperty.get()/*+" for "+userData+""*/ );
                    JavaFX.getInstance().execute( new AddTask( ключ, clicks, (Group)content ) );
                    board.getChildren().remove( path );
                    clicks.clear();
                }
                else if( clicks.size() == 1 )
                {
                    path = new Polyline();
                    path.setFill( null );
                    path.setStroke( Color.GREY );
                    path.setStyle( "style:dashed" );
                    double px = e.getX();
                    double py = e.getY();
                    ObservableList<Double> points = path.getPoints();
                    points.addAll( px, py, px, py );
                    board.getChildren().add( path );
                }
                else
                {
                    ObservableList<Double> points = path.getPoints();
                    points.subList( points.size() - 2, points.size() ).clear();
                    double px = e.getX();
                    double py = e.getY();
                    points.addAll( px, py, px, py );
                }
            }
            else
            {
                LOGGER.getLogger().info( "missed addition at "+xProperty.get()+","+yProperty.get()+" for "+e.getSource() );
            }
        e.consume();
    }
    
    private void onDelete( MouseEvent e )
    {
        if( e.getSource() == board )
        {
            updateMousePosition( e );
            EventTarget target = e.getTarget();
            for( Node node = target instanceof Node ? (Node)target : null; 
                    node != null & node != content; node = node.getParent() )
            {
                Object userData = node.getUserData();
                if( userData != null )
                {
                    if( MouseButton.PRIMARY == e.getButton() )
                    {
                        LOGGER.getLogger().info( "deletion at "+xProperty.get()+","+yProperty.get()+" for "+userData+"" );
                        JavaFX.getInstance().execute( new DeleteTask( node ) );
                    }
                    break;
                }
            }
            e.consume();
        }
    }
    
    private static final Map<String,Integer> limit;
    static
    {
        limit = new HashMap<>();
        limit.put( XmlBrains.XML_COMPUTE, 1 );
        limit.put( XmlBrains.XML_CONVERTER, 1 );
        limit.put( XmlBrains.XML_FIELD, 1 );
        limit.put( XmlBrains.XML_FRAGMENT, 1 );
        limit.put( XmlBrains.XML_JAVA, 1 );
        limit.put( XmlBrains.XML_JOINT, 2 );
        limit.put( XmlBrains.XML_LIBRARY, 1 );
        limit.put( XmlBrains.XML_MODULE, 1 );
        limit.put( XmlBrains.XML_NOTE, 1 );
        limit.put( XmlBrains.XML_PIN, 2 );
        limit.put( XmlBrains.XML_PARAMETER, 2 );
        limit.put( XmlBrains.XML_POINT, 2 );
        limit.put( XmlBrains.XML_PROCESSOR, 1 );
        limit.put( XmlBrains.XML_PROJECT, 1 );
        limit.put( XmlBrains.XML_SENSOR, 1 );
        limit.put( XmlBrains.XML_TIMELINE, 1 );
        limit.put( XmlSvg.SVG_ELEMENT_TEXT, 1 );
        limit.put( XmlSvg.SVG_ELEMENT_LINE, 2 );
        limit.put( XmlSvg.SVG_ELEMENT_RECT, 2 );
        limit.put( XmlSvg.SVG_ELEMENT_CIRCLE, 2 );
        limit.put( XmlSvg.SVG_ELEMENT_ELLIPSE, 2 );
    }
}
