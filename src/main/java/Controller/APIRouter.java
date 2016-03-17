package Controller;

import Configuration.Configuration;
import Model.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Robin on 2016-03-16.
 * <p/>
 * Provides a management API to the controller system.
 */
class APIRouter {
    private TokenFactory serverToken = new TokenFactory(Configuration.SERVER_SECRET);
    private AsyncVotingStore votings;
    private AsyncMasterClient client;

    public void register(Router router, AsyncVotingStore votings, AsyncMasterClient client) {
        this.votings = votings;
        this.client = client;

        router.post("/api/create").handler(this::create);
        router.post("/api/terminate").handler(this::terminate);
        router.post("/api/list").handler(this::list);
    }

    private void create(RoutingContext context) {
        HttpServerResponse response = context.response();
        JsonObject request = context.getBodyAsJson();

        if (authorized(context)) {
            Voting voting = (Voting) Serializer.unpack(request.getJsonObject("voting"), Voting.class);
            Future<Void> master = Future.future();

            voting.setOwner(tokenFrom(context).getDomain());

            master.setHandler(result -> {
                try {
                    if (result.succeeded()) {
                        Future<Void> storage = Future.future();

                        storage.setHandler(create -> {
                            if (create.succeeded())
                                response.setStatusCode(HttpResponseStatus.OK.code()).end();
                            else
                                response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                        });

                        votings.create(storage, voting);
                    } else
                        throw master.cause();

                } catch (Throwable throwable) {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            });
            client.create(master, voting);
        }
    }

    private Token tokenFrom(RoutingContext context) {
        return (Token) Serializer.unpack(context.getBodyAsJson().getJsonObject("token"), Token.class);
    }


    private void terminate(RoutingContext context) {
        HttpServerResponse response = context.response();
        JsonObject request = context.getBodyAsJson();
        Voting voting = (Voting) Serializer.unpack(request.getJsonObject("voting"), Voting.class);

        if (authorized(context, voting)) {
            Future<Void> master = Future.future();

            master.setHandler(result -> {
                try {
                    if (result.succeeded()) {
                        Future<Void> storage = Future.future();

                        storage.setHandler(terminate -> {
                            if (terminate.succeeded())
                                response.setStatusCode(HttpResponseStatus.OK.code()).end();
                            else
                                response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                        });

                        votings.terminate(storage, voting.getOwner());
                    } else
                        throw result.cause();

                } catch (Throwable throwable) {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            });
            client.terminate(master, voting);
        }
    }


    private void list(RoutingContext context) {
        HttpServerResponse response = context.response();
        Future<VotingList> future = Future.future();

        if (authorized(context)) {
            future.setHandler(result -> {
                try {
                    if (result.succeeded())
                        response.setStatusCode(HttpResponseStatus.OK.code()).end(Serializer.pack(result.result()));
                    else
                        throw result.cause();

                } catch (Throwable throwable) {
                    response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                }
            });
            votings.list(future, context.getBodyAsJson().getString("owner"));
        }
    }

    private boolean authorized(RoutingContext context, Voting voting) {
        String owner = context.getBodyAsJson().getString("owner");
        return owner.equals(voting.getOwner()) && authorized(context);
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
