package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.*;
import com.varankin.brains.jfx.db.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

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

        if( основной )
            group.getChildren().add( createMarker( 3d ) );
        group.getChildren().add( createBounds() );
        
        for( FxФрагмент фрагмент : ЭЛЕМЕНТ.фрагменты() )
            group.getChildren().add( new EdtФрагмент( фрагмент ).загрузить( false ) );
        for( FxПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( false ) );
        for( FxСигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( false ) );
        for( FxБиблиотека библиотека : ЭЛЕМЕНТ.библиотеки() )
            group.getChildren().add( new EdtБиблиотека( библиотека ).загрузить( false ) );
        
        return group;
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
