package com.example.alice.theapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OneFragment extends Fragment {

    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    int[] image_expense = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher, R.mipmap.ic_launcher_round}; //存储图片

    public OneFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one, container, false);

        //写死的数据，用于测试
        String[] expense_category = new String[]{"商品1", "商品2","卖家","买家","商品1", "商品2","卖家","买家","商品1", "商品2","卖家","买家","商品1", "商品2","卖家","买家"};
        String[] expense_money = new String[]{"30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00","30000.00", "1500.00"};
        for (int i = 0; i < image_expense.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image_expense", image_expense[i]);
            map.put("expense_category", expense_category[i]);
            map.put("expense_money", expense_money[i]);
            listitem.add(map);
        }
        //getData(); //query data from a database

        SimpleAdapter adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.fragment_one_item
                , new String[]{"expense_category", "expense_money", "image_expense"}
                , new int[]{R.id.tv_expense_category, R.id.tv_expense_money, R.id.image_expense});
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）

        ListView listView = (ListView) v.findViewById(R.id.lv_expense);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                //在点击某笔明细的时候，Tip出明细内容
                Toast.makeText(getActivity(), map.get("expense_category").toString(), Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }
}