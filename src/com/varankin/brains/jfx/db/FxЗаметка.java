package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЗаметка;
import com.varankin.brains.db.КороткийКлюч;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbЗаметка.*;

/**
 *
 * @author Varankine
 */
public final class FxЗаметка extends FxУзел<DbЗаметка>
{
    static final КороткийКлюч КЛЮЧ_А_ТЕКСТ = new КороткийКлюч( "текст", null );

    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final FxReadOnlyProperty<String> ТЕКСТ;
    private final FxProperty<Long> ГЛУБИНА;

    public FxЗаметка( DbЗаметка элемент )
    {
        super( элемент );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, e -> new FxГрафика( e ), e -> e.getSource() ) );
        ТЕКСТ = new FxReadOnlyProperty<>( элемент, "текст", КЛЮЧ_А_ТЕКСТ, () -> элемент.текст( "\n" ) );
        ГЛУБИНА = new FxProperty<>( элемент, "глубина", КЛЮЧ_А_ГЛУБИНА, () -> элемент.глубина(), t -> элемент.глубина( t ) );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public FxReadOnlyProperty<String> текст()
    {
        return ТЕКСТ;
    }

    public FxProperty<Long> глубина()
    {
        return ГЛУБИНА;
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
