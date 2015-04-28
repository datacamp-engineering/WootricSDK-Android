package com.wootric.androidsdk.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import com.wootric.androidsdk.objects.EndUser;
import com.wootric.androidsdk.utils.ConnectionUtils;
import com.wootric.androidsdk.utils.Constants;

import java.io.IOException;

/**
 * Created by maciejwitowski on 4/20/15.
 */
public class CreateResponseTask extends AsyncTask<Void, Void, Void>{

    private final String accessToken;
    private final EndUser endUser;
    private final String originUrl;
    private final String score;
    private final String text;

    public CreateResponseTask(String accessToken, EndUser endUser, String originUrl, String score, String text) {
        this.accessToken = accessToken;
        this.endUser = endUser;
        this.originUrl = originUrl;
        this.score = score;
        this.text = text;
    }

    @Override
    protected Void doInBackground(Void... params) {
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.END_USERS_URL)
                .append(endUser.getId())
                .append("/responses?")
                .append(requestParams());

        try {
            ConnectionUtils.sendAuthorizedPost(builder.toString(), accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String requestParams() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(Constants.PARAM_RESPONSE_ORIGIN_URL, originUrl)
                .appendQueryParameter(Constants.PARAM_RESPONSE_SCORE, score)
                .appendQueryParameter(Constants.PARAM_RESPONSE_TEXT, text);

        return builder.build().getEncodedQuery();
    }
}
