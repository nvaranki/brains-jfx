package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbСенсор;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.xml.XLinkActuate;
import com.varankin.brains.db.xml.XLinkShow;

import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlСенсор.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxСенсор 
        extends FxЭлемент<DbСенсор> 
        implements FxТиповой<FxСенсор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<XLinkShow> ВИД;
    private final FxPropertyImpl<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxСенсор> ЭКЗЕМПЛЯР;
    //private final FxPropertyImpl<DbСенсор.Приоритет> ПРИОРИТЕТ;

    public FxСенсор( DbСенсор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, FxПараметр::new, FxАтрибутный::getSource ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, FxКлассJava::new, FxАтрибутный::getSource ) );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     элемент::ссылка,     элемент::ссылка     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        элемент::вид,        элемент::вид        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, элемент::реализация, элемент::реализация );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::типовой );
        //ПРИОРИТЕТ = new FxPropertyImpl<>( элемент, "приоритет", КЛЮЧ_А_ПРИОРИТЕТ, элемент::приоритет, элемент::приоритет );
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
    public FxProperty<XLinkShow> вид()
    {
        return ВИД;
    }

    @Override
    public FxProperty<XLinkActuate> реализация()
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
        return экземпляр != null ? (FxСенсор)FxФабрика.getInstance().apply( экземпляр ) : null;
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
