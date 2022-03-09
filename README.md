# rey
   [http://rey.xream.io](http://rey.xream.io) 
   
[![license](https://img.shields.io/github/license/x-ream/rey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![maven](https://img.shields.io/maven-central/v/io.xream.rey/rey-parent.svg)](https://search.maven.org/search?q=io.xream)


### GLIMPSE 

       rey
             @EnableReySupport          and { private ReyTemplate reyTemplate }
             @EnableReyClient           @ReyClient
             @EnableFallback            @Fallback
        
       rey-spring-boot-starter
       
       rey-seata-spring-boot-starter
    
       
### NOTES   
       1. @Fallback not dependent on remote call, can add fallback on any class
       2. @ReyClient, call remote service, if get exception, will respond exception message with
           status 222, while actual status in message body
       3. If deploy many copies of a set of microservices, how to route to the service?
            
            public class FooRouter implements GroupRouter{
                 public String replaceHolder(){
                      return "#xxx#";
                 }
                 public String replaceValue(Object obj) {
                      // See the demo: CatServiceGroupRouterForK8S.java
                      // Anyway, coding to ensure all the data only in the dbs connected by the target services
                      // each set of services connect diffent db and cache, one set include: storage, db, cache, 
                      // and your program 
                      // all in docker, all in k8s, set/k8s namespace
                 }
            }
            @ReyClient(value = "http://${service.demo}/xxx", groupRouter = FooRouter.class)
            
            config:
            # when write/read db, sharding db can't support more TPS
            # we set the k8s namespace: prod_0, prod_1, prod_2 ....
            # k8s ingress to front service(no connection to DB), front service call service-demo
            # by this way, one set of services' TPS is 10000, deploy 10 sets, TPS become almost 100000
            service.demo=service-demo.prod#xxx#
            
            