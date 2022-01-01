package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbСигнал;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.scene.*;

import static com.varankin.brains.db.xml.XmlBrains.*;
import static com.varankin.io.xml.svg.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtСигнал extends EdtЭлемент<DbСигнал,FxСигнал>
{
    EdtСигнал( FxСигнал элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.size() < 3 ) return false;
        
        FxГрафика графика;
        
        // изображение
        графика = графика( SVG_ELEMENT_POLYLINE );
        графика.атрибут( SVG_ATTR_POINTS ).setValue( points( path, path.size()-1 ) );
        графика.атрибут( SVG_ATTR_STROKE ).setValue( "teal" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // название  
        графика = название( "Новый сигнал", "../@" + XML_NAME, path.poll() );
        графика.атрибут( SVG_ATTR_FILL ).setValue( "teal" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
    private static int[] points( Queue<int[]> path, int cnt ) 
    {
        int[] points = new int[ cnt*2 ];
        for( int i = 0; i < cnt; i++)
        {
            int[] xy = path.poll();
            points[i*2+0] = xy[0];
            points[i*2+1] = xy[1];
        }
        return points;
    }
    
}
