# xxe_ssrf_challenge
A simple challenge that require to exploit an XXE vulnerability to perform an SSRF attack.

## Prerequisites
- docker (see: [https://www.docker.com/get-started](https://www.docker.com/get-started))
- docker-compose ( see: [https://docs.docker.com/compose/](https://docs.docker.com/compose/))

## Start Challenge
To run the challenge environment run the command:
```sh
docker-compose up
```
You can connect to central_store from [http://localhost:5000/](http://localhost:5000/), to xml_store1 from [http://localhost:5001/](http://localhost:5001/) and to xml_store2 from [http://localhost:5002/](http://localhost:5002/).
The flag can be retrieved by admin_app.

## Environment
The docker-compose.yml file specifies the network and the docker istances tha will be created by the above command.
```
version: "3.7"

services:
  central_store:
    build: ./package_central_store/
    ports:
      - 5000:8080
    networks:
      default:
        ipv4_address: 172.200.1.1

  xml_store1:
    build: ./package_xml_store1/
    ports:
      - 5001:8080
    networks:
      default:
        ipv4_address: 172.200.1.11

  xml_store2:
    build: ./package_xml_store2/
    ports:
      - 5002:8080
    networks:
      default:
        ipv4_address: 172.200.1.12

  admin_app:
    build: ./package_admin_app/
    networks:
      default:
        ipv4_address: 172.200.1.21


networks:
  default:
    ipam:
      driver: default
      config:
        - subnet: 172.200.0.0/16

```
The network has the ip 172.200.0.0/16 and the name "default".  
It contains 4 servers(services), each of which running a different application on the port 8080.
Central store servers consists of an online store realized using flask, it has ip 172.200.1.1 and its 8080 port is mapped with 5000 port of localhost.
XML store 1 and XML store 2 are 2 java servers that check the stocking of some product in their storage, they have ip 172.200.1.11 and 172.200.1.12 and their 8080 ports are mapped with 5001 and 5002 ports of localhost.
Admin app is a flask application that run on the ip 172.200.1.21 and is the target server.
