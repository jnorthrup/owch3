package owch;
import  java.util.*;


/*
JMSDestination
The JMSDestination header field contains the destination to which the message
is being sent.
When a message is sent this value is ignored. After completion of the send it
holds the destination object specified by the sending method.
When a message is received, its destination value must be equivalent to the
value assigned when it was sent.

JMSDeliveryMode
The JMSDeliveryMode header field contains the delivery mode specified when
the message was sent.
When a message is sent this value is ignored. After completion of the send it
holds the delivery mode specified by the sending method.
See Section 4.7, Message Delivery Mode for more information.

3.4.3 JMSMessageID
The JMSMessageID header field contains a value that uniquely identifies each
message sent by a provider.
When a message is sent, JMSMessageID is ignored. When the send method
returns it contains a provider-assigned value.
A JMSMessageID is a String value which should function as a unique key for
identifying messages in a historical repository. The exact scope of uniqueness is
provider defined. It should at least cover all messages for a specific installation
of a provider where an installation is some connected set of message routers.
All JMSMessageID values must start with the prefix ID:. Uniqueness of
message ID values across different providers is not required.
Since message IDs take some effort to create and increase a messages size,
some JMS providers may be able to optimize message overhead if they are
given a hint that message ID is not used by an application. JMS
MessageProducer provides a hint to disable message ID. When a client sets a
producer to disable message ID it is saying that it does not depend on the
value of message ID for the messages it produces. These messages must either
have message ID set to null or, if the hint is ignored, message ID must be set to
its normal unique value.

3.4.4 JMSTimestamp
The JMSTimestamp header field contains the time a message was handed off to
a provider to be sent. It is not the time the message was actually transmitted
because the actual send may occur later due to transactions or other client side
queueing of messages.
When a message is sent, JMSTimestamp is ignored. When the send method
returns it contains a a time value somewhere in the interval between the call
and the return. It is in the format of a normal Java millis time value.
Since timestamps take some effort to create and increase a messages size, some
JMS providers may be able to optimize message overhead if they are given a
hint that timestamp is not used by an application. JMS MessageProducer
provides a hint to disable timestamps. When a client sets a producer to disable
timestamps it is saying that it does not depend on the value of timestamp for
the messages it produces. These messages must either have timestamp set to
zero or, if the hint is ignored, timestamp must be set to its normal value.

3.4.5 JMSCorrelationID
A client can use the JMSCorrelationID header field to link one message with
another. A typically use is to link a response message with its request message.
JMSCorrelationID can hold one of the following:
" A provider-specific message ID
" An application-specific String
" A provider-native byte[] value.
Since each message sent by a JMS provider is assigned a message ID value it is
convenient to link messages via message ID. All message ID values must start
with the ID: prefix.
In some cases, an application (made up of several clients) needs to use an
application specific value for linking messages. For instance, an application
may use JMSCorrelationID to hold a value referencing some external
information. Application specified values must not start with the ID: prefix;
this is reserved for provider-generated message ID values.
If a provider supports the native concept of correlation ID, a JMS client may
need to assign specific JMSCorrelationID values to match those expected by
non-JMS clients. A byte[] value is used for this purpose. JMS providers without
native correlation ID values are not required to support byte[] values * . The use
of a byte[] value for JMSCorrelationID is non-portable.

JMSReplyTo
The JMSReplyTo header field contains a Destination supplied by a client when
a message is sent. It is the destination where a reply to the message should be
sent.
Messages sent with a null JMSReplyTo value may be a notification of some
event or they may just be some data the sender thinks is of interest.
Messages sent with a JMSReplyTo value are typically expecting a response. A
response may be optional, it is up to the client to decide.

3.4.7 JMSRedelivered
If a client receives a message with the JMSRedelivered indicator set, it is likely,
but not guaranteed, that this message was delivered to the client earlier but the
client did not acknowledge its receipt at that earlier time. See Section 4.4.13,
Message Acknowledgment for more information.
This header field has no meaning on send and is left unassigned by the
sending method.

3.4.8 JMSType
The JMSType header field contains a message type identifier supplied by a
client when a message is sent.
Some JMS providers use a message repository that contains the definition of
messages sent by applications. The type header field may reference a messages
definition in the providers repository.
JMS does not define a standard message definition repository nor does it
define a naming policy for the definitions it contains.
Some messaging systems require that a message type definition for each
application message be created and that each message specify its type. In order
to work with such JMS providers, JMS clients should assign a value to JMSType
whether the application makes use of it or not. This insures that it is properly
set for those providers that require it.
To insure portability, JMS clients should use symbolic values for JMSType that
can be configured at installation time to the values defined in the current
providers message repository. If string literals are use they may not be valid
type names for some JMS providers.

3.4.9 JMSExpiration
When a message is sent, its expiration time is calculated as the sum of the time-to-
live value specified on the send method and the current GMT. On return
from the send method, the messages JMSExpiration header field contains this
value. When a message is received its JMSExpiration header field contains this
same value.
If the time-to-live is specified as zero, expiration is set to zero which indicates
the message does not expire.
When GMT is later than an undelivered messages expiration time, the
message should be destroyed. JMS does not define a notification of message
expiration.
Clients should not receive messages that have expired; however, JMS does not
guarantee that this will not happen.

3.4.10 JMSPriority
The JMSPriority header field contains the messages priority.
When a message is sent this value is ignored. After completion of the send it
holds the value specified by the method sending the message.
JMS defines a ten level priority value with 0 as the lowest priority and 9 as the
highest. In addition, clients should consider priorities 0-4 as gradations of
normal priority and priorities 5-9 as gradations of expedited priority.
JMS does not require that a provider strictly implement priority ordering of
messages; however, it should do its best to deliver expedited messages ahead
of normal messages.
 */

/**
 * @version $Id: Notification.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public class Notification extends Location
{

    public Notification()
    {
    };
    /**
     *
     * Copy C'tor
     *
     */
    public Notification(Map p)
    {
        super(p);
    }
;
};

