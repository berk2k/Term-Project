from flask import Flask, render_template, request, redirect, url_for, jsonify, session, flash
import requests
from flask_cors import CORS

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Set a secret key for session management
CORS(app, resources={r"/api/*": {"origins": "http://127.0.0.1:5000"}})  # Enable CORS for all routes

users = [
    {"id": 1, "email": "user1@example.com", "password": "password1", "role": "Admin", "username": "Username1"},
    {"id": 2, "email": "user2@example.com", "password": "password2", "username": "Username2"}
]

@app.route('/')
def home():
    user_id = session.get('user_id')
    if 'user_id' in session:
        user_id = session['user_id']
        """
        user = next((user for user in users if user['id'] == user_id), None
        if user:
        """
        username = user_id = session.get('user_name')
        role = "admin"
        return render_template('home.html', username=username, role=role)

    return redirect(url_for('login'))

"""
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        for user in users:
            if user['email'] == email and user['password'] == password:
                # Store user_id in session
                session['user_id'] = user['id']
                # Redirect to home page
                return redirect(url_for('home'))
        return jsonify({'error': 'Invalid credentials'}), 401  # Unauthorized status code
    return render_template('login.html')
"""

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        api_url = 'https://localhost:7001/api/Login/LoginForStaff'
        payload = {'email': email, 'password': password}
        #response = requests.get('https://localhost:7001/api/Login/LoginForStaff', verify=False)
        response = requests.post('https://localhost:7001/api/Login/LoginForStaff', verify=False, json=payload)
        if response.status_code == 200:
            user_data = response.json()['result']['apiUser']
            user_id = user_data['id']
            user_name = user_data['name']
            user_role = user_data['role']

            print(str(user_data))
            session['user_id'] = user_id
            #session['user_info'] = user_data
            session['user_name'] = user_name

            return redirect(url_for('home'))
        
        return jsonify({'error': 'Invalid credentials'}), 401 
    
    return render_template('login.html')

@app.route('/get_users',methods=['GET', 'POST'])
def get_users():
    if request.method == 'GET':
        response = requests.get('https://localhost:7001/api/UserProfile/GetAllUsers',verify=False,)
        if response.status_code == 200:
            users = response.json()
            print(str(users))
            return jsonify(users), 200
    
@app.route('/register', methods=['POST'])
def register():
    email = request.form.get('email')
    password = request.form.get('password')
    confirm_password = request.form.get('confirmPassword')

    if password != confirm_password:
        return jsonify({'success': False, 'message': 'Password does not match confirmation'})
    
    return jsonify({'success': True, 'message': 'Registration successful'})


@app.route('/logout', methods=['GET'])
def logout():
    # Clear the user session
    session.pop('user_id', None)
    return redirect(url_for('home'))

# Sample customers data
customers = [
    {"id": 1, "name": "John Doe"},
    {"id": 2, "name": "Jane Smith"},
    {"id": 3, "name": "Alice Johnson"}
]

# Sample staff data
staff_members = [
    {"id": 1, "name": "John Doe", "email": "john@example.com", "role": "Admin"},
    {"id": 2, "name": "Jane Smith", "email": "jane@example.com", "role": "Staff"}
]

@app.route('/staff')
def staff():
    return render_template('staff.html', staff_members=staff_members)


@app.route('/addstaff', methods=['GET', 'POST'])
def addstaff():
    if request.method == 'POST':
        # Get data from the form submission
        name = request.form['name']
        email = request.form['email']
        role = request.form['role']
        password = request.form['password']

        staff_data = {'email': email, 'name': name, 'password': password, 'role': role}
        print(staff_data)
        new_staff_member = {"id": 3, "name": name, "email": email, "role": role}
        
        api_url = 'https://localhost:7001/api/VetStaff/CreateStaff'
        response = requests.post(api_url, json=staff_data, verify=False)
        if response.status_code == 200:
            staff_members.append(new_staff_member)
            return render_template('home.html')
        else:
            return render_template('addstaff.html')
        
    return render_template('addstaff.html')

@app.route('/addappointment')
def addappointment():
    return render_template('addappointment.html')


@app.route('/message', methods=['GET', 'POST'])
def message():
    """
    if request.method == 'POST':
        message = request.form['message']
        username = request.form['customer']
    
        user_id_response = requests.get('https://localhost:7001/api/UserProfile/GetUserIdByUserName', verify=False, params={'userName': username})
        if user_id_response.status_code == 200:
            # Print the content of the response
            print(user_id_response.text)
        else:
            # Print an error message if the request was not successful
            print(f"Error: {user_id_response.status_code}")
        
        print("Message: "+ message)  
        print("Username: "+ username)
        if user_id_response.status_code == 200:
            api_url = 'https://localhost:7001/api/Notification/SendNotification'
            payload = {'userId': user_id_response, 'message': message}
            #response = requests.get('https://localhost:7001/api/Login/LoginForStaff', verify=False)
            #response = requests.post(api_url, verify=False, json=payload)
            """       
    return render_template('message.html', customers=customers)

# Sample items data
items = [
    {"id": 1, "name": "Medicine A","quantity": 1},
    {"id": 2, "name": "Medicine B","quantity": 1},
    {"id": 3, "name": "Vitamin C","quantity": 1}
]

# Sample appointments data
appointments = [
    {"date": "15-03-2024", "time": "09:00", "customer": "John Doe", "reason": "Routine checkup"},
    {"date": "16-03-2024", "time": "10:30", "customer": "Jane Smith", "reason": "Vaccination"},
    {"date": "17-03-2024", "time": "13:45", "customer": "Alice Johnson", "reason": "Emergency"}
]

@app.route('/appointments')
def view_appointments():
    return render_template('appointments.html', appointments=appointments)


@app.route('/stock', methods=['GET', 'POST'])
def stock():
    if request.method == 'POST':
        item_id = int(request.form['item_id'])
        action = request.form['action']
        for item in items:
            if item['id'] == item_id:
                if action == 'increase':
                    item['quantity'] += 1
                elif action == 'decrease':
                    item['quantity'] -= 1
                break

        return jsonify({'success': True, 'message': 'Stock adjusted successfully'})

    return render_template('stock.html', items=items)

if __name__ == '__main__':
    app.run(debug=True)