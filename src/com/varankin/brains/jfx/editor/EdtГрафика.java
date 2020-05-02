package com.varankin.brains.jfx.editor;

import com.varankin.brains.db.DbГрафика;
import com.varankin.brains.jfx.db.*;
import java.util.List;
import java.util.Queue;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import static com.varankin.brains.io.xml.XmlSvg.*;
import javafx.scene.transform.Translate;

/**
 *
 * @author &copy; 2020 Николай Варанкин
 */
class EdtГрафика extends EdtУзел<DbГрафика,FxГрафика>
{
    EdtГрафика( FxГрафика элемент )
    {
        super( элемент );
    }
    
    @Override
    protected void инструкции( List<Node> children )
    {
    }
    
    @Override
    protected void тексты( List<Node> children )
    {
    }
    
    @Override
    public Group загрузить( boolean основной )
    {
        Group group = /*основной ? загрузить( new Group(), true ) :*/ super.загрузить( true/*false*/ ); // без текстов, инструкций и прочего
        group.getChildren().addAll( загрузить( ЭЛЕМЕНТ.графики() ) );

        String s;
        if( XMLNS_SVG.equals( ЭЛЕМЕНТ.тип().getValue().uri() ) )
            switch( ЭЛЕМЕНТ.тип().getValue().название() )
            {
                case SVG_ELEMENT_TEXT:
                    //VBox box = new VBox();
                    for( FxИнструкция э : ЭЛЕМЕНТ.инструкции() )
                        /*box*/group.getChildren().add( true/*основной*/ ?
                                new SvgTextController( ЭЛЕМЕНТ, э ).build() :
                                new EdtИнструкция( э ).загрузить( false ) ); //TODO offset?
                    for( FxТекстовыйБлок э : ЭЛЕМЕНТ.тексты() )
                        /*box*/group.getChildren().add( true/*основной*/ ?
                                new SvgTextController( ЭЛЕМЕНТ, э ).build() :
                                new EdtТекстовыйБлок( э ).загрузить( false ) ); //TODO offset?
                    //group.getChildren().add( box );
                    break;
                    
                case SVG_ELEMENT_RECT:
                    Rectangle rect = new Rectangle();
                    rect.setX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X, 0d ) );
                    rect.setY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y, 0d ) );
                    rect.setWidth( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_WIDTH, 0d ) );
                    rect.setHeight( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_HEIGHT, 0d ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) rect.setStroke( toSvgColor( s ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) rect.setFill( toSvgColor( s ) );
                    group.getChildren().add( rect );
                    break;
                    
                case SVG_ELEMENT_LINE:
                    Line line = new Line();
                    line.setStartX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X1, 0d ) );
                    line.setStartY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y1, 0d ) );
                    line.setEndX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X2, 0d ) );
                    line.setEndY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y2, 0d ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) line.setStroke( toSvgColor( s ) );
                    group.getChildren().add( line );
                    break;
                    
                case SVG_ELEMENT_POLYLINE:
                    Polyline polyline = new Polyline();
                    polyline.getPoints().setAll( ЭЛЕМЕНТ.toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) polyline.setStroke( toSvgColor( s ) );
                    group.getChildren().add( polyline );
                    break;
                    
                case SVG_ELEMENT_POLYGON:
                    Polygon polygon = new Polygon();
                    polygon.getPoints().setAll( ЭЛЕМЕНТ.toSvgPoints( SVG_ATTR_POINTS, new Double[]{0d,0d} ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) polygon.setStroke( toSvgColor( s ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) polygon.setFill( toSvgColor( s ) );
                    group.getChildren().add( polygon );
                    break;
                    
                case SVG_ELEMENT_CIRCLE:
                    Circle circle = new Circle();
                    circle.setCenterX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_CX, 0d ) );
                    circle.setCenterY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_CY, 0d ) );
                    circle.setRadius( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_R, 0d ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) circle.setStroke( toSvgColor( s ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) circle.setFill( toSvgColor( s ) );
                    group.getChildren().add( circle );
                    break;
                    
                case SVG_ELEMENT_ELLIPSE:
                    Ellipse ellipse = new Ellipse();
                    ellipse.setCenterX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_CX, 0d ) );
                    ellipse.setCenterY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_CY, 0d ) );
                    ellipse.setRadiusX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_RX, 0d ) );
                    ellipse.setRadiusY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_RY, 0d ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_STROKE, null );
                    if( s != null ) ellipse.setStroke( toSvgColor( s ) );
                    s = ЭЛЕМЕНТ.toSvgString( SVG_ATTR_FILL, null );
                    if( s != null ) ellipse.setFill( toSvgColor( s ) );
                    group.getChildren().add( ellipse );
                    break;
                    
                case SVG_ELEMENT_SYMBOL:
                    group.setVisible( false );
                    break;
                    
                case SVG_ELEMENT_USE:
                    FxГрафика экземпляр = ЭЛЕМЕНТ.экземпляр().getValue();
                    if( экземпляр != null )
                    {
                        Node use = EdtФабрика.getInstance().создать( экземпляр ).загрузить( false );
                        use.setVisible( true ); // see above for SVG_ELEMENT_USE
                        use.getTransforms().add( new Translate( 
                                ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X, 0d ), 
                                ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y, 0d ) ));
                        group.getChildren().add( use );
                    }
                    else if( ЭЛЕМЕНТ.ссылка().getValue() != null )
                    {
                        Rectangle use = new Rectangle();
                        use.setX( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_X, 0d ) - 2d );
                        use.setY( ЭЛЕМЕНТ.toSvgDouble( SVG_ATTR_Y, 0d ) - 2d );
                        use.setWidth( 4d );
                        use.setHeight( 4d );
                        use.setFill( Color.CYAN );
                        use.setStroke( Color.BLUEVIOLET );
                        use.setStrokeType( StrokeType.OUTSIDE );
                        use.setStrokeWidth( 1d );
                        group.getChildren().add( use );
                    }
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
        switch( ЭЛЕМЕНТ.тип().getValue().название() )
        {
            case SVG_ELEMENT_TEXT:
                int[] xy = path.poll();
                if( !path.isEmpty() ) return false;
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_X ).setValue( xy[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_Y ).setValue( xy[1] );
                ЭЛЕМЕНТ.тексты().add( блок( "New text" ) );
                break;

            case SVG_ELEMENT_RECT:
                int[] lt = path.poll();
                int[] rb = path.poll();
                if( !path.isEmpty() ) return false;
                if( lt[0] > rb[0] ) { int t = lt[0]; lt[0] = rb[0]; rb[0] = t; }
                if( lt[1] > rb[1] ) { int t = lt[1]; lt[1] = rb[1]; rb[1] = t; }
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_X ).setValue( lt[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_Y ).setValue( lt[1] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_WIDTH ).setValue( rb[0]-lt[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_HEIGHT ).setValue( rb[1]-lt[1] );
                break;
                
                
            case SVG_ELEMENT_LINE:
                xy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_X1 ).setValue( xy[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_Y1 ).setValue( xy[1] );
                xy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_X2 ).setValue( xy[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_Y2 ).setValue( xy[1] );
                if( !path.isEmpty() ) return false;
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_STROKE ).setValue( "black" ); // не видно, если не задать
                break;

            case SVG_ELEMENT_CIRCLE:
                int[] cxy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_CX ).setValue( cxy[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_CY ).setValue( cxy[1] );
                xy = path.poll();
                double dx = cxy[0] - xy[0];
                double dy = cxy[1] - xy[1];
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_R ).setValue( (int)Math.round( Math.sqrt(dx*dx+dy*dy) ) ); //TODO round ?!
                if( !path.isEmpty() ) return false;
                break;

            case SVG_ELEMENT_ELLIPSE:
                cxy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_CX ).setValue( cxy[0] );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_CY ).setValue( cxy[1] );
                xy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_RX ).setValue( Math.abs( cxy[0] - xy[0] ) );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_RY ).setValue( Math.abs( cxy[1] - xy[1] ) );
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
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_POINTS ).setValue( points );
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_STROKE ).setValue( "black" ); // не видно, если не задать
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_FILL ).setValue( "none" ); // выполняется заливка, если не задать
                break;
                
            case SVG_ELEMENT_POLYGON:
                points = new int[path.size()*2];
                for( int i = 0, max = path.size(); i < max; i++ )
                {
                    xy = path.poll();
                    points[i*2+0] = xy[0];
                    points[i*2+1] = xy[1];
                }
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_POINTS ).setValue( points );
                //ЭЛЕМЕНТ.определить( SVG_ATTR_FILL, null, "black" ); // не видно, если не задать
                break;

            default:
                xy = path.poll();
                ЭЛЕМЕНТ.атрибут( SVG_ATTR_TRANSFORM ).setValue( String.format( "translate(%d,%d)", xy[0], xy[1] ) );
        }
        
        return true;
    }
    
}
