package com.varankin.brains.jfx;

import javafx.beans.value.ObservableStringValue;
import javafx.scene.Node;

/**
 * Контейнер озаглавленного {@linkplain Node узла} графа сцены.
 *
 * @author &copy; 2012 Николай Варанкин
 */
@Deprecated // use Node.getProperties()
class TitledSceneGraph 
{
    final Node node;
    final ObservableStringValue title;

    TitledSceneGraph( Node node, ObservableStringValue title )
    {
        this.node = node;
        this.title = title;
    }

}
