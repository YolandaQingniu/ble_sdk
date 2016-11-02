package com.kingnewblesdk.demo;

/**
 * Created by hdr on 15/12/21.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hdr.yolanda.kingnewblesdk.app.R;
import com.kitnew.ble.QNData;
import com.kitnew.ble.QNItemData;

public class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> {

    QNData qnData;

    LayoutInflater inflater;

    public IndicatorAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setQnData(QNData qnData) {
        this.qnData = qnData;
        notifyDataSetChanged();
    }

    @Override
    public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.indicator_list_item, parent, false);
        return new IndicatorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IndicatorViewHolder holder, int position) {
        holder.init(qnData.getAll().get(position));
    }

    @Override
    public int getItemCount() {
        return qnData == null ? 0 : qnData.size();
    }

    class IndicatorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView valueTv;

        public IndicatorViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            valueTv = (TextView) itemView.findViewById(R.id.valueTv);
        }

        void init(QNItemData itemData) {
            nameTv.setText(itemData.name);
            if (itemData.type == QNData.TYPE_BODY_SHAPE) {
                valueTv.setText(itemData.valueStr);
            } else {
                valueTv.setText(itemData.value + "");
            }

        }
    }

}


