package com.varankin.brains.jfx.db;

import com.varankin.brains.db.КороткийКлюч;
import com.varankin.brains.io.xml.Xml;

/**
 * Типовой элемент мыслительной структуры. 
 * Содержит ссылку на образец элемента.
 *
 * @author &copy; 2020 Николай Варанкин
 */
public interface FxТиповой<T> 
{
    final КороткийКлюч КЛЮЧ_А_ЭКЗЕМПЛЯР = new КороткийКлюч( "экземпляр", null );

    FxProperty<String> ссылка();

    FxProperty<Xml.XLinkShow> вид();

    FxProperty<Xml.XLinkActuate> реализация();
    
    FxReadOnlyProperty<T> экземпляр();
    
}
