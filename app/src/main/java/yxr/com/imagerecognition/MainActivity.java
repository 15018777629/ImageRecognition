package yxr.com.imagerecognition;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import yxr.com.library.SimilarityCallBack;
import yxr.com.library.SimilarityHelper;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg1;
    private RadioGroup rg2;
    private TextView tv1;
    private TextView tv2;
    private TextView tvContent;
    private Button btn;
    private int testImage1 = R.drawable.image1;
    private int testImage2 = R.drawable.image1;
    private SimilarityHelper similarityHelper;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }
    private void initView() {
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rg2 = (RadioGroup) findViewById(R.id.rg2);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tvContent = (TextView) findViewById(R.id.tvContent);
        btn = (Button) findViewById(R.id.btn);
    }
    private void initListener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                similarityHelper.similarity(testImage1, testImage2, new SimilarityCallBack() {
                    public ProgressDialog show;

                    @Override
                    public void onSimilarityStart() {
                         show = ProgressDialog.show(MainActivity.this, "", "正在识别 . . ");
                        tvContent.setText("正在识别 . . ");
                    }

                    @Override
                    public void onSimilaritySuccess(int similarity, int different) {
                        if(show != null)
                            show.dismiss();
                        tvContent.setText("匹配度为 ： " + ((float)similarity / (similarity + different) * 100) + " %");
                    }

                    @Override
                    public void onSimilarityError(String reason) {
                        if(show != null)
                            show.dismiss();
                        tvContent.setText(reason);
                    }
                },getApplicationContext());
            }
        });
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_1:
                        RadioButton child = (RadioButton) group.getChildAt(0);
                        tv1.setText(child.getText());
                        testImage1 = R.drawable.image1;
                        break;
                    case R.id.rb1_2:
                        RadioButton child1 = (RadioButton) group.getChildAt(1);
                        tv1.setText(child1.getText());
                        testImage1 = R.drawable.image2;
                        break;
                    case R.id.rb1_3:
                        RadioButton child2 = (RadioButton) group.getChildAt(2);
                        tv1.setText(child2.getText());
                        testImage1 = R.drawable.image3;
                        break;
                }
            }
        });
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb2_1:
                        RadioButton child = (RadioButton) group.getChildAt(0);
                        tv2.setText(child.getText());
                        testImage2 = R.drawable.image1;
                        break;
                    case R.id.rb2_2:
                        RadioButton child1 = (RadioButton) group.getChildAt(1);
                        tv2.setText(child1.getText());
                        testImage2 = R.drawable.image2;
                        break;
                    case R.id.rb2_3:
                        RadioButton child2 = (RadioButton) group.getChildAt(2);
                        tv2.setText(child2.getText());
                        testImage2 = R.drawable.image3;
                        break;
                }
            }
        });
    }

    private void initData() {
        similarityHelper = SimilarityHelper.instance();

        ImageLoader imageLoader = ImageLoaderUtil.getImageLoader(this);
        DisplayImageOptions options = ImageLoaderUtil.getOptions(R.mipmap.ic_launcher, R.mipmap.ic_launcher);

        imageLoader.displayImage("drawable://" + R.drawable.image1 ,iv1,options);
        imageLoader.displayImage("drawable://" + R.drawable.image2 ,iv2,options);
        imageLoader.displayImage("drawable://" + R.drawable.image3 ,iv3,options);
    }
}
