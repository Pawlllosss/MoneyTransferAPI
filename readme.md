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

  /client/

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
