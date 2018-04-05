![](resources/solstice.png)

Author: Keun Lee (@keunlee)

# From Start to PKS

This guide details a workflow from the starting phases of application development, Dockerizing, deployment to a local K8S cluster, to deployment onto Pivotal's PKS. This guide presents to you one of many options and routes that you can take to organizing your development workflow. 

https://github.com/keunlee/from-start-to-pks

# Assumptions

* You are running all your development from a Mac or other Unix flavored OS

# Pre-requisites

Make sure the following are installed on your box before proceeding: 

* Docker - https://www.docker.com/get-docker
* Virtualbox - https://www.virtualbox.org/wiki/Downloads
* Minikube - https://github.com/kubernetes/minikube
* Kubectl - https://kubernetes.io/docs/tasks/tools/install-kubectl/
* Kompose - https://github.com/kubernetes/kompose
* Helm - https://docs.helm.sh/using_helm/#quickstart
* JDK 8 - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* Maven - https://maven.apache.org/
* Node + NPM (>= v8) - https://github.com/creationix/nvm
* Angular CLI - https://cli.angular.io/
* Watch CLI - http://osxdaily.com/2010/08/22/install-watch-command-on-os-x/

Make sure you have the following available for PKS Cluster Deployment:

* You have a GCP account
* You have Ops Manager installed on GCP, see: https://docs.pivotal.io/pivotalcf/2-0/customizing/deploy-bosh-om.html
* You have PKS Installed in Ops Manager, see: https://docs.pivotal.io/runtimes/pks/1-0/gcp.html

# Useful Links

__Docker Ecosystem__

* docker - https://docs.docker.com/engine/reference/commandline/cli/
* docker cli cheatsheet - https://www.docker.com/sites/default/files/Docker_CheatSheet_08.09.2016_0.pdf
* docker-compose - https://docs.docker.com/compose/reference/overview/
* docker-compose cheatsheet - https://devhints.io/docker-compose

__Kubernetes Ecosystem__

* k8s yaml - https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.10
* kubectl - https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands 
* kubectl cheatsheet - https://kubernetes.io/docs/reference/kubectl/cheatsheet/
* kompose - https://github.com/kubernetes/kompose/blob/master/docs/user-guide.md
* minikube - https://github.com/kubernetes/minikube

# Overview

This guide goes over in detail a development workflow which covers the following sections: 

* Section 1
    * Local application development and testing
    * Deployment and Validation against a local Docker runtime
* Section 2
    * Deployment and Validation against a local Kubernetes cluster - Minikube
* Section 3
    * Deployment and Validation against a Kubernetes cluster - PKS

The supplied code in this guide may be found at the following Github repository: https://github.com/keunlee/from-start-to-pks

The supplied code details the following:

* A Spring Boot Microservice Application with Mongodb as an underlying datasource. This Microservice allows end-users to run CRUD methods for a sample model, "Styles".
* An Angular 4 application which consumes the Microservice, and allows end-users to add new Style models 

# Development and Deployment Workflow Summary

The following outlines the development and deployment workflow used. You may use this approach, or 
take any other approach that serves you. 

__Step 1. Develop your applications from start to finish__

__Step 2. Containerize your finished applications by creating a Dockerfile and building a Docker image__ 

```bash
docker build -t my-docker-image:v1 .
```

__Step 3. Publish the Docker image to a local container registry__

__Step 4. Create a docker-compose.yaml file and validate it against your local Docker runtime__ 

```bash 
docker-compose up
```
 
 Deploying to Docker is NOT necessary if you're aim is to get your application's Docker image to a Kubernetes cluster.
 If that is the case, then you can skip Docker validation and move ahead to the next step. 
 
 Otherwise, why the extra steps in creating a docker-compose file, deploying to Docker, and then eventually deploying to
 Kubernetes? 
 
 Before you read further, the justification below is an opinionated approach. Here are the "whys": 
 
__You can generate a Kubernetes Kompose YAML file from a Docker Compose YAML file__
  
 * Writing a Docker Compose YAML file can be executed in much shorter order then a Kubernetes Kompose YAML file. You can see the difference for
 yourself. Compare any two pair of files: `docker-compose.yaml` vs. `k8s-kompose.yaml`. 
 * What you get from docker compose is a bare-bones version of what you "can" deploy to Kubernetes. It may not be 
 the final YAML that you want to deploy to Kubernetes. However, you can tweak and modify the Kubernetes YAML to your hearts
 content and desire afterwards. 
 * Keep in mind, Kubernetes has all the orchestration features that Docker doesn't have -- (with exception to Docker Swarm),
 hence the sheer amount of customization available at your fingertips when writing a Kubernetes YAML file. 
 
__Keeping focus on validating the container itself__
 
 * Docker compose, makes that validation easy as the difference between: `docker-compose up` (which brings up the container) and `ctrl-c`
 which brings the container back down
 * So if you need to tweak your container more, you can do that from Docker's context, since that is where you will likely go back
 to if your container needs more attention. 
 
__Step 5. Get familiar with Kubernetes on your local machine using Minikube__
 
__Step 6. Convert the docker-compose.yaml file to a Kuberbetes kompose.yaml file__

```bash
kompose convert -f docker-compose.yaml --out k8s-kompose.yaml
```

__Step 7. Deploy the generated Kubernetes yaml to a local Kubernetes cluster__

```bash
kubectl create -f k8s-kompose.yaml
```

__Step 8. Create and deploy a Kubernetes Ingress yaml (i.e. k8s-ingress.yaml) to a local Kubernetes cluster__

__Step 9. Deploy to a PKS Kubernetes Cluster__

# Section 1: Development and Containerization

_This section covers steps 1-4 of the prescribed workflow_

## Style Service - Containerization Walkthrough

This setup can be used for development and testing. You should keep this setup until you are ready to move on
to next steps, which involves dockerizing, deployment to docker and then eventually deploying to kubernetes.

Until then, use this setup judiciously for development. 

### Mongo Database Setup

Run the following

```bash
docker pull mongo
docker run --name mongodb-instance -d -p 27017:27017 -v ~/data:/data/db mongo
```

This will do the following: 

* pull the latest mongo image from the public docker image registry
* create and run a local mongo instance: 
    * with name: mongodb-instance
    * -d : deploy the image to a container in your local docker runtime
    * -p : on internal port: 27017, and external port: 27017
    * -v : using the following file system location to persist mongo data: ~/data

Alternatively, you can create an instance of this using "Kitematic" and search and deploy mongo from there. 

![](https://content.screencast.com/users/Keun/folders/Jing/media/802afed7-2c9b-4cdf-ad78-456b052567fe/00000239.png)

### Build, Test, and Run

To build, run unit tests, and package a jar file

```bash
mvn clean package
```

To run the service: 

```bash
mvn spring-boot:run
```

press `ctrl-c` at anytime to stop your application

### Launch Swagger and Validate your Service

Check that the service is running by going to the swagger url: 

http://localhost:8084/swagger-ui.html

Your screen should look like the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/f223dc85-ffa2-4945-99a9-24a523455bc4/00000264.png)

At this point, you can continue to make modifications in the service application while it is development. When you are
done, you can go on to the next step which walks you through the shutdown process. 

### Shutdown Mongo and your Service

To shutdown your service, type: `ctrl-c`. Do NOT delete the `target` folder yet. You will need that in later steps. 

To shutdown mongo and remove the container, you have two options: 

__option 1: delete from kitematic dashboard__

open kitematic and click on the `x` icon next to `mongodb-instance`. see image below: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/1b7b8236-3db4-4c16-9234-c69629f8625f/00000241.png)

__option 2: command line option__

Run the following to list your running Docker containers filtered by name: `mongodb-instance`

```bash
docker ps -f name=mongodb-instance
```

You will get similar output: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/9531813d-8c2b-4dc6-9efb-d35ca84d090c/00000249.png)

Make a note of the container id. In this case: `b6a33eb20580`

Next run the following command to stop and delete the container: 

```bash
docker rm -f b6a33eb20580
```

Your mongo container should be stopped and removed. You can validate this by running the previous list command: 

```bash
docker ps -f name=mongodb-instance
```

## Docker Validation Setup

This setup is used to Containerize our application and validate it against Docker. 

### Dockerizing Your Service

At this point you have run this service application from a development context

You are now ready to deploy and validate your service as a container

The next steps: 

* Build a Docker image of your service
* Test and validate the image by deploying locally to your Docker local runtime as a Container

If your are still running mongo and your service from the previous steps, please review the shutdown steps in the 
previous step ___Shutdown Mongo and your Service___

#### Build a Docker Image

__Build a Jar file__

If you ran: 

```bash
mvn clean package

```

and did not delete the generated `target` folder, you can skip this step. Otherwise, rerun that command to 
build the jar file. 

__Create a Dockerfile__

Open the file: `Dockerfile`

You will see the following contents: 

```dockerfile
FROM openjdk:latest
ADD target/style-service-0.0.1-SNAPSHOT.jar style-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/style-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8084
```

The above lines mean: 
* pull down the Docker image: `openjdk`, at the `latest` version and run it
* copy the file: `style-service-0.0.1-SNAPSHOT.jar` into the image once it has started
* execute the following command inside the image: `java -jar style-service-0.0.1-SNAPSHOT.jar`

__Build a Docker Image__

To build a Docker image, run the following in the same directory as the Dockerfile

```bash
docker build -t style-service-image:v1 .
```

After this command runs, this will add the newly created image to your local Docker image registry

You can validate that the image has been added to your local image registry by running the following: 

```bash
docker image ls style-service-image
```

which will yield the following output: 

```bash
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
style-service-image   v1                  abd73abea2f3        3 days ago          771MB
```

#### Write a Docker Compose YAML File, Deploy, and Validate

Observe the file `docker-compose.yaml`

```yaml
version: "3"
services:
  style-service:
    build: .
    image: "style-service-image:v1"
    container_name: "style-service"
    ports:
      - "8084:8084"
    links:
      - mongodb
    depends_on:
      - mongodb
    environment:
        SPRING_DATA_MONGODB_URI: mongodb://mongodb-instance/solsticedb
  mongodb:
    image: mongo:latest
    container_name: "mongodb-instance"
    volumes:
      - ./data/db:/data/db
    environment:
      - MONGO_DATA_DIR=/data/db
      - MONGO_LOG_DIR=/dev/null
    ports:
      - "27017:27017"
```

This file describes specifications for two containers: 

* style-service
* mongodb-instance

To bring these containers up, execute the following: 

```bash
docker-compose up
```

after running this command, you can validate the containers have started up by going to: 

http://localhost:8084/swagger-ui.html

Once you are finished, `ctrl-c` to bring the containers back down

## Style Profiles Web App - Containerization Walkthrough

This setup can be used for development and testing. You should keep this setup until you are ready to move on
to next steps, which involves dockerizing, deployment to docker and then eventually deploying to kubernetes.

Until then, use this setup judiciously for development. 

### Build, Test, and Run

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.5.0.

#### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

#### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

#### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

#### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

#### Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

#### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Docker Validation Setup

This setup is used to Containerize our application and validate it against Docker. 

### Dockerizing Your Service

At this point you have run this application from a development context

You are now ready to deploy and validate your application as a container

The next steps: 

* Build a Docker image of your service
* Test and validate the image by deploying locally to your Docker local runtime as a Container

If your are still running application, make sure to shutdown the application at this point. 

#### Build a Docker Image

__Build the application__ 

```bash
ng build
```

__Create a Dockerfile__

Open the file: `Dockerfile`

You will see the following contents: 

```dockerfile
FROM nginx:latest
COPY dist /usr/share/nginx/html
EXPOSE 80
```

The above lines mean: 
* pull down the Docker image: `nginx`, at the `latest` version and run it
* copy the folder: `dist` into the image container location `/usr/share/nginx/html`

__Build a Docker Image__

To build a Docker image, run the following in the same directory as the Dockerfile

```bash
docker build -t style-webapp-image:v1 .
```

After this command runs, this will add the newly created image to your local Docker image registry

You can validate that the image has been added to your local image registry by running the following: 

```bash
docker image ls style-webapp-image
```

which will yield the following output: 

```bash
REPOSITORY             TAG                 IMAGE ID            CREATED                  SIZE
style-webapp-image   v1                  2d1e99910e64        Less than a second ago   116MB
```

#### Write a Docker Compose YAML File, Deploy, and Validate

Observe the file `docker-compose.yaml`

```yaml
version: "3"
services:
  style-webapp:
    build: .
    image: "style-webapp-image:v1"
    container_name: "style-webapp"
    ports:
      - "8085:80"
```

This file describes specifications for two containers: 

* style-webapp

To bring this container up, execute the following: 

```bash
docker-compose up
```

after running this command, you can validate the container has started up by going to: 

http://localhost:8085

Once you are finished, `ctrl-c` to bring the containers back down

# Section 2: Local Kubernetes (Minikube) Setup and Cluster Deployment

_This section covers steps 5-8 of the prescribed workflow_

## Minikube : "Your Local K8S Sandbox Cluster"

__Start Minikube__

Open a new terminal (__NOTE: avoid using the same terminal in previous steps__)

Start Minikube: 

```bash
minikube start
```

then: 

```bash
eval $(minikube docker-env)
```

This will set the current shell's docker runtime to the docker runtime installed on the virtual machine that
is started up by Minikube. This is important to keep in mind, since the containers that Minikube uses, will be
located here. 

if you check to see which docker containers are running at this point, you'll see this list of containers differs 
significantly to the list of containers running in your local docker runtime. Try running the following in the 
current terminal and also in a previous terminal. 

```bash
docker ps
```

You'll see that the running containers are different. 

At this point you should be running a local Kubernetes cluster via Minikube

try running the following to bring up the Kubernetes dashboard: 

```bash
minikube dashboard
```

This will cause your browser to open up the Kubernetes Dashboard, which may look similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/4644146b-c3aa-4b20-a816-73378cd9e194/00000242.png)

__IMPORTANT NOTE:__ For all subsequent terminals opened used for working with Minikube, make sure to enter the following, prior to
working with your local cluster: 

```bash
eval $(minikube docker-env)
```

## Create Docker Images for Minikube Docker Image Registry

After running the command, `eval $(minikube docker-env)`, the local Docker Image Registry points to one created by Minikube. 

If you've installed images to your native Docker Registry, you will need to install those images again if you intend to use them
in your Minikube cluster. You can do this by running the commands to create and register your images as you did previously. 

From `style-service` directory: 

```bash
mvn clean package -DskipTests
docker build -t style-service-image:v1 .
```

from `style-webapp` directory:

```bash
npm install
ng build
docker build -t style-webapp-image:v1 .
```

verify the images exist in the registry by runnning the following: 

```bash
docker image ls
```

## Kubernetes YAML Generation, Tweaking, and Deployment

__Start Watching Your Kubernetes Cluster__

In a new terminal, enter the following: 

```bash
eval $(minikube docker-env)
watch -n 1 kubectl get pod,service,replicaset,deployment,persistentvolumeclaim,persistentvolume,ingress
```

This will open a watch window into what your local Kubernetes cluster looks like, which will update every second. 

Based on the command entered, we will be monitoring the following Kubernetes artifacts in your local cluster: 

* Pods
* Services
* Deployments
* Persistent Volume Claims
* Persistent Volumes
* Ingress Controllers

You'll see the following output in your terminal: 

```
Every 1.0s: kubectl get pod,service,replicaset,deployment,persistentvolumeclaim,persistentvolume,ingress        

NAME             TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
svc/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   9d
```

If and when these resources become available, they will be listed in the order requested. 

For the time being, keep this terminal open to keep tabs on the status of your cluster. 

__Generate Kubernetes Yaml and Install Artifacts : Style Service__

In previous steps, we generated docker-compose.yaml files. Those files can be used to generate Kubernetes yaml files
using the Kompose CLI application. 

Locate the file: `style-service/docker-compose.yaml`

at the terminal: 

```bash
kompose convert -f docker-compose.yaml --out k8s-kompose.yaml
```

A Kubernetes yaml file will be created. Below is a collapsed summary of what was generated: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/0fcef6b5-e7c9-4a63-803d-62b831a295ad/00000243.png)

At first glance, you'll see that there are five generated sections. If you expand the sections, you'll see the sections 
breakdown into the following: 

* two services
    * style service
    * mongodb
* two deployments
    * style service
    * mongodb
* one persistent volume claim
    * mongodb

This reflects similarly to the docker-compose.yaml file we create earlier. 

To install the artifacts from the Kubernetes Yaml file, `k8s-kompose.yaml`, run the following command: 

```bash
kubectl create -f k8s-kompose.yaml 
```

You'll see the terminal output the following: 

```
service "mongodb" created
service "style-service" created
deployment "mongodb" created
persistentvolumeclaim "mongodb-claim0" created
deployment "style-service" created
```

If you observe the watch window created earlier you'll see that it, too, has changed as well: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/61eb1fbb-7728-4c02-90cc-b162b388bfea/00000267.png)

If you open/refresh the Kubernetes dashboard, you'll see something similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/57110631-56a6-48b0-9c7e-44c41d816050/00000265.png)

__Generate Kubernetes Yaml and Install Artifacts : Style Profiles Web App__

Similarly as was done before, locate the file: `style-webapp/docker-compose.yaml`

at the terminal: 

```bash
kompose convert -f docker-compose.yaml --out k8s-kompose.yaml
```

The generated Kubernetes Yaml file will have the following (collapsed & summarized): 

![](https://content.screencast.com/users/Keun/folders/Jing/media/e4b8a76d-cfe9-433b-af9a-5a8e251f72e3/00000245.png)

 If you expand the sections, you'll see the sections 
breakdown into the following: 

* one service
    * style web app
* one deployment
    * style web app
    
This reflects similarly to the docker-compose.yaml file we create earlier. 

To install the artifacts from the Kubernetes Yaml file, `k8s-kompose.yaml`, run the following command: 

```bash
kubectl create -f k8s-kompose.yaml 
```

You'll see the terminal output the following: 

```
service "style-webapp" created
deployment "style-webapp" created
```

If you observe the watch window created earlier you'll see that it, too, has changed as well: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/02387bf3-51c2-4aee-a007-81965d3fa8d9/00000268.png)

If you open/refresh the Kubernetes dashboard, you'll see something similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/4f226fe7-0d78-4aad-b4c7-54f572dea512/00000269.png)

You now have a fully deployed backend service and frontend application in your Kubernetes cluster. 

One caveat though... You can't access the cluster externally yet, because you have not done either one of following: 

* Exposed ports by changing the Service Type to either: `NodePort` or `LoadBalancer`. See more on exposing ports in 
Kubernetes: https://kubernetes.io/docs/tutorials/stateless-application/expose-external-ip-address/
* Created an Ingress Controller which can be used to create publicly accessible url routes. 

## Setting up a Cluster Ingress Controller

__Enable Ingress on Minikube__

To setup an Ingress Controller on Minikube make sure the Minikube addon is enabled. To see which addons are
available and enabled: 

```bash
minikube addons list
```

You'll get a result which should look similar to the following: 

```
- addon-manager: enabled
- coredns: disabled
- dashboard: enabled
- default-storageclass: enabled
- efk: disabled
- freshpod: disabled
- heapster: disabled
- ingress: disabled
- kube-dns: enabled
- registry: disabled
- registry-creds: disabled
- storage-provisioner: enabled
```

To enable ingress: 

```bash
minikube addons enable ingress
```

__Create an Ingress Controller Yaml File__

Open up the file: `k8s-yaml/local/k8s-ingress.yaml`

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-demo
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  backend:
    serviceName: default-http-backend
    servicePort: 80
  rules:
  - http:
      paths:
      - path: /
        backend:
          serviceName: style-webapp
          servicePort: 8085
      - path: /style-service
        backend:
          serviceName: style-service
          servicePort: 8084
```

From the specification above, we can gather the following: 

* The Ingress Controller will be hosted on port 80, as specified in `spec/backend`
* All traffic sent to the Ingress IP is directed to the Kubernetes Service listed under `spec/backend`.
* Each host under `http/paths/path`, is given a route and a `backend` which exposes a Kubernetes service. 

For more information about Kubenetes Ingress Controllers see: https://kubernetes.io/docs/concepts/services-networking/ingress/
 
To create the ingress run the following: 

```bash
kubectl create -f k8s-ingress.yaml
``` 

if you go back to your watch window, you'll see the following line added: 

```
NAME               HOSTS     ADDRESS          PORTS     AGE
ing/ingress-demo   *         192.168.99.100   80        41s
```
 
At first you may not see the `ADDRESS` field populated. Give it a few minutes and it should populate with
an IP address. 
 
## Validating your Deployments

Goto the following url: http://[INGRESS_IP]

In this demo, that url is: http://192.168.99.100

You'll see the following web page: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/f526b578-bfc2-4998-b292-d12dd7fe6cf2/00000262.png)

Additionally visit the following url, and you should be taken to a Swagger page: https://192.168.99.100/style-service/swagger-ui.html

That page will look similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/bd1ebfb2-5448-429c-97ef-9944b63819bf/00000263.png)

# Section 3: Deployment to PKS

_This section covers step 9 of the prescribed workflow_

At this stage, you will be deploying containers to a PKS cluster. 

The following key points should be taken into consideration: 

* PKS clusters do NOT have an Ingress controller deployed by default. You will need to do that. We will go over that process in steps ahead. 
* There is no builtin container registry in PKS. You do have multiple options at your disposal to make this happen. A few for consideration: 
    * Harbor by VMWare - http://vmware.github.io/harbor/
    * Docker Hub - https://hub.docker.com/
    * Gitlab - https://about.gitlab.com/2016/05/23/gitlab-container-registry/
* Containers that require Persistent Volument Claims, will also require you to add a Storage Class. We will be adding a storage class for Mongodb. See about Kubernetes StorageClass here: https://kubernetes.io/docs/concepts/storage/storage-classes/

## Logging into a PKS and Selecting a Cluster

__Logging into PKS__

Login to PKS with the following login command.

```bash
export PKS_API=https://PKS_API_URL
pks login -a $PKS_API -u "pks_username" -p "pks_password" -k
```

For more specifics on where and how to obtain login credentials and PKS API url, see PKS Prerequisites: https://docs.pivotal.io/runtimes/pks/1-0/login.html

__Listing Clusters__

The following command will list available clusters in your PKS instance

```bash
pks list-clusters
```

resulting output: 

```
Name           Plan Name  UUID                                  Status     Action
my-cluster                9f484bd4-9eab-41ef-a0d1-a81f4fd3e061  succeeded  CREATE
```

__Selecting a Cluster to Manage__

The following command will select a cluster for management by kubectl

```bash
pks get-credentials my-cluster
```

resulting output: 

```
Fetching credentials for cluster my-cluster.
Context set for cluster my-cluster.

You can now switch between clusters by using:
$kubectl config set-context <cluster-name>
```

When you have multiple clusters, you can switch between clusters by using the PKS command above or by using kubectl as stated above. 

At this point, once you've selected a cluster to manage, you can run kubectl commands to manage your cluster as you would any k8s cluster. 

## Kubernetes YAML Files 

All K8S yaml files for this portion of the exercise can be found in 

```
k8s-yaml/pks
```

## Deploying Containers

__Open a Watch Window and K8S Dashboard__

Open a watch window by executing the following: 

```
watch -n 1 kubectl get pod,service,replicaset,deployment,persistentvolumeclaim,persistentvolume,ingress,storageclass,configmap
```

This will open up a window into the status of your cluster via terminal and update after every second. 

Additionally, you may also open up a K8S dashboard into your cluster by executing the following (do this in a seperate terminal): 

```
kubectl proxy
```

Afterwards, point your browser to the following url: http://localhost:8001/ui

__Deploying Style Rest API Service__

We'll need to create a storage class so that Mongodb knows where to store and persist data on a file system.

When running a local cluster such as Minikube, the file system volume is an already known asset to the cluster. Essentially, your local file system can act as a storage class and by doing so, can be associated to a persistent volume. This is an important distinction to take note of, when doing work on a local K8S cluster vs a cloud native K8S cluster. 

To create a storage class the following from w/in the folder `k8s-yaml/pks/style-service`:

```bash
kubectl create -f mongodb-storage-class.yaml
```

After your storage is created, then execute the script: `k8s-kompose.yaml`:

```bash
kubectl create -f k8s-kompose.yaml
```

The above script is slightly different then the generated script we executed before in our local cluster. That difference can be seen by taking a look at the persistent volume claim section in the script: 

```yaml
apiVersion: v1
  kind: PersistentVolumeClaim
  metadata:
    creationTimestamp: null
    labels:
      io.kompose.service: mongodb-claim0
    name: mongodb-claim0
    annotations:
        volume.beta.kubernetes.io/storage-class: mongodb-storage
  spec:
    accessModes:
    - ReadWriteOnce
    resources:
      requests:
        storage: 100Mi
    storageClassName: mongodb-storage
  status: {}
```

Additionally, since our cluster is not local, we need to change the reference of the container image to pull down. Since we aren't specifying a container registry to pull down from, we've specified a public container to pull down from. In this use case, from Docker Hub. If you scroll down to the Deployment section you'll see that we've changed the image to pull from to the following: 

```yaml
image: keunlee48105/style-service-image:v1
```

Which will grab the container from the following docker hub repository: https://hub.docker.com/r/keunlee48105/style-service-image/ 

You will probably want to change this so that images are pulled down from a private image repository. 

As you can see, we've tweaked the original script. 

You can see we are referencing the storage class in metadata annotataions as well as inside the spec. 

At this point your watch window will have a similar display as illustrated below: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/1bd3138d-1a7d-4ec1-93fd-3f2bca09ea11/00000271.png)

__Deploying Style Profiles Web App__

To deploy the web app into our cluster, we'll execute the script located in `k8s-yaml/pks/style-webapp`

In comparison to the script that we ran earlier for our local cluster, you'll see that we've changed the image to pull from to the following (Docker Hub):

```yaml
image: keunlee48105/style-webapp-image:v1
```

Which will grab the container from the following docker hub repository: https://hub.docker.com/r/keunlee48105/style-webapp-image/ 

Execute the following: 

```bash
kubectl create -f k8s-kompose.yaml
```

After some time, your watch window will look similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/f7ba6f03-2e89-424c-bdc8-dd3fcaceec04/00000272.png)

## Adding an Ingress Controller

PKS does not come with a default selectable Ingress Controller. Because of this, we'll have to add one to our cluster. 

__Intro to Helm__

The easiest way to install an Ingress Controller, is through Helm Charts. 

Helm Charts is a package manager for Kubernetes. Rather then going through various yaml scripts to deploy a 
set of containers to create a software suite, you can simply run a search against Helm charts to find a desired package. 

See: 

* https://helm.sh/
* https://kubeapps.com/

__Initialize Helm on Your Cluster__

To initialize Helm on your cluster, 

```bash
helm init
```

Once initialized, you will be able to begin installing Helm packages, known as Charts, in your cluster. 

__Install Nginx Ingress Controller__

To search for an Ingress Controller to install type the following: 

```bash
helm search ingress
```

You will get the following similar output: 

```
NAME                	CHART VERSION	APP VERSION	DESCRIPTION
stable/nginx-ingress	0.11.3       	0.11.0     	An nginx Ingress controller that uses ConfigMap...
stable/external-dns 	0.5.2        	0.4.8      	Configure external DNS servers (AWS Route53, Go...
stable/lamp         	0.1.4        	           	Modular and transparent LAMP stack chart suppor...
stable/nginx-lego   	0.3.1        	           	Chart for nginx-ingress-controller and kube-lego
stable/traefik      	1.25.2       	1.5.4      	A Traefik based Kubernetes ingress controller w...
stable/voyager      	3.2.1        	6.0.0      	Voyager by AppsCode - Secure Ingress Controller...
```

You will want to install the helm package: `stable/nginx-ingress`

more info on this package can be found here: https://github.com/kubernetes/ingress-nginx

To install, type: 

```bash
helm install stable/nginx-ingress --name nginx-ingress --version 0.11.3
```

After installation is complete, look over to your watch window. It should look similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/493d3e86-50fb-4777-b505-f530e59c3018/00000273.png)

__Configure Nginx Ingress Controller__

In this step we will be modifying the Nginx Controller to a static load balancer. 

More information on this here: https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/static-ip

To do this, we will need to modify the yaml for the nginx controller deploymment. We have a few options to do this: 

(1) Modify via command line: 

```
kubectl edit deployment nginx-ingress-controller
```

(2) Modify via K8S Dashboard

If you haven't already, create a proxy to the K8S Cluster Dashboard: 

```bash
kubectl proxy
```

Goto the url: http://localhost:8001/ui

Look for the deployment: `nginx-ingress-controller`

And click on the menu option to edit it's YAML

![](https://content.screencast.com/users/Keun/folders/Jing/media/0673d3b1-1262-44e0-b2ed-fa7811986b59/00000274.png)

Once clicked on add the following entry to the `containers` section in the yaml as illustrated below: 

```
"--publish-service=default/nginx-ingress-controller"
```

![](https://content.screencast.com/users/Keun/folders/Jing/media/8ae476bf-ffd8-4435-bd74-45fc39f0d78f/00000275.png)

Once updated your deployment will be redeployed

You now should have a running ingress controller that you can create an Ingress against. 

__Configure and Install an Ingress__

To deploy an Ingress into our cluster, we'll execute the script located in `k8s-yaml/pks/k8s-ingress.yaml`

```bash
kubectl create -f k8s-ingress.yaml
```

You'll notice that there are some differences from when we last ran this script against our local cluster. 

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-demo
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  backend:
    serviceName: nginx-ingress-default-backend
    servicePort: 80
  rules:
  - http:
      paths:
      - path: /
        backend:
          serviceName: style-webapp
          servicePort: 8085
      - path: /style-service
        backend:
          serviceName: style-service
          servicePort: 8084
```

We've tweaked the script in the following ways: 

* specified an ingress class: `kubernetes.io/ingress.class: nginx`
* changed the name of the service name of the ingress controller: `serviceName: nginx-ingress-default-backend`

At this point our watch window will look similar to the following: 

![](https://content.screencast.com/users/Keun/folders/Jing/media/0677d511-9c9c-43e9-8de2-8841028ee659/00000276.png)

The Ingress address should point to the Load Balancer's external IP address. This means, that the two highlighted addresses should match. 

At this point, navigate to the Ingress's Address in your browser: http://[INGRESS_IP] 

You should see that the web application has been deployed and is accessible via the ingress you just deployed. 