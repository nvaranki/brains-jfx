package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@linkplain Task Задача} удаления элемента из архива.
 * 
 * @author &copy; 2019 Николай Варанкин
 */
class RestoreTreeItemTask extends TransactionalChildParentTask
{
    private static final FxОператор<Boolean> ОПЕРАТОР = new FxОператор<Boolean>()
    {
        @Override
        public <T> Boolean выполнить( T объект, ObservableList<T> коллекция )
        {
            return false;//TODO NOT IMPL. коллекция.restore( объект );
        }
    };

    RestoreTreeItemTask( TreeItem<FxАтрибутный> ti )
    {
        super( ti, ОПЕРАТОР, "task.restore.succeeded", 
                "task.restore.surprise", "task.restore.failed" );
    }

}
