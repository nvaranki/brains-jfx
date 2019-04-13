package com.varankin.brains.jfx.archive.props;

/**
 * Агент приема-передачи значения атрибута между экраном и хранилищем.
 *
 * @author &copy; 2014 Николай Варанкин
 */
public interface AttributeAgent
{
    void fromScreen();
    void toScreen();
    void fromStorage();
    void toStorage();
}
