import pandas as pd
import requests
from sklearn.neighbors import NearestNeighbors
from sklearn.decomposition import TruncatedSVD
from data_procesing.procesar_datos import preparar_datos
import warnings
        

class Recomendador:
    def __init__(self, latitud=4.6097, longitud=-74.0817):
        """Inicializa el recomendador cargando los datos de lugares y preferencias en la ubicación dada."""
        self.df = preparar_datos(latitud, longitud)  # Obtiene los lugares cercanos desde OpenStreetMap
        self.ratings = pd.read_csv("user_data/preferencias_usuarios.csv")  # Carga calificaciones
        self.modelo_knn = None
        self.modelo_svd = None
        warnings.simplefilter(action='ignore', category=UserWarning)
        self._entrenar_modelo()

    def _entrenar_modelo(self):
        """Entrena modelos de Filtrado Colaborativo usando k-NN y SVD."""
        matriz_usuario_lugar = self.ratings.pivot(index="usuario", columns="lugar", values="calificacion").fillna(0)

        # Modelo k-NN (Usuarios Similares)
        self.modelo_knn = NearestNeighbors(metric="cosine", algorithm="brute")
        self.modelo_knn.fit(matriz_usuario_lugar)

        # Modelo SVD (Descomposición en Componentes Latentes)
        self.modelo_svd = TruncatedSVD(n_components=min(5, matriz_usuario_lugar.shape[1] - 1))
        self.modelo_svd.fit(matriz_usuario_lugar)

    def actualizar_modelo(self):
        """
        Vuelve a entrenar el modelo de recomendación después de actualizar los datos.
        """
        self.ratings = pd.read_csv("user_data/preferencias_usuarios.csv")  # Recargar datos
        matriz_usuario_lugar = self.ratings.pivot(index="usuario", columns="lugar", values="calificacion").fillna(0)

        # Reentrenar k-NN
        self.modelo_knn.fit(matriz_usuario_lugar)

        # Reentrenar SVD
        self.modelo_svd.fit(matriz_usuario_lugar)

    def recomendar_usuario(self, usuario, top_n=5):
        """Recomienda lugares según usuarios similares usando k-NN."""
        if usuario not in self.ratings["usuario"].values:
            return ["Usuario no encontrado."]

        matriz_usuario_lugar = self.ratings.pivot(index="usuario", columns="lugar", values="calificacion").fillna(0)
        usuario_idx = list(matriz_usuario_lugar.index).index(usuario)
        
        distancias, indices = self.modelo_knn.kneighbors([matriz_usuario_lugar.iloc[usuario_idx]], n_neighbors=3)
        usuarios_similares = [matriz_usuario_lugar.index[i] for i in indices.flatten() if i != usuario_idx]

        recomendaciones = self.ratings[self.ratings["usuario"].isin(usuarios_similares)]
        recomendaciones = recomendaciones.groupby("lugar")["calificacion"].mean().sort_values(ascending=False).head(top_n)

        return recomendaciones.index.tolist()
    
    def obtener_clima(self, latitud, longitud):
        """Obtiene el clima actual en una ubicación específica usando Open-Meteo."""
        headers = {"User-Agent": "HangOutGuide/1.0 (contacto@ejemplo.com)"}
        try:
            clima_url = f"https://api.open-meteo.com/v1/forecast?latitude={latitud}&longitude={longitud}&current_weather=true"
            respuesta_clima = requests.get(clima_url, headers=headers)

            if respuesta_clima.status_code != 200:
                print("Error al obtener clima:", respuesta_clima.status_code)
                return None, None

            datos_clima = respuesta_clima.json()
            return datos_clima["current_weather"]["temperature"], datos_clima["current_weather"]["weathercode"]

        except requests.exceptions.RequestException as e:
            print(f"Error de red: {e}")
            return None, None
        except ValueError as e:
            print(f"Error al decodificar JSON: {e}")
            return None, None

    def recomendar_segun_clima(self, latitud, longitud, top_n=5):
        """Recomienda lugares según el clima actual en la ubicación dada."""
        temp, clima = self.obtener_clima(latitud, longitud)
        if temp is None:
            return ["No se pudo obtener el clima."]

        # Clasificación dinámica según los tipos de lugares disponibles
        lugares_disponibles = set(self.df["tipo"].unique())

        # Definir categorías generales de lugares según el clima
        lugares_al_aire_libre = {"park", "viewpoint", "attraction", "picnic_site", "theme_park"}
        lugares_bajo_techo = {"museum", "gallery", "artwork", "wine_cellar", "hotel", "restaurant", "shopping_mall"}

        # Filtrar por los lugares que realmente existen en la ciudad
        lugares_exteriores = list(lugares_al_aire_libre & lugares_disponibles)
        lugares_interiores = list(lugares_bajo_techo & lugares_disponibles)

        # Decidir los lugares según el clima
        if clima in [1, 2, 3]:  # Soleado o nublado
            tipo_lugares = lugares_exteriores if lugares_exteriores else lugares_interiores
        else:  # Lluvia o frío
            tipo_lugares = lugares_interiores if lugares_interiores else lugares_exteriores

        # Filtrar el dataframe con los lugares seleccionados
        df_filtrado = self.df[self.df["tipo"].isin(tipo_lugares)]

        return df_filtrado["nombre"].sample(n=min(top_n, len(df_filtrado))).tolist()

    def recomendar_por_ubicacion(self, latitud, longitud, radio_km=5, top_n=5):
        """Recomienda lugares cercanos en un radio específico (por defecto, 5 km)."""
        radio_grados = radio_km / 111  # Conversión aproximada de km a grados de latitud/longitud
        df_cercanos = self.df[
            (self.df["lat"].between(latitud - radio_grados, latitud + radio_grados)) &
            (self.df["lon"].between(longitud - radio_grados, longitud + radio_grados))
        ]
        return df_cercanos.sample(n=min(top_n, len(df_cercanos)))["nombre"].tolist()
    
    def recomendar_por_interes(self, intereses_usuario):
        """
        Filtra lugares según intereses del usuario.
        """     
        df_filtrado = self.df[self.df["tipo"].isin(intereses_usuario)]
        return df_filtrado.sample(n=min(5, len(df_filtrado)))["nombre"].tolist()

