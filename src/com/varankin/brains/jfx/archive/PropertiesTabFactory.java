package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.BuilderFX;
import com.varankin.brains.jfx.db.*;
import com.varankin.util.LoggerX;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 *
 * @author Varankine
 */
final class PropertiesTabFactory
{
    private static final LoggerX LOGGER = LoggerX.getLogger( PropertiesTabFactory.class );

    List<Tab> collectTabs( FxNameSpace value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxАрхив value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxАтрибутный<?> value )
    {
        BuilderFX<? extends Node,TabAttrsController> builder = new BuilderFX<>();
        builder.init( TabAttrsController.class,
                TabAttrsController.RESOURCE_FXML, TabAttrsController.RESOURCE_BUNDLE );
        TabAttrsController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.attrs.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        return Collections.singletonList( tab );
    }
    
    List<Tab> collectTabs( FxБиблиотека value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxГрафика value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxУзел)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxЗаметка value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxУзел)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxИнструкция value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxКлассJava value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxКонвертер value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxКонтакт value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxЛента value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxМодуль value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxМусор value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxПакет value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxУзел)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxПараметр value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxПоле value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxПроект value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxПроцессор value )
    {
        BuilderFX<? extends Node,TabProcessorController> builder = new BuilderFX<>();
        builder.init( TabProcessorController.class,
                TabProcessorController.RESOURCE_FXML, TabProcessorController.RESOURCE_BUNDLE );
        TabProcessorController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.processor.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxЭлемент)value ) );
        tabs.add( 0, tab );
        return tabs;
    }
    
    List<Tab> collectTabs( FxРасчет value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxСенсор value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxСигнал value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxСоединение value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxТекстовыйБлок value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxТочка value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxУзел<?> value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxАтрибутный)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxФрагмент value )
    {
        List<Tab> tabs = new ArrayList<>();
        //TODO
        tabs.addAll( collectTabs( (FxЭлемент)value ) );
        return tabs;
    }
    
    List<Tab> collectTabs( FxЭлемент<?> value )
    {
        BuilderFX<? extends Node,TabElementController> builder = new BuilderFX<>();
        builder.init( TabElementController.class,
                TabElementController.RESOURCE_FXML, TabElementController.RESOURCE_BUNDLE );
        TabElementController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.element.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxАтрибутный)value ) );
        tabs.add( 0, tab );
        return tabs;
    }
    
    List<Tab> collectTabs( Object value )
    {
        List<Tab> tabs;
        if( value instanceof FxNameSpace     ) tabs = collectTabs( (FxNameSpace)value ); else
        if( value instanceof FxАрхив         ) tabs = collectTabs( (FxАрхив)value ); else
        if( value instanceof FxБиблиотека    ) tabs = collectTabs( (FxБиблиотека)value ); else
        if( value instanceof FxГрафика       ) tabs = collectTabs( (FxГрафика)value ); else
        if( value instanceof FxЗаметка       ) tabs = collectTabs( (FxЗаметка)value ); else
        if( value instanceof FxИнструкция    ) tabs = collectTabs( (FxИнструкция)value ); else
        if( value instanceof FxКлассJava     ) tabs = collectTabs( (FxКлассJava)value ); else
        if( value instanceof FxКонвертер     ) tabs = collectTabs( (FxКонвертер)value ); else
        if( value instanceof FxКонтакт       ) tabs = collectTabs( (FxКонтакт)value ); else
        if( value instanceof FxЛента         ) tabs = collectTabs( (FxЛента)value ); else
        if( value instanceof FxМодуль        ) tabs = collectTabs( (FxМодуль)value ); else
        if( value instanceof FxМусор         ) tabs = collectTabs( (FxМусор)value ); else
        if( value instanceof FxПакет         ) tabs = collectTabs( (FxПакет)value ); else
        if( value instanceof FxПараметр      ) tabs = collectTabs( (FxПараметр)value ); else
        if( value instanceof FxПоле          ) tabs = collectTabs( (FxПоле)value ); else
        if( value instanceof FxПроект        ) tabs = collectTabs( (FxПроект)value ); else
        if( value instanceof FxПроцессор     ) tabs = collectTabs( (FxПроцессор)value ); else
        if( value instanceof FxРасчет        ) tabs = collectTabs( (FxРасчет)value ); else
        if( value instanceof FxСенсор        ) tabs = collectTabs( (FxСенсор)value ); else
        if( value instanceof FxСигнал        ) tabs = collectTabs( (FxСигнал)value ); else
        if( value instanceof FxСоединение    ) tabs = collectTabs( (FxСоединение)value ); else
        if( value instanceof FxТекстовыйБлок ) tabs = collectTabs( (FxТекстовыйБлок)value ); else
        if( value instanceof FxТочка         ) tabs = collectTabs( (FxТочка)value ); else
        if( value instanceof FxФрагмент      ) tabs = collectTabs( (FxФрагмент)value ); else
        if( value instanceof FxЭлемент       ) tabs = collectTabs( (FxЭлемент)value ); else
        if( value instanceof FxУзел          ) tabs = collectTabs( (FxУзел)value ); else
        if( value instanceof FxАтрибутный    ) tabs = collectTabs( (FxАтрибутный)value ); else
        throw new IllegalArgumentException();
        
        return tabs;
    }        
    
}
