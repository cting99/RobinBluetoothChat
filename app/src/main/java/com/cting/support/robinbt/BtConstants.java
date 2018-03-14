package com.cting.support.robinbt;

import java.util.UUID;

public class BtConstants {

    static final String UUID_NAME = "00000000-0000-1000-8000-00805F9B34FB";
    public static final UUID MY_UUID = UUID.fromString(UUID_NAME);


    public static final int MSG_ACCEPT_SUCCESS = 1;
    public static final int MSG_ACCEPT_FAIL = 2;
    public static final int MSG_CONNECT_SUCCESS = 3;
    public static final int MSG_CONNECT_FAIL = 4;
    public static final int MSG_READ_SUCCESS = 5;
    public static final int MSG_READ_FAIL = 6;
    public static final int MSG_WRITE_SUCCESS = 7;
    public static final int MSG_WRITE_FAIL = 8;
}
