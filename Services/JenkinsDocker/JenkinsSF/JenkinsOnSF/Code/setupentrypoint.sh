#!/bin/bash -e

isJenkinsPresent=`cat /etc/passwd | grep jenkins | wc -l`
echo "isJenkinsPresent=$isJenkinsPresent"
isAlreadyMounted=`cat /etc/mtab | grep /mnt/docker_dump/jenkins_home | wc -l`
echo "isAlreadyMounted=$isAlreadyMounted"
mountPoint="/mnt/docker_dump/jenkins_home"
fileShareConnectionOptionString="[FILE_SHARE_CONNECT_OPTIONS_STRING]"
remoteFileShareLocation="[REMOTE_FILE_SHARE_LOCATION]"

if (( isJenkinsPresent == 0 && isAlreadyMounted == 0 ));then 
    echo "inside if - new set-up. Create jenkins user and mount remote storage."
    user=jenkins
    group=jenkins
    uid=1010
    gid=1010
    sudo groupadd -g ${gid} ${group}
    sudo useradd  -u ${uid} -g ${gid} ${user}
    echo "jenkins user created"
    sudo mkdir -p ${mountPoint}
    sudo chown -R jenkins:jenkins ${mountPoint}
    echo "mount location created: ${mountPoint}"
    sudo apt-get install -qqy cifs-utils
    sudo mount -t cifs ${remoteFileShareLocation} ${mountPoint} -o ${fileShareConnectionOptionString},uid=${uid},gid=${gid}
    echo "mounted the remote storage at: ${mountPoint}"
elif (( isAlreadyMounted == 0 ));then 
    echo "Jenkins user present. Need to mount."   
    uid=`id -u jenkins`
    gid=`id -g jenkins`
    echo "Jenkins user is present with uid=${uid} and gid=${gid}."
    sudo mkdir -p ${mountPoint}
    sudo chown -R jenkins:jenkins ${mountPoint}
    echo "mount location created: ${mountPoint}"
    sudo apt-get install -qqy cifs-utils
    sudo mount -t cifs ${remoteFileShareLocation} ${mountPoint} -o ${fileShareConnectionOptionString},uid=${uid},gid=${gid}
    echo "mounted the remote storage at: ${mountPoint}"    
else 
    echo "already set-up, do nothing"
fi

