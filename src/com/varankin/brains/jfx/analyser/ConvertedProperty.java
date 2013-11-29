package com.varankin.brains.jfx.analyser;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.util.Callback;

/**
 *
 * @author Николай
 */
public class ConvertedProperty<T,S> extends SimpleObjectProperty<T> // extends ObjectPropertyBase<T>// implements Property<T> //
{
    private final Callback<S,T> S2T;
    private final Callback<T,S> T2S;
    
    private ChangeListener<S> peerListener;
    private ChangeListener<T> thisListener;
    private Property<S> PEER;

    public ConvertedProperty( Callback<S,T> s2t, Callback<T,S> t2s )
    {
        S2T = s2t;
        T2S = t2s;
    }

    public final void setPeer( Property<S> peer )
    {
        PEER = peer;
        setValue( getValue() );
        //bind( Bindings.b );
        //thisListener = ;
        addListener( new WeakChangeListener<>( thisListener ) );
        //peerListener = ;
        peer.addListener( new WeakChangeListener<>( peerListener ) );
    }
    
    @Override
    public final void setValue( T t )
    {
        if( PEER != null )
            PEER.setValue( T2S.call( t ) );
        else
            super.setValue( t );
    }
    
//    abstract protected T convertPeerToThis( S value );
//    abstract protected S convertThisToPeer( T value );
    
//    @Override
//    public T get()
//    {
//        return null;
//    }
//
//    @Override
//    public void set( T t )
//    {
//        
//    }

}
