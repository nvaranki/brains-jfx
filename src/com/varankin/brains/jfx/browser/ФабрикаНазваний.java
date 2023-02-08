package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.*;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.factory.КаталогФабричныхСвойств;
import com.varankin.brains.factory.db.Базовый;
import com.varankin.characteristic.Именованный;
import com.varankin.characteristic.КаталогСвойств;
import com.varankin.characteristic.Полисвойственный;
import com.varankin.characteristic.Свойство;
import com.varankin.util.Текст;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author &copy; 2022 Николай Варанкин
 */
public class ФабрикаНазваний 
{
    private static final Logger LOGGER = Logger.getLogger( ФабрикаНазваний.class.getName() );

    private static final Class[] КЛАССЫ_СТАНДАРТНЫХ_ЭЛЕМЕНТОВ =
    {
        Источник.class, Приемник.class,
        Значение.class, Ветвь.class, Аргумент.class,
        СенсорноеПоле.class, 
        ПроцессорРасчета.class,
        Проект.class
    };
    private static final Class[] КЛАССЫ_ЭЛЕМЕНТОВ_ПО_ПОРЯДКУ 
        = AbstractIconLocator.классыЭлементовПоПорядку().toArray( new Class[0] );

    private final Текст словарь;
    
    public ФабрикаНазваний( Map<Locale.Category,Locale> специфика )
    {
        this.словарь = Текст.ПАКЕТЫ.словарь( ФабрикаНазваний.class.getPackage(), "NameBuilder", специфика );
    }
    
    public String метка( Class класс, boolean элемент )
    {
        String текст = словарь.текст( "node." + класс.getSimpleName() + ( элемент ? ".1" : ".N" ) );
        
        if( текст == null || текст.isEmpty() )
        {
            for( Class к : КЛАССЫ_СТАНДАРТНЫХ_ЭЛЕМЕНТОВ )
                if( класс == к )
                    break;
                else if( к.isAssignableFrom( класс ) )
                    return метка( к, элемент );
            
            текст = разрядить( класс.getSimpleName() ).toString();
        }
        return текст;
    }
    
    /**
     * @param элемент элемент отображения.
     * @return текст для обозначения элемента.
     */
    public String метка( Базовый элемент )
    {
        DbЭлемент шаблон = элемент.шаблон();
        if( шаблон == null )
            return метка( элемент.getClass(), true );
        else
            try( final Транзакция т = шаблон.транзакция() ) //TODO too much transactions
            {
                т.согласовать( Транзакция.Режим.ЧТЕНИЕ_БЕЗ_ЗАПИСИ, шаблон.архив() );
                String название = шаблон.название();
                т.завершить( true );
                return название;
            }
            catch( Exception ex )
            {
                LOGGER.log( Level.SEVERE, "Failure to obtain name of element.", ex );
                return метка( элемент.getClass(), true );
            }
    }
    
    /**
     * @param элемент элемент отображения.
     * @return текст для обозначения элемента.
     */
    public String метка( Полисвойственный элемент )
    {
        КаталогСвойств каталог = ((Полисвойственный)элемент).свойства();
        Свойство name = каталог.свойство( КаталогФабричныхСвойств.НАЗВАНИЕ );
        Свойство type = каталог.свойство( КаталогФабричныхСвойств.ТИП );
        StringBuilder название = new StringBuilder();//TODO ( "", "." );
        if( name != null )
        {
            Object значение = name.значение();
            if( значение != null )
                название.append( значение.toString() );
            else
                название.append( "?" );
        }
        if( type != null )
        {
            название.append( " " );
            Object значение = type.значение();
            if( значение != null )
                название.append( значение.toString() );
            else
                название.append( "?" );
        }
        return название.toString().trim().length() > 0 ? 
                название.toString() : метка( элемент.getClass(), элемент instanceof Элемент );
    }
    
    /**
     * @param элемент элемент отображения.
     * @return текст для обозначения элемента.
     */
    public String метка( Именованный элемент )
    {
        String название = ((Именованный)элемент).название();//TODO ( "", "." );
        //StringBuilder текст = new StringBuilder(  );
        return название == null || название.trim().isEmpty() ? метка( элемент.getClass(), true ) : название;
                
                //текст.append( ' ' ).append( название ).toString();
    }
    
    // копия из NeoЭлемент, эта фабрика была единственным потребителем метода.
//    public final String текст( int детализация )
//    {
//        String замена = "";
//        int близость = Integer.MAX_VALUE;
//        Документированный.Детализация группа = Документированный.Детализация.valueOf( детализация );
//        for( DbЗаметка заметка : ЗАМЕТКИ )
//        {
//            Integer значение = заметка.глубина().уровень();
//            if( Integer.valueOf( детализация ).equals( значение ) )
//            {
//                return заметка.текст( "" );
//            }
//            else if( значение != null && группа.equals( Документированный.Детализация.valueOf( значение ) ) )
//            {
//                int отклонение = Math.abs( значение - детализация );
//                if( отклонение < близость )
//                {
//                    близость = отклонение;
//                    замена = заметка.текст( "" );
//                }
//            }
//        }
//        return замена;
//    }

    public String метка( Элемент элемент )
    {
        String текст;
        if( элемент == ВСЕ_ТОЧКИ )
            текст = метка( Ветвь.class, false );
        else if( элемент == ВСЕ_ПОЛЯ )
            текст = метка( СенсорноеПоле.class, false );
//        else if( элемент == ВСЕ_СЕНСОРЫ )
//            текст = метка( Сенсор.class, false );
        else if( элемент == ВСЕ_ИСТОЧНИКИ )
            текст = метка( Источник.class, false );
        else if( элемент == ВСЕ_ПРИЕМНИКИ )
            текст = метка( Приемник.class, false );
        else if( элемент == ВСЕ_ПРОЦЕССОРЫ )
            текст = метка( ПроцессорРасчета.class, false );
        else if( элемент == ВСЕ_МЫСЛИТЕЛИ )
            текст = метка( Проект.class, false );
        else
            текст = метка( элемент.getClass(), true );
        return текст;
    }
    
    /**
     * @param объект элемент отображения.
     * @return текст для обозначения элемента.
     */
    public String метка( Object объект )
    {
        String текст;
        if( объект instanceof Базовый )
            текст = метка( (Базовый)объект );
        else if( объект instanceof Именованный )
            текст = метка( (Именованный)объект );
        else if( объект instanceof Полисвойственный)
            текст = метка( (Полисвойственный)объект );
        else if( объект instanceof Элемент )
            текст = метка( (Элемент)объект );
        else if( объект instanceof Class )
            текст = метка( (Class)объект, false );
        else if( объект != null )
            текст = метка( объект.getClass(), true );
        else
            текст = "";
        return текст != null ? текст : метка( объект.getClass(), объект instanceof Элемент );
    }
    
    static private CharSequence разрядить( CharSequence текст )
    {
        StringBuilder строка = new StringBuilder();
        for( int i = 0; i < текст.length(); i++ )
        {
            char c = текст.charAt( i );
            if( i > 0 && Character.isUpperCase( c ) )
                строка.append( ' ' );
            строка.append( c );
        }
        return строка;
    }
    
    public static int индекс( Object object )
    {
        return object != null ? индекс( object.getClass() ) : -1;
    }
    
    public static int индекс( Class<?> класс )
    {
        for( int i = 0; i < КЛАССЫ_ЭЛЕМЕНТОВ_ПО_ПОРЯДКУ.length; i++ )
            if( КЛАССЫ_ЭЛЕМЕНТОВ_ПО_ПОРЯДКУ[i].isAssignableFrom( класс ) )
                return i;
        return -1;
    }
    
    // псевдо-контейнеры
    
    public static Элемент ВСЕ_МЫСЛИТЕЛИ = new ЭлементImpl() {}; 
    public static Элемент ВСЕ_ПОЛЯ = new ЭлементImpl() {};
    public static Элемент ВСЕ_ПРОЦЕССОРЫ = new ЭлементImpl() {};
    public static Элемент ВСЕ_ФУНКЦИИ = new ЭлементImpl() {};
    public static Элемент ВСЕ_ИСТОЧНИКИ = new ЭлементImpl() {};
    public static Элемент ВСЕ_ПРИЕМНИКИ = new ЭлементImpl() {};
    public static Элемент ВСЕ_СЕНСОРЫ = new ЭлементImpl() {};
    public static Элемент ВСЕ_ТОЧКИ = new ЭлементImpl() {};
    
    @Deprecated
    static class ЭлементImpl implements Элемент
    {
//        @Override
        public Элемент вхождение() { return null; }
        
//        @Override
        public Collection<Элемент> элементы() { return Collections.emptyList(); }
    }

}
