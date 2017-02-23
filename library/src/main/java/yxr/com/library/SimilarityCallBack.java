package yxr.com.library;

/**
 * Created by Lrin on 2017/2/23.
 */

public interface SimilarityCallBack {
    /**
     * 开始图片识别
     * 可以直接进行UI操作
     */
    void onSimilarityStart();

    /**
     * 图片识别成功
     * 可以直接进行UI操作
     * @param similarity : 图片相似像素点
     * @param different : 图片不相似像素点
     */
    void onSimilaritySuccess(int similarity, int different);

    /**
     * 图片识别出错
     * 可以直接进行UI操作
     * @param reason : 出错原因
     */
    void onSimilarityError(String reason);
}
