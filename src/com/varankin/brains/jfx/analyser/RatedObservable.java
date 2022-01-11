package com.varankin.brains.jfx.analyser;

import com.varankin.brains.artificial.rating.КаталогРанжировщиков;
import com.varankin.brains.artificial.rating.Ранжируемый;
import com.varankin.brains.artificial.rating.СтандартныйРанжировщик;
import com.varankin.characteristic.Именованный;
import com.varankin.characteristic.КаталогСвойств;
import com.varankin.characteristic.НаблюдаемоеСвойство;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Наблюдаемое свойство с набором ранжировщиков.
 * 
 * @param <T> тип наблюдаемой величины.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
public final class RatedObservable<T> implements Именованный
{
    private static final Ранжируемый РАНЖИРОВЩИК = new СтандартныйРанжировщик();
    
    private final НаблюдаемоеСвойство<T> СВОЙСТВО;
    private final String НАЗВАНИЕ;
    private final Collection<Ранжируемый> РАНЖИРОВЩИКИ;

    public RatedObservable(НаблюдаемоеСвойство<T> свойство, КаталогСвойств каталог, ResourceBundle rb )
    {
        String ключ = каталог.ключ( свойство );
        Collection<Ранжируемый> варианты = КаталогРанжировщиков.getInstance().get( ключ );
        СВОЙСТВО = свойство;
        НАЗВАНИЕ = rb.containsKey( ключ ) ? rb.getString( ключ ) : ключ;
        РАНЖИРОВЩИКИ = new ArrayList<>();
        if( варианты != null ) РАНЖИРОВЩИКИ.addAll( варианты );
        РАНЖИРОВЩИКИ.add( РАНЖИРОВЩИК ); // универсальный
    }
    
    public RatedObservable( НаблюдаемоеСвойство<T> свойство, 
            Collection<Ранжируемый> варианты, String название )
    {
        СВОЙСТВО = свойство;
        НАЗВАНИЕ = название;
        РАНЖИРОВЩИКИ = new ArrayList<>(); //TODO index
        if( варианты != null ) РАНЖИРОВЩИКИ.addAll( варианты );
        РАНЖИРОВЩИКИ.add( РАНЖИРОВЩИК ); // универсальный
    }
    
    public НаблюдаемоеСвойство<T> свойство()
    {
        return СВОЙСТВО;
    }
    
    @Override
    public String название()
    {
        return НАЗВАНИЕ;
    }
    
    public Collection<Ранжируемый> ранжировщики()
    {
        return РАНЖИРОВЩИКИ;
    }
    
 }
