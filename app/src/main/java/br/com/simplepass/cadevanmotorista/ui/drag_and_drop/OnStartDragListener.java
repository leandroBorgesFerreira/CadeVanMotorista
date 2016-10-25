package br.com.simplepass.cadevanmotorista.ui.drag_and_drop;

import android.support.v7.widget.RecyclerView;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;

/**
 * Created by leandro on 3/16/16.
 */
public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
