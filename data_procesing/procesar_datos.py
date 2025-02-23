import pandas as pd
from .obtener_lugares import obtener_lugares, obtener_lugares_por_ubicacion

def preparar_datos(latitud, longitud):
    lugares = obtener_lugares_por_ubicacion(latitud, longitud)
    df = pd.DataFrame(lugares)

    # Llenar valores nulos
    df.fillna({"nombre": "Desconocido", "tipo": "No definido"}, inplace=True)

    # Convertir coordenadas a strings
    df["coordenadas"] = df["lat"].astype(str) + "," + df["lon"].astype(str)

    return df


def registrar_visita(usuario, lugar, calificacion=5):
    """
    Registra la visita de un usuario a un lugar y actualiza el archivo de calificaciones,
    evitando entradas duplicadas.
    """
    archivo_csv = "C:\\Users\\laura\\Documents\\Universidad\\2024-2\\DADM\\Proyecto\\HangOut-Guide\\user_data\\preferencias_usuarios.csv"

    # Cargar datos existentes o crear DataFrame vacío
    try:
        df = pd.read_csv(archivo_csv)
    except FileNotFoundError:
        df = pd.DataFrame(columns=["usuario", "lugar", "calificacion"])

    # Verificar si la entrada ya existe
    if not ((df["usuario"] == usuario) & (df["lugar"] == lugar)).any():
        # Agregar nueva entrada si no existe
        nueva_fila = pd.DataFrame([{"usuario": usuario, "lugar": lugar, "calificacion": calificacion}])
        df = pd.concat([df, nueva_fila], ignore_index=True)

        # Guardar actualización
        df.to_csv(archivo_csv, index=False)
        print(f"Visita registrada: {usuario} visitó {lugar}.")
    else:
        print(f"La visita de {usuario} a {lugar} ya está registrada.")


