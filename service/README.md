#Mailgun Send Message API 

Using the simple Mailgun tool as a base, this is a Play app that exposes the POST /messages Mailgun API.

## Usage
Export `MAILGUN_API_KEY` and `MAILGUN_DOMAIN` into your env, or add them to `conf/application.conf`.

Run locally with: `sbt service/run`
 
Example call:

```curl
curl -X POST -H "Content-Type: application/json" -d '{
  "to": "somebody@ca.com",
  "from": "yesss@yesss.ca",
  "subject": "Hello!",
  "body": "<h1>Hey!</h1>",
  "template": {
    "type": "passwordReset",
    "userName": "blah",
    "resetPassUrl": "http://password-reset-url"
  }
}
' 'http://localhost:9000/v1/mail'

```
The JSON string has the following schema:
```json
  {
    "to": "target@some.com",
    "subject": "Hello!",
    "body": "<h1>Hey!</h1>",
    "template": {
      "type": "passwordReset",
      "userName": "blah",
      "resetUrl": "http://password-reset-url"
    }
  }
``` 
The template is optional and will overwrite the body.
 
The template can also be a welcome email template:
```json 
  {
    "to": "target@some.com",
    "subject": "Hello!",
    "body": "<h1>Hey!</h1>",
    "template": {
      "type": "welcome",
      "userName": "blah",
      "confirmAcctUrl": "http://confirm-acct-url"
    }
  }
``` 

Valid template types are: `welcome` and `passwordReset`.
