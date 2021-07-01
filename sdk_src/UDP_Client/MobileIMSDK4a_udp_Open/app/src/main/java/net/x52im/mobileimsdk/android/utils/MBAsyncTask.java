package net.x52im.mobileimsdk.android.utils;

public abstract class MBAsyncTask {
    public void execute(final Object... params) {
        MBThreadPoolExecutor.runInBackground(() -> {
            final int code = doInBackground(params);
            MBThreadPoolExecutor.runOnMainThread(() -> {
                onPostExecute(code);
            });
        });
    }

    protected abstract Integer doInBackground(Object... params);

    protected void onPostExecute(Integer code) {
    }
}
