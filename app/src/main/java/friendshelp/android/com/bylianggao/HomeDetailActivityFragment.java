package friendshelp.android.com.bylianggao;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gao on 2015/12/8.
 */
public class HomeDetailActivityFragment extends Fragment {
    private static final String ARG_TEXT = "text";
    private static int itemClicked = -1;
    private RecyclerView recyclerView;
    private List<FFService> testList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<JSONObject> jsonArray;
    private String activityObjectId;
    private JSONArray mArray;
    private TextView serviceName;
    private TextView servicePrice;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    parseFriendList();
                    //updateUI();
                    break;
                case 2:
                    updateUI();
                    break;
                default:
                    break;
            }
        };
    };

    private void parseFriendList() {
        for (int i = 0; i < mArray.length(); i++) {
            try {
                JSONObject object = (JSONObject) mArray.get(i);
                jsonArray.add(object);
                Message msg = Message.obtain();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_detail_activity_container,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_detail_activity_item_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        activityObjectId = getArguments().getString(ARG_TEXT).toString();

        jsonArray = new ArrayList<>();


        getFriendListFromParse();

        updateUI();
        return view;
    }

    private void getFriendListFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FFActivity");
        query.whereEqualTo("objectId", activityObjectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject object = objects.get(0);
                    mArray = object.getJSONArray("serviceList");
                    Log.i("sssssss", "friendList get success!!:::" + mArray.toString());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Log.i("sssssss", "img failed " + e.toString());
                }

            }
        });
    }

    private void addItemToList() {

    }

    private void updateUI() {
        if(recyclerViewAdapter == null){
            recyclerViewAdapter = new RecyclerViewAdapter(jsonArray);
            recyclerView.setAdapter(recyclerViewAdapter);
        }else{
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }



    private class RecyclerViewAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<JSONObject> mJsonArray;

        public RecyclerViewAdapter(List<JSONObject> list) {
            mJsonArray = list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.home_detail_activity_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            try {
                JSONObject object = mJsonArray.get(position);
                String name = object.getString("name");
                String price = object.getString("price");
                Log.i("ssssss", "bindView" + name + "  " + price);
                holder.bindItem(name, price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJsonArray.size();
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
            serviceName = (TextView) itemView.findViewById(R.id.home_detail_activity_item_serviceName);
            servicePrice = (TextView) itemView.findViewById(R.id.home_detail_activity_item_servicePrice);
        }

        public void bindItem(String name, String price) {
            serviceName.setText(name);
            servicePrice.setText(price);
        }
    }

}
