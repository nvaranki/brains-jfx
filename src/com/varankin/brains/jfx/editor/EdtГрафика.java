package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.io.xml.XmlSvg;
import javafx.scene.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

import static com.varankin.brains.io.xml.XmlSvg.*;
import static com.varankin.brains.jfx.editor.EdtНеизвестный.toSvgColor;
import static com.varankin.brains.jfx.editor.EdtФрагмент.toTransforms;

/**
 *
 * @author Николай
 */
class EdtГрафика extends EdtУзел<DbГрафика>
{
    EdtГрафика( DbГрафика элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean изменяемый )
    {
        Group group = super.загрузить( изменяемый );
        if( изменяемый ) group.setUserData( ЭЛЕМЕНТ );
        
        String ts = DbАтрибутный.toStringValue( ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM, XMLNS_SVG, "" ) );
        group.getTransforms().addAll( toTransforms( ts ) );

        for( DbГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( изменяемый ) );
        
        String s;
        if( XmlSvg.XMLNS_SVG.equals( ЭЛЕМЕНТ.тип().uri() ) )
            switch( ЭЛЕМЕНТ.тип().название() )
            {
                case XmlSvg.SVG_ELEMENT_TEXT:
                    group.getChildren().add( new SvgTextController( ЭЛЕМЕНТ, изменяемый ).build() );
                    break;
                    
                case XmlSvg.SVG_ELEMENT_RECT:
                    Rectangle rect = new Rectangle();
                    rect.setX( toSvgDouble( SVG_ATTR_X, 0d ) );
                    rect.setY( toSvgDouble( SVG_ATTR_Y, 0d ) );
                    rect.setWidth( toSvgDouble( SVG_ATTR_WIDTH, 0d ) );
                    rect.setHeight(toSvgDouble( SVG_ATTR_HEIGHT, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) rect.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) rect.setFill( toSvgColor( s ) );
                    group.getChildren().add( rect );
                    break;
                    
                case XmlSvg.SVG_ELEMENT_LINE:
                    Line line = new Line();
                    line.setStartX( toSvgDouble( SVG_ATTR_X1, 0d ) );
                    line.setStartY( toSvgDouble( SVG_ATTR_Y1, 0d ) );
                    line.setEndX( toSvgDouble( SVG_ATTR_X2, 0d ) );
                    line.setEndY( toSvgDouble( SVG_ATTR_Y2, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) line.setStroke( toSvgColor( s ) );
                    group.getChildren().add( line );
                    break;
                    
                case XmlSvg.SVG_ELEMENT_POLYLINE:
                    Polyline path = new Polyline();
                    path.getPoints().setAll( toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) path.setStroke( toSvgColor( s ) );
                    group.getChildren().add( path );
                    break;
                    
                case XmlSvg.SVG_ELEMENT_CIRCLE:
                    Circle circle = new Circle();
                    circle.setCenterX( toSvgDouble( SVG_ATTR_CX, 0d ) );
                    circle.setCenterY( toSvgDouble( SVG_ATTR_CY, 0d ) );
                    circle.setRadius( toSvgDouble( SVG_ATTR_R, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) circle.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) circle.setFill( toSvgColor( s ) );
                    group.getChildren().add( circle );
                    break;
                    
                default:
//                    Group group = new Group();
//                    for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
//                        group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//                    for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//                        group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//                    for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
//                        group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
//                    node = group;
            }
//        else
//        {
//            Group group = new Group();
//            for( DbИнструкция н : ЭЛЕМЕНТ.инструкции() )
//                group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//            for( DbТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//                group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//            for( DbАтрибутный н : ЭЛЕМЕНТ.прочее() )
//                group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
//            node = group;
//        }
        return group;
    }
    
}
