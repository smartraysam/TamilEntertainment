package com.softDev.tamilentertainment.adapter; /**
 /**
 * Created by SMARTTECHX on 7/31/2017.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softDev.tamilentertainment.model.FavouriteItem;
import com.softDev.tamilentertainment.R;
import com.softDev.tamilentertainment.util.SharedPreference;

import java.util.List;

public class FavouriteAdapterList extends ArrayAdapter<FavouriteItem> {

    private Context context;
    List<FavouriteItem> stationList;
    SharedPreference sharedPreference;


    public FavouriteAdapterList(Context context, List<FavouriteItem> stationList) {
        super(context, R.layout.favourite_list_item, stationList);
        this.context = context;
        this.stationList= stationList;
        sharedPreference = new SharedPreference();
    }
    private class ViewHolder {
        TextView stationName;
        ImageView stationImage;
        ImageView favoriteImg;
    }


    public int getCount() {
        return stationList.size();
    }

    @Override
    public FavouriteItem getItem(int position) {
        return stationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favourite_list_item, null);
            holder = new ViewHolder();
            holder.stationName = (TextView) convertView
                    .findViewById(R.id.title);
            holder.stationImage = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.favoriteImg = (ImageView) convertView
                    .findViewById(R.id.imgbtn_favorite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FavouriteItem newItem = (FavouriteItem) getItem(position);
        holder.stationName.setText(newItem.getstationName());
        holder.stationImage.setImageResource(newItem.getstationImage());


		/*If a product exists in shared preferences then set heart_red drawable
		 * and set a tag*/
        if (checkFavoriteItem(newItem)) {
            holder.favoriteImg.setImageResource(R.drawable.heart_red);
            holder.favoriteImg.setTag("red");
        } else {
            holder.favoriteImg.setImageResource(R.drawable.heart_grey);
            holder.favoriteImg.setTag("grey");
        }

        return convertView;
    }
    public boolean checkFavoriteItem(FavouriteItem checkitem) {
        boolean check = false;
        List<FavouriteItem> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (FavouriteItem item : favorites) {
                if (item.equals(checkitem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void add(FavouriteItem favouriteItem) {
        super.add(favouriteItem);
        stationList.add(favouriteItem);
        notifyDataSetChanged();
    }

    @Override
    public void remove(FavouriteItem favouriteItem) {
        super.remove(favouriteItem);
        stationList.remove(favouriteItem);
        notifyDataSetChanged();
    }

}