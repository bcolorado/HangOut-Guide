from models.recomendaciones import Recomendador
from data_procesing.procesar_datos import registrar_visita

if __name__ == "__main__":
    recomendador = Recomendador()
    lat, lon = 4.6097, -74.0817  # Bogotá

    # Obtener clima en la ubicación
    temp, clima = recomendador.obtener_clima(lat, lon)
    print(f"Temperatura: {temp}°C, Código de Clima: {clima}")

    # Obtener recomendaciones según el clima
    print("Recomendaciones por clima:", recomendador.recomendar_segun_clima(lat, lon))

    # Obtener lugares cercanos
    print("Recomendaciones cercanas:", recomendador.recomendar_por_ubicacion(lat, lon, radio_km=3))

    # Recomendaciones para un usuario
    print("Recomendaciones para usuario:", recomendador.recomendar_usuario("user2"))

    registrar_visita("user10", "Parque Simón Bolívar", 3)

    recomendador.actualizar_modelo()

    # Recomendaciones para un usuario
    print("Recomendaciones para usuario:", recomendador.recomendar_usuario("user2"))