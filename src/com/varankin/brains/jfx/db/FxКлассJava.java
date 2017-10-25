package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbКлассJava;
import com.varankin.brains.db.КороткийКлюч;
import javafx.beans.property.*;

import static com.varankin.brains.db.DbКлассJava.*;

/**
 *
 * @author Varankine
 */
public final class FxКлассJava extends FxЭлемент<DbКлассJava>
{
    static final КороткийКлюч КЛЮЧ_А_КОД = new КороткийКлюч( "код", null );

    private final ReadOnlyListProperty<FxКонвертер> КОНВЕРТЕРЫ;
    private final FxProperty<Boolean> ОСНОВНОЙ;
    private final FxProperty<String> КОД;

    public FxКлассJava( DbКлассJava элемент ) 
    {
        super( элемент );
        КОНВЕРТЕРЫ = buildReadOnlyListProperty( элемент, "конвертеры", 
            new FxList<>( элемент.конвертеры(), элемент, e -> new FxКонвертер( e ), e -> e.getSource() ) );
        ОСНОВНОЙ = new FxProperty<>( элемент, "основной", КЛЮЧ_А_ОСНОВНОЙ, () -> элемент.основной(), t -> элемент.основной( t ) );
        КОД      = new FxProperty<>( элемент, "код",      КЛЮЧ_А_КОД,      () -> элемент.код(),      t -> элемент.код( t )      );
    }
    
    
    public ReadOnlyListProperty<FxКонвертер> конвертеры()
    {
        return КОНВЕРТЕРЫ;
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
        X результат;
        if( узел instanceof FxКонвертер )
            результат = оператор.выполнить( (FxКонвертер)узел, конвертеры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
