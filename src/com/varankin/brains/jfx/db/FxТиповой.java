package com.varankin.brains.jfx.db;

import com.varankin.brains.db.xml.XLinkActuate;
import com.varankin.brains.db.xml.XLinkShow;
import com.varankin.brains.db.xml.ЗонныйКлюч;

/**
 * Типовой элемент мыслительной структуры. 
 * Содержит ссылку на образец элемента.
 *
 * @author &copy; 2021 Николай Варанкин
 */
public interface FxТиповой<T> 
{
    final ЗонныйКлюч КЛЮЧ_А_ЭКЗЕМПЛЯР = new ЗонныйКлюч( "экземпляр", null );

    FxProperty<String> ссылка();

    FxProperty<XLinkShow> вид();

    FxProperty<XLinkActuate> реализация();
    
    FxReadOnlyProperty<T> экземпляр();
    
}
