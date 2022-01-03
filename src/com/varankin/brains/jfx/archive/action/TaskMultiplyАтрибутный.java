package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.db.DbПреобразователь;
import com.varankin.brains.db.Транзакция;
import com.varankin.brains.db.xml.МаркированныйЗонныйКлюч;
import com.varankin.brains.jfx.archive.multi.Схема;
import com.varankin.brains.jfx.db.FxProperty;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import com.varankin.brains.jfx.db.FxЭлемент;
import com.varankin.brains.jfx.JavaFX;
import com.varankin.io.xml.svg.XmlSvg;
import com.varankin.util.LoggerX;
import com.varankin.util.Текст;

import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.concurrent.Task;

/**
 * Задача мультипликации элемента коллекции.
 * 
 * @author &copy; 2021 Николай Варанкин
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
                            FxProperty атрибут = дубликат.атрибут( XmlSvg.SVG_ATTR_TRANSFORM, XmlSvg.XMLNS_SVG, FxProperty.class ); 
                            String value = DbПреобразователь.toStringValue( атрибут.getValue() );
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
        ReadOnlyProperty<МаркированныйЗонныйКлюч> тип = образец.тип();
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

}
