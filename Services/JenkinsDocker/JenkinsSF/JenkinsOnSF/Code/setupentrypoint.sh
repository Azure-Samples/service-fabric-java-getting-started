#!/bin/bash -e

user=jenkins
group=jenkins
uid=1003
gid=1003
sudo groupadd -g ${gid} ${group}
sudo useradd  -u ${uid} -g ${gid} ${user}
sudo mkdir -p /mnt/docker_dump/jenkins_home
sudo chown -R jenkins:jenkins /mnt/docker_dump/jenkins_home

sudo apt-get install -qqy cifs-utils
sudo mount -t cifs //teststoragess1.file.core.windows.net/tempfileshare /mnt/docker_dump/jenkins_home -o vers=3.0,username=teststoragess1,password=NgYbDlXf3VPZBRUak3VVmU/2eUVVpTjp4DSpgQTanXGNRQyKMWUAmdCoWmwDJZpZoxQk724eqHBhzRQkEUvc3w==,dir_mode=0777,file_mode=0777,uid=${uid},gid=${gid}


