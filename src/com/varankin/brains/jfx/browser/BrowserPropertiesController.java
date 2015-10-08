package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.ИзменяемоеСвойство;
import com.varankin.characteristic.Именованный;
import com.varankin.characteristic.Свойственный;
import com.varankin.characteristic.Свойство;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Builder;

/**
 * FXML-контроллер свойств элемента. 
 * 
 * @author &copy; 2015 Николай Варанкин
 */
public final class BrowserPropertiesController implements Builder<Parent>
{
    private static final Logger LOGGER = Logger.getLogger(
            BrowserPropertiesController.class.getName(),
            BrowserPropertiesController.class.getPackage().getName() + ".text" );
    private static final String RESOURCE_CSS  = "/fxml/browser/BrowserProperties.css";
    private static final String CSS_CLASS = "browser-properties";

    public static final String RESOURCE_FXML  = "/fxml/browser/BrowserProperties.fxml";
    public static final ResourceBundle RESOURCE_BUNDLE = LOGGER.getResourceBundle();
    
    private final StringProperty title;
    private final ФабрикаНазваний ФН;

    @FXML private BorderPane pane;
    @FXML private VBox panel;
//    @FXML private Pane properties;
    @FXML private Button buttonOK, buttonApply;

    public BrowserPropertiesController() 
    {
        title = new SimpleStringProperty( this, "title" );
        ФН = new ФабрикаНазваний( JavaFX.getInstance().контекст.специфика );
    }

    /**
     * Создает панель свойств элемента. 
     * Применяется в конфигурации без FXML.
     * 
     * @return панель навигатора. 
     */
    @Override
    public Parent build()
    {
        buttonOK = new Button( text( "button.ok" ) );
        buttonOK.setId( "buttonOK" );
        buttonOK.setDefaultButton( true );
        buttonOK.setOnAction( this::onActionOK );

        buttonApply = new Button( text( "button.apply" ) );
        buttonApply.setId( "buttonApply" );
        buttonApply.setOnAction( this::onActionApply );

        Button buttonCancel = new Button( text( "button.cancel" ) );
        buttonCancel.setCancelButton( true );
        buttonCancel.setOnAction( this::onActionCancel );

        HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll( buttonOK, buttonCancel, buttonApply );

        panel = new VBox();
        
        pane = new BorderPane();
        pane.setId( "pane" );
        pane.setCenter( panel );
        pane.setBottom( buttonBar );

        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );
        pane.getStyleClass().add( CSS_CLASS );
        
        initialize();
        
        return pane;
    }
    
    @FXML
    protected void initialize()
    {
        //pane.
    }
        
    @FXML
    void onActionOK( ActionEvent event )
    {
        EventHandler<ActionEvent> handler = buttonApply.getOnAction();
        if( handler != null ) handler.handle( event );
        buttonApply.getScene().getWindow().hide();
    }
    
    @FXML
    void onActionApply( ActionEvent event )
    {
        event.consume();
    }
    
    @FXML
    void onActionCancel( ActionEvent event )
    {
        buttonApply.getScene().getWindow().hide();
        event.consume();
    }
    
    StringProperty titleProperty()
    {
        return title;
    }

    static String text( String ключ )
    {
        return LOGGER.getResourceBundle().getString( ключ );
    }

    void reset(Элемент элемент) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void populate( String ключ, Свойство свойство )
    {
        Label величина = new Label( ключ + ':' );
        TextField значение = new TextField( свойство.значение().toString() );
        значение.editableProperty().setValue( свойство instanceof ИзменяемоеСвойство );
        значение.focusTraversableProperty().setValue( свойство instanceof ИзменяемоеСвойство );
        HBox панель = new HBox();
        панель.getChildren().addAll( величина, значение );
        panel.getChildren().add( панель );
    }

    void populate( WindowEvent event )
    {
        Object userData = pane.getScene().getRoot().getUserData();
        if( userData instanceof Элемент )
        {
            Элемент элемент = (Элемент)userData;
            String название = "";
            if( элемент instanceof Именованный )
                название = ((Именованный)элемент).название();
            title.setValue( String.format( RESOURCE_BUNDLE.getString( "properties.title" ), 
                    ФН.метка( элемент ), название ) );
            if( элемент instanceof Свойственный )
            {
                Map<String,Свойство<?>> свойства = ((Свойственный)элемент).свойства();
                for( Map.Entry<String,Свойство<?>> e : свойства.entrySet() )
                    populate( e.getKey(), e.getValue() );
            }
            //TODO
        }
        else
            LOGGER.log( Level.WARNING, "Queried object is not an Элемент: {0}", 
                    userData != null ? userData.getClass().getName() : null );
    }
    
}
