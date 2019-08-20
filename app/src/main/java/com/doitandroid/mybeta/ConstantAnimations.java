package com.doitandroid.mybeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantAnimations {
    public static final Integer ACTION = R.raw.data;
    public static final List<Integer> list = Collections.unmodifiableList(
            new ArrayList<Integer>() {{
                add(R.raw.icon_home);
                add(R.raw.home_sample_bodymovin);
                add(R.raw.home_test);
                add(R.raw.ping_base_hi);
                add(R.raw.ping_base_hi2);
                add(R.raw.ping_base_hi3);
                add(R.raw.ping_base_hi4);
                add(R.raw.ping_base_hi5);
                add(R.raw.ping_base_hi6);
                add(R.raw.ping_base_hi7);
                add(R.raw.ping_base_hi8);
                add(R.raw.ping_base_hi9);
                // etc
            }});

    public static final Map<String, Integer> map = Collections.unmodifiableMap(
            new HashMap<String, Integer>(){
                {
                    put("1", R.raw.icon_home);
                    put("2", R.raw.icon_home);
                    put("3", R.raw.home_sample_bodymovin);
                    put("4", R.raw.home_test);
                    put("5", R.raw.ping_base_hi);
                    put("6", R.raw.ping_base_hi2);
                    put("7", R.raw.ping_base_hi3);
                    put("8", R.raw.ping_base_hi4);
                    put("9", R.raw.ping_base_hi5);
                    put("10", R.raw.ping_base_hi6);
                    put("11", R.raw.ping_base_hi7);
                    put("12", R.raw.ping_base_hi8);
                    put("13", R.raw.ping_base_hi9);
                }
            }
    );

    public static final Map<String, String> textMap = Collections.unmodifiableMap(
            new HashMap<String, String>(){
                {
                    put("1", "안녕하세요");
                    put("2", "싸인을보내 시그널보내");
                    put("3", "근데 전혀 안통해");
                    put("4", "못 알아듣내 ㅡㅡ ");
                    put("5", "싸인을보내 시그날보내");
                    put("6", "소용이벗내 ");
                    put("7", "아 더워");
                    put("8", "추워");
                    put("9", "넘넘추워!!!!");
                    put("10", "왜이렇게도");
                    put("11", "찌리찌리찌리");
                    put("12", "콧노래가 나오다가 나도몰래");
                    put("13", "이대로 사라져버리면안되염이대로 사라져버리면안되염이대로 사라져버리면안되염");
                }
            }
    );
}

