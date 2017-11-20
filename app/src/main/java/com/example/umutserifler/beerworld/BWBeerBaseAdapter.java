package com.example.umutserifler.beerworld;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by umutserifler on 19.11.2017.
 */

public class BWBeerBaseAdapter extends BaseAdapter {

    ArrayList<DataListProperties> myCurrentList = new ArrayList<DataListProperties>();

    Context context;
    LayoutInflater inflater;
    int listViewType;

    BWBeerModalClass beerClass = BWBeerModalClassSingleton.getInstance(this.context).getBeerClass();

    public BWBeerBaseAdapter(Context context, ArrayList<DataListProperties> myList, int type) {
        this.context = context;
        this.myCurrentList = myList;
        this.listViewType = type;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myCurrentList.size();
    }

    @Override
    public Object getItem(int position) {
        return myCurrentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            switch (this.listViewType) {
                case 0:
                    convertView = inflater.inflate(R.layout.custom_beer_listview_layout, viewGroup, false);
                    break;
                case 1:
                    //convertView = inflater.inflate(R.layout.custom_cell_oven_imageview, parent, false);
                    break;
                case 2:
                    //convertView = inflater.inflate(R.layout.custom_cell_oven_imagetextview, parent, false);
                    break;
                case 5:
                    //convertView = inflater.inflate(R.layout.custom_cell_shdevice_error, parent, false);
                    break;
                case 6:
                    //convertView = inflater.inflate(R.layout.custom_cell_oven_delay_textview, parent, false);
                    break;
                default:
                    break;
            }

            mViewHolder = new MyViewHolder(convertView, this.listViewType);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        DataListProperties currentListData = (DataListProperties) getItem(position);
        switch (this.listViewType) {
            case 0:
                mViewHolder.textViewForCustomCell.setText(currentListData.getTitle());
                break;
            case 1:
                mViewHolder.imageViewForCustomCell.setImageResource(currentListData.getImgResId());
                break;
            case 2:
                mViewHolder.textViewForCustomCell.setText(currentListData.getTitle());
                mViewHolder.imageViewForCustomCell.setImageResource(currentListData.getImgResId());
                mViewHolder.textViewForCustomCellExtra.setText(currentListData.getExtra());
                break;
            case 5:
                mViewHolder.textViewForCustomCell.setText(currentListData.getTitle());
                mViewHolder.textViewForCustomCellExtra.setText(currentListData.getExtra());
                break;
            case 6:
                mViewHolder.textViewForCustomDelayItem.setText(currentListData.getTitle());
            default:
                break;
        }

        if (this.listViewType == 0) {

        } else if (this.listViewType == 1) {

        } else if (this.listViewType == 5) {

        }else if (this.listViewType == 6) {

        }
        if(reachedEndOfList(position)) loadMoreData();

        return convertView;
    }

    private void loadMoreData() {
        beerClass.getRequestNewDataWithPageNumber();
    }

    private boolean reachedEndOfList(int position) {
        return (beerClass.beerListViewArrayList.size() > (position + 30)) ? false : true;
    }

    private class MyViewHolder {
        TextView textViewForCustomCell;
        TextView textViewForCustomDelayItem;
        TextView textViewForCustomCellExtra;
        ImageView imageViewForCustomCell;
        int celltype;

        public MyViewHolder(View item, int celltype) {
            this.celltype = celltype;
            switch (this.celltype) {
                case 0:
                    textViewForCustomCell = (TextView) item.findViewById(R.id.textViewCustomCell);
                    break;
                case 1:
                    //imageViewForCustomCell = (ImageView) item.findViewById(R.id.imageCustomCell);
                    break;
                case 2:
                    //textViewForCustomCell = (TextView) item.findViewById(R.id.textViewCustomCellInImageTextView);
                    //imageViewForCustomCell = (ImageView) item.findViewById(R.id.imageCustomCellInImageTextView);
                    //textViewForCustomCellExtra = (TextView) item.findViewById(R.id.textViewCustomCellExtra);
                    break;
                case 5:
                    //textViewForCustomCell = (TextView) item.findViewById(R.id.shDeviceErrorName);
                    //textViewForCustomCellExtra = (TextView) item.findViewById(R.id.shDeviceErrorValue);
                    break;
                case 6:
                    //textViewForCustomDelayItem = (TextView) item.findViewById(R.id.textViewCustomCellForDelayItem);
                    break;
                default:
                    break;
            }

        }
    }


}
