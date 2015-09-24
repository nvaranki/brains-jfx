package com.varankin.brains.jfx.analyser;

import com.varankin.brains.db.Базовый;
import com.varankin.brains.factory.structured.Структурный;
import com.varankin.brains.jfx.IntegerConverter;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.ObjectBindings;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.brains.Контекст;
import com.varankin.characteristic.Свойственный;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Builder;

/**
 * FXML-контроллер выбора параметров отображения наблюдаемого значения.
 * 
 * @author &copy; 2014 Николай Варанкин
 */
public final class ObservableMiscPaneController implements Builder<Pane>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( ObservableMiscPaneController.class );
    private static final String RESOURCE_CSS  = "/fxml/analyser/ObservableMiscPane.css";
    private static final String CSS_CLASS = "observable-misc-pane";

    private final SingleSelectionProperty<String> titleProperty;
    private final ReadOnlyObjectWrapper<Integer> bufferProperty;
    private final ReadOnlyBooleanWrapper validProperty;

    private int count = 0;

    @FXML private ComboBox<String> title;
    @FXML private TextField buffer;
    
    public ObservableMiscPaneController()
    {
        titleProperty = new SingleSelectionProperty<>();
        bufferProperty = new ReadOnlyObjectWrapper<>( Integer.valueOf( 
            JavaFX.getInstance().контекст.параметр( Контекст.Параметры.BUFFER ) ) );
        validProperty = new ReadOnlyBooleanWrapper();
    }

    /**
     * Создает панель для выбора и установки параметров отображения наблюдаемого значения.
     * Применяется в конфигурации без FXML.
     * 
     * @return созданная панель.
     */
    @Override
    public Pane build()
    {
        title = new ComboBox<>();
        title.setEditable( true );
        title.setId( "title" );
        title.setFocusTraversable( true );
        title.setVisibleRowCount( 5 );
        
        buffer = new TextField();
        buffer.setId( "buffer" );
        buffer.setFocusTraversable( true );
        buffer.setPrefColumnCount( 6 );
        
        GridPane pane = new GridPane();
        pane.add( new Label( LOGGER.text( "observable.setup.value.name" ) ), 0, 0 );
        pane.add( title, 1, 0 );
        pane.add( new Label( LOGGER.text( "observable.setup.buffer.name" ) ), 0, 1 );
        pane.add( buffer, 1, 1 );
        
        pane.getStyleClass().add( CSS_CLASS );
        pane.getStylesheets().add( getClass().getResource( RESOURCE_CSS ).toExternalForm() );

        initialize();
        
        return pane;
    }
        
    @FXML
    protected void initialize()
    {
        titleProperty.setModel( title.getSelectionModel() );
        Bindings.bindBidirectional( buffer.textProperty(), bufferProperty, 
                new IntegerConverter( buffer ) );
        IntegerBinding bpb = Bindings.createIntegerBinding( () ->
            {
                Integer value = bufferProperty.getValue();
                return value != null ? value : Integer.MIN_VALUE;
            }, 
            bufferProperty );
        BooleanBinding validBinding = 
            Bindings.and( 
                Bindings.greaterThanOrEqual( bpb, 0 ), 
                ObjectBindings.isNotNull( titleProperty ) );
        validProperty.bind( validBinding );
    }
    
    ReadOnlyProperty<String> titleProperty()
    {
        return titleProperty;
    }

    ReadOnlyProperty<Integer> bufferProperty()
    {
        return bufferProperty.getReadOnlyProperty();
    }

    ReadOnlyBooleanProperty validProperty()
    {
        return validProperty.getReadOnlyProperty();
    }

    /**
     * Устанавливает монитор наблюдаемого значения.
     * 
     * @param monitor монитор.
     */
    void setMonitor( Свойственный monitor )
    {
        title.getItems().clear();
        title.getItems().addAll( suggestTitles( monitor ) );
        title.selectionModelProperty().getValue().select( 0 );
    }

    /**
     * Создает набор названий объекта.
     * 
     * @param object объект.
     * @return набор названий.
     */
    @Deprecated // TODO misuse of interfaces
    private Collection<String> suggestTitles( Object object ) 
    {
        Collection<String> items = new LinkedHashSet<>();
        String suffix = "#" + Integer.toString( count++ );

        if( object instanceof Структурный )
        {
            StringBuilder text = new StringBuilder();
            for( Структурный o = (Структурный)object; o != null;  )
            {
                String name = o instanceof Базовый ? 
                        ((Базовый)o).шаблон().название() :
                        alias( basicClassOf( o.getClass(), CLASS_APPL ) );
                text.insert( 0, text.length() > 0 ? "." : "" ).insert( 0, name );
                Iterator<Структурный> it = o.вхождение().iterator();
                o = it.hasNext() ? it.next() : null;
            }
            items.add( text.append( object instanceof Базовый ? "" : suffix ).toString() );
        }
        if( object instanceof Базовый )
        {
            Базовый o = (Базовый)object;
            items.add( o.шаблон().название() );
            items.add( o.шаблон().положение( "." ) );
            String alias = alias( basicClassOf( o.шаблон().getClass(), CLASS_DB ) );
            items.add( alias + '_' + o.шаблон().название() );
            items.add( alias + suffix );
        }
        items.add( alias( basicClassOf( object.getClass(), CLASS_APPL ) ) + suffix );
        
        items.remove( "" );
        if( items.isEmpty() ) items.add( suffix );
        
        return items;
    }
    
    private static final Class CLASS_APPL = com.varankin.brains.artificial.Элемент.class;
    private static final Class CLASS_DB   = com.varankin.brains.db.Элемент.class;
    
    /**
     * Находит ближайший к {@code pattern} субинтерфейс, реализованный в {@code original).
     * 
     * @param original исследуемый класс.
     * @param pattern  базовый класс.
     * @return интерфейс или {@code null}.
     */
    private static Class implementationOf( Class original, Class pattern )
    {
        if( original == null )
        {
            return null;
        }
        else if( original.equals( pattern ) ) 
        {
            return original;
        }
        else for( Class i : original.getInterfaces() )
        {
            if( i.equals( pattern ) && original.getPackage().equals( pattern.getPackage() ) ) 
                return original;
            Class found = implementationOf( i, pattern );
            if( found != null ) return found;
        }
        
        return implementationOf( original.getSuperclass(), pattern );
    }
    
    /**
     * Понижает класс {@code original} до ближайшего к {@code pattern}, 
     * если это возможно.
     * 
     * @param original исследуемый класс.
     * @param pattern  базовый класс.
     * @return класс; возможно {@code original}; никогда {@code null}.
     */
    private static Class basicClassOf( Class original, Class pattern )
    {
        Class c = implementationOf( original, pattern );
        return c != null ? c : original;
    }
    
    /**
     * Находит публичное короткое название класса.
     * 
     * @param класс класс.
     * @return название класса. 
     */
    private static String alias( Class класс )
    {
        String simpleName = класс.getSimpleName();
        ResourceBundle rb = LOGGER.getLogger().getResourceBundle();
        String key = "element.".concat( simpleName );
        return rb.containsKey( key ) ? rb.getString( key ) : simpleName;
    }
    
}
