package com.varankin.brains.jfx.db;

import com.varankin.brains.db.type.DbГрафика;
import com.varankin.brains.db.xml.XLinkActuate;
import com.varankin.brains.db.xml.XLinkShow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.scene.paint.Color;

import static com.varankin.brains.db.DbПреобразователь.*;
import static com.varankin.brains.db.xml.type.XmlГрафика.*;
import static com.varankin.io.xml.svg.XmlSvg.XMLNS_SVG;
import static com.varankin.brains.jfx.db.FxТиповой.КЛЮЧ_А_ЭКЗЕМПЛЯР;

/**
 *
 * @author &copy; 2021 Николай Варанкин
 */
public final class FxГрафика extends FxУзел<DbГрафика> implements FxТиповой<FxГрафика>
{
    private final FxPropertyImpl<String> ССЫЛКА;
    private final FxPropertyImpl<XLinkShow> ВИД;
    private final FxPropertyImpl<XLinkActuate> РЕАЛИЗАЦИЯ;
    private final FxReadOnlyPropertyImpl<FxГрафика> ЭКЗЕМПЛЯР;
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;

    public FxГрафика( DbГрафика элемент )
    {
        super( элемент );
        ССЫЛКА     = new FxPropertyImpl<>( элемент, "ссылка",     КЛЮЧ_А_ССЫЛКА,     элемент::ссылка,     элемент::ссылка     );
        ВИД        = new FxPropertyImpl<>( элемент, "вид",        КЛЮЧ_А_ВИД,        элемент::вид,        элемент::вид        );
        РЕАЛИЗАЦИЯ = new FxPropertyImpl<>( элемент, "реализация", КЛЮЧ_А_РЕАЛИЗАЦИЯ, элемент::реализация, элемент::реализация );
        ЭКЗЕМПЛЯР  = new FxReadOnlyPropertyImpl<>( элемент, "экземпляр", КЛЮЧ_А_ЭКЗЕМПЛЯР, this::типовой );
        ГРАФИКИ = buildReadOnlyListProperty( элемент, "графики", 
            new FxList<>( элемент.графики(), элемент, FxГрафика::new, FxАтрибутный::getSource ) );
    }

    private FxГрафика типовой()
    {
        DbГрафика экземпляр = getSource().экземпляр();
        return экземпляр != null ? FxФабрика.getInstance().создать( экземпляр ) : null;
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
    public FxReadOnlyProperty<FxГрафика> экземпляр()
    {
        return ЭКЗЕМПЛЯР;
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public final FxProperty атрибут( String название )
    {
        return super.атрибут( название, XMLNS_SVG, FxProperty.class );
    }

    public double toSvgDouble( String атрибут, double нет )
    {
        Double v = toDoubleValue( атрибут( атрибут ).getValue() );
        return v != null ? v : нет;
    }

    public String toSvgString( String атрибут, String нет )
    {
        String v = toStringValue( атрибут( атрибут ).getValue() );
        return v != null ? v : нет;
    }

    public Double[] toSvgPoints( String атрибут, Double[] нет )
    {
        Object a = атрибут( атрибут ).getValue();
        if( a == null ) return нет;
        List<Double> v = new ArrayList<>();
        if( a.getClass().isArray() && !( a instanceof char[] ) )
            for( int i = 0, max = Array.getLength( a ); i < max; i++ )
                v.add( Array.getDouble( a, i ) );
         else
            for( String p : toStringValue( a ).split( "[\\s,]+" ) )
                v.add( Double.valueOf( p.trim() ) );
        return v.isEmpty() ? нет : v.toArray( new Double[v.size()] );
    }
    
    public Color toSvgColor( String атрибут, String нет )
    {
        String s = toSvgString( атрибут, нет );
        return s == null || "none".equals( s ) ? null : Color.valueOf( s );
    }
    
    @Override
    /*default*/ public <X> X выполнить( FxОператор<X> оператор, FxАтрибутный узел )
    {
        X результат;
        if( узел instanceof FxГрафика )
            результат = оператор.выполнить( (FxГрафика)узел, графики() );
        else 
            результат = /*FxУзел.*/super.выполнить( оператор, узел );
        return результат;
    }

}
