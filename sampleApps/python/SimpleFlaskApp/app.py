from flask import Flask, render_template, request, redirect, url_for
import psycopg2

app = Flask(__name__)

# Connect to the database
conn = psycopg2.connect(database="products", user="postgres",
						password="postgres", host="localhost", port="5432")

# create a cursor
cur = conn.cursor()

# if you already have any table or not id doesnt matter this
# will create a products table for you.
cur.execute(
	''' create table if not exists cars (id serial primary key, make varchar(100), model varchar (100), price float);
;''')

# Insert some data into the table
cur.execute(
	'''INSERT INTO cars (make,model,price) VALUES ('Toyota', 'Celica', 10000.99), ('Austin','DB9', 100000.99), ('Honda','Civic', 200000.59);
;''')

# commit the changes
conn.commit()

# close the cursor and connection
cur.close()
conn.close()


@app.route('/')
def index():
	# Connect to the database
	conn = psycopg2.connect(database="products",
							user="postgres",
							password="postgres",
							host="localhost", port="5432")

	# create a cursor
	cur = conn.cursor()

	# Select all products from the table
	cur.execute('''SELECT * FROM cars''')

	# Fetch the data
	data = cur.fetchall()

	# close the cursor and connection
	cur.close()
	conn.close()

	return render_template('index.html', data=data)


@app.route('/create', methods=['POST'])
def create():
	conn = psycopg2.connect(database="products",
							user="postgres",
							password="postgres",
							host="localhost", port="5432")

	cur = conn.cursor()

	# Get the data from the form
	make = request.form['make']
	model = request.form['model']
	price = request.form['price']

	# Insert the data into the table
	cur.execute(
		'''INSERT INTO cars (make,model, price) VALUES (%s,%s, %s)''',
		(make, model,price))

	# commit the changes
	conn.commit()

	# close the cursor and connection
	cur.close()
	conn.close()

	return redirect(url_for('index'))


@app.route('/update', methods=['POST'])
def update():
	conn = psycopg2.connect(database="product",
							user="postgres",
							password="postgres",
							host="localhost", port="5432")

	cur = conn.cursor()

	# Get the data from the form
	make = request.form['make']
	model = request.form['model']
	price = request.form['price']
	id = request.form['id']

	# Update the data in the table
	cur.execute(
		'''UPDATE cars SET make=%s,model=%s,price=%s WHERE id=%s''', (make,model, price, id))

	# commit the changes
	conn.commit()
	return redirect(url_for('index'))


@app.route('/delete', methods=['POST'])
def delete():
	conn = psycopg2.connect (database="products", user="postgres", password="postgres", host="localhost", port="5432")
	cur = conn.cursor()

	# Get the data from the form
	id = request.form['id']

	# Delete the data from the table
	cur.execute('''DELETE FROM cars WHERE id=%s''', (id,))

	# commit the changes
	conn.commit()

	# close the cursor and connection
	cur.close()
	conn.close()

	return redirect(url_for('index'))


if __name__ == '__main__':
	app.run(debug=True)
