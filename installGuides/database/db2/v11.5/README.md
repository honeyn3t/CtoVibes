# Installing DB2 11.5.4 on a Redhat 9.4 box
The following are the steps to take to install redhat 11.5.4 on a redhat server, and then to connect to that server from another machine. Note that there are 3 users that are required for this to happen. We also provide a companion code snipet that will provide a connection from a remote server to the db2 server.

## Prerequisite files

Download the IBM DB2 software:
- [DB2 Software](https://www.ibm.com/products/db2-database/pricing)

## Create required users
Create instance userID
```
adduser db2inst1
passwd db2inst1
_<password_for_db2inst1>_
```
Change user permissions 
```
usermod -a -G root db2inst1
```
Open sudoer file to add new user
```
vi /etc/sudoers
```
add line to bottom of file db2inst1 ALL= (ALL:ALL) ALL

Create fenceID
```
useradd db2fenc1

passwd db2fenc1

_<password_for_dbfence1>_
```
Create a DB user for running quierie and accessing DB
```
sudo useradd db2user 

sudo passwd db2user

_<password_for_db2user>_
```

## Prepare the Install files

Create a new directory. Full directory access might not be required. This is only for demo purposes and is not for prduction
```
mkdir db2v11

chmod 777 db2v11
```
Your install software will come in tar.gz format. Copy your install files to the newlly created folder
```
cp Filename.tar.gz db2v11
```
You will need to untar this file with the following command:
```
tar -vxf db2v11/Filename.tar.gz
```
This should create a new directory called `server` or `server_dec``. This is dependant on what you are installing. Community edition or Advanced Enterprise

## Prerequisite check
See the IBM [prequisite check page](https://www.ibm.com/docs/en/db2/11.5?topic=commands-db2prereqcheck-check-installation-prerequisites) for extra guidance.
Run the following to change into the folder
```
cd server_dec
./db2prereqcheck
```
## Installing DB2

Run the command: 
```
./db2_install -f sysreq
```
Follow the steps
1. Accept Licence
```
yes
```
2. Accept default directory for install - /opt/ibm/db2/V11.5
```
yes
```
Select one of the options to install DB2 products. 
```
1. SERVER
2. CONSV
3. CLIENT
4. RTCL
```
3. Select server option


4. Do you want to install the DB2 pureScale Feature? [yes/no]
```
no
```
At this point DB installation will begin and the output on screen should show tasks being performed

## Validate install
Run the following to validate the install:
```
/opt/ibm/db2/V11.5/bin/db2val
```

## Start the DB2 database
To start the DB2 database run the following
```
db2start
```

## Create an instance

./instance/db2icrt db2fenc1 db2inst1

change to instance user

su - db2inst1

db2start

./instance/db2ilistls

 ps -ef | grep db2sysc - check if instance is up and running

## Create a DB
```
db2 create database demodb

db2 connect to demodb

db2 Grant connect privilege db2 grant connect on database to user db2user 

db2 grant createtab, bindadd, connect on database to user db2user
```

## Remote connections
On remote client run

db2 catalog tcpip node rdemodb remote <redhat9 server ip> server 50000

db2 catalog database demodb as ademodb at node rdemodb 

db2 connect to ademodb user db2user using <password_for_db2user>

