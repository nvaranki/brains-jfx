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
        BuilderFX<? extends Node,TabNameSpaceController> builder = new BuilderFX<>();
        builder.init( TabNameSpaceController.class,
                TabNameSpaceController.RESOURCE_FXML, TabNameSpaceController.RESOURCE_BUNDLE );
        TabNameSpaceController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.ns.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxАтрибутный)value ) );
        tabs.add( 0, tab );
        return tabs;
    }
    
    List<Tab> collectTabs( FxАрхив value )
    {
        BuilderFX<? extends Node,TabArchiveController> builder = new BuilderFX<>();
        builder.init( TabArchiveController.class,
                TabArchiveController.RESOURCE_FXML, TabArchiveController.RESOURCE_BUNDLE );
        TabArchiveController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.archive.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxАтрибутный)value ) );
        tabs.add( 0, tab );
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
        BuilderFX<? extends Node,TabNoteController> builder = new BuilderFX<>();
        builder.init( TabNoteController.class,
                TabNoteController.RESOURCE_FXML, TabNoteController.RESOURCE_BUNDLE );
        TabNoteController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.note.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxУзел)value ) );
        tabs.add( 0, tab );
        return tabs;
    }
    
    List<Tab> collectTabs( FxИнструкция value )
    {
        BuilderFX<? extends Node,TabInstructionController> builder = new BuilderFX<>();
        builder.init( TabInstructionController.class,
                TabInstructionController.RESOURCE_FXML, TabInstructionController.RESOURCE_BUNDLE );
        TabInstructionController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.instruction.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( /*collectTabs( (FxАтрибутный)value )*/ );
        tabs.add( 0, tab );
        return tabs;
    }
    
    List<Tab> collectTabs( FxКлассJava value )
    {
        BuilderFX<? extends Node,TabClassJavaController> builder = new BuilderFX<>();
        builder.init( TabClassJavaController.class,
                TabClassJavaController.RESOURCE_FXML, TabClassJavaController.RESOURCE_BUNDLE );
        TabClassJavaController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.class.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxЭлемент)value ) );
        tabs.add( 0, tab );
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
        BuilderFX<? extends Node,TabPinController> builder = new BuilderFX<>();
        builder.init( TabPinController.class,
                TabPinController.RESOURCE_FXML, TabPinController.RESOURCE_BUNDLE );
        TabPinController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.pin.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxЭлемент)value ) );
        tabs.add( 0, tab );
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
        BuilderFX<? extends Node,TabParameterController> builder = new BuilderFX<>();
        builder.init( TabParameterController.class,
                TabParameterController.RESOURCE_FXML, TabParameterController.RESOURCE_BUNDLE );
        TabParameterController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.parameter.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxЭлемент)value ) );
        tabs.add( 0, tab );
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
        BuilderFX<? extends Node,TabSignalController> builder = new BuilderFX<>();
        builder.init( TabSignalController.class,
                TabSignalController.RESOURCE_FXML, TabSignalController.RESOURCE_BUNDLE );
        TabSignalController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.signal.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        List<Tab> tabs = new ArrayList<>( collectTabs( (FxЭлемент)value ) );
        tabs.add( 0, tab );
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
        BuilderFX<? extends Node,TabTextController> builder = new BuilderFX<>();
        builder.init( TabTextController.class,
                TabTextController.RESOURCE_FXML, TabTextController.RESOURCE_BUNDLE );
        TabTextController controller = builder.getController();
        controller.set( value );
        Tab tab = new Tab( LOGGER.text( "properties.tab.text.title" ), builder.getNode() );
        tab.setOnCloseRequest( e -> controller.set( null ) );
        return Collections.singletonList( tab );
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
