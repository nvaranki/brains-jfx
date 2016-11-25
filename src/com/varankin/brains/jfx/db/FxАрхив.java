package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАрхив;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxАрхив extends FxАтрибутный<DbАрхив>
{
    private final ReadOnlyListProperty<FxПакет> ПАКЕТЫ;
    private final ReadOnlyListProperty<FxNameSpace> NAMESPACES;
    private final ReadOnlyListProperty<FxМусор> МУСОР;

    public FxАрхив( DbАрхив архив ) 
    {
        super( архив );
        ПАКЕТЫ = buildReadOnlyListProperty( архив, "пакеты", 
            new FxList<>( архив.пакеты(), архив, e -> new FxПакет( e ), e -> e.getSource() ) );
        NAMESPACES = buildReadOnlyListProperty( архив, "namespaces", 
            new FxList<>( архив.namespaces(), архив, e -> new FxNameSpace( e ), e -> e.getSource() ) );
        МУСОР = buildReadOnlyListProperty( архив, "мусор", 
            new FxList<>( архив.мусор(), архив, e -> new FxМусор( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxПакет> пакеты()
    {
        return ПАКЕТЫ;
    }
    
    public ReadOnlyListProperty<FxNameSpace> namespaces()
    {
        return NAMESPACES;
    }
    
    public ReadOnlyListProperty<FxМусор> мусор()
    {
        return МУСОР;
    }
    
    public FxАтрибутный создатьНовыйЭлемент( String название, String uri )
    {
        return FxФабрика.getInstance().создать( getSource().создатьНовыйЭлемент( название, uri ) );
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxПакет )
            результат = оператор.выполнить( (FxПакет)узел, пакеты() );
        else if( узел instanceof FxМусор )
            результат = оператор.выполнить( (FxМусор)узел, мусор() );
        else if( узел != null )
            throw new ClassCastException( узел.getClass().getName() );
        else 
            throw new NullPointerException();
        return результат;
    }
    
}
