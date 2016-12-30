package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАрхив;
import com.varankin.brains.db.Транзакция;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Varankine
 */
public final class FxАрхив extends FxАтрибутный<DbАрхив>
{
    private final ReadOnlyListProperty<FxПакет> ПАКЕТЫ;
    private final ReadOnlyListProperty<FxNameSpace> NAMESPACES;
    private final ReadOnlyListProperty<FxМусор> МУСОР;
    private final ReadOnlyProperty<String> РАСПОЛОЖЕНИЕ; // скрыть изменяемость

    FxАрхив( DbАрхив элемент ) 
    {
        super( элемент );
        ПАКЕТЫ = buildReadOnlyListProperty( элемент, "пакеты", 
            new FxList<>( элемент.пакеты(), элемент, e -> new FxПакет( e ), e -> e.getSource() ) );
        NAMESPACES = buildReadOnlyListProperty( элемент, "namespaces", 
            new FxList<>( элемент.namespaces(), элемент, e -> new FxNameSpace( e ), e -> e.getSource() ) );
        МУСОР = buildReadOnlyListProperty( элемент, "мусор", 
            new FxList<>( элемент.мусор(), элемент, e -> new FxМусор( e ), e -> e.getSource() ) );
        РАСПОЛОЖЕНИЕ = new FxReadOnlyProperty<>( элемент, "расположение", () -> элемент.расположение() );
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
    
    public ReadOnlyProperty<String> расположение()
    {
        return РАСПОЛОЖЕНИЕ;
    }
    
    public FxАтрибутный создатьНовыйЭлемент( String название, String uri )
    {
        return FxФабрика.getInstance().создать( getSource().создатьНовыйЭлемент( название, uri ) );
    }
    
    public FxNameSpace определитьПространствоИмен( String uri, String префикс )
    {
        return NAMESPACES.stream()
            .filter( ns -> ns.uri().getValue().equalsIgnoreCase( uri ) )
            .findAny().orElseGet( () -> 
            {
                try( final Транзакция транзакция = getSource().транзакция() )
                {
                    FxNameSpace ns = FxФабрика.getInstance().создать( 
                        getSource().определитьПространствоИмен( uri, префикс ) );
                    транзакция.завершить( NAMESPACES.add( ns ) );
                    return ns;
                }
                catch( Exception e )
                {
                    throw new RuntimeException( "Failed to create namespace", e );
                }
            } );
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxПакет )
            результат = оператор.выполнить( (FxПакет)узел, пакеты() );
        else if( узел instanceof FxМусор )
            результат = оператор.выполнить( (FxМусор)узел, мусор() );
        else if( узел instanceof FxNameSpace )
            //TODO TransactionFailureException: Transaction rolled back even if marked as successful
            результат = оператор.выполнить( (FxNameSpace)узел, namespaces() );
        else if( узел != null )
            throw new ClassCastException( узел.getClass().getName() );
        else 
            throw new NullPointerException();
        return результат;
    }
    
}
