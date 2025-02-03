# Red Hat Data Grid and Quarkus lab




## Technologies Used

This lab was executed with the following specifications:

- OpenShift 4.16
- Red Hat Data Grid Operator 8.5.4
- MySQL 8.0
- Quarkus 3.18.1 (Java 17)

## Pré Prerequisites

Before starting, it is necessary to have access to OpenShift, as the entire lab will be conducted through it. 
The user must be properly authenticated.


## Provisioning the required infrastructure

Let's start by creating two projects in OpenShift to establish a logical isolation layer between the parts of our solution.
- remote-cache
- application

### Step 1: remote-cache project

After creating this project, we will install the Red Hat Data Grid operator. In the *Administrator* perspective, go to the left-side menu, select *Operators*, and click on *OperatorHub*. In the search bar, look for *Red Hat Data Grid*.

![Installing the Data Grid Operator](/images/operator-data-grid.png)

Keep the default settings and click the *Install button*. Once the installation is complete, click the *View Operator button* to access the operator screen.

![Details of the Data Grid Operator](/images/operator-data-grid-details.png)


Next, we will create the necessary instances using the YAML files available in this repository [here](/infra/openshift/data-grid/).

Note: In the infinispan.yaml file, the route of the OpenShift cluster host must be used.

For example:
console-dg.<cluster-hostname>
console-dg.apps.cluster-g6kn4.g6kn4.sandbox1680.opentlc.com

Click *Create Instance* under the Infinispan Cluster option, paste the contents of the infinispan.yaml file into the text box, and click the *Create button*.

![Infinispan YAML Definition](/images/infinispan-yaml.png)

Notice that two pods will be created for the cluster.

![Infinispan Pods](/images/infinispan-pods.png)

The same should be done for the Cache option. Paste the contents of the cache.yaml file into the text box and click the *Create button*.

![Cache YAML Definition](/images/cache-yaml.png)

Notice that three caches have been created. This behavior is normal, as the memcached-cache and resp-cache caches are automatically created by the Infinispan cluster. The important thing is that they are in the Ready status.

![Cache Pods](/images/cache-pods.png)

An interesting point is that a route will be enabled to access the Red Hat Data Grid console. In the left-side menu, go to *Networking* and then *Routes*. Finally, click on the *Location link* for the infinispan-external route.

The console will ask for a username and password to access it. These credentials are stored in a *Secret* called *infinispan-generated-secret*. In the left-side menu, go to Workloads and then Secrets.

After entering the correct credentials, click the *Open the console button*.

![Data Grid Authentication Screen](/images/dg-console-auth.png)

![Data Grid Panel](/images/dg-console-panel.png)


We have completed this step.

### Step 2: application project

#### Database

After creating this project, we will provision our MySQL database.

In the *Developer perspective*, we will use a predefined template to create the database. In the left-side menu, click *+Add*.

Among the available options, select *Database* within the *Developer Catalog panel*.

![+Add Panel](/images/developer-perspective-add.png)

We will select the MySQL - Provided by Red Hat, Inc. option and click the *Instantiate Template button*.

![MySQL Database](/images/mysql-db-install.png)

In the form, we need to define some parameters for the database configuration. Below is the list of parameters and the values to be adjusted:

| Parameter | Value |
| ----- | ----- |
| MySQL Connection Username | Choose the name you find most appropriate |
| MySQL Connection Password | Choose the password you find most appropriate |
| MySQL root user Password | Choose the password you find most appropriate. |
| MySQL Database Name | presentation_db |
| Version of MySQL Image | 8.0-el7|

**Finally, click the Create button.**

![MySQL Database Form](/images/mysql-db-form.png)

This process may take a few minutes to complete. As a result, we should see the pod in Running status (blue circle).

![MySQL 8 Pod](/images/mysql-pod.png)


#### Aplications

We will deploy the applications using the *Source to Image (S2I) strategy* of Red Hat OpenShift, based on our Git repository.

To begin, we will deploy our presentation microservice. In the *Developer perspective*, go to the left-side menu and click *+Add*.

Among the available options, select *Import from Git* within the Git Repository panel.

We will fill in the form with the following parameters:

| Parameter | Value |
| ----- | ----- |
| Git Repo URL | https://github.com/rh-imesquit/quarkus-remote-cache |
| Context dir | /apps/quarkus-presentation-ms |
| Application name | quarkus-presentation-ms |
| Name | quarkus-presentation-ms |

In the Deploy section, click the *Show advanced Deployment options* link and fill in the following environment variables.

| Environment Variable | Typo | Value |
| ----- | ----- | ----- |
| DB_NAME | Secret | mysql - database-name|
| DB_USER | Secret | mysql - database-user|
| DB_PASSWORD | Secret | mysql - database-password|
| DB_HOST | Text | The hostname generated for the MySQL Service |

**Finally, click the Create button.**

![Importing the presentation microservice from Git](/images/import-git-microservice.png)

As a result, we should see the *quarkus-presentation-ms* application pod in *Running* status (blue circle).

![Presentation microservice pod](/images/presentation-microservice-running.png)

Let's make some requests to check if the service is actually working. Don't forget to find the generated route for the application in the left-side menu *Networking*, and then *Routes*.

In your computer's terminal, run the following commands:

```
curl -X GET <Generated route for the presentation microservice>/presentation && echo
```

The expected result is: *{"message": "There are no registered presentations"}*.

Let's insert a record into this microservice's database.

```
curl -X POST <Generated route for the presentation microservice>/presentation \
     -H "Content-Type: application/json" \
     -d '{
           "theme": "Quarkus and Red Hat Data Grid",
           "author": "Ian Mesquita",
           "dateTime": "2025-02-07T16:00:00"
         }'  && echo
```

The expected result is: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus and Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}*.

At this point, the record has been inserted into the database.

Now, let's retrieve this record individually to observe the expected behavior.

```
curl -X GET <Generated route for the presentation microservice>/presentation/1 && echo
```
The expected result is: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus and Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}*.

Notice that the result took a while to appear, and that's correct. This is the expected behavior! This endpoint was implemented to simulate a backend that takes approximately 8 seconds to respond.

Now, let's move on to the final part of this lab.

Following the same procedure, we will deploy the *quarkus-infinispan-cache microservice*. First, we need to create a *Secret* with the authentication credentials for Infinispan.

In the *Administrator perspective*, go to the left-side menu *Workloads* and then *Secrets*. Click the *Create button* and select *Key/Value Secret*.

![Creating the Secret with Infinispan credentials](/images/infinispan-secret-creation.png)

Add three Key/Value options and set them as shown in the table below:

| Parameter | Value |
| ----- | ----- |
| Secret name | infinispan |
| Key 1 | cache-host |
| Value 1 | <Host generated for the Infinispan Service (remote-cache project)> |
| Key 2 | cache-user |
| Value 2 | <User generated in the Secret infinispan-generated-secret (remote-cache project)> |
| Key 3 | cache-password |
| Value 3 | <Password generated in the Secret infinispan-generated-secret (remote-cache project)> |

In the *Developer perspective*, go to the left-side menu and click *+Add*.

Among the available options, select *Import from Git* within the *Git Repository panel*.

Let's fill out the form with the following parameters:

| Parâmetro | Value |
| ----- | ----- |
| Git Repo URL | https://github.com/rh-imesquit/quarkus-remote-cache |
| Context dir | /apps/quarkus-infinispan-cache |
| Application name | quarkus-infinispan-cache |
| Name | quarkus-infinispan-cache |

In the *Deploy section*, click the *Show advanced Deployment options* link and fill in the following environment variables.

| Environment Variable | Type | Value |
| ----- | ----- | ----- |
| INFINISPAN_HOST | Secret | infinispan - cache-host|
| INFINISPAN_USER | Secret | infinispan - cache-user|
| INFINISPAN_PASSWORD | Secret | infinispan - cache-password|
| API_PRESENTATION_URL | Text | Generated route for the quarkus-presentation-ms microservice |

**Finally, click the Create button.**

As a result, we should see the *quarkus-infinispan-cache* application pod in *Running* status (blue circle).

![Cache microservice pod](/images/presentation-cache-microservice-running.png)

Let's test the setup, but first, retrieve the route created for the cache microservice in the left-side menu *Networking* and then *Routes*.

First, let's test the endpoint without cache and measure the response time.

```
time curl -X GET <Generated route for the quarkus-infinispan-cache microservice>/presentation/nocache/1 && echo
```

The result should be something close to: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus and Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}
real	0m9.013s
user	0m0.015s
sys	0m0.010s*.

Exactly as expected, the presentation microservice is slow.

Before moving forward, check in the Red Hat Data Grid console panel that there are no entries in the presentations cache.

![Presentation cache details](/images/presentation-cache-details.png)

Now, let's use the endpoint with the implemented Infinispan cache.

The first execution will take longer because the entry is not yet in the cache, as we observed earlier. However, in subsequent executions, the response time will decrease significantly. 

```
time curl -X GET <Generated route for the quarkus-infinispan-cache microservice>/presentation/cache/1 && echo
```

The result should be something close to: *{"id":1,"author":"Ian Mesquita","theme":"Quarkus and Red Hat Data Grid","dateTime":"2025-02-07T16:00:00"}
real	0m0.548s
user	0m0.016s
sys	0m0.004s*.

Now we are accessing the cache.

Go to the *Data Grid panel* and check that the entry has been recorded in the cache with a TTL (Time to Live) of 1800 seconds, which means 30 minutes.

![Cache populated with the record](/images/cache-populated.png)

Great job! We have completed our lab.