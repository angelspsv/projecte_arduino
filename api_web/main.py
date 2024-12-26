from fastapi import FastAPI, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import mysql.connector
from pydantic import BaseModel
from typing import Union
from typing import Optional


app = FastAPI()

#endpoint de prueba
@app.get("/")
def read_root():
    return { "Hello world!" }


# Configuración de CORS (si la app está en otro dominio o puerto)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Cambiar a dominios específicos en producción
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

   

# Configuracio de la base de dades
db_config = {
    "host": "192.168.12.2",  # Cambiar por la IP o host de tu servidor MariaDB
    "user": "root",         # Usuario de la base de datos
    "password": "pirineus", # Contraseña de la base de datos
    "database": "RegistreAsistencia",  # Nombre de la base de datos
    "port": 3306
}


# Modelo Pydantic 
class Usuari(BaseModel):
    nom: str
    cognom: str
    email: str
    dni: str


# Endpoint para insertar un nuevo usuario
@app.post("/nou_usuari/")
async def nou_usuari(
    nom: str = Form(...),
    cognom: str = Form(...),
    mail: str = Form(...),
    dni: str = Form(...)
):
    try:
        # Conexión a la base de datos
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

        # Consulta SQL para insertar datos
        query = """
        INSERT INTO Usuaris (nom, cognom, mail, dni, profe)
        VALUES (%s, %s, %s, %s, %s)
        """

        #establecemos que profe = 0 por defecto
        values = (nom, cognom, mail, dni, 0)

        # Ejecutar la consulta
        cursor.execute(query, values)
        connection.commit()

        # Cerrar conexión
        cursor.close()
        connection.close()

        return {"message": "Usuari creat correctament"}

    except mysql.connector.Error as err:
        raise HTTPException(status_code=500, detail=f"Error amb la base de dades: {err}")

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error inesperat: {e}")






@app.get("/inici_sessio/")
async def inici_sessio(email: str):
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        # Consulta para verificar el email
        query = "SELECT * FROM Usuaris WHERE mail = %s"
        cursor.execute(query, (email,))
        user = cursor.fetchone()
        conn.close()

        if user:
            return {"exists": True}  # Email encontrado
        else:
            return {"exists": False}  # Email no encontrado

    except mysql.connector.Error as e:
        raise HTTPException(status_code=500, detail=f"Error en la base de datos: {e}")
    



class User(BaseModel):
    id_usuari: int
    id_grup: Optional[int]
    nom: str
    cognom: str
    mail: str
    dni: str
    profe: int


@app.get("/inici_sessio_dades/")
async def inici_sessio_dades(email: str):
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        # Consulta para obtener datos desde el email
        query = "SELECT * FROM Usuaris WHERE mail = %s"
        cursor.execute(query, (email,))
        user = cursor.fetchone()
        conn.close()

        if user:
            # Mapeamos la tupla de la base de datos a un diccionario
            return User(
                id_usuari=user[0],
                id_grup=user[1] if user[1] is not None else None,
                nom=user[2],
                cognom=user[3],
                mail=user[4],
                dni=user[5],
                profe=user[6]
            )
        else:
            # Si no se encuentra el usuario
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

    except mysql.connector.Error as e:
        raise HTTPException(status_code=500, detail=f"Error en la base de datos: {e}")