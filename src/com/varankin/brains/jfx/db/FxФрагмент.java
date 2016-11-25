package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbФрагмент;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.Коммутируемый;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Varankine
 */
public final class FxФрагмент extends FxЭлемент<DbФрагмент> implements FxКоммутируемый
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final Property<String> ПРОЦЕССОР;
    private final ReadOnlyProperty<FxКоммутируемый> ЭКЗЕМПЛЯР;

    public FxФрагмент( DbФрагмент элемент ) 
    {
        super( элемент );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( элемент, "соединения", 
            new FxList<>( элемент.соединения(), элемент, e -> new FxСоединение( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        ПРОЦЕССОР = new FxProperty<>( элемент, "процессор", () -> элемент.процессор(), (t) -> элемент.процессор( t ) );
        ЭКЗЕМПЛЯР = new FxReadOnlyProperty<>( элемент, "экземпляр", this::коммутируемый );
    }
    
    @Override
    public ReadOnlyListProperty<FxСоединение> соединения()
    {
        return СОЕДИНЕНИЯ;
    }
    
    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public Property<String> процессор()
    {
        return ПРОЦЕССОР;
    }

    public ReadOnlyProperty<FxКоммутируемый> экземпляр()
    {
        return ЭКЗЕМПЛЯР;
    }

    private FxКоммутируемый коммутируемый()
    {
        DbЭлемент экземпляр = getSource().экземпляр();
        return экземпляр != null ? (FxКоммутируемый)FxФабрика.getInstance().создать( экземпляр ) : null;
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
