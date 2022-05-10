package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbТочка;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.xml.XLinkActuate;
import com.varankin.brains.db.xml.XLinkShow;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlТочка.*;

/**
 *
 *  
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxТочка 
        extends FxЭлемент<DbТочка> 
        implements FxТиповой<FxТочка>
{
    private final ReadOnlyListProperty<FxТочка> ТОЧКИ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<XLinkShow> ВИД;
    private final FxPropertyImpl<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxТочка> ЭКЗЕМПЛЯР;
    private final FxPropertyImpl<Integer> ИНДЕКС;
    private final FxPropertyImpl<Boolean> ДАТЧИК;
    @Deprecated private final FxPropertyImpl<Float> ПОРОГ;
    private final FxPropertyImpl<String> КОНТАКТ;

    public FxТочка( DbТочка элемент ) 
    {
        super( элемент );
        ТОЧКИ = buildReadOnlyListProperty( элемент, "точки", 
            new FxList<>( элемент.точки(), элемент, FxТочка::new, FxАтрибутный::getSource ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, FxПараметр::new, FxАтрибутный::getSource ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, FxКлассJava::new, FxАтрибутный::getSource ) );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     элемент::ссылка,     элемент::ссылка     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        элемент::вид,        элемент::вид        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, элемент::реализация, элемент::реализация );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::типовой );
        ИНДЕКС  = new FxPropertyImpl<>( элемент, "индекс",  КЛЮЧ_А_ИНДЕКС,  элемент::индекс,  элемент::индекс  );
        ДАТЧИК  = new FxPropertyImpl<>( элемент, "датчик",  КЛЮЧ_А_ДАТЧИК,  элемент::датчик,  элемент::датчик  );
        ПОРОГ   = new FxPropertyImpl<>( элемент, "порог",   КЛЮЧ_А_ПОРОГ,   элемент::порог,   элемент::порог   );
        КОНТАКТ = new FxPropertyImpl<>( элемент, "контакт", КЛЮЧ_А_КОНТАКТ, элемент::контакт, элемент::контакт );
    }

    public ReadOnlyListProperty<FxТочка> точки()
    {
        return ТОЧКИ;
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
    public FxReadOnlyProperty<FxТочка> экземпляр()
    {
        return ЭКЗЕМПЛЯР;
    }

    public FxProperty<Integer> индекс()
    {
        return ИНДЕКС;
    }
    
    public FxProperty<Boolean> датчик()
    {
        return ДАТЧИК;
    }
    
    @Deprecated 
    public FxProperty<Float> порог()
    {
        return ПОРОГ;
    }
    
    public FxProperty<String> контакт()
    {
        return КОНТАКТ;
    }

    private FxТочка типовой()
    {
        DbЭлемент экземпляр = getSource().экземпляр();
        return экземпляр != null ? (FxТочка)FxФабрика.getInstance().создать( экземпляр ) : null;
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxКлассJava )
            результат = оператор.выполнить( (FxКлассJava)узел, классы() );
        else if( узел instanceof FxПараметр )
            результат = оператор.выполнить( (FxПараметр)узел, параметры() );
        else if( узел instanceof FxТочка )
            результат = оператор.выполнить( (FxТочка)узел, точки() );
        else 
            результат = /*FxЭлемент.*/super.выполнить( оператор, узел );
        return результат;
    }
    
}
