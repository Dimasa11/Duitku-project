# Ambil image dari OpenJDK 11 (bisa disesuaikan dengan versi Java yang digunakan)
FROM openjdk:11-jre-slim

# Buat direktori untuk aplikasi
WORKDIR /app

# Salin file JAR aplikasi dari direktori target (pastikan aplikasi sudah dibuild sebelumnya)
COPY target/duitku-0.0.1-SNAPSHOT.jar /app/duitku-0.0.1-SNAPSHOT.jar

# Eksekusi perintah ketika container berjalan
CMD ["java", "-jar", "duitku-0.0.1-SNAPSHOT.jar"]