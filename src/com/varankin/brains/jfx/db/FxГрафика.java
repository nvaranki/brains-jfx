package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbГрафика;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxГрафика extends FxУзел<DbГрафика>
{
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;

    public FxГрафика( DbГрафика графика )
    {
        super( графика );
        ГРАФИКИ = buildReadOnlyListProperty( графика, "графики", 
            new FxList<>( графика.графики(), e -> new FxГрафика( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxГрафика )
            результат = оператор.выполнить( (FxГрафика)узел, графики() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
