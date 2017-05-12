#!/bin/bash -e
isJenkinsPresent=`cat /etc/passwd | grep jenkins | wc -l`
echo "isJenkinsPresent=$isJenkinsPresent"
isAlreadyMounted=`cat /etc/mtab | grep /mnt/docker_dump/jenkins_home | wc -l`
echo "isAlreadyMounted=$isAlreadyMounted"
if (( isJenkinsPresent == 0 && isAlreadyMounted == 0 ));then 
    echo "inside if - new set-up. Create jenkins user and mount remote storage."
    user=jenkins
    group=jenkins
    uid=1010
    gid=1010
    mountPoint="/mnt/docker_dump/jenkins_home"
    sudo groupadd -g ${gid} ${group}
    sudo useradd  -u ${uid} -g ${gid} ${user}
    echo "jenkins user created"
    sudo mkdir -p ${mountPoint}
    sudo chown -R jenkins:jenkins ${mountPoint}
    echo "mount location created: ${mountPoint}"
    sudo apt-get install -qqy cifs-utils
    sudo mount -t cifs //teststoragess1.file.core.windows.net/tempfileshare ${mountPoint} -o vers=3.0,username=teststoragess1,password=NgYbDlXf3VPZBRUak3VVmU/2eUVVpTjp4DSpgQTanXGNRQyKMWUAmdCoWmwDJZpZoxQk724eqHBhzRQkEUvc3w==,dir_mode=0777,file_mode=0777,uid=${uid},gid=${gid}
    echo "mounted the remote storage at: ${mountPoint}"
else 
    echo "already set-up, do nothing"
fi

