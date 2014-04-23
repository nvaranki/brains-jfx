package com.varankin.brains.jfx.analyser;

import com.varankin.property.PropertyMonitor;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Николай
 */
class ObservablePropertiesStage extends Stage
{
    private PropertyMonitor monitor;

    void setMonitor( PropertyMonitor value )
    {
        monitor = value;
    }

    /**
     * Создает новое значение, отображаемое на графике.
     */ 
    Value createValueInstance()
    {
        //TODO DEBUG START
        @Deprecated int i = 0;//observables.size();
        // first tab
        int buffer = 1000;
        String name = monitor.getClass().getSimpleName() + i;
        String property = "DEBUG"; 
        Value.Convertor<Float> convertor = (Float value, long timestamp) -> new Dot( value, timestamp );
        // next tab
        @Deprecated Color[] colors = {Color.RED, Color.BLUE, Color.GREEN };
        @Deprecated int[][][] patterns = { DotPainter.CROSS, DotPainter.CROSS45, DotPainter.BOX };
        int[][] pattern = patterns[i%patterns.length];
        Color color = colors[i%colors.length];
        //TODO DEBUG END
        DotPainter painter = new BufferedDotPainter( new LinkedBlockingQueue<>(), buffer );
//        painter.valueConvertorProperty().bind( valueRulerController.convertorProperty() );
//        painter.timeConvertorProperty().bind( timeRulerController.convertorProperty() );
//        painter.writableImageProperty().bind( graphController.writableImageProperty() );
        return new Value( monitor, property, convertor, painter, pattern, color, name );
    }
}
