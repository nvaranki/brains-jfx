package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbПроцессор;
import com.varankin.brains.db.type.DbПроцессор.Стратегия;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.xml.XLinkActuate;
import com.varankin.brains.db.xml.XLinkShow;

import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlПроцессор.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxПроцессор 
        extends FxЭлемент<DbПроцессор> 
        implements FxТиповой<FxПроцессор>
{
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final ReadOnlyListProperty<FxКлассJava> КЛАССЫ;
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<XLinkShow> ВИД;
    private final FxPropertyImpl<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxПроцессор> ЭКЗЕМПЛЯР;
    private final FxPropertyImpl<Long> ЗАДЕРЖКА;
    private final FxPropertyImpl<Integer> НАКОПЛЕНИЕ;
    private final FxPropertyImpl<Long> ПАУЗА;
    private final FxPropertyImpl<Boolean> РЕСТАРТ;
    private final FxPropertyImpl<Boolean> СЖАТИЕ;
    private final FxPropertyImpl<Boolean> ОЧИСТКА;
    private final FxPropertyImpl<Стратегия> СТРАТЕГИЯ;

    public FxПроцессор( DbПроцессор элемент ) 
    {
        super( элемент );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, FxПараметр::new, FxАтрибутный::getSource ) );
        КЛАССЫ = buildReadOnlyListProperty( элемент, "классы", 
            new FxList<>( элемент.классы(), элемент, FxКлассJava::new, FxАтрибутный::getSource ) );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     элемент::ссылка,     элемент::ссылка     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        элемент::вид,        элемент::вид        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, элемент::реализация, элемент::реализация );
        ЗАДЕРЖКА   = new FxPropertyImpl<>( элемент, "задержка",   КЛЮЧ_А_ЗАДЕРЖКА,   элемент::задержка,   элемент::задержка   );
        НАКОПЛЕНИЕ = new FxPropertyImpl<>( элемент, "накопление", КЛЮЧ_А_НАКОПЛЕНИЕ, элемент::накопление, элемент::накопление );
        ПАУЗА      = new FxPropertyImpl<>( элемент, "пауза",      КЛЮЧ_А_ПАУЗА,      элемент::пауза,      элемент::пауза      );
        РЕСТАРТ    = new FxPropertyImpl<>( элемент, "рестарт",    КЛЮЧ_А_РЕСТАРТ,    элемент::рестарт,    элемент::рестарт    );
        СЖАТИЕ     = new FxPropertyImpl<>( элемент, "сжатие",     КЛЮЧ_А_СЖАТИЕ,     элемент::сжатие,     элемент::сжатие     );
        ОЧИСТКА    = new FxPropertyImpl<>( элемент, "очистка",    КЛЮЧ_А_ОЧИСТКА,    элемент::очистка,    элемент::очистка    );
        СТРАТЕГИЯ  = new FxPropertyImpl<>( элемент, "стратегия",  КЛЮЧ_А_СТРАТЕГИЯ,  элемент::стратегия,  элемент::стратегия  );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::типовой );
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
    public FxReadOnlyProperty<FxПроцессор> экземпляр()
    {
        return ЭКЗЕМПЛЯР;
    }

    public FxProperty<Long> задержка()
    {
        return ЗАДЕРЖКА;
    }
    
    public FxProperty<Integer> накопление()
    {
        return НАКОПЛЕНИЕ;
    }
    
    public FxProperty<Long> пауза()
    {
        return ПАУЗА;
    }
    
    public FxProperty<Boolean> рестарт()
    {
        return РЕСТАРТ;
    }
    
    public FxProperty<Boolean> сжатие()
    {
        return СЖАТИЕ;
    }
    
    public FxProperty<Boolean> очистка()
    {
        return ОЧИСТКА;
    }
    
    public FxProperty<Стратегия> стратегия()
    {
        return СТРАТЕГИЯ;
    }

    private FxПроцессор типовой()
    {
        DbЭлемент экземпляр = getSource().экземпляр();
        return экземпляр != null ? (FxПроцессор)FxФабрика.getInstance().создать( экземпляр ) : null;
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
