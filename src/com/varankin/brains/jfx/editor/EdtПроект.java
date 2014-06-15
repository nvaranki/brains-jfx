package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.Проект;
import com.varankin.brains.db.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static com.varankin.brains.artificial.io.xml.XmlSvg.*;
import static com.varankin.brains.db.neo4j.Architect.*;

/**
 *
 * @author Николай
 */
class EdtПроект extends EdtАтрибутныйЭлемент<Проект>
{
    EdtПроект( Проект элемент )
    {
        super( элемент );
    }
    
    Node загрузить()
    {
        Group group = new Group();
        group.setUserData( ЭЛЕМЕНТ );
        
        group.getChildren().add( createMarker( 3d ) );
        group.getChildren().add( createBounds() );
        
        for( Фрагмент фрагмент : ЭЛЕМЕНТ.фрагменты() )
            group.getChildren().add( new EdtФрагмент( фрагмент ).загрузить() );
        for( Сигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить() );
        
        return group;
    }
    
    private Node createBounds()
    {
        double w, h;
        Double атрибутWidth  = toDoubleValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_WIDTH,  XMLNS_SVG, 200d ) );
        Double атрибутHeight = toDoubleValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_HEIGHT, XMLNS_SVG, 100d ) );
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
