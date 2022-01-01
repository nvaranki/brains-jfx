package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbЭлемент;
import java.util.Collection;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlЭлемент.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 * @param <T>
 */
public class FxЭлемент<T extends DbЭлемент> extends FxУзел<T>
{
    private final ReadOnlyListProperty<FxЗаметка> ЗАМЕТКИ;
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final FxPropertyImpl<String> НАЗВАНИЕ;
    private final FxPropertyImpl<Collection<String>> СБОРКИ;

    public FxЭлемент( T элемент ) 
    {
        super( элемент );
        ЗАМЕТКИ = buildReadOnlyListProperty( элемент, "заметки", 
            new FxList<>( элемент.заметки(), элемент, FxЗаметка::new, FxАтрибутный::getSource ) );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, FxГрафика::new, FxАтрибутный::getSource ) );
        НАЗВАНИЕ  = new FxPropertyImpl<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, элемент::название, элемент::название );
        СБОРКИ    = new FxPropertyImpl<>( элемент, "сборки",   КЛЮЧ_А_СБОРКИ,   элемент::сборки,   элемент::сборки   );
    }

    public final ReadOnlyListProperty<FxЗаметка> заметки()
    {
        return ЗАМЕТКИ;
    }
    
    public final ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public FxProperty<String> название()
    {
        return НАЗВАНИЕ;
    }
    
    public FxProperty<Collection<String>> сборки()
    {
        return СБОРКИ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxЗаметка )
            результат = оператор.выполнить( (FxЗаметка)узел, заметки() );
        else if( узел instanceof FxГрафика )
            результат = оператор.выполнить( (FxГрафика)узел, графики() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
