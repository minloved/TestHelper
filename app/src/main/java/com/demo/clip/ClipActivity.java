package com.demo.clip;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.SeekBar;
import android.widget.TextView;

import com.demo.R;
public class ClipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.clip_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final View parent = findViewById(R.id.parent);
        final View child = findViewById(R.id.child);
        parent.setTranslationZ(10);
        child.setTranslationZ(10);

        final TextView pTips = (TextView) findViewById(R.id.pc_tip);
        final TextView cTips = (TextView) findViewById(R.id.cc_tip);

        SeekBar cc = (SeekBar) findViewById(R.id.cc);
        SeekBar pc = (SeekBar) findViewById(R.id.pc);


        cc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cTips.setText("Child TopClipProgress: " + i);
                clip(child,i * child.getHeight() / 100 ,child.getHeight());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pTips.setText("Parent TopClipProgress: " + i);
                clip(parent,i * parent.getHeight() / 100, parent.getHeight());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void clip(View view, final int top, final int bottom){

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
              //outline.setRect(0,top,view.getWidth(),bottom);
              outline.setRect(0,0,view.getWidth(),bottom - top);
            }
        });

//      view.setClipBounds(new Rect(0,0,view.getWidth(),bottom - top));
        Log.e("CLIP","TOP = " + top);
        Log.e("CLIP","BOTTOM = " + bottom);

        view.setClipToOutline(false);
        view.invalidateOutline();
    }
}
