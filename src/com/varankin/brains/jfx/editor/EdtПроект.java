package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbПроект;
import com.varankin.brains.db.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import static com.varankin.brains.io.xml.XmlSvg.*;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtПроект extends EdtЭлемент<DbПроект>
{
    EdtПроект( DbПроект элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );
        group.setUserData( ЭЛЕМЕНТ );
        
        String ts = DbАтрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        if( изменяемый )
            group.getChildren().add( createMarker( 3d ) );
        group.getChildren().add( createBounds() );
        
        for( DbФрагмент фрагмент : ЭЛЕМЕНТ.фрагменты() )
            group.getChildren().add( new EdtФрагмент( фрагмент ).загрузить( изменяемый ) );
        for( DbПроцессор процессор : ЭЛЕМЕНТ.процессоры() )
            group.getChildren().add( new EdtПроцессор( процессор ).загрузить( изменяемый ) );
        for( DbСигнал сигнал : ЭЛЕМЕНТ.сигналы() )
            group.getChildren().add( new EdtСигнал( сигнал ).загрузить( изменяемый ) );
        for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
            group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
        for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
            group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
        for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
            group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
        for( DbБиблиотека библиотека : ЭЛЕМЕНТ.библиотеки() )
            group.getChildren().add( new EdtБиблиотека( библиотека ).загрузить( изменяемый ) );
        
        return group;
    }
    
    private Node createBounds()
    {
        double w, h;
        Double атрибутWidth  = DbАтрибутный.toDoubleValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_WIDTH,  XMLNS_SVG, 200d ) );
        Double атрибутHeight = DbАтрибутный.toDoubleValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_HEIGHT, XMLNS_SVG, 100d ) );
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
