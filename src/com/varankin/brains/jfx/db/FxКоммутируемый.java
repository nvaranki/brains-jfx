package com.varankin.brains.jfx.db;

import javafx.beans.property.ReadOnlyListProperty;

/**
 * Коммутируемый элемент мыслительной структуры. 
 * Содержит набор соединений для передачи сигналов.
 *
 * @author &copy; 2016 Николай Варанкин
 */
public interface FxКоммутируемый
{

    /**
     * @return подключаемые соединения.
     */
    ReadOnlyListProperty<FxСоединение> соединения();
    
}
