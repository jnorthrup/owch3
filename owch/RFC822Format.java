


/**
     address     =  mailbox                      ; one addressee
                 /  group                        ; named list
     addr-spec   =  local-part "@" domain        ; global address
     ALPHA       =  <any ASCII alphabetic character> ; (101-132, 65.- 90.)
                                                 ; (141-172, 97.-122.)
     atom        =  1*<any CHAR except specials, SPACE and CTLs> authentic   =   "From"       ":"   mailbox  ; Single author
                 / ( "Sender"     ":"   mailbox  ; Actual submittor
                     "From"       ":" 1#mailbox) ; Multiple authors
                                                 ;  or not sender
     CHAR        =  <any ASCII character> ; (  0-177,  0.-127.)
     comment     =  "(" *(ctext / quoted-pair / comment) ")"
     CR          =  <ASCII CR, carriage return> ; (     15,      13.)
     CRLF        =  CR LF
     ctext       =  <any CHAR excluding "(",     ; => may be folded
                     ")", "\" & CR, & including
                     linear-white-space> CTL         =  <any ASCII control           ; (  0- 37,  0.- 31.)
                     character and DEL> ; (    177,     127.)
     date        =  1*2DIGIT month 2DIGIT        ; day month year
                                                 ;  e.g. 20 Jun 82
     dates       =   orig-date                   ; Original
                   [ resent-date ] ; Forwarded
     date-time   =  [ day "," ] date time        ; dd mm yy
                                                 ;  hh:mm:ss zzz
     day         =  "Mon"  / "Tue" /  "Wed"  / "Thu"
                 /  "Fri"  / "Sat" /  "Sun"
     delimiters  =  specials / linear-white-space / comment
     destination =  "To"          ":" 1#address  ; Primary
                 /  "Resent-To"   ":" 1#address
                 /  "cc"          ":" 1#address  ; Secondary
                 /  "Resent-cc"   ":" 1#address
                 /  "bcc"         ":"  #address  ; Blind carbon
                 /  "Resent-bcc"  ":"  #address
     DIGIT       =  <any ASCII decimal digit> ; ( 60- 71, 48.- 57.)
     domain      =  sub-domain *("." sub-domain)
     domain-literal =  "[" *(dtext / quoted-pair) "]"
     domain-ref  =  atom                         ; symbolic reference
     dtext       =  <any CHAR excluding "[", ; => may be folded "]", "\" & CR, & including
                     linear-white-space> extension-field =
                   <Any field which is defined in a document
                    published as a formal extension to this
                    specification; none will have names beginning
                    with the string "X-">  Standard for ARPA Internet Text Messages

     field       =  field-name ":" [ field-body ] CRLF
     fields      =    dates                      ; Creation time,
                      source                     ;  author id & one
                    1*destination                ;  address required
                     *optional-field             ;  others optional
     field-body  =  field-body-contents
                    [CRLF LWSP-char field-body] field-body-contents =
                   <the ASCII characters making up the field-body, as
                    defined in the following sections, and consisting
                    of combinations of atom, quoted-string, and
                    specials tokens, or else consisting of texts> field-name  =  1*<any CHAR, excluding CTLs, SPACE, and ":"> group       =  phrase ":" [#mailbox] ";"
     hour        =  2DIGIT ":" 2DIGIT [":" 2DIGIT] ; 00:00:00 - 23:59:59
     HTAB        =  <ASCII HT, horizontal-tab> ; (     11,       9.)
     LF          =  <ASCII LF, linefeed> ; (     12,      10.)
     linear-white-space =  1*([CRLF] LWSP-char)  ; semantics = SPACE
                                                 ; CRLF => folding
     local-part  =  word *("." word)             ; uninterpreted
                                                 ; case-preserved
     LWSP-char   =  SPACE / HTAB                 ; semantics = SPACE
     mailbox     =  addr-spec                    ; simple address
                 /  phrase route-addr            ; name & addr-spec
     message     =  fields *( CRLF *text )       ; Everything after
                                                 ;  first null line
                                                 ;  is message body
     month       =  "Jan"  /  "Feb" /  "Mar"  /  "Apr"
                 /  "May"  /  "Jun" /  "Jul"  /  "Aug"
                 /  "Sep"  /  "Oct" /  "Nov"  /  "Dec"
     msg-id      =  "<" addr-spec ">"            ; Unique message id
     optional-field =
                 /  "Message-ID"        ":"   msg-id
                 /  "Resent-Message-ID" ":"   msg-id
                 /  "In-Reply-To"       ":"  *(phrase / msg-id)
                 /  "References"        ":"  *(phrase / msg-id)
                 /  "Keywords"          ":"  #phrase
                 /  "Subject"           ":"  *text
                 /  "Comments"          ":"  *text
                 /  "Encrypted"         ":" 1#2word
                 /  extension-field              ; To be defined
                 /  user-defined-field           ; May be pre-empted
     orig-date   =  "Date"        ":"   date-time
     originator  =   authentic                   ; authenticated addr
                   [ "Reply-To" ":" 1#address] )
     phrase      =  1*word                       ; Sequence of words

 
     Standard for ARPA Internet Text Messages

     qtext       =  <any CHAR excepting <">,     ; => may be folded
                     "\" & CR, and including
                     linear-white-space> quoted-pair =  "\" CHAR                     ; may quote any char
     quoted-string = <"> *(qtext/quoted-pair) <">; Regular qtext or
                                                 ;   quoted chars.
     received    =  "Received"    ":"            ; one per relay
                       ["from" domain] ; sending host
                       ["by" domain] ; receiving host
                       ["via" atom] ; physical path
                      *("with" atom)             ; link/mail protocol
                       ["id" msg-id] ; receiver msg id
                       ["for" addr-spec] ; initial form
                        ";"    date-time         ; time received

     resent      =   resent-authentic
                   [ "Resent-Reply-To" ":" 1#address] )
     resent-authentic =
                 =   "Resent-From"      ":"   mailbox
                 / ( "Resent-Sender"    ":"   mailbox
                     "Resent-From"      ":" 1#mailbox  )
     resent-date =  "Resent-Date" ":"   date-time
     return      =  "Return-path" ":" route-addr ; return address
     route       =  1#("@" domain) ":"           ; path-relative
     route-addr  =  "<" [route] addr-spec ">"
     source      = [ trace ] ; net traversals
                      originator                 ; original mail
                   [ resent ] ; forwarded
     SPACE       =  <ASCII SP, space> ; (     40,      32.)
     specials    =  "(" / ")" / "<" / ">" / "@"  ; Must be in quoted-
                 /  "," / ";" / ":" / "\" / <"> ;  string, to use
                 /  "." / "[" / "]"              ;  within a word.
     sub-domain  =  domain-ref / domain-literal
     text        =  <any CHAR, including bare    ; => atoms, specials,
                     CR & bare LF, but NOT       ;  comments and
                     including CRLF> ;  quoted-strings are
                                                 ;  NOT recognized.
     time        =  hour zone                    ; ANSI and Military
     trace       =    return                     ; path to sender
                    1*received                   ; receipt tags
     user-defined-field =
                   <Any field which has not been defined
                    in this specification or published as an
                    extension to this specification; names for
                    such fields must be unique and may be
                    pre-empted by published extensions> word        =  atom / quoted-string

 
     Standard for ARPA Internet Text Messages

     zone        =  "UT"  / "GMT"                ; Universal Time
                                                 ; North American : UT
                 /  "EST" / "EDT"                ;  Eastern:  - 5/ - 4
                 /  "CST" / "CDT"                ;  Central:  - 6/ - 5
                 /  "MST" / "MDT"                ;  Mountain: - 7/ - 6
                 /  "PST" / "PDT"                ;  Pacific:  - 8/ - 7
                 /  1ALPHA                       ; Military: Z = UT;
     <"> =  <ASCII quote mark> ; (     42,      34.)
*/


package owch;
import java.io.*;
import java.util.*;
public class  RFC822Format implements Format
{
   
    public     RFC822Format(){};
    public   void read(InputStream is,MetaProperties m)throws IOException
    {
	String line,key,val;
	int col; 
  
	BufferedReader ins
	    = new  BufferedReader(new InputStreamReader(is));
	do{
	    line=ins.readLine();
	    if(line==null)
		return; 
	    col=line.indexOf(':');
	    if(col<1)
		return; 
	    key=line.substring(0,col).trim();
	    val=line.substring(col+1).trim();
	    m.put(key,val);
	}while(true); 
        
    }

    public   void write(OutputStream o,MetaProperties m)  throws IOException
    {
	String line,key;
	for(Iterator i=m.keySet().iterator();i.hasNext();) {
	    
	    key=(String)i.next();
	    line=key.toString()+": "+m.get(key).toString();
	    o.write((line+'\n').getBytes());
	    Env.debug(200,"RFC822Format line saved:"+line);
	}
	o.write('\n');
	o.flush();
    }
}
