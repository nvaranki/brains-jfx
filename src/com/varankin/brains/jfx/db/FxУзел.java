package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbУзел;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 * @param <T>
 */
public class FxУзел<T extends DbУзел> extends FxАтрибутный<T>
{
    private final ReadOnlyListProperty<FxИнструкция> ИНСТРУКЦИИ;
    private final ReadOnlyListProperty<FxТекстовыйБлок> ТЕКСТЫ;
    private final ReadOnlyListProperty<FxАтрибутный> ПРОЧЕЕ;

    public FxУзел( T элемент )
    {
        super( элемент );
        ИНСТРУКЦИИ = buildReadOnlyListProperty( элемент, "инструкции", 
            new FxList<>( элемент.инструкции(), элемент, FxИнструкция::new, FxАтрибутный::getSource ) );
        ТЕКСТЫ = buildReadOnlyListProperty( элемент, "тексты", 
            new FxList<>( элемент.тексты(), элемент, FxТекстовыйБлок::new, FxАтрибутный::getSource ) );
        ПРОЧЕЕ = buildReadOnlyListProperty( элемент, "прочее", 
            new FxList<>( элемент.прочее(), элемент, FxАтрибутный::new, FxАтрибутный::getSource ) );
    }
    
    public final ReadOnlyListProperty<FxИнструкция> инструкции()
    {
        return ИНСТРУКЦИИ;
    }
    
    public final ReadOnlyListProperty<FxТекстовыйБлок> тексты()
    {
        return ТЕКСТЫ;
    }
    
    public final ReadOnlyListProperty<FxАтрибутный> прочее()
    {
        return ПРОЧЕЕ;
    }

    /**
     * Выполняет заданный оператор над узлом и подходящей коллекцией.
     * 
     * @param <X>      класс результата, возвращаемого оператором.
     * @param оператор оператор над узлом и коллекцией.
     * @param узел     узел базы данных.
     * @return результат, возвращенный оператором.
     * @exception NullPointerException если узел - {@code null}. 
     */
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxИнструкция )
            результат = оператор.выполнить( (FxИнструкция)узел, инструкции() );
        else if( узел instanceof FxТекстовыйБлок )
            результат = оператор.выполнить( (FxТекстовыйБлок)узел, тексты() );
        else if( узел != null )
            результат = оператор.выполнить( узел, прочее() );
        else 
            throw new NullPointerException();
        return результат;
    }
    
}
