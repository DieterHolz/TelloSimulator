/*
 * Copyright 2020 Fritz Windisch
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tellosimulator.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.regex.Pattern;

public class TelloSDKValues {

    public static final String OP_IP_ADDRESS = "127.0.0.1";
    public static final int OP_COMMAND_PORT = 8889;
    public static final int SIM_COMMAND_PORT = 8879;
    public static final int OP_STATE_PORT = 8890;
    public static final int SIM_STATE_PORT = 8880;
    public static final int OP_STREAM_PORT = 11111;
    public static final int SIM_STREAM_PORT = 11101;

    public static final int COMMAND_SOCKET_TIMEOUT = 200000; //todo: change to 20000
    public static final int COMMAND_TIMEOUT = 15000;
    public static final int STATE_SOCKET_TIMEOUT = 1000;
    public static final int VIDEO_SOCKET_TIMEOUT = 1000;
    public static final int COMMAND_SOCKET_BINARY_ATTEMPTS = 5;

    public static final int STREAM_DEFAULT_PACKET_SIZE = 1460;

    public static final boolean DEBUG = false;

    public static final int VIDEO_WIDTH = 960;
    public static final int VIDEO_HEIGHT = 720;

    public static final Pattern COMMAND_REPLY_PATTERN = Pattern.compile("[\\w. ;:\"]+");

}
