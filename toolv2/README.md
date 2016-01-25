#Mailgun with SDS

Pulls send email requests from SDS and sends them via Mailgun.

## Usage

Replace `MAILGUN_API_KEY`, `MAILGUN_DOMAIN` and `SQS_QUEUE_URL` in application.conf with real values, or export them to your env. E.g., `export MAILGUN_DOMAIN=yourdomain.com`. 

The SDS client uses the [default creds provider chain](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html).

Build with: `sbt toolv2/assembly`, it will be `mailgunner/toolv2/target/scala-2.11/mailerv2.jar`

Run with: `java -jar mailerv2.jar`

## Creating test data
To fill SDS with SendMessageRequest JSON strings, something like [this](https://gist.github.com/mailgunner/d53ff57b887952d2abde) could work. Just add this to the toolv2 project src.

## Issues/Comments
* Probably could have wrapped all the SQS stuff behind a trait. And used a Scala wrapper. The nice thing about SQS is that I didn't have to worry about the state where I pulled some messages off the queue and then the app failed before I could send them.
* Used Akka's built-in scheduler to long pull messages from the SQS queue at some configurable interval. Probably good enough for a tool like this.
* All the calls to SQS and the Mailgun client are blocking. Making them async is easy but makes graceful shutdown harder. If I had more time, non-blocking definitely.
* The Mailgun response should have been checked for specific things like 400's because of bad data or being over the rate limit. The app should probably just error out and stop if it's a 401 or 503. I don't even have a supervisor strategy in place for when bad data kills one of the MailgunWorkers so the worker doesn't restart.
* I did implement some graceful handling of sigterm -- messages in the pipes should be sent and deleted before the app shuts down. On shutdown, the Manager stops responding to requests from the scheduler to get more messages. Then the Workers are poison pilled, then the Supervisor. Once the Supervisor is stopped, the QueueManager (for SQS requests) can be safely poison pilled (since the Workers are gone). And when the QueueManager is terminated, the Manager and the actorsystem can shutdown.
* Again, should use execution contexts to blockhead calls to SQS and the MailgunClient.
* Could probably reuse the ConfigComponent to refactor the calls to "ConfigFactory.load()..." and abstract out the config access.
* If this were to be deployed on a machine somewhere, would probably need init.d scripts or the like to daemonize and start/stop the tool.

