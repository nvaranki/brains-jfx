package com.varankin.brains.jfx.browser;

import com.varankin.brains.appl.ФабрикаНазваний;
import com.varankin.brains.artificial.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.*;
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
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    
    private static final StringConverter<Byte> STRING_CONVERTER_BYTE 
            = new NumericStringConverter<>( Byte::valueOf, "byte" );
    private static final StringConverter<Short> STRING_CONVERTER_SHORT 
            = new NumericStringConverter<>( Short::valueOf, "short integer" );
    private static final StringConverter<Integer> STRING_CONVERTER_INTEGER 
            = new NumericStringConverter<>( Integer::valueOf, "integer" );
    private static final StringConverter<Long> STRING_CONVERTER_LONG 
            = new NumericStringConverter<>( Long::valueOf, "long integer" );
    private static final StringConverter<Float> STRING_CONVERTER_FLOAT 
            = new NumericStringConverter<>( Float::valueOf, "floating point" );
    private static final StringConverter<Double> STRING_CONVERTER_DOUBLE 
            = new NumericStringConverter<>( Double::valueOf, "floating point" );
    private static final StringConverter<Object> STRING_CONVERTER_OBJECT 
            = new ObjectStringConverter();

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
                    populate( e, каталог.класс( e ), каталог.ключ( e ) );
            }
            //TODO
        }
        else
            LOGGER.log( Level.WARNING, "Queried object is not an Элемент: {0}", 
                    userData != null ? userData.getClass().getName() : null );
    }

    void populate( Свойство свойство, Class класс, String ключ )
    {
        Label название = new Label( ключ + ':' );
        
        boolean изменяемое = свойство instanceof ИзменяемоеСвойство;
        boolean наблюдаемое = свойство instanceof Наблюдаемый;
        
        CheckBox dyn = new CheckBox();
        dyn.setDisable( !наблюдаемое );
        dyn.setUserData( свойство );

        Object величина = свойство.значение();
        Node node;
        if( Enum.class.isAssignableFrom( класс ) && изменяемое )
            node = makeEnumView( (Enum)величина, dyn, класс, наблюдаемое );
        else if( Boolean.class.isAssignableFrom( класс ) )
            node = makeBooleanView( (Boolean)величина, dyn, изменяемое, наблюдаемое );
        else if( Byte.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Byte)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_BYTE );
        else if( Short.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Short)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_SHORT );
        else if( Integer.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Integer)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_INTEGER );
        else if( Long.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Long)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_LONG );
        else if( Float.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Float)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_FLOAT );
        else if( Double.class.isAssignableFrom( класс ) )
            node = makeNumericView( (Double)величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_DOUBLE );
        else
            node = makeObjectView( величина, dyn, изменяемое, наблюдаемое, STRING_CONVERTER_OBJECT );
        node.focusTraversableProperty().setValue( изменяемое );
        
        dyn.setSelected( наблюдаемое );
        
        HBox панель = new HBox();
        панель.getChildren().addAll( название, node, dyn );
        
        panel.getChildren().add( панель );
    }
    
    //<editor-fold defaultstate="collapsed" desc="поля">
    
    private TextField makeObjectView( Object величина, CheckBox dyn,
            boolean изменяемое, boolean наблюдаемое, StringConverter<Object> sc )
    {
        TextField tf = new TextField();
        TextFormatter textFormatter = new TextFormatter<>( sc );
        tf.setTextFormatter( textFormatter );
        textFormatter.setValue( величина );
        tf.setEditable( изменяемое );
        if( наблюдаемое )
            dyn.selectedProperty().addListener( new DynChangeListener(
                    ((Наблюдаемый)dyn.getUserData()).наблюдатели(),
                    () -> new НаблюдательObject( tf ),
                    o -> o instanceof НаблюдательObject ) );
        return tf;
    }
    
    private <T extends Number> TextField makeNumericView( T величина, CheckBox dyn,
            boolean изменяемое, boolean наблюдаемое, StringConverter<T> sc )
    {
        TextField tf = new TextField();
        TextFormatter<T> textFormatter = new TextFormatter<>( sc );
        tf.setTextFormatter( textFormatter );
        textFormatter.setValue( величина );
        tf.setEditable( изменяемое );
        if( наблюдаемое )
            dyn.selectedProperty().addListener( new DynChangeListener(
                    ((Наблюдаемый)dyn.getUserData()).наблюдатели(),
                    () -> new НаблюдательNumber<>( tf ),
                    o -> o instanceof НаблюдательNumber ) );
        return tf;
    }
    
    private CheckBox makeBooleanView( Boolean величина, CheckBox dyn, boolean изменяемое, boolean наблюдаемое )
    {
        CheckBox cb = new CheckBox();
        cb.setSelected( величина );
        cb.setDisable( !изменяемое );
        if( наблюдаемое )
            dyn.selectedProperty().addListener( new DynChangeListener(
                    ((Наблюдаемый)dyn.getUserData()).наблюдатели(),
                    () -> new НаблюдательBoolean( cb ),
                    o -> o instanceof НаблюдательBoolean ) );
        return cb;
    }
    
    private ComboBox<Enum> makeEnumView( Enum величина, CheckBox dyn, Class класс, boolean наблюдаемое )
    {
        ComboBox<Enum> cb = new ComboBox<>();
        cb.setEditable( false );
        cb.setConverter( new EnumStringConverter( класс ) );
        for( Object constant : класс.getEnumConstants() )
            cb.getItems().add( (Enum)constant );
        cb.setValue( величина );
        if( наблюдаемое )
            dyn.selectedProperty().addListener( new DynChangeListener(
                    ((Наблюдаемый)dyn.getUserData()).наблюдатели(),
                    () -> new НаблюдательEnum( cb ),
                    o -> o instanceof НаблюдательEnum ) );
        return cb;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="наблюдатели">
    
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
            Platform.runLater( () ->
            {
                TextFormatter textFormatter = TF.getTextFormatter();
                textFormatter.setValue( изменение.АКТУАЛЬНОЕ );
            } );
        }
    }
    
    private static class НаблюдательBoolean implements Наблюдатель<Boolean>
    {
        final CheckBox CB;
        
        НаблюдательBoolean( CheckBox cb )
        {
            CB = cb;
        }
        
        @Override
        public void отклик( Изменение<Boolean> изменение )
        {
            if( изменение.АКТУАЛЬНОЕ != null )
                Platform.runLater( () -> CB.setSelected( изменение.АКТУАЛЬНОЕ ) );
            else
                Platform.runLater( () -> CB.setIndeterminate( true ) );
        }
    }
    
    private static class НаблюдательEnum implements Наблюдатель<Enum>
    {
        final ComboBox<Enum> CB;
        
        НаблюдательEnum( ComboBox<Enum> cb )
        {
            CB = cb;
        }
        
        @Override
        public void отклик( Изменение<Enum> изменение )
        {
            Platform.runLater( () -> CB.setValue( изменение.АКТУАЛЬНОЕ ) );
        }
    }
    
    private static class НаблюдательNumber<T extends Number> implements Наблюдатель<T>
    {
        final TextField TF;
        
        НаблюдательNumber( TextField tf )
        {
            TF = tf;
        }
        
        @Override
        public void отклик( Изменение<T> изменение )
        {
            Platform.runLater( () ->
            {
                TextFormatter textFormatter = TF.getTextFormatter();
                textFormatter.setValue( изменение.АКТУАЛЬНОЕ );
            } );
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="конвертеры">
    
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
    
    private static class NumericStringConverter<T extends Number> extends StringConverter<T>
    {
        final Function<String,T> P;
        final String MSG;
        
        NumericStringConverter( Function<String, T> p, String msg )
        {
            P = p;
            MSG = msg;
        }
        
        @Override
        public String toString( T e )
        {
            return e != null ? e.toString() : "";
        }
        
        @Override
        public T fromString( String s )
        {
            try
            {
                return P.apply( s );
            }
            catch( NumberFormatException e )
            {
                LOGGER.log( Level.SEVERE, "Failure to convert string \"{0}\" to {1} value.", new Object[]{ s, MSG } );
                return null;
            }
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
    
    //</editor-fold>
    
    private static class DynChangeListener implements ChangeListener<Boolean>
    {
        final Collection<Наблюдатель<?>> C;
        final Supplier<Наблюдатель<?>> S;
        final Predicate<Наблюдатель<?>> F;

        DynChangeListener( Collection<Наблюдатель<?>> c, Supplier<Наблюдатель<?>> s, Predicate<Наблюдатель<?>> f )
        {
            C = c;
            S = s;
            F = f;
        }

        @Override
        public void changed( ObservableValue<? extends Boolean> ov, Boolean before, Boolean after )
        {
            if( after )
                C.add( S.get() );
            else
                C.removeAll( C.stream().filter( F ).collect( Collectors.toList() ) );
        }
    }
    
}