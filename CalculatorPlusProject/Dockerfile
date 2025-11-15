# Use a imagem oficial do OpenJDK 11
FROM openjdk:11-jre-slim-buster

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR do projeto para dentro do container
COPY target/CalculatorPlus-1.0.0.jar app.jar

# Expõe a porta que seu Spring Boot vai rodar
EXPOSE 9000

# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
