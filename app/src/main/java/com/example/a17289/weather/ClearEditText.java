//package com.example.a17289.weather;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.EditText;
//
//import java.nio.charset.Charset;
//
///**
// * Created by 17289 on 2019/7/5.
// */
//
//public class ClearEditText implements View.OnFocusChangeListener, TextWatcher{
//    // 删除按钮的引用
//    private Drawable mClearDrawable;
//    public ClearEditText(Context context) {
//        this(context, null);
//    }
//    public ClearEditText(Context context, AttributeSet atts, int defStyle) {
//        // 这个构造函数很重要, 不加这个很多属性不能再XML里面定义
//        this(context, attrs, android.R.attr.editTextStyle);
//    }
//    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//    private void init() {
//        mClearDrawable = getCompoundDrawables()[2];
//        if(mClearDrawable == null) {
//            mClearDrawable = getResources().getDrawable(R.drawable.omotionstore_progresscancelbtn);
//        }
//        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
//        setClearIconVisible(false);
//        setOnFocusChangeListener(this);
//        addTextChangeListener(this);
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(getCompoundDrawables()[2] != null) {
//            if(event.getAction() == MotionEvent.ACTION_UP) {
//                boolean touchable = event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) && (event.getX() < ((getWidth() - getPaddingRight())));
//                if(touchable) {
//                    this.setText("");
//                }
//            }
//        }
//        return super.onTouchEvent(event);
//    }
//    // 当ClearEditText焦点发生变化的时候, 判断里面的字符串长度设置清除图标的显示与隐藏
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if(hasFocus){
//            setClearIconVisiable(getText().length() > 0);
//        }else {
//            setClearIconVisible(false);
//        }
//    }
//
//    // 设置图标的显示和隐藏，调用setCompoundDrawables为EditText绘制上去
//    protected void setClearIconVisible(boolean visible) {
//        Drawable right = visible ? mClearDrawable : null;
//        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
//    }
//
//    //当输入框里的内容发送变化的时候回调的方法
//    @Override
//    public void onTextChanged(CharSequence s, int start, int count, int after) {
//        setClearIconVisible(s.length() > 0);
//    }
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s ){
//
//    }
//
//    //设置晃动动画
//    public void setShakeAnimation() {
//        this.setAnimation(shakeAnimation(5));
//    }
//}
//
