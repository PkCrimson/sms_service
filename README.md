# Implemented tech or tools
  - Spring Framework
  - Maven
  - Redis
  - MongoDB
  - RabbitMq
  - Aliyun SMS Service
# API
## /api/auth/sms/send/{db}
  - `Path:/{db}` = `redis` / `mongo` (Dependence which db to use)
  - `RequestBody`
    ```
    {
      "countryCode": "852",
      "phoneNumber": "12345678",
      "language": "EN"
    }
    ```
  - `Response`
      - `OK`
      - `Send SMS failed`
## /api/auth/sms/verify/{db}
- `Path:/{db}` = `redis` / `mongo` (Dependence which db to use)
  - `RequestBody`
      ```
      {
        "countryCode": "852",
        "phoneNumber": "12345678",
        "code": "1234"
      }
      ```
  - `Response`
    - `OK`
    - `Verification Expired`
    - `Verification Failed`
