package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbСоединение;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxСоединение extends FxЭлемент<DbСоединение>
{
    private final ReadOnlyListProperty<FxКонтакт> КОНТАКТЫ;

    public FxСоединение( DbСоединение элемент ) 
    {
        super( элемент );
        КОНТАКТЫ = buildReadOnlyListProperty( элемент, "контакты", 
            new FxList<>( элемент.контакты(), элемент, FxКонтакт::new, FxАтрибутный::getSource ) );
    }

    public ReadOnlyListProperty<FxКонтакт> контакты()
    {
        return КОНТАКТЫ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКонтакт )
            результат = оператор.выполнить( (FxКонтакт)узел, контакты() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
