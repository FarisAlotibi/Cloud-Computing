import com.datastax.driver.core.*;

public class CassandraService {

    private static CassandraService instance;

    private Cluster cluster;
    private Session session;
    private String[] CONTACT_POINTS = {"104.131.73.39"};
    private int PORT = 9042;

    private CassandraService() {}

    public static CassandraService getInstance() {
        if(CassandraService.instance == null) {
            CassandraService.instance = new CassandraService();
        }

        return CassandraService.instance;
    }

    public void connect() {

        // The Cluster object is the main entry point of the driver.
        // It holds the known state of the actual Cassandra cluster (notably the Metadata).
        // This class is thread-safe, you should create a single instance (per target Cassandra cluster), and share
        // it throughout your application.
        this.cluster = Cluster.builder()
                .addContactPoints(CONTACT_POINTS)
                .withPort(PORT)
                .build();
        PoolingOptions poolingOptions = cluster.getConfiguration().getPoolingOptions();
        poolingOptions
            .setMaxRequestsPerConnection(HostDistance.LOCAL, 32768)
            .setMaxRequestsPerConnection(HostDistance.REMOTE, 2000);
        Metadata meta = cluster.getMetadata();

        System.out.println("Connected to Cassandra: " + meta.getClusterName());

        for(Host host:meta.getAllHosts()) {
            System.out.println("Datacenter: " + host.getDatacenter() + ", Host: " + host.getAddress() + ", Rack: " + host.getRack());
        }

        // The Session is what you use to execute queries. Likewise, it is thread-safe and should be reused.
        this.session = this.cluster.connect();
    }

    public void createSchema() {
        this.dropSchema();
        //// Create the keyspace 
        session.execute("CREATE KEYSPACE Log_Analysis WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : '2' };");
        //// Create the 1st table 
        // session.execute("CREATE TABLE Log_Analysis.access_log_ip ( id int, ip text, identity text, username text, processing_time text, request text, status_code text, object_size text, PRIMARY KEY (ip, id));");
        session.execute("CREATE TABLE Log_Analysis.access_log ( id int, ip text, identity text, username text, processing_time text, request text, status_code text, object_size text, PRIMARY KEY ((ip), request, id));");
        //// Create the 2nd table 
        session.execute("CREATE TABLE Log_Analysis.access_log_request ( id int, ip text, identity text, username text, processing_time text, request text, status_code text, object_size text, PRIMARY KEY (request, id));");
    }

    public void createIndex() {
        // create a secondary index for request to enable LIKE operation
        session.execute("CREATE CUSTOM INDEX ON Log_Analysis.access_log(request)  USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = { 'analyzer_class' : 'org.apache.cassandra.index.sasi.analyzer.NonTokenizingAnalyzer', 'case_sensitive' : 'false', 'mode' : 'CONTAINS' }; ");
    }

    public void dropSchema() {
        // We use execute to send a query to Cassandra.
        //// Drop the existing keyspace 
        session.execute("DROP KEYSPACE IF EXISTS Log_Analysis;");
        System.out.println("Cassandra schema dropped!");
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        this.cluster.close();
        System.out.println("Cassandra connection closed");
    }

}