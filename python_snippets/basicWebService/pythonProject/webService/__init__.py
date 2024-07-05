from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/', methods=['GET'])
def home():
    return "Hello, this is a GET request!"

# curl -X POST -H "Content-Type: application/json" -d '{"key": "value"}' http://127.0.0.1:8000/api/data
@app.route('/api/data', methods=['POST'])
def get_data():
    data = request.get_json()
    response = {
        'message': 'POST request received!',
        'data': data
    }
    return jsonify(response)

# curl -X PUT -H "Content-Type: application/json" -d '{"key": "value"}' http://127.0.0.1:8000/api/data
@app.route('/api/data', methods=['PUT'])
def put_data():
    data = request.get_json()
    response = {
        'message': 'PUT request received!',
        'data': data
    }
    return jsonify(response)

# curl -X DELETE -H "Content-Type: application/json" -d '{"key": "value"}' http://127.0.0.1:8000/api/data
@app.route('/api/data', methods=['DELETE'])
def delete_data():
    response = {
        'message': 'DELETE request received!'
    }
    return jsonify(response)

if __name__ == "__main__":
    app.run(debug=True, port=8000)
