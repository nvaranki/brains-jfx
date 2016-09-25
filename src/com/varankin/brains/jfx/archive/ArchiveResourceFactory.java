package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.characteristic.Именованный;
import java.util.*;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import jdk.nashorn.internal.objects.NativeString;

import static com.varankin.brains.io.xml.XmlBrains.*;

/**
 * Фабрика ресурсов для навигатора по проектам.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class ArchiveResourceFactory
{
    private static final String RB_BASE_NAME = ArchiveResourceFactory.class.getPackage().getName() + ".text";
    private static final Logger LOGGER = Logger.getLogger( ArchiveResourceFactory.class.getName(), RB_BASE_NAME );
    private static final ResourceBundle RB = LOGGER.getResourceBundle();
    private static final Map<String,String> mm, mi, xn, xi, xt;

    private ArchiveResourceFactory() {}
    
    static Node марка( DbАтрибутный элемент )
    {
        String тип = элемент.тип().название(), x;
        if( xi.containsKey( тип ) )
            x = xi.get( тип );
        else if( элемент instanceof DbИнструкция )
            x = "fragment.png";
        else if( элемент instanceof DbТекстовыйБлок )
            x = "fragment.png";
        else if( элемент instanceof DbГрафика )
            x = "preview.png";
        else 
            x = "fragment.png";
        return JavaFX.icon( "icons16x16/" + x );
    }

    static Node марка( String тип )
    {
        String x;
        if( mi.containsKey( тип ) )
            x = mi.get( тип );
        else 
            x = "fragment.png";
        return JavaFX.icon( "icons16x16/" + x );
    }

    static String название( DbАтрибутный элемент )
    {
        String тип = элемент.тип().название();
        String текст;
        if( элемент instanceof DbИнструкция )
            текст = RB.getString( "collection.instruction.1" );
        else if( элемент instanceof DbТекстовыйБлок )
            текст = RB.getString( "collection.text.1" );
        else if( элемент instanceof DbМусор )
            текст = RB.getString( "collection.waste.1" );
        else if( элемент instanceof DbNameSpace )
            текст = RB.getString( "collection.namespace.1" );
        else if( элемент instanceof DbГрафика )
            текст = RB.getString( "collection.graphic.1" );
        else if( элемент instanceof DbАрхив )
            текст = RB.getString( "collection.archive.1" );
        else if( элемент instanceof Именованный )
        {
            текст = ((Именованный)элемент).название();
            if( текст == null || текст.trim().isEmpty() )
                текст = названиеПоТипу( тип );
        }
        else 
            текст = названиеПоТипу( тип );
        return текст;
    }

    private static String названиеПоТипу( String тип )
    {
        if( xn.containsKey( тип ) ) 
            return RB.getString( "collection." + xn.get( тип ) + ".1" );
        else
            return RB.getString( "collection.other.1" );
    }

    static String метка( String id )
    {
        String key = "collection." + mm.get( id ) + ".n" ;
        if( RB.containsKey( key ) )
            return RB.getString( key );
        else 
            return id;
    }

    static Tooltip подсказка( DbАтрибутный элемент )
    {
        String тип = элемент.тип().название();
        String текст;
        if( элемент instanceof DbИнструкция )
            текст = RB.getString( "cell.instruction" );
        else if( элемент instanceof DbТекстовыйБлок )
            текст = RB.getString( "cell.text" );
        else if( элемент instanceof DbМусор )
            текст = RB.getString( "cell.basket" );
        else if( элемент instanceof DbNameSpace )
            текст = RB.getString( "cell.namespace" );
        else if( элемент instanceof DbГрафика )
            текст = RB.getString( "cell.graphic" );
        else if( элемент instanceof DbАрхив )
            текст = RB.getString( "cell.archive" );
        else 
            текст = xt.containsKey( тип ) ? xt.get( тип ) : "cell.unknown";
        return текст != null && !текст.trim().isEmpty() && RB.containsKey( текст ) ? 
                new Tooltip( RB.getString( текст ) ) : null;
    }
    
    static
    {
        mm = new HashMap<>();
        mm.put( "тексты", "text" );
        mm.put( "пакеты", "package" );
        mm.put( "мусор", "waste" );
        mm.put( "инструкции", "instruction" );
        mm.put( "прочее", "other" );
        mm.put( "namespaces", "namespace" );
        mm.put( "библиотеки", "library" );
        mm.put( "проекты", "project" );
        mm.put( "сигналы", "signal" );
        mm.put( "фрагменты", "fragment" );
        mm.put( "заметки", "note" );
        mm.put( "процессоры", "processor" );
        mm.put( "параметры", "parameter" );
        mm.put( "классы", "class" );
        mm.put( "соединения", "connection" );
        mm.put( "контакты", "pin" );
        mm.put( "графики", "graphic" );
        mm.put( "конвертеры", "converter" );
        mm.put( "расчеты", "compute" );
        mm.put( "модули", "module" );
        mm.put( "ленты", "timeline" );
        mm.put( "поля", "field" );
        mm.put( "сенсоры", "sensor" );
        mm.put( "точки", "point" );
    }

    static
    {
        mi = new HashMap<>();
        mi.put( "тексты", "text" );
        mi.put( "пакеты", "package" );
        mi.put( "мусор", "remove.png" );
        mi.put( "инструкции", "instruction" );
        mi.put( "прочее", "other" );
        mi.put( "namespaces", "namespace" );
        mi.put( "библиотеки", "new-library.png" );
        mi.put( "проекты", "new-project.png" );
        mi.put( "сигналы", "signal.png" );
        mi.put( "фрагменты", "fragment.png" );
        mi.put( "заметки", "properties.png" );
        mi.put( "процессоры", "processor2.png" );
        mi.put( "параметры", "parameter" );
        mi.put( "классы", "JavaIcon.gif" );
        mi.put( "соединения", "connector.png" );
        mi.put( "контакты", "pin.png" );
        mi.put( "графики", "preview.png" );
        mi.put( "конвертеры", "converter" );
        mi.put( "расчеты", "function.png" );
        mi.put( "модули", "module.png" );
        mi.put( "ленты", "timeline" );
        mi.put( "поля", "field2.png" );
        mi.put( "сенсоры", "sensor.png" );
        mi.put( "точки", "point.png" );
    }

    static
    {
        xn = new HashMap<>();
//        xn.put( null, "text" );
        xn.put( XML_BRAINS, "package" );
//        xn.put( "мусор", "waste" );
//        xn.put( "инструкции", "instruction" );
//        xn.put( "прочее", "other" );
//        xn.put( "namespaces", "namespace" );
        xn.put( XML_LIBRARY, "library" );
        xn.put( XML_PROJECT, "project" );
        xn.put( XML_SIGNAL, "signal" );
        xn.put( XML_FRAGMENT, "fragment" );
        xn.put( XML_NOTE, "note" );
        xn.put( XML_PROCESSOR, "processor" );
        xn.put( XML_PARAMETER, "parameter" );
        xn.put( XML_JAVA, "class" );
        xn.put( XML_JOINT, "connection" );
        xn.put( XML_PIN, "pin" );
//        xn.put( "графики", "graphic" );
        xn.put( XML_CONVERTER, "converter" );
        xn.put( XML_COMPUTE, "compute" );
        xn.put( XML_MODULE, "module" );
        xn.put( XML_TIMELINE, "timeline" );
        xn.put( XML_FIELD, "field" );
        xn.put( XML_SENSOR, "sensor" );
        xn.put( XML_POINT, "point" );
    }

    static
    {
        xi = new HashMap<>();
//        xi.put( null, "text" );
        xi.put( XML_BRAINS, "package" );
//        xi.put( "мусор", "waste" );
//        xi.put( "инструкции", "instruction" );
//        xi.put( "прочее", "other" );
//        xi.put( "namespaces", "namespace" );
        xi.put( XML_LIBRARY, "new-library.png" );
        xi.put( XML_PROJECT, "new-project.png" );
        xi.put( XML_SIGNAL, "signal.png" );
        xi.put( XML_FRAGMENT, "fragment.png" );
        xi.put( XML_NOTE, "properties.png" );
        xi.put( XML_PROCESSOR, "processor2.png" );
        xi.put( XML_PARAMETER, "parameter" );
        xi.put( XML_JAVA, "JavaIcon.gif" );
        xi.put( XML_JOINT, "connector.png" );
        xi.put( XML_PIN, "pin.png" );
//        xi.put( "графики", "graphic" );
        xi.put( XML_CONVERTER, "converter" );
        xi.put( XML_COMPUTE, "function.png" );
        xi.put( XML_MODULE, "module.png" );
        xi.put( XML_TIMELINE, "timeline" );
        xi.put( XML_FIELD, "field2.png" );
        xi.put( XML_SENSOR, "sensor.png" );
        xi.put( XML_POINT, "point.png" );
    }
    
    static
    {
        xt = new HashMap<>();
//        xt.put( null, "text" );
        xt.put( XML_BRAINS, "cell.package" );
//        xt.put( "мусор", "waste" );
//        xt.put( "инструкции", "instruction" );
//        xt.put( "прочее", "other" );
//        xt.put( "namespaces", "namespace" );
        xt.put( XML_LIBRARY, "cell.library" );
        xt.put( XML_PROJECT, "cell.project" );
        xt.put( XML_SIGNAL, "cell.signal" );
        xt.put( XML_FRAGMENT, "cell.fragment" );
        xt.put( XML_NOTE, "cell.note" );
        xt.put( XML_PROCESSOR, "cell.processor" );
        xt.put( XML_PARAMETER, "cell.parameter" );
        xt.put( XML_JAVA, "cell.class.java" );
        xt.put( XML_JOINT, "cell.connector" );
        xt.put( XML_PIN, "cell.pin" );
//        xt.put( "графики", "graphic" );
        xt.put( XML_CONVERTER, "cell.converter" );
        xt.put( XML_COMPUTE, "cell.compute" );
        xt.put( XML_MODULE, "cell.module" );
        xt.put( XML_TIMELINE, "cell.timeline" );
        xt.put( XML_FIELD, "cell.field" );
        xt.put( XML_SENSOR, "cell.sensor" );
        xi.put( XML_POINT, "cell.point" );
    }
    
}
