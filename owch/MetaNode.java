package owch;

//www.iconcomp.com/papers/comp/comp_40.html

/*

<HTML>
<HEAD>
<TITLE> Design Pattern [23] : Name Objects</TITLE>
</HEAD>
<BODY BGCOLOR=#ffffff>
<A NAME=HEADING39>
<A HREF="comp_39.html"><IMG SRC="prev_1.gif" ALIGN=TOP></A><A HREF="comp_1.html"><IMG SRC="top_1.gif" ALIGN=TOP></A><A HREF="comp_41.html"><IMG SRC="next_1.gif" ALIGN=TOP></A><P>
Section 2:  A Framework<P>
<H1>Design Pattern [23] : Name Objects<P></H1>
<HR>
 Problem:<P>
<UL>
<LI>A client often needs to refer to another object contained within a composite<P>
<LI>The simplest way to do this is to acquire and hold a direct reference<P>
<LI>This causes <EM>Identity Coupling</EM> to a specific object rather than to a <EM>Role</EM><BR><IMG SRC="comp_AFrame_5.gif"><BR>
<P>
</UL>
 Solution:<P>
<UL>
<LI>Synthesize a "Name" or "Role" object to refer to the Role within the composite<P>
<LI>Have the client use this name object as a handle to the role-player<P>
</UL>

<HR>
<ADDRESS>A Comparison of OOA and OOD Methods - Copyright 1995 <A HREF="http://www.iconcomp.com">ICON Computing, Inc.</A></ADDRESS>
<HR>
<A HREF="comp_39.html"><IMG SRC="prev_1.gif" ALIGN=TOP></A><A HREF="comp_1.html"><IMG SRC="top_1.gif" ALIGN=TOP></A><A HREF="comp_41.html"><IMG SRC="next_1.gif" ALIGN=TOP></A><P>
</BODY>
</HTML>
*/
public interface MetaNode
    {
        public String getNodeName();
        public String getURL();
    }

