/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class command
/*     */ {
/*     */   private String name;
/*     */   private char code;
/*     */   private String description;
/*  17 */   private static Map names = new TreeMap(); private static Map codes = new TreeMap();
/*     */ 
/*     */   private command(String name, int code, String des) {
/*  20 */     setName(name);
/*  21 */     setCode((char)code);
/*  22 */     setDescription(des);
/*  23 */     getCodes().put(new Character((char)code), this);
/*  24 */     getNames().put(name, this);
/*     */   }
/*     */ 
/*     */   private static void createCommand(String s, int i, String s1)
/*     */   {
/* 102 */     new command(s, i, s1);
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 106 */     return name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 110 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public char getCode() {
/* 114 */     return code;
/*     */   }
/*     */ 
/*     */   public void setCode(char code) {
/* 118 */     this.code = code;
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 122 */     return description;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description) {
/* 126 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public static Map getNames() {
/* 130 */     return names;
/*     */   }
/*     */ 
/*     */   public static void setNames(Map names) {
/* 134 */     names = names;
/*     */   }
/*     */ 
/*     */   public static Map getCodes() {
/* 138 */     return codes;
/*     */   }
/*     */ 
/*     */   public static void setCodes(Map codes) {
/* 142 */     codes = codes;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     createCommand("PCT_LOOKUP", 32, "  lookup operation");
/*  30 */     createCommand("PCT_KEEP_ALIVE", 33, "! integrity check");
/*  31 */     createCommand("PCT_SPECIAL_MSG", 34, ",  special display text");
/*  32 */     createCommand("PCT_RESET_DEFAULTS", 35, "# use factory defaults");
/*  33 */     createCommand("PCT_PING", 36, "$ anyone there ?");
/*  34 */     createCommand("PCT_SET_KEEP_ALIVE_TIMER", 38, "& interval for keepalives");
/*  35 */     createCommand("PCT_GET_KEEP_ALIVE_TIMER", 39, "\\  ");
/*  36 */     createCommand("PCT_SET_DISPLAY", 40, "( display configuration");
/*  37 */     createCommand("PCT_GET_DISPLAY", 41, ")");
/*  38 */     createCommand("PCT_SET_RESP_NUM", 42, "* # lookup response screens");
/*  39 */     createCommand("PCT_GET_RESP_NUM", 43, "+");
/*  40 */     createCommand("PCT_SET_RESP_TIMER", 44, ", lookup response timer");
/*  41 */     createCommand("PCT_GET_RESP_TIMER", 45, "-");
/*  42 */     createCommand("PCT_SET_IDLE_NUM", 46, ". # of idle screens");
/*  43 */     createCommand("PCT_GET_IDLE_NUM", 47, "/");
/*  44 */     createCommand("PCT_SET_IDLE_MSG", 48, "0 idle message text");
/*  45 */     createCommand("PCT_GET_IDLE_MSG", 49, "1");
/*  46 */     createCommand("PCT_SET_IDLE_TIMER", 50, "2 time between idle screens");
/*  47 */     createCommand("PCT_GET_IDLE_TIMER", 51, "3");
/*  48 */     createCommand("PCT_SET_PLSWAIT_NUM", 52, "4 # please wait screens");
/*  49 */     createCommand("PCT_GET_PLSWAIT_NUM", 53, "5");
/*  50 */     createCommand("PCT_SET_PLSWAIT_MSG", 54, "6 please wait text");
/*  51 */     createCommand("PCT_GET_PLSWAIT_MSG", 55, "7");
/*  52 */     createCommand("PCT_SET_PLSWAIT_TIMER", 56, "8 time between wait screens");
/*  53 */     createCommand("PCT_GET_PLSWAIT_TIMER", 57, "9");
/*  54 */     createCommand("PCT_SET_OUTSERV_NUM", 58, ": #out service screens");
/*  55 */     createCommand("PCT_GET_OUTSERV_NUM", 59, ";");
/*  56 */     createCommand("PCT_SET_OUTSERV_MSG", 60, "< out of service text");
/*  57 */     createCommand("PCT_GET_OUTSERV_MSG", 61, "=");
/*  58 */     createCommand("PCT_SET_OUTSERV_TIMER", 62, "> time between oos screens");
/*  59 */     createCommand("PCT_GET_OUTSERV_TIMER", 63, "?");
/*  60 */     createCommand("PCT_SET_SCANNER_STATE", 64, "@ active or inactive");
/*  61 */     createCommand("PCT_GET_SCANNER_STATE", 65, "A");
/*  62 */     createCommand("PCT_SCANNER_CFG", 66, "B scanner configuration");
/*  63 */     createCommand("PCT_SET_HOST_TO", 68, "D host to waiting for reply");
/*  64 */     createCommand("PCT_GET_HOST_TO", 69, "E");
/*  65 */     createCommand("PCT_SET_HOST_RETRIES", 70, "F # times we retry");
/*  66 */     createCommand("PCT_GET_HOST_RETRIES", 71, "G");
/*  67 */     createCommand("PCT_NET_PING", 72, "H nct sends these");
/*  68 */     createCommand("PCT_DOWNLOAD", 73, "I download command");
/*  69 */     createCommand("PCT_CLEAR_IP", 74, "J clear all IP address info");
/*  70 */     createCommand("PCT_SET_SPEC_TIMER", 75, "K special msg timer");
/*  71 */     createCommand("PCT_GET_SPEC_TIMER", 76, "L");
/*  72 */     createCommand("PCT_GET_STATISTICS", 77, "M retrieve PCT statistics");
/*  73 */     createCommand("PCT_CLR_STATISTICS", 78, "N clear PCT statistics");
/*  74 */     createCommand("PCT_SET_GBL_IDLE_1", 79, "O define non-deletable idle 1");
/*  75 */     createCommand("PCT_DISABLE_BEEP", 80, "P disable beeping");
/*  76 */     createCommand("PCT_ENABLE_BEEP", 81, "Q enable beeping");
/*  77 */     createCommand("PCT_GET_IP_UNIT", 82, "R unit IP address");
/*  78 */     createCommand("PCT_SET_IP_UNIT", 83, "S");
/*  79 */     createCommand("PCT_GET_IP_SERV_PRI", 84, "T primary server IP address");
/*  80 */     createCommand("PCT_SET_IP_SERV_PRI", 85, "U");
/*  81 */     createCommand("PCT_GET_IP_SERV_SEC", 86, "V secondary server IP address");
/*  82 */     createCommand("PCT_SET_IP_SERV_SEC", 87, "W");
/*  83 */     createCommand("PCT_GET_IP_PORT_SERV", 88, "X server IP port");
/*  84 */     createCommand("PCT_SET_IP_PORT_SERV", 89, "Y");
/*  85 */     createCommand("PCT_GET_IP_PORT_UNIT", 90, "Z unit IP port");
/*  86 */     createCommand("PCT_SET_IP_PORT_UNIT", 91, "(");
/*  87 */     createCommand("PCT_GET_S24_PROTOCOL", 92, "\\ S24 IP protocol, TCP or UDP");
/*  88 */     createCommand("PCT_SET_S24_PROTOCOL", 93, "]");
/*  89 */     createCommand("PCT_GET_S24_GATEWAY", 94, "^ S24 gateway server IP address");
/*  90 */     createCommand("PCT_SET_S24_GATEWAY", 95, "_");
/*  91 */     createCommand("PCT_GET_S24_SUBNET_MASK", 96, " S24 IP subnet mask");
/*  92 */     createCommand("PCT_SET_S24_SUBNET_MASK", 97, "a");
/*  93 */     createCommand("PCT_GET_S24_NET_ID", 98, "b S24 Spring protocol network ID");
/*  94 */     createCommand("PCT_SET_S24_NET_ID", 99, "c");
/*  95 */     createCommand("PCT_GET_S24_ESS_ID", 100, "d S24 802.11 protocol network ID");
/*  96 */     createCommand("PCT_SET_S24_ESS_ID", 101, "e");
/*  97 */     createCommand("PCT_GET_S24_USER_ID", 102, "f S24 user defined modem ID");
/*  98 */     createCommand("PCT_SET_S24_USER_ID", 103, "g");
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.command
 * JD-Core Version:    0.6.0
 */