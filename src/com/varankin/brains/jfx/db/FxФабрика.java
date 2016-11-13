package com.varankin.brains.jfx.db;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.*;

/**
 * Фабрика генераторов текста в формате XML для произвольного элемента.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class FxФабрика implements Фабрика<DbАтрибутный,FxАтрибутный>
{
    private static final FxФабрика ФАБРИКА = new FxФабрика();
    
    public static final FxФабрика getInstance()
    {
        return ФАБРИКА;
    }

    private FxФабрика() {}
    
    public FxГрафика создать( DbГрафика элемент )
    {
        return new FxГрафика( (DbГрафика)элемент );
    }
    
    @SuppressWarnings("Confusing indentation")
    @Override
    public FxАтрибутный создать( DbАтрибутный элемент )
    {
        FxАтрибутный p;
        if( элемент == null ) throw new NullPointerException(); else 
        if( элемент instanceof DbБиблиотека    ) p = new FxБиблиотека( (DbБиблиотека)элемент ); else 
        if( элемент instanceof DbКонтакт       ) p = new FxКонтакт( (DbКонтакт)элемент ); else 
//        if( элемент instanceof DbЛента         ) p = new FxЛента( (DbЛента)элемент ); else 
        if( элемент instanceof DbМодуль        ) p = new FxМодуль( (DbМодуль)элемент ); else 
        if( элемент instanceof DbПараметр      ) p = new FxПараметр( (DbПараметр)элемент ); else
        if( элемент instanceof DbПоле          ) p = new FxПоле( (DbПоле)элемент ); else 
        if( элемент instanceof DbПроект        ) p = new FxПроект( (DbПроект)элемент ); else 
        if( элемент instanceof DbПроцессор     ) p = new FxПроцессор( (DbПроцессор)элемент ); else 
        if( элемент instanceof DbРасчет        ) p = new FxРасчет( (DbРасчет)элемент ); else 
        if( элемент instanceof DbСигнал        ) p = new FxСигнал( (DbСигнал)элемент ); else 
        if( элемент instanceof DbСоединение    ) p = new FxСоединение( (DbСоединение)элемент ); else 
        if( элемент instanceof DbТочка         ) p = new FxТочка( (DbТочка)элемент ); else 
        if( элемент instanceof DbФрагмент      ) p = new FxФрагмент( (DbФрагмент)элемент ); else
//        if( элемент instanceof DbПакет         ) p = new FxПакет( (DbПакет)элемент ); else
        if( элемент instanceof DbКлассJava     ) p = new FxКлассJava( (DbКлассJava)элемент ); else
        if( элемент instanceof DbКонвертер     ) p = new FxКонвертер( (DbКонвертер)элемент ); else
//        if( элемент instanceof DbЭлемент       ) p = new FxЭлемент( (DbЭлемент)элемент ); else
        if( элемент instanceof DbГрафика       ) p = new FxГрафика( (DbГрафика)элемент ); else
        if( элемент instanceof DbЗаметка       ) p = new FxЗаметка( (DbЗаметка)элемент ); else
//        if( элемент instanceof DbНеизвестный   ) p = new FxНеизвестный( (DbНеизвестный)элемент ); else
//        if( элемент instanceof DbУзел          ) p = new FxУзел( (DbУзел)элемент ); else
        if( элемент instanceof DbИнструкция    ) p = new FxИнструкция( (DbИнструкция)элемент ); else
        if( элемент instanceof DbТекстовыйБлок ) p = new FxТекстовыйБлок( (DbТекстовыйБлок)элемент ); else
        throw new UnsupportedOperationException();//p = (x) -> new TextArea("DEBUG: Loaded element will be here in the near future.");
//                                                 p = new FxАтрибутный( (DbАтрибутный)элемент );
//                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
        return p;
    }
    
}
