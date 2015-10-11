package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.ИзменяемоеСвойство;
import com.varankin.characteristic.Именованный;
import com.varankin.characteristic.Наблюдаемый;
import com.varankin.characteristic.Наблюдатель;
import com.varankin.characteristic.Свойственный;
import com.varankin.characteristic.Свойство;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Builder;
import javafx.util.StringConverter;

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
                Свойственный.Каталог каталог = ((Свойственный)элемент).свойства();
                for( Свойство<?> e : каталог.перечень() )
                    populate( каталог.ключ( e ), e, каталог.класс( e ) );
            }
            //TODO
        }
        else
            LOGGER.log( Level.WARNING, "Queried object is not an Элемент: {0}", 
                    userData != null ? userData.getClass().getName() : null );
    }

    void populate( String ключ, Свойство свойство, Class класс )
    {
        Label название = new Label( ключ + ':' );
        Object величина = свойство.значение();
        boolean изменяемое = свойство instanceof ИзменяемоеСвойство;
        boolean наблюдаемое = свойство instanceof Наблюдаемый;
        Node node;
        CheckBox dyn = new CheckBox();
        dyn.setDisable( !наблюдаемое );
        dyn.setUserData( свойство );
        //TODO ключ as type selector
        if( величина instanceof Enum && изменяемое )
        {
            ComboBox<Enum> cb = new ComboBox<>();
            cb.setEditable( false );
            Class cls = величина.getClass();
            cb.setConverter( new EnumStringConverter( cls ) );
            for( Object constant : cls.getEnumConstants() )
                cb.getItems().add( (Enum)constant );
            cb.setValue( (Enum)величина );
            node = cb;
        }
        else if( величина instanceof Boolean )
        {
            CheckBox cb = new CheckBox();
            cb.setSelected( (Boolean)величина );
            cb.setDisable( !изменяемое );
            node = cb;
        }
        else
        {
            TextField tf = new TextField();
            TextFormatter textFormatter = new TextFormatter<Object>( new ObjectStringConverter() );
            tf.setTextFormatter( textFormatter );
            if( величина != null )
                textFormatter.setValue( величина );
            tf.editableProperty().setValue( изменяемое );
            if( наблюдаемое )
            {
                dyn.selectedProperty().addListener( new ChangeListener<Boolean>() 
                {

                    @Override
                    public void changed( ObservableValue<? extends Boolean> ov, Boolean before, Boolean after ) 
                    {
                        Наблюдаемый наблюдаемый = (Наблюдаемый)dyn.getUserData();
                        Collection<Наблюдатель<?>> наблюдатели = ((Наблюдаемый)dyn.getUserData()).наблюдатели();
                        if( after )
                        {
                            наблюдатели.add( new НаблюдательObject( tf ) );
                        }
                        else
                        {
                            наблюдатели.removeAll( наблюдатели.stream().filter( o -> o instanceof НаблюдательObject )
                                    .collect( Collectors.toList() ) );
                        }
                    }
                });
            }
            node = tf;
        }
        node.focusTraversableProperty().setValue( изменяемое );
        dyn.setSelected( наблюдаемое );
        
        HBox панель = new HBox();
        панель.getChildren().addAll( название, node, dyn );
        
        panel.getChildren().add( панель );
    }
    
    private static class НаблюдательObject implements Наблюдатель 
    {
        final TextField TF;

        НаблюдательObject( TextField tf ) 
        {
            TF = tf;
        }

        @Override
        public void отклик( Изменение изменение ) 
        {
            if( изменение.АКТУАЛЬНОЕ != null )
                Platform.runLater( () -> 
                {
                    TextFormatter textFormatter = TF.getTextFormatter();
                    textFormatter.setValue( изменение.АКТУАЛЬНОЕ );
                } );
            else
                TF.setText( null );
        }
    }
    
    private static class EnumStringConverter extends StringConverter<Enum> 
    {
        final Class cls;

        public EnumStringConverter( Class cls ) 
        {
            this.cls = cls;
        }

        @Override
        public String toString( Enum e )
        {
            return e.name();
        }

        @Override
        public Enum fromString( String s )
        {
            return Arrays.stream( cls.getEnumConstants() ).map( o -> (Enum)o )
                    .filter( e -> e.name().equals( s ) ).findFirst().orElse( null );
        }
    }
    
    private static class ObjectStringConverter extends StringConverter<Object> 
    {
        @Override
        public String toString( Object e )
        {
            return e != null ? e.toString() : "";//e instanceof Number ? ((Number)e).;
        }

        @Override
        public Object fromString( String s )
        {
            return s;//TODO
//                    Arrays.stream( cls.getEnumConstants() ).map( o -> (Enum)o )
//                    .filter( e -> e.name().equals( s ) ).findFirst().orElse( null );
        }
    }
    
}
