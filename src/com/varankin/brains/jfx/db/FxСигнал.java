package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСигнал;

import static com.varankin.brains.db.DbСигнал.*;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
public final class FxСигнал extends FxЭлемент<DbСигнал>
{
    private final FxPropertyImpl<DbСигнал.Приоритет> ПРИОРИТЕТ;

    public FxСигнал( DbСигнал элемент ) 
    {
        super( элемент );
        ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, () -> элемент.приоритет(), (t) -> элемент.приоритет( t ) );
    }

    public FxProperty<DbСигнал.Приоритет> приоритет()
    {
        return ПРИОРИТЕТ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        return /*FxЭлемент.*/super.выполнить( оператор, узел );
    }
    
}
