package com.varankin.brains.jfx.db;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика генераторов текста в формате XML для произвольного элемента.
 * 
 * @author &copy; 2020 Николай Варанкин
 */
public final class FxФабрика implements Фабрика<DbАтрибутный,FxАтрибутный>
{
    private static final FxФабрика ФАБРИКА = new FxФабрика();
    
    public static final FxФабрика getInstance()
    {
        return ФАБРИКА;
    }
    
    private final Map<DbАрхив,FxАрхив> АРХИВЫ;

    private FxФабрика() 
    {
        АРХИВЫ = new HashMap<>();
    }
    
    public FxАрхив создать( DbАрхив элемент )
    {
        FxАрхив архив = АРХИВЫ.get( элемент );
        if( архив == null ) АРХИВЫ.put( элемент, архив = new FxАрхив( элемент ) );
        return архив;
    }
        
    public FxГрафика создать( DbГрафика элемент )
    {
        return new FxГрафика( элемент );
    }
    
    public FxNameSpace создать( DbNameSpace элемент )
    {
        return new FxNameSpace( элемент );
    }
    
    @Override
    public FxАтрибутный создать( DbАтрибутный элемент )
    {
        return создатьПоЭлементу( элемент );
    }
    
    @SuppressWarnings("Confusing indentation")
    public <T extends DbАтрибутный> FxАтрибутный<T> создатьПоЭлементу( T элемент )
    {
        FxАтрибутный p;
        if( элемент == null ) throw new NullPointerException(); else 
        if( элемент instanceof DbБиблиотека    ) p = new FxБиблиотека( (DbБиблиотека)элемент ); else 
        if( элемент instanceof DbКонтакт       ) p = new FxКонтакт( (DbКонтакт)элемент ); else 
        if( элемент instanceof DbЛента         ) p = new FxЛента( (DbЛента)элемент ); else 
        if( элемент instanceof DbМодуль        ) p = new FxМодуль( (DbМодуль)элемент ); else 
        if( элемент instanceof DbПараметр      ) p = new FxПараметр( (DbПараметр)элемент ); else
        if( элемент instanceof DbПоле          ) p = new FxПоле( (DbПоле)элемент ); else 
        if( элемент instanceof DbПроект        ) p = new FxПроект( (DbПроект)элемент ); else 
        if( элемент instanceof DbПроцессор     ) p = new FxПроцессор( (DbПроцессор)элемент ); else 
        if( элемент instanceof DbРасчет        ) p = new FxРасчет( (DbРасчет)элемент ); else 
        if( элемент instanceof DbСенсор        ) p = new FxСенсор( (DbСенсор)элемент ); else 
        if( элемент instanceof DbСигнал        ) p = new FxСигнал( (DbСигнал)элемент ); else 
        if( элемент instanceof DbСоединение    ) p = new FxСоединение( (DbСоединение)элемент ); else 
        if( элемент instanceof DbТочка         ) p = new FxТочка( (DbТочка)элемент ); else 
        if( элемент instanceof DbФрагмент      ) p = new FxФрагмент( (DbФрагмент)элемент ); else
        if( элемент instanceof DbПакет         ) p = new FxПакет( (DbПакет)элемент ); else
        if( элемент instanceof DbКлассJava     ) p = new FxКлассJava( (DbКлассJava)элемент ); else
        if( элемент instanceof DbМусор         ) p = new FxМусор( (DbМусор)элемент ); else
        if( элемент instanceof DbЭлемент       ) p = new FxЭлемент( (DbЭлемент)элемент ); else
        if( элемент instanceof DbNameSpace     ) p = new FxNameSpace( (DbNameSpace)элемент ); else
        if( элемент instanceof DbГрафика       ) p = new FxГрафика( (DbГрафика)элемент ); else
        if( элемент instanceof DbЗаметка       ) p = new FxЗаметка( (DbЗаметка)элемент ); else
        if( элемент instanceof DbИнструкция    ) p = new FxИнструкция( (DbИнструкция)элемент ); else
        if( элемент instanceof DbТекстовыйБлок ) p = new FxТекстовыйБлок( (DbТекстовыйБлок)элемент ); else
        throw new ClassCastException( элемент.getClass().getName() );//p = (x) -> new TextArea("DEBUG: Loaded element will be here in the near future.");
//                                                 p = new FxАтрибутный( (DbАтрибутный)элемент );
//                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
        return p;
    }
    
}
