#Mailgun with SDS

Pulls send email requests from SDS and sends them via Mailgun.

## Usage

Replace `MAILGUN_API_KEY`, `MAILGUN_DOMAIN` and `SQS_QUEUE_URL` in application.conf with real values, or export them to your env. E.g., `export MAILGUN_DOMAIN=yourdomain.com`. 

The SDS client uses the [default creds provider chain](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html).

Build with: `sbt toolv2/assembly`, it will be `mailgunner/toolv2/target/scala-2.11/mailerv2.jar`

Run with: `java -jar mailerv2.jar`