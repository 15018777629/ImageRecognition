package yxr.com.imagerecognition;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author YIN.XR
 * ImageLoaderUtil
 */
public class ImageLoaderUtil {
	private static ImageLoader imageloader;  
	private ImageLoaderUtil (){}  
	public static synchronized ImageLoader getImageLoader(Context context) {
		if (imageloader == null) {  
			imageloader = ImageLoader.getInstance();
			imageloader.init(ImageLoaderConfiguration.createDefault(context));
		}  
		return imageloader;  
	} 
	public static synchronized DisplayImageOptions getOptions(int defalut,int loading) {  
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.showImageForEmptyUri(defalut) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(defalut)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
			.cacheOnDisk(true)
			.build();
		return options;  
	} 
}
