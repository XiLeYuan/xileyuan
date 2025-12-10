package com.xly.middlelibrary.widget;

/*
public class LYAnimButton extends FrameLayout {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.unread_num)
    public TextView unreadNumTv;

    @BindView(R.id.unread_num_more)
    public TextView unreadNumMore;

    @BindView(R.id.unReadDot)
    AppCompatImageView unReadDot;



    @BindView(R.id.image_layout)
    RelativeLayout imageLayout;

    int[] imageResArray, textColorResArray;
    boolean check;
    private ScaleAnimation imageScaleAnimation1, imageScaleAnimation2, imageScaleAnimation3, imageScaleAnimation4;
    private Context context;
    private long downTime = 0;


    public LYAnimButton(Context context) {
        super(context);
        initView(context);
    }

    public LYAnimButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LYAnimButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setUnreadNumAndVisible(long num) {
        if (num > 0) {
            if (num > 99) {
                unreadNumTv.setVisibility(GONE);
                unreadNumMore.setVisibility(VISIBLE);
                if (num >= 999) {
                    unreadNumMore.setText("999+");
                } else {
                    unreadNumMore.setText(num + "");
                }
            } else {
                unreadNumTv.setVisibility(VISIBLE);
                unreadNumMore.setVisibility(GONE);
                unreadNumTv.setText(num + "");
            }
        } else {
            unreadNumTv.setVisibility(GONE);
            unreadNumMore.setVisibility(GONE);
        }

    }

    private void initView(Context context) {
        this.context = context;
        View v = LayoutInflater.from(context).inflate(R.layout.anim_button, this, true);
        ButterKnife.bind(this, v);
        initAnim();
    }

    public void clearImageColorFilter() {
        image.clearColorFilter();
    }

    public void setImageColorFilter(int color) {
        image.setColorFilter(color);
    }

    public void setImageRes(int[] resArray) {
        imageResArray = resArray;
        if (check) {
            image.setImageResource(imageResArray[1]);
        } else {
            image.setImageResource(imageResArray[0]);
        }
    }

    public void setTextColorRes(int[] resArray) {
        textColorResArray = resArray;
        if (check) {
            text.setTextColor(textColorResArray[1]);
        } else {
            text.setTextColor(textColorResArray[0]);
        }
    }

    public void setText(String title) {
        text.setText(title);
    }

    public void setTextSize(int size) {
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
        if (check) {
            if (imageResArray != null) {
                image.setImageResource(imageResArray[1]);
            }
            if (textColorResArray != null)
                text.setTextColor(textColorResArray[1]);
        } else {
            if (imageResArray != null) {
                image.setImageResource(imageResArray[0]);
            }
            if (textColorResArray != null)
                text.setTextColor(textColorResArray[0]);
        }

    }


    Handler handler = new Handler(Looper.getMainLooper());

    */
/***
     * 点击后的动画效果
     *//*

    public void startClickAnim() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                playBounceAnimation(LYAnimButton.this);
            }
        });
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                long thisTime = System.currentTimeMillis();
                if (thisTime - downTime < 300L && doubleClickListener != null) {
                    doubleClickListener.onDoubleClick();
                }
                downTime = thisTime;
                break;
            case MotionEvent.ACTION_UP:
                if (check) {
                    if (imageResArray != null)
                        image.setImageResource(imageResArray[1]);
                    if (textColorResArray != null)
                        text.setTextColor(textColorResArray[1]);
                } else {
                    if (imageResArray != null)
                        image.setImageResource(imageResArray[0]);
                    if (textColorResArray != null)
                        text.setTextColor(textColorResArray[0]);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initAnim() {

        imageScaleAnimation1 = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        imageScaleAnimation1.setDuration(50);

        imageScaleAnimation2 = new ScaleAnimation(0.6f, 1.1f, 0.6f, 1.1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        imageScaleAnimation2.setDuration(50);

        imageScaleAnimation3 = new ScaleAnimation(1.1f, 0.9f, 1.1f, 0.9f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        imageScaleAnimation3.setDuration(50);

        imageScaleAnimation4 = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        imageScaleAnimation4.setDuration(50);
    }


    AnimatorSet animatorSet;
    AnimatorSet shrinkAnim;
    AnimatorSet expandAnim;
    AnimatorSet resetAnim;

    private void initBounceAnimation(View view) {
        // 创建动画集合
        animatorSet = new AnimatorSet();

        // 第一阶段：缩小到 0.8 倍（300ms）
        ObjectAnimator shrinkX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.6f);
        ObjectAnimator shrinkY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.6f);
        shrinkAnim = new AnimatorSet();
        shrinkAnim.setDuration(200);
        shrinkAnim.playTogether(shrinkX, shrinkY);
        shrinkAnim.setInterpolator(new AccelerateInterpolator()); // 加速效果

        // 第二阶段：放大到 1.2 倍（200ms）
        ObjectAnimator expandX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.1f);
        ObjectAnimator expandY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.1f);
        expandAnim = new AnimatorSet();
        expandAnim.setDuration(120);
        expandAnim.playTogether(expandX, expandY);
        expandAnim.setInterpolator(new OvershootInterpolator(2.0f)); // 弹性效果

        // 第三阶段：恢复原始大小（150ms）
        ObjectAnimator resetX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f);
        ObjectAnimator resetY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f);
        resetAnim = new AnimatorSet();
        resetAnim.setDuration(120);
        resetAnim.playTogether(resetX, resetY);
        resetAnim.setInterpolator(new DecelerateInterpolator()); // 减速效果
        // 组合动画
        */
/*animatorSet.playSequentially(shrinkAnim, expandAnim, resetAnim);
        animatorSet.start();*//*

    }


    private void playBounceAnimation(View view) {
        initBounceAnimation(view);
        animatorSet.playSequentially(shrinkAnim, expandAnim, resetAnim);
        animatorSet.start();
    }


    public void setUnReadDotStatus(boolean isShow) {
        if (isShow) {
            unReadDot.setVisibility(View.VISIBLE);
        } else {
            unReadDot.setVisibility(View.GONE);
        }
    }

}
*/
