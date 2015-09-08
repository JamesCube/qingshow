package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.U16BonusListAdapter;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.widget.LoadingDialogs;
import com.focosee.qingshow.widget.QSTextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U16BonusListActivity extends Activity {

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.u16_recycler)
    RecyclerView u16Recycler;

    private U16BonusListAdapter adapter;
    private MongoPeople people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u16_bonus_list);
        ButterKnife.inject(this);
        initUser();
        matchUI();
    }

    public void initUser(){
        final LoadingDialogs dialogs = new LoadingDialogs(U16BonusListActivity.this);
        dialogs.show();
        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                dialogs.dismiss();
                people = QSModel.INSTANCE.getUser();
                initList();
            }
        });
    }

    public void matchUI() {
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(getText(R.string.bonus_activity_settings));

    }

    public void initList(){
        LinearLayoutManager manager = new LinearLayoutManager(U16BonusListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        u16Recycler.setLayoutManager(manager);
        adapter = new U16BonusListAdapter(people.bonuses, U16BonusListActivity.this, R.layout.item_u16_bonuses_list);
        u16Recycler.setAdapter(adapter);
    }
}
