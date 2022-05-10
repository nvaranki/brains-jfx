package com.varankin.brains.jfx.archive.multi;

import com.varankin.brains.jfx.IntegerFilter;
import com.varankin.brains.jfx.SignlessIntegerFilter;
import com.varankin.util.LoggerX;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createIntegerBinding;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Builder;

/**
 * FXML-контроллер панели диалога для выбора и установки параметров дублирования элементов.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
public final class MultiplyController implements Builder<Parent>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( MultiplyController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/Multiply.css";
    private static final String CSS_CLASS = "multiply";
    
    static final String RESOURCE_FXML = "/fxml/archive/Multiply.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private Схема схема;
    
    @FXML private Spinner<Integer> index, nx, ny;
    @FXML private TextField prefix, dx, dy;
    @FXML private CheckBox hasPrefix, hasIndex;
    @FXML private Circle c00, c10, c20, c30, c40;
    @FXML private Circle c01, c11, c21, c31, c41;
    @FXML private Circle c02, c12, c22, c32, c42;
    @FXML private Circle c03, c13, c23, c33, c43;
    @FXML private Circle c04, c14, c24, c34, c44;
    @FXML private GridPane preview;
    @FXML private Button buttonOK;
    @FXML private Label note1, note2;

    /**
     * Создает панель диалога для выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель диалога.
     */
    @Override
    public Parent build() 
    {
        prefix = new TextField();
        prefix.setId( "prefix" );
        
        hasPrefix = new CheckBox();
        hasPrefix.setId( "hasPrefix" );
        
        index = new Spinner();
        index.setId( "index" );
        index.setEditable( true );
        index.getStyleClass().add( Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL );

        hasIndex = new CheckBox();
        hasIndex.setId( "hasIndex" );
        
        nx = new Spinner();
        nx.setId( "nx" );
        nx.setEditable( true );
        nx.getStyleClass().add( Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL );
        
        ny = new Spinner();
        ny.setId( "ny" );
        ny.setEditable( true );
        ny.getStyleClass().add( Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL );

        dx = new TextField( "0" );
        dx.setId( "dx" );
        dx.setPrefColumnCount( 3 );
        dx.setAlignment( Pos.BASELINE_RIGHT );
        
        dy = new TextField( "0" );
        dy.setId( "dy" );
        dy.setPrefColumnCount( 3 );
        dy.setAlignment( Pos.BASELINE_RIGHT );
        
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment( HPos.CENTER );
        cc.setMinWidth( 20 );

        RowConstraints rc = new RowConstraints();
        rc.setValignment( VPos.CENTER );
        rc.setMinHeight( 20 );

        preview = new GridPane();
        preview.setId( "preview" );
        preview.getColumnConstraints().addAll( cc, cc, cc, cc, cc );
        preview.getRowConstraints().addAll( rc, rc, rc, rc, rc );
        
        preview.add( c00 = new Circle(  9 ), 0, 0 );
        preview.add( c10 = new Circle( 10 ), 1, 0 );
        preview.add( c20 = new Circle( 10 ), 2, 0 );
        preview.add( c30 = new Circle( 10 ), 3, 0 );
        preview.add( c40 = new Circle( 10 ), 4, 0 );
        
        preview.add( c01 = new Circle( 10 ), 0, 1 );
        preview.add( c11 = new Circle( 10 ), 1, 1 );
        preview.add( c21 = new Circle( 10 ), 2, 1 );
        preview.add( c31 = new Circle( 10 ), 3, 1 );
        preview.add( c41 = new Circle( 10 ), 4, 1 );
        
        preview.add( c02 = new Circle( 10 ), 0, 2 );
        preview.add( c12 = new Circle( 10 ), 1, 2 );
        preview.add( c22 = new Circle( 10 ), 2, 2 );
        preview.add( c32 = new Circle( 10 ), 3, 2 );
        preview.add( c42 = new Circle( 10 ), 4, 2 );
        
        preview.add( c03 = new Circle( 10 ), 0, 3 );
        preview.add( c13 = new Circle( 10 ), 1, 3 );
        preview.add( c23 = new Circle( 10 ), 2, 3 );
        preview.add( c33 = new Circle( 10 ), 3, 3 );
        preview.add( c43 = new Circle( 10 ), 4, 3 );

        preview.add( c04 = new Circle( 10 ), 0, 4 );
        preview.add( c14 = new Circle( 10 ), 1, 4 );
        preview.add( c24 = new Circle( 10 ), 2, 4 );
        preview.add( c34 = new Circle( 10 ), 3, 4 );
        preview.add( c44 = new Circle( 10 ), 4, 4 );
        
        c00.setId( "c00" );
        c00.setStrokeWidth( 2 );
        
        note1 = new Label();
        note1.setId( "note1" );
        
        note2 = new Label();
        note2.setId( "note2" );
        
        GridPane pane = new GridPane();
        pane.setId( "pane" );
        pane.add( new Label( LOGGER.text( "multiply.count" ) ), 1, 0 );
        pane.add( new Label( LOGGER.text( "multiply.offset" ) ), 2, 0 );
        pane.add( new Label( LOGGER.text( "multiply.nx" ) ), 0, 1 );
        pane.add( nx, 1, 1 );
        pane.add( dx, 2, 1 );
        pane.add( new Label( LOGGER.text( "multiply.ny" ) ), 0, 2 );
        pane.add( ny, 1, 2 );
        pane.add( dy, 2, 2 );
        pane.add( new Label( LOGGER.text( "multiply.preview" ) ), 0, 3 );
        pane.add( preview, 1, 3 );
        pane.add( note1, 0, 4, 3, 1 );
        pane.add( new Label( LOGGER.text( "multiply.prefix" ) ), 0, 5 );
        pane.add( prefix, 1, 5 );
        pane.add( hasPrefix, 2, 5 );
        pane.add( new Label( LOGGER.text( "multiply.index" ) ), 0, 6 );
        pane.add( index, 1, 6 );
        pane.add( hasIndex, 2, 6 );
        pane.add( note2, 0, 7, 3, 1 );
        
        buttonOK = new Button( LOGGER.text( "multiply.start" ) );
        buttonOK.setId( "buttonOK" );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );

        Button buttonCancel = new Button( LOGGER.text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel );
        
        BorderPane bp = new BorderPane();
        bp.setCenter( pane );
        bp.setBottom( buttonBar );

        bp.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        bp.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return bp;
    }
    
    @FXML
    protected void initialize()
    {
        ReadOnlyObjectProperty<Integer> nxValueProperty = nx.valueProperty();
        ReadOnlyObjectProperty<Integer> nyValueProperty = ny.valueProperty();
        
        prefix.disableProperty().bind( createBooleanBinding( 
                () -> !hasPrefix.isSelected(), hasPrefix.selectedProperty() ) );

        hasPrefix.selectedProperty().addListener( ( obs, o, n ) -> 
        {
            boolean rename = n || hasIndex.isSelected();
            note2.setText( rename ? LOGGER.text( "multiply.note2" ) : null );
        } );

        index.setValueFactory( new IntegerSpinnerValueFactory( 0, Integer.MAX_VALUE, 0, 1 ) );
        index.disableProperty().bind( createBooleanBinding( 
                () -> !hasIndex.isSelected(), hasIndex.selectedProperty() ) );
        index.getEditor().setAlignment( Pos.BASELINE_RIGHT );
        index.getEditor().textProperty().addListener( ( obs, o, n ) -> 
        {
            // связать ручной ввод со "стрелочками"
            SpinnerValueFactory<Integer> vf = index.getValueFactory();
            vf.setValue( vf.getConverter().fromString( n ) );
        } );
        index.getEditor().setTextFormatter( new TextFormatter<>( new SignlessIntegerFilter() ) );

        hasIndex.selectedProperty().addListener( ( obs, o, n ) -> 
        {
            boolean rename = n || hasPrefix.isSelected();
            note2.setText( rename ? LOGGER.text( "multiply.note2" ) : null );
        } );
        
        nx.setValueFactory( new IntegerSpinnerValueFactory( 1, Integer.MAX_VALUE, 1, 1 ) );
        nx.getEditor().setAlignment( Pos.BASELINE_RIGHT );
        nx.getEditor().textProperty().addListener( ( obs, o, n ) -> 
        {
            // связать ручной ввод со "стрелочками"
            SpinnerValueFactory<Integer> vf = nx.getValueFactory();
            vf.setValue( vf.getConverter().fromString( n ) );
        } );
        nx.valueProperty().addListener( ( obs, o, n ) -> 
        {
            int total = nx.getValue() * ny.getValue();
            note1.setText( total > 1 ? LOGGER.text( "multiply.note1", total ) : null );
        } );

        ny.setValueFactory( new IntegerSpinnerValueFactory( 1, Integer.MAX_VALUE, 1, 1 ) );
        ny.getEditor().setAlignment( Pos.BASELINE_RIGHT );
        ny.getEditor().textProperty().addListener( ( obs, o, n ) -> 
        {
            // связать ручной ввод со "стрелочками"
            SpinnerValueFactory<Integer> vf = ny.getValueFactory();
            vf.setValue( vf.getConverter().fromString( n ) );
        } );
        ny.valueProperty().addListener( ( obs, o, n ) -> 
        {
            int total = nx.getValue() * ny.getValue();
            note1.setText( total > 1 ? LOGGER.text( "multiply.note1", total ) : null );
        } );

        dx.textProperty().addListener( ( obs, o, n ) -> preview.setScaleX( 
                n != null && n.trim().startsWith( "-" ) ? -1d : +1d ) );
        dx.disableProperty().bind( createBooleanBinding( () -> nx.getValue() <= 1, nxValueProperty ) );
        dx.setTextFormatter( new TextFormatter( new IntegerFilter() ) );

        dy.textProperty().addListener( ( obs, o, n ) -> preview.setScaleY( 
                n != null && n.trim().startsWith( "-" ) ? -1d : +1d ) );
        dy.disableProperty().bind( createBooleanBinding( () -> ny.getValue() <= 1, nyValueProperty ) );
        dy.setTextFormatter( new TextFormatter<>( new IntegerFilter() ) );
        
        buttonOK.disableProperty().bind( Bindings.and( dx.disableProperty(), dy.disableProperty() ) );

        c00.visibleProperty().bind( createNxNyBinding( 1, 1 ) );
        c10.visibleProperty().bind( createNxNyBinding( 2, 1 ) );
        c20.visibleProperty().bind( createNxNyBinding( 3, 1 ) );
        c30.visibleProperty().bind( createNxNyBinding( 4, 1 ) );
        c40.visibleProperty().bind( createNxNyBinding( 5, 1 ) );
        
        c01.visibleProperty().bind( createNxNyBinding( 1, 2 ) );
        c11.visibleProperty().bind( createNxNyBinding( 2, 2 ) );
        c21.visibleProperty().bind( createNxNyBinding( 3, 2 ) );
        c31.visibleProperty().bind( createNxNyBinding( 4, 2 ) );
        c41.visibleProperty().bind( createNxNyBinding( 5, 2 ) );
        
        c02.visibleProperty().bind( createNxNyBinding( 1, 3 ) );
        c12.visibleProperty().bind( createNxNyBinding( 2, 3 ) );
        c22.visibleProperty().bind( createNxNyBinding( 3, 3 ) );
        c32.visibleProperty().bind( createNxNyBinding( 4, 3 ) );
        c42.visibleProperty().bind( createNxNyBinding( 5, 3 ) );
        
        c03.visibleProperty().bind( createNxNyBinding( 1, 4 ) );
        c13.visibleProperty().bind( createNxNyBinding( 2, 4 ) );
        c23.visibleProperty().bind( createNxNyBinding( 3, 4 ) );
        c33.visibleProperty().bind( createNxNyBinding( 4, 4 ) );
        c43.visibleProperty().bind( createNxNyBinding( 5, 4 ) );
        
        c04.visibleProperty().bind( createNxNyBinding( 1, 5 ) );
        c14.visibleProperty().bind( createNxNyBinding( 2, 5 ) );
        c24.visibleProperty().bind( createNxNyBinding( 3, 5 ) );
        c34.visibleProperty().bind( createNxNyBinding( 4, 5 ) );
        c44.visibleProperty().bind( createNxNyBinding( 5, 5 ) );
        
        c30.radiusProperty().bind( createRadiusBinding( nx ) );
        c31.radiusProperty().bind( createRadiusBinding( nx ) );
        c32.radiusProperty().bind( createRadiusBinding( nx ) );
//      c33.radiusProperty().bind( createRadiusBinding( nx ) );
        c34.radiusProperty().bind( createRadiusBinding( nx ) );
        
        c03.radiusProperty().bind( createRadiusBinding( ny ) );
        c13.radiusProperty().bind( createRadiusBinding( ny ) );
        c23.radiusProperty().bind( createRadiusBinding( ny ) );
//      c33.radiusProperty().bind( createRadiusBinding( ny ) );
        c43.radiusProperty().bind( createRadiusBinding( ny ) );

        c33.radiusProperty().bind( createIntegerBinding( 
                () -> nx.getValue() > 5 | ny.getValue() > 5 ? 3 : 10, nxValueProperty, nyValueProperty ) );

        // не раньше
        hasPrefix.setSelected( false );
        hasIndex.setSelected( false );
    }
    
    private IntegerBinding createRadiusBinding( Spinner<Integer> spinner )
    {
        return createIntegerBinding( () -> spinner.getValue() > 5 ? 3 : 10, spinner.valueProperty() );
    }
    
    private BooleanBinding createNxNyBinding( int x, int y )
    {
        return createBooleanBinding( () -> ny.getValue() >= y & nx.getValue() >= x, nx.valueProperty(), ny.valueProperty() );
    }

    @FXML
    void onActionOK( ActionEvent event )
    {
        event.consume();
        схема = new Схема();
        схема.префикс = hasPrefix.isSelected() ? prefix.getText().trim() : null;
        схема.индекс = hasIndex.isSelected() ? index.getValue() : null;
        схема.x.повтор = nx.getValue();
        схема.y.повтор = ny.getValue();
        схема.x.смещение = dx.isDisabled() ? 0 : toInteger( dx, 0 );
        схема.y.смещение = dy.isDisabled() ? 0 : toInteger( dy, 0 );
        buttonOK.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        event.consume();
        схема = null;
        buttonOK.getScene().getWindow().hide();
    }
    
    Схема getSchema()
    {
        return схема;
    }

    private static int toInteger( TextField field, int d ) 
    {
        String text = field.getText();
        try
        {
            return text == null || text.trim().isEmpty() ? d : Double.valueOf( text ).intValue();
        }
        catch( NumberFormatException e )
        {
            LOGGER.log( Level.WARNING, "multiply.integer.wrong", 
                    new Object[]{ text, d, e.getMessage() });
            return d;
        }
    }
    
}
