FROM java:8
WORKDIR /
ADD ./target/UserPreferenceService.jar UserPreferenceService.jar
ENTRYPOINT ["java", "-Dprocess.name=UserPreferenceService", "-jar", "UserPreferenceService.jar"]