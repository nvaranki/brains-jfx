package com.varankin.brains.jfx.archive;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.io.xml.XmlSvg;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.brains.jfx.db.FxProperty;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.util.LoggerX;
import com.varankin.util.Текст;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.concurrent.Task;

/**
 * Задача создания элемента коллекции.
 * 
 * @author &copy; 2017 Николай Варанкин
 */
class TaskMultiplyАтрибутный extends Task<Void>
{
    private static final LoggerX LOGGER = LoggerX.getLogger( TaskMultiplyАтрибутный.class );
    private static final FxОператор ВЛОЖИТЬ = ( о, к ) -> к.add( о );
    
    private final FxАтрибутный<?> владелец, образец;
    private final Схема схема;

    TaskMultiplyАтрибутный( FxАтрибутный<?> владелец, FxАтрибутный<?> образец, Схема схема ) 
    {
        this.владелец = владелец;
        this.образец = образец;
        this.схема = схема;
    }
    
    @Override
    protected Void call() throws Exception 
    {
        try( final Транзакция т = владелец.getSource().транзакция() )
        {
            т.согласовать( Транзакция.Режим.ЗАПРЕТ_ДОСТУПА, владелец.getSource() );
            // дублировать образец в RC матрицу, оставив место [0,0] для образца
            for( int r = 0; r < схема.y.повтор; ++r )
                for( int c = 0; c < схема.x.повтор; ++c )
                    if( r == 0 & c == 0 )
                    {
                        final int fr = r, fc = c;
                        Platform.runLater( () -> переименовать( образец, fr, fc ) ); //TODO why runLater() for Fx*?
                    }
                    else
                    {
                        // создать именованный дубликат по образцу
                        FxАтрибутный<?> дубликат = FxАтрибутный.дублировать( образец, владелец.архив() );
                        переименовать( дубликат, r, c );
                        // сместить
                        int dx = c * схема.x.смещение, dy = r * схема.y.смещение;
                        if( схема.x.смещение != 0 | схема.y.смещение != 0 )
                        {
                            // добавить смещение к последовательности трансформаций
                            FxProperty атрибут = дубликат.атрибут( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG ); 
                            String value = DbАтрибутный.toStringValue( атрибут.getValue() );
                            value = value == null ? "" : value + " ";
                            атрибут.setValue( String.format( "%stranslate(%d,%d)", value, dx, dy ) ); //TODO optimize transformations
                        }
                        // можно вкладывать; сделать здесь для минимума уведомлений
                        владелец.выполнить( ВЛОЖИТЬ, дубликат );
                        // копий может быть очень много
                        int скопировано = ( r * схема.x.повтор ) + c;
                        if( скопировано % 100 == 0 && 1 + скопировано < схема.y.повтор * схема.x.повтор )
                            LOGGER.log( Level.INFO, "multiply.subtotal", скопировано );
                    }
            т.завершить( true );
            LOGGER.log( Level.INFO, "multiply.summary", схема.y.повтор * схема.x.повтор - 1 );
        }
        return null;
    }

    @Override
    protected void failed()
    {
        Текст словарь = JavaFX.getInstance().словарь( TaskMultiplyАтрибутный.class );
        ReadOnlyProperty<DbАтрибутный.Ключ> тип = образец.тип();
        String msg = словарь.текст( "failure", тип.getValue().название() );
        Throwable exception = this.getException();
        LOGGER.log( Level.SEVERE, msg, exception );
    }

    @Override
    protected void cancelled()
    {
        TaskMultiplyАтрибутный.this.failed();
    }

    private void переименовать( FxАтрибутный дубликат, int row, int col ) 
    {
        if( образец instanceof FxЭлемент )
        {
            Property<String> но = ((FxЭлемент)образец).название();
            Property<String> нд = ((FxЭлемент)дубликат).название();
            нд.setValue( схема.название( row, col, но.getValue() ) );
        }
    }

    static class Схема
    {
        static class Направление
        {
            int повтор, смещение;
        }
        enum Наименование
        {
            ЦЕПОЧКА_RC,
            ЦЕПОЧКА_CR,
            МАТРИЦА_RC,
            МАТРИЦА_CR
        }
        String префикс;
        Integer индекс;
        Наименование наименование = Наименование.ЦЕПОЧКА_RC;
        final Направление x = new Направление(), y = new Направление();

        private String название( int row, int col, String название ) 
        {
            String[] spl = split( название );
            String префикс, суффикс;
            String name = this.префикс != null ? this.префикс : spl[0];
            Integer index = индекс != null ? индекс : ( spl[1] == null || spl[1].isEmpty() ) ? null : Integer.valueOf( spl[1] );
            if( name == null && index == null ) return null;
            префикс = name == null ? "" : name;

            int dcc, dcr, dcn; //TODO index defines base, so base + x.повтор and so on
            dcc = 1 + (int)Math.floor( Math.log10( Math.max( 1, x.повтор ) ) );
            dcr = 1 + (int)Math.floor( Math.log10( Math.max( 1, y.повтор ) ) );
            dcn = 1 + (int)Math.floor( Math.log10( Math.max( 1, y.повтор * x.повтор ) ) );

            switch( наименование )
            {
                case ЦЕПОЧКА_RC:
                    суффикс = index == null ? "" : Integer.toString( index + row * x.повтор + col );//TODO dcn
                    return префикс + суффикс;
                case ЦЕПОЧКА_CR:
                    суффикс = index == null ? "" : Integer.toString( index + col * y.повтор + row );
                    return префикс + суффикс;
                case МАТРИЦА_RC:
                    суффикс = index == null ? "" : String.format( "%0" + dcr + "d", row ) + String.format( "%0" + dcc + "d" , col );
                    return префикс + суффикс;
                case МАТРИЦА_CR:
                    суффикс = index == null ? "" : String.format( "%0" + dcr + "d", row ) + String.format( "%0" + dcc + "d" , col );
                    return префикс + суффикс;
                default:
                    return null;
            }
        }
        
        private static String[] split( String название )
        {
            Pattern p = Pattern.compile( "(\\D*)(\\d*)\\z" );
            Matcher pm = p.matcher( название );
            return pm.matches() ?
                    new String[] { pm.group( 1 ), pm.group( 2 ) } :
                    new String[] { null, null };
        }

    }
}
