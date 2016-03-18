package Controller;

import Configuration.Configuration;
import Model.AsyncMasterClient;
import Model.AsyncVotingStore;
import Model.MasterClient;
import Model.VotingDB;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author Robin Duda
 * <p/>
 * Start up the webserver.
 */
public class WebServer implements Verticle {
    private AsyncVotingStore votings;
    private AsyncMasterClient client;
    private Vertx vertx;

    public WebServer() {
    }

    public WebServer(AsyncVotingStore votings, AsyncMasterClient client) {
        this.votings = votings;
        this.client = client;
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;

        if (votings == null) {
            votings = new VotingDB(
                    MongoClient.createShared(vertx,
                            new JsonObject()
                                    .put("connection_string", Configuration.CONNECTION_STRING)
                                    .put("db_name", Configuration.DB_NAME)
                    )
            );
        }

        if (client == null) {
            client = new MasterClient(vertx);
        }
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        new APIRouter().register(router, votings, client);

        server.requestHandler(router::accept).listen(Configuration.CONTROLLER_PORT);
        future.complete();
    }

    @Override
    public void stop(Future<Void> future) throws Exception {
        future.complete();
    }
}
