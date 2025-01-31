/*
 * Copyright (c) 2016 Wootric (https://wootric.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.wootric.androidsdk;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import com.wootric.androidsdk.network.WootricRemoteClient;
import com.wootric.androidsdk.objects.EndUser;
import com.wootric.androidsdk.objects.Settings;
import com.wootric.androidsdk.objects.User;
import com.wootric.androidsdk.objects.WootricCustomMessage;
import com.wootric.androidsdk.objects.WootricCustomThankYou;
import com.wootric.androidsdk.utils.PermissionsValidator;
import com.wootric.androidsdk.utils.PreferencesUtils;
import com.wootric.androidsdk.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.wootric.androidsdk.utils.Utils.checkNotNull;

/**
 * Created by maciejwitowski on 4/10/15.
 */
public class Wootric {

    WeakReference<FragmentActivity> weakFragmentActivity;
    WeakReference<Activity> weakActivity;
    WeakReference<Context> weakContext;
    WootricSurveyCallback surveyCallback;

    final EndUser endUser;
    final User user;
    final Settings settings;

    PreferencesUtils preferencesUtils;
    PermissionsValidator permissionsValidator;

    static volatile Wootric singleton;

    /**
     * It configures the SDK with required parameters.
     *
     * @param fragmentActivity FragmentActivity where the survey will be presented.
     * @param clientId Found in API section of the Wootric's admin panel.
     * @param accountToken Found in Install section of the Wootric's admin panel.
     */
    public static Wootric init(FragmentActivity fragmentActivity, String clientId, String accountToken) {
        Wootric local = singleton;
        if(local == null) {
            synchronized (Wootric.class) {
                local = singleton;
                if(local == null) {
                    checkNotNull(fragmentActivity, "FragmentActivity");
                    checkNotNull(clientId, "Client Id");
                    checkNotNull(accountToken, "Account Token");
                    singleton = local = new Wootric(fragmentActivity, clientId, accountToken);
                }
            }
        }

        return local;
    }

    /**
     * It configures the SDK with required parameters.
     *
     * @param activity Activity where the survey will be presented.
     * @param clientId Found in API section of the Wootric's admin panel.
     * @param accountToken Found in Install section of the Wootric's admin panel.
     */
    public static Wootric init(Activity activity, String clientId, String accountToken) {
        Wootric local = singleton;
        if(local == null) {
            synchronized (Wootric.class) {
                local = singleton;
                if (local == null) {
                    checkNotNull(activity, "Activity");
                    checkNotNull(clientId, "Client Id");
                    checkNotNull(accountToken, "Account Token");
                    singleton = local = new Wootric(activity, clientId, accountToken);
                }
            }
        }

        return local;
    }

    /**
     * It configures the SDK with required parameters.
     *
     * @param fragmentActivity FragmentActivity where the survey will be presented.
     * @param accountToken Found in Install section of the Wootric's admin panel.
     */
    public static Wootric init(FragmentActivity fragmentActivity, String accountToken) {
        Wootric local = singleton;
        if(local == null) {
            synchronized (Wootric.class) {
                local = singleton;
                if(local == null) {
                    checkNotNull(fragmentActivity, "FragmentActivity");
                    checkNotNull(accountToken, "Account Token");
                    singleton = local = new Wootric(fragmentActivity, accountToken);
                }
            }
        }

        return local;
    }

    /**
     * It configures the SDK with required parameters.
     *
     * @param activity Activity where the survey will be presented.
     * @param accountToken Found in Install section of the Wootric's admin panel.
     */
    public static Wootric init(Activity activity, String accountToken) {
        Wootric local = singleton;
        if(local == null) {
            synchronized (Wootric.class) {
                local = singleton;
                if (local == null) {
                    checkNotNull(activity, "Activity");;
                    checkNotNull(accountToken, "Account Token");
                    singleton = local = new Wootric(activity, accountToken);
                }
            }
        }

        return local;
    }

    public static void notifySurveyFinished(boolean surveyShown, boolean responseSent, Integer resurvey_days) {
        if (singleton == null) return;

        if (surveyShown) {
            singleton.preferencesUtils.touchLastSurveyed(responseSent, resurvey_days);
        }

        singleton = null;
    }

    // For tests and integrations
    public Wootric get() {
        return singleton;
    }

    public void setSurveyCallback(WootricSurveyCallback surveyCallback){
        this.surveyCallback = surveyCallback;
    }
    /**
     *  Set event.
     *
     * @param eventName String of the event name.
     */
    public void setEventName(String eventName) {
        settings.setEventName(eventName);
    }

    /**
     *  End user email is optional. If not provided, the end user will be considered as "Unknown".
     *
     * @param email String of the end user's email.
     */
    public void setEndUserEmail(String email) {
        endUser.setEmail(email);
    }

    /**
     *  Set an end user's external id.
     *
     * @param externalId String of the end user's external id.
     */
    public void setEndUserExternalId(String externalId) {
        endUser.setExternalId(externalId);
    }

    /**
     *  Set the end user's phone number.
     *
     * @param phoneNumber String of the end user's phone number.
     */
    public void setEndUserPhoneNumber(String phoneNumber) {
        endUser.setPhoneNumber(phoneNumber);
    }

    /**
     * Set the end user created date. The createdAt  must be a seconds unix time value and
     * it must be 10 digits only.
     * Important: If you use System.currentTimeMillis() make sure you divide it by 1000.
     * @param createdAt
     */
    public void setEndUserCreatedAt(long createdAt) {
        Utils.checkDate(createdAt);
        endUser.setCreatedAt(createdAt);
    }

    /**
     *  End user properties can be provided as a HashMap<String, String> object.
     * <br/><br/>
     * Example:
     * <pre>
     * {@code
     * HashMap<String, String> properties = new HashMap<String, String>();
     *  properties.put("company", "Wootric");
     *  properties.put("type", "awesome");
     *  wootric.setProperties(properties);
     * }
     * </pre>
     *
     * @param properties HashMap containing custom properties.
     */
    public void setProperties(HashMap<String, String> properties) {
        endUser.setProperties(properties);
    }


    /**
     * If surveyImmediately is set to YES and user wasn't surveyed yet - eligibility check will
     * return "true" and survey will be displayed.
     * Important: This shouldn't be used on production.
     * @param surveyImmediately A boolean to set if the end user should be surveyed immediately.
     */
    public void setSurveyImmediately(boolean surveyImmediately) {
        this.settings.setSurveyImmediately(surveyImmediately);
    }

    /**
     * If showOptOut is set to YES then opt out link will be shown
     * return "true" and opt out link will be shown
     * @param showOptOut A boolean to set if opt out link is to be shown.
     */
    public void setShowOptOut(boolean showOptOut) {
        this.settings.setShowOptOut(showOptOut);
    }

    /**
     * Wootric provides designated class for providing custom messages
     * @param customMessage WootricCustomMessage object
     */
    public void setCustomMessage(WootricCustomMessage customMessage) {
        settings.setLocalCustomMessage(customMessage);
    }

    /**
     * Used to check if end user was created/last seen earlier than ago and therefore if survey
     * is required.
     * @param value An int representing the days after the first survey should be shown.
     */
    public void setFirstSurveyDelay(int value) { settings.setFirstSurveyDelay(value); }

    /**
     * This allows you to set a ceiling on the number of responses you want to collect in a day.
     * Once the cap is reached no more users will be surveyed that day. The default is no cap.
     * <br />
     * Your limits on the volume of users reached will continue to apply.
     *
     * @param value An int representing the maximum number of responses to be collected in a day.
     */
    public void setDailyResponseCap(int value) {
        settings.setDailyResponseCap(value);
    }

    /**
     * This allows you to set the percentage of your registered user traffic that you wish to
     * sample.
     * <br />
     * By default, this is set to 33% or a registered user who visits your siteha s a random 1 in 3
     * chance of being served the survey.
     * <br />
     * Make sure that you are sending us an email address for the registered user.
     *
     * @param value An int representing the maximum number of responses to be collected in a day.
     */
    public void setRegisteredPercent(int value) {
        settings.setRegisteredPercent(value);
    }

    /**
     * This allows you to set the percentage of your visitor traffic (non registered
     * users) that you wish to sample.
     * <br />
     * By default, this is set to 1% or a visitor to your site has a 1 in 100 chance of being served
     * the survey.
     * <br />
     * This setting is only for unknown users.
     *
     * @param value An int (0-100) representing the visitors' percentage to be surveyed.
     */
    public void setVisitorPercent(int value) {
        settings.setVisitorPercent(value);
    }

    /**
     * This allows you to set the number of days after an user sees a survey that they will not be
     * surveyed again.
     * <br />
     * Once the throttle period has passed the user is eligible for resurvey.
     * The default is 90 days.
     * <br />
     * Remember that eligibility does not mean they will be surveyed exactly 90 days after their
     * previous survey.
     *
     * @param value An int representing the number of days of the resurvey throttle.
     */
    public void setResurveyThrottle(int value) {
        settings.setResurveyThrottle(value);
    }

    /**
     * It sets the language of the survey e.g. 'ES', 'FR', 'CN_S'.
     *
     * @see <a href="http://docs.wootric.com/install/#custom-language-setting">Wootric docs </a>for
     * a complete list of supported languages and their codes.
     *
     * @param languageCode language code String.
     */
    public void setLanguageCode(String languageCode) {
        settings.setLanguageCode(languageCode);
    }

    /**
     * It sets the product name for the end user. This will change the default question.
     * <br />
     * e.g. How likely are you to recommend productName to a friend or co-worker?
     * @param productName product name to be shown to the end user.
     */
    public void setProductName(String productName) {
        settings.setProductName(productName);
    }

    /**
     * If surveyedDefault is set to false the SDK will always check with the server
     * to see if the end user is eligible
     * @param surveyedDefault A boolean to set if the SDK should check with the eligibility server.
     */
    public void setSurveyedDefault(Boolean surveyedDefault) {
        settings.setSurveyedDefault(surveyedDefault);
    }

    /**
     * It sets the audience of the survey.
     * <br />
     * e.g. How likely are you to recommend this product or service to recommendTarget?
     * @param recommendTarget target to be shown to the end user.
     */
    public void setRecommendTarget(String recommendTarget) {
        settings.setRecommendTarget(recommendTarget);
    }

    /**
     * If configured, a third screen for promoters (score 9-10) will show a Facebook like
     * (thumbs up) button and a share button.
     * @param facebookPage Facebook page.
     */
    public void setFacebookPageId(String facebookPage) {
        settings.setFacebookPageId(facebookPage);
    }

    /**
     * If configured, a third screen for promoters (score 9-10) will show a Twitter share button.
     * @param twitterPage Twitter handler.
     */
    public void setTwitterPage(String twitterPage) {
        settings.setTwitterPage(twitterPage);
    }

    /**
     * Wootric provides designated class for providing custom thank you.
     * @param customThankYou WootricCustomThankYou object.
     */
    public void setCustomThankYou(WootricCustomThankYou customThankYou) {
        settings.setCustomThankYou(customThankYou);
    }

    /**
     * Changes background color and text buttons color for the survey.
     * @param surveyColor survey color reference int.
     */
    public void setSurveyColor(int surveyColor) {
        settings.setSurveyColor(surveyColor);
    }

    /**
     * Changes score selector color and comment highlight color.
     * @param scoreColor score color reference int.
     */
    public void setScoreColor(int scoreColor) {
        settings.setScoreColor(scoreColor);
    }

    /**
     * Changes Thank You button color on the final view.
     * @param thankYouButtonBackgroundColor background color reference int.
     */
    public void setThankYouButtonBackgroundColor(int thankYouButtonBackgroundColor) {
        settings.setThankYouButtonBackgroundColor(thankYouButtonBackgroundColor);
    }

    /**
     * Changes Facebook and Twitter buttons colors.
     * @param socialSharingColor social color reference int.
     */
    public void setSocialSharingColor(int socialSharingColor) {
        settings.setSocialSharingColor(socialSharingColor);
    }

    /**
     * The followup screen can be skipped for promoters, so they are taken straight to thank you screen.
     * @param shouldSkipFollowupScreenForPromoters boolean value.
     */
    public void shouldSkipFollowupScreenForPromoters(boolean shouldSkipFollowupScreenForPromoters) {
        settings.setSkipFollowupScreenForPromoters(shouldSkipFollowupScreenForPromoters);
    }

    /**
      * Setter to skip feedback screen and go to thank you message.
      * @param skipFeedbackScreen boolean value.
      */
    public void skipFeedbackScreen(boolean skipFeedbackScreen) {
        settings.setSkipFeedbackScreen(skipFeedbackScreen);
    }

    /**
     * Change the time delay to show the survey (in seconds).
     * @param  customTimeDelay int value
     */
    public void setCustomTimeDelay(int customTimeDelay) {
        settings.setTimeDelay(customTimeDelay);
    }

    /**
     * Set Survey Type Scale
     * @param surveyTypeScale typeScale int
     */
    public void setSurveyTypeScale(int surveyTypeScale) {
        settings.setCustomSurveyTypeScale(surveyTypeScale);
    }

    /**
     * Hides a survey if it’s displayed without generating a decline.
     * If the delay time is still running stop will abort that survey from happening.
     */
    public void stop() {
        buildSurveyManager().stop();
    }

    /**
     * Starts the survey with an event name if configuration is correctly set and elibility returns true.
     */
    public void survey(String eventName) {
        this.setEventName(eventName);
        if (!permissionsValidator.check()) {
            return;
        }

        OfflineDataHandler offlineDataHandler = new OfflineDataHandler(preferencesUtils);
        WootricRemoteClient wootricRemoteClient = new WootricRemoteClient(offlineDataHandler, user.getAccountToken());
        SurveyValidator surveyValidator = buildSurveyValidator();

        if (weakFragmentActivity != null) {
            buildSurveyManager().start(weakFragmentActivity.get(), wootricRemoteClient,
                    user, endUser, settings, preferencesUtils, surveyCallback, surveyValidator);
        } else {
            buildSurveyManager().start(weakActivity.get(), wootricRemoteClient,
                    user, endUser, settings, preferencesUtils, surveyCallback, surveyValidator);
        }
    }

    /**
     * Starts the survey if configuration is correctly set and elibility returns true.
     */
    public void survey() {
        this.survey(settings.getEventName());
    }

    public void showSurveyInActivity(Activity activity, String eventName) {
        this.setEventName(eventName);
        if (!permissionsValidator.check()) {
            return;
        }

        OfflineDataHandler offlineDataHandler = new OfflineDataHandler(preferencesUtils);
        WootricRemoteClient wootricRemoteClient = new WootricRemoteClient(offlineDataHandler, user.getAccountToken());
        SurveyValidator surveyValidator = buildSurveyValidator();

        if (weakFragmentActivity != null) {
            getSurveyManagerForActivity(activity).start(weakFragmentActivity.get(), wootricRemoteClient,
                    user, endUser, settings, preferencesUtils, surveyCallback, surveyValidator);
        } else {
            getSurveyManagerForActivity(activity).start(weakActivity.get(), wootricRemoteClient,
                    user, endUser, settings, preferencesUtils, surveyCallback, surveyValidator);
        }
    }

    public void showSurveyInActivity(Activity activity) {
        this.showSurveyInActivity(activity, settings.getEventName());
    }

    private SurveyManager getSurveyManagerForActivity(Activity activity) {
        if (activity instanceof FragmentActivity) {
            weakFragmentActivity = new WeakReference<>((FragmentActivity) activity);
        } else {
            weakActivity = new WeakReference<>(activity);
        }

        weakContext = new WeakReference<>(activity.getApplicationContext());

        preferencesUtils = new PreferencesUtils(weakContext);
        permissionsValidator = new PermissionsValidator(weakContext);

        return buildSurveyManager();
    }

    private Wootric(FragmentActivity fragmentActivity, String clientId, String accountToken) {
        if (fragmentActivity != null && fragmentActivity.getResources().getBoolean(R.bool.isTablet)) {
            fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        weakFragmentActivity = new WeakReference<>(fragmentActivity);
        weakContext = new WeakReference<>(fragmentActivity.getApplicationContext());

        endUser = new EndUser();
        user = new User(accountToken);
        settings = new Settings();

        preferencesUtils = new PreferencesUtils(weakContext);
        permissionsValidator = new PermissionsValidator(weakContext);
    }

    private Wootric(Activity activity, String clientId, String accountToken) {
        if (activity != null && activity.getResources().getBoolean(R.bool.isTablet)) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        weakActivity = new WeakReference<>(activity);
        weakContext = new WeakReference<>(activity.getApplicationContext());

        endUser = new EndUser();
        user = new User(accountToken);
        settings = new Settings();

        preferencesUtils = new PreferencesUtils(weakContext);
        permissionsValidator = new PermissionsValidator(weakContext);
    }

    private Wootric(FragmentActivity fragmentActivity, String accountToken) {
        if (fragmentActivity != null && fragmentActivity.getResources().getBoolean(R.bool.isTablet)) {
            fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        weakFragmentActivity = new WeakReference<>(fragmentActivity);
        weakContext = new WeakReference<>(fragmentActivity.getApplicationContext());

        endUser = new EndUser();
        user = new User(accountToken);
        settings = new Settings();

        preferencesUtils = new PreferencesUtils(weakContext);
        permissionsValidator = new PermissionsValidator(weakContext);
    }

    private Wootric(Activity activity, String accountToken) {
        if (activity != null && activity.getResources().getBoolean(R.bool.isTablet)) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        weakActivity = new WeakReference<>(activity);
        weakContext = new WeakReference<>(activity.getApplicationContext());

        endUser = new EndUser();
        user = new User(accountToken);
        settings = new Settings();

        preferencesUtils = new PreferencesUtils(weakContext);
        permissionsValidator = new PermissionsValidator(weakContext);
    }

    SurveyValidator buildSurveyValidator() {
        return new SurveyValidator();
    }

    SurveyManager buildSurveyManager() {
        return SurveyManager.getSharedInstance();
    }
}
