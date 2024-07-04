# Installing DB2 11.5.4 on a Redhat 9.4 box
The following are the steps to take to install redhat 11.5.4 on a redhat server, and then to connect to that server from another machine. Note that there are 3 users that are required for this to happen. We also provide a companion code snipet that will provide a connection from a remote server to the db2 server.
## On (soon to be) DB2 Server
Commands to Install DB2

adduser db2inst1

passwd db2inst1

<password_for_db2inst1>

usermod -a -G root db2inst1

vi /etc/sudoers

add line to bottom of file db2inst1 ALL= (ALL:ALL) ALL

useradd db2fenc1

passwd db2fenc1

<password_for_dbfence1>

sudo useradd db2user 

sudo passwd db2user

<password_for_db2user>

mkdir db2v11

chmod 777 db2v11rm db2v

<copy file to folder>

cp gzip -dv DB2S_11.5.4_MPML.tar.gz

chmod 755 DB2S_11.5.4_MPML.tar

tar -xvf DB2S_11.5.4_MPML.tar

chmod 777 server_dec

cd server_dec

./db2prereqcheck -v 11.5.4

Run the command: ./db2_install -f sysreq

Follow the steps

1 yes

2 yes

3 SERVER

4 no for DB2 pureScale

cd /opt/ibm/db2/V11.5/bin

.cd bin/db2val

db2start

./instance/db2ilistls

Create an instance

cd 

./instance/db2icrt db2fenc1 db2inst1

change to instance user

su - db2inst1

db2start

./instance/db2ilistls

 ps -ef | grep db2sysc - check if instance is up and running

## Create a DB

db2 create database demodb

db2 connect to demodb

db2 Grant connect privilege db2 grant connect on database to user db2user 

db2 grant createtab, bindadd, connect on database to user db2user


## Remote connections
On remote client run

db2 catalog tcpip node rdemodb remote <redhat9 server ip> server 50000

db2 catalog database demodb as ademodb at node rdemodb 

db2 connect to ademodb user db2user using <password_for_db2user>

