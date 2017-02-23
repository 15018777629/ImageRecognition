# ImageRecognition
这是一个图片识别项目，主要用于识别两张图片的相似度。
识别算法比较简单，就是遍历比较两张图片的所有像素点的灰度值，
然后比较这两个像素的灰度值差是否在一定的容差范围内来判断该像素点是否相似。
最后得到所有的相似像素点和不相似像素点，求出图片的相似度。
这里可能比较大图，为了防止内存异常和识别速率，会将大图压缩到一定比例后在进行比较。
其他更多详细的说明在项目的Library里有很详细的注释，有兴趣的用户可以参考一下。
声明一下，该项目不做任何依据，都是个人的见解，如果你有更好的思路或代码可以共同讨论。
# 怎么使用
使用起来十分的简洁，只需要短短的几行代码，demo里面虽然有很多乱七八糟的代码，
但是阵阵核心的只有几句，如下:
```java
SimilarityHelper similarityHelper = SimilarityHelper.instance(); 
similarityHelper.similarity(testImage1, testImage2, new SimilarityCallBack() {
                    @Override
                    public void onSimilarityStart() {
                    }
                    @Override
                    public void onSimilaritySuccess(int similarity, int different) {
                    }
                    @Override
                    public void onSimilarityError(String reason) {
                    }
                },getApplicationContext());
                
```java
# 最后贴出几张效果图
![Screenshot](https://github.com/15018777629/ImageRecognition/blob/master/screenshot/image1.png)
![Screenshot](https://github.com/15018777629/ImageRecognition/blob/master/screenshot/image2.png)
![Screenshot](https://github.com/15018777629/ImageRecognition/blob/master/screenshot/image3.png)
![Screenshot](https://github.com/15018777629/ImageRecognition/blob/master/screenshot/image4.png)
