package net.sourceforge.owch2.agent;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Nov 30, 2002
 * Time: 10:00:05 PM
 * To change this template use Options | File Templates.
 */
public class PCTMessage {
    private command cmd_type;
    private char arg;
    private char flags;
    private String sn;
    private String data;

    public PCTMessage(command cmd_type, char arg, char flags,
                      String sn, String data) {
        this.setCmd_type(cmd_type);
        this.setArg(arg);
        this.setFlags(flags);
        this.setSn(sn);
        this.setData(data);
    }


    public command getCmd_type() {
        return cmd_type;
    }

    public void setCmd_type(command cmd_type) {
        this.cmd_type = cmd_type;
    }

    public char getArg() {
        return arg;
    }

    public void setArg(char arg) {
        this.arg = arg;
    }

    public char getFlags() {
        return flags;
    }

    public void setFlags(char flags) {
        this.flags = flags;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
