#= Build ============================================================
FROM adoptopenjdk/openjdk11:alpine-slim as build

# copy gradle resources
COPY *.gradle.kts gradlew /workspace/
COPY gradle /workspace/gradle
WORKDIR /workspace
# download gradle wrapper (for caching)
RUN ./gradlew --version

# copy application
COPY . .
RUN ./gradlew -Dorg.gradle.daemon=false :assemble
RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../*.jar)

#= Run ==============================================================
FROM adoptopenjdk/openjdk11-openj9:alpine-slim
EXPOSE 8080
VOLUME /tmp
VOLUME /log
COPY --from=build /workspace/build/libs/dependency/BOOT-INF/lib /app/lib
COPY --from=build /workspace/build/libs/dependency/META-INF /app/META-INF
COPY --from=build /workspace/build/libs/dependency/BOOT-INF/classes /app
RUN addgroup -S iwashere && adduser -S iwashere -G iwashere
USER iwashere
ENTRYPOINT java -Xtune:virtualized -cp app:app/lib/* li.doerf.iwashere.IwashereApplicationKt
