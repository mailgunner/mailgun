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

## Issues
* Responses > 200 status aren't handled, instead they spit out built-in Play html
* Valid responses are just what Mailgun returns. Normally, I would transform into some format.
* JSON validation is just a catch-all. You would want to match on the possible `ValidationError`s and return appropriate error responses.
* If the server OOM's, the server stays down. I would usually set a restart command in the OnOutOfMemoryError jvm option. Also, there is no request draining. Though, I would probably have a ping endpoint on the service and put the instances behind an LB. And, no monitoring.
* Bulkheading: I would probably put the calls to the Mailgun client in a separate execution context, instead of just using the default global ec everywhere.
* Authentication: for simplicity, the service uses a single API key/domain from config, though ideally you would pass that in as query params. I didn't put in SSL support.
* Probably more
