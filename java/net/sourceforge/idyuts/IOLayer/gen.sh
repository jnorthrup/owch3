#!/bin/sh
	sed -   \
	-e "s#@XCLASS@#${XCLASS}#g;"\
	-e "s#@XFILTERTYPE@#${XFILTERTYPE}#g;"\
	-e "s#@XADDSOURCE@#${XADDSOURCE}#g;"
