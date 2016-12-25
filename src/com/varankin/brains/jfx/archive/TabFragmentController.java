package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxФрагмент;
import com.varankin.util.LoggerX;
import com.varankin.brains.io.xml.Xml.XLinkShow;
import com.varankin.brains.io.xml.Xml.XLinkActuate;
import com.varankin.brains.jfx.db.FxКоммутируемый;
import com.varankin.brains.jfx.db.FxЭлемент;

import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Builder;

/**
 * FXML-контроллер панели выбора и установки параметров фрагмента.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class TabFragmentController implements Builder<GridPane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TabFragmentController.class );
    private static final String RESOURCE_CSS  = "/fxml/archive/TabFragment.css";
    private static final String CSS_CLASS = "properties-tab-fragment";

    static final String RESOURCE_FXML = "/fxml/archive/TabFragment.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    private FxФрагмент фрагмент;
    
    @FXML private TextField processor;
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
        
        processor = new TextField();
        processor.setFocusTraversable( true );
        
        GridPane pane = new GridPane();
        pane.setId( "fragment" );
        pane.add( new Label( LOGGER.text( "tab.fragment.href" ) ), 0, 0 );
        pane.add( href, 1, 0 );
        pane.add( new Label( LOGGER.text( "properties.tab.instruction.result" ) ), 0, 1 );
        pane.add( path, 1, 1 );
        pane.add( new Label( LOGGER.text( "tab.fragment.show" ) ), 0, 2 );
        pane.add( show, 1, 2 );
        pane.add( new Label( LOGGER.text( "tab.fragment.actuate" ) ), 0, 3 );
        pane.add( actuate, 1, 3 );
        pane.add( new Label( LOGGER.text( "tab.fragment.processor" ) ), 0, 4 );
        pane.add( processor, 1, 4 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        href.textProperty().addListener( new ChangeListenerHref() );
        
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
    
    void set( FxФрагмент фрагмент )
    {
        if( this.фрагмент != null )
        {
            href.textProperty().unbindBidirectional( this.фрагмент.ссылка() );
            show.valueProperty().unbindBidirectional( this.фрагмент.вид() );
            actuate.valueProperty().unbindBidirectional( this.фрагмент.реализация() );
            processor.textProperty().unbindBidirectional( this.фрагмент.процессор() );
        }
        if( фрагмент != null )
        {
            href.textProperty().bindBidirectional( фрагмент.ссылка() );
            show.valueProperty().bindBidirectional( фрагмент.вид() );
            actuate.valueProperty().bindBidirectional( фрагмент.реализация() );
            processor.textProperty().bindBidirectional( фрагмент.процессор() );
        }
        this.фрагмент = фрагмент;
    }

    private class ChangeListenerHref implements ChangeListener<String>
    {

        @Override
        public void changed( ObservableValue<? extends String> v, String o, String n )
        {
            Platform.runLater( () ->
            {
                String s = null;
                if( фрагмент != null )
                {
                    FxКоммутируемый коммутируемый = фрагмент.экземпляр().getValue();
                    if( коммутируемый instanceof FxЭлемент )
                    {
                        FxЭлемент<?> element = (FxЭлемент<?>)коммутируемый;
                        s = element.положение().getValue() + element.тип().getValue().название();
                    }
                }
                path.setText( s );
            } );}
    }
    
}
