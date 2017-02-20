package com.ss.android.article.webmonitor;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.regex.Pattern;

import static com.ss.android.article.webmonitor.BaseApplication.ADD_KEY_OK;
import static com.ss.android.article.webmonitor.BaseApplication.DELETE_KEY_OK;
import static com.ss.android.article.webmonitor.BaseApplication.GET_LIST_OK;
import static com.ss.android.article.webmonitor.BaseApplication.MAX_SESSION_OVERFLOW;
import static com.ss.android.article.webmonitor.BaseApplication.MISS_VALUE;

/**
 * Created by poxiaoge on 2017/1/18.
 */

public class SettingFragment extends Fragment {


    EditText edit_goods_name;
    EditText edit_start_price;
    EditText edit_end_price;
    EditText edit_monitor_interval;
    Button btn_add_key;

    TextView nameView;
    TextView startView;
    TextView endView;

    ListView list_keys;
    ImageButton btn_update_keys;
    MyAdapter<KeyItem> adapter;
    List<KeyItem> keyItemList = new ArrayList<>();
    KeyItem tempKeyItem = new KeyItem();

    View rootView;
    Context mContext;

    private final String tag = this.getClass().getSimpleName();
    ClipboardManager cm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        initView();
    }


    @Override
    public void onStart() {
        super.onStart();
        updateKeys();
    }


    public void initView() {
        edit_goods_name = (EditText) rootView.findViewById(R.id.edit_goods);
        edit_start_price = (EditText) rootView.findViewById(R.id.edit_start_price);
        edit_end_price = (EditText) rootView.findViewById(R.id.edit_end_price);
        edit_monitor_interval = (EditText) rootView.findViewById(R.id.edit_monitor_interval);
        btn_add_key = (Button) rootView.findViewById(R.id.btn_add_key);
        btn_add_key.setOnClickListener(clickListener1);

        btn_update_keys = (ImageButton) rootView.findViewById(R.id.btn_update_keys);
        btn_update_keys.setOnClickListener(clickListener2);

        list_keys = (ListView) rootView.findViewById(R.id.list_keys);

        adapter = new MyAdapter<KeyItem>((ArrayList<KeyItem>) keyItemList, R.layout.key_item_layout) {
            @Override
            public void bindView(ViewHolder holder, KeyItem obj) {
                holder.setText(R.id.key_session_id, obj.getSessionId());
                holder.setText(R.id.key_monitor_interval, obj.getMonitorInterval());
                holder.setText(R.id.key_goods_name, obj.getGoods());
                holder.setText(R.id.key_start_price, obj.getStartPrice());
                holder.setText(R.id.key_end_price, obj.getEndPrice());
            }
        };

        list_keys.setAdapter(adapter);
        list_keys.setOnItemLongClickListener(longClickListener);

    }

    ListView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            TextView sessionIdView = (TextView) view.findViewById(R.id.key_session_id);
            nameView = (TextView) view.findViewById(R.id.key_goods_name);
            startView = (TextView) view.findViewById(R.id.key_start_price);
            endView = (TextView) view.findViewById(R.id.key_end_price);
            final String sessionId = sessionIdView.getText().toString();
            final String[] dialogItems = {"删除此key","用浏览器打开url","返回"};
            final AlertDialog alert;
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            alert = builder
                    .setTitle("请选择此项的操作")
                    .setItems(dialogItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (dialogItems[which]) {
                                case "删除此key":
                                    String urlString = Utils.createDeleteKeyURLString(sessionId);
                                    new HttpHandleThread("delete", sessionId, urlString, myHandler).start();
                                    Log.e(tag, "发送了删除命令");
                                    break;
                                case "用浏览器打开url":
                                    Uri uri = Uri.parse(Utils.createLocalURLString(nameView.getText().toString(),
                                            startView.getText().toString(), endView.getText().toString()));
                                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(i);
                                    break;
                                case "返回":
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    }).create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
            return true;
        }
    };


    View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (verifyInput()) {
                String sessionId = Utils.createSessionId();
                tempKeyItem.setSessionId(sessionId);
                tempKeyItem.setMonitorInterval(edit_monitor_interval.getText().toString());
                tempKeyItem.setGoods(edit_goods_name.getText().toString());
                tempKeyItem.setStartPrice(edit_start_price.getText().toString());
                tempKeyItem.setEndPrice(edit_end_price.getText().toString());
                String urlString = Utils.createAddKeyURLString(tempKeyItem);
                new HttpHandleThread("add", sessionId, urlString, myHandler).start();
            } else {
                Toast.makeText(mContext, "Please input valid content!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener clickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateKeys();
        }
    };

//TODO:加入输入验证
    public Boolean verifyInput() {

        String textGoods = edit_goods_name.getText().toString();
        String textStart = edit_start_price.getText().toString();
        String textEnd = edit_end_price.getText().toString();
        String textInterval = edit_monitor_interval.getText().toString();

        String regPrice = "\\d+";//匹配1个以上的数字
        String regName = "\\S+?"; //匹配任意1个以上的字符串
        String regInterval = "\\d{4,7}"; //匹配四位数到七位数

        Boolean boolStart = Pattern.matches(regPrice,textStart);
        Boolean boolEnd = Pattern.matches(regPrice,textEnd);
        Boolean boolName = Pattern.matches(regName,textGoods);
        Boolean boolInterval = Pattern.matches(regInterval,textInterval);

        return boolStart && boolEnd && boolName && boolInterval;
    }


    public void updateKeys() {
        new HttpHandleThread("get_list","",Utils.createGetListURLString(),myHandler).start();
    }


    public Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_KEY_OK:
                    KeyItem keyItem = new KeyItem();
                    keyItem.setSessionId(tempKeyItem.getSessionId());
                    keyItem.setMonitorInterval(tempKeyItem.getMonitorInterval());
                    keyItem.setGoods(tempKeyItem.getGoods());
                    keyItem.setStartPrice(tempKeyItem.getStartPrice());
                    keyItem.setEndPrice(tempKeyItem.getEndPrice());

                    GoodsItem goodsItem = null;
                    SessionItem sessionItem = new SessionItem();
                    sessionItem.setSessionId(keyItem.getSessionId());
                    sessionItem.setKeyItem(keyItem);
                    sessionItem.setGoodsItem(goodsItem);
                    BaseApplication.sessionList.add(sessionItem);

                    updateKeys();
//                    Toast.makeText(mContext, "ADD_KEY_OK", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_KEY_OK:
                    updateKeys();
                    break;
                case MAX_SESSION_OVERFLOW:
                    Toast.makeText(mContext, "MAX_SESSION_OVERFLOW", Toast.LENGTH_SHORT).show();
                    break;
                case MISS_VALUE:
                    Toast.makeText(mContext, "MISS_VALUE", Toast.LENGTH_SHORT).show();
                    break;
                case GET_LIST_OK:
                    keyItemList.clear();
                    keyItemList.addAll(BaseApplication.getKeyItemList());
                    adapter.notifyDataSetChanged();
//                    Toast.makeText(mContext,"GET_LIST_OK",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
