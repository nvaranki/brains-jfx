package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static com.varankin.brains.io.xml.XmlBrains.*;
import static com.varankin.brains.io.xml.XmlSvg.*;

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
        double w, h;
        Double атрибутWidth  = DbАтрибутный.toDoubleValue( ЭЛЕМЕНТ.getSource().атрибут( SVG_ATTR_WIDTH,  XMLNS_SVG, 200d ) );
        Double атрибутHeight = DbАтрибутный.toDoubleValue( ЭЛЕМЕНТ.getSource().атрибут( SVG_ATTR_HEIGHT, XMLNS_SVG, 100d ) );
        w = атрибутWidth  != null ? атрибутWidth  : 200d; 
        h = атрибутHeight != null ? атрибутHeight : 100d;
        Polygon countor = new Polygon( 0d, 0d, w, 0d, w, h, 0d, h );
        countor.setFill( null );
        countor.setStroke( Color.GRAY );
        countor.setStrokeType( StrokeType.CENTERED );
        countor.setStyle( "-fx-stroke-dash-array: 1px 1px" );
        return countor;
    }
}
