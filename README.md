# User service [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Build Status](https://travis-ci.org/parabrisassi/user-service.svg?branch=master)](https://travis-ci.org/parabrisassi/user-service)

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

## License

Copyright 2018 Parabrisas San Isidro SRL

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
