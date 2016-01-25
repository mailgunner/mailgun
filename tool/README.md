#Mailgun tool 

## Usage

Simple command-line tool to send single email from JSON using Mailgun API.
 
Takes 3 args: a Mailgun API key, Mailgun domain, and a JSON request string.
 
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

Run directly with:
```
sbt 'tool/run key-123 sandbox123.mailgun.org {"to":"me@some.com","subject":"Hi","body":"<html><body><h1>hi</h1></body></html>"}'
```

Or, build the fat jar first: `sbt tool/assembly` and run the resulting jar with:

```
java -jar mailer.jar  key-123 somedomain.org '{"to":"somebody@ca.com","subject":"Hello!","body":"wow","from":"me@gmail.com","template":{"type":"passwordReset","userName":"blah","resetPassUrl":"http://google.ca"}}'
```
