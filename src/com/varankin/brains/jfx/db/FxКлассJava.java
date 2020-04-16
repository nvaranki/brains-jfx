package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКлассJava;
import com.varankin.brains.db.КороткийКлюч;

import static com.varankin.brains.db.DbКлассJava.*;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
public final class FxКлассJava extends FxЭлемент<DbКлассJava>
{
    static final КороткийКлюч КЛЮЧ_А_КОД = new КороткийКлюч( "код", null );

    private final FxPropertyImpl<Boolean> ОСНОВНОЙ;
    private final FxPropertyImpl<String> КОД;

    public FxКлассJava( DbКлассJava элемент ) 
    {
        super( элемент );
        ОСНОВНОЙ = new FxPropertyImpl<>( элемент, "основной", КЛЮЧ_А_ОСНОВНОЙ, () -> элемент.основной(), t -> элемент.основной( t ) );
        КОД      = new FxPropertyImpl<>( элемент, "код",      КЛЮЧ_А_КОД,      () -> элемент.код(),      t -> элемент.код( t )      );
    }
    
    
    public FxProperty<Boolean> основной()
    {
        return ОСНОВНОЙ;
    }
    
    public FxProperty<String> код()
    {
        return КОД;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        return /*FxЭлемент.*/super.выполнить( оператор, узел );
    }
    
}
