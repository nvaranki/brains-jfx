package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.db.type.DbЭлемент;
import static com.varankin.brains.db.xml.XmlBrains.*;
import com.varankin.brains.factory.db.Базовый;
import com.varankin.brains.factory.КаталогФабричныхСвойств;
import com.varankin.brains.factory.Контейнер;
import com.varankin.characteristic.КаталогСвойств;
import com.varankin.characteristic.Полисвойственный;
import com.varankin.characteristic.Свойство;
import com.varankin.io.resource.AbstractResourceLocator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.*;

/**
 *
 * @param <T>
 * @author &copy; 2023 Николай Варанкин
 */
public abstract class AbstractIconLocator<T> extends AbstractResourceLocator<T>
{
    private static final Logger LOGGER = Logger.getLogger( AbstractIconLocator.class.getName() );

    public AbstractIconLocator( String адрес, ClassLoader загрузчик )
    {
        super( адрес, загрузчик );
    }
    
    private String name( DbЭлемент шаблон )
    {
//        if( шаблон == null )
//            return null;
//        else
        {
            //DbЭлемент шаблон = ((Базовый)элемент).шаблон();
            String тип = шаблон != null ? шаблон.тип().НАЗВАНИЕ : "thinker"; //TODO
            return КАТАЛОГ_ТИПОВ.getOrDefault( тип, КАТАЛОГ_ТИПОВ.get( null ) );
            //return name( ((Базовый)id).шаблон() );
        }
//            return КАТАЛОГ_ТИПОВ.get( шаблон.тип().НАЗВАНИЕ );
            //return КАТАЛОГ_ШАБЛОНОВ.get( шаблон.getClass() );
    }
    
    private String name( Элемент элемент )
    {
        if( элемент instanceof Базовый )
            return name( ((Базовый)элемент).шаблон() );
        
        if( элемент instanceof Полисвойственный )
            return name( ( (Полисвойственный)элемент ) );
        
        if( элемент instanceof Контейнер )
            return name( ( (Контейнер)элемент ).вложение() );
        
        Class класс = элемент != null ? элемент.getClass() : null;
        String строка = КАТАЛОГ_ЭЛЕМЕНТОВ.get( класс );
        if( строка == null )
            if( элемент instanceof Полисвойственный)
                строка = name( (Полисвойственный)элемент );
            else if( элемент instanceof Базовый )
                строка = name( ((Базовый)элемент).шаблон() );
        return строка;
    }
    
    private String name( Полисвойственный id )
    {
        String строка = null;
        КаталогСвойств каталог = id.свойства();
        Свойство свойство = каталог.свойство( КаталогФабричныхСвойств.ТИП );
        if( свойство != null )
        {
            Object значение = свойство.значение();
            if( значение != null )
                строка = КАТАЛОГ_ТИПОВ.get( значение.toString() );
        }
        return строка;
    }
    
    private String name( String тип )
    {
        if( тип == null )
            return null;
        else
            return КАТАЛОГ_ТИПОВ.get( тип );
    }
    
    @Override
    protected String name( Object id )
    {
        if( id instanceof Элемент )
            return name( (Элемент)id );
        
        if( id instanceof DbЭлемент )
            return name( (DbЭлемент)id );
        
        //TODO other options
        
            Class класс = null;
            if( id instanceof Базовый )
            {
                DbЭлемент шаблон = ((Базовый)id).шаблон();
                if( шаблон != null )
                    класс = шаблон.getClass();
            }
            else if( id instanceof Полисвойственный)
            {
                if( класс == null )
                    класс = id.getClass();
            }
            else if( id != null )
            {
                класс = id.getClass();
            }
            LOGGER.log( Level.FINE, "Icon search for class {0}.", класс );
            if( класс != null )
                for( Map.Entry<Class,String> e : КАТАЛОГ_ЭЛЕМЕНТОВ.entrySet() )
                    if( e.getKey().isAssignableFrom( класс ) )
                        return e.getValue();
            return null;
    }
    
    private static class ClassMap<V> extends LinkedHashMap<Class,V>
    {
        @Override
        public V get( Object o )
        {
            return o instanceof Class ? get( (Class)o ) : super.get( o );
        }
        
        V get( Class<?> cls )
        {
            for( Map.Entry<Class,V> e : entrySet() )
                if( e.getKey().isAssignableFrom( cls ) )
                    return e.getValue();
            return null;
        }
    }
        
    /** Иконки базы данных */
    private static final Map<Class,String> КАТАЛОГ_ТИПОВ_БД = new ClassMap<>();
    static
    {
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbАрхив.class, null );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbПакет.class, "file-xml.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbПроект.class, "new-project.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbМодуль.class, "module.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbРасчет.class, "function.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbПроцессор.class, "load1.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbПоле.class, "field2.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbСигнал.class, "signal.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbТочка.class, "point.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbКонтакт.class, "pin.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbСоединение.class, "connector.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbФрагмент.class, "fragment.png" );
        КАТАЛОГ_ТИПОВ_БД.put(com.varankin.brains.db.type.DbБиблиотека.class, "new-library.png" );
    }
    private static final Map<Class,String> КАТАЛОГ_ШАБЛОНОВ = new ClassMap<>();
    static
    {
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbАрхив.class, null );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbПакет.class, "file-xml.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbПроект.class, "new-project.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbМодуль.class, "module.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbРасчет.class, "function.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbПроцессор.class, "load1.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbПоле.class, "field2.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbСигнал.class, "signal.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbТочка.class, "point.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbКонтакт.class, "pin.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbСоединение.class, "connector.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbФрагмент.class, "fragment.png" );
        КАТАЛОГ_ШАБЛОНОВ.put(com.varankin.brains.db.type.DbБиблиотека.class, "new-library.png" );
    }
        
    /** Иконки элементов мыслительной структуры */
    private static final Map<Class,String> КАТАЛОГ_ЭЛЕМЕНТОВ = new ClassMap<>();
    static
    {
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Аргумент.class, "point.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Ветвь.class, "point.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Значение.class, "point.png" );
        //КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.loader.Разветвитель.class, "pin.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Источник.class, "transmitter2.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Мыслитель.class, "load.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Приемник.class, "receiver2.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Проект.class, "new-project.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.ПроцессорРасчета.class, "load1.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Канал.class, "sensor.png" );
        КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.СенсорноеПоле.class, "field2.png" );
        //КАТАЛОГ_ЭЛЕМЕНТОВ.put( com.varankin.brains.artificial.Элемент.class, "fragment.png" );
    }

    /** Иконки элементов XML */
    private static final Map<String,String> КАТАЛОГ_ТИПОВ;
    static
    {
        КАТАЛОГ_ТИПОВ = new LinkedHashMap<>();
        КАТАЛОГ_ТИПОВ.put( XML_COMPUTE, "function.png" );
        КАТАЛОГ_ТИПОВ.put( XML_FIELD, "field2.png" );
        КАТАЛОГ_ТИПОВ.put( XML_FRAGMENT, "fragment.png" );
        КАТАЛОГ_ТИПОВ.put( XML_JOINT, "connector.png" );
        КАТАЛОГ_ТИПОВ.put( XML_MODULE, "module.png" );
        КАТАЛОГ_ТИПОВ.put( XML_PIN, "pin.png" );
        КАТАЛОГ_ТИПОВ.put( XML_POINT, "point.png" );
        КАТАЛОГ_ТИПОВ.put( XML_PROCESSOR, "load1.png" );
        КАТАЛОГ_ТИПОВ.put( XML_PROJECT, "new-project.png" );
        КАТАЛОГ_ТИПОВ.put( XML_SENSOR, "sensor.png" );
        КАТАЛОГ_ТИПОВ.put( XML_SIGNAL, "signal.png" );
        КАТАЛОГ_ТИПОВ.put( XML_THINKER, "load.png" );
        КАТАЛОГ_ТИПОВ.put( null, "fragment.png" ); //TODO image
    }

    //TODO Иконки для разных целей
    
    public static Set<Class> классыЭлементовПоПорядку()
    {
        return Collections.unmodifiableSet( КАТАЛОГ_ЭЛЕМЕНТОВ.keySet() );
    }

}
