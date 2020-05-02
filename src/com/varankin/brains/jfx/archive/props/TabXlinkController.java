package com.varankin.brains.jfx.archive.props;

import com.varankin.brains.io.xml.Xml.XLinkShow;
import com.varankin.brains.io.xml.Xml.XLinkActuate;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxТиповой;
import com.varankin.util.LoggerX;

import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров фрагмента.
 * 
 * @author &copy; 2020 Николай Варанкин
 */
public final class TabXlinkController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabXlinkController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabXlink.css";
    private static final String CSS_CLASS = "properties-tab-xlink";

    static final String RESOURCE_FXML = "/fxml/archive/TabXlink.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxТиповой образец;
    
    @FXML private Label path;
    @FXML private TextField href;
    @FXML private ChoiceBox<XLinkShow> show;
    @FXML private ChoiceBox<XLinkActuate> actuate;

    /**
     * Создает панель выбора и установки параметров.
     * Применяется в конфигурации без FXML.
     * 
     * @return панель выбора и установки параметров.
     */
    @Override
    public GridPane build()
    {
        href = new TextField();
        href.setFocusTraversable( true );
        href.setPrefColumnCount( 24 );

        path = new Label();
        
        show = new ChoiceBox<>();
        show.setFocusTraversable( true );
        
        actuate = new ChoiceBox<>();
        actuate.setFocusTraversable( true );
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setMinWidth( 90 );
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        
        GridPane pane = new GridPane();
        pane.setId( "xlink" );
        pane.getColumnConstraints().addAll( cc0, cc1 );
        pane.add( new Label( LOGGER.text( "tab.fragment.href" ) ), 0, 0 );
        pane.add( href, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.tab.instruction.result" ) ), 0, 1 );
        pane.add( path, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.fragment.show" ) ), 0, 2 );
        pane.add( show, 1, 2 );
        pane.add( new Label( LOGGER.text( "tab.fragment.actuate" ) ), 0, 3 );
        pane.add( actuate, 1, 3 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        href.textProperty().addListener( this::onHrefChanged );
        
        show.getItems().addAll( XLinkShow.values() );
        show.getItems().remove( XLinkShow.NEW );
        show.getItems().remove( XLinkShow.REPLACE );
        show.getItems().add( 0, null );
        show.setConverter( new StringToEnumConverter<>( XLinkShow.values(), "tab.fragment.show." ) );
        
        actuate.getItems().addAll( XLinkActuate.values() );
        actuate.getItems().add( 0, null );
        actuate.getItems().remove( XLinkActuate.OTHER );
        actuate.setConverter( new StringToEnumConverter<>( XLinkActuate.values(), "tab.fragment.actuate." ) );
    }
    
    void set( FxТиповой образец )
    {
        if( this.образец != null )
        {
            href.textProperty().unbindBidirectional( this.образец.ссылка() );
            show.valueProperty().unbindBidirectional( this.образец.вид() );
            actuate.valueProperty().unbindBidirectional( this.образец.реализация() );
        }
        if( образец != null )
        {
            href.textProperty().bindBidirectional( образец.ссылка() );
            show.valueProperty().bindBidirectional( образец.вид() );
            actuate.valueProperty().bindBidirectional( образец.реализация() );
        }
        this.образец = образец;
    }

    private void onHrefChanged( ObservableValue<? extends String> v, String o, String n )
    {
        Platform.runLater( () ->
        {
            String s = null;
            if( образец != null )
            {
                Object типовой = образец.экземпляр().getValue();
                if( типовой instanceof FxАтрибутный )
                {
                    FxАтрибутный<?> element = (FxАтрибутный<?>)типовой;
                    s = element.положение().getValue();
                }
            }
            path.setText( s );
        } );
    }
    
}
