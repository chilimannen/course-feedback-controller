import Configuration.Configuration;
import Controller.WebServer;
import Model.Serializer;
import Model.Token;
import Model.TokenException;
import Model.TokenFactory;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

/**
 * Created by Robin on 2016-03-16.
 */

@RunWith(VertxUnitRunner.class)
public class APITest {
    private Vertx vertx;
    private TokenFactory tokenFactory;

    @Rule
    public Timeout timeout = Timeout.seconds(15);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new WebServer(new VotingDBMock()), context.asyncAssertSuccess());
        tokenFactory = new TokenFactory(Configuration.SERVER_SECRET);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void testCreateVoting(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/create", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getValidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    private JsonObject getVotingConfiguration() {
        return new JsonObject()
                .put("name", "test-voting")
                .put("duration", 10)
                .put("options",
                        new JsonArray()
                                .add(new JsonObject()
                                        .put("name", "query 1")
                                        .put("answer",
                                                new JsonArray()
                                                        .add("value 1")
                                                        .add("value 2")
                                                        .add("value 3")))
                                .add(new JsonObject()
                                        .put("name", "query 2")
                                        .put("answer",
                                                new JsonArray()
                                                        .add("value a")
                                                        .add("value b")
                                                        .add("value c"))));
    }

    @Test
    public void testTerminateVoting(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getValidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }


    @Test
    public void testListVotingInProgress(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getValidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    @Test
    public void testCreateVotingRequiresToken(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    @Test
    public void testListVotingRequiresToken(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    @Test
    public void testTerminateVotingRequiresToken(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.CONTROLLER_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }


    private JsonObject getInvalidToken() {
        return new JsonObject()
                .put("key", "invalid")
                .put("domain", Configuration.SERVER_NAME)
                .put("expiry", Instant.now().getEpochSecond() + 90);
    }

    private JsonObject getValidToken() throws TokenException {
        return new JsonObject(Serializer.pack(new Token(tokenFactory, Configuration.SERVER_NAME)));
    }
}
