package com.ss.android.article.webmonitor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by poxiaoge on 2017/1/18.
 */

public class AboutFragment extends Fragment {


    EditText edit_query_server_interval;
    Button btn_set_interval;

    EditText edit_server_ip;
    Button btn_set_ip;

    String ip;

    View rootView;

    String intervalStr;

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        rootView = view;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
    }

    public void initView() {
        edit_query_server_interval = (EditText) rootView.findViewById(R.id.edit_query_server_interval);
        btn_set_interval = (Button) rootView.findViewById(R.id.btn_set_interval);
        btn_set_interval.setOnClickListener(clickListener1);

        edit_server_ip = (EditText) rootView.findViewById(R.id.edit_server_ip);
        btn_set_ip = (Button) rootView.findViewById(R.id.btn_set_ip);
        btn_set_ip.setOnClickListener(clickListener1);

    }

    View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_set_interval:
                    intervalStr = edit_query_server_interval.getText().toString();
                    if (verifyInput(intervalStr)) {
                        int interval = Integer.parseInt(intervalStr);
                        if (interval > 10000) {
                            BaseApplication.queryServerInterval = interval;
                        }
                        Toast.makeText(mContext, "设置查询间隔成功！",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "请设置一个合适的间隔!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_set_ip:
                    ip = edit_server_ip.getText().toString();
                    if (Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", ip)) {
                        //  "http://118.89.216.213:3000"
                        BaseApplication.urlPrefix = "http://" + ip + ":3000";
                        Toast.makeText(mContext, "设置IP成功！", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(mContext,"请输入正确的IP格式！",Toast.LENGTH_SHORT).show();
                    }

                    break;
            }







        }
    };

    public boolean verifyInput(String intervalStr) {
        String regInterval = "\\d{4,6}";
        return Pattern.matches(regInterval,intervalStr);
    }





}
