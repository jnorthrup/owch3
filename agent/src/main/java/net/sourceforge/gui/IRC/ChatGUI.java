package net.sourceforge.gui.IRC;

import net.sourceforge.gui.AgentVisitor;
import net.sourceforge.gui.ScrollingListModel;
import net.sourceforge.owch2.kernel.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class
ChatGUI extends JInternalFrame implements AgentVisitor {
    private JList UsersList = new JList(new ScrollingListModel());
    private final Container EntryDoc = new JToolBar();
    private JList msgList = new JList(new ScrollingListModel());
    private final Component ValueText = new JTextField();
    DefaultMapNotification agentEventDescriptor;
    ChatAgent node;

    public ChatGUI(HasProperties joinMsg) {
        agentEventDescriptor = new DefaultMapNotification(joinMsg);

        agentEventDescriptor.put("IRCManager", agentEventDescriptor.get(ImmutableNotification.FROM_KEY));
        agentEventDescriptor.put(ImmutableNotification.FROM_KEY, agentEventDescriptor.get("Value"));
        initGUI();
        startAgent();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     */
    public void initGUI() {
        JLabel Value = new JLabel();
        JScrollPane msgScroll = new JScrollPane();
        JScrollPane userScroll = new JScrollPane();
        JSplitPane mainSplitter = new JSplitPane();
        JTextArea renderPane = new JTextArea();

        msgScroll.getViewport().add(getMsgList());
        msgScroll.getViewport().add(msgList);
        userScroll.getViewport().add(getUsersList());
        userScroll.getViewport().add(UsersList);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainSplitter, BorderLayout.CENTER);
        getContentPane().add(EntryDoc, BorderLayout.SOUTH);
        setBounds(new Rectangle(0, 0, 541, 360));
        setIconifiable(true);
        setClosable(true);
        setMaximizable(true);
        EntryDoc.add(Value);
        EntryDoc.add(ValueText);
        Value.setText(">>");
        setResizable(true);
        addInternalFrameListener(
                new InternalFrameAdapter() {
                    public void internalFrameClosing(InternalFrameEvent e) {
                        stopAgent();
                    }
                });
        mainSplitter.setBounds(new Rectangle(116, 135, 180, 29));
        mainSplitter.setOneTouchExpandable(true);
        mainSplitter.setDividerLocation(400);
        mainSplitter.add(msgScroll, JSplitPane.LEFT);
        mainSplitter.add(userScroll, JSplitPane.RIGHT);
        /*  getMsgList().setCellRenderer(
                new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus ) {
                        pane.setText( value.toString() );
                        return pane;
                    };
                } );
        */
        renderPane.setWrapStyleWord(true);
        renderPane.setLineWrap(true);
    }

    public Object get(String key) {
        return null;
    }

    public void put(CharSequence key, Object val) {
    }

    public void stopAgent() {
        final DefaultMapTransaction message = new DefaultMapTransaction();
        message.put("JMSType", "Dissolve");
        message.put(ImmutableNotification.FROM_KEY, node.get("IRCManager"));
        message.put(ImmutableNotification.DESTINATION_KEY, node.getFrom());
        Env.getInstance().send(message);
    }

    public void startAgent() {
        node = new ChatAgent(this, agentEventDescriptor);
        setTitle(agentEventDescriptor.get("Value").toString());
    }

    /**
     * gets keys
     *
     * @return keys
     */
    public String[] getApp_keys() {
        return new String[0];
    }

    public String getApp_keys(int index) {
        return "";
    }

    public AbstractAgent getNode() {
        return node;
    }

    public JList getMsgList() {
        return msgList;
    }

    private void setMsgList(JList msgList) {
        this.msgList = msgList;
    }

    public JList getUsersList() {
        return UsersList;
    }

    protected void setUsersList(JList UsersList) {
        this.UsersList = UsersList;
    }
}


