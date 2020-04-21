package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbСенсор;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.io.xml.Xml;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.Типовой.*;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
public final class FxСенсор 
        extends FxЭлемент<DbСенсор> 
        implements FxТиповой<FxСенсор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<Xml.XLinkShow> ВИД;
    private final FxPropertyImpl<Xml.XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxСенсор> ЭКЗЕМПЛЯР;
    //private final FxPropertyImpl<DbСенсор.Приоритет> ПРИОРИТЕТ;

    public FxСенсор( DbСенсор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, e -> new FxКлассJava( e ), e -> e.getSource() ) );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     () -> элемент.ссылка(),     t -> элемент.ссылка( t )     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        () -> элемент.вид(),        t -> элемент.вид( t )        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, () -> элемент.реализация(), t -> элемент.реализация( t ) );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::типовой );
        //ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, () -> элемент.приоритет(), (t) -> элемент.приоритет( t ) );
    }

    public ReadOnlyListProperty<FxПараметр> параметры()
    {
        return ПАРАМЕТРЫ;
    }
    
    public ReadOnlyListProperty<FxКлассJava> классы()
    {
        return КЛАССЫ;
    }
    
    @Override
    public FxProperty<String> ссылка()
    {
        return ССЫЛКА;
    }

    @Override
    public FxProperty<Xml.XLinkShow> вид()
    {
        return ВИД;
    }

    @Override
    public FxProperty<Xml.XLinkActuate> реализация()
    {
        return РЕАЛИЗАЦИЯ;
    }

    @Override
    public FxReadOnlyProperty<FxСенсор> экземпляр()
    {
        return ЭКЗЕМПЛЯР;
    }

//    public FxProperty<DbСенсор.Приоритет> приоритет()
//    {
//        return ПРИОРИТЕТ;
//    }

    private FxСенсор типовой()
    {
        DbЭлемент экземпляр = getSource().экземпляр();
        return экземпляр != null ? (FxСенсор)FxФабрика.getInstance().создать( экземпляр ) : null;
    }

    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
