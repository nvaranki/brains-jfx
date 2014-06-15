package com.varankin.brains.jfx;

import javafx.beans.value.ObservableStringValue;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Контейнер озаглавленного {@linkplain Node узла} графа сцены.
 *
 * @author &copy; 2012 Николай Варанкин
 */
@Deprecated // use Node.getProperties()
class TitledSceneGraph 
{
    final Node node;
    final Image icon;
    final ObservableStringValue title;

    TitledSceneGraph( Node node, Image icon, ObservableStringValue title )
    {
        this.node = node;
        this.icon = icon;
        this.title = title;
    }

}
