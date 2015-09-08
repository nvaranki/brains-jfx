package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.async.Процесс;
import com.varankin.brains.artificial.Проект;
import com.varankin.brains.artificial.Элемент;
import com.varankin.brains.factory.Вложенный;
import com.varankin.brains.jfx.JavaFX;

/**
 * Экранная форма просмотра мыслительных структур.
 *
 * @author &copy; 2015 Николай Варанкин
 */
public class BrowserView extends AbstractBrowserView<Элемент> 
{
    public BrowserView()
    {
        super( JavaFX.getInstance().контекст.мыслитель,
               ( Элемент э ) -> Вложенный.извлечь( Проект.class,  э ) != null,
               ( Элемент э ) -> Вложенный.извлечь( Процесс.class, э ) != null, 
               ( Элемент э ) -> Вложенный.извлечь( Элемент.class, э ) != null );
    }
    
    @Override
    protected <T> T convert( Элемент элемент, Class<T> cls )
    {
        return Вложенный.извлечь( cls, элемент );
    }

}
