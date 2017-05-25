# lionrpc
JAVA RPC框架,基于netty,kryo,zookeeper 和 spring实现，可无缝接入Spring，客户端和服务器端均采用异步处理，实现20000+TPS。负载均衡策略为轮询。

服务器端Spring启动配置:

    <!--服务端接口实现类，需要添加@LionService注解-->
    <context:component-scan base-package="com.my.lionrpc.lionrpc.server"/>

    <context:property-placeholder location="classpath:server.properties"/>

    <!--业务处理线程池配置-->
    <bean id="workerCenter" class="com.my.lionrpc.common.ApplicationWorkerCenter">
    	<constructor-arg name="coreSize" value="4"/>
    	<constructor-arg name="maxSize" value="20"/>
    	<constructor-arg name="keepAliveSecond" value="10"/>
    	<constructor-arg name="queueSize" value="100"/>
    </bean>

    <!--zookeeper服务注册配置-->
    <bean id="serviceRegistry" class="com.my.lionrpc.registry.ServiceRegistry">
        <constructor-arg name="registryAddress" value="${registry.address}"/>
    </bean>

   <!--服务配置-->
    <bean id="rpcServer" class="com.my.lionrpc.server.LionServer">
        <constructor-arg name="serverAddress" value="${server.address}"/>
        <constructor-arg name="workerCenter" ref="workerCenter"/>
        <constructor-arg name="registry" ref="serviceRegistry"/>
    </bean>


客户端Spring启动配置:

    <context:property-placeholder location="classpath:client.properties"/>

    <!--zookeeper服务发现配置-->
    <bean id="serviceDiscovery" class="com.my.lionrpc.registry.ServiceDiscovery">
        <constructor-arg name="registryAddress" value="${registry.address}"/>
    </bean>

    <!--Spring远程服务接口配置-->
    <bean id="lsProxyFactory" class="com.my.lionrpc.client.spring.LionServiceProxyFactory">
        <property name="serviceMap">
            <map>
                <!--key标示注入到Spring容器的beanID,value为服务接口类。-->
                <entry key="helloService" value="com.my.lionrpc.lionrpc.server.IHelloService"/>
                <entry key="teacherService" value="com.my.lionrpc.lionrpc.server.ITeacherService"/>
            </map>
        </property>
    </bean>

客户端业务类如果需要使用helloService接口可在类中添加如下注解即可完成对象注入：

    @Autowired
	private IHelloService service;


测试报告：

测试机配置：

  型号标识符：	MacBookPro12,1 
  
  处理器名称：	Intel Core i5 
  
  处理器速度：	2.9 GHz
  
  处理器数目：	1
  
  核总数：	2
  
  L2 缓存（每个核）：	256 KB
  
  L3 缓存：	3 MB
  
  内存：	8 GB

服务器端和客户端在同一测试机上部署：

客户端启动15个线程进行RPC调用，每个线程调用1000万次（共调用1.5亿次,耗时5593797ms），平均每秒完成2.68万+次调用。测试过程中有启停服务端测试，客户端可以正常重连。且服务器端和客户端内存占用较少。

PID   COMMAND      %CPU      TIME     #TH   #WQ  #PORT MEM    PURG   CMPRS  PGRP

3381  java         77.4      07:46.49 34/4  0    106   118M+  0B     0B     596     服务端

3380  java         159.9     17:53.84 51/1  0    140   165M+  0B     0B     596     客户端


声明：开发过程中有借鉴其它java rpc框架设计思想。
