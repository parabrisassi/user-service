# user-service

Service in charge of managing users, together with authentication and authorization

## Getting started

1. Install postgres, and set the corresponding user, password and database, or set application properties to use a remote postgres database.

2. Change working directory to ```<project-root>```

3. Install project modules

	``` 
	$ mvn clean install
	```

4. Build the project
	
	``` 
	$ mvn clean package
	```

5. Run the application
	
	``` 
	$ java -jar <-Dproperties> <project-root>/user-service-webapp/target/user-service-webapp-0.0.1-SNAPSHOT.jar
	```
