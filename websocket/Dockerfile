# Utilise une image Java officielle
FROM openjdk:17

# Crée un répertoire de travail
WORKDIR /app

# Copie tous les fichiers dans le conteneur
COPY . /app

# Télécharge la dépendance java-websocket si tu ne passes pas par Maven/Gradle
# Sinon, utilise Maven ou Gradle pour gérer les dépendances
RUN mkdir -p lib && \
    curl -L -o lib/java-websocket.jar https://repo1.maven.org/maven2/org/java-websocket/Java-WebSocket/1.5.3/Java-WebSocket-1.6.0.jar

# Compile ton serveur
RUN javac -cp "lib/java-websocket.jar" WebSocketChatServer.java

# Lance ton serveur
CMD ["java", "-cp", ".:lib/java-websocket.jar", "WebSocketChatServer"]
