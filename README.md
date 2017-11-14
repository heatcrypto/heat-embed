# HEAT Embedding Project

This work in progress project acts both as a test-bed for further developing HEAT 
embedding functionality. As well as a working starter application that shows you
how to add a custom binary message protocol and how through a combination of a 
`handler` and a `model` we can turn those blockchain messages into entries in a SQL
database. Plus we show how to extend the HEAT server HTTP API with your own HTTP
end-points.

## The Sample Application

The whole of the sample application lives in `com.heatledger.embed.sample` which can 
be found in [src/main/java/com/heatledger/embed/sample](/src/main/java/com/heatledger/embed/sample).

Lets briefly go over the files in the sample to explain what they do.

### SampleMain.java

This is the heart of the application that ties everything together. It is also where we find the `main` method.

[SampleMain.java](/src/main/java/com/heatledger/embed/sample/SampleMain.java)

The `main()` method typically constructs an instance of `com.heatledger.embed.EmbedBuilder` which we 
use to configure the embedded HEAT instance.

When all configurations are there you would call `EmbedBuilder.build()` which in turn gives you a 
instance of `com.heatledger.embed.Embed` which among other things has a `start` method which will start
the embedded HEAT instance.

As can be seen in `SampleMain.java` two other classes are created and passed to EmbedBuilder, these are:

- EmbedExternalAPIAdditions
- EmbedExternalReplicatorAdditions

These two classes are your way of extending HEAT core with either your own HTTP API additions (`EmbedExternalAPIAdditions`)
or with your own database additions which will come with both a `handler` and with a `message` implementation with 
which we encode and decode and optionally encrypt and decrypt to/from binary transaction message attachments. More on that later.

### SampleMessageDB.java

This is our database model, in a normal application you would have 1 model for each table in your SQL database. Our model
extends `BasicReplicator<SampleMessage>` which extends `AbstractReplicator` which in turn is how all [optional] replicator 
database models  in HEAT core work.

The actual SQL referenced in the class constructor lives in the [resources](/src/main/resources) directory.

[SampleMessageDB.java](/src/main/java/com/heatledger/embed/sample/SampleMessageDB.java)

We would be inserting data into our model through the `SampleMessageDB.create` method which will in turn be called from 
our `handler` (keep reading for that part). To get data out of our model, this would mostly happen from our API additions, we 
would add methods like `SampleMessageDB.search` or `SampleMessageDB.lits`.

    Important Note: Until measurements to prevent this are in place creators of embedded HEAT applications
    must always take in account that inject methods like `SampleMessageDB.create` can and most likely will
    be called multiple times! The reason being that in a real-time decentralized network we can and will
    see blockchain reorgs/forks, we do however believe we have a solution that solves this. The solution is 
    part of the HEAT core p2p network layer remake.
    
### SampleMessageMessage.java

When embedding HEAT, often you would want to be sending and receiving structured data in the form of [optionally encrypted] binary encoded messages. Structured messages should extend `AbstractBundleMessage` which in turn can be used in both the `handlers` and in the `BundleProcessor`.

[SampleMessageMessage.java](/src/main/java/com/heatledger/embed/sample/SampleMessageMessage.java) is an extremely simple example since all
it does is encode/decode a single string. In more complex examples you could add as much data as you need.

 
### SampleMessageHandler.java

Handlers are how we connect structured messages with their assigned message identifiers and are used to feed the messages into the database models.

[SampleMessageHandler.java](/src/main/java/com/heatledger/embed/sample/SampleMessageHandler.java)

### ResourceSample.java

This shows how we can add HTTP API additions using standard [JAX-RS](https://github.com/jax-rs) to your HEAT embedded app.

We add these resources by instantiating them and registering them with HEAT core as can be seen in `SampleMain`. 

### Models.java

This shows how we can add JAX-RS compatible annotated models, we use these to encode data to JSON for use in our HTTP additions.

## Installation

Note that you would need to manually include `heatledger-2.0.0.jar` which as it stands is available only by downloading https://github.com/Heat-Ledger-Ltd/heatledger/releases/tag/v2.0.0, extracting the zip file and looking for `heatledger-2.0.0.jar` in the lib directory.

We are working on publishing HEAT core to maven which should make this part easier.

### Eclipse setup

`$ gradle compileJava cleanEclipse eclipse`

### Build executable

`$ gradle clean assemble installDist` 
 