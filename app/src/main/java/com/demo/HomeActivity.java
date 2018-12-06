package com.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.demo.blur.BlurActivity;
import com.demo.clip.ClipActivity;
import com.demo.fullScreen.FullScreenActivity;
import com.demo.ghost.GhostEyesActivity;
import com.demo.notification.NotificationSenderActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HomeAdapter adapter = new HomeAdapter();

        doInitData(adapter);

        ListView ls  = (ListView) findViewById(R.id.list);
        ls.setAdapter(adapter);
    }

    private void doInitData(HomeAdapter adapter){

        ActionBean[] locActionBeans = {
                new ActionBean("通知",this,NotificationSenderActivity.class),
                /*new ActionBean("高斯模糊", this ,BlurActivity.class),
                new ActionBean("状态栏颜色",this,FullScreenActivity.class),
                new ActionBean("Clip Demo", this,ClipActivity.class),*/
                new ActionBean("GhostEyes", this,GhostEyesActivity.class)
        };

        for (ActionBean ab:locActionBeans)
            adapter.mDataList.add(ab);
    }

    static class ActionBean {
        String title;
        Runnable action;

        public ActionBean(String title, final Context context , final Class<? extends Activity> cls) {
            this.title = title;
            action = new Runnable() {
                @Override
                public void run() {
                    context.startActivity(new Intent(context,cls));
                }
            };
        }
    }

    static class ViewHolder {
        TextView rootView;

        ViewHolder(Context context){
            rootView = (TextView) LayoutInflater.from(context).inflate(R.layout.home_list_item_layout,null);
        }

        View bindData(final ActionBean actionBean){
            rootView.setText(actionBean.title);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionBean.action.run();
                }
            });
            return rootView;
        }
    }

    private static class HomeAdapter extends BaseAdapter{

        private List<ActionBean> mDataList = new ArrayList<>();

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public ActionBean getItem(int i) {
            return mDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = view != null ? (ViewHolder) view.getTag() : null;
            if (viewHolder == null){
                viewHolder = new ViewHolder(viewGroup.getContext());
            }
            View ret = viewHolder.bindData(getItem(i));
            return ret;
        }
    }
}
