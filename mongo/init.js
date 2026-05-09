db = db.getSiblingDB('retail_db');

db.items.insertMany([
    { "_id": ObjectId("6640000000000000000000a1"), "name": "Laptop",       "price": 1200.00, "type": "NON_GROCERY", "description": "High performance laptop" },
    { "_id": ObjectId("6640000000000000000000a2"), "name": "T-Shirt",      "price": 25.00,   "type": "NON_GROCERY", "description": "Cotton t-shirt" },
    { "_id": ObjectId("6640000000000000000000a3"), "name": "Headphones",   "price": 150.00,  "type": "NON_GROCERY", "description": "Noise cancelling headphones" },
    { "_id": ObjectId("6640000000000000000000a4"), "name": "Sneakers",     "price": 90.00,   "type": "NON_GROCERY", "description": "Running sneakers" },
    { "_id": ObjectId("6640000000000000000000a5"), "name": "Desk Lamp",    "price": 45.00,   "type": "NON_GROCERY", "description": "LED desk lamp" },
    { "_id": ObjectId("6640000000000000000000a6"), "name": "Milk",         "price": 2.50,    "type": "GROCERY",     "description": "Full fat milk 1L" },
    { "_id": ObjectId("6640000000000000000000a7"), "name": "Bread",        "price": 3.00,    "type": "GROCERY",     "description": "Whole wheat bread" },
    { "_id": ObjectId("6640000000000000000000a8"), "name": "Eggs",         "price": 5.00,    "type": "GROCERY",     "description": "Free range eggs x12" },
    { "_id": ObjectId("6640000000000000000000a9"), "name": "Orange Juice", "price": 4.00,    "type": "GROCERY",     "description": "Fresh squeezed orange juice" },
    { "_id": ObjectId("6640000000000000000000aa"), "name": "Rice",         "price": 6.00,    "type": "GROCERY",     "description": "Basmati rice 1kg" }
]);
