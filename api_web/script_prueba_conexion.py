import mysql.connector

db_config = {
    "host": "192.168.12.2",
    "user": "root",
    "password": "pirineus",
    "database": "RegistreAsistencia",
    "port": 3306
}

print("iniciando conexion...")

try:
    conn = mysql.connector.connect(**db_config)
    print("¡Conexión exitosa!")
    conn.close()
except mysql.connector.Error as e:
    print(f"Error al conectar: {e}")