package club.fromfactory.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import club.fromfactory.fresco.build.FrescoImageloadBuilder;
import club.fromfactory.fresco.listener.LoadFrescoListener;

/**
 * Created by lxm on 2017/7/4.
 */

public class FrescoUtils {

    private static FrescoUtils mFrescoUtils;

    private FrescoUtils() {
    }

    public static FrescoUtils getInstance() {

        if (mFrescoUtils == null) {
            synchronized (FrescoUtils.class) {
                if (mFrescoUtils == null) {
                    mFrescoUtils = new FrescoUtils();
                }
            }
        }
        return mFrescoUtils;
    }

    public FrescoImageloadBuilder getFrescoImageLoad() {
        return new FrescoImageloadBuilder();
    }


    /**
     * 返回bitmap,也可以用来监听下载，bitmap会被fresco自动回收
     *
     * @param context   上下文
     * @param url       网络地址
     * @param width     宽度
     * @param height    高度
     * @param processor 处理图片
     * @param listener  回调
     */
    public void getFrescoImgProcessor(Context context, final String url, final int width, final int height,
                                             BasePostprocessor processor, final LoadFrescoListener listener) {

        ResizeOptions resizeOptions = null;
        if (width != 0 && height != 0) {
            resizeOptions = new ResizeOptions(width, height);
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(false)
                .setPostprocessor(processor)
                .setResizeOptions(resizeOptions)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                //图片不能是GIF
                listener.onSuccess(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                listener.onFail();
            }
        }, CallerThreadExecutor.getInstance());

    }


    /**
     * 图片是否已经存在了
     */
    public boolean isCached(Context context, Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<Boolean> dataSource = imagePipeline.isInDiskCache(uri);
        if (dataSource == null) {
            return false;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context);
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainFileCache().getResource(cacheKey);
        return resource != null && dataSource.getResult() != null && dataSource.getResult();
    }

    /**
     * 本地缓存文件
     */
    public File getCache(Context context, Uri uri) {
        if (!isCached(context, uri))
            return null;
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context);
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainFileCache().getResource(cacheKey);
        File file = ((FileBinaryResource) resource).getFile();
        return file;
    }



}
