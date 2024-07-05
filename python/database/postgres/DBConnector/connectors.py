import os
import sys
import psycopg2

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



# This requires a Postgres instance with a products table in a database called products_db
postgres_query = "SELECT * from public.products"
run_postgresq(postgres_query)
