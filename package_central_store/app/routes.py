from app import app
from flask import render_template, request

products = {"kimono": [
                        {"name": "White kimono", "price": 70, "productID": 1, "image": "white_kimono.jpeg"},
                        {"name": "Blue kimono", "price": 65, "productID": 2, "image": "blue_kimono.jpg"},
                        {"name": "Black kimono", "price": 60, "productID": 3, "image": "black_kimono.jpeg"}
                    ],
            "belt": [
                        {"name": "White belt", "price": 15, "productID": 4, "image": "white_belt.jpeg"},
                        {"name": "Blue belt", "price": 20, "productID": 5, "image": "blue_belt.jpeg"},
                        {"name": "Purple belt", "price": 30, "productID": 6, "image": "purple_belt.jpeg"},
                        {"name": "Brown belt", "price": 45, "productID": 7, "image": "brown_belt.jpeg"},
                        {"name": "Black belt", "price": 65, "productID": 8, "image": "black_belt.jpeg"},
                    ],
            "tatami": [
                        {"name": "Red-Green tatami", "price": 35, "productID": 9, "image": "red_green_tatami.jpeg"},
                        {"name": "Blue-Yellow tatami", "price": 35, "productID": 10, "image": "blue_yellow_tatami.jpeg"},
                        {"name": "Blue-Red tatami", "price": 35, "productID": 11, "image": "blue_red_tatami.jpeg"}
                    ]
                }

@app.route('/')
@app.route('/index')
@app.route('/home')
def index():
    return render_template('index.html', active_page="home", title='Home')

@app.route('/store')
def store():
    product_type = request.args.get("product_type")

    if product_type.lower() not in ["kimono", "belt", "tatami"]:
        return render_template("store.html", active_page="error", title="Store", error="invalid product type")

    print("product_type: ", product_type.lower())
    print("products: ", products[product_type.lower()])

    return render_template("store.html", title="Store", active_page="store_" + product_type.lower(), products=products[product_type.lower()])



