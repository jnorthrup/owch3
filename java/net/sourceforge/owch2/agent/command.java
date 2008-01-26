package net.sourceforge.owch2.agent;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Nov 30, 2002
 * Time: 9:59:20 PM
 * To change this template use Options | File Templates.
 */
public class command {
    private String name;
    private char code;
    private String description;
    private static Map<String, command> names = new TreeMap<String, command>();
    private static Map<Character, command> codes = new TreeMap<Character, command>();

    private command(String name, int code, String des) {
        this.setName(name);
        this.setCode((char) code);
        setDescription(des);
        getCodes().put(Character.valueOf((char) code), this);
        getNames().put(name, this);
    }


    static {
        createCommand("PCT_LOOKUP", 0x20, "  lookup operation");
        createCommand("PCT_KEEP_ALIVE", 0x21, "! integrity check");
        createCommand("PCT_SPECIAL_MSG", 0x22, ",  special display text");
        createCommand("PCT_RESET_DEFAULTS", 0x23, "# use factory defaults");
        createCommand("PCT_PING", 0x24, "$ anyone there ?");
        createCommand("PCT_SET_KEEP_ALIVE_TIMER", 0x26, "& interval for keepalives");
        createCommand("PCT_GET_KEEP_ALIVE_TIMER", 0x27, "\\  ");
        createCommand("PCT_SET_DISPLAY", 0x28, "( display configuration");
        createCommand("PCT_GET_DISPLAY", 0x29, ")");
        createCommand("PCT_SET_RESP_NUM", 0x2a, "* # lookup response screens");
        createCommand("PCT_GET_RESP_NUM", 0x2b, "+");
        createCommand("PCT_SET_RESP_TIMER", 0x2c, ", lookup response timer");
        createCommand("PCT_GET_RESP_TIMER", 0x2d, "-");
        createCommand("PCT_SET_IDLE_NUM", 0x2e, ". # of idle screens");
        createCommand("PCT_GET_IDLE_NUM", 0x2f, "/");
        createCommand("PCT_SET_IDLE_MSG", 0x30, "0 idle message text");
        createCommand("PCT_GET_IDLE_MSG", 0x31, "1");
        createCommand("PCT_SET_IDLE_TIMER", 0x32, "2 time between idle screens");
        createCommand("PCT_GET_IDLE_TIMER", 0x33, "3");
        createCommand("PCT_SET_PLSWAIT_NUM", 0x34, "4 # please wait screens");
        createCommand("PCT_GET_PLSWAIT_NUM", 0x35, "5");
        createCommand("PCT_SET_PLSWAIT_MSG", 0x36, "6 please wait text");
        createCommand("PCT_GET_PLSWAIT_MSG", 0x37, "7");
        createCommand("PCT_SET_PLSWAIT_TIMER", 0x38, "8 time between wait screens");
        createCommand("PCT_GET_PLSWAIT_TIMER", 0x39, "9");
        createCommand("PCT_SET_OUTSERV_NUM", 0x3a, ": #out service screens");
        createCommand("PCT_GET_OUTSERV_NUM", 0x3b, ";");
        createCommand("PCT_SET_OUTSERV_MSG", 0x3c, "< out of service text");
        createCommand("PCT_GET_OUTSERV_MSG", 0x3d, "=");
        createCommand("PCT_SET_OUTSERV_TIMER", 0x3e, "> time between oos screens");
        createCommand("PCT_GET_OUTSERV_TIMER", 0x3f, "?");
        createCommand("PCT_SET_SCANNER_STATE", 0x40, "@ active or inactive");
        createCommand("PCT_GET_SCANNER_STATE", 0x41, "A");
        createCommand("PCT_SCANNER_CFG", 0x42, "B scanner configuration");
        createCommand("PCT_SET_HOST_TO", 0x44, "D host to waiting for reply");
        createCommand("PCT_GET_HOST_TO", 0x45, "E");
        createCommand("PCT_SET_HOST_RETRIES", 0x46, "F # times we retry");
        createCommand("PCT_GET_HOST_RETRIES", 0x47, "G");
        createCommand("PCT_NET_PING", 0x48, "H nct sends these");
        createCommand("PCT_DOWNLOAD", 0x49, "I download command");
        createCommand("PCT_CLEAR_IP", 0x4a, "J clear all IP address info");
        createCommand("PCT_SET_SPEC_TIMER", 0x4b, "K special msg timer");
        createCommand("PCT_GET_SPEC_TIMER", 0x4c, "L");
        createCommand("PCT_GET_STATISTICS", 0x4d, "M retrieve PCT statistics");
        createCommand("PCT_CLR_STATISTICS", 0x4e, "N clear PCT statistics");
        createCommand("PCT_SET_GBL_IDLE_1", 0x4f, "O define non-deletable idle 1");
        createCommand("PCT_DISABLE_BEEP", 0x50, "P disable beeping");
        createCommand("PCT_ENABLE_BEEP", 0x51, "Q enable beeping");
        createCommand("PCT_GET_IP_UNIT", 0x52, "R unit IP address");
        createCommand("PCT_SET_IP_UNIT", 0x53, "S");
        createCommand("PCT_GET_IP_SERV_PRI", 0x54, "T primary server IP address");
        createCommand("PCT_SET_IP_SERV_PRI", 0x55, "U");
        createCommand("PCT_GET_IP_SERV_SEC", 0x56, "V secondary server IP address");
        createCommand("PCT_SET_IP_SERV_SEC", 0x57, "W");
        createCommand("PCT_GET_IP_PORT_SERV", 0x58, "X server IP port");
        createCommand("PCT_SET_IP_PORT_SERV", 0x59, "Y");
        createCommand("PCT_GET_IP_PORT_UNIT", 0x5a, "Z unit IP port");
        createCommand("PCT_SET_IP_PORT_UNIT", 0x5b, "(");
        createCommand("PCT_GET_S24_PROTOCOL", 0x5c, "\\ S24 IP protocol, TCP or UDP");
        createCommand("PCT_SET_S24_PROTOCOL", 0x5d, "]");
        createCommand("PCT_GET_S24_GATEWAY", 0x5e, "^ S24 gateway server IP address");
        createCommand("PCT_SET_S24_GATEWAY", 0x5f, "_");
        createCommand("PCT_GET_S24_SUBNET_MASK", 0x60, " S24 IP subnet mask");
        createCommand("PCT_SET_S24_SUBNET_MASK", 0x61, "a");
        createCommand("PCT_GET_S24_NET_ID", 0x62, "b S24 Spring protocol network ID");
        createCommand("PCT_SET_S24_NET_ID", 0x63, "c");
        createCommand("PCT_GET_S24_ESS_ID", 0x64, "d S24 802.11 protocol network ID");
        createCommand("PCT_SET_S24_ESS_ID", 0x65, "e");
        createCommand("PCT_GET_S24_USER_ID", 0x66, "f S24 user defined modem ID");
        createCommand("PCT_SET_S24_USER_ID", 0x67, "g");
    }

    private static void createCommand(String s, int i, String s1) {
        new command(s, i, s1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getCode() {
        return code;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Map<String, command> getNames() {
        return names;
    }

    public static void setNames(Map<String, command> names) {
        command.names = names;
    }

    public static Map<Character, command> getCodes() {
        return codes;
    }

    public static void setCodes(Map<Character, command> codes) {
        command.codes = codes;
    }

}
