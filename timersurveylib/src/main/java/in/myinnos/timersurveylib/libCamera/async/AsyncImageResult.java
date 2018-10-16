package in.myinnos.timersurveylib.libCamera.async;

import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import in.myinnos.timersurveylib.R;
import in.myinnos.timersurveylib.libCamera.bean.PickResult;
import in.myinnos.timersurveylib.libCamera.bundle.PickSetup;
import in.myinnos.timersurveylib.libCamera.enums.EPickType;
import in.myinnos.timersurveylib.libCamera.img.ImageHandler;
import in.myinnos.timersurveylib.libCamera.resolver.IntentResolver;

/**
 * Created by jrvansuita on 08/02/17.
 */

public class AsyncImageResult extends AsyncTask<Intent, Void, PickResult> {

    private WeakReference<IntentResolver> weakIntentResolver;
    private WeakReference<PickSetup> weakSetup;
    private OnFinish onFinish;

    public AsyncImageResult(IntentResolver intentResolver, PickSetup setup) {
        this.weakIntentResolver = new WeakReference<>(intentResolver);
        this.weakSetup = new WeakReference<>(setup);
    }

    public AsyncImageResult setOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @Override
    protected PickResult doInBackground(Intent... intents) {

        //Create a PickResult instance
        PickResult result = new PickResult();

        IntentResolver resolver = weakIntentResolver.get();

        if (resolver == null) {
            result.setError(new Error(resolver.getActivity().getString(R.string.activity_destroyed)));
            return result;
        }

        try {
            //Get the data intent from onActivityResult()
            Intent data = intents[0];

            //Define if it was pick from camera
            boolean fromCamera = resolver.fromCamera(data);

            //Instance of a helper class
            ImageHandler imageHandler = ImageHandler
                    .with(resolver.getActivity()).setup(weakSetup.get())
                    .provider(fromCamera ? EPickType.CAMERA : EPickType.GALLERY)
                    .uri(fromCamera ? resolver.cameraUri() : data.getData());

            //Setting uri and path for result
            result.setUri(imageHandler.getUri())
                    .setPath(imageHandler.getUriPath())
                    .setBitmap(imageHandler.decode());


            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(e);
            return result;
        }
    }


    @Override
    protected void onPostExecute(PickResult r) {
        if (onFinish != null)
            onFinish.onFinish(r);
    }

    public interface OnFinish {
        void onFinish(PickResult pickResult);
    }

}
