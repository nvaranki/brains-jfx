package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbБиблиотека;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Varankine
 */
public final class FxБиблиотека extends FxЭлемент<DbБиблиотека>
{
    private final ReadOnlyListProperty<FxПоле> ПОЛЯ;
    private final ReadOnlyListProperty<FxМодуль> МОДУЛИ;
    private final ReadOnlyListProperty<FxРасчет> РАСЧЕТЫ;
    private final ReadOnlyListProperty<FxЛента> ЛЕНТЫ;
    private final ReadOnlyListProperty<FxПроцессор> ПРОЦЕССОРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;

    public FxБиблиотека( DbБиблиотека библиотека ) 
    {
        super( библиотека );
        ПОЛЯ = buildReadOnlyListProperty( библиотека, "поля", 
            new FxList<>( библиотека.поля(), e -> new FxПоле( e ), e -> e.getSource() ) );
        МОДУЛИ = buildReadOnlyListProperty( библиотека, "модули", 
            new FxList<>( библиотека.модули(), e -> new FxМодуль( e ), e -> e.getSource() ) );
        РАСЧЕТЫ = buildReadOnlyListProperty( библиотека, "расчеты", 
            new FxList<>( библиотека.расчеты(), e -> new FxРасчет( e ), e -> e.getSource() ) );
        ЛЕНТЫ = buildReadOnlyListProperty( библиотека, "ленты", 
            new FxList<>( библиотека.ленты(), e -> new FxЛента( e ), e -> e.getSource() ) );
        ПРОЦЕССОРЫ = buildReadOnlyListProperty( библиотека, "процессоры", 
            new FxList<>( библиотека.процессоры(), e -> new FxПроцессор( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( библиотека, "классы", 
            new FxList<>( библиотека.классы(), e -> new FxКлассJava( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxПоле> поля()
    {
        return ПОЛЯ;
    }
    
    public ReadOnlyListProperty<FxМодуль> модули()
    {
        return МОДУЛИ;
    }
    
    public ReadOnlyListProperty<FxРасчет> расчеты()
    {
        return РАСЧЕТЫ;
    }
    
    public ReadOnlyListProperty<FxЛента> ленты()
    {
        return ЛЕНТЫ;
    }
    
    public ReadOnlyListProperty<FxПроцессор> процессоры()
    {
        return ПРОЦЕССОРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxПоле )
            результат = оператор.выполнить( (FxПоле)узел, поля() );
        else if( узел instanceof FxМодуль )
            результат = оператор.выполнить( (FxМодуль)узел, модули() );
        else if( узел instanceof FxРасчет )
            результат = оператор.выполнить( (FxРасчет)узел, расчеты() );
        else if( узел instanceof FxЛента )
            результат = оператор.выполнить( (FxЛента)узел, ленты() );
        else if( узел instanceof FxПроцессор )
            результат = оператор.выполнить( (FxПроцессор)узел, процессоры() );
        else if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
