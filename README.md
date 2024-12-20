# Setup environment variables
1. Create a `.env` file in the root directory of the project.
```shell
cp .env.example .env
```
2. Setup the environment variables of firebase in the `firebase.properties` file.
```shell
cp services/account-service/src/main/resources/firebase.template.properties services/account-service/src/main/resources/firebase.properties
```
Write the firebase configuration in the `firebase.properties` file.
# Run the project
1. Run the following command to start the project.
```shell
docker-compose up -d mysql rabbitmq
```
2. Create database
```shell
cd database
chmod +x create_db.sh
./create_db.sh
```
3. Restart the project.
```shell
docker-compose up -d
```