package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.runtime.RtЭлемент;
import com.varankin.brains.jfx.JavaFX;

/**
 * Экранная форма просмотра мыслительных структур.
 *
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserViewRt extends AbstractBrowserView<RtЭлемент> 
{
    public BrowserViewRt()
    {
        super( null,//TODO JavaFX.getInstance().контекст.rtt,
               ( RtЭлемент э ) -> RtЭлемент.извлечь( Проект.class,  э ) != null,
               ( RtЭлемент э ) -> RtЭлемент.извлечь( Процесс.class, э ) != null, 
               ( RtЭлемент э ) -> RtЭлемент.извлечь( Элемент.class, э ) != null );
    }
    
    @Override
    protected <T> T convert( RtЭлемент элемент, Class<T> cls )
    {
        return RtЭлемент.извлечь( cls, элемент );
    }

}
