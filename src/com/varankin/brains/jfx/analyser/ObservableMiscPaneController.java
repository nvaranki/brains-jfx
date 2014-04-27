package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.factory.structured.Структурный;
import com.varankin.brains.db.factory.Базовый;
import com.varankin.brains.jfx.IntegerConverter;
import com.varankin.brains.jfx.ObjectBindings;
import com.varankin.brains.jfx.SingleSelectionProperty;
import com.varankin.property.PropertyMonitor;
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
        bufferProperty = new ReadOnlyObjectWrapper<>(
        //TODO DEBUG START
                1000
        //TODO DEBUG END
        );
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
        
        buffer = new TextField(
        //TODO DEBUG START
                "1000"
        //TODO DEBUG END
        );
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
    void setMonitor( PropertyMonitor monitor )
    {
        title.getItems().clear();
        title.getItems().addAll( createSuggestedTitles( monitor ) );
        title.selectionModelProperty().getValue().select( 0 );
        
    }

    private Collection<String> createSuggestedTitles( Object object ) 
    {
        Collection<String> items = new LinkedHashSet<>();
        String suffix = "#" + Integer.toString( count++ );

        if( object instanceof Структурный )
        {
            StringBuilder text = new StringBuilder();
            for( Структурный o = (Структурный)object; o != null; o = o.вхождение() )
            {
                String name = o instanceof Базовый ? 
                        ((Базовый)o).шаблон().название() :
                        alias( basicClassOf( o.getClass(), PKG_APPL ) );
                text.insert( 0, text.length() > 0 ? "." : "" ).insert( 0, name );
            }
            if( !( object instanceof Базовый ) )
                items.add( text.append( suffix ).toString() );
        }
        if( object instanceof Базовый )
        {
            Базовый o = (Базовый)object;
            items.add( o.шаблон().название() );
            items.add( o.шаблон().название( "", "." ) );
            items.add( alias( basicClassOf( o.шаблон().getClass(), PKG_DB ) ) + suffix );
        }
        items.add( alias( basicClassOf( object.getClass(), PKG_APPL ) ) + suffix );
        
        items.remove( "" );
        if( items.isEmpty() ) items.add( suffix );
        
        return items;
    }
    
    private static final Package PKG_APPL = com.varankin.brains.artificial.Элемент.class.getPackage();
    private static final Package PKG_DB   = com.varankin.brains.db.Элемент.class.getPackage();
    
    private static Class basicClassOf( Class original, Package pkg )
    {
        if( pkg.equals( original.getPackage() ) )
        {
            return original;
        }
        else for( Class i : original.getInterfaces() )
        {
            Class found = basicClassOf( i, pkg );
            if( found != null && !Object.class.equals( found ) ) 
                return found;
        }
        Class found = null;
        Class sc = original.getSuperclass();
        if( sc != null ) found = basicClassOf( sc, pkg );
        return found != null && !Object.class.equals( found ) ? found : original;
    }
    
    private static String alias( Class original )
    {
        String simpleName = original.getSimpleName();
        ResourceBundle rb = LOGGER.getLogger().getResourceBundle();
        String key = "element.".concat( simpleName );
        return rb.containsKey( key ) ? rb.getString( key ) : simpleName;
    }
    
}
