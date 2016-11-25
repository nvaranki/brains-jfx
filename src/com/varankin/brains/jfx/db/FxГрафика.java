package com.varankin.brains.jfx.db;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbГрафика;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.scene.paint.Color;

import static com.varankin.brains.io.xml.XmlSvg.XMLNS_SVG;

/**
 *
 * @author Varankine
 */
public final class FxГрафика extends FxУзел<DbГрафика>
{
    private final ReadOnlyListProperty<FxГрафика> ГРАФИКИ;

    public FxГрафика( DbГрафика графика )
    {
        super( графика );
        ГРАФИКИ = buildReadOnlyListProperty( графика, "графики", 
            new FxList<>( графика.графики(), графика, e -> new FxГрафика( e ), e -> e.getSource() ) );
    }

    public ReadOnlyListProperty<FxГрафика> графики()
    {
        return ГРАФИКИ;
    }
    
    public final Property атрибут( String название )
    {
        return super.атрибут( название, XMLNS_SVG );
    }

    public double toSvgDouble( String атрибут, double нет )
    {
        Double v = DbАтрибутный.toDoubleValue( атрибут( атрибут ).getValue() );
        return v != null ? v : нет;
    }

    public String toSvgString( String атрибут, String нет )
    {
        String v = DbАтрибутный.toStringValue( атрибут( атрибут ).getValue() );
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
            for( String p : DbАтрибутный.toStringValue( a ).split( "[\\s,]+" ) )
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
