package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbФрагмент;
import com.varankin.brains.db.DbЭлемент;
import com.varankin.brains.io.xml.Xml.XLinkShow;
import com.varankin.brains.io.xml.Xml.XLinkActuate;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyProperty;

/**
 *
 * @author Varankine
 */
public final class FxФрагмент extends FxЭлемент<DbФрагмент> implements FxКоммутируемый
{
    private final ReadOnlyListProperty<FxСоединение> СОЕДИНЕНИЯ;
    private final ReadOnlyListProperty<FxПараметр> ПАРАМЕТРЫ;
    private final Property<String> ПРОЦЕССОР;
    private final Property<String> ССЫЛКА;
    private final Property<XLinkShow> ВИД;
    private final Property<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final ReadOnlyProperty<FxКоммутируемый> ЭКЗЕМПЛЯР;

    public FxФрагмент( DbФрагмент элемент ) 
    {
        super( элемент );
        СОЕДИНЕНИЯ = buildReadOnlyListProperty( элемент, "соединения", 
            new FxList<>( элемент.соединения(), элемент, e -> new FxСоединение( e ), e -> e.getSource() ) );
        ПАРАМЕТРЫ = buildReadOnlyListProperty( элемент, "параметры", 
            new FxList<>( элемент.параметры(), элемент, e -> new FxПараметр( e ), e -> e.getSource() ) );
        ПРОЦЕССОР = new FxProperty<>( элемент, "процессор", () -> элемент.процессор(), (t) -> элемент.процессор( t ) );
        ССЫЛКА = new FxProperty<>( элемент, "ссылка", () -> элемент.ссылка(), (t) -> элемент.ссылка( t ) );
        ВИД = new FxProperty<>( элемент, "вид", () -> элемент.вид(), (t) -> элемент.вид( t ) );
        РЕАЛИЗАЦИЯ = new FxProperty<>( элемент, "реализация", () -> элемент.реализация(), (t) -> элемент.реализация( t ) );
        ЭКЗЕМПЛЯР = new FxReadOnlyProperty<>( элемент, "экземпляр", this::коммутируемый );
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
    
    public Property<String> процессор()
    {
        return ПРОЦЕССОР;
    }

    public Property<String> ссылка()
    {
        return ССЫЛКА;
    }

    public Property<XLinkShow> вид()
    {
        return ВИД;
    }

    public Property<XLinkActuate> реализация()
    {
        return РЕАЛИЗАЦИЯ;
    }

    public ReadOnlyProperty<FxКоммутируемый> экземпляр()
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
