package com.varankin.brains.jfx.editor;

import com.varankin.brains.jfx.db.*;

import java.util.function.Function;

/**
 * Фабрика генераторов текста в формате XML для произвольного элемента.
 * 
 * @author &copy; 2022 Николай Варанкин
 */
public final class EdtФабрика implements Function<FxАтрибутный,NodeBuilder>
{
    private static final EdtФабрика ФАБРИКА = new EdtФабрика();
    
    public static final EdtФабрика getInstance()
    {
        return ФАБРИКА;
    }

    private EdtФабрика() {}
    
    @SuppressWarnings("Confusing indentation")
    @Override
    public NodeBuilder apply( FxАтрибутный элемент )
    {
        NodeBuilder p;
        if( элемент == null ) throw new NullPointerException(); else 
        if( элемент instanceof FxБиблиотека    ) p = new EdtБиблиотека( (FxБиблиотека)элемент ); else 
        if( элемент instanceof FxКонтакт       ) p = new EdtКонтакт( (FxКонтакт)элемент ); else 
        if( элемент instanceof FxЛента         ) p = new EdtЛента( (FxЛента)элемент ); else 
        if( элемент instanceof FxМодуль        ) p = new EdtМодуль( (FxМодуль)элемент ); else 
        if( элемент instanceof FxПараметр      ) p = new EdtПараметр( (FxПараметр)элемент ); else
        if( элемент instanceof FxПоле          ) p = new EdtПоле( (FxПоле)элемент ); else 
        if( элемент instanceof FxПроект        ) p = new EdtПроект( (FxПроект)элемент ); else 
        if( элемент instanceof FxПроцессор     ) p = new EdtПроцессор( (FxПроцессор)элемент ); else 
        if( элемент instanceof FxРасчет        ) p = new EdtРасчет( (FxРасчет)элемент ); else 
        if( элемент instanceof FxСигнал        ) p = new EdtСигнал( (FxСигнал)элемент ); else 
        if( элемент instanceof FxСенсор        ) p = new EdtСенсор ((FxСенсор)элемент ); else 
        if( элемент instanceof FxСоединение    ) p = new EdtСоединение( (FxСоединение)элемент ); else 
        if( элемент instanceof FxТочка         ) p = new EdtТочка( (FxТочка)элемент ); else 
        if( элемент instanceof FxФрагмент      ) p = new EdtФрагмент( (FxФрагмент)элемент ); else
//        if( элемент instanceof FxПакет         ) p = new EdtПакет( (FxПакет)элемент ); else
        if( элемент instanceof FxКлассJava     ) p = new EdtКлассJava( (FxКлассJava)элемент ); else
//        if( элемент instanceof FxЭлемент       ) p = new EdtЭлемент( (FxЭлемент)элемент ); else
        if( элемент instanceof FxГрафика       ) p = new EdtГрафика( (FxГрафика)элемент ); else
        if( элемент instanceof FxЗаметка       ) p = new EdtЗаметка( (FxЗаметка)элемент ); else
//        if( элемент instanceof FxНеизвестный   ) p = new EdtНеизвестный( (FxНеизвестный)элемент ); else
//        if( элемент instanceof FxУзел          ) p = new EdtУзел( (FxУзел)элемент ); else
        if( элемент instanceof FxИнструкция    ) p = new EdtИнструкция( (FxИнструкция)элемент ); else
        if( элемент instanceof FxТекстовыйБлок ) p = new EdtТекстовыйБлок( (FxТекстовыйБлок)элемент ); else
        throw new UnsupportedOperationException( EdtФабрика.class.getName()+":"+элемент);//p = (x) -> new TextArea("DEBUG: Loaded element will be here in the near future.");
//                                                 p = new EdtАтрибутный( (FxАтрибутный)элемент );
//                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
        return p;
    }
    
}
