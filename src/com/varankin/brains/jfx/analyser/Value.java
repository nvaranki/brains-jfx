package com.varankin.brains.jfx.analyser;

import com.varankin.brains.appl.RatedObservable;
import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.artificial.rating.СтандартныйРанжировщик;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.Контекст;
import com.varankin.characteristic.*;
import com.varankin.util.LoggerX;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Значение, отображаемое на графике.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class Value
{
    static private final LoggerX LOGGER = LoggerX.getLogger( Value.class );
    static private final ResourceBundle RESOURCE_BUNDLE = LOGGER.getLogger().getResourceBundle();

    @Deprecated private final static Iterator<Color> colors = new CyclicIterator<>( Arrays.asList( Color.RED, Color.BLUE, Color.GREEN ) );
    @Deprecated private final static Iterator<int[][]> patterns = new CyclicIterator<>( Arrays.asList( DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX ) );
    
    final List<String> метки;
    final List<RatedObservable> observables;
    
    RatedObservable observable;
    Ранжируемый convertor;
    
    private final IntegerProperty bufferProperty;
    private final Property<int[][]> patternProperty;
    private final Property<Color> colorProperty;
    private final ReadOnlyObjectWrapper<Node> graphicProperty;
    private final ReadOnlyObjectWrapper<DotPainter> painterProperty;
    private final StringProperty titleProperty;
    private final BooleanProperty enabledProperty;
    
    private Наблюдатель наблюдатель;

    Value( Свойственный свойственный, String метка )
    {
        Collection<String> м = new LinkedHashSet<>();
        м.add( метка );
        метки( свойственный.getClass(), м );
        метки = Collections.unmodifiableList( new ArrayList<>( м ) );
        observables = Collections.unmodifiableList( collect( свойственный ) );
        
        bufferProperty = new SimpleIntegerProperty( Integer.valueOf( JavaFX.getInstance().контекст.параметр( Контекст.Параметры.BUFFER ) ) );
        colorProperty = new SimpleObjectProperty<>( colors.next() );
        patternProperty = new SimpleObjectProperty<>( patterns.next() );
        painterProperty = new ReadOnlyObjectWrapper<>();
        titleProperty = new SimpleStringProperty( метка );
        enabledProperty = new SimpleBooleanProperty();
        graphicProperty = new ReadOnlyObjectWrapper<>();
        
        graphicProperty.bind( Bindings.createObjectBinding( this::createIcon, colorProperty, patternProperty ) );
        painterProperty.bind( Bindings.createObjectBinding( this::createPainter, colorProperty, patternProperty, bufferProperty ) );
        
        enabledProperty.addListener( this::onStartStop );
    }

    IntegerProperty bufferProperty()
    {
        return bufferProperty;
    }

    ReadOnlyProperty<DotPainter> painterProperty()
    {
        return painterProperty.getReadOnlyProperty();
    }
    
    StringProperty titleProperty()
    {
        return titleProperty;
    }
    
    ReadOnlyProperty<Node> graphicProperty()
    {
        return graphicProperty.getReadOnlyProperty();
    }
    
    BooleanProperty enabledProperty()
    {
        return enabledProperty;
    }
    
    Property<Color> colorProperty()
    {
        return colorProperty;
    }

    Property<int[][]> patternProperty()
    {
        return patternProperty;
    }
    
    private static List<RatedObservable> collect( Свойственный свойственный )
    {
        Свойственный.Каталог каталог = свойственный.свойства();
        return каталог.перечень().stream()
                .filter( p -> p instanceof НаблюдаемоеСвойство ).map( p -> (НаблюдаемоеСвойство)p )
                .map( p -> new RatedObservable( p, каталог, RESOURCE_BUNDLE ) )
                .sorted( (p1,p2) -> p1.название().compareTo( p2.название() ) )
                .collect( Collectors.toList() );
    }

    private static void метки( Class c, Collection<String> коллектор )
    {
        while( c != null && Элемент.class.isAssignableFrom( c ) )
        {
            String simpleName = c.getSimpleName();
            String key = "element.".concat( simpleName );
            коллектор.add( RESOURCE_BUNDLE.containsKey( key ) ? RESOURCE_BUNDLE.getString( key ) : simpleName );
            for( Class i : c.getInterfaces() )
                метки( i, коллектор );
            c = c.getSuperclass();
        }
    }

    private void onStartStop( ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue )
    {
        DotPainter painter = painterProperty.getValue();
        if( painter != null )
        {
            if( newValue != null )
            {
                Collection наблюдатели = observable != null ? observable.свойство().наблюдатели() : null;
                if( newValue )
                {
                    painter.enabledProperty().setValue( newValue );
                    if( наблюдатели != null )
                        наблюдатели.add( наблюдатель = this::onObservation );
                }
                else
                {
                    painter.enabledProperty().setValue( newValue );
                    if( наблюдатели != null )
                        наблюдатели.remove( наблюдатель );
                }
            }
        }
    }

    private void onObservation( Изменение изменение )
    {
        if( convertor instanceof СтандартныйРанжировщик )
            ((СтандартныйРанжировщик)convertor).setOldValue( изменение.ПРЕЖНЕЕ );

        Dot dot = new Dot( convertor.значение( изменение.АКТУАЛЬНОЕ ), System.currentTimeMillis() );

        DotPainter painter = painterProperty.getValue();
        if( painter == null )
            LOGGER.log( Level.FINEST, "Painter of \"{0}\" is missing.", titleProperty.getValue() );
        else if( !painter.offer( dot ) )
            LOGGER.log( Level.FINEST, "Painter of \"{0}\" rejected a dot.", titleProperty.getValue() );
    }

    private Node createIcon()
    {
        if( colorProperty != null && patternProperty != null )
        {
            Color outlineColor = Color.LIGHTGRAY;
            WritableImage sample = new WritableImage( 16, 16 );
            PixelWriter pixelWriter = sample.getPixelWriter();
            for( int i = 1; i < 15; i ++ )
            {
                pixelWriter.setColor( i,  0, outlineColor );
                pixelWriter.setColor( i, 15, outlineColor );
                pixelWriter.setColor(  0, i, outlineColor );
                pixelWriter.setColor( 15, i, outlineColor );
            }
            DotPainter.paint( 7, 7, colorProperty.getValue(), patternProperty.getValue(), pixelWriter, 16, 16 );
            return new ImageView( sample );
        }
        return null;
    }

    private DotPainter createPainter()
    {
        BlockingQueue<Dot> queue = new LinkedBlockingQueue<>();
        int buffer = bufferProperty.get();
        DotPainter painter = buffer > 0 ? new BufferedDotPainter( queue, buffer ) : new DotPainter( queue );
        painter.colorProperty().setValue( colorProperty.getValue() );
        painter.patternProperty().setValue( patternProperty.getValue() );
        return painter;
    }

}
