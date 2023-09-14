This is a hello world project to learn scala

To run, use sbt
make sure the java version and sbt version aligns with other projects
sbt 1.8.2 java 11

to run, 
sbt to enter shell and then compile -> run
or directly sbt compile run

So how the system know which file to run?
The key is the build.sbt
It gets all package, and run all object in the package in all scala files in the main folder
In this example, it is package example, object Hello
example.Hello
To run test cases, sbt compile test
It read src/test files

There can be multiple projects under a same folder
you may use sbt projects to list the projects
for example, a sub project foo one may write some code in the build.properties
lazy val foo = (project in file("foo"))
  .settings(
    // other settings
  )
And build a folder foo, under it a full folder structure for the project, esp build.sbt, src/main..., src/test...


For akka, it is something different. It uses message based approach and work like actor sending message and handling message receive and sendin backing reply message.
To run akka, sbt 
reStart

the actor path is like 

Study notes on Scala:
def means function, val means immutable values. One funny thing is people can define static function like val
def area = width*height
val area = width*height
The difference is that def runs everytime someone use it, val only runs once and keep result.
it can also run multiple lines
def area: Int = 
    val wid: Int = width
    val hei: Int = wid*height
    hei // area will return hei, the last line with correct indent
case class Rectangle(width: Int, height: Int)
    def area: Int = width*height
val x = Rectangle(4,3)
x.area = x.area*2 // This will not work as reassigning value into area
val y = x.copy(width = width*2) // This works as it copy x with a new width value
y.area

sealed trait Shape // The shape can only be rectangle or circle
case class Rectangle(width: Int, height: Int) extends Shape
case class Circle(radius: Int) extends Shape
Here is a way to calculate someShapeArea from a val someShape
val someShapeArea = 
    someShape match
        case Rectangle(width, height) => width*height
        case Circle(radius) => width*width*3.14
// If the match does not cover every case, there will be a compile error
val shapeDescription = 
    someShape match
        case circle: Circle => s"This is a ciecle with radius {circle.radius}" //In case you need to use something inside someShape
        case _ => "This is only a shape" //Widecard match all, like a default in switch
enum variables have a function .values to return array of all possible values
also valueOf("Green") to return enum.Green

List contacts
contacts.size // Return the size
contacts.contain(alice) // Return true if contacts contain an element equivalent to alice
contacts.map(contact => contact.name) //Return list of contact name
contracts.filter(contact => contact.phoneNumber.nonEmpty) //Return list of contact which phoneNumber.nonEmpty is true
contracts.find(contact => contact.phoneNumber.nonEmpty) // Return the first contact which phoneNumber.nonEmpty is true. 
It maybe Some(contact) or None, it is called Option

Function and Method
def area(x: Int, y: Int): Int = x+y
val area = (x:Int, y: Int) => x+y
val area: (Int, Int) => Int = (x,y) => x+y

area(3,4) is equivalent to area.apply(3,4). So if an object has apply method inside, it can work with this arrow syntex like function
functions can be used as a varible
If there is only one input / default input, you can use placeholder _ to replace the input. _ also works as widecard
val add: (Int, Int) => Int = _ + _ //Funny thing is that this works. (x1: Int, x2: Int) => x1 + x2

val contacts = List(alice,bob) // equals to alice :: bob :: Nil == alice :: ( bob :: Nil) == Nil.::(bob).::(alice) Nil calls :: method to join bob and then alice 
val contacts2 = carol :: contacts // List(carol,alice,bob)
contacts match
    case first :: second :: tail => first.name // The first is carol, second is alice, tail is bob
    case first :: Nil => first.name // Do not work unless it has only one element
    case _ => 'Widecard match'
contacts.head == carol, contacts.tail == list(alice,bob), contacts.tail.head = alice, contacts(2) = bob
List(1,2,3) ++ List(4,5,6) // List(1,2,3,4,5,6)
val first = mutable.ArrayBuffer(1,2,3)
val second = mutable.ArrayBuffer(3,4,3)
first ++= second // first will be ArrayBuffer(1,2,3,3,4,3) second = ArrayBuffer(3,4)
first -= 3 // first will be ArrayBuffer(1,2,3,4,3)
first --= List(3,4,5) //first will be ArrayBufer(1,2,3)
first.exists(x=>x<0) //first contains any negative values?
first.forAll(x=>x<0) //all first contain are negative? 

Tuple: 
val pair1: (String, Int) = "alice" -> 42 == ("alice",42)
("alice",Sex.female,13,18) == "alice" -> Sex.female -> 13 -> 18
val (x,y) = pair1 // x == 'alice', y == 42
Map is not ordered

More on List
case Contact(name: String, phoneNumbers: List[String])
val contacts = List[Contact]
contacts.map(contact => contact.phoneNumbers) // Return List[List[String]]
contacts.flatMap(contact => contact.phoneNumbers) // Return List[String], so the String are flatten into one level only

foldLeft a function to run cummulatively
List(1,2,3).foldLeft(0)( (accum, ele) => accum + ele ) // Return 6, it calculates as 0 => 1 => 3 => 6
List(1,2,3).foldLeft(0)( (accum, ele) => 2*accum + ele ) // Return 11, it calculates as 0 => 1 => 4 => 11
List(1,2,3).foldLeft(List.empty[Int])( (accum, ele) => (2*ele) :: accum ) // Return List(2,4,6)
xs.foldLeft(z,f) == f(...f(f(z,xs(0)),xs(1))...,xs(n-1))
1 +: 2 +: 3 == List(1,2,3)
(1 :+ 2) :+ 3 == List(3,2,1)
GroupBy, group values by a function
val getDomain: String => String =
    email => email.dropWhile(c => c != '@').drop(1) //Find the first occurance of @ and extract the substring after
val emailDomains = email.groupBy(getDomain)
//Result will be Map("sca.la" -> List("alice@sca.la","coral@sca.la"), "earth.world" -> List("bob@earth.world"))
contacts.sortBy((name,_) => name) //Return a new list sorted by the first entry of tuple, which we call it name
Map.get('a') returns a option Some(value) or none

Option
is a collection with at most one value, Some(value) or none
getOrElse(default) // get the option value, if it is none, use the default value to sub
val aliceOrBobEmail: Option[(String,String)] = alice.maybeEmail.zip(bob.maybeEmail)
contacts.headOption // Some(contact) or there is a head, none otherwise

Loop
(1 to n) //List(1,2,...,n) == 1.to(n)
(1 to n by k) //List(1,k+1,2k+1,...,n)
(n until 1 by k) //List(n,n-k,n-2k,...,something just above 1)
LazyList.from(0) // A infinite list starting from 0
Tail recursion
def loopB(contacts: List[Contact]): List =
    var emailList = nil
    var contactsTail = contacts
    while(contactsTail.hasTail){
        emailList = contactsTail.head.email :: emailList
        contactsTail = contactsTail.tail
    }
    emailList
Tail recusion is better as it does not use stack to keep the loop index, it runs infinitely. loop may bufferoverflow if n > 100,000
For loop
for(s) yield // s is the loop + filter statement, yield gives the result
val nameAndSwissNumber: List[(String,String)] =
    for
        contact <- contacts
        phoneNumber <- contact.phoneNumbers
        if phoneNumber.startsWith("+41")
    yield (contact.name, phoneNumber)
//Result is like List[("alice","+411234"),("alice","+415678"),("bob",+413234)...]
for x <- e1 yield e2 == e1.map(x => e2)
for x <- e1 do e2 == e1.foreach(x => e2) //Do a map but does not return anything

## Toilet service readme
# Build process:
//Make sure Java SDK is 8 or 11
export JAVA_HOME=$(/usr/libexec/java_home -v11)
# export neccessary path
export PRIVATE_ARTIFACTORY_CREDENTIAL=/etc/sbt_repo/credentials-dev
export PRIVATE_ARTIFACTORY=https://nexus.exaleap.ai
vim /etc/sbt_repo/credentials-dev

realm=Sonatype Nexus Repository Manager
host=nexus.exaleap.ai
user=scala_user
password=exaleap

//Check the sbt version to be correct
sbt --version

sbt
compile
run

# Testing a feature
sbt 'testOnly *ACXToiletVoTest -- -z json'
ACXToiletVoTest is a class, will run all tests which name contains json


# Toilet service basic structure
It has two parts, one is GRPC server (ToiletAccessAppSrvImpl, DeviceSettingSvc) for adding and removing device in database. 
Another one is Actor structure for receiving messages from aws-iot (Kafka) and do the toilet flow.
(KafkaHandler.consumer, which call .graph to validate everything and store it, commitflow to send out message)

Inside graph, it uses doValidateDDI to check the DDI and make the message into a object and check the QR code is valid in time
then uses checkDeviceAndSpaceTypeId to read data from database and package a VerifyQRCodeRequest object 
then uses invokeGRPCPermissionControlService to call permissionControlServiceClient fucntion to verifyQRcode
then uses invokeGRPCPCSSuccess to send out message
.via(sendNotificationMessageByMqtt)
      .via(invokeDeviceGrpcFlow)
sendNotificationMessageByMqtt fire message to MQTT, indicating if the QR code access is success or not
SendMessageToDeviceRequest send a GRPC message to the toilet for opening / not opening the toilet, using reactiveiot.grpc library

In the process, no matter some steps have error or every step success, will call saveRecordToCassandra to save the result into cassandra
If the message is correct, just cannot pass the permissionControlService checking or can pass, will also call ProduceAccessRecordFlow.product(recordTypeID) to build up userAccessRecord in the message, which shows the relationship between the message and an user.

Inside commitFlow, If there is userAccessRecord in the message, will fire a ProducerMessage to Kafka for a new ProducerRecord

      

