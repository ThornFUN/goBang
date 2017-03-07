package jinpeng.test.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 2017/3/1.
 */
public class GoBangPanel extends View {

    private String TAG = "test";
    private int mPanelWidth;//棋盘宽度
    private float mLineHeight;//每行行高
    private int MAX_LINE = 10;//十个格子
    private Paint mPaint = new Paint();//new一个paint

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float mLineHeightPiece = 3*1.0f/4;

    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner = false;

    private int MAX_COUNT_IN_LINE = 5;

    public GoBangPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        initPaint();
    }

    private void initPaint() {//新建画笔
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    /*
    绘制棋盘区域大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        //为了防止出现UNSPECIFIED的情况出现，做如下判断
        if(widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width,width);//取小的一边，画一个矩形
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //对尺寸相关的变量进行赋值
        mPanelWidth = w;
        mLineHeight = mPanelWidth*1.0f/MAX_LINE;

        int mPieceWidth = (int) (mLineHeightPiece * mLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,mPieceWidth,mPieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,mPieceWidth,mPieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsGameOver) return false;//游戏结束，不再落子

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG,"点击了33");
            //表明点击我们的View时，将点击事件交由我们处理
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y);
            Log.i(TAG,"点击"+p.x+"///"+p.y);
            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }
            if(mIsWhite){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }

            invalidate();//请求重绘图形
            mIsWhite = !mIsWhite;
            Log.i(TAG,"点击"+mIsWhite);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }



    /*
    绘制棋盘线条，需要用到paint
     */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
       boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if(whiteWin||blackWin){
            mIsGameOver = true;
            if(whiteWin) mIsWhiteWinner = true;
            String text = whiteWin?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    public void reStartGame(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    private boolean checkFiveInLine(List<Point> points) {
        for(Point p:points){
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x,y,points)||
                    checkVertical(x,y,points)||
                    checkLeftDiagonal(x,y,points)||
                    checkRightDigonal(x,y,points);
            if(win) return true;

        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //判断左边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        //判断右边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //判断上边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        //判断下边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //判断上边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        //判断下边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private boolean checkRightDigonal(int x, int y, List<Point> points) {
        int count = 1;
        //判断上边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        //判断下边是否达到五个
        for(int i = 1 ;i < MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }

        if(count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for(int i = 0,n = mWhiteArray.size();i < n; i++){//画白子
            Point whitePoint = mWhiteArray.get(i);
            Log.i(TAG,"whitePoint"+whitePoint.x+".."+whitePoint.y);
            canvas.drawBitmap(mWhitePiece,
                    ((whitePoint.x+(1 - mLineHeightPiece)/2)*mLineHeight),
                    ((whitePoint.y+(1 - mLineHeightPiece)/2)*mLineHeight),null);
        }
        for(int i = 0,n = mBlackArray.size();i < n; i++){//画黑子
            Point blackPoint = mBlackArray.get(i);
            Log.i(TAG,"blackPoint"+blackPoint.x+".."+blackPoint.y);
            canvas.drawBitmap(mBlackPiece,
                    ((blackPoint.x+(1 - mLineHeightPiece)/2)*mLineHeight),
                    ((blackPoint.y+(1 - mLineHeightPiece)/2)*mLineHeight),null);
        }

    }

    private void drawBoard(Canvas canvas) {
        //成员变量用局部变量替代
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for(int i=0;i<MAX_LINE;i++){
            int startX = (int) (lineHeight/2);
            int endX = (int) (w - lineHeight/2);
            int y = (int) ((0.5 + i)*lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle)state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            //取出系统默认
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
