#!/bin/bash
set -x
appplicationCount=`sfctl application list | grep JenkinsSF | wc -l`
if [[ "$appplicationCount" -eq "0" ]];then
    echo "Deploying Application"
    /bin/bash Scripts/install.sh
else    
    echo "CleanUp: Undeploying existing app"
    /bin/bash Scripts/uninstall.sh
    echo "Redeploying"
    /bin/bash Scripts/install.sh
fi
