package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.db.DbТекстовыйБлок;
import com.varankin.brains.io.xml.Xml;
import com.varankin.brains.jfx.db.*;
import java.util.Queue;
import javafx.scene.*;
import javafx.scene.shape.*;

import static com.varankin.brains.io.xml.XmlSvg.*;

/**
 *
 * @author Николай
 */
class EdtГрафика extends EdtУзел<DbГрафика,FxГрафика>
{
    EdtГрафика( FxГрафика элемент )
    {
        super( элемент );
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = true/*основной*/ ? загрузить( new Group(), true ) : super.загрузить( false ); // без текстов, инструкций и прочего

        for( FxГрафика э : ЭЛЕМЕНТ.графики() )
            group.getChildren().add( new EdtГрафика( э ).загрузить( false ) );
        
        String s;
        if( XMLNS_SVG.equals( ЭЛЕМЕНТ.getSource().тип().uri() ) )
            switch( ЭЛЕМЕНТ.getSource().тип().название() )
            {
                case SVG_ELEMENT_TEXT:
                    //VBox box = new VBox();
                    for( FxИнструкция э : ЭЛЕМЕНТ.инструкции() )
                        /*box*/group.getChildren().add( true/*основной*/ ?
                                new SvgTextController( ЭЛЕМЕНТ.getSource(), э.getSource() ).build() :
                                new EdtИнструкция( э ).загрузить( false ) ); //TODO offset?
                    for( FxТекстовыйБлок э : ЭЛЕМЕНТ.тексты() )
                        /*box*/group.getChildren().add( true/*основной*/ ?
                                new SvgTextController( ЭЛЕМЕНТ.getSource(), э.getSource() ).build() :
                                new EdtТекстовыйБлок( э ).загрузить( false ) ); //TODO offset?
                    //group.getChildren().add( box );
                    break;
                    
                case SVG_ELEMENT_RECT:
                    Rectangle rect = new Rectangle();
                    rect.setX( toSvgDouble( SVG_ATTR_X, 0d ) );
                    rect.setY( toSvgDouble( SVG_ATTR_Y, 0d ) );
                    rect.setWidth( toSvgDouble( SVG_ATTR_WIDTH, 0d ) );
                    rect.setHeight( toSvgDouble( SVG_ATTR_HEIGHT, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) rect.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) rect.setFill( toSvgColor( s ) );
                    group.getChildren().add( rect );
                    break;
                    
                case SVG_ELEMENT_LINE:
                    Line line = new Line();
                    line.setStartX( toSvgDouble( SVG_ATTR_X1, 0d ) );
                    line.setStartY( toSvgDouble( SVG_ATTR_Y1, 0d ) );
                    line.setEndX( toSvgDouble( SVG_ATTR_X2, 0d ) );
                    line.setEndY( toSvgDouble( SVG_ATTR_Y2, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) line.setStroke( toSvgColor( s ) );
                    group.getChildren().add( line );
                    break;
                    
                case SVG_ELEMENT_POLYLINE:
                    Polyline polyline = new Polyline();
                    polyline.getPoints().setAll( toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) polyline.setStroke( toSvgColor( s ) );
                    group.getChildren().add( polyline );
                    break;
                    
                case SVG_ELEMENT_POLYGON:
                    Polygon polygon = new Polygon();
                    polygon.getPoints().setAll( toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) polygon.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) polygon.setFill( toSvgColor( s ) );
                    group.getChildren().add( polygon );
                    break;
                    
                case SVG_ELEMENT_CIRCLE:
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
                    
                case SVG_ELEMENT_ELLIPSE:
                    Ellipse ellipse = new Ellipse();
                    ellipse.setCenterX( toSvgDouble( SVG_ATTR_CX, 0d ) );
                    ellipse.setCenterY( toSvgDouble( SVG_ATTR_CY, 0d ) );
                    ellipse.setRadiusX( toSvgDouble( SVG_ATTR_RX, 0d ) );
                    ellipse.setRadiusY( toSvgDouble( SVG_ATTR_RY, 0d ) );
                    s = toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) ellipse.setStroke( toSvgColor( s ) );
                    s = toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) ellipse.setFill( toSvgColor( s ) );
                    group.getChildren().add( ellipse );
                    break;
                    
                default:
//                    Group group = new Group();
//                    for( FxИнструкция н : ЭЛЕМЕНТ.инструкции() )
//                        group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//                    for( FxТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//                        group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//                    for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//                        group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
//                    node = group;
            }
//        else
//        {
//            Group group = new Group();
//            for( FxИнструкция н : ЭЛЕМЕНТ.инструкции() )
//                group.getChildren().add( new EdtИнструкция( н ).загрузить( изменяемый ) );
//            for( FxТекстовыйБлок н : ЭЛЕМЕНТ.тексты() )
//                group.getChildren().add( new EdtТекстовыйБлок( н ).загрузить( изменяемый ) );
//            for( FxАтрибутный н : ЭЛЕМЕНТ.прочее() )
//                group.getChildren().add( new EdtНеизвестный( н ).загрузить( изменяемый ) );
//            node = group;
//        }
        return group;
    }
    
    @Override
    public boolean составить( Queue<int[]> path )
    {
        if( path.isEmpty() ) return false;
        switch( ЭЛЕМЕНТ.getSource().тип().название() )
        {
            case SVG_ELEMENT_TEXT:
                int[] xy = path.poll();
                if( !path.isEmpty() ) return false;
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_X, XMLNS_SVG, xy[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_Y, XMLNS_SVG, xy[1] );
                DbТекстовыйБлок текст = (DbТекстовыйБлок)ЭЛЕМЕНТ.getSource().архив()
                        .создатьНовыйЭлемент( Xml.XML_CDATA, null );
                текст.текст( "New text" );
                ЭЛЕМЕНТ.тексты().add( new FxТекстовыйБлок( текст ) );
                break;

            case SVG_ELEMENT_RECT:
                int[] lt = path.poll();
                int[] rb = path.poll();
                if( !path.isEmpty() ) return false;
                if( lt[0] > rb[0] ) { int t = lt[0]; lt[0] = rb[0]; rb[0] = t; }
                if( lt[1] > rb[1] ) { int t = lt[1]; lt[1] = rb[1]; rb[1] = t; }
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_X, XMLNS_SVG, lt[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_Y, XMLNS_SVG, lt[1] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_WIDTH, XMLNS_SVG, rb[0]-lt[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_HEIGHT, XMLNS_SVG, rb[1]-lt[1] );
                break;
                
                
            case SVG_ELEMENT_LINE:
                xy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_X1, XMLNS_SVG, xy[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_Y1, XMLNS_SVG, xy[1] );
                xy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_X2, XMLNS_SVG, xy[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_Y2, XMLNS_SVG, xy[1] );
                if( !path.isEmpty() ) return false;
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_STROKE, XMLNS_SVG, "black" ); // не видно, если не задать
                break;

            case SVG_ELEMENT_CIRCLE:
                int[] cxy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_CX, XMLNS_SVG, cxy[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_CY, XMLNS_SVG, cxy[1] );
                xy = path.poll();
                double dx = cxy[0] - xy[0];
                double dy = cxy[1] - xy[1];
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_R, XMLNS_SVG, (int)Math.round( Math.sqrt(dx*dx+dy*dy) ) ); //TODO round ?!
                if( !path.isEmpty() ) return false;
                break;

            case SVG_ELEMENT_ELLIPSE:
                cxy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_CX, XMLNS_SVG, cxy[0] );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_CY, XMLNS_SVG, cxy[1] );
                xy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_RX, XMLNS_SVG, Math.abs( cxy[0] - xy[0] ) );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_RY, XMLNS_SVG, Math.abs( cxy[1] - xy[1] ) );
                if( !path.isEmpty() ) return false;
                break;

            case SVG_ELEMENT_POLYLINE:
                int[] points = new int[path.size()*2];
                for( int i = 0, max = path.size(); i < max; i++ )
                {
                    xy = path.poll();
                    points[i*2+0] = xy[0];
                    points[i*2+1] = xy[1];
                }
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_POINTS, XMLNS_SVG, points );
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_STROKE, XMLNS_SVG, "black" ); // не видно, если не задать
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_FILL, XMLNS_SVG, "none" ); // выполняется заливка, если не задать
                break;
                
            case SVG_ELEMENT_POLYGON:
                points = new int[path.size()*2];
                for( int i = 0, max = path.size(); i < max; i++ )
                {
                    xy = path.poll();
                    points[i*2+0] = xy[0];
                    points[i*2+1] = xy[1];
                }
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_POINTS, XMLNS_SVG, points );
                //ЭЛЕМЕНТ.определить( SVG_ATTR_FILL, XMLNS_SVG, null, "black" ); // не видно, если не задать
                break;

            default:
                xy = path.poll();
                ЭЛЕМЕНТ.getSource().определить( SVG_ATTR_TRANSFORM, XMLNS_SVG, String.format( "translate(%d,%d)", xy[0], xy[1] ) );
        }
        
        return true;
    }
    
}
