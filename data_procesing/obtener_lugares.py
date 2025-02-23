import requests

def obtener_lugares(ciudad="Bogotá"):
    query = f"""
    [out:json];
    area[name="{ciudad}"]->.searchArea;
    (
      node["tourism"](area.searchArea);
      way["tourism"](area.searchArea);
      relation["tourism"](area.searchArea);
    );
    out center;
    """
    url = "http://overpass-api.de/api/interpreter"
    response = requests.get(url, params={"data": query})

    if response.status_code == 200:
        data = response.json()
        lugares = []
        for lugar in data["elements"]:
            nombre = lugar.get("tags", {}).get("name", "Desconocido")
            tipo = lugar.get("tags", {}).get("tourism", "No definido")
            lat = lugar.get("lat", "N/A")
            lon = lugar.get("lon", "N/A")
            lugares.append({"nombre": nombre, "tipo": tipo, "lat": lat, "lon": lon})
        return lugares
    else:
        return None


def obtener_lugares_por_ubicacion(latitud, longitud, radio_km=5):
    """
    Obtiene lugares turísticos cerca de una ubicación específica (lat, lon) en un radio determinado.
    """
    query = f"""
    [out:json];
    (
      node["tourism"](around:{radio_km * 1000},{latitud},{longitud});
      way["tourism"](around:{radio_km * 1000},{latitud},{longitud});
      relation["tourism"](around:{radio_km * 1000},{latitud},{longitud});
    );
    out center;
    """
    
    url = "http://overpass-api.de/api/interpreter"
    response = requests.get(url, params={"data": query})

    if response.status_code == 200:
        data = response.json()
        lugares = []
        for lugar in data["elements"]:
            nombre = lugar.get("tags", {}).get("name", "Desconocido")
            if nombre == "Desconocido":
                continue
            tipo = lugar.get("tags", {}).get("tourism", "No definido")

            # Para los nodos, lat y lon están directamente disponibles
            if "lat" in lugar and "lon" in lugar:
                lat = lugar["lat"]
                lon = lugar["lon"]
            # Para ways y relations, se usa el centroide (center)
            elif "center" in lugar:
                lat = lugar["center"]["lat"]
                lon = lugar["center"]["lon"]
            else:
                lat, lon = "N/A", "N/A"
            # info_extra = obtener_info_wikipedia(nombre)

            lugares.append({"nombre": nombre, "tipo": tipo, "lat": lat, "lon": lon})
        
        return lugares
    else:
        print(f"Error {response.status_code}: No se pudo obtener datos de Overpass API")
        return None


def obtener_info_wikipedia(lugar):
    """
    Busca información en Wikipedia sobre un lugar específico.
    """
    url = f"https://es.wikipedia.org/api/rest_v1/page/summary/{lugar.replace(' ', '_')}"
    response = requests.get(url)

    if response.status_code == 200:
        data = response.json()
        return {
            "titulo": data.get("title", lugar),
            "extracto": data.get("extract", "No hay información disponible."),
            "url": data.get("content_urls", {}).get("desktop", {}).get("page", "#")
        }
    return {"extracto": "No hay información disponible."}
