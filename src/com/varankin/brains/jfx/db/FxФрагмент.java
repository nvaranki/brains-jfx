package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbФрагмент;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.io.xml.Xml.XLinkShow;
import com.varankin.brains.io.xml.Xml.XLinkActuate;
import javafx.beans.property.ReadOnlyListProperty;

import static com.varankin.brains.db.DbФрагмент.*;

/**
 *
 * @author Varankine
 */
public final class FxФрагмент extends FxЭлемент<DbФрагмент> implements FxКоммутируемый
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final FxProperty<String> ПРОЦЕССОР;
    private final FxProperty<String> НАЗВАНИЕ, ССЫЛКА;
    private final FxProperty<XLinkShow> ВИД;
    private final FxProperty<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyProperty<FxКоммутируемый> ЭКЗЕМПЛЯР;

    public FxФрагмент( DbФрагмент элемент ) 
    {
        super( элемент );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( элемент, "соединения", 
            new FxList<>( элемент.соединения(), элемент, e -> new FxСоединение( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        ПРОЦЕССОР  = new FxProperty<>( элемент, "процессор",  КЛЮЧ_А_ПРОЦЕССОР,  () -> элемент.процессор(),  t -> элемент.процессор( t )  );
        НАЗВАНИЕ   = new FxProperty<>( элемент, "название",   КЛЮЧ_А_НАЗВАНИЕ,   () -> элемент.название(),   t -> элемент.название( t )   );
        ССЫЛКА     = new FxProperty<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     () -> элемент.ссылка(),     t -> элемент.ссылка( t )     );
        ВИД        = new FxProperty<>( элемент, "вид",        КЛЮЧ_А_ВИД,        () -> элемент.вид(),        t -> элемент.вид( t )        );
        РЕАЛИЗАЦИЯ = new FxProperty<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, () -> элемент.реализация(), t -> элемент.реализация( t ) );
        ЭКЗЕМПЛЯР  = new FxReadOnlyProperty<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::коммутируемый );
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
    
    @Override
    public FxProperty<String> название()
    {
        return НАЗВАНИЕ;
    }

    public FxProperty<String> процессор()
    {
        return ПРОЦЕССОР;
    }

    public FxProperty<String> ссылка()
    {
        return ССЫЛКА;
    }

    public FxProperty<XLinkShow> вид()
    {
        return ВИД;
    }

    public FxProperty<XLinkActuate> реализация()
    {
        return РЕАЛИЗАЦИЯ;
    }

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
