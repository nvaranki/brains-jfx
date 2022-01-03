package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.type.DbПроект;
import com.varankin.brains.jfx.db.*;

import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static com.varankin.brains.db.DbПреобразователь.*;
import static com.varankin.brains.db.xml.XmlBrains.*;
import static com.varankin.io.xml.svg.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtПроект extends EdtЭлемент<DbПроект,FxПроект>
{
    EdtПроект( FxПроект элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = super.загрузить( основной );
        ObservableList<Node> children = group.getChildren();
        children.addAll( загрузить( ЭЛЕМЕНТ.фрагменты(), 0, XML_FRAGMENT ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.сигналы(), 1, XML_SIGNAL ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.процессоры(), 2, XML_PROCESSOR ) );
        children.addAll( загрузить( ЭЛЕМЕНТ.библиотеки(), 3, XML_LIBRARY ) );

        if( основной )
            children.add( createMarker( 3d ) );
        children.add( createBounds() );

        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        позиция( path.poll() );
        ЭЛЕМЕНТ.графики().add( название( "Новый проект", "../@" + XML_NAME ) );
        return path.isEmpty();
    }

    private Node createBounds()
    {
        FxProperty атрибутWidth = ЭЛЕМЕНТ.атрибут( SVG_ATTR_WIDTH,  XMLNS_SVG, FxProperty.class );
        FxProperty атрибутHeight = ЭЛЕМЕНТ.атрибут( SVG_ATTR_HEIGHT, XMLNS_SVG, FxProperty.class );
        Double svgWidth  = toDoubleValue( атрибутWidth.getValue() );
        Double svgHeight = toDoubleValue( атрибутHeight.getValue() );
        double w = svgWidth  != null ? svgWidth  : 200d; 
        double h = svgHeight != null ? svgHeight : 100d;
        Polygon countor = new Polygon( 0d, 0d, w, 0d, w, h, 0d, h );
        countor.setFill( null );
        countor.setStroke( Color.GRAY );
        countor.setStrokeType( StrokeType.CENTERED );
        countor.setStyle( "-fx-stroke-dash-array: 1px 1px" );
        return countor;
    }
}
