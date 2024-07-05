import os
import sys

os.add_dll_directory('C:\Program Files\IBM\SQLLIB\BIN') #This is the location of the DB2 Drivers - the ibm_db is really just a wrapper, so you might need to actually point to the drivers
import ibm_db
from ibm_db import connect
from ibm_db import tables
from ibm_db import exec_immediate
import ibm_db_dbi as db

def get_connection():
    try:
        connection = db.connect('DATABASE=DEMODB;'
                         'HOSTNAME=<db2ip>;'  
                         'PORT=50000;'
                         'PROTOCOL=TCPIP;'
                         'UID=db2user;'
                         'PWD=<db2userpasswd>;', '', '')
        return connection
    except:
        print("no connection: ", ibm_db.conn_errormsg())
        sys.exit(1)

def get_connection2():
    try:
        connection2 = ibm_db.connect('DATABASE=DEMODB;'
                         'HOSTNAME=<db2ip>;'  # 127.0.0.1 or localhost works if it's local
                         'PORT=50000;'
                         'PROTOCOL=TCPIP;'
                         'UID=db2user;'
                         'PWD=<db2userpasswd>;', '', '')
        return connection2
    except:
        print("no connection: ", ibm_db.conn_errormsg())
        sys.exit(1)


#On the database I've created a very simple table called Table2. This table has one column called 'name' which is a char type.

def run_query(query):
    conn = get_connection2()
    query_stmt = ibm_db.prepare(conn, query_str)
    ibm_db.execute(query_stmt)
    data = ibm_db.fetch_both(query_stmt)
    while data != False:
        print ("" , data["name"])
        data = ibm_db.fetch_both(query_stmt)

query_str = "SELECT * FROM db2inst1.Table2"
run_query(query_str)