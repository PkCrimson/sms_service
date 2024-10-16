# Implemented tech or tools
  - Spring Framework
  - Redis
  - RabbitMq
  - Aliyun SMS Service
# API
## /api/auth/sms/send
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
## /api/auth/sms/verify
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
