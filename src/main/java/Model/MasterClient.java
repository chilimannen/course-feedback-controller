package Model;

import Configuration.Configuration;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * @author Robin Duda
 *         <p/>
 *         A client used to communicate with masters.
 */
public class MasterClient implements AsyncMasterClient {
    private TokenFactory serverToken = new TokenFactory(Configuration.SERVER_SECRET);
    private Vertx vertx;

    public MasterClient(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void create(Future<Void> future, Voting voting) {

        vertx.createHttpClient().post(Configuration.MASTER_PORT, "localhost", "/api/create", handler -> {

            if (handler.statusCode() == HttpResponseStatus.OK.code())
                future.complete();
            else
                future.fail(new MasterFailureException());

        }).end(new JsonObject()
                .put("token", getServerToken())
                .put("voting", Serializer.json(voting))
                .encode());
    }

    @Override
    public void terminate(Future<Void> future, Voting voting) {

        vertx.createHttpClient().post(Configuration.MASTER_PORT, "localhost", "/api/terminate", handler -> {

            if (handler.statusCode() == HttpResponseStatus.OK.code())
                future.complete();
            else
                future.fail(new MasterFailureException());

        }).end(
                new JsonObject()
                        .put("token", getServerToken())
                        .put("voting", Serializer.json(voting))
                        .encode()
        );
    }

    private JsonObject getServerToken() {
        return new JsonObject(Json.encode(new Token(serverToken, Configuration.SERVER_NAME)));
    }
}
