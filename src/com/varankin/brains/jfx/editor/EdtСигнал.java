package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;

import static com.varankin.brains.io.xml.XmlBrains.*;
import static com.varankin.brains.io.xml.XmlSvg.*;

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
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.параметры(), 0, XML_PARAMETER ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.классы(), 1, XML_JAVA ) );
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.size() < 3 ) return false;
        
        DbАрхив архив = ЭЛЕМЕНТ.getSource().архив();
        FxГрафика графика;
        
        // изображение
        графика = FxФабрика.getInstance().создать( 
            (DbГрафика)архив.создатьНовыйЭлемент( SVG_ELEMENT_POLYLINE, XMLNS_SVG ) );
        графика.определить( SVG_ATTR_POINTS, points( path, path.size()-1 ) );
        графика.определить( SVG_ATTR_STROKE, "teal" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        // название  
        графика = название( "Новый сигнал", "../@" + XML_NAME, path.poll() );
        графика.определить( SVG_ATTR_FILL, "teal" );
        ЭЛЕМЕНТ.графики().add( графика );
        
        return path.isEmpty();
    }
    
    int[] points( Queue<int[]> path, int cnt ) 
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
