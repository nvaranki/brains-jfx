package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЭлемент;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 * @param <T>
 */
public class FxЭлемент<T extends DbЭлемент> extends FxУзел<T>
{
    private final ReadOnlyListProperty<FxЗаметка> ЗАМЕТКИ;
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final Property<String> НАЗВАНИЕ;

    public FxЭлемент( T элемент ) 
    {
        super( элемент );
        ЗАМЕТКИ = buildReadOnlyListProperty( элемент, "заметки", 
            new FxList<>( элемент.заметки(), e -> new FxЗаметка( e ), e -> e.getSource() ) );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), e -> new FxГрафика( e ), e -> e.getSource() ) );
        НАЗВАНИЕ = new FxProperty<>( элемент, "название" );
    }

    public final ReadOnlyListProperty<FxЗаметка> заметки()
    {
        return ЗАМЕТКИ;
    }
    
    public final ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public final Property<String> название()
    {
        return НАЗВАНИЕ;
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
