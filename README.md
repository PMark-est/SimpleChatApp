# Login screem

Login uses JWT

![image](https://github.com/user-attachments/assets/05f1e295-f90b-4fbe-84d7-1d1abfff6272)

# Chat

POV user admin:

![image](https://github.com/user-attachments/assets/129c020f-f5c1-4772-961d-0ffce97c31be)

POV user best:

![image](https://github.com/user-attachments/assets/25e5a44e-4e3a-4d16-a55b-b6306fb0f03a)


## Add groups

![image](https://github.com/user-attachments/assets/35fffc11-ba94-4556-af81-dc4556cbaf4f)

## Add users to groups

![image](https://github.com/user-attachments/assets/b4e8d45e-1e89-4be9-93c3-5714b0ca00a5)


# How to run

Install postgre database (provded docker image, but can run local db aswell):
```docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -d postgres```

in the frontend folder
```npm i```
then
```npm start```.

in the backend folder run
```gradlew bootRun```
or
```./gradlew bootRun```
