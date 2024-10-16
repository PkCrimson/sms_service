# Implemented tech or tools
  - Spring Framework
  - Maven
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
# Dependencies
```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.11.0</version>
        </dependency>

        <dependency>
            <groupId>com.aliyun</groupId>
            <artifactId>dysmsapi20180501</artifactId>
            <version>1.0.4</version>
        </dependency>
```
