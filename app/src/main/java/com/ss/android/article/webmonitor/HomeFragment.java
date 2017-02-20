package com.ss.android.article.webmonitor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.ss.android.article.webmonitor.BaseApplication.GET_HTTP_DATA_FAIL;
import static com.ss.android.article.webmonitor.BaseApplication.GET_LIST_OK;

/**
 * Created by poxiaoge on 2017/1/18.
 */

public class HomeFragment extends Fragment {


    View rootView;
    Context mContext;

    ImageButton btn_update_goods;
    ListView list_goods;
    MyAdapter<GoodsItem> adapter;
    List<GoodsItem> goodsItemList;

    TextView linkView;
    TextView titleView;
    TextView sellerView;

    ClipboardManager cm;


    private final String tag = this.getClass().getSimpleName();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
    public void onResume() {
        super.onResume();
        Log.e(tag, "onStart");
        updateData();
    }

    public void initView() {
        btn_update_goods = (ImageButton) rootView.findViewById(R.id.btn_update_goods);
        list_goods = (ListView) rootView.findViewById(R.id.list_goods);
        goodsItemList = BaseApplication.getGoodsItemList();

        adapter = new MyAdapter<GoodsItem>((ArrayList<GoodsItem>) goodsItemList,R.layout.goods_item_layout) {
            @Override
            public void bindView(ViewHolder holder, GoodsItem obj) {
                holder.setText(R.id.goods_title, obj.getTitle());
                holder.setText(R.id.goods_price, obj.getPrice());
                holder.setText(R.id.goods_link, obj.getLink());
                holder.setText(R.id.goods_desc, obj.getDesc());
                holder.setText(R.id.goods_seller, obj.getSeller());
            }
        };

        list_goods.setAdapter(adapter);
        list_goods.setOnItemClickListener(itemClickListener);

        btn_update_goods.setOnClickListener(clickListener1);

    }

    View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateData();
        }
    };

    ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            linkView = (TextView) view.findViewById(R.id.goods_link);
            titleView = (TextView) view.findViewById(R.id.goods_title);
            sellerView = (TextView) view.findViewById(R.id.goods_seller);
            final String goodsLink = linkView.getText().toString();
            final String goodsTitle = titleView.getText().toString();
            final String goodsSeller = sellerView.getText().toString();
            final String[] dialogItems = {"用浏览器打开链接","复制链接到剪贴板","复制商品Title并打开闲鱼","复制商品Seller并打开闲鱼","返回"};
            final AlertDialog alert;
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            alert = builder
                    .setTitle("请选择此项的操作")
                    .setItems(dialogItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (dialogItems[which]) {
                                case "用浏览器打开链接":
                                    Log.e(tag, "打开链接:"+goodsLink);
                                    Uri uri = Uri.parse(goodsLink);
                                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(i);
                                    break;
                                case "复制链接到剪贴板":
                                    cm.setPrimaryClip(ClipData.newPlainText("urilink",goodsLink));
                                    Toast.makeText(mContext,"复制链接成功",Toast.LENGTH_SHORT).show();
                                    break;
                                case "复制商品Title并打开闲鱼":
                                    cm.setPrimaryClip(ClipData.newPlainText("goodstitle",goodsTitle));
                                    openXianyu();
                                    Toast.makeText(mContext,"复制商品Title成功",Toast.LENGTH_SHORT).show();
                                    break;
                                case "复制商品Seller并打开闲鱼":
                                    cm.setPrimaryClip(ClipData.newPlainText("goodsseller","@"+goodsSeller));
                                    openXianyu();
                                    Toast.makeText(mContext,"复制商品Seller成功",Toast.LENGTH_SHORT).show();
                                    break;
                                case "返回":
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    }).create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        }
    };

    public void openXianyu() {
        Intent intent3 = new Intent();
        intent3.setAction("android.intent.action.idlefish");
        intent3.addCategory("android.intent.category.DEFAULT");
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String packageName = "com.taobao.idlefish";
        String className = "com.taobao.fleamarket.activity.MainActivity";
        ComponentName cn = new ComponentName(packageName,className);
        intent3.setComponent(cn);
        startActivity(intent3);
    }




    public void updateData() {
        new HttpHandleThread("get_list", "", Utils.createGetListURLString(),myHandler).start();
    }


    public Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_LIST_OK:
                    goodsItemList.clear();
                    goodsItemList.addAll(BaseApplication.getGoodsItemList());
                    adapter.notifyDataSetChanged();
//                    Toast.makeText(mContext,"GET_LIST_OK",Toast.LENGTH_SHORT).show();
                    break;
                case GET_HTTP_DATA_FAIL:
                    Toast.makeText(mContext,"GET_HTTP_DATA_FAIL",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };





}
