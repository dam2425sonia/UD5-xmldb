import org.xmldb.api.base.*;
import org.xmldb.api.*;
import org.xmldb.api.modules.XQueryService;

public class XMLDBExample {

    public static void main(String[] args) {
        try {
            // Configura la URI del driver (en este caso, el de una base de datos de ejemplo)
            String uri = "xmldb:exist://localhost:8080/exist/xmlrpc"; // Cambia a tu configuración

            // Configura el nombre de la colección en la base de datos
            String collectionURI = "/db/Europa"; // Reemplaza con tu colección específica

            // Registra el driver
            Class<?> driver = Class.forName("org.exist.xmldb.DatabaseImpl");
            Database database = (Database) driver.getDeclaredConstructor().newInstance();

            // Obtén la conexión a la base de datos XML
            DatabaseManager.registerDatabase(database);

            // Conectar a la colección
            Collection col = DatabaseManager.getCollection(uri + collectionURI, "admin", "");

            if (col == null) {
                System.out.println("No se pudo conectar a la base de datos.");
                return;
            }
            System.out.println("Conexion exitosa");

            // Realiza una consulta XPath o XQuery
            String query = "for $doc in collection('/db/Europa')//pais return $doc"; // Ejemplo XQuery
            /*  ó esta otra Consulta XQuery correcta
            String query = "for $pais in doc('/db/Europa/Europa.xml')//pais " +
                        "return <nombre>{data($pais/nombre)}</nombre>";
            */          

            // Crear el servicio XQuery
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.setProperty("indent", "yes");

            // 1. Compilar la consulta
            CompiledExpression compiled = service.compile(query);

            // 2. Ejecutar la consulta
            ResourceSet result = service.execute(compiled);

            // Procesar los resultados
            ResourceIterator i = result.getIterator();
            while (i.hasMoreResources()) {
                Resource res = i.nextResource();
                System.out.println(res.getContent());
            }

            // Cerrar la colección
            col.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
