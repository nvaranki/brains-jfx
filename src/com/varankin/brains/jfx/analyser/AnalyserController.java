package com.varankin.brains.jfx.analyser;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Builder;

import static com.varankin.brains.jfx.JavaFX.icon;

/**
 * FXML-контроллер панели графиков. 
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class AnalyserController implements Builder<Node>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( AnalyserController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/Analyser.css";
    private static final String CSS_CLASS = "analyser";
    
    static final String RESOURCE_FXML  = "/fxml/analyser/Analyser.fxml";
    static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();
    
    private final ChangeListener<Scene> lifeCycleListener;
    
    private TimeLineSetupStage setup;

    @FXML private BorderPane pane;
    @FXML private Pane toolbar;
    @FXML private Button buttonRemoveAll;
    @FXML private ToggleButton buttonDynamic;
    @FXML private VBox box;
    @FXML private ContextMenu popup;
    @FXML private MenuItem menuItemAdd;

    public AnalyserController()
    {
        lifeCycleListener = new LifeCycleListener();
    }
    
    /**
     * Создает панель графиков. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель графиков. 
     */
    @Override
    public Pane build()
    {
        Button buttonAdd = new Button( LOGGER.text( "analyser.popup.add" ) );
        buttonAdd.setOnAction( this::onActionAddTimeLine );
        
        buttonRemoveAll = new Button( LOGGER.text( "analyser.popup.clean" ) );
        buttonRemoveAll.setId( "buttonRemoveAll" );
        buttonRemoveAll.setGraphic( icon( "icons16x16/remove.png" ) );
        buttonRemoveAll.setOnAction( this::onActionRemoveAllTimeLines );
        
        buttonDynamic = new ToggleButton( LOGGER.text( "analyser.popup.pause" ) );
        buttonDynamic.setId( "buttonDynamic" );
        buttonDynamic.setGraphic( icon( "icons16x16/pause.png" ) );
        buttonDynamic.setSelected( false );
        
        toolbar = new HBox();
        toolbar.setId( "toolbar" );
        toolbar.getChildren().addAll( buttonAdd, buttonRemoveAll, buttonDynamic );
        
        menuItemAdd = new MenuItem( LOGGER.text( "analyser.popup.add" ) );
        menuItemAdd.setId( "menuItemAdd" );
        menuItemAdd.setOnAction( this::onActionAddTimeLine );
        
        popup = new ContextMenu();
        popup.getItems().add( menuItemAdd );

        box = new VBox();
        box.setId( "box" );
        box.setFillWidth( true );
        box.setFocusTraversable( true );
        
        ScrollPane roll = new ScrollPane();
        roll.setContent( box );
        roll.setContextMenu( popup );
        roll.setFitToWidth( true );
        roll.setHbarPolicy( ScrollPane.ScrollBarPolicy.NEVER );
        roll.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );

        pane = new BorderPane();
        pane.setTop( toolbar );
        pane.setCenter( roll );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        buttonRemoveAll.disableProperty().bind( Bindings.lessThan( 
                Bindings.size( box.getChildren() ), 1 ) );
        pane.sceneProperty().addListener( new WeakChangeListener<>( lifeCycleListener ) ); 
    }
    
    @FXML
    private void onActionAddTimeLine( ActionEvent e )
    {
        if( setup == null )
        {
            setup = new TimeLineSetupStage( this::resetToDefaults, this::createNewTimeline );
            setup.initModality( Modality.APPLICATION_MODAL );
            setup.initOwner( JavaFX.getInstance().платформа );
            setup.setTitle( LOGGER.text( "analyser.popup.add" ) );
        }
        setup.showAndWait();
    }
    
    @FXML
    private void onActionRemoveAllTimeLines( ActionEvent e )
    {
        boolean confirmed = true;//TODO popup and ask again
        if( confirmed ) 
            box.getChildren().clear();
    }

    void resetToDefaults( TimeLineSetupController controller )
    {
        Font font = new Text().getFont();
        
        TimeRulerPropertiesPaneController tpc = controller.timeRulerController();
        tpc.unitProperty().setValue( TimeUnit.MILLISECONDS );
        tpc.durationProperty().setValue( 10000L );
        tpc.excessProperty().setValue( 200L );
        tpc.textColorProperty().setValue( Color.BLACK );
        tpc.tickColorProperty().setValue( Color.BLACK );
        tpc.textFontProperty().setValue( font );
        tpc.resetColorPicker();
        
        ValueRulerPropertiesPaneController vpc = controller.valueRulerController();
        vpc.valueMinProperty().setValue(  0f );
        vpc.valueMaxProperty().setValue( +1f );
        vpc.textColorProperty().setValue( Color.BLACK );
        vpc.tickColorProperty().setValue( Color.BLACK );
        vpc.textFontProperty().setValue( font );
        vpc.resetColorPicker();

        GraphPropertiesPaneController gpc = controller.graphAreaController();
        gpc.labelProperty().setValue( "" );
        gpc.rateUnitProperty().setValue( TimeUnit.MILLISECONDS );
        gpc.rateValueProperty().setValue( 20L );
        gpc.borderColorProperty().setValue( Color.BLACK );
        gpc.borderDisplayProperty().setValue( false );
        gpc.zeroColorProperty().setValue( Color.GRAY );
        gpc.zeroDisplayProperty().setValue( true );
        gpc.timeFlowProperty().setValue( true );
        gpc.resetColorPicker();
    }

    void createNewTimeline( TimeLineSetupController setupController )
    {
        BuilderFX<Pane,TimeLineController> builder = new BuilderFX<>();
        builder.init( TimeLineController.class, 
                TimeLineController.RESOURCE_FXML, TimeLineController.RESOURCE_BUNDLE );
        TimeLineController controller = builder.getController();
        controller.reset( setupController );
        controller.extendPopupMenu( popup.getItems() );
        controller.addFlowListenerTo( buttonDynamic.selectedProperty(), false );
        box.getChildren().addAll( builder.getNode() );
    }
    
    private class LifeCycleListener implements ChangeListener<Scene>
    {
        @Override
        public void changed( ObservableValue<? extends Scene> __, 
                            Scene oldValue, Scene newValue )
        {
            if( newValue != null )
            {
                // всё уже настроено
            }
            else if( oldValue != null )
            {
                buttonRemoveAll.disableProperty().unbind();
                // удалить все графики, чтобы они отключили мониторинг по такому случаю
                box.getChildren().clear();
            }
        }
    }

}
