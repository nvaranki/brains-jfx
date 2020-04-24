package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.db.КороткийКлюч;
import java.util.Collection;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbЭлемент.*;

/**
 *
 * @author &copy; 2019 Николай Варанкин
 * @param <T>
 */
public class FxЭлемент<T extends DbЭлемент> extends FxУзел<T>
{
    static final КороткийКлюч КЛЮЧ_А_ПОЛОЖЕНИЕ = new КороткийКлюч( "положение", null );

    private final ReadOnlyListProperty<FxЗаметка> ЗАМЕТКИ;
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;
    private final FxPropertyImpl<String> НАЗВАНИЕ;
    private final FxReadOnlyPropertyImpl<String> ПОЛОЖЕНИЕ;
    private final FxPropertyImpl<Collection<String>> СБОРКИ;

    public FxЭлемент( T элемент ) 
    {
        super( элемент );
        ЗАМЕТКИ = buildReadOnlyListProperty( элемент, "заметки", 
            new FxList<>( элемент.заметки(), элемент, e -> new FxЗаметка( e ), e -> e.getSource() ) );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, e -> new FxГрафика( e ), e -> e.getSource() ) );
        НАЗВАНИЕ  = new FxPropertyImpl<>( элемент, "название", КЛЮЧ_А_НАЗВАНИЕ, () -> элемент.название(), t -> элемент.название( t ) );
        ПОЛОЖЕНИЕ = new FxReadOnlyPropertyImpl<>( элемент, "положение", КЛЮЧ_А_ПОЛОЖЕНИЕ, () -> 
        {
            String разделитель = "/";
            String значение = элемент.положение( разделитель ); // "/у0/у1/.../уN/название"
            return значение.isEmpty() ? разделитель : значение;
        } );
        СБОРКИ  = new FxPropertyImpl<>( элемент, "сборки", КЛЮЧ_А_СБОРКИ, () -> элемент.сборки(), t -> элемент.сборки( t ) );
    }

    public final ReadOnlyListProperty<FxЗаметка> заметки()
    {
        return ЗАМЕТКИ;
    }
    
    public final ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public FxProperty<String> название()
    {
        return НАЗВАНИЕ;
    }
    
    public FxProperty<Collection<String>> сборки()
    {
        return СБОРКИ;
    }
    
    public final FxReadOnlyProperty<String> положение()
    {
        return ПОЛОЖЕНИЕ;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxЗаметка )
            результат = оператор.выполнить( (FxЗаметка)узел, заметки() );
        else if( узел instanceof FxГрафика )
            результат = оператор.выполнить( (FxГрафика)узел, графики() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
