package Controller;

import Configuration.Configuration;
import Model.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Robin on 2016-03-16.
 * <p/>
 * Provides a management API to the controller system.
 */
public class APIRouter {
    private Vertx vertx;
    private AsyncVotingStore votings;
    private TokenFactory serverToken;

    public void register(Router router, AsyncVotingStore votings, Vertx vertx) {
        this.vertx = vertx;
        this.votings = votings;

        serverToken = new TokenFactory(Configuration.SERVER_SECRET);

        router.post("/api/create").handler(this::create);
        router.post("/api/terminate").handler(this::terminate);
        router.post("/api/list").handler(this::list);
    }

    private void create(RoutingContext context) {
        HttpServerResponse response = context.response();
        JsonObject request = context.getBodyAsJson();
        Future<Void> future = Future.future();

        if (authorized(context)) {
            Voting voting = (Voting) Serializer.unpack(request.getJsonObject("voting"), Voting.class);

            vertx.createHttpClient().post(Configuration.MASTER_PORT, "localhost", "/api/create", handler -> {
                if (handler.statusCode() == HttpResponseStatus.OK.code()) {

                    future.setHandler(result -> {
                        try {
                            if (result.succeeded()) {
                                response.setStatusCode(HttpResponseStatus.OK.code()).end();
                            } else
                                throw future.cause();

                        } catch (Throwable throwable) {
                            response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                        }
                    });
                    votings.create(future, voting);
                } else {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            }).end(new JsonObject()
                    .put("token", getServerToken())
                    .put("voting", Serializer.pack(voting))
                    .encode());
        }
    }

    private void terminate(RoutingContext context) {
        HttpServerResponse response = context.response();
        JsonObject request = context.getBodyAsJson();
        Future<Void> future = Future.future();

        if (authorized(context)) {
            Voting voting = (Voting) Serializer.unpack(request.getJsonObject("voting"), Voting.class);

            vertx.createHttpClient().post(Configuration.MASTER_PORT, "localhost", "/api/terminate", handler -> {
                if (handler.statusCode() == HttpResponseStatus.OK.code()) {
                    future.setHandler(result -> {
                        try {
                            if (result.succeeded()) {
                                response.setStatusCode(HttpResponseStatus.OK.code()).end();
                            } else
                                throw result.cause();

                        } catch (Throwable throwable) {
                            response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                        }
                    });
                    votings.terminate(future, voting.getOwner());
                } else {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            }).end(
                    new JsonObject()
                            .put("token", getServerToken())
                            .put("voting", Serializer.pack(voting))
                            .encode()
            );


        }
    }

    private void list(RoutingContext context) {
        HttpServerResponse response = context.response();
        JsonObject request = context.getBodyAsJson();
        Future<VotingList> future = Future.future();

        if (authorized(context)) {
            Voting voting = (Voting) Serializer.unpack(request.getJsonObject("voting"), Voting.class);

            future.setHandler(result -> {
                try {
                    if (result.succeeded())
                        response.setStatusCode(HttpResponseStatus.OK.code()).end();
                    else
                        throw result.cause();

                } catch (Throwable throwable) {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            });
            votings.list(future, voting.getOwner());
        }
    }

    private JsonObject getServerToken() {
        return new JsonObject(Json.encode(new Token(serverToken, Configuration.SERVER_NAME)));
    }

    private boolean authorized(RoutingContext context) {
        boolean authorized = serverToken.verifyToken((Token)
                Serializer.unpack(context.getBodyAsJson().getJsonObject("token"), Token.class));

        if (!authorized) {
            context.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code()).end();
        }

        return authorized;
    }
}
