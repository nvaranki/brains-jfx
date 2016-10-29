package com.varankin.brains.jfx.editor;

import com.varankin.brains.artificial.io.Фабрика;
import com.varankin.brains.db.*;
import com.varankin.io.container.Provider;
import javafx.scene.control.TextArea;

/**
 * Фабрика генераторов текста в формате XML для произвольного элемента.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class EdtФабрика implements Фабрика<DbАтрибутный,NodeBuilder>
{
    private static final EdtФабрика ФАБРИКА = new EdtФабрика();
    
    public static final EdtФабрика getInstance()
    {
        return ФАБРИКА;
    }

    private EdtФабрика() {}
    
    @SuppressWarnings("Confusing indentation")
    @Override
    public NodeBuilder создать( DbАтрибутный элемент )
    {
        NodeBuilder p;
        if( элемент == null ) throw new NullPointerException(); else 
        if( элемент instanceof DbБиблиотека    ) p = new EdtБиблиотека( (DbБиблиотека)элемент ); else 
        if( элемент instanceof DbКонтакт       ) p = new EdtКонтакт( (DbКонтакт)элемент ); else 
//        if( элемент instanceof DbЛента         ) p = new EdtЛента( (DbЛента)элемент ); else 
        if( элемент instanceof DbМодуль        ) p = new EdtМодуль( (DbМодуль)элемент ); else 
        if( элемент instanceof DbПараметр      ) p = new EdtПараметр( (DbПараметр)элемент ); else
        if( элемент instanceof DbПоле          ) p = new EdtПоле( (DbПоле)элемент ); else 
        if( элемент instanceof DbПроект        ) p = new EdtПроект( (DbПроект)элемент ); else 
        if( элемент instanceof DbПроцессор     ) p = new EdtПроцессор( (DbПроцессор)элемент ); else 
        if( элемент instanceof DbРасчет        ) p = new EdtРасчет( (DbРасчет)элемент ); else 
        if( элемент instanceof DbСигнал        ) p = new EdtСигнал( (DbСигнал)элемент ); else 
        if( элемент instanceof DbСоединение    ) p = new EdtСоединение( (DbСоединение)элемент ); else 
        if( элемент instanceof DbТочка         ) p = new EdtТочка( (DbТочка)элемент ); else 
        if( элемент instanceof DbФрагмент      ) p = new EdtФрагмент( (DbФрагмент)элемент ); else
//        if( элемент instanceof DbПакет         ) p = new EdtПакет( (DbПакет)элемент ); else
        if( элемент instanceof DbКлассJava     ) p = new EdtКлассJava( (DbКлассJava)элемент ); else
        if( элемент instanceof DbКонвертер     ) p = new EdtКонвертер( (DbКонвертер)элемент ); else
//        if( элемент instanceof DbЭлемент       ) p = new EdtЭлемент( (DbЭлемент)элемент ); else
        if( элемент instanceof DbГрафика       ) p = new EdtГрафика( (DbГрафика)элемент ); else
        if( элемент instanceof DbЗаметка       ) p = new EdtЗаметка( (DbЗаметка)элемент ); else
        if( элемент instanceof DbНеизвестный   ) p = new EdtНеизвестный( (DbНеизвестный)элемент ); else
//        if( элемент instanceof DbУзел          ) p = new EdtУзел( (DbУзел)элемент ); else
        if( элемент instanceof DbИнструкция    ) p = new EdtИнструкция( (DbИнструкция)элемент ); else
        if( элемент instanceof DbТекстовыйБлок ) p = new EdtТекстовыйБлок( (DbТекстовыйБлок)элемент ); else
        throw new UnsupportedOperationException();//p = (x) -> new TextArea("DEBUG: Loaded element will be here in the near future.");
//                                                 p = new EdtАтрибутный( (DbАтрибутный)элемент );
//                content = new TextArea("DEBUG: Loaded element will be here."); //TODO not impl
        return p;
    }
    
}
