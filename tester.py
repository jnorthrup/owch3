


agents=[
    [
    "Domain","Cheetah.Domain -name default -HostPort 2112"
    ], [
    "GateKeeper","Cheetah.GateKeeper -name GateKeeper -HostPort 8080"
    ], [
    "Deploy-1","Cheetah.Deploy -name deploy_1"
    ], [
    "QUICKSTART1","Cheetah.WebPage -name quickstart -Resource /bookmark.html -Content-Type text/html"
    ], [
    "clone jarfile","Cheetah.WebPage -name jarfile    -Resource /rnodi_owch.jar -Clone deploy_1"
    ],
    ]

import os
for i in agents:
    cmdline="xterm  -title "+`i[0]`+" -sl 5000 -sb -rightbar -e "+ "/opt/jdk/bin/java -classpath . "+i[1] +"&" 
    os.system(cmdline) 
