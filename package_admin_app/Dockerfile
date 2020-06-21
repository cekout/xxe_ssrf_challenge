FROM ubuntu:18.04
#requirements
RUN apt-get update && apt-get install -y python3 python3-venv
RUN apt-get install -y python3-pip locales
#need to install locales and set LANG and LC_ALL because ubuntu has problem with characters encoding (don't know why)
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
#app
USER root
RUN useradd -U -m -s /bin/bash bjj_store && mkdir /home/bjj_store/admin_app
COPY ./admin_app.py /home/bjj_store/admin_app/admin_app.py
COPY ./requirements.txt /home/bjj_store/admin_app/requirements.txt
COPY env/ /home/bjj_store/admin_app/env
COPY app/ /home/bjj_store/admin_app/app
RUN chown -R bjj_store:bjj_store /home/bjj_store
USER bjj_store
RUN python3 -m venv /home/bjj_store/admin_app/env \
 && . /home/bjj_store/admin_app/env/bin/activate \
 && pip3 install -r /home/bjj_store/admin_app/requirements.txt
WORKDIR /home/bjj_store/admin_app
CMD ./env/bin/flask run --host=0.0.0.0 --port=8080
