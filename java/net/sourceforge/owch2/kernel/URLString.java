/*
*
@(#)URLString.java	1.29 96/02/29
*
* Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
*
* Permission to use, copy, modify, and distribute this software and its
* documentation for NON-COMMERCIAL purposes and without fee is hereby
* granted provided that this copyright notice appears in all
copies. Please
* refer to the file "copyright.html" for further
important copyright and
* licensing information.
*
* SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
SUITABILITY OF THE
* SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
* IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE,
* OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY
DAMAGES SUFFERED BY
* LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
THIS SOFTWARE OR
* ITS DERIVATIVES.
*/

package net.sourceforge.owch2.kernel;

import java.net.*;

/**
 * Class URLString is a shameless rip of URL.java's string app. Copyright info will stay for the most part, in his name, and
 * should he bitch, someone else will get paid to write the same things.
 * This class represents the first sacrifice from RNODI design spec-- we cannot build our own URLStreamHandlerFactory. -jimn
 * @version 	$Id: URLString.java,v 1.1 2002/12/08 16:05:52 grrrrr Exp $
 * @author James Northrup
 */
public final class URLString {
    public final String toString() {
        return toExternalForm(this);
    }

    /** The protocol to use (ftp, http, nntp, ... etc.) . */
    private String protocol;

    /** The host name in which to connect to. */
    private String host;

    /** The protocol port to connect to. */
    private int port = -1;

    /** The specified file name on that host. */
    private String file;

    /** # reference. */
    private String ref;

    /**
     * Creates an absolute URL from the specified protocol, host, port and file.
     * @param protocol the protocol to use
     * @param host the host to connect to
     * @param port the port at that host to connect to
     * @param file the file on that host
     */
    URLString(String protocol, String host, int port, String file) {
        this.protocol = protocol;
        this.host = host;
        this.file = file;
        this.port = port;
    }

    /**
     * Creates an absolute URL from the specified protocol, host, and file.  The port number used will be the
     * default for the protocol.
     * @param protocol the protocol to use
     * @param host the host to connect to
     * @param file the file on that host
     */
    URLString(String protocol, String host, String file) {
        this(protocol, host, -1, file);
    }

    /**
     * Creates a URL from the unparsed absolute URL.
     * @param spec the URL String to parse
     */
    public URLString(String spec) {
        this(null, spec);
    }

    /**
     * Creates a URL from the unparsed URL in the specified context.If
     * spec is an absolute URL it is used as is. Otherwise it isparsed
     * in terms of the context.  Context may be null (indicating no context).
     * @param context the context to parse the URL to
     * @param spec the URL String to parse
     */
    URLString(URLString context, String spec) {
        int i, limit, c;
        int start = 0;
        String newProtocol = null;
        if (spec != null) {
            limit = spec.length();
        }
        else {
            limit = 0;
        }
        ;
        while ((limit > 0) && (spec.charAt(limit - 1) <= ' ')) {
            limit--; //eliminate trailing whitespace
        }
        while ((start < limit) && (spec.charAt(start) <= ' ')) {
            start++; // eliminate leading whitespace
        }
        if (spec.regionMatches(true, start, "url:", 0, 4)) {
            start += 4;
        }
        for (i = start; (i < limit) && ((c = spec.charAt(i)) != '/'); i++) {
            if (c == ':') {
                newProtocol = spec.substring(start, i).toLowerCase();
                start = i + 1;
                break;
            }
        }
        // Only use our context if the protocols match.
        if ((context != null) && ((newProtocol == null) || newProtocol.equals(context.protocol))) {
            protocol = context.protocol;
            host = context.host;
            port = context.port;
            file = context.file;
        }
        else {
            protocol = newProtocol;
        }
        i = spec.indexOf('#', start);
        if (i >= 0) {
            ref = spec.substring(i + 1, limit);
            limit = i;
        }

        /**
         * This method is called to parse the string spec into URL u.  If there is any inherited context then it has
         * already been copied into u.  The parameters <code>start</code> and <code>limit</code> refer to the
         * range of characters in spec that should be parsed.  The default method uses
         * parsing rules that match the http spec, which most URL protocol families follow.  If you are writing a protocol
         * handler that has a different syntax, override this routine.
         * @param	u the URL to receive the result of parsing the spec
         * @param	spec the URL string to parse
         * @param	start the character position to start parsing at.  This is just past the ':' (if there is one).
         * @param	limit the character position to stop parsing at.  This is the end of the string or the position of the "#"
         * character if present (the "#" reference syntax is protocol independent).
         */
        if ((start <= limit - 2) && (spec.charAt(start) == '/') && (spec.charAt(start + 1) == '/')) {
            start += 2;
            i = spec.indexOf('/', start);
            if (i < 0) {
                i = limit;
            }
            int prn = spec.indexOf(':', start);
            port = -1;
            if ((prn < i) && (prn >= 0)) {
                try {
                    port = Integer.parseInt(spec.substring(prn + 1, i));
                }
                catch (Exception e) {
                    // ignore bogus port numbers
                }
                if (prn > start) {
                    host = spec.substring(start, prn);
                }
            }
            else {
                host = spec.substring(start, i);
            }
            start = i;
            file = null;
        }
        else if (host == null) {
            host = "";
        }
        if (start < limit) {
            if (spec.charAt(start) == '/') {
                file = spec.substring(start, limit);
            }
            else {
                file = (file != null ? file.substring(0, file.lastIndexOf('/')) : "") + "/" +
                        spec.substring(start, limit);
            }
        }
        if ((file == null) || (file.length() == 0)) {
            file = "/";
        }
        while ((i = file.indexOf("/./")) >= 0) {
            file = file.substring(0, i) + file.substring(i + 2);
        }
        while ((i = file.indexOf("/../")) >= 0) {
            if ((limit = file.lastIndexOf('/', i - 1)) >= 0) {
                file = file.substring(0, limit) + file.substring(i + 3);
            }
            else {
                file = file.substring(i + 3);
            }
        }
    }

    /**
     * Reverses the parsing of the URL.  This should probably be overridden if you override parseURL().
     * @param u the URL
     * @return	the textual representation of the fully
     * qualified URL (i.e. after the context and canonicalization have been applied).
     */
    String toExternalForm(URLString u) {
        String result = u.getProtocol() + ":";
        if ((u.getHost() != null) && (u.getHost().length() > 0)) {
            result = result + "//" + u.getHost();
            if (u.getPort() != -1) {
                result += ":" + u.getPort();
            }
        }
        result += u.getFile();
        if (u.getRef() != null) {
            result += "#" + u.getRef();
        }
        return result;
    }

    /**
     * Sets the fields of the URL. This is not a public method so that only URLStreamHandlers can modify URL fields. URLs are
     * otherwise constant. REMIND: this method will be moved to URLStreamHandler
     * @param protocol the protocol to use
     * @param host the host name to connecto to
     * @param port the protocol port to connect to
     * @param file the specified file name on that host
     * @param ref the reference
     */
    void set(String protocol, String host, int port, String file, String ref) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.file = file;
        this.ref = ref;
    }

    /** Gets the port number. Returns -1 if the port is not set. */
    final public int getPort() {
        return port;
    }

    /** Gets the protocol name. */
    String getProtocol() {
        return protocol;
    }

    /** Gets the host name. */
    public String getHost() {
        //inetAddress
        return host;
    }

    /** Gets the file name. */
    String getFile() {
        return file;
    }

    /** Gets the ref. */
    String getRef() {
        return ref;
    }

    /**
     * Compares two URLs.
     * @param	obj the URL to compare against.
     * @return	true if and only if they are equal, false otherwise.
     */
    public final boolean equals(Object obj) {
        return (obj instanceof URLString) && sameFile((URLString) obj);
    }

    /** Creates an integer suitable for hash table indexing. */
    public final int hashCode() {
        int inhash = 0;
        if (!host.equals("")) {
            try {
                inhash = InetAddress.getByName(host).hashCode();
            }
            catch (UnknownHostException e) {
            }
        }
        return protocol.hashCode() ^ inhash ^ file.hashCode();
    }

    /**
     * Compares the host components of two URLs.
     * @param h1 the URL of the first host to compare
     * @param h2 the URL of the second host to compare
     * @return	true if and only if they are equal, false otherwise.

     */
    boolean hostsEqual(String h1, String h2) {
        if (h1.equals(h2)) {
            return true;
        }
        // Have to resolve addresses before comparing, otherwise
        // names like tachyon and tachyon.eng would compare different
        try {
            InetAddress a1 = InetAddress.getByName(h1);
            InetAddress a2 = InetAddress.getByName(h2);
            return a1.equals(a2);
        }
        catch (UnknownHostException e) {
        }
        catch (SecurityException e) {
        }
        return false;
    }

    /**
     * Compares two URLs, excluding the "ref" fields: sameFile is true if the true references the same remote object, but
     * not necessarily the same subpiece of that object.
     * @param	other	the URL to compare against.
     * @return	true if and only if they are equal, false otherwise.
     */
    boolean sameFile(URLString other) {
        // AVH: should we not user getPort to compare ports?
        return protocol.equals(other.protocol) && hostsEqual(host, other.host) && (port == other.port) &&
                file.equals(other.file);
    }
}


