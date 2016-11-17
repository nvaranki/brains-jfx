package com.varankin.brains.jfx.db;

import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.property.adapter.Disposer;
import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.db.Транзакция;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import sun.reflect.misc.MethodUtil;

/**
 *
 * @author Varankine
 */
class FxProperty<T> implements Property<T>, 
        WritableObjectValue<T>, ReadOnlyProperty<T>, 
        ObservableObjectValue<T>, JavaBeanProperty<T>
{
    protected final FxPropertyDescriptor descriptor;
    protected final FxPropertyDescriptor.FxListener<T> listener;
    protected ObservableValue<? extends T> observable = null;
    protected ExpressionHelper<T> helper = null;
    protected final AccessControlContext acc = AccessController.getContext();

    FxProperty( DbАтрибутный элемент, String название )
    {
        descriptor = new FxPropertyDescriptor( название, элемент.getClass(), 
            getAccessibleMethod( элемент, название, 0 ), 
            getAccessibleMethod( элемент, название, 1 ) );
        listener = descriptor.new FxListener<T>( элемент, FxProperty.this );
        descriptor.addListener( listener );
        Disposer.addRecord( FxProperty.this, 
                new DescriptorListenerCleaner( descriptor, listener ) );
    }

    private static Method getAccessibleMethod( Object элемент, String название, int pc )
    {
        for( Method m : элемент.getClass().getMethods() )
            if( m.getName().equals( название ) )
                if( m.getParameterCount() == pc )
                {
                    m.setAccessible( true ); // проблема с унаследованным public final
                    return m;
                }
        return null;
    }
    
    @Override
    public final T get()
    {
        return AccessController.doPrivileged( (PrivilegedAction<T>)() -> 
        {
            DbАтрибутный bean = getBean();
            try( final Транзакция т = bean.транзакция() )
            {
                T rv = (T)MethodUtil.invoke( descriptor.getGetter(), bean, (Object[])null );
                т.завершить( true );
                т.завершить( true );
                return rv;
            }
            catch( IllegalAccessException | InvocationTargetException e )
            {
                throw new UndeclaredThrowableException( e );
            }
            catch( Exception e )
            {
                throw new RuntimeException( e );
            }
        }, acc );
    }

    @Override
    public final void set( final T value )
    {
        if( isBound() )
        {
            throw new RuntimeException( "A bound value cannot be set." );
        }
        AccessController.doPrivileged( (PrivilegedAction<Void>)() -> 
        {
            DbАтрибутный bean = getBean();
            try( final Транзакция т = bean.транзакция() )
            {
                MethodUtil.invoke( descriptor.getSetter(), bean, new Object[] {value} );
                т.завершить( true );
                ExpressionHelper.fireValueChangedEvent( helper );
            }
            catch( IllegalAccessException | InvocationTargetException e )
            {
                throw new UndeclaredThrowableException( e );
            }
            catch( Exception e )
            {
                throw new RuntimeException( e );
            }
            return null;
        }, acc );
    }

    @Override
    public final void bind( ObservableValue<? extends T> observable )
    {
        if( observable == null )
        {
            throw new NullPointerException( "Cannot bind to null" );
        }
        if( !observable.equals( this.observable ) )
        {
            unbind();
            set( observable.getValue() );
            this.observable = observable;
            this.observable.addListener( listener );
        }
    }

    @Override
    public final void unbind()
    {
        if( observable != null )
        {
            observable.removeListener( listener );
            observable = null;
        }
    }

    @Override
    public final boolean isBound()
    {
        return observable != null;
    }

    @Override
    public final DbАтрибутный getBean()
    {
        return (DbАтрибутный)listener.getBean();
    }

    @Override
    public final String getName()
    {
        return descriptor.getName();
    }

    @Override
    public final void addListener( ChangeListener<? super T> listener )
    {
        helper = ExpressionHelper.addListener( helper, this, listener );
    }

    @Override
    public final void removeListener( ChangeListener<? super T> listener )
    {
        helper = ExpressionHelper.removeListener( helper, listener );
    }

    @Override
    public final void addListener( InvalidationListener listener )
    {
        helper = ExpressionHelper.addListener( helper, this, listener );
    }

    @Override
    public final void removeListener( InvalidationListener listener )
    {
        helper = ExpressionHelper.removeListener( helper, listener );
    }

    @Override
    public final void fireValueChangedEvent()
    {
        ExpressionHelper.fireValueChangedEvent( helper );
    }

    @Override
    public final void dispose()
    {
        descriptor.removeListener( listener );
    }
    
    @Override
    public final T getValue()
    {
        return get();
    }

    @Override
    public final void setValue( T v )
    {
        set( v );
    }

    @Override
    public final void bindBidirectional( Property<T> other )
    {
        Bindings.bindBidirectional( this, other );
    }

    @Override
    public final void unbindBidirectional( Property<T> other )
    {
        Bindings.unbindBidirectional( this, other );
    }

}
