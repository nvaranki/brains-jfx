package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbФрагмент;
import com.varankin.brains.db.type.DbЭлемент;
import com.varankin.brains.db.xml.XLinkShow;
import com.varankin.brains.db.xml.XLinkActuate;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.xml.type.XmlФрагмент.*;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxФрагмент 
        extends FxЭлемент<DbФрагмент> 
        implements FxКоммутируемый, FxТиповой<FxКоммутируемый>
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final FxPropertyImpl<String> ПРОЦЕССОР;
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<XLinkShow> ВИД;
    private final FxPropertyImpl<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxКоммутируемый> ЭКЗЕМПЛЯР;

    public FxФрагмент( DbФрагмент элемент ) 
    {
        super( элемент );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( элемент, "соединения", 
            new FxList<>( элемент.соединения(), элемент, e -> new FxСоединение( e ), FxАтрибутный::getSource ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), FxАтрибутный::getSource ) );
        ПРОЦЕССОР  = new FxPropertyImpl<>( элемент, "процессор",  КЛЮЧ_А_ПРОЦЕССОР,  элемент::процессор,  элемент::процессор  );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     элемент::ссылка,     элемент::ссылка     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        элемент::вид,        элемент::вид        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, элемент::реализация, элемент::реализация );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", FxТиповой.КЛЮЧ_А_ЭКЗЕМПЛЯР, this::коммутируемый );
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
    
    public FxProperty<String> процессор()
    {
        return ПРОЦЕССОР;
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
    public FxReadOnlyProperty<FxКоммутируемый> экземпляр()
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
