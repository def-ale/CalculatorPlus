# Stage 1: Build do projeto
FROM maven:3.9.4-eclipse-temurin-11 AS build

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache do Docker
COPY pom.xml .

# Baixa as dependências do Maven (cacheável)
RUN mvn dependency:go-offline -B

# Copia todo código-fonte
COPY src ./src

# Faz o build do projeto (produz o JAR)
RUN mvn clean package -DskipTests

# Stage 2: Imagem final mais leve
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copia o JAR do stage de build
COPY --from=build /app/target/CalculatorPlus-1.0.0.jar app.jar

# Porta que a aplicação vai expor
EXPOSE 9000

# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
