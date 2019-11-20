package com.doitandroid.mybeta;

public final class ConstantIntegers {
    public final static int SUCCESS = 1;
    public final static int FAIL = 1;

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

    /* result codes */

    public final static int RESULT_SUCCESS = 200;

    public final static int RESULT_LOGOUTTED= 400;
    public final static int RESULT_CANCELED= 404;




    /* feed opt */
    public final static int OPT_DEFAULT_PING = 1;
    public final static int OPT_TO_CLICK = 2;

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
}
