package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S07ListAdapter;
import com.focosee.qingshow.entity.ShowDetailEntity;
import java.util.ArrayList;

public class S07CollectActivity extends Activity {

    public static final String INPUT_ITEMS = "S07CollectActivity_input_items";
    public static final String INPUT_BACK_IMAGE = "S07CollectActivity_input_back_image";
    public static final String INPUT_BRAND_TEXT = "S07CollectActivity_input_brand_text";

    private ListView listView;
    private S07ListAdapter adapter;

    private String brandText = null;

    private ArrayList<ShowDetailEntity.RefItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s07_collect);

        findViewById(R.id.S07_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S07CollectActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        items = (ArrayList<ShowDetailEntity.RefItem>)bundle.getSerializable(INPUT_ITEMS);
        brandText = intent.getStringExtra(INPUT_BRAND_TEXT);

        listView = (ListView) findViewById(R.id.S07_item_list);

        adapter = new S07ListAdapter(this, (null != brandText), items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(S07CollectActivity.this, S05ItemActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(S05ItemActivity.INPUT_ITEMS, items);
                intent1.putExtras(bundle1);
                startActivity(intent1);
            }
        });

//        ImageLoader.getInstance().displayImage(getIntent().getStringExtra(INPUT_BACK_IMAGE),(ImageView)findViewById(R.id.S07_background_image));
        if (null != brandText) {
            ((TextView)findViewById(R.id.S07_brand_tv)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.S07_brand_tv)).setText(brandText);
        } else {
            ((TextView)findViewById(R.id.S07_brand_tv)).setVisibility(View.GONE);
        }
    }


}
