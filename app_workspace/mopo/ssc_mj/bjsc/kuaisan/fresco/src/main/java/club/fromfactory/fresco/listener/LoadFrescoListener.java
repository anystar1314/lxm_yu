package club.fromfactory.fresco.listener;

import android.graphics.Bitmap;

public interface LoadFrescoListener {
    void onSuccess(Bitmap bitmap);

    void onFail();
}