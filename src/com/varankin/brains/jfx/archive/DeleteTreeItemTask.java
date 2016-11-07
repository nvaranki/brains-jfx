package com.varankin.brains.jfx.archive;

import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@linkplain Task Задача} удаления элемента из архива.
 * 
 * @author &copy; 2016 Николай Варанкин
 */
class DeleteTreeItemTask extends TransactionalChildParentTask
{
    private static final FxОператор<Boolean> ОПЕРАТОР = new FxОператор<Boolean>()
    {
        @Override
        public <T> Boolean выполнить( T объект, ObservableList<T> коллекция )
        {
            return коллекция.remove( объект );
        }
    };

    DeleteTreeItemTask( TreeItem<FxАтрибутный> ti )
    {
        super( ti, ОПЕРАТОР, "task.delete.succeeded", 
                "task.delete.surprise", "task.delete.failed" );
    }

}
