FROM java:8
WORKDIR /
ADD ./target/UserPreferencesService.jar UserPreferencesService.jar
ENTRYPOINT ["java", "-Dprocess.name=UserPreferencesService", "-jar", "UserPreferencesService.jar"]