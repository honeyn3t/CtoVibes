import os
import sys
import psycopg2

os.add_dll_directory('C:\\Program Files\\IBM\\IBM DATA SERVER DRIVER\\bin')
import ibm_db
from ibm_db import connect
from ibm_db import tables
from ibm_db import exec_immediate
import ibm_db_dbi as db
# DB2 Connectors
def get_connection():
    try:
        connection = db.connect('DATABASE=DEMODB;'
                         'HOSTNAME=localhost;'  # 127.0.0.1 or localhost works if it's local. Remote IP can also be added here
                         'PORT=50000;'
                         'PROTOCOL=TCPIP;'
                         'UID=dbuser;' # DB User name here
                         'PWD=dbpassword;', '', '')  # DB Password name here
        return connection
    except:
        print("no connection: ", ibm_db.conn_errormsg())
        sys.exit(1)

def get_connection2():
    try:
        connection2 = db.connect('DATABASE=DEMODB;'
                         'HOSTNAME=localhost;'  # 127.0.0.1 or localhost works if it's local. Remote IP can also be added here
                         'PORT=50000;'
                         'PROTOCOL=TCPIP;'
                         'UID=dbuser;' # DB User name here
                         'PWD=dbpassword;', '', '')  # DB Password name here
        return connection2
    except:
        print("no connection: ", ibm_db.conn_errormsg())
        sys.exit(1)


# Postgres Connector
def get_postgresconn():
    try:
        connection = psycopg2.connect(database="products_db", user="postgres", password="postgres", host="localhost", port=5432)

        return connection
    except:
        print("no connection: ", psycopg2.Warning)
        sys.exit(1)

# Postgres Query
def run_postgresq(query):
    conn = get_postgresconn()
    cursor = conn.cursor()
    cursor.execute(query)
    # Fetch all rows from database
    record = cursor.fetchall()

    print("Data from Database:- ", record)

# DB2 Query
def run_query(query):
    conn = get_connection2()
    query_stmt = ibm_db.prepare(conn, query)
    ibm_db.execute(query_stmt)
    data = ibm_db.fetch_both(query_stmt)
    while data != False:
        print ("" , data["name"])
        data = ibm_db.fetch_both(query_stmt)
#   print(t)

# This requires a DB2 instance called db2inst1 with a table called Table2
db2_query_str = "SELECT * FROM db2inst1.Table2"
run_query(db2_query_str)

# This requires a Postgres instance with a products table in a database called products_db
postgres_query = "SELECT * from public.products"
run_postgresq(postgres_query)
