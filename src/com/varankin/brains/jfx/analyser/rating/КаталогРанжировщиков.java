package com.varankin.brains.jfx.analyser.rating;

import java.util.*;

/**
 *
 * @author &copy; 2014 Николай Варанкин
 */
public final class КаталогРанжировщиков
{
    private static final Map<String,Collection<Ранжируемый>> КАТАЛОГ 
            = Collections.unmodifiableMap( new КаталогРанжировщиков().индекс );
    
    public static final Map<String,Collection<Ранжируемый>> getInstance()
    {
        return КАТАЛОГ;
    }
    
    private final Map<String,Collection<Ранжируемый>> индекс;
    
    private КаталогРанжировщиков()
    {
        индекс = new HashMap<>();
        индекс.put( ПроцессСостояние, Arrays.asList( new РанжировщикПроцессСостояние() ) );
    }
    
    public static final String ПроцессСостояние = "observable.process.state";
    public static final String ПроцессорПауза = "observable.processor.pause";
    public static final String ПроцессорРестарт = "observable.processor.restart";
    public static final String ПроцессорСтратегия = "observable.processor.strategy";
    public static final String ЗначениеВыход    = "observable.function.output";
    public static final String ЗначимыйЗначение = "observable.computable.output";
    public static final String АргументВход = "observable.function.input";
    public static final String АргументРанг = "observable.computable.rating";
    public static final String ПриемникВход = "observable.receiver.input";
    public static final String ПриемникПринято = "observable.receiver.accepted";
    public static final String ИсточникВыход = "observable.transmitter.output";
    public static final String ИсточникПередано = "observable.transmitter.accepted";
    
}
