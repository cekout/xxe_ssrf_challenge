FROM ubuntu:18.04
#requirements
RUN apt-get update && apt-get install -y default-jdk
#app
USER root
RUN useradd -U -m -s /bin/bash bjj_store && mkdir /home/bjj_store/xml_store
COPY app/ /home/bjj_store/xml_store/app
RUN chown -R bjj_store:bjj_store /home/bjj_store
USER bjj_store
WORKDIR /home/bjj_store/xml_store/app
RUN javac JavaHTTPServer.java
CMD java JavaHTTPServer
