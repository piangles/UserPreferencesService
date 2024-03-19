FROM eclipse-temurin:17-jre-alpine
WORKDIR /
ADD ./target/UserPreferencesService.jar UserPreferencesService.jar
ENTRYPOINT ["java", "-Dprocess.name=UserPreferencesService", "-jar", "UserPreferencesService.jar"]
