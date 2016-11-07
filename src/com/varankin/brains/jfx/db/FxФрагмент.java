package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbФрагмент;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Varankine
 */
public final class FxФрагмент extends FxЭлемент<DbФрагмент>
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final StringProperty ПРОЦЕССОР;

    public FxФрагмент( DbФрагмент фрагмент ) 
    {
        super( фрагмент );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( фрагмент, "соединения", 
            new FxList<>( фрагмент.соединения(), e -> new FxСоединение( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( фрагмент, "параметры", 
            new FxList<>( фрагмент.параметры(), e -> new FxПараметр( e ), e -> e.getSource() ) );
        ПРОЦЕССОР = buildStringProperty( фрагмент, "процессор" );
    }

    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }
    
    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public StringProperty процессор()
    {
        return ПРОЦЕССОР;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxСоединение )
            результат = оператор.выполнить( (FxСоединение)узел, соединения() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
