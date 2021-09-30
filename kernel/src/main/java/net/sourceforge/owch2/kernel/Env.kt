package net.sourceforge.owch2.kernel

import net.sourceforge.owch2.protocol.Transport
import net.sourceforge.owch2.protocol.TransportEnum
import java.io.FileInputStream
import java.io.InputStream
import java.net.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.AbstractMap.SimpleEntry
import java.util.concurrent.ConcurrentHashMap
import javax.script.Invocable
import javax.script.ScriptException

/**
 * Mobile Agent Hosting Environment. This class acts as a hub for realtime messages
 * passing among several namespaces and transports.
 *
 *
 * This also acts as the central network-stack kernel to unify the interfaces and routing of the various transports
 * that messages are destined for.
 *
 *
 * Workflow design is facilitated by decoupling the URI and URL endpoints from the
 * agent identifiers (labeled <B>"FROM_KEY"</B>).  Agents are semantically named with short human-readable ID's in order to
 * facilitate generic service names living among cloned, replicated, and mobile agents, who will
 * always communicate via the nearest agent hop named "default" initially to locate direct transport locations to
 * route to.
 *
 *
 * "default" Agent routing is bootstrapped into an agent host and all traffic of unknown destination path will
 * forward to the agent named "default", typically a  domain object.
 *
 *
 * Owch messages are intended to use multiple, transport specific fields, not solely any single syntax or
 * URI convention.
 *
 *
 * the defacto delivery model is non-escaped, non multiline RFC822 with no serialization facilities in order to
 * keep the scope and the footprint simple. that said, some amount of REST and SMTP rfc822 usage may test this resolve.
 *
 * @author James Northrup
 * @version $Id$
 * @see AbstractAgent
 */
class Env private constructor() : Invocable {


    @JvmField
    @Volatile
    var shutdown = false//
    /**
     * sets the flag on the Factory Objects to act as parental sendr in all  location resolution.
     *
     * @param flag sets the state to true or false
     */
    /**
     * accessor for parental node being present in the current Process.
     *
     * @return whether we are the Parent Sendr of all transactions
     */
    var isParentHost = false
    var owchPort = 0
    var httpPort = 0

    //
    var hostThreads = 2

    //
    var socketCount = 2
    var domainName = "default"
    private val formatCache: MutableMap<String, Format> by lazy { TreeMap() }


    //    private PathResolver pathResolver = new LeafPathResolver();

    var hostInterface: NetworkInterface? = null
    var pipePort = 0
    val httpRegistry: httpRegistry? = null
    fun send(notification: Transaction) {
        for (outboundTransport in outboundTransports) if (outboundTransport.hasPath(notification.destination)) outboundTransport.send(notification)
    }

    fun recv(notificationDescriptor1: Transaction) {
        for (inboundTransport in inboundTransports) {
            if (inboundTransport.hasPath(notificationDescriptor1.destination)) {
                inboundTransport.recv(notificationDescriptor1)
            } else if (instance.isParentHost) {
                send(notificationDescriptor1)
            }
        }
    }

    /**
     * Calls a method on a script object compiled during a previous script execution,
     * which is retained in the state of the `ScriptEngine`.
     *
     * @param name The name of the procedure to be called.
     * @param thiz If the procedure is a member  of a class
     * defined in the script and thiz is an instance of that class
     * returned by a previous execution or invocation, the named method is
     * called through that instance.
     * @param args Arguments to pass to the procedure.  The rules for converting
     * the arguments to scripting variables are implementation-specific.
     * @return The value returned by the procedure.  The rules for converting the scripting
     * variable returned by the script method to a Java Object are implementation-specific.
     * @throws javax.script.ScriptException if an error occurrs during invocation of the method.
     * @throws NoSuchMethodException        if method with given name or matching argument types cannot be found.
     * @throws NullPointerException         if the method name is null.
     * @throws IllegalArgumentException     if the specified thiz is null or the specified Object is
     * does not represent a scripting object.
     */
    @Throws(ScriptException::class, NoSuchMethodException::class)
    override fun invokeMethod(thiz: Any, name: String, vararg args: Any): Any? {
        return null //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Used to call top-level procedures and functions defined in scripts.
     *
     * @param args Arguments to pass to the procedure or function
     * @return The value returned by the procedure or function
     * @throws javax.script.ScriptException if an error occurrs during invocation of the method.
     * @throws NoSuchMethodException        if method with given name or matching argument types cannot be found.
     * @throws NullPointerException         if method name is null.
     */
    @Throws(ScriptException::class, NoSuchMethodException::class)
    override fun invokeFunction(name: String, vararg args: Any): Any? {
        return null //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an implementation of an interface using functions compiled in
     * the interpreter. The methods of the interface
     * may be implemented using the `invokeFunction` method.
     *
     * @param clasz The `Class` object of the interface to return.
     * @return An instance of requested interface - null if the requested interface is unavailable,
     * i. e. if compiled functions in the `ScriptEngine` cannot be found matching
     * the ones in the requested interface.
     * @throws IllegalArgumentException if the specified `Class` object
     * is null or is not an interface.
     */
    @Deprecated("tmfm here with interface")
    override fun <T> getInterface(clasz: Class<T>): T? {
        return null //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an implementation of an interface using member functions of
     * a scripting object compiled in the interpreter. The methods of the
     * interface may be implemented using the `invokeMethod` method.
     *
     * @param thiz  The scripting object whose member functions are used to implement the methods of the interface.
     * @param clasz The `Class` object of the interface to return.
     * @return An instance of requested interface - null if the requested interface is unavailable,
     * i. e. if compiled methods in the `ScriptEngine` cannot be found matching
     * the ones in the requested interface.
     * @throws IllegalArgumentException if the specified `Class` object
     * is null or is not an interface, or if the specified Object is
     * null or does not represent a scripting object.
     */
    @Deprecated("tmfm here with <Any>interface")
    override fun <T> getInterface(thiz: Any, clasz: Class<T>): T? {
        return null
    }

    internal enum class ProtocolParam(val description: String) {
        Threads("Number of Threads to service all of protocol's ports"), Port("Network port number"), HostAddress("Host address to use"), HostInterface("Host interface to use"), Sockets("Multiple dynamic sockets for high load");
    }

    /**
     * needs work being friendlier
     *
     * @param arguments usu. the commandline args or the source args for a clone instance
     * @return the props from the commandline
     */
    fun parseCommandLineArgs(vararg arguments: CharSequence): Iterator<Map.Entry<CharSequence, Any>>? {
        try {
            val bootMessage = ArrayList<Map.Entry<CharSequence, Any>>()
            //harsh but effective, assume everything is key value pairs.
            var i = 0
            while (i < arguments.size - arguments.size % 2) {
                var argument: String
                argument = arguments[i].toString()
                if (!argument.startsWith("-")) {
                    throw RuntimeException("err:parameter '$argument':Params must all start with -")
                }
                var protoToken = argument.substring(1)
                val valueString = arguments[i + 1].toString()
                when (protoToken) {
                    "help" -> {
                        throw RuntimeException("requested help")
                    }
                    "name" -> {
                        protoToken = HasOrigin.FROM_KEY
                        i += 2
                        continue
                    }
                    "HostAddress" -> {
                        hostAddress = InetAddress.getByName(valueString)
                        i += 2
                        continue
                    }
                    "HostInterface" -> {
                        hostInterface = NetworkInterface.getByName(valueString)
                        i += 2
                        continue
                    }
                }
                val strings = protoToken.split(":".toRegex(), 2).toTypedArray()
                when {
                    strings.size == 2 -> {
                        try {
                            val transport: Transport = TransportEnum.valueOf(protoToken)
                            val attrToken = strings[1]
                            val param = ProtocolParam.valueOf(attrToken)
                            when (param) {
                                ProtocolParam.HostAddress -> transport.setHostAddress(InetAddress.getByName(valueString))
                                ProtocolParam.HostInterface -> transport.setHostInterface(NetworkInterface.getByName(valueString))
                                ProtocolParam.Port -> transport.port = valueString.toShort()
                                ProtocolParam.Sockets -> transport.setSockets(Integer.valueOf(valueString))
                                ProtocolParam.Threads -> transport.setThreads(Integer.valueOf(valueString))
                            }
                        } catch (e: IllegalArgumentException) {
                        }
                    }
                    protoToken == "HostThreads" -> {
                        hostThreads = Integer.decode(valueString)
                        i += 2
                        continue
                    }
                    protoToken == "SocketCount" -> {
                        socketCount = Integer.decode(valueString)
                        i += 2
                        continue
                    }
                    protoToken == "ParentURL" -> {
                        val evt = parentNode as Notification?
                        evt!!["URL"] = valueString
                        parentNode = evt
                        i += 2
                        continue
                    }
                    protoToken == "config" -> {
                        val streamTokenizer: Enumeration<*> = StringTokenizer(valueString)

                        while (streamTokenizer.hasMoreElements()) {
                            val tempString = streamTokenizer.nextElement() as String
                            val fileInputStream: InputStream = FileInputStream(tempString)
                            Files.readAllLines(Paths.get(tempString)).let { lines: MutableList<String> ->
                                bootMessage += lines.mapNotNull {
                                    it.  split(":".toRegex(), 2).takeIf {
                                        it.size == 2
                                    }?.let { (a, b) ->
                                        SimpleEntry(a .trim() , b.trim())
                                    }
                                }
                            }
                        }
                        i += 2
                        continue
                    }
                }
                bootMessage.add(SimpleEntry<CharSequence, Any>(protoToken, valueString))
                i += 2
            }
            return bootMessage.iterator()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            cmdLineHelp("<this was an Env-cmdline syntax problem>")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun sethttpRegistry(h: httpRegistry?) {}
    fun getFormat(name: Any?): Format? {
        return formatCache[name]
    }

    fun registerFormat(name: String, f: Format) {
        formatCache[name] = f
        //  if (Env.logDebug) Env.log(100, "Registering Formatter: " + name);
    }

    val defaultURI: URI?
        get() = if (!isParentHost) {
            parentNode!!.uri
        } else {
            null
        }

    var parentNode: HasOrigin? = null
        get() {
            if (field == null) {
                val l = DefaultMapNotification()
                l["Created"] = "env.getDomain()"
                l[HasOrigin.FROM_KEY] = "default"
                try {
                    l[HasOrigin.URI_KEY] = URI("owch", "default", hostAddress!!.canonicalHostName, 2112, "/default", "", "")
                } catch (e: URISyntaxException) {
                    e.printStackTrace() //To change body of catch statement use File | Settings | File Templates.
                }
                field = l
            }
            return field
        }

    var hostAddress: InetAddress? = null
        get() {
            if (null == field) {
                var hostInterface: NetworkInterface?
                hostInterface = field
                if (hostInterface != null) return getExternalAddress(hostInterface).also { field = it }
                val networkInterfaces: Enumeration<NetworkInterface?>? = try {
                    NetworkInterface.getNetworkInterfaces()
                } catch (e: SocketException) {
                    e.printStackTrace() //To change body of catch statement use Options | File Templates.
                    return null
                }
                while (networkInterfaces!!.hasMoreElements() && field == null) {
                    hostInterface = networkInterfaces.nextElement()
                    hostInterface = hostInterface
                    //                Env.log(133, "Interface name: " + hostInterface.getName());
                    //                Env.log(133, "Interface DisplayName: " + hostInterface.getDisplayName());
                    field = getExternalAddress(hostInterface)
                }
            }
            return field
        }


    val hostname: String
        get() = hostAddress!!.hostName

    companion object {
        @JvmStatic
        val instance: Env by lazy { Env() }
        var httpdSockets: Map<String, Socket> = ConcurrentHashMap()
        var inboundTransports = arrayOf<Transport>(TransportEnum.local, TransportEnum.owch, TransportEnum.http, TransportEnum.Default)
        private var outboundTransports = arrayOf<Transport>(TransportEnum.local, TransportEnum.owch, TransportEnum.http, TransportEnum.Default)

        @JvmStatic
        val localAgents
            get() = TransportEnum.local.localAgents

        fun setOutboundTransports(outboundTransport: Array<Transport>) {
            outboundTransports = outboundTransport
        }

        @JvmStatic
        fun cmdLineHelp(t: String) {
            var s = """*************owch kernel Env (global) cmdline options***********
All cmdline params are of the pairs form -key 'Value'

 valid environmental cmdline options are typically:
-config      - config file[s] to use having (RFC822) pairs of Key: Value
-FROM_KEY  - Name of agent
-name        - shorthand for FROM_KEY
-HostAddress - Host address to use
-HostInterface - Host interface to use
-SocketCount - Multiple dynamic sockets for high load?
-debugLevel  - controls how much scroll is displayed
"""
            s += "-ParentURL   - typically owch://hostname:2112 -- instructs our agent host where to find an uplink\n\n"
            s += "this edition of the Agent Hosting Platform comes with the folowing configurable protocols: \n"
            for (ptype in TransportEnum.values()) {
                if (ptype.port.toInt() == -1) {
                    continue
                }
                s += "\t" + ptype.toString()
            }
            s += "\n\n\t -- Each protocol allows the following configurable syntax: \n"
            for (param in ProtocolParam.values()) {
                s += """
                    [-<proto>:${param.name}]	-	${param.description}
                    
                    """.trimIndent()
            }
            s = """$s

	this Edition of the parser: ${"$"}Id$


************* Agent Env cmdline specification:***********
$t"""
            println(s)
            System.exit(2)
        }

        private fun getExternalAddress(hostInterface: NetworkInterface?): InetAddress? {
            var siteLocalAddress: InetAddress?
            siteLocalAddress = null
            val inetAddresses: Enumeration<InetAddress>
            if (hostInterface != null) {
                inetAddresses = hostInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddr = inetAddresses.nextElement()
                    if (inetAddr.isSiteLocalAddress) {
                        siteLocalAddress = inetAddr
                    }
                    if (!inetAddr.isAnyLocalAddress && !inetAddr.isLinkLocalAddress && !inetAddr.isLoopbackAddress && !inetAddr.isMulticastAddress && !inetAddr.isSiteLocalAddress) return inetAddr
                }
            }
            return siteLocalAddress
        }
    }
}