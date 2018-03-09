#!/bin/bash
set -x
protocol='http'
clientkey=0
clientCert=0
certArgs=''
ip=''
port=''

profileToPick=''

if [ $# -eq 0 ]; then
    profileToPick=local
elif [ "$1" = "local" ]; then
    profileToPick=local
elif [ "$1" = "cloud" ]; then
    profileToPick=cloud    
fi

isJqInstalled=`which jq`
if [ "$isJqInstalled" = "" ]; then
    echo -e "\njq utility is not installed on your machine.\nIf you are in root mode, it will be installed automatically.\n"
    if [[ "$EUID" = "0" ]]; then
        echo -e "Running in root mode, the utility will be installed."
        sudo apt-get update
        sudo apt-get install jq
    else
        echo -e "Application not running in root mode. Please run the following commands to install the utility, before you try to proceed.\nsudo apt-get update\nsudo apt-get install jq\n"
        exit 1
    fi
fi 

if [ "$profileToPick" = "local" ]; then
    echo "Reading local profile."
    ip=`cat PublishProfiles/Local.json | jq '.ClusterConnectParameters.ConnectionIPOrURL' | cut -d"\"" -f2`
    port=`cat PublishProfiles/Local.json | jq '.ClusterConnectParameters.ConnectionPort' | cut -d"\"" -f2`
    clientkey=`cat PublishProfiles/Local.json | jq '.ClusterConnectParameters.ClientKey' | cut -d"\"" -f2`
    clientCert=`cat PublishProfiles/Local.json | jq '.ClusterConnectParameters.ClientCert' | cut -d"\"" -f2`
elif [ "$profileToPick" = "cloud" ]; then
    echo "Reading cloud profile."
    ip=`cat PublishProfiles/Cloud.json | jq '.ClusterConnectParameters.ConnectionIPOrURL' | cut -d"\"" -f2`
    port=`cat PublishProfiles/Cloud.json | jq '.ClusterConnectParameters.ConnectionPort' | cut -d"\"" -f2`
    clientkey=`cat PublishProfiles/Cloud.json | jq '.ClusterConnectParameters.ClientKey' | cut -d"\"" -f2`
    clientCert=`cat PublishProfiles/Cloud.json | jq '.ClusterConnectParameters.ClientCert' | cut -d"\"" -f2`
fi

if [ "$clientkey" != "0" ] && [ "$clientkey" != "" ] && [ "$clientCert" != "0" ] && [ "$clientCert" != "" ]; then
    protocol='https'
    certArgs="--key $clientkey --cert $clientCert --no-verify"
fi

url=${protocol}://${ip}:${port}
if [ "${ip}" = "" ] || [ "${port}" = "" ]; then
    echo "IP and Port has to be non-empty."
    exit 1
fi
#In case of local cluster, check if the cluster is up, if not, then try to bring it up first.
if [[ $url == *"localhost"* ]]; then
    echo "For local cluster, check if the cluster is already up or not and accordingly set up the cluster."
    if [ `systemctl | grep -i fabric | wc -l` == 1 ]; then
        echo "Local cluster is up, now will try to connect to the cluster."
    else 
        echo "cluster is not up, set up the cluster first."
        if [[ $EUID -ne 0 ]]; then
            echo "Cluster-setup script must be run as root, please open your IDE as root to set-up the local cluster." 1>&2
            exit 1
        fi
        sudo /opt/microsoft/sdk/servicefabric/common/clustersetup/devclustersetup.sh
        if [ $? -ne 0 ]; then
            echo "Dev cluster set-up failed."
            exit 1
        fi
        echo -n "Setting up cluster."
        n=`ps -eaf | grep -i "filestoreservice" | grep -v grep | wc -l`
        until [ $n -eq 3 ]; do
            echo -n "."
            n=`ps -eaf | grep -i "filestoreservice" | grep -v grep | wc -l`
            sleep 30
        done
    fi
fi

echo "Connecting to $url"
sfctl cluster select --endpoint $url $certArgs
if [ $? != 0 ]; then
    echo "Something went wrong while connecting to cluster."
    exit 1
fi
echo "Connected to:$url"
