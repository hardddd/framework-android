/*
 * Copyright (C) 2020 Veli Tasalı
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.genonbeta.android.framework.util.actionperformer;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.object.Selectable;

import java.util.ArrayList;
import java.util.List;

public class EngineConnection<T extends Selectable> implements IEngineConnection<T>
{
    private IPerformerEngine mEngine;
    private SelectableProvider<T> mSelectableProvider;
    private SelectableHost<T> mSelectableHost;

    public EngineConnection()
    {
        this(null);
    }

    public EngineConnection(IPerformerEngine engine)
    {
        setEngine(engine);
    }

    protected boolean changeSelectionState(T selectable, boolean selected)
    {
        return selectable.setSelectableSelected(selected) && (selected && getHostList().add(selectable))
                || (!selected && getHostList().remove(selectable));
    }

    @Nullable
    public IPerformerEngine getEngine()
    {
        return mEngine;
    }

    @Override
    public List<Selectable> getSelectableList()
    {
        return new ArrayList<Selectable>(getSelectionList());
    }

    public List<T> getHostList() {
        return getSelectableHost().getSelectableList();
    }

    public List<T> getSelectionList() {
        return getSelectableProvider().getSelectableList();
    }

    public SelectableHost<T> getSelectableHost()
    {
        return mSelectableHost;
    }

    public SelectableProvider<T> getSelectableProvider()
    {
        return mSelectableProvider;
    }

    public boolean isSelected(T selectable)
    {
        return getSelectionList().contains(selectable);
    }

    public void setEngine(IPerformerEngine engine)
    {
        engine.ensureSlot(this);
        mEngine = engine;
    }

    public void setSelectableHost(SelectableHost<T> host) {
        mSelectableHost = host;
    }

    public void setSelectableProvider(@Nullable SelectableProvider<T> provider)
    {
        mSelectableProvider = provider;
    }

    public boolean setSelected(RecyclerView.ViewHolder holder)
    {
        return setSelected(holder.getAdapterPosition());
    }

    public boolean setSelected(int position)
    {
        return setSelected(mSelectableProvider.getSelectableList().get(position), position);
    }

    public boolean setSelected(T selectable)
    {
        return setSelected(selectable, !isSelected(selectable), -1);
    }

    public boolean setSelected(T selectable, boolean selected)
    {
        return setSelected(selectable, selected, -1);
    }

    public boolean setSelected(T selectable, int position)
    {
        return setSelected(selectable, !isSelected(selectable), position);
    }

    public boolean setSelected(T selectable, boolean selected, int position)
    {
        // if it is already the same
        if (selected == isSelected(selectable))
            return selected;

        return getEngine() != null && changeSelectionState(selectable, selected)
                && getEngine().check(this, selectable, selected, position);
    }
}