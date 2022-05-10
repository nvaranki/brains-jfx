package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.db.type.DbАтрибутный;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxОператор;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@linkplain Task Задача} удаления элемента из архива.
 * 
 * @author &copy; 2021 Николай Варанкин
 */
class RestoreTreeItemTask extends TransactionalChildParentTask
{
    private static final FxОператор<Boolean> ОПЕРАТОР = new FxОператор<Boolean>()
    {
        @Override
        public <T> Boolean выполнить( T объект, ObservableList<T> коллекция )
        {
            FxАтрибутный<? extends DbАтрибутный> a = (FxАтрибутный<?>)объект;
            boolean удален = a.восстановимый().getValue() && коллекция.remove( объект );
            return удален && a.архив().вернуть( a );
        }
    };

    RestoreTreeItemTask( TreeItem<FxАтрибутный> ti )
    {
        super( ti, ОПЕРАТОР, "task.restore.succeeded", 
                "task.restore.surprise", "task.restore.failed" );
    }

}
