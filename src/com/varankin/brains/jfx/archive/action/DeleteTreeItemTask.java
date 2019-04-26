package com.varankin.brains.jfx.archive.action;

import com.varankin.brains.db.DbАтрибутный;
import com.varankin.brains.jfx.db.FxАтрибутный;
import com.varankin.brains.jfx.db.FxМусор;
import com.varankin.brains.jfx.db.FxОператор;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 * {@linkplain Task Задача} удаления элемента из архива.
 * 
 * @author &copy; 2019 Николай Варанкин
 */
class DeleteTreeItemTask extends TransactionalChildParentTask
{
    private static final FxОператор<Boolean> ОПЕРАТОР = new FxОператор<Boolean>()
    {
        @Override
        public <T> Boolean выполнить( T объект, ObservableList<T> коллекция )
        {
            FxАтрибутный<? extends DbАтрибутный> a = (FxАтрибутный<?>)объект;
            boolean мусор = a instanceof FxМусор || a.предок( true ) instanceof FxМусор;
            boolean удален = коллекция.remove( объект );
            return удален && ( мусор ? a.архив().удалить( a ) : a.архив().утилизировать( a ) );
        }
    };

    DeleteTreeItemTask( TreeItem<FxАтрибутный> ti )
    {
        super( ti, ОПЕРАТОР, "task.delete.succeeded", 
                "task.delete.surprise", "task.delete.failed" );
    }

}
