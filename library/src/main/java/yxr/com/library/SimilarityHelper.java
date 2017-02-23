package yxr.com.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;


/**
 * Created by Lrin on 2017/2/9.
 */

public class SimilarityHelper {
    /**图片匹配开始*/
    private static final int ON_SIMILARITY_START = 0;
    /**图片匹配出错*/
    private static final int ON_SIMILARITY_ERROR = 1;
    /**图片匹配成功*/
    private static final int ON_SIMILARITY_SUCCESS = 2;
    /**当前状态*/
    private static int mStatus = 3;
    private static SimilarityHelper helper;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mStatus = msg.what;
            switch (msg.what){
                case ON_SIMILARITY_START:
                    if(callBack != null)
                        callBack.onSimilarityStart();
                    break;
                case ON_SIMILARITY_ERROR:
                    if(callBack != null)
                        callBack.onSimilarityError((String) msg.obj);
                    break;
                case ON_SIMILARITY_SUCCESS:
                    if(callBack != null)
                        callBack.onSimilaritySuccess(msg.arg1,msg.arg2);
                    break;
            }
        }
    };
    private SimilarityCallBack callBack;

    private SimilarityHelper(){
    }
    public static SimilarityHelper instance(){
        if(helper == null)
            helper = new SimilarityHelper();
        return helper;
    }

    /**
     * 识别两张图片的匹配度
     * @param res1 : 图片1资源
     * @param res2 ： 图片2资源
     * @param callBack : 图片识别回调
     * @param context : 上下文
     */
    public void similarity (final int res1 , final int res2 , final SimilarityCallBack callBack, final Context context) {
        this.callBack = callBack;
        startMsg();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //将资源文件转换成Bitmap
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                opt.inSampleSize = 10;
                Bitmap bm1 = BitmapFactory.decodeResource(context.getResources(), res1,opt);
                Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), res2,opt);
                similarity(bm1,bm2,callBack);
            }
        }).start();
    }

    /**
     * 识别两张图片的匹配度
     * @param bm1 : 图片1
     * @param bm2 ： 图片2
     * @param callBack : 图片识别回调接口
     */
    public void similarity (final Bitmap bm1 , final Bitmap bm2 , final SimilarityCallBack callBack) {
        this.callBack = callBack;
        startMsg();
        //识别过程比较耗时，放在子线程处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                toSimilarity(bm1,bm2);
            }
        }).start();
    }

    /**
     * 图片开始匹配
     * @param bm1 : 图片1
     * @param bm2 ： 图片2
     */
    private void toSimilarity(Bitmap bm1, Bitmap bm2) {
        if(bm1 == null){
            errorMsg("图片1不能为空!");
            return;
        }
        if(bm2 == null){
            errorMsg("图片2不能为空!");
            return;
        }

        int width1 = bm1.getWidth();
        int height1 = bm1.getHeight();
        if(width1 <= 0 || height1 <= 0){
            errorMsg("图片1无效!");
            return;
        }

        float s1 = 300f / width1;
        float s2 = 300f / height1;

        // 当图片的长或宽大于300像素时压缩图片
        // 1.有效提高识别速率 : 因为要逐一比较每个像素点的信息,如果是大图
        // 进行比较,可能会有几百万甚至几千万的像素点,这样比较起来速率将会非常耗时
        // 2.防止内存溢出 : 过大的图片容易造成内存溢出，导致程序异常
        // 3.这里取s1和s2中较小的一个值用于缩放，如果取大的一个，像长图这样的图可能还是会存在巨大的像素
        if(s1 < 1 || s2 < 1){
            float scale1 = Math.min(s1,s2);

            //把图片1缩小或放大
            Matrix matrix1 = new Matrix();
            matrix1.setScale(scale1, scale1);
            bm1 = Bitmap.createBitmap(bm1, 0, 0, width1
                    , height1, matrix1, true);

            width1 = bm1.getWidth();
            height1 = bm1.getHeight();
            if(width1 <= 0 || height1 <= 0){
                errorMsg("图片1无效!");
                return;
            }
        }

        int width2 = bm2.getWidth();
        int height2 = bm2.getHeight();
        if(width2 <= 0 || height2 <= 0){
            errorMsg("图片2无效!");
            return;
        }

        // 把图片缩放成和 bm1一样的大小
        // 因为足一比较每个像素的信息，所以两个图片保持相同的像素点比较合理
        Matrix matrix2 = new Matrix();
        matrix2.setScale(width1 / (float)width2, height1 / (float)height2);
        bm2 = Bitmap.createBitmap(bm2, 0, 0, width2
                , height2, matrix2, true);

        width2 = bm2.getWidth();
        height2 = bm2.getHeight();
        if(width2 <= 0 || height2 <= 0){
            errorMsg("图片2无效!");
            return;
        }
        // 获取图1的所有像素点信息
        int [] pixels1 = new int[width1 * height1];
        bm1.getPixels(pixels1,0,width1,0,0,width1,height1);

        // 获取图2的所有像素点信息
        int [] pixels2 = new int[width2 * height2];
        bm2.getPixels(pixels2,0,width2,0,0,width2,height2);

        // 虽然前面一步已经将图2缩放成图1大小
        // 为了确保程序安全这里还是取两个图片像素中较少的一个作为基准防止越界
        int size = Math.min(pixels1.length,pixels2.length);
        // 相似像素点个数
        int similarity = 0;
        // 不相似像素点个数
        int different = 0;

        // 遍历每一个像素点并获取该像素点的灰度
        // 比较两个灰度看是否在一定容差范围内，如果在则认为相似，否则认为不相似
        // 这里比较投机，有需要可以研究分布概率在判断相似度
        // 这里的容差并非是一个标准数据，是本人实验得出来比较合理的容差范围，仅仅代表个人观点
        for(int i = 0 ; i < size ; i++){
            int ya1 = rgbToGray(pixels1[i]);
            int ya2 = rgbToGray(pixels2[i]);
            if(Math.abs(ya1 - ya2) <= 20){
                similarity++;
            }else{
                different++;
            }
        }
        successMsg(similarity,different);

        // 内存回收
        bm1.recycle();
        bm2.recycle();
        System.gc();
        bm1 = null;
        bm2 = null;
    }

    /**
     * 图片识别成功
     * @param similarity : 相似像素个数
     * @param different : 不相似像素个数
     */
    private void successMsg(int similarity, int different) {
        Message msg = new Message();
        msg.what = ON_SIMILARITY_SUCCESS;
        msg.arg1 = similarity;
        msg.arg2 = different;
        handler.sendMessage(msg);
    }

    /**
     * 开始图片识别
     */
    private void startMsg() {
        if(mStatus != ON_SIMILARITY_START)
            handler.sendEmptyMessage(ON_SIMILARITY_START);
    }

    /**
     * 图片识别出错
     * @param reason : 出错原因
     */
    private void errorMsg(String reason) {
        Message msg = new Message();
        msg.what = ON_SIMILARITY_ERROR;
        msg.obj = reason;
        handler.sendMessage(msg);
    }

    /**
     * 灰度值计算
     * @param pixels 像素
     * @return int 灰度值
     */
    public static int rgbToGray(int pixels) {
        return (int) (0.3 * Color.red(pixels) + 0.59 * Color.green(pixels) + 0.11 * Color.blue(pixels));
    }
}
