package com.doitandroid.mybeta;

public final class ConstantIntegers {
    public final static int SUCCESS = 1;
    public final static int FAIL = 0;

    /* handler int */

    public final static int TOUCH_DOWN = 1001;
    public final static int TOUCH_UP = 1002;
    public final static int SEND_PROGRESS = 1003;
    public final static int STOP_PROGRESS = 1004;

    public final static int SEND_INSTANT_PING = 1005;

    /* rest responses */

    public final static int SUCCEED_RESPONSE = 10002;
    public final static int FAILED_RESPONSE = 10004;




    public final static int IS_LOGINED = 100;
    public final static int IS_NOT_LOGINED = 200;

    public final static int USER_CREATED = 1111;


    /* request codes */

    public final static int REQUEST_SETTING_ACTIVITY= 300;
    public final static int REQUEST_ADD_POST = 301;
    public final static int REQUEST_SEARCH_RESULT = 302;

    public final static int REQUEST_CONTENT = 303;
    public final static int REQUEST_COMMENT = 305;
    public final static int REQUEST_REACT = 306;

    public final static int REQUEST_FOLLOW = 310;
    public final static int REQUEST_PROFILE_CHANGE = 311;

    public final static int REQUEST_PING_SEARCH = 312;

    /* result codes */

    public final static int RESULT_SUCCESS = 200;

    public final static int RESULT_PROFILE_NOT_CHANGED = 202;
    public final static int RESULT_PROFILE_CHANGED = 203;

    public final static int RESULT_LOGGED_OUT = 400;
    public final static int RESULT_CANCELED= 404;

    /* feed grid */
    public final static int GRID_SPAN = 4;



    /* feed opt */
    public final static int OPT_DEFAULT_PING = 1;
    public final static int OPT_TO_CLICK = 2;
    public final static int OPT_LIST = 3;
    public final static int OPT_RECENT_LIST = 4;
    public final static int OPT_QUARTER_DAY_LIST = 5;
    public final static int OPT_A_DAY_LIST = 6;
    public final static int OPT_EMPTY = 7;

    public final static int OPT_LOADING = 200;



    /* times */
    public final static int TIME_OVER_TWENTEY = 1001;
    public final static int TIME_OVER_TWENTEY_NINE = 2;
    public final static int TIME_DEFAULT = 3003;


    /* notices */

    public final static int NOTICE_FOLLOW = 1001;
    public final static int NOTICE_POST_COMMENT = 2002;
    public final static int NOTICE_POST_REACT = 2003;
    public final static int NOTICE_TIMEBAR_QUARTER_DAY = 2004;
    public final static int NOTICE_TIMEBAR_DAY = 2005;

    /* notification options */
    public final static int NOTIFICATION_FOLLOW = 3001;
    public final static int NOTIFICATION_POST_COMMENT = 3002;
    public final static int NOTIFICATION_POST_REACT = 3003;
    public final static int NOTIFICATION_POST = 3004;

    /* validate */
    public final static int VALIDATE_OK = 1100;
    public final static int VALIDATE_FAILED = 1400;

    /* validate codes */

    public final static int USER_USERNAME_VALIDATE_REGEX_PROBLEM = 1001;
    public final static int USER_USERNAME_VALIDATE_LENGTH_PROBLEM = 1002;
    public final static int USER_USERNAME_VALIDATE_DIGIT_PROBLEM = 1003;
    public final static int USER_USERNAME_VALIDATE_BANNED_PROBLEM = 1004;

    public final static int USER_FULL_NAME_VALIDATE_LENGTH_PROBLEM = 2002;


    public final static int USER_EMAIL_VALIDATE_REGEX_PROBLEM = 3001;
    public final static int USER_EMAIL_VALIDATE_LENGTH_PROBLEM = 3002;
    public final static int USER_EMAIL_EXIST_PROBLEM = 3007;

    public final static int USER_PASSWORD_VALIDATE_SELF_EQUAL_PROBLEM = 4005;
    public final static int USER_PASSWORD_VALIDATE_USERNAME_EQUAL_PROBLEM = 4006;
    public final static int USER_PASSWORD_VALIDATE_LENGTH_PROBLEM = 4002;
    public final static int USER_PASSWORD_VALIDATE_BANNED_PROBLEM = 4004;

}
