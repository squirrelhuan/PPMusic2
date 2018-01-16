package com.example.ppmusic.adapter;

import com.example.ppmusic.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by CGQ on 2016/8/18.
 */
public class EditView_withDeleteButton extends RelativeLayout{
    public EditView_withDeleteButton(Context context) {
        super(context);
        initView(context,null);
    }

    public EditView_withDeleteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public EditView_withDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }
    
	public EditText editView;
    ImageView imageView;
    Button button;
    public void initView(Context context ,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditView_withDeleteButton);//TypedArray是一个数组容器
        String text = a.getString(R.styleable.EditView_withDeleteButton_text);//防止在XML文件里没有定义，就加上了默认值30
        String hint = a.getString(R.styleable.EditView_withDeleteButton_hint);//同上,这里的属性是:名字_属性名
        int inputType =  a.getInteger(R.styleable.EditView_withDeleteButton_inputType,0);
        editView = new EditText(context);

        editView.setBackgroundColor(getResources().getColor(R.color.transparent));
        //editView.setTextSize(16);
        editView.setHint(hint);
        editView.setInputType(inputType);
        editView.setInputType(InputType.TYPE_CLASS_TEXT);
        //this.setBackgroundColor(getResources().getColor(R.color.transparent));
        LayoutParams layoutParams0 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams0.addRule(RelativeLayout.CENTER_VERTICAL);
        //layoutParams0.gravity = Gravity.CENTER_VERTICAL;
        this.setLayoutParams(layoutParams0);
        //this.setGravity(Gravity.CENTER_VERTICAL);

        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.gom);
        LayoutParams layoutParams2 = new LayoutParams((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,40),(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 40));
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParams2.setMargins(0,0,(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 5),0);
        imageView.setPadding((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10),(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10),(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10),(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10));
        imageView.setLayoutParams(layoutParams2);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editView.setText("");
            }
        });
        imageView.setVisibility(View.GONE);
        this.addView(imageView);
        
       // button = new Button(context);
        

        editView.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 40));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
       // layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.setMargins((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15),0,(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 38),0);

        //layoutParams.addRule(RelativeLayout.LEFT_OF, imageView.getId());
        editView.setPadding(0,(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1),0,0);
        editView.setLayoutParams(layoutParams);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>=1){
                    imageView.setVisibility(VISIBLE);
                }else{
                    imageView.setVisibility(GONE);
                }
            }
        });
        editView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(((EditText)v).getText().length()>=1){
                        imageView.setVisibility(VISIBLE);
                    }else{
                        imageView.setVisibility(GONE);
                    }

                }else {
                        imageView.setVisibility(GONE);
                }
            }
        });
        this.addView(editView);



    }

    // 方法一
    public float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }
    public String getText(){
       return editView.getText().toString();
    }
}
