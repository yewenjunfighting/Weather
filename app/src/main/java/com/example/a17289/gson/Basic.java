package com.example.a17289.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 17289 on 2019/7/7.
 */

public class Basic {

        @SerializedName("city")
        public String cityName;

        @SerializedName("id")
        public String weatherId;

        public Update update;

        public class Update {

            @SerializedName("loc")
            public String updateTime;

        }
}
