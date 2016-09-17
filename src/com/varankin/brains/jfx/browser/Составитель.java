package com.varankin.brains.jfx.browser;

import com.varankin.brains.artificial.Элемент;
import com.varankin.characteristic.Изменение;
import com.varankin.characteristic.Наблюдатель;

/**
 * Контроллер структуры узлов дерева.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
final class Составитель implements Наблюдатель<Элемент>
{
    private final DelayedNamedTreeItem treeItem;

    Составитель( DelayedNamedTreeItem treeItem )
    {
        this.treeItem = treeItem;
    }

    @Override
    public void отклик( Изменение<Элемент> изменение )
    {
        if( изменение.ПРЕЖНЕЕ != null )
        {
            treeItem.разобратьДерево( изменение.ПРЕЖНЕЕ );
        }
        if( изменение.АКТУАЛЬНОЕ != null )
        {
            treeItem.построитьДерево( изменение.АКТУАЛЬНОЕ );
        }
    }

}
