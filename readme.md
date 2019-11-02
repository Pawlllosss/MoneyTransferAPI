## Description

Api consists of client service, account service and account operations service. Each client can have multiple accounts.
On accounts following operations can be performed: withdraw, deposit and transfer between accounts.


## Stack

   * Java 9
   * Spark Java
   * Guice
   * JUnit 5
   * Mockito
   * Hamcrest
   * REST Assured

## Assumptions

* No currency conversion
* Api starts on port 4567

## How to run

Run tests:
```
./gradlew test
```

Run API:
```
./gradlew run
```

## Endpoints

Usage of endpoints is shown in Postman script contained in `/doc` folder.

**Get all clients**
----
  Returns json data about all clients.

* **URL**

  /client

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200 <br />
  *  **Content:** 
    ```
    [
      {
          "id": 1,
          "firstName": "Homer",
          "surname": "Simpson",
          "accounts": []
      }
    ]
    ```

**Get client by id**
----
  Returns json data about client with given id.

* **URL**

  /client/:id

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
  {
      "id": 1,
      "firstName": "Homer",
      "surname": "Simpson",
      "accounts": []
  }
    ```
 
* **Error response:**

  * **Code:** 404
  * **Content:** 
  ```
  {
      "message": "Client with the following id does not exist: 2"
  }
  ```

**Create client**
----
  Creates client and return information about him.

* **URL**

  /client

* **Method:**

  `POST`

* **Success Response:**

  * **Code:** 201
  *  **Content:** 
    ```
    {
        "id": 1,
        "firstName": "Homer",
        "surname": "Simpson",
        "accounts": []
    }
    ```
 
* **Sample request body:**

  ```
    {
        "firstName": "Homer",
        "surname": "Simpson"
    }
  ```
  
  **Update client**
----
  Update client with given id and return information about him.

* **URL**

  /client/:id

* **Method:**

  `PUT`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
    {
        "id": 1,
        "firstName": "Bart",
        "surname": "Simpson",
        "accounts": []
    }
    ```
 
 * **Error response:**
 
   * **Code:** 404
   * **Content:** 
   ```
   {
       "message": "Client with the following id does not exist: 2"
   }
   ```
 
* **Sample request body:**

  ```
    {
        "firstName": "Bart",
        "surname": "Simpson"
    }
  ```
 
**Remove client**
----
  Remove client with given id.

* **URL**

  /client/:id

* **Method:**

  `DELETE`

* **Success Response:**

  * **Code:** 204
 
* **Error response:**

  * **Code:** 404
  * **Content:** 
  ```
  {
      "message": "Client with the following id does not exist: 2"
  }
  ```

**Get all accounts**
----
  Returns json data about all accounts.

* **URL**

  /account

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200 <br />
  *  **Content:** 
    ```
    [
        {
            "id": 2,
            "balance": 1000,
            "clientId": 1
        }
    ]
    ```

**Get account by id**
----
  Returns json data about account with given id.

* **URL**

  /account/:id

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
    {
        "id": 2,
        "balance": 1000,
        "clientId": 1
    }
    ```
 
* **Error response:**

  * **Code:** 404
  * **Content:** 
  ```
    {
        "message": "Account with the following id does not exists: 3"
    }
  ```

**Create account**
----
  Creates account and return information about it.

* **URL**

  /account

* **Method:**

  `POST`

* **Success Response:**

  * **Code:** 201
  *  **Content:** 
    ```
    {
        "balance": 1000,
        "clientId": 1
    }
    ```
    
* **Error response:**

  * **Code:** 404
  * **Content:** 
  ```
    {
        "message": "Client with the following id does not exist: 2"
    }
  ```
 
* **Sample request body:**

  ```
    {
        "balance": 1000,
        "clientId": 1
    }
  ```
  
**Deposit money**
----
  Deposits money on account and returns updated information about it.

* **URL**

  /account/deposit

* **Method:**

  `POST`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
    {
        "id": 2,
        "balance": 1040,
        "clientId": 1
    }
    ```
    
* **Error response:**

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Account with the following id does not exists: 3"
    }
  ```

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Amount should be bigger than zero"
    }
  ```
 
* **Sample request body:**

  ```
    {
        "id": 3,
        "amount": 1000
    }
  ```  
**Withdraw money**
----
  Withdraws money on account and returns updated information about it.

* **URL**

  /account/withdraw

* **Method:**

  `POST`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
    {
        "id": 2,
        "balance": 1040,
        "clientId": 1
    }
    ```
    
* **Error response:**

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Account with the following id does not exists: 3"
    }
  ```

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "There is not enough money to perform operation on account with id: 2"
    }
  ```
  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Amount should be bigger than zero"
    }
  ```
 
* **Sample request body:**

  ```
    {
        "id": 3,
        "amount": 1000
    }
  ```
  
**Transfer money**
----
  Transfer money between accounts and return updated information about them.

* **URL**

  /account/transfer

* **Method:**

  `POST`

* **Success Response:**

  * **Code:** 200
  *  **Content:** 
    ```
    [
        {
            "id": 2,
            "balance": 100,
            "clientId": 1
        },
        {
            "id": 3,
            "balance": 990,
            "clientId": 1
        }
    ]
    ```
    
* **Error response:**

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Account with the following id does not exists: 3"
    }
  ```

  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "There is not enough money to perform operation on account with id: 2"
    }
  ```
  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Amount should be bigger than zero"
    }
  ```
    
  * **Code:** 400
  * **Content:** 
  ```
    {
        "message": "Trying to transfer money to the same account"
    }
  ```
 
* **Sample request body:**

  ```
    {
        "idFrom": 3,
        "idTo": 3,
        "amount": 100
    }
  ```
  
 
**Remove account**
----
  Remove account with given id.

* **URL**

  /account/:id

* **Method:**

  `DELETE`

* **Success Response:**

  * **Code:** 204
 
* **Error response:**

  * **Code:** 404
  * **Content:** 
  ```
  {
      "message": "Account with the following id does not exist: 2"
  }
  ```
