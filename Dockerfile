FROM tomcat:9

COPY /target/templateSpringMVC-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/springmvc.war

#Enter as root user
USER root

#Install ssh server to beused for remote deployments
RUN apt-get update && apt-get install -y openssh-server

#Install curL
RUN apt-get update && apt-get install -y curl
CMD /bin/bash

#Install vim editor
RUN apt install --assume-yes vim

#create user deployer with pass 'deployer'
RUN useradd -m deployer -p deployer

#give access for deployer user, to tomcat webapps folder
RUN chown -R deployer:deployer webapps

#create user home dir
#RUN mkdr /home/deployer dir is create with the above command to create user normally


RUN service ssh start

#Install and start Graylog Sidecar to be used to send log files
#----------------------------------------------------------------
RUN wget https://packages.graylog2.org/repo/packages/graylog-sidecar-repository_1-2_all.deb
RUN dpkg -i graylog-sidecar-repository_1-2_all.deb
RUN apt-get update && apt-get install graylog-sidecar
RUN graylog-sidecar -service install
COPY sidecar.yml /etc/graylog/sidecar/sidecar.yml    
RUN systemctl enable graylog-sidecar
#RUN service graylog-sidecar start

#Install Filebeat
RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-8.3.2-amd64.deb
RUN dpkg -i filebeat-8.3.2-amd64.deb
#RUN service filebeat start
#----------------------------------------------------------------

EXPOSE 8080

#ENTRYPOINT service graylog-sidecar start
#RUN service filebeat start
#RUN /usr/bin/graylog-sidecar

#start ssh server when comtainer is launched
ENTRYPOINT service ssh restart && bash
ENTRYPOINT /usr/local/tomcat/bin/catalina.sh run